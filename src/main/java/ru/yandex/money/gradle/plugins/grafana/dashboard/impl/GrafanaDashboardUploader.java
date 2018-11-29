package ru.yandex.money.gradle.plugins.grafana.dashboard.impl;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TODO:
 *
 * @author Oleg Kandaurov
 * @since 29.11.2018
 */
public class GrafanaDashboardUploader {

    private final Logger log = Logging.getLogger(GrafanaDashboardUploader.class);

    private final List<DashboardContentCreator> dashboardContentCreators;
    private final GrafanaConnectionSettings grafanaConnectionSettings;

    public GrafanaDashboardUploader(List<DashboardContentCreator> dashboardContentCreators,
                                    GrafanaConnectionSettings grafanaConnectionSettings) {
        this.dashboardContentCreators = new ArrayList<>(dashboardContentCreators);
        this.grafanaConnectionSettings = grafanaConnectionSettings;
    }

    /**
     * Загрузить все dashboard из заданной директории в grafana
     *
     * @param targetDir директория для поиска дашбордов
     */
    public void uploadDashboards(File targetDir) {
        log.lifecycle("Finding grafana dashboards to upload");
        if (!targetDir.isDirectory()) {
            log.lifecycle("Grafana directory not found: dir={}", targetDir);
            return;
        }
        File[] files = targetDir.listFiles(pathname -> dashboardContentCreators.stream()
                .anyMatch(creator -> creator.isProcessable(pathname)));
        List<File> dashboards = files == null ? Collections.emptyList() : Arrays.asList(files);
        if (dashboards.isEmpty()) {
            log.lifecycle("No grafana dashboards: dir={}", targetDir.getAbsolutePath());
            return;
        }

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            DashboardSender sender = new DashboardSender(client, grafanaConnectionSettings);
            dashboards.forEach(file -> dashboardContentCreators.stream()
                    .filter(creator -> creator.isProcessable(file)).findFirst()
                    .map(creator -> {
                        log.lifecycle("processing dashboard content: file={}", file.getName());
                        return creator.createContent(file);
                    })
                    .ifPresent(dashboardContent -> {
                        log.info("saving dashboard content to grafana: file={}, content =\n\n{}\n\n", file.getName(), dashboardContent);
                        try {
                            sender.sendContentToGrafana(dashboardContent);
                        } catch (IOException e) {
                            throw new RuntimeException("cannot send dashboard content to grafana", e);
                        }
                    }));
        } catch (IOException e) {
            throw new RuntimeException("cannot close http client", e);
        }
    }

}
