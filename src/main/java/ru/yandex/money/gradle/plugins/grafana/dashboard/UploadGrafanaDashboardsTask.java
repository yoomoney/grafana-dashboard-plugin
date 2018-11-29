package ru.yandex.money.gradle.plugins.grafana.dashboard;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskAction;
import ru.yandex.money.gradle.plugins.grafana.dashboard.impl.GrafanaConnectionSettings;
import ru.yandex.money.gradle.plugins.grafana.dashboard.impl.GrafanaDashboardUploader;
import ru.yandex.money.gradle.plugins.grafana.dashboard.impl.KotlinScriptContentCreator;
import ru.yandex.money.gradle.plugins.grafana.dashboard.impl.RawContentCreator;

import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Task для загрузки всех dashboard в Grafana
 */
public class UploadGrafanaDashboardsTask extends DefaultTask {

    private Configuration grafanaConfiguration;
    private GrafanaDashboardExtension grafanaDashboardExtension;

    /**
     * Основное действие
     */
    @TaskAction
    void uploadGrafanaDashboards() {
        new GrafanaDashboardUploader(
                Arrays.asList(new RawContentCreator(), new KotlinScriptContentCreator(grafanaConfiguration)),
                new GrafanaConnectionSettings.Builder()
                        .withUrl(grafanaDashboardExtension.url)
                        .withUser(grafanaDashboardExtension.user)
                        .withPassword(grafanaDashboardExtension.password)
                        .build())
                .uploadDashboards(Paths.get(getProject().getProjectDir().toString(), grafanaDashboardExtension.dir).toFile());
    }

    void setGrafanaConfiguration(Configuration configuration) {
        this.grafanaConfiguration = configuration;
    }

    void setGrafanaDashboardExtension(GrafanaDashboardExtension grafanaDashboardExtension) {
        this.grafanaDashboardExtension = grafanaDashboardExtension;
    }
}