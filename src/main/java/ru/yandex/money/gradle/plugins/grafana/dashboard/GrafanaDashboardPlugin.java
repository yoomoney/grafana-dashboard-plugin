package ru.yandex.money.gradle.plugins.grafana.dashboard;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Grafana dashboards plugin
 */
@SuppressWarnings("ClassWithoutConstructor")
public class GrafanaDashboardPlugin implements Plugin<Project> {
    /**
     * Directory, where dashboards from libraries will be saved
     */
    static final String DASHBOARDS_FROM_LIBRARY_DIR = "grafana";
    private static final String GRAFANA_LIBRARY_SOURCE_SET_NAME = "grafanaFromLibrary";
    private static final String GRAFANA_DIR_SOURCE_SET_NAME = "grafanaFromDir";
    private static final String UPLOAD_TASK_NAME = "uploadGrafanaDashboards";

    /**
     * Actions when applying GrafanaDashboardPlugin to a project
     *
     * @param target project to apply to
     */
    @Override
    public void apply(Project target) {
        target.getPluginManager().apply(JavaBasePlugin.class);
        GrafanaDashboardExtension grafanaDashboardExtension = getGrafanaDashboardExtensionWithDefaults(target);

        Configuration grafanaFromDirConfiguration = configureDirSourceSets(target, grafanaDashboardExtension);
        Configuration grafanaFromLibraryConfiguration = configureLibrarySourceSets(target);

        target.afterEvaluate(project -> {
            createUploadGrafanaDashboardTask(project,
                    grafanaFromDirConfiguration, grafanaFromLibraryConfiguration, grafanaDashboardExtension);
            createExtractGrafanaDashboardsTask(grafanaFromLibraryConfiguration, target, grafanaDashboardExtension);
        });
    }

    private static GrafanaDashboardExtension getGrafanaDashboardExtensionWithDefaults(Project target) {
        GrafanaDashboardExtension grafanaDashboardExtension = target.getExtensions()
                .create("grafana", GrafanaDashboardExtension.class);
        grafanaDashboardExtension.classpath = target.files();
        return grafanaDashboardExtension;
    }

    private Configuration configureDirSourceSets(Project project,
                                                 GrafanaDashboardExtension grafanaDashboardExtension) {
        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
        SourceSet grafanaSourceset = sourceSets.create(GRAFANA_DIR_SOURCE_SET_NAME);
        grafanaSourceset.getJava().srcDir(new File(grafanaDashboardExtension.dir));

        return project.getConfigurations()
                .maybeCreate(GRAFANA_DIR_SOURCE_SET_NAME + "Compile");
    }

    private Configuration configureLibrarySourceSets(Project project) {
        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
        SourceSet grafanaSourceset = sourceSets.create(GRAFANA_LIBRARY_SOURCE_SET_NAME);
        grafanaSourceset.getJava()
                .srcDir(Paths.get(project.getBuildDir().toString(), GRAFANA_LIBRARY_SOURCE_SET_NAME).toString());

        return project.getConfigurations()
                .maybeCreate(GRAFANA_LIBRARY_SOURCE_SET_NAME + "Compile");
    }

    private void createExtractGrafanaDashboardsTask(Configuration grafanaCompileConfiguration, Project project,
                                                    GrafanaDashboardExtension grafanaDashboardExtension) {
        if (grafanaDashboardExtension.additionalDashboardLibraries.isEmpty()) {
            return;
        }

        Copy task = project.getTasks().create("extractGrafanaDashboards", Copy.class);
        List<FileTree> files = grafanaCompileConfiguration.getFiles().stream()
                .filter(file -> grafanaDashboardExtension
                        .additionalDashboardLibraries.stream()
                        .anyMatch(library -> file.getName().contains(library)))
                .map(project::zipTree)
                .collect(Collectors.toList());
        task.from(files);

        task.into(Paths.get(project.getBuildDir().toString(), DASHBOARDS_FROM_LIBRARY_DIR).toString());
        project.getTasks().getByName(UPLOAD_TASK_NAME).dependsOn(task);
    }

    private static void createUploadGrafanaDashboardTask(
            Project target, Configuration grafanaFromDirConfiguration,
            Configuration grafanaFromLibraryConfiguration, GrafanaDashboardExtension grafanaDashboardExtension) {
        UploadGrafanaDashboardsTask task = target.getTasks()
                .create(UPLOAD_TASK_NAME, UploadGrafanaDashboardsTask.class);
        task.setGroup("other");
        task.setDescription("Upload Grafana dashboards");
        task.setGrafanaFromDirConfiguration(grafanaFromDirConfiguration);
        task.setGrafanaFromLibraryConfiguration(grafanaFromLibraryConfiguration);
        task.setGrafanaDashboardExtension(grafanaDashboardExtension);
    }

}