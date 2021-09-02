package ru.yoomoney.gradle.plugins.grafana;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskAction;
import ru.yoomoney.gradle.plugins.grafana.impl.GrafanaDashboard;
import ru.yoomoney.gradle.plugins.grafana.impl.GrafanaDashboardCollector;
import ru.yoomoney.gradle.plugins.grafana.impl.GrafanaDashboardUploader;
import ru.yoomoney.gradle.plugins.grafana.impl.GrafanaUploadSettings;
import ru.yoomoney.gradle.plugins.grafana.impl.KotlinScriptContentCreator;
import ru.yoomoney.gradle.plugins.grafana.impl.RawContentCreator;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static ru.yoomoney.gradle.plugins.grafana.GrafanaDashboardPlugin.DASHBOARDS_FROM_ARTIFACT_DIR;

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
                .withApiToken(grafanaDashboardExtension.apiToken)
                .withUser(grafanaDashboardExtension.user)
                .withPassword(grafanaDashboardExtension.password)
                .withFolderId(grafanaDashboardExtension.folderId)
                .withOverwrite(grafanaDashboardExtension.overwrite)
                .withTrustAllSslCertificates(grafanaDashboardExtension.trustAllSslCertificates)
                .build();

        GrafanaDashboardUploader grafanaUploader = new GrafanaDashboardUploader(grafanaUploadSettings);

        List<GrafanaDashboard> dashboardsContent = getDashboardsContent(grafanaFromArtifactConfiguration,
                Paths.get(getProject().getBuildDir().toString(), DASHBOARDS_FROM_ARTIFACT_DIR).toFile());
        grafanaUploader.uploadDashboards(dashboardsContent);

        List<GrafanaDashboard> dashboardsContentFromDir = getDashboardsContent(grafanaFromDirConfiguration,
                Paths.get(getProject().getProjectDir().toString(), grafanaDashboardExtension.dir).toFile());
        grafanaUploader.uploadDashboards(dashboardsContentFromDir);
    }

    private List<GrafanaDashboard> getDashboardsContent(Configuration configuration, File targetDir) {
        return new GrafanaDashboardCollector(Arrays.asList(new RawContentCreator(), kotlinScriptContentCreator(configuration)))
                .collectDashboards(targetDir);
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