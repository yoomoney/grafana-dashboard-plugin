package ru.yoomoney.tech.plugins.grafana;

import kotlin.KotlinVersion;
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
     * Directory, where dashboards from libraries will be saved
     */
    static final String DASHBOARDS_FROM_ARTIFACT_DIR = "grafana";
    private static final String GRAFANA_ARTIFACT_SOURCE_SET_NAME = "grafanaFromArtifact";
    private static final String GRAFANA_DIR_SOURCE_SET_NAME = "grafanaFromDir";
    private static final String GRAFANA_DASHBOARDS_CONFIGURATION_NAME = "grafanaDashboards";
    private static final String UPLOAD_TASK_NAME = "uploadGrafanaDashboards";
    private static final String COLLECT_TASK_NAME = "collectGrafanaDashboards";

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
        Configuration grafanaFromArtifactConfiguration = configureArtifactSourceSets(target);

        Configuration grafanaDashboardsConfiguration = target.getConfigurations()
                .maybeCreate(GRAFANA_DASHBOARDS_CONFIGURATION_NAME + "Compile");

        target.afterEvaluate(project -> {
            grafanaFromArtifactConfiguration.extendsFrom(grafanaDashboardsConfiguration);

            createUploadGrafanaDashboardTask(project,
                    grafanaFromDirConfiguration, grafanaFromArtifactConfiguration, grafanaDashboardExtension);

            createCollectGrafanaDashboardTask(project,
                    grafanaFromDirConfiguration, grafanaFromArtifactConfiguration, grafanaDashboardExtension);

            createExtractGrafanaDashboardsTask(grafanaDashboardsConfiguration, target);
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

        Configuration grafanaDirCompile = project.getConfigurations()
                .maybeCreate(GRAFANA_DIR_SOURCE_SET_NAME + "Compile");

        return addKotlinDependencies(grafanaDirCompile);
    }

    private Configuration configureArtifactSourceSets(Project project) {
        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
        SourceSet grafanaSourceset = sourceSets.create(GRAFANA_ARTIFACT_SOURCE_SET_NAME);
        grafanaSourceset.getJava()
                .srcDir(Paths.get(project.getBuildDir().toString(), GRAFANA_ARTIFACT_SOURCE_SET_NAME).toString());

        Configuration grafanaDirCompile = project.getConfigurations()
                .maybeCreate(GRAFANA_ARTIFACT_SOURCE_SET_NAME + "Compile");

        return addKotlinDependencies(grafanaDirCompile);
    }

    private Configuration addKotlinDependencies(Configuration grafanaConfiguration) {
        DependencySet grafanaCompileDependencies = grafanaConfiguration.getDependencies();
        grafanaCompileDependencies.add(new DefaultExternalModuleDependency(
                "org.jetbrains.kotlin", "kotlin-stdlib", KotlinVersion.CURRENT.toString()));
        grafanaCompileDependencies.add(new DefaultExternalModuleDependency(
                "org.jetbrains.kotlin", "kotlin-reflect", KotlinVersion.CURRENT.toString()));
        grafanaCompileDependencies.add(new DefaultExternalModuleDependency(
                "org.jetbrains.kotlin", "kotlin-scripting-compiler-embeddable", KotlinVersion.CURRENT.toString()));
        return grafanaConfiguration;
    }

    private void createExtractGrafanaDashboardsTask(Configuration grafanaDashboardsConfiguration, Project project) {
        Copy task = project.getTasks().create("extractGrafanaDashboards", Copy.class);
        List<FileTree> files = grafanaDashboardsConfiguration.getFiles().stream()
                .map(project::zipTree)
                .collect(Collectors.toList());
        task.from(files);

        task.into(Paths.get(project.getBuildDir().toString(), DASHBOARDS_FROM_ARTIFACT_DIR).toString());
        project.getTasks().getByName(UPLOAD_TASK_NAME).dependsOn(task);
        project.getTasks().getByName(COLLECT_TASK_NAME).dependsOn(task);
    }

    private static void createUploadGrafanaDashboardTask(
            Project target, Configuration grafanaFromDirConfiguration,
            Configuration grafanaFromArtifactConfiguration, GrafanaDashboardExtension grafanaDashboardExtension) {
        UploadGrafanaDashboardsTask task = target.getTasks()
                .create(UPLOAD_TASK_NAME, UploadGrafanaDashboardsTask.class);
        task.setGroup("other");
        task.setDescription("Upload Grafana dashboards");
        task.setGrafanaFromDirConfiguration(grafanaFromDirConfiguration);
        task.setGrafanaFromArtifactConfiguration(grafanaFromArtifactConfiguration);
        task.setGrafanaDashboardExtension(grafanaDashboardExtension);
    }

    private static void createCollectGrafanaDashboardTask(
            Project target, Configuration grafanaFromDirConfiguration,
            Configuration grafanaFromArtifactConfiguration, GrafanaDashboardExtension grafanaDashboardExtension) {

        CollectGrafanaDashboardsTask task = target.getTasks()
                .create(COLLECT_TASK_NAME, CollectGrafanaDashboardsTask.class);
        task.setGroup("other");
        task.setDescription("Collect grafana dashboards");
        task.setGrafanaFromDirConfiguration(grafanaFromDirConfiguration);
        task.setGrafanaFromArtifactConfiguration(grafanaFromArtifactConfiguration);
        task.setGrafanaDashboardExtension(grafanaDashboardExtension);
    }
}