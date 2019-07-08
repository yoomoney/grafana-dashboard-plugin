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

import static ru.yandex.money.gradle.plugins.grafana.dashboard.GrafanaDashboardPlugin.DASHBOARDS_FROM_ARTIFACT_DIR;

/**
 * Task for uploading all dashboards into Grafana
 */
public class UploadGrafanaDashboardsTask extends DefaultTask {

    private Configuration grafanaFromDirConfiguration;
    private Configuration grafanaFromArtifactConfiguration;
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
                Arrays.asList(new RawContentCreator(), kotlinScriptContentCreator(grafanaFromDirConfiguration)),
                grafanaUploadSettings)
                .uploadDashboards(Paths.get(getProject().getProjectDir().toString(),
                        grafanaDashboardExtension.dir).toFile());

        new GrafanaDashboardUploader(
                Arrays.asList(new RawContentCreator(), kotlinScriptContentCreator(grafanaFromArtifactConfiguration)),
                grafanaUploadSettings)
                .uploadDashboards(Paths.get(getProject().getBuildDir().toString(),
                        DASHBOARDS_FROM_ARTIFACT_DIR).toFile());
    }

    private KotlinScriptContentCreator kotlinScriptContentCreator(Configuration grafanaConfiguration) {
        return new KotlinScriptContentCreator(grafanaConfiguration, grafanaDashboardExtension.classpath);
    }

    void setGrafanaFromArtifactConfiguration(Configuration artifactConfiguration) {
        this.grafanaFromArtifactConfiguration = artifactConfiguration;
    }

    void setGrafanaFromDirConfiguration(Configuration dirConfiguration) {
        this.grafanaFromDirConfiguration = dirConfiguration;
    }

    void setGrafanaDashboardExtension(GrafanaDashboardExtension grafanaDashboardExtension) {
        this.grafanaDashboardExtension = grafanaDashboardExtension;
    }
}