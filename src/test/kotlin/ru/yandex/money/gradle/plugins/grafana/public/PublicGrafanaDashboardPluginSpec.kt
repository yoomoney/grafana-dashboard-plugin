package ru.yandex.money.gradle.plugins.grafana.public

import org.junit.Test
import ru.yandex.money.gradle.plugins.grafana.GrafanaDashboardPluginSpec

class PublicGrafanaDashboardPluginSpec : GrafanaDashboardPluginSpec() {
    override var repositories: String
        get() = """
            repositories {
                jcenter()
            }
        """.trimIndent()
        set(_) {}
    override var pluginId: String
        get() = "com.yandex.money.tech.grafana-dashboard-plugin"
        set(_) {}

    @Test
    fun `public - should process dashboards`() {
        `should process dashboards`()
    }

    @Test
    fun `public - should collect dashboards`() {
        `should collect dashboards`()
    }
}