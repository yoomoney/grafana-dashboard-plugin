package ru.yandex.money.gradle.plugins.grafana.private

import org.junit.Test
import ru.yandex.money.gradle.plugins.grafana.GrafanaDashboardPluginSpec

class PrivateGrafanaDashboardPluginSpec : GrafanaDashboardPluginSpec() {
    override var repositories: String
        get() = """
            repositories {
                    maven { url 'http://nexus.yamoney.ru/content/repositories/jcenter.bintray.com/' }
                }
        """.trimIndent()
        set(_) {}
    override var pluginId: String
        get() = "yamoney-grafana-dashboard-plugin"
        set(_) {}

    @Test
    fun `private - should process dashboards`() {
        `should process dashboards`()
    }
}