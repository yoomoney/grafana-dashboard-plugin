package ru.yoomoney.gradle.plugins.grafana.impl;

/**
 * Grafana plugin settings
 */
public class GrafanaUploadSettings {
    private final String url;
    private final String apiToken;
    /**
     * Use {@link #apiToken} instead
     */
    @Deprecated
    private final String user;
    /**
     * Use {@link #apiToken} instead
     */
    @Deprecated
    private final String password;
    private final String folderId;
    private final boolean overwrite;
    private final boolean trustAllSslCertificates;

    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    private GrafanaUploadSettings(Builder builder) {
        url = builder.url;
        apiToken = builder.apiToken;
        user = builder.user;
        password = builder.password;
        folderId = builder.folderId;
        overwrite = builder.overwrite == null ? false : builder.overwrite;
        trustAllSslCertificates = builder.trustAllSslCertificates == null ? false : builder.trustAllSslCertificates;

    }

    /**
     * Url to Grafana
     */
    String getUrl() {
        return url;
    }

    /**
     * Grafana authentication api token
     */
    String getApiToken() {
        return apiToken;
    }

    /**
     * Grafana username
     */
    String getUser() {
        return user;
    }

    /**
     * Grafana user password
     */
    String getPassword() {
        return password;
    }

    /**
     * Folder id to save to http://docs.grafana.org/http_api/folder/
     */
    String getFolderId() {
        return folderId;
    }

    /**
     * Overwrite existing dashboards
     */
    boolean isOverwrite() {
        return overwrite;
    }

    /**
     * Trust all SSL certificates
     */
    public boolean isTrustAllSslCertificates() {
        return trustAllSslCertificates;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Билдер
     */
    public static final class Builder {

        private String url;
        private String apiToken;
        private String user;
        private String password;
        private String folderId;
        private Boolean overwrite;
        private Boolean trustAllSslCertificates;

        public Builder withUrl(String val) {
            url = val;
            return this;
        }

        public Builder withApiToken(String val) {
            apiToken = val;
            return this;
        }

        public Builder withUser(String val) {
            user = val;
            return this;
        }

        public Builder withPassword(String val) {
            password = val;
            return this;
        }

        public Builder withFolderId(String val) {
            folderId = val;
            return this;
        }

        public Builder withOverwrite(Boolean val) {
            overwrite = val;
            return this;
        }

        public Builder withTrustAllSslCertificates(Boolean val) {
            trustAllSslCertificates = val;
            return this;
        }

        public GrafanaUploadSettings build() {
            return new GrafanaUploadSettings(this);
        }
    }
}