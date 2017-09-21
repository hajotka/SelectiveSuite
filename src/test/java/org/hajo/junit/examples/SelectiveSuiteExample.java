package org.hajo.junit.examples;

import org.hajo.junit.SelectiveSuite;
import org.junit.runner.RunWith;

@RunWith(SelectiveSuite.class)

@SelectiveSuite.TestSelectors(testSelectors =
        {@SelectiveSuite.TestSelector(
                klass = ExampleTests.class,
                testnames = {
                        "testA",
                        "testWithDataProvider[0: InputA]"})
        })

public final class SelectiveSuiteExample {
}
