package org.hajo.junit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.lang.annotation.*;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * <code>SelectiveSuite</code> extends <code>Suite</code> to run only specific
 * tests methods given in the <code>TestSelectors</code> annotation
 */
public class SelectiveSuite extends Suite {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface TestSelectors {
        TestSelector[] testSelectors();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface TestSelector {
        /**
         * The test class from which tests are to be executed
         */
        Class<?> klass();

        /**
         * List of names of the test methods to be executed.
         * Matching is done with {@link java.lang.String#startsWith startswith},
         * i.e. 'test' will match 'testA' and 'testB'.
         */
        String[] testnames();
    }

    private static Class<?>[] getTestClasses(Class<?> klass) throws InitializationError {
        TestSelectors testSelectorsAnnotation = klass.getAnnotation(TestSelectors.class);
        if (testSelectorsAnnotation == null) {
            throw new InitializationError("Class " + klass.getName() + " must have a TestSelectors annotation");
        }

        ArrayList<Class<?>> classes = new ArrayList<>();
        for (TestSelector testSelector : testSelectorsAnnotation.testSelectors()) {
            classes.add(testSelector.klass());
        }
        return classes.toArray(new Class<?>[classes.size()]);
    }

    public SelectiveSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError, NoTestsRemainException {
        super(builder, klass, getTestClasses(klass));

        final TestSelectorEvaluator testSelectorEvaluator = new TestSelectorEvaluator(klass.getAnnotation(TestSelectors.class));

        testSelectorEvaluator.checkAllSelectedTestsExist(getChildren());

        filter(new Filter() {
            @Override
            public boolean shouldRun(Description description) {
                return description.isSuite() || testSelectorEvaluator.isTestSelected(description);
            }

            @Override
            public String describe() {
                return "Only run tests ";
            }

        });

    }


    static class TestSelectorEvaluator {

        final Map<Class, Set<String>> selectedTests = new HashMap<>();

        public TestSelectorEvaluator(TestSelectors testSelectors) {
            extractSelectedTests(testSelectors);
        }

        private void extractSelectedTests(TestSelectors testSelectors) {
            for (TestSelector selector : testSelectors.testSelectors()) {
                if (!selectedTests.containsKey(selector.klass())) {
                    selectedTests.put(selector.klass(), new HashSet<String>());
                }
                Set<String> testnamesForClass = selectedTests.get(selector.klass());
                testnamesForClass.addAll(asList(selector.testnames()));
            }
        }

        void checkAllSelectedTestsExist(List<Runner> runners) throws InitializationError {
            final Map<Class, Set<String>> allTests = extractTestsFromRunners(runners);

            for (Class testClass : selectedTests.keySet()) {
                Set<String> existingTests = allTests.get(testClass);
                for (String testname : selectedTests.get(testClass)) {
                    if (!anyTestnameStartsWithPrefix(existingTests, testname)) {
                        throw new InitializationError("No test in class " + testClass.getName()
                                + " with name starting with " + testname);
                    }
                }
            }
        }

        private Map<Class, Set<String>> extractTestsFromRunners(List<Runner> runners) {
            final Map<Class, Set<String>> allTests = new HashMap<>();

            for (Runner child : runners) {
                for (Description description : child.getDescription().getChildren()) {
                    if (!allTests.containsKey(description.getTestClass())) {
                        allTests.put(description.getTestClass(), new HashSet<String>());
                    }
                    Set<String> testnamesForClass = allTests.get(description.getTestClass());
                    testnamesForClass.add(description.getDisplayName());
                }
            }
            return allTests;
        }

        boolean isTestSelected(Description description) {
            Set<String> testnamePrefixesForClass = selectedTests.get(description.getTestClass());
            return testnameStartsWithAnyOfPrefixes(description.getDisplayName(), testnamePrefixesForClass);
        }

        private boolean testnameStartsWithAnyOfPrefixes(String testname, Set<String> testnamePrefixes) {
            for (String prefix : testnamePrefixes) {
                if (testname.startsWith(prefix)) {
                    return true;
                }
            }
            return false;
        }

        private boolean anyTestnameStartsWithPrefix(Set<String> testnames, String testnamePrefix) {
            for (String testname : testnames) {
                if (testname.startsWith(testnamePrefix)) {
                    return true;
                }
            }
            return false;
        }

    }
}
