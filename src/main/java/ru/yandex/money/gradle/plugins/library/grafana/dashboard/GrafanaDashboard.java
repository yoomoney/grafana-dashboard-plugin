package ru.yandex.money.gradle.plugins.library.grafana.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Настройки dashboard. Содержатся как часть контента dashboard.
 */
@SuppressWarnings({"unused", "InstanceVariableMayNotBeInitialized", "ClassWithoutConstructor"})
@JsonIgnoreProperties(ignoreUnknown = true)
class GrafanaDashboard {
    private Integer id;
    private String title;
    private Integer version;

    @JsonProperty
    Integer getId() {
        return id;
    }

    @JsonProperty
    String getTitle() {
        return title;
    }

    @JsonProperty
    Integer getVersion() {
        return version;
    }
}
