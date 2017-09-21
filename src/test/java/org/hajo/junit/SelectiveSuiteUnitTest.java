package org.hajo.junit;

import com.google.common.annotations.VisibleForTesting;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class SelectiveSuiteUnitTest {

    @Test
    public void throwsExceptionIfThereIsNoSuiteTestsAnnotation() throws Exception {
        try {
            SelectiveSuite suite = new SelectiveSuite(SelectiveSuiteIntegratedTest.TestSuiteWithoutSuiteTestsAnnotation.class, mock(RunnerBuilder.class));
            fail("InitializationError expected because of missing annotation.");
        } catch (InitializationError e) {
            assertThat(e.getCauses()).hasSize(1);
            assertThat(e.getCauses().get(0)).hasMessageContaining("annotation");
        }
    }

}
