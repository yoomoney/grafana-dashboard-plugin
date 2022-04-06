package ru.yoomoney.gradle.plugins.grafana;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.SourceSet;
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

    private SourceSet grafanaFromDirSourceSet;
    private SourceSet grafanaFromArtifactSourceSet;
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

        List<GrafanaDashboard> dashboardsContent = getDashboardsContent(grafanaFromArtifactSourceSet,
                Paths.get(getProject().getBuildDir().toString(), DASHBOARDS_FROM_ARTIFACT_DIR).toFile());
        grafanaUploader.uploadDashboards(dashboardsContent);

        List<GrafanaDashboard> dashboardsContentFromDir = getDashboardsContent(grafanaFromDirSourceSet,
                Paths.get(getProject().getProjectDir().toString(), grafanaDashboardExtension.dir).toFile());
        grafanaUploader.uploadDashboards(dashboardsContentFromDir);
    }

    private List<GrafanaDashboard> getDashboardsContent(SourceSet sourceSet, File targetDir) {
        return new GrafanaDashboardCollector(Arrays.asList(new RawContentCreator(), kotlinScriptContentCreator(sourceSet)))
                .collectDashboards(targetDir);
    }

    private KotlinScriptContentCreator kotlinScriptContentCreator(SourceSet grafanaSourceSet) {
        return new KotlinScriptContentCreator(grafanaSourceSet, grafanaDashboardExtension.classpath);
    }

    void setGrafanaFromArtifactSourceSet(SourceSet sourceSet) {
        this.grafanaFromArtifactSourceSet = sourceSet;
    }

    void setGrafanaFromDirSourceSet(SourceSet dirSourceSet) {
        this.grafanaFromDirSourceSet = dirSourceSet;
    }

    void setGrafanaDashboardExtension(GrafanaDashboardExtension grafanaDashboardExtension) {
        this.grafanaDashboardExtension = grafanaDashboardExtension;
    }
}