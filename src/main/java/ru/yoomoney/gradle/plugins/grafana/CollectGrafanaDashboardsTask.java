package ru.yoomoney.gradle.plugins.grafana;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.SourceSet;
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

    private SourceSet grafanaFromDirSourceSet;
    private SourceSet grafanaFromArtifactSourceSet;
    private GrafanaDashboardExtension grafanaDashboardExtension;

    /**
     * Main action
     */
    @TaskAction
    void collectGrafanaDashboards() {
        log.lifecycle("Collect Grafana dashboards: printCollectedDashboards={}", grafanaDashboardExtension.printCollectedDashboards);

        List<GrafanaDashboard> dashboardsContentFromArtifact = getDashboardsContent(grafanaFromArtifactSourceSet,
                Paths.get(getProject().getBuildDir().toString(), GrafanaDashboardPlugin.DASHBOARDS_FROM_ARTIFACT_DIR).toFile());

        List<GrafanaDashboard> dashboardsContentFromDir = getDashboardsContent(grafanaFromDirSourceSet,
                Paths.get(getProject().getProjectDir().toString(), grafanaDashboardExtension.dir).toFile());

        if (!grafanaDashboardExtension.printCollectedDashboards) {
            return;
        }

        printDashboards(dashboardsContentFromArtifact);
        printDashboards(dashboardsContentFromDir);
    }

    private List<GrafanaDashboard> getDashboardsContent(SourceSet sourceSet, File targetDir) {
        return new GrafanaDashboardCollector(Arrays.asList(new RawContentCreator(), kotlinScriptContentCreator(sourceSet)))
                .collectDashboards(targetDir);
    }

    private void printDashboards(List<GrafanaDashboard> dashboardsContent) {
        dashboardsContent
                .forEach(dashboard -> log.lifecycle("Collect dashboard: fileName={} json={}",
                        dashboard.getFileName(), new JSONObject(dashboard.getContent()).toString(4)));

    }

    private KotlinScriptContentCreator kotlinScriptContentCreator(SourceSet sourceSet) {
        return new KotlinScriptContentCreator(sourceSet, grafanaDashboardExtension.classpath);
    }

    void setGrafanaFromArtifactSourceSet(SourceSet artifactConfiguration) {
        this.grafanaFromArtifactSourceSet = artifactConfiguration;
    }

    void setGrafanaFromDirSourceSet(SourceSet grafanaFromDirSourceSet) {
        this.grafanaFromDirSourceSet = grafanaFromDirSourceSet;
    }

    void setGrafanaDashboardExtension(GrafanaDashboardExtension grafanaDashboardExtension) {
        this.grafanaDashboardExtension = grafanaDashboardExtension;
    }
}