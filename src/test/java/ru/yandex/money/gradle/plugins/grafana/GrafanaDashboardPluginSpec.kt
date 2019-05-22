package ru.yandex.money.gradle.plugins.grafana

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import java.io.File
import java.io.IOException
import java.net.ServerSocket
import java.nio.file.Paths
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Oleg Kandaurov
 * @since 22.11.2018
 */
abstract class GrafanaDashboardPluginSpec {

    protected abstract var pluginId: String
    protected abstract var repositories: String

    private val testProjectDir = TemporaryFolder()
    private lateinit var buildFile: File

    private val grafanaPort: Int by lazy {
        try {
            ServerSocket(0).use { socket -> socket.localPort }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    inner class HelloHandler : AbstractHandler() {
        @Throws(IOException::class, ServletException::class)
        override fun handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
            response.status = HttpServletResponse.SC_OK
            baseRequest.setHandled(true)
        }
    }

    private fun setupJettyServer() {
        val server = Server(grafanaPort)
        server.setStopAtShutdown(true)
        val handler = HelloHandler()
        server.setHandler(handler)
        server.start()
    }

    @Before
    fun setup() {
        setupJettyServer()
        testProjectDir.create()
        buildFile = testProjectDir.newFile("build.gradle")
        initializeTestProject()
        val grafanaPath = Paths.get(testProjectDir.root.absolutePath, "grafana")
        grafanaPath.toFile().mkdir()
        Paths.get(grafanaPath.toFile().absolutePath, "test-kotlin.kts").toFile().writeText("""
        import ru.yandex.money.TestEnum
        println("Goodbye!!!")
        """.trimIndent())
        Paths.get(grafanaPath.toFile().absolutePath, "test-json.json").toFile().writeText("""
        {"a": "1"}
        """.trimIndent())
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
        """.trimIndent())
    }

    private fun initializeTestProject() {
        testProjectDir.newFolder("src", "main", "java", "ru", "yandex", "money")
        val enumFile = testProjectDir.newFile("src/main/java/ru/yandex/money/TestEnum.java")
        enumFile.writeText("""
                package ru.yandex.money;

                public enum TestEnum { }
            """.trimIndent())
    }

    fun `should process dashboards`() {
        val result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("build", "uploadGrafanaDashboards", "--stacktrace", "--info")
                .withPluginClasspath()
                .withDebug(true)
                .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":uploadGrafanaDashboards")?.outcome)
        assertTrue(result.output.contains("Goodbye!!!"))
        assertTrue(result.output.contains("Processing dashboard content: file=test-json.json"))
        assertTrue(result.output.contains("Processing dashboard content: file=test-kotlin.kts"))
    }
}