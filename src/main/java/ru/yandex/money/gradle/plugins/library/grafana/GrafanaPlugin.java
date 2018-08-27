package ru.yandex.money.gradle.plugins.library.grafana;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import ru.yandex.money.gradle.plugins.library.grafana.settings.GrafanaConnectionExtension;
import ru.yandex.money.gradle.plugins.library.grafana.settings.GrafanaConnectionSettings;

/**
 * Плагин для работы с Grafana
 */
@SuppressWarnings("ClassWithoutConstructor")
public class GrafanaPlugin implements Plugin<Project> {

    private static final String GRAFANA_EXTENSION_NAME = "grafana";
    private static final String GRAFANA_CONFIGURATION_NAME = "grafana";

    /**
     * Действия при применении плагина GrafanaPlugin
     *
     * @param target проект, для которого применяется плагин
     */
    @Override
    public void apply(Project target) {
        Configuration grafanaConfiguration = target.getConfigurations().maybeCreate(GRAFANA_CONFIGURATION_NAME);
        UpsertGrafanaDashboardsTask upsertGrafanaDashboardsTask = upsertGrafanaDashboardTask(
                target,
                grafanaConfiguration
        );

        GrafanaConnectionExtension grafanaConnection =
                target.getExtensions().create(GRAFANA_EXTENSION_NAME, GrafanaConnectionExtension.class);

        target.afterEvaluate(project ->
                upsertGrafanaDashboardsTask.setGrafanaConnectionSettings(newGrafanaConnectionSettings(grafanaConnection)));
    }

    private static UpsertGrafanaDashboardsTask upsertGrafanaDashboardTask(Project target, Configuration configuration) {
        UpsertGrafanaDashboardsTask task = target.getTasks().create(UpsertGrafanaDashboardsTask.TASK_NAME,
                UpsertGrafanaDashboardsTask.class);
        task.setGroup("other");
        task.setDescription("Upsert Grafana dashboard");
        task.setGrafanaConfiguration(configuration);
        return task;
    }

    private static GrafanaConnectionSettings newGrafanaConnectionSettings(GrafanaConnectionExtension grafanaConnection) {
        return new GrafanaConnectionSettings.Builder()
                .withUrl(grafanaConnection.url)
                .withUser(grafanaConnection.user)
                .withPassword(grafanaConnection.password)
                .build();
    }

}