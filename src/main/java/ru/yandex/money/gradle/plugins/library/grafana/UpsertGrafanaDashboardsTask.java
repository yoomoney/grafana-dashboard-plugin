package ru.yandex.money.gradle.plugins.library.grafana;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import ru.yandex.money.gradle.plugins.library.grafana.dashboard.DashboardSender;
import ru.yandex.money.gradle.plugins.library.grafana.settings.GrafanaConnectionSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Task для вставки/обновления всех dashboard из репозитория в Grafana
 */
@SuppressWarnings("ClassWithoutConstructor")
public class UpsertGrafanaDashboardsTask extends DefaultTask {
    private final Logger log = Logging.getLogger(UpsertGrafanaDashboardsTask.class);
    private static final Pattern GRAFANA_FILE_NAME = Pattern.compile(".*\\.json");
    private static final String GRAFANA_DIRECTORY = "grafana";

    static final String TASK_NAME = "upsertGrafanaDashboards";

    private GrafanaConnectionSettings grafanaConnectionSettings;

    /**
     * Основное действие
     *
     * @throws IOException в случае проблем с IO
     */
    @SuppressWarnings("unused")
    @TaskAction
    void upsertGrafanaDashboardsFromRepo() throws IOException {
        log.lifecycle("Finding dashboards to upsert");
        List<File> dashboards = getFilesWithGrafanaDashboards();

        if (dashboards.isEmpty()) {
            log.lifecycle("No dashboards in repo");
            return;
        }
        log.lifecycle("Upserting dashboards to Grafana, count={}", dashboards.size());
        upsertGrafanaDashboards(dashboards);
    }

    /**
     * Получение списка файлов с настройкам dashboards
     *
     * @return список файлов с настройкам dashboards
     */
    private List<File> getFilesWithGrafanaDashboards() {
        File root = new File(GRAFANA_DIRECTORY);
        if (!root.isDirectory()) {
            log.info("Grafana directory not found at {}", GRAFANA_DIRECTORY);
            return Collections.emptyList();
        }
        File[] files = root.listFiles(file -> GRAFANA_FILE_NAME.matcher(file.getName()).matches());
        return files == null ? Collections.emptyList() : Arrays.asList(files);
    }

    /**
     * Вставка/обновление списки dashboards из файлов
     *
     * @param dashboardFiles список файлов с dashboards
     * @throws IOException в случае проблем с IO
     */
    private void upsertGrafanaDashboards(List<File> dashboardFiles) throws IOException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            DashboardSender sender = new DashboardSender(client, grafanaConnectionSettings);
            dashboardFiles.forEach(file -> {
                try {
                    String dashboardContent = new String(Files.readAllBytes(file.toPath()), "UTF-8");
                    sender.sendContentToGrafana(dashboardContent);
                    log.info("Successfully processed {}", file.getPath());
                } catch (IOException e) {
                    log.error("Error during upsert file: {}", file.getPath(), e);
                }
            });
        }
    }

    void setGrafanaConnectionSettings(GrafanaConnectionSettings grafanaConnectionSettings) {
        this.grafanaConnectionSettings = grafanaConnectionSettings;
    }
}