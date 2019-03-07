package ru.yandex.money.gradle.plugins.grafana.dashboard;

/**
 * Grafana plugin settings
 */
public class GrafanaDashboardExtension {

    /**
     * Url to Grafana
     */
    public String url;

    /**
     * Grafana username
     */
    public String user;

    /**
     * Grafana user password
     */
    public String password;

    /**
     * Directory with dashboards descriptions
     */
    public String dir = "grafana";

    /**
     * Folder id to save to http://docs.grafana.org/http_api/folder/
     */
    public String folderId = "0";

    /**
     * API key
     */
    public String apiKey;

    /**
     * Overwrite existing dashboards
     */
    public Boolean overwrite = true;
}
