package org.hajo.junit.examples;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@RunWith(DataProviderRunner.class)
public class ExampleTests {

    @Test
    public void testA() {
    }

    @Test
    public void testB() {
    }

    @Test
    @DataProvider({ "InputA", "InputB" })
    public void testWithDataProvider(String input) {
    }

}
