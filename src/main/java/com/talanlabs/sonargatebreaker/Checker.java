package com.talanlabs.sonargatebreaker;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

public class Checker {

    private static final Logger LOGGER = LoggerFactory.getLogger(Checker.class);

    private String reportTaskPath;
    private String username;
    private String password;
    private int nbRetry = 50;
    private long sleep = 1000L;
    private boolean failOnWarn = false;
    private boolean printReport = true;
    private PrintStream printStream = System.out;

    public void setReportTaskPath(String reportTaskPath) {
        this.reportTaskPath = reportTaskPath;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNbRetry(int nbRetry) {
        this.nbRetry = nbRetry;
    }

    public void setSleep(long sleep) {
        this.sleep = sleep;
    }

    public void setFailOnWarn(boolean failOnWarn) {
        this.failOnWarn = failOnWarn;
    }

    public void setPrintReport(boolean printReport) {
        this.printReport = printReport;
    }

    public void setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }

    /**
     * Verify if status is SUCCESS
     *
     * @return true is SUCCESS
     */
    public boolean check() {
        String status = getStatus();

        if (status == null || "ERROR".equals(status) || (failOnWarn && "WARN".equals(status))) {
            LOGGER.info("Quality Gate in SonarQube is not success ({})", status);
            return false;
        }

        return true;
    }

    /**
     * Get status for qality gate
     *
     * @return status
     */
    public String getStatus() {
        ReportTask reportTask;
        try {
            reportTask = readReportTask();
        } catch (Exception e) {
            LOGGER.error("Failed to read file {}", reportTaskPath, e);
            return null;
        }

        Pair<String, String> statusOfAnalyze;
        try {
            statusOfAnalyze = getStatusOfAnalyze(reportTask);
            int retry = 0;
            while ((statusOfAnalyze.getRight() == null || !("SUCCESS".equals(statusOfAnalyze.getRight()) || "ERROR".equals(statusOfAnalyze.getRight()))) && retry < nbRetry) {
                Thread.sleep(sleep);
                retry++;
                statusOfAnalyze = getStatusOfAnalyze(reportTask);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to read analyze in SonarQube", e);
            return null;
        }

        if (statusOfAnalyze.getRight() == null || !"SUCCESS".equals(statusOfAnalyze.getRight())) {
            LOGGER.info("Analyze in SonarQube is not success ({})", statusOfAnalyze.getRight());
            return null;
        }

        String status;
        try {
            status = getQualityGateStatus(reportTask, statusOfAnalyze.getLeft());
        } catch (Exception e) {
            LOGGER.error("Failed to read quality gate in SonarQube", e);
            return null;
        }
        return status;
    }

    private Pair<String, String> getStatusOfAnalyze(ReportTask reportTask) throws Exception {
        GetRequest getRequest = Unirest.get(reportTask.ceTaskUrl);
        if (username != null) {
            getRequest.basicAuth(username, password != null ? password : "");
        }
        HttpResponse<JsonNode> jsonResponse = getRequest.asJson();
        if (jsonResponse.getStatus() != 200) {
            throw new Exception("Failed to read " + reportTask.ceTaskUrl + " return status " + jsonResponse.getStatus());
        }
        JSONObject jsonNode = jsonResponse.getBody().getObject().getJSONObject("task");
        String analysisId = jsonNode.has("analysisId") ? jsonNode.getString("analysisId") : null;
        String status = jsonNode.has("status") ? jsonNode.getString("status") : null;
        return Pair.of(analysisId, status);
    }

    private String getQualityGateStatus(ReportTask reportTask, String analysisId) throws Exception {
        GetRequest getRequest = Unirest.get(reportTask.serverUrl + "/api/qualitygates/project_status?analysisId=" + analysisId);
        if (username != null) {
            getRequest.basicAuth(username, password != null ? password : "");
        }
        HttpResponse<JsonNode> jsonResponse = getRequest.asJson();
        if (jsonResponse.getStatus() != 200) {
            throw new Exception("Failed to read project status " + jsonResponse.getStatus());
        }
        JSONObject jsonObject = jsonResponse.getBody().getObject();

        if (printReport) {
            printReport(jsonObject);
        }

        return jsonObject.getJSONObject("projectStatus").getString("status");
    }

    private ReportTask readReportTask() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(reportTaskPath));

        if (properties.getProperty("serverUrl") == null || properties.getProperty("ceTaskId") == null || properties.getProperty("ceTaskUrl") == null) {
            throw new IOException("Failed to read file, not content serverUrl or ceTaskId or ceTaskUrl");
        }

        return new ReportTask(properties.getProperty("serverUrl"), properties.getProperty("ceTaskUrl"));
    }

    private void printReport(JSONObject jsonObject) {
        JSONArray condisJSONArray = jsonObject.getJSONObject("projectStatus").getJSONArray("conditions");
        if (condisJSONArray != null && condisJSONArray.length() > 0) {
            for (int i = 0; i < condisJSONArray.length(); i++) {
                printCond(condisJSONArray.getJSONObject(i));
            }
        }
    }

    private void printCond(JSONObject jsonObject) {
        StringBuilder sb = new StringBuilder();

        sb.append(jsonObject.getString("metricKey")).append(" => ").append(jsonObject.getString("status"));
        sb.append(" ( Actual : ").append(jsonObject.getString("actualValue")).append(" ").append(jsonObject.getString("comparator"));
        if (jsonObject.has("warningThreshold")) {
            sb.append(" Warning : ").append(jsonObject.getString("warningThreshold"));
        }
        if (jsonObject.has("errorThreshold")) {
            sb.append(" Error : ").append(jsonObject.getString("errorThreshold"));
        }
        sb.append(" )");

        printStream.println(sb.toString());
    }

    private static class ReportTask {

        private final String serverUrl;
        private final String ceTaskUrl;

        public ReportTask(String serverUrl, String ceTaskUrl) {
            super();

            this.serverUrl = serverUrl;
            this.ceTaskUrl = ceTaskUrl;
        }
    }
}
