package ru.yoomoney.gradle.plugins.grafana.impl;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Grafana dashboard uploader
 *
 * @author Oleg Kandaurov
 * @since 29.11.2018
 */
public class GrafanaDashboardUploader {

    private final Logger log = Logging.getLogger(GrafanaDashboardUploader.class);

    private final GrafanaUploadSettings grafanaUploadSettings;

    public GrafanaDashboardUploader(GrafanaUploadSettings grafanaUploadSettings) {
        this.grafanaUploadSettings = grafanaUploadSettings;
    }

    /**
     * Upload all dashboards from specified folder into grafana
     *
     * @param dashboards dashboards for upload
     */
    public void uploadDashboards(List<GrafanaDashboard> dashboards) {
        if (dashboards.isEmpty()) {
            log.info("No grafana dashboards");
            return;
        }

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();

        if (grafanaUploadSettings.isTrustAllSslCertificates()) {
            try {
                SSLContextBuilder contextBuilder = new SSLContextBuilder()
                        .loadTrustMaterial((chain, authType) -> true);
                clientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(contextBuilder.build()));
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                throw new IllegalStateException("Failed to load trust material", e);
            }
        }

        try (CloseableHttpClient client = clientBuilder.build()) {
            DashboardSender sender = new DashboardSender(client, grafanaUploadSettings);
            dashboards.forEach(dashboardContent -> {
                log.info("Saving dashboard content to grafana: content=\n\n{}", dashboardContent.getContent());
                try {
                    sender.sendContentToGrafana(dashboardContent.getContent());
                } catch (IOException e) {
                    throw new RuntimeException("Cannot send dashboard content to grafana", e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Cannot close http client", e);
        }
    }
}
