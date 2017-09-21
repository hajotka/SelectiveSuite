package org.hajo.junit;

import com.google.common.annotations.VisibleForTesting;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class SelectiveSuiteIntegratedTest {

    private static List<String> executedTestMethods = newArrayList();

    @Before
    public void setup() {
        executedTestMethods.clear();
    }

    public static class TestClassA {
        @Test
        public void testA1() {
            executedTestMethods.add("testA1");
        }

        @Test
        public void testA2() {
            executedTestMethods.add("testA2");
        }
    }

    public static class TestClassB {
        @Test
        public void testA1() {
            executedTestMethods.add("testA1");
        }

        @Test
        public void testA2() {
            executedTestMethods.add("testA2");
        }

        @Test
        public void testB1() {
            executedTestMethods.add("testB1");
        }

        @Test
        public void testB2() {
            executedTestMethods.add("testB2");
        }

        @Test
        public void testC1() {
            executedTestMethods.add("testC1");
        }
    }

    @RunWith(SelectiveSuite.class)
    @SelectiveSuite.TestSelectors(testSelectors = {
            @SelectiveSuite.TestSelector(klass = TestClassA.class, testnames = { "testA2" }),
            @SelectiveSuite.TestSelector(klass = TestClassB.class, testnames = { "testB" }) })
    public class TestSuiteAWithNormalTests {
    }

    @RunWith(SelectiveSuite.class)
    public class TestSuiteWithoutSuiteTestsAnnotation {
    }

    @Test
    public void testMethodsAreFilteredCorrectly() {
        Result result = JUnitCore.runClasses(TestSuiteAWithNormalTests.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(executedTestMethods).containsExactly("testA2", "testB1", "testB2");
    }

    @VisibleForTesting
    @RunWith(DataProviderRunner.class)
    public static class TestClassWithDataProvider {
        @Test
        // @formatter:off
        @DataProvider({
            "parameter0",
            "parameter1",
            "parameter2"
        })
        // @formatter:on
        public void testWithDataProvider(String argument) {
            executedTestMethods.add("testWithDataProvider(" + argument + ")");
        }
    }

    @RunWith(SelectiveSuite.class)
    @SelectiveSuite.TestSelectors(testSelectors = {
            @SelectiveSuite.TestSelector(klass = TestClassWithDataProvider.class, testnames = {
                    "testWithDataProvider[1: parameter1]" }) })
    public class TestSuiteBWithDataProviderTest {
    }

    @Test
    public void testMethodsWithDataProviderAreFilteredCorrectly() {
        Result result = JUnitCore.runClasses(TestSuiteBWithDataProviderTest.class);
        System.out.println(executedTestMethods);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(executedTestMethods).containsExactly("testWithDataProvider(parameter1)");
    }

    @RunWith(SelectiveSuite.class)
    @SelectiveSuite.TestSelectors(testSelectors = {
            @SelectiveSuite.TestSelector(klass = TestClassA.class, testnames = { "testA1", "testA2" }),
            @SelectiveSuite.TestSelector(klass = TestClassB.class, testnames = { "nonExistingTest" }) })
    public class SelectiveTestSuiteWithNonExistingTest {
    }

    @Test
    public void testNonExistingTestsCauseException() {
        Result result = JUnitCore.runClasses(SelectiveTestSuiteWithNonExistingTest.class);
        assertThat(result.wasSuccessful()).isFalse();
    }
}
