package ru.yandex.money.gradle.plugins.grafana.private

import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert
import org.junit.Test
import ru.yandex.money.gradle.plugins.grafana.GrafanaDashboardPluginSpec

class PrivateGrafanaDashboardPluginSpec : GrafanaDashboardPluginSpec() {
    override var repositories: String
        get() = """
            repositories {
                    maven { url 'http://nexus.yamoney.ru/repository/thirdparty/' }
                    maven { url 'http://nexus.yamoney.ru/repository/central/' }
                    maven { url 'http://nexus.yamoney.ru/repository/releases/' }
                    maven { url 'http://nexus.yamoney.ru/repository/snapshots/' }
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

    @Test
    fun `private - should process library dashboards`() {
        buildFile.writeText("""
                plugins {
                    id 'java'
                    id '$pluginId'
                }

                grafana {
                    url = 'http://localhost:$grafanaPort'
                    classpath += sourceSets.main.output
                }

                $repositories

                dependencies {
                    grafanaDashboardsCompile 'ru.yandex.money.common:yamoney-grafana-dashboards:1.7.1'
                    grafanaFromArtifactCompile 'ru.yandex.money.common:yamoney-grafana-dashboards:1.7.1'
                    grafanaFromDirCompile 'ru.yandex.money.common:yamoney-grafana-dashboards:1.5.0'
                }
        """.trimIndent())

        val result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("build", "uploadGrafanaDashboards", "--stacktrace", "--info")
                .withPluginClasspath()
                .withDebug(true)
                .build()

        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":uploadGrafanaDashboards")?.outcome)
        assertTrue(result.output.contains("Processing dashboard content: file=CommonInfo.kts"))
        assertFalse(result.output.contains("Saving dashboard content to grafana: file=CommonInfo.kts"))
    }
}