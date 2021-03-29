package ru.yoomoney.tech.plugins.grafana.public

import org.junit.Test
import ru.yoomoney.tech.plugins.grafana.GrafanaDashboardPluginSpec

class PublicGrafanaDashboardPluginSpec : GrafanaDashboardPluginSpec() {
    override var repositories: String
        get() = """
            repositories {
                jcenter()
            }
        """.trimIndent()
        set(_) {}
    override var pluginId: String
        get() = "ru.yoomoney.tech.grafana-dashboard-plugin"
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