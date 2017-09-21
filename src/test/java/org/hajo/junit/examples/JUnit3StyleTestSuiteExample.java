package org.hajo.junit.examples;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestSuite;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.AllTests;

@RunWith(AllTests.class)
public final class JUnit3StyleTestSuiteExample {

    public static TestSuite suite() throws NoTestsRemainException {
        TestSuite suite = new TestSuite();
        suite.addTest(createTest(ExampleTests.class, "testA"));
        suite.addTest(createTest(ExampleTests.class, "testWithDataProvider[1: InputB]"));
        return suite;
    }

    /**
     * @throws NoTestsRemainException If no test with the given display name prefix exists in the test class.
     */
    public static JUnit4TestAdapter createTest(Class<?> testClass, final String displayNamePrefix)
            throws NoTestsRemainException {
        JUnit4TestAdapter adapter = new JUnit4TestAdapter(testClass);
        adapter.filter(new Filter() {
            @Override
            public boolean shouldRun(Description description) {
                return description.getDisplayName().startsWith(displayNamePrefix);
            }

            @Override
            public String describe() {
                return "Only run tests with name starting with " + displayNamePrefix;
            }
        });
        return adapter;
    }

}
