package ru.yandex.money.gradle.plugins.library.grafana.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Ответ на запрос получения настроек dashboard
 */
@SuppressWarnings({"unused", "InstanceVariableMayNotBeInitialized", "ClassWithoutConstructor"})
@JsonIgnoreProperties(ignoreUnknown = true)
class GrafanaGetDashboardResponse {
    private GrafanaDashboard dashboard;

    @JsonProperty
    GrafanaDashboard getDashboard() {
        return dashboard;
    }
}
