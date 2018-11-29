package ru.yandex.money.gradle.plugins.grafana.dashboard.impl;

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
 * Класс для обновления/вставки dashboard
 */
class DashboardSender {
    private final CloseableHttpClient client;
    private final GrafanaConnectionSettings grafanaConnectionSettings;

    /**
     * Конструктор
     *
     * @param client                    http клиент для внешних вызовов
     * @param grafanaConnectionSettings настройки подключения к Grafana
     */
    DashboardSender(CloseableHttpClient client, GrafanaConnectionSettings grafanaConnectionSettings) {
        this.client = client;
        this.grafanaConnectionSettings = grafanaConnectionSettings;
    }

    /**
     * Отсылка данных нового dashboard в Grafana
     *
     * @param dashboardContent содержимое dashboard
     * @throws IOException в случае IO проблем
     */
    void sendContentToGrafana(String dashboardContent) throws IOException {
        HttpPost request = new HttpPost(grafanaConnectionSettings.getUrl() + "/api/dashboards/db");
        request.setHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        request.setHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.AUTHORIZATION, getAuthHeader());

        request.setEntity(new StringEntity("{\"message\": \"Auto import\", \"folderId\": 0, \"overwrite\": true, " +
                "\"dashboard\": " + dashboardContent + '}', Charsets.UTF_8));

        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HTTP_OK) {
                throw new IllegalStateException("Error send content to grafana: code="+ statusCode +
                        ", response=" + getHttpResponseBodyAsString(response));
            }
        }
    }

    /**
     * Получение ответа в виде строки
     *
     * @param response http ответ
     * @return строка с содержимым ответа
     * @throws IOException в случае IO проблем
     */
    private static String getHttpResponseBodyAsString(HttpResponse response) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private String getAuthHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (grafanaConnectionSettings.getUser() + ':' + grafanaConnectionSettings.getPassword()).getBytes(UTF_8));
    }

}