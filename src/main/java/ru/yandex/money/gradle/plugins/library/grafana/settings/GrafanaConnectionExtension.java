package ru.yandex.money.gradle.plugins.library.grafana.settings;

/**
 * Настройки подключения к Grafana
 */
@SuppressWarnings({"PublicField", "InstanceVariableMayNotBeInitialized"})
public class GrafanaConnectionExtension {

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
     * Конструктор
     */
    @SuppressWarnings("CallToSystemGetenv")
    public GrafanaConnectionExtension() {
        user = System.getenv("GRAFANA_USER");
        password = System.getenv("GRAFANA_PASSWORD");
    }
}
