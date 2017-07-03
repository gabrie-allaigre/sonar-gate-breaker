package com.talanlabs.sonargatebreaker;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.text.MessageFormat;

public class CheckerTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();
    @Rule
    public MockWebServer sonar = new MockWebServer();

    private String reportPath;

    @Before
    public void init() throws IOException {
        String text = IOUtils.toString(CheckerTest.class.getResourceAsStream("/test.txt"), "UTF-8");
        String report = MessageFormat.format(text, "http://" + sonar.getHostName() + ":" + sonar.getPort());
        File file = temp.newFile();
        reportPath = file.getAbsolutePath();
        IOUtils.write(report, new FileOutputStream(file), "UTF-8");
    }

    @Test
    public void testSuccess1() {
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"task\":{\"organization\":\"default-organization\",\"id\":\"AVz4Pj0lCGu3nUwPQk4H\",\"type\":\"REPORT\",\"componentId\":\"AVm3vySdp12Xu7MIiqvQ\",\"componentKey\":\"com.talanlabs:avatar-generator-parent\",\"componentName\":\"Avatar Generator - Parent\",\"componentQualifier\":\"TRK\",\"analysisId\":\"AVz4Pj8EKLxfdP2AyK4d\",\"status\":\"SUCCESS\",\"submittedAt\":\"2017-06-30T09:04:07+0000\",\"submitterLogin\":\"gitlab-ci\",\"startedAt\":\"2017-06-30T09:04:07+0000\",\"executedAt\":\"2017-06-30T09:04:09+0000\",\"executionTimeMs\":1697,\"logs\":false,\"hasScannerContext\":true}}"));
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"projectStatus\":{\"status\":\"SUCCESS\",\"conditions\":[{\"status\":\"OK\",\"metricKey\":\"blocker_violations\",\"comparator\":\"GT\",\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"critical_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"open_issues\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"10\",\"actualValue\":\"1\"},{\"status\":\"OK\",\"metricKey\":\"reopened_issues\",\"comparator\":\"GT\",\"warningThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"ERROR\",\"metricKey\":\"new_coverage\",\"comparator\":\"LT\",\"periodIndex\":1,\"errorThreshold\":\"70\",\"actualValue\":\"0.0\"},{\"status\":\"OK\",\"metricKey\":\"major_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"1\",\"errorThreshold\":\"10\",\"actualValue\":\"0\"}],\"periods\":[{\"index\":1,\"mode\":\"previous_version\",\"date\":\"2017-01-26T09:58:11+0000\",\"parameter\":\"1.1.0-SNAPSHOT\"}]}}"));
        Checker checker = new Checker();
        checker.setReportTaskPath(reportPath);
        checker.setPrintReport(false);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        checker.setPrintStream(new PrintStream(out));

        Assertions.assertThat(checker.check()).isTrue();
        Assertions.assertThat(out.toString()).isEmpty();
    }

    @Test
    public void testSuccess2() {
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"task\":{\"organization\":\"default-organization\",\"id\":\"AVz4Pj0lCGu3nUwPQk4H\",\"type\":\"REPORT\",\"componentId\":\"AVm3vySdp12Xu7MIiqvQ\",\"componentKey\":\"com.talanlabs:avatar-generator-parent\",\"componentName\":\"Avatar Generator - Parent\",\"componentQualifier\":\"TRK\",\"analysisId\":\"AVz4Pj8EKLxfdP2AyK4d\",\"status\":\"SUCCESS\",\"submittedAt\":\"2017-06-30T09:04:07+0000\",\"submitterLogin\":\"gitlab-ci\",\"startedAt\":\"2017-06-30T09:04:07+0000\",\"executedAt\":\"2017-06-30T09:04:09+0000\",\"executionTimeMs\":1697,\"logs\":false,\"hasScannerContext\":true}}"));
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"projectStatus\":{\"status\":\"SUCCESS\",\"conditions\":[{\"status\":\"OK\",\"metricKey\":\"blocker_violations\",\"comparator\":\"GT\",\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"critical_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"open_issues\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"10\",\"actualValue\":\"1\"},{\"status\":\"OK\",\"metricKey\":\"reopened_issues\",\"comparator\":\"GT\",\"warningThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"ERROR\",\"metricKey\":\"new_coverage\",\"comparator\":\"LT\",\"periodIndex\":1,\"errorThreshold\":\"70\",\"actualValue\":\"0.0\"},{\"status\":\"OK\",\"metricKey\":\"major_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"1\",\"errorThreshold\":\"10\",\"actualValue\":\"0\"}],\"periods\":[{\"index\":1,\"mode\":\"previous_version\",\"date\":\"2017-01-26T09:58:11+0000\",\"parameter\":\"1.1.0-SNAPSHOT\"}]}}"));
        Checker checker = new Checker();
        checker.setReportTaskPath(reportPath);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        checker.setPrintStream(new PrintStream(out));

        Assertions.assertThat(checker.check()).isTrue();
        Assertions.assertThat(out.toString()).isNotEmpty();
    }

    @Test
    public void testPendingSuccess() {
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"task\":{\"organization\":\"default-organization\",\"id\":\"AVz4Pj0lCGu3nUwPQk4H\",\"type\":\"REPORT\",\"componentId\":\"AVm3vySdp12Xu7MIiqvQ\",\"componentKey\":\"com.talanlabs:avatar-generator-parent\",\"componentName\":\"Avatar Generator - Parent\",\"componentQualifier\":\"TRK\",\"analysisId\":\"AVz4Pj8EKLxfdP2AyK4d\",\"status\":\"PENDING\",\"submittedAt\":\"2017-06-30T09:04:07+0000\",\"submitterLogin\":\"gitlab-ci\",\"startedAt\":\"2017-06-30T09:04:07+0000\",\"executedAt\":\"2017-06-30T09:04:09+0000\",\"executionTimeMs\":1697,\"logs\":false,\"hasScannerContext\":true}}"));
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"task\":{\"organization\":\"default-organization\",\"id\":\"AVz4Pj0lCGu3nUwPQk4H\",\"type\":\"REPORT\",\"componentId\":\"AVm3vySdp12Xu7MIiqvQ\",\"componentKey\":\"com.talanlabs:avatar-generator-parent\",\"componentName\":\"Avatar Generator - Parent\",\"componentQualifier\":\"TRK\",\"analysisId\":\"AVz4Pj8EKLxfdP2AyK4d\",\"status\":\"PENDING\",\"submittedAt\":\"2017-06-30T09:04:07+0000\",\"submitterLogin\":\"gitlab-ci\",\"startedAt\":\"2017-06-30T09:04:07+0000\",\"executedAt\":\"2017-06-30T09:04:09+0000\",\"executionTimeMs\":1697,\"logs\":false,\"hasScannerContext\":true}}"));
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"task\":{\"organization\":\"default-organization\",\"id\":\"AVz4Pj0lCGu3nUwPQk4H\",\"type\":\"REPORT\",\"componentId\":\"AVm3vySdp12Xu7MIiqvQ\",\"componentKey\":\"com.talanlabs:avatar-generator-parent\",\"componentName\":\"Avatar Generator - Parent\",\"componentQualifier\":\"TRK\",\"analysisId\":\"AVz4Pj8EKLxfdP2AyK4d\",\"status\":\"PENDING\",\"submittedAt\":\"2017-06-30T09:04:07+0000\",\"submitterLogin\":\"gitlab-ci\",\"startedAt\":\"2017-06-30T09:04:07+0000\",\"executedAt\":\"2017-06-30T09:04:09+0000\",\"executionTimeMs\":1697,\"logs\":false,\"hasScannerContext\":true}}"));
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"task\":{\"organization\":\"default-organization\",\"id\":\"AVz4Pj0lCGu3nUwPQk4H\",\"type\":\"REPORT\",\"componentId\":\"AVm3vySdp12Xu7MIiqvQ\",\"componentKey\":\"com.talanlabs:avatar-generator-parent\",\"componentName\":\"Avatar Generator - Parent\",\"componentQualifier\":\"TRK\",\"analysisId\":\"AVz4Pj8EKLxfdP2AyK4d\",\"status\":\"SUCCESS\",\"submittedAt\":\"2017-06-30T09:04:07+0000\",\"submitterLogin\":\"gitlab-ci\",\"startedAt\":\"2017-06-30T09:04:07+0000\",\"executedAt\":\"2017-06-30T09:04:09+0000\",\"executionTimeMs\":1697,\"logs\":false,\"hasScannerContext\":true}}"));

        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"projectStatus\":{\"status\":\"SUCCESS\",\"conditions\":[{\"status\":\"OK\",\"metricKey\":\"blocker_violations\",\"comparator\":\"GT\",\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"critical_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"open_issues\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"10\",\"actualValue\":\"1\"},{\"status\":\"OK\",\"metricKey\":\"reopened_issues\",\"comparator\":\"GT\",\"warningThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"ERROR\",\"metricKey\":\"new_coverage\",\"comparator\":\"LT\",\"periodIndex\":1,\"errorThreshold\":\"70\",\"actualValue\":\"0.0\"},{\"status\":\"OK\",\"metricKey\":\"major_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"1\",\"errorThreshold\":\"10\",\"actualValue\":\"0\"}],\"periods\":[{\"index\":1,\"mode\":\"previous_version\",\"date\":\"2017-01-26T09:58:11+0000\",\"parameter\":\"1.1.0-SNAPSHOT\"}]}}"));
        Checker checker = new Checker();
        checker.setReportTaskPath(reportPath);

        Assertions.assertThat(checker.check()).isTrue();
    }

    @Test
    public void testError1() {
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"task\":{\"organization\":\"default-organization\",\"id\":\"AVz4Pj0lCGu3nUwPQk4H\",\"type\":\"REPORT\",\"componentId\":\"AVm3vySdp12Xu7MIiqvQ\",\"componentKey\":\"com.talanlabs:avatar-generator-parent\",\"componentName\":\"Avatar Generator - Parent\",\"componentQualifier\":\"TRK\",\"analysisId\":\"AVz4Pj8EKLxfdP2AyK4d\",\"status\":\"SUCCESS\",\"submittedAt\":\"2017-06-30T09:04:07+0000\",\"submitterLogin\":\"gitlab-ci\",\"startedAt\":\"2017-06-30T09:04:07+0000\",\"executedAt\":\"2017-06-30T09:04:09+0000\",\"executionTimeMs\":1697,\"logs\":false,\"hasScannerContext\":true}}"));
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"projectStatus\":{\"status\":\"ERROR\",\"conditions\":[{\"status\":\"OK\",\"metricKey\":\"blocker_violations\",\"comparator\":\"GT\",\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"critical_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"open_issues\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"10\",\"actualValue\":\"1\"},{\"status\":\"OK\",\"metricKey\":\"reopened_issues\",\"comparator\":\"GT\",\"warningThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"ERROR\",\"metricKey\":\"new_coverage\",\"comparator\":\"LT\",\"periodIndex\":1,\"errorThreshold\":\"70\",\"actualValue\":\"0.0\"},{\"status\":\"OK\",\"metricKey\":\"major_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"1\",\"errorThreshold\":\"10\",\"actualValue\":\"0\"}],\"periods\":[{\"index\":1,\"mode\":\"previous_version\",\"date\":\"2017-01-26T09:58:11+0000\",\"parameter\":\"1.1.0-SNAPSHOT\"}]}}"));
        Checker checker = new Checker();
        checker.setReportTaskPath(reportPath);

        Assertions.assertThat(checker.check()).isFalse();
    }

    @Test
    public void testWarn1() {
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"task\":{\"organization\":\"default-organization\",\"id\":\"AVz4Pj0lCGu3nUwPQk4H\",\"type\":\"REPORT\",\"componentId\":\"AVm3vySdp12Xu7MIiqvQ\",\"componentKey\":\"com.talanlabs:avatar-generator-parent\",\"componentName\":\"Avatar Generator - Parent\",\"componentQualifier\":\"TRK\",\"analysisId\":\"AVz4Pj8EKLxfdP2AyK4d\",\"status\":\"SUCCESS\",\"submittedAt\":\"2017-06-30T09:04:07+0000\",\"submitterLogin\":\"gitlab-ci\",\"startedAt\":\"2017-06-30T09:04:07+0000\",\"executedAt\":\"2017-06-30T09:04:09+0000\",\"executionTimeMs\":1697,\"logs\":false,\"hasScannerContext\":true}}"));
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"projectStatus\":{\"status\":\"WARN\",\"conditions\":[{\"status\":\"OK\",\"metricKey\":\"blocker_violations\",\"comparator\":\"GT\",\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"critical_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"open_issues\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"10\",\"actualValue\":\"1\"},{\"status\":\"OK\",\"metricKey\":\"reopened_issues\",\"comparator\":\"GT\",\"warningThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"ERROR\",\"metricKey\":\"new_coverage\",\"comparator\":\"LT\",\"periodIndex\":1,\"errorThreshold\":\"70\",\"actualValue\":\"0.0\"},{\"status\":\"OK\",\"metricKey\":\"major_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"1\",\"errorThreshold\":\"10\",\"actualValue\":\"0\"}],\"periods\":[{\"index\":1,\"mode\":\"previous_version\",\"date\":\"2017-01-26T09:58:11+0000\",\"parameter\":\"1.1.0-SNAPSHOT\"}]}}"));
        Checker checker = new Checker();
        checker.setReportTaskPath(reportPath);

        Assertions.assertThat(checker.check()).isTrue();
    }

    @Test
    public void testWarn2() {
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"task\":{\"organization\":\"default-organization\",\"id\":\"AVz4Pj0lCGu3nUwPQk4H\",\"type\":\"REPORT\",\"componentId\":\"AVm3vySdp12Xu7MIiqvQ\",\"componentKey\":\"com.talanlabs:avatar-generator-parent\",\"componentName\":\"Avatar Generator - Parent\",\"componentQualifier\":\"TRK\",\"analysisId\":\"AVz4Pj8EKLxfdP2AyK4d\",\"status\":\"SUCCESS\",\"submittedAt\":\"2017-06-30T09:04:07+0000\",\"submitterLogin\":\"gitlab-ci\",\"startedAt\":\"2017-06-30T09:04:07+0000\",\"executedAt\":\"2017-06-30T09:04:09+0000\",\"executionTimeMs\":1697,\"logs\":false,\"hasScannerContext\":true}}"));
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"projectStatus\":{\"status\":\"WARN\",\"conditions\":[{\"status\":\"OK\",\"metricKey\":\"blocker_violations\",\"comparator\":\"GT\",\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"critical_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"open_issues\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"10\",\"actualValue\":\"1\"},{\"status\":\"OK\",\"metricKey\":\"reopened_issues\",\"comparator\":\"GT\",\"warningThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"ERROR\",\"metricKey\":\"new_coverage\",\"comparator\":\"LT\",\"periodIndex\":1,\"errorThreshold\":\"70\",\"actualValue\":\"0.0\"},{\"status\":\"OK\",\"metricKey\":\"major_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"1\",\"errorThreshold\":\"10\",\"actualValue\":\"0\"}],\"periods\":[{\"index\":1,\"mode\":\"previous_version\",\"date\":\"2017-01-26T09:58:11+0000\",\"parameter\":\"1.1.0-SNAPSHOT\"}]}}"));
        Checker checker = new Checker();
        checker.setReportTaskPath(reportPath);
        checker.setFailOnWarn(true);

        Assertions.assertThat(checker.check()).isFalse();
    }

    @Test
    public void testError2() {
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"task\":{\"organization\":\"default-organization\",\"id\":\"AVz4Pj0lCGu3nUwPQk4H\",\"type\":\"REPORT\",\"componentId\":\"AVm3vySdp12Xu7MIiqvQ\",\"componentKey\":\"com.talanlabs:avatar-generator-parent\",\"componentName\":\"Avatar Generator - Parent\",\"componentQualifier\":\"TRK\",\"analysisId\":\"AVz4Pj8EKLxfdP2AyK4d\",\"status\":\"ERROR\",\"submittedAt\":\"2017-06-30T09:04:07+0000\",\"submitterLogin\":\"gitlab-ci\",\"startedAt\":\"2017-06-30T09:04:07+0000\",\"executedAt\":\"2017-06-30T09:04:09+0000\",\"executionTimeMs\":1697,\"logs\":false,\"hasScannerContext\":true}}"));
        Checker checker = new Checker();
        checker.setReportTaskPath(reportPath);
        Assertions.assertThat(checker.check()).isFalse();
    }

    @Test
    public void testError3() {
        sonar.enqueue(new MockResponse().setResponseCode(404));
        Checker checker = new Checker();
        checker.setReportTaskPath(reportPath);
        Assertions.assertThat(checker.check()).isFalse();
    }

    @Test
    public void testError4() throws IOException {
        sonar.enqueue(new MockResponse().setResponseCode(404));
        Checker checker = new Checker();
        checker.setReportTaskPath(temp.newFile().getAbsolutePath());
        Assertions.assertThat(checker.check()).isFalse();
    }

    @Test
    public void testAuth1() {
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"task\":{\"organization\":\"default-organization\",\"id\":\"AVz4Pj0lCGu3nUwPQk4H\",\"type\":\"REPORT\",\"componentId\":\"AVm3vySdp12Xu7MIiqvQ\",\"componentKey\":\"com.talanlabs:avatar-generator-parent\",\"componentName\":\"Avatar Generator - Parent\",\"componentQualifier\":\"TRK\",\"analysisId\":\"AVz4Pj8EKLxfdP2AyK4d\",\"status\":\"SUCCESS\",\"submittedAt\":\"2017-06-30T09:04:07+0000\",\"submitterLogin\":\"gitlab-ci\",\"startedAt\":\"2017-06-30T09:04:07+0000\",\"executedAt\":\"2017-06-30T09:04:09+0000\",\"executionTimeMs\":1697,\"logs\":false,\"hasScannerContext\":true}}"));
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"projectStatus\":{\"status\":\"SUCCESS\",\"conditions\":[{\"status\":\"OK\",\"metricKey\":\"blocker_violations\",\"comparator\":\"GT\",\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"critical_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"open_issues\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"10\",\"actualValue\":\"1\"},{\"status\":\"OK\",\"metricKey\":\"reopened_issues\",\"comparator\":\"GT\",\"warningThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"ERROR\",\"metricKey\":\"new_coverage\",\"comparator\":\"LT\",\"periodIndex\":1,\"errorThreshold\":\"70\",\"actualValue\":\"0.0\"},{\"status\":\"OK\",\"metricKey\":\"major_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"1\",\"errorThreshold\":\"10\",\"actualValue\":\"0\"}],\"periods\":[{\"index\":1,\"mode\":\"previous_version\",\"date\":\"2017-01-26T09:58:11+0000\",\"parameter\":\"1.1.0-SNAPSHOT\"}]}}"));
        Checker checker = new Checker();
        checker.setReportTaskPath(reportPath);
        checker.setUsername("test");
        checker.setPassword("test");

        Assertions.assertThat(checker.check()).isTrue();
    }

    @Test
    public void testAuth2() {
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"task\":{\"organization\":\"default-organization\",\"id\":\"AVz4Pj0lCGu3nUwPQk4H\",\"type\":\"REPORT\",\"componentId\":\"AVm3vySdp12Xu7MIiqvQ\",\"componentKey\":\"com.talanlabs:avatar-generator-parent\",\"componentName\":\"Avatar Generator - Parent\",\"componentQualifier\":\"TRK\",\"analysisId\":\"AVz4Pj8EKLxfdP2AyK4d\",\"status\":\"SUCCESS\",\"submittedAt\":\"2017-06-30T09:04:07+0000\",\"submitterLogin\":\"gitlab-ci\",\"startedAt\":\"2017-06-30T09:04:07+0000\",\"executedAt\":\"2017-06-30T09:04:09+0000\",\"executionTimeMs\":1697,\"logs\":false,\"hasScannerContext\":true}}"));
        sonar.enqueue(new MockResponse().setResponseCode(200).setBody("{\"projectStatus\":{\"status\":\"SUCCESS\",\"conditions\":[{\"status\":\"OK\",\"metricKey\":\"blocker_violations\",\"comparator\":\"GT\",\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"critical_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"errorThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"OK\",\"metricKey\":\"open_issues\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"10\",\"actualValue\":\"1\"},{\"status\":\"OK\",\"metricKey\":\"reopened_issues\",\"comparator\":\"GT\",\"warningThreshold\":\"0\",\"actualValue\":\"0\"},{\"status\":\"ERROR\",\"metricKey\":\"new_coverage\",\"comparator\":\"LT\",\"periodIndex\":1,\"errorThreshold\":\"70\",\"actualValue\":\"0.0\"},{\"status\":\"OK\",\"metricKey\":\"major_violations\",\"comparator\":\"GT\",\"periodIndex\":1,\"warningThreshold\":\"1\",\"errorThreshold\":\"10\",\"actualValue\":\"0\"}],\"periods\":[{\"index\":1,\"mode\":\"previous_version\",\"date\":\"2017-01-26T09:58:11+0000\",\"parameter\":\"1.1.0-SNAPSHOT\"}]}}"));
        Checker checker = new Checker();
        checker.setReportTaskPath(reportPath);
        checker.setUsername("test");

        Assertions.assertThat(checker.check()).isTrue();
    }
}
