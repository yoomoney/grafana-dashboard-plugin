package ru.yandex.money.gradle.plugins.grafana.dashboard;

/**
 * Настройки подключения к Grafana
 */
public class GrafanaDashboardExtension {

    /**
     * Url для доступа к Grafana
     */
    public String url;

    /**
     * Grafana user для доступа к Grafana
     */
    public String user;

    /**
     * Grafana password для доступа к Grafana
     */
    public String password;

    /**
     * Директория с описанием дашбордов
     */
    public String dir = "grafana";

    /**
     * Идентификатор папки для сохранения дашборда http://docs.grafana.org/http_api/folder/
     */
    public String folderId = "0";

    /**
     * Перезаписывать-ли содержимое дашборда
     */
    public Boolean overwrite = true;
}
