package ru.yandex.money.gradle.plugins.grafana.dashboard;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.file.FileTree;
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency;
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
     * SourceSet name for common dashboards
     */
    static final String GRAFANA_COMMON_SOURCE_SET_NAME = "grafanaCommon";
    private static final String GRAFANA_CUSTOM_SOURCE_SET_NAME = "grafanaCustom";
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

        Configuration grafanaCustomConfiguration = configureCustomSourceSets(target, grafanaDashboardExtension);
        Configuration grafanaCommonConfiguration = configureCommonSourceSets(target);

        target.afterEvaluate(project -> {
            createUploadGrafanaDashboardTask(project,
                    grafanaCustomConfiguration, grafanaCommonConfiguration, grafanaDashboardExtension);
            createCopyAdditionalDashboardsTask(grafanaCommonConfiguration, target, grafanaDashboardExtension);
        });
    }

    private static GrafanaDashboardExtension getGrafanaDashboardExtensionWithDefaults(Project target) {
        GrafanaDashboardExtension grafanaDashboardExtension = target.getExtensions()
                .create("grafana", GrafanaDashboardExtension.class);
        grafanaDashboardExtension.classpath = target.files();
        return grafanaDashboardExtension;
    }

    private Configuration configureCustomSourceSets(Project project,
                                                    GrafanaDashboardExtension grafanaDashboardExtension) {
        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
        SourceSet grafanaSourceset = sourceSets.create(GRAFANA_CUSTOM_SOURCE_SET_NAME);
        grafanaSourceset.getJava().srcDir(new File(grafanaDashboardExtension.dir));

        Configuration grafanaCompileConfiguration = project.getConfigurations()
                .maybeCreate(GRAFANA_CUSTOM_SOURCE_SET_NAME + "Compile");

        return addKotlinDependencies(grafanaCompileConfiguration);
    }

    private Configuration configureCommonSourceSets(Project project) {
        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
        SourceSet grafanaSourceset = sourceSets.create(GRAFANA_COMMON_SOURCE_SET_NAME);
        grafanaSourceset.getJava()
                .srcDir(Paths.get(project.getBuildDir().toString(), GRAFANA_COMMON_SOURCE_SET_NAME).toString());

        Configuration grafanaCommonCompile = project.getConfigurations()
                .maybeCreate(GRAFANA_COMMON_SOURCE_SET_NAME + "Compile");

        return addKotlinDependencies(grafanaCommonCompile);
    }

    private Configuration addKotlinDependencies(Configuration grafanaConfiguration) {
        DependencySet grafanaCompileDependencies = grafanaConfiguration.getDependencies();
        grafanaCompileDependencies.add(new DefaultExternalModuleDependency(
                "org.jetbrains.kotlin", "kotlin-stdlib", "1.2.61"));
        grafanaCompileDependencies.add(new DefaultExternalModuleDependency(
                "org.jetbrains.kotlin", "kotlin-reflect", "1.2.61"));
        return grafanaConfiguration;
    }

    private void createCopyAdditionalDashboardsTask(Configuration grafanaCompileConfiguration, Project project,
                                        GrafanaDashboardExtension grafanaDashboardExtension) {
        if (grafanaDashboardExtension.additionalDashboardLibraryName == null) {
            return;
        }

        Copy task = project.getTasks().create("copyAdditionalDashboards", Copy.class);
        List<FileTree> files = grafanaCompileConfiguration.getFiles().stream()
                .filter(file -> file.getName().contains(grafanaDashboardExtension.additionalDashboardLibraryName))
                .map(project::zipTree)
                .collect(Collectors.toList());
        task.from(files);

        task.into(Paths.get(project.getBuildDir().toString(), GRAFANA_COMMON_SOURCE_SET_NAME).toString());

        project.getTasks().getByName(UPLOAD_TASK_NAME).dependsOn(task);
    }

    private static UploadGrafanaDashboardsTask createUploadGrafanaDashboardTask(
            Project target, Configuration grafanaCustomConfiguration,
            Configuration grafanaCommonConfiguration, GrafanaDashboardExtension grafanaDashboardExtension) {
        UploadGrafanaDashboardsTask task = target.getTasks()
                .create(UPLOAD_TASK_NAME, UploadGrafanaDashboardsTask.class);
        task.setGroup("other");
        task.setDescription("Upload Grafana dashboards");
        task.setGrafanaCustomConfiguration(grafanaCustomConfiguration);
        task.setGrafanaCommonConfiguration(grafanaCommonConfiguration);
        task.setGrafanaDashboardExtension(grafanaDashboardExtension);
        return task;
    }

}