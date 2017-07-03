package com.talanlabs.sonargatebreaker;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Checker {

    private static final Logger LOGGER = LoggerFactory.getLogger(Checker.class);

    private String reportTaskPath;
    private String username;
    private String password;
    private int nbRetry = 50;
    private long sleep = 1000 * 2;
    private boolean failOnWarn = false;

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
        return Pair.of(jsonNode.getString("analysisId"), jsonNode.getString("status"));
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
        JsonNode jsonNode = jsonResponse.getBody();
        return jsonNode.getObject().getJSONObject("projectStatus").getString("status");
    }

    private ReportTask readReportTask() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(reportTaskPath));

        if (properties.getProperty("serverUrl") == null || properties.getProperty("ceTaskId") == null || properties.getProperty("ceTaskUrl") == null) {
            throw new IOException("Failed to read file, not content serverUrl or ceTaskId or ceTaskUrl");
        }

        return new ReportTask(properties.getProperty("serverUrl"), properties.getProperty("ceTaskId"), properties.getProperty("ceTaskUrl"));
    }

    private static class ReportTask {

        private String serverUrl;
        private String ceTaskId;
        private String ceTaskUrl;

        public ReportTask(String serverUrl, String ceTaskId, String ceTaskUrl) {
            super();

            this.serverUrl = serverUrl;
            this.ceTaskId = ceTaskId;
            this.ceTaskUrl = ceTaskUrl;
        }

        public String getServerUrl() {
            return serverUrl;
        }

        public String getCeTaskId() {
            return ceTaskId;
        }

        public String getCeTaskUrl() {
            return ceTaskUrl;
        }
    }
}
