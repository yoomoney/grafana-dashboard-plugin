package ru.yandex.money.gradle.plugins.grafana.dashboard;

import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.collections.FileCollectionAdapter;

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
     * Library name with additional dashboard
     */
    public String additionalDashboardLibraryName;

}
