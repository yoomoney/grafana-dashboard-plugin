package ru.yandex.money.gradle.plugins.grafana.dashboard.impl;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * Grafana dashboard
 *
 * @author horyukova
 * @since 17.09.2019
 */
public class GrafanaDashboard {
    private final String fileName;
    private final String content;

    private GrafanaDashboard(@Nonnull String fileName, @Nonnull String content) {
        this.fileName = requireNonNull(fileName, "fileName");
        this.content = requireNonNull(content, "content");
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    public String getFileName() {
        return fileName;
    }

    @Nonnull
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "GrafanaDashboard{" +
                "fileName='" + fileName + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    /**
     * Builder for {@link GrafanaDashboard}
     */
    public static final class Builder {
        private String fileName;
        private String content;

        private Builder() {
        }

        public Builder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public GrafanaDashboard build() {
            return new GrafanaDashboard(fileName, content);
        }
    }
}
