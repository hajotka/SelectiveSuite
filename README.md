# Introduction

Just like JUnits Suite runner lets you assemble test suites from test classes, 
SelectiveSuite lets you assemble test suites from individual test methods collected
from different test classes.

The code is compatible with Java 7.

# Usage

See `SelectiveSuiteExample` for an example test suite.

Note: tests are selected by checking whether their display name starts with one of the `testnames` 
given in the TestSelector annotation. You can use this to select tests with similar names.
This is especially useful for tests using junit-dataprovider (https://github.com/TNG/junit-dataprovider)

See JUnit3StyleTestSuiteExample for how to achieve the same using a JUnit 3-style `suite()` method 
(which might be deprecated at some point).
