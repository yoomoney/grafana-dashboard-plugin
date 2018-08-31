package ru.yandex.money.gradle.plugins.library.grafana;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.script.ScriptEngineManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine;
import ru.yandex.money.gradle.plugins.library.grafana.dashboard.DashboardSender;
import ru.yandex.money.gradle.plugins.library.grafana.settings.GrafanaConnectionSettings;

/**
 * Task для вставки/обновления всех dashboard из репозитория в Grafana
 */
@SuppressWarnings("ClassWithoutConstructor")
public class UpsertGrafanaDashboardsTask extends DefaultTask {
    private final Logger log = Logging.getLogger(UpsertGrafanaDashboardsTask.class);
    private static final String GRAFANA_DIRECTORY = "grafana";

    static final String TASK_NAME = "upsertGrafanaDashboards";

    private GrafanaConnectionSettings grafanaConnectionSettings;

    private Configuration grafanaConfiguration;

    /**
     * Основное действие
     *
     * @throws IOException в случае проблем с IO
     */
    @SuppressWarnings("unused")
    @TaskAction
    void upsertGrafanaDashboardsFromRepo() throws IOException {
        log.lifecycle("Finding dashboards to upsert");

        File root = new File(GRAFANA_DIRECTORY);
        if (!root.isDirectory()) {
            log.info("Grafana directory not found at {}", GRAFANA_DIRECTORY);
            return;
        }

        upsertKotlinScriptDashboards(root);
        upsertJsonDashboards(root);
    }

    private void upsertKotlinScriptDashboards(File root) throws IOException {
        File[] files = root.listFiles(file -> file.getName().toLowerCase().endsWith(".kts"));
        List<File> dashboards = files == null ? Collections.emptyList() : Arrays.asList(files);
        if (dashboards.isEmpty()) {
            log.lifecycle("No kotlin dashboards in repo");
            return;
        }

        log.lifecycle("Upserting kotlin dsl dashboards to Grafana, count={}", dashboards.size());

        KotlinJsr223JvmLocalScriptEngine kotlinScript = (KotlinJsr223JvmLocalScriptEngine) new ScriptEngineManager()
                .getEngineByExtension("kts");
        kotlinScript.getTemplateClasspath().addAll(grafanaConfiguration.getFiles());
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            DashboardSender sender = new DashboardSender(client, grafanaConnectionSettings);
            dashboards.forEach(file -> {
                try {
                    String dashboardScript = new String(Files.readAllBytes(file.toPath()), "UTF-8");
                    String dashboardContent = (String) kotlinScript.eval(dashboardScript);
                    log.debug("Dashboard {} : content =\n\n{}\n\n", file.getName(), dashboardContent);
                    sender.sendContentToGrafana(dashboardContent);
                    log.info("Successfully processed {}", file.getPath());
                } catch (Exception e) {
                    log.error("Error during upsert file: {}", file.getPath(), e);
                }
            });
        }
    }

    private void upsertJsonDashboards(File root) throws IOException {
        File[] files = root.listFiles(file -> file.getName().toLowerCase().endsWith(".json"));
        List<File> dashboards = files == null ? Collections.emptyList() : Arrays.asList(files);
        if (dashboards.isEmpty()) {
            log.lifecycle("No json dashboards in repo");
            return;
        }

        log.lifecycle("Upserting json dashboards to Grafana, count={}", dashboards.size());

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            DashboardSender sender = new DashboardSender(client, grafanaConnectionSettings);
            dashboards.forEach(file -> {
                try {
                    String dashboardContent = new String(Files.readAllBytes(file.toPath()), "UTF-8");
                    sender.sendContentToGrafana(dashboardContent);
                    log.info("Successfully processed {}", file.getPath());
                } catch (Exception e) {
                    log.error("Error during upsert file: {}", file.getPath(), e);
                }
            });
        }
    }

    void setGrafanaConnectionSettings(GrafanaConnectionSettings grafanaConnectionSettings) {
        this.grafanaConnectionSettings = grafanaConnectionSettings;
    }

    void setGrafanaConfiguration(Configuration grafanaConfiguration) {
        this.grafanaConfiguration = grafanaConfiguration;
    }
}