package ru.yandex.money.gradle.plugins.grafana.dashboard;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskAction;
import ru.yandex.money.gradle.plugins.grafana.dashboard.impl.GrafanaDashboardUploader;
import ru.yandex.money.gradle.plugins.grafana.dashboard.impl.GrafanaUploadSettings;
import ru.yandex.money.gradle.plugins.grafana.dashboard.impl.KotlinScriptContentCreator;
import ru.yandex.money.gradle.plugins.grafana.dashboard.impl.RawContentCreator;

import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Task for uploading all dashboards into Grafana
 */
public class UploadGrafanaDashboardsTask extends DefaultTask {

    private Configuration grafanaConfiguration;
    private GrafanaDashboardExtension grafanaDashboardExtension;

    /**
     * Main action
     */
    @TaskAction
    void uploadGrafanaDashboards() {
        new GrafanaDashboardUploader(
                Arrays.asList(new RawContentCreator(), kotlinScriptContentCreator()),
                new GrafanaUploadSettings.Builder()
                        .withUrl(grafanaDashboardExtension.url)
                        .withUser(grafanaDashboardExtension.user)
                        .withPassword(grafanaDashboardExtension.password)
                        .withFolderId(grafanaDashboardExtension.folderId)
                        .withOverwrite(grafanaDashboardExtension.overwrite)
                        .build())
                .uploadDashboards(Paths.get(getProject().getProjectDir().toString(), grafanaDashboardExtension.dir).toFile());
    }

    private KotlinScriptContentCreator kotlinScriptContentCreator() {
        return new KotlinScriptContentCreator(grafanaConfiguration, grafanaDashboardExtension.classpath);
    }

    void setGrafanaConfiguration(Configuration configuration) {
        this.grafanaConfiguration = configuration;
    }

    void setGrafanaDashboardExtension(GrafanaDashboardExtension grafanaDashboardExtension) {
        this.grafanaDashboardExtension = grafanaDashboardExtension;
    }

}