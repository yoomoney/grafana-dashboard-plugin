package ru.yoomoney.gradle.plugins.grafana;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.json.JSONObject;
import ru.yoomoney.gradle.plugins.grafana.impl.GrafanaDashboard;
import ru.yoomoney.gradle.plugins.grafana.impl.GrafanaDashboardCollector;
import ru.yoomoney.gradle.plugins.grafana.impl.KotlinScriptContentCreator;
import ru.yoomoney.gradle.plugins.grafana.impl.RawContentCreator;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Task for collect and print all dashboards
 */
public class CollectGrafanaDashboardsTask extends DefaultTask {

    private final Logger log = Logging.getLogger(CollectGrafanaDashboardsTask.class);

    private Configuration grafanaFromDirConfiguration;
    private Configuration grafanaFromArtifactConfiguration;
    private GrafanaDashboardExtension grafanaDashboardExtension;

    /**
     * Main action
     */
    @TaskAction
    void collectGrafanaDashboards() {

        List<GrafanaDashboard> dashboardsContentFromArtifact = getDashboardsContent(grafanaFromArtifactConfiguration,
                Paths.get(getProject().getBuildDir().toString(), GrafanaDashboardPlugin.DASHBOARDS_FROM_ARTIFACT_DIR).toFile());
        printDashboards(dashboardsContentFromArtifact);

        List<GrafanaDashboard> dashboardsContentFromDir = getDashboardsContent(grafanaFromDirConfiguration,
                Paths.get(getProject().getProjectDir().toString(), grafanaDashboardExtension.dir).toFile());
        printDashboards(dashboardsContentFromDir);
    }

    private List<GrafanaDashboard> getDashboardsContent(Configuration configuration, File targetDir) {
        return new GrafanaDashboardCollector(Arrays.asList(new RawContentCreator(), kotlinScriptContentCreator(configuration)))
                .collectDashboards(targetDir);
    }

    private void printDashboards(List<GrafanaDashboard> dashboardsContent) {
        dashboardsContent
                .forEach(dashboard -> log.lifecycle("Collect dashboard: fileName={} json={}",
                        dashboard.getFileName(), new JSONObject(dashboard.getContent()).toString(4)));

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