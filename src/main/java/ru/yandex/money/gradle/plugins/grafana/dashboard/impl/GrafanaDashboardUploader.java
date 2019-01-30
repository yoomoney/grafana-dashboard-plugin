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
 * Grafana dashboard uploader
 *
 * @author Oleg Kandaurov
 * @since 29.11.2018
 */
public class GrafanaDashboardUploader {

    private final Logger log = Logging.getLogger(GrafanaDashboardUploader.class);

    private final List<DashboardContentCreator> dashboardContentCreators;
    private final GrafanaUploadSettings grafanaUploadSettings;

    public GrafanaDashboardUploader(List<DashboardContentCreator> dashboardContentCreators,
                                    GrafanaUploadSettings grafanaUploadSettings) {
        this.dashboardContentCreators = new ArrayList<>(dashboardContentCreators);
        this.grafanaUploadSettings = grafanaUploadSettings;
    }

    /**
     * Upload all dashboards from specified folder into grafana
     *
     * @param targetDir folder to search dashboards for
     */
    public void uploadDashboards(File targetDir) {
        log.lifecycle("Finding grafana dashboards to upload");
        if (!targetDir.isDirectory()) {
            log.lifecycle("Grafana directory not found: dir={}", targetDir);
            return;
        }
        File[] files = targetDir.listFiles(pathname -> dashboardContentCreators.stream()
                .anyMatch(creator -> creator.isSupported(pathname)));
        List<File> dashboards = files == null ? Collections.emptyList() : Arrays.asList(files);
        if (dashboards.isEmpty()) {
            log.lifecycle("No grafana dashboards: dir={}", targetDir.getAbsolutePath());
            return;
        }

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            DashboardSender sender = new DashboardSender(client, grafanaUploadSettings);
            dashboards.forEach(file -> dashboardContentCreators.stream()
                    .filter(creator -> creator.isSupported(file)).findFirst()
                    .map(creator -> {
                        log.lifecycle("Processing dashboard content: file={}", file.getName());
                        return creator.createContent(file);
                    })
                    .ifPresent(dashboardContent -> {
                        log.info("Saving dashboard content to grafana: file={}, content =\n\n{}\n\n", file.getName(), dashboardContent);
                        try {
                            sender.sendContentToGrafana(dashboardContent);
                        } catch (IOException e) {
                            throw new RuntimeException("Cannot send dashboard content to grafana", e);
                        }
                    }));
        } catch (IOException e) {
            throw new RuntimeException("Cannot close http client", e);
        }
    }

}
