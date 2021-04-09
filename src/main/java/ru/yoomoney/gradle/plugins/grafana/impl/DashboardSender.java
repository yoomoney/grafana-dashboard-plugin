package ru.yoomoney.gradle.plugins.grafana.impl;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.stream.Collectors;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/**
 * Dashboards update/insert code
 */
class DashboardSender {
    private final CloseableHttpClient client;
    private final GrafanaUploadSettings grafanaUploadSettings;

    /**
     * Constructor
     *
     * @param client                Http client for api calls
     * @param grafanaUploadSettings Grafana connection settings
     */
    DashboardSender(CloseableHttpClient client, GrafanaUploadSettings grafanaUploadSettings) {
        this.client = client;
        this.grafanaUploadSettings = grafanaUploadSettings;
    }

    /**
     * Upload dashboards content to Grafana
     *
     * @param dashboardContent dashboard content
     * @throws IOException in case of errors with IO
     */
    void sendContentToGrafana(String dashboardContent) throws IOException {
        HttpPost request = new HttpPost(grafanaUploadSettings.getUrl() + "/api/dashboards/db");
        request.setHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        request.setHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.AUTHORIZATION, getAuthHeader());

        request.setEntity(new StringEntity("{\"message\": \"Auto import\"" +
                ", \"folderId\": " + grafanaUploadSettings.getFolderId() +
                ", \"overwrite\": " + grafanaUploadSettings.isOverwrite() +
                ", \"dashboard\": " + dashboardContent + '}', Charsets.UTF_8));

        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HTTP_OK) {
                throw new IllegalStateException("Error send content to grafana: code=" + statusCode +
                        ", response=" + getHttpResponseBodyAsString(response));
            }
        }
    }

    /**
     * Get http response as a string
     *
     * @param response http response
     * @return string with response content
     * @throws IOException in case of errors with IO
     */
    private static String getHttpResponseBodyAsString(HttpResponse response) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private String getAuthHeader() {
        if (grafanaUploadSettings.getApiToken() != null && !grafanaUploadSettings.getApiToken().isEmpty()) {
            return "Bearer " + grafanaUploadSettings.getApiToken();
        }
        return "Basic " + Base64.getEncoder().encodeToString(
                (grafanaUploadSettings.getUser() + ':' + grafanaUploadSettings.getPassword()).getBytes(UTF_8));
    }

}