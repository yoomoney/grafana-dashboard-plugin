package ru.yandex.money.gradle.plugins.library.grafana.dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import ru.yandex.money.gradle.plugins.library.grafana.settings.GrafanaConnectionSettings;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/**
 * Класс для обновления/вставки dashboard
 */
public class DashboardUpserter {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final CloseableHttpClient client;
    private final GrafanaConnectionSettings grafanaConnectionSettings;

    /**
     * Конструктор
     *
     * @param client                    http клиент для внешних вызовов
     * @param grafanaConnectionSettings настройки подключения к Grafana
     */
    public DashboardUpserter(CloseableHttpClient client, GrafanaConnectionSettings grafanaConnectionSettings) {
        this.client = client;
        this.grafanaConnectionSettings = grafanaConnectionSettings;
    }

    /**
     * Обновление/вставка dashboard в Grafana
     *
     * @param dashboardContent содержимое dashboard
     * @throws IOException в случае различных проблем с IO при работе с dashboard
     */
    public void upsertDashboard(String dashboardContent) throws IOException {
        GrafanaDashboard newDashboard = OBJECT_MAPPER.readValue(dashboardContent, GrafanaDashboard.class);
        Optional<GrafanaDashboard> oldDashboard = getDashboardFromGrafana(newDashboard.getTitle());

        String fixedDashboardContext = fixDashboardIdAndVersion(dashboardContent, newDashboard, oldDashboard.orElse(null));

        sendContentToGrafana(fixedDashboardContext);
    }

    /**
     * Исправление версии и id содержимого dashboard для корректного обновления/вставки
     *
     * @param dashboardContent содержимое dashboard, которые нужно поправить
     * @param newDashboard     настройки нового dashboard
     * @param oldDashboard     настройки старого dashboard
     * @return откорректированное содержимое dashboard
     */
    private static String fixDashboardIdAndVersion(String dashboardContent, GrafanaDashboard newDashboard,
                                                   @Nullable GrafanaDashboard oldDashboard) {
        if (oldDashboard != null) {
            return dashboardContent
                    .replaceAll("\"version\": " + newDashboard.getVersion(), "\"version\":" + oldDashboard.getVersion())
                    .replaceAll("\"id\": " + newDashboard.getId(), "\"id\":" + oldDashboard.getId());
        }
        if (newDashboard.getId() != null) {
            return dashboardContent.replaceAll("\"id\": " + newDashboard.getId(), "\"id\": null");
        }
        return dashboardContent;
    }

    /**
     * Отсылка данных нового dashboard в Grafana
     *
     * @param dashboardContent содержимое dashboard
     * @throws IOException в случае IO проблем
     */
    private void sendContentToGrafana(String dashboardContent) throws IOException {
        HttpPost request = new HttpPost(grafanaConnectionSettings.getUrl() + "/api/dashboards/db");
        request.setHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        request.setHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.AUTHORIZATION, getAuthHeader());

        request.setEntity(new StringEntity("{\"overwrite\": false, \"dashboard\": " + dashboardContent + '}'));

        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HTTP_OK) {
                throw new IllegalStateException("Error send content to grafana: code={}" +
                        getHttpResponseBodyAsString(response) + statusCode);
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

    /**
     * Получение настроек dashboard из Grafana по title
     *
     * @param dashboardTitle название dashboard
     * @return настройки дашборда с указанным title
     * @throws IOException в случае IO проблем
     */
    private Optional<GrafanaDashboard> getDashboardFromGrafana(String dashboardTitle) throws IOException {
        HttpGet request = new HttpGet(grafanaConnectionSettings.getUrl() + "/api/dashboards/db/" + dashboardTitle);
        request.setHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        request.setHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.AUTHORIZATION, getAuthHeader());

        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HTTP_NOT_FOUND) {
                return Optional.empty();
            }
            if (statusCode != HTTP_OK) {
                throw new IllegalStateException(String.format("Error during get dashboard: title=%s, code=%d", response, statusCode));
            }
            GrafanaGetDashboardResponse dashboardResponse = OBJECT_MAPPER.readValue(response.getEntity()
                    .getContent(), GrafanaGetDashboardResponse.class);
            return Optional.ofNullable(dashboardResponse.getDashboard());
        }
    }

    private String getAuthHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (grafanaConnectionSettings.getUser() + ':' + grafanaConnectionSettings.getPassword()).getBytes(UTF_8));
    }

}