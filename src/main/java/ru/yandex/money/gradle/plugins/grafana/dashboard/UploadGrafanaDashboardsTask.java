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

import static ru.yandex.money.gradle.plugins.grafana.dashboard.GrafanaDashboardPlugin.GRAFANA_COMMON_SOURCE_SET_NAME;

/**
 * Task for uploading all dashboards into Grafana
 */
public class UploadGrafanaDashboardsTask extends DefaultTask {

    private Configuration grafanaCustomConfiguration;
    private Configuration grafanaCommonConfiguration;
    private GrafanaDashboardExtension grafanaDashboardExtension;

    /**
     * Main action
     */
    @TaskAction
    void uploadGrafanaDashboards() {
        GrafanaUploadSettings grafanaUploadSettings = new GrafanaUploadSettings.Builder()
                .withUrl(grafanaDashboardExtension.url)
                .withUser(grafanaDashboardExtension.user)
                .withPassword(grafanaDashboardExtension.password)
                .withFolderId(grafanaDashboardExtension.folderId)
                .withOverwrite(grafanaDashboardExtension.overwrite)
                .build();

        new GrafanaDashboardUploader(
                Arrays.asList(new RawContentCreator(), kotlinScriptContentCreator(grafanaCustomConfiguration)),
                grafanaUploadSettings)
                .uploadDashboards(Paths.get(getProject().getProjectDir().toString(),
                        grafanaDashboardExtension.dir).toFile());

        new GrafanaDashboardUploader(
                Arrays.asList(new RawContentCreator(), kotlinScriptContentCreator(grafanaCommonConfiguration)),
                grafanaUploadSettings)
                .uploadDashboards(Paths.get(getProject().getBuildDir().toString(),
                        GRAFANA_COMMON_SOURCE_SET_NAME).toFile());
    }

    private KotlinScriptContentCreator kotlinScriptContentCreator(Configuration grafanaConfiguration) {
        return new KotlinScriptContentCreator(grafanaConfiguration, grafanaDashboardExtension.classpath);
    }

    void setGrafanaCommonConfiguration(Configuration commonConfiguration) {
        this.grafanaCommonConfiguration = commonConfiguration;
    }

    void setGrafanaCustomConfiguration(Configuration customConfiguration) {
        this.grafanaCustomConfiguration = customConfiguration;
    }

    void setGrafanaDashboardExtension(GrafanaDashboardExtension grafanaDashboardExtension) {
        this.grafanaDashboardExtension = grafanaDashboardExtension;
    }
}