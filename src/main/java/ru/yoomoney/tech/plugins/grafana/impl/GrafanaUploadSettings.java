package ru.yoomoney.tech.plugins.grafana.impl;

/**
 * Grafana plugin settings
 */
public class GrafanaUploadSettings {
    private final String url;
    private final String user;
    private final String password;
    private final String folderId;
    private final boolean overwrite;

    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    private GrafanaUploadSettings(Builder builder) {
        url = builder.url;
        user = builder.user;
        password = builder.password;
        folderId = builder.folderId;
        overwrite = builder.overwrite == null ? false : builder.overwrite;

    }

    /**
     * Url to Grafana
     */
    String getUrl() {
        return url;
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

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Билдер
     */
    public static final class Builder {

        private String url;
        private String user;
        private String password;
        private String folderId;
        private Boolean overwrite;

        public Builder withUrl(String val) {
            url = val;
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

        public GrafanaUploadSettings build() {
            return new GrafanaUploadSettings(this);
        }
    }
}