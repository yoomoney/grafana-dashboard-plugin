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
}
