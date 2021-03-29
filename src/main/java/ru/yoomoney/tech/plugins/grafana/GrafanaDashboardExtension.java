package ru.yoomoney.tech.plugins.grafana;

import org.gradle.api.file.FileCollection;

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
     * Overwrite existing dashboards
     */
    public Boolean overwrite = true;

    /**
     * Additional classpath to use during dashboard scripts evaluation
     */
    public FileCollection classpath;

    /**
     * Print collected dashboards to stdout
     */
    public Boolean printCollectedDashboards = false;

}
