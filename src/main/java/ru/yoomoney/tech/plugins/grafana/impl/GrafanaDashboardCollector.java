package ru.yoomoney.tech.plugins.grafana.impl;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Grafana dashboard collector
 *
 * @author horyukova
 * @since 17.09.2019
 */
public class GrafanaDashboardCollector {

    private final Logger log = Logging.getLogger(GrafanaDashboardCollector.class);

    private final Collection<DashboardContentCreator> dashboardContentCreators;

    public GrafanaDashboardCollector(Collection<DashboardContentCreator> dashboardContentCreators) {
        this.dashboardContentCreators = new ArrayList<>(dashboardContentCreators);
    }

    /**
     * Collect all dashboards from specified folder into grafana
     *
     * @param targetDir folder to search dashboards for
     */
    public List<GrafanaDashboard> collectDashboards(File targetDir) {
        List<GrafanaDashboard> jsonDashboards = new ArrayList<>();

        log.lifecycle("Finding grafana dashboards");
        if (!targetDir.isDirectory()) {
            log.lifecycle("Grafana directory not found: dir={}", targetDir);
            return jsonDashboards;
        }
        List<File> dashboards;
        try {
            dashboards = Files.walk(targetDir.toPath())
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(pathname -> dashboardContentCreators.stream()
                            .anyMatch(creator -> creator.isSupported(pathname)))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Can't get dashboard files", e);
        }

        if (dashboards.isEmpty()) {
            log.lifecycle("No grafana dashboards: dir={}", targetDir.getAbsolutePath());
            return jsonDashboards;
        }

        dashboards.forEach(file -> dashboardContentCreators.stream()
                .filter(creator -> creator.isSupported(file)).findFirst()
                .map(creator -> {
                    log.lifecycle("Processing dashboard content: file={}", file.getName());
                    return creator.createContent(file);
                })
                .filter(content -> !content.isEmpty())
                .ifPresent(content -> jsonDashboards.add(GrafanaDashboard.builder()
                        .withContent(content)
                        .withFileName(file.getName()).build())));

        return jsonDashboards;
    }
}
