package ru.yandex.money.gradle.plugins.grafana.dashboard.impl;

import kotlin.text.Charsets;
import org.gradle.api.artifacts.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Cоздание контента при помощи запуска kotlin-скрипта
 *
 * @author Oleg Kandaurov
 * @since 29.11.2018
 */
public class KotlinScriptContentCreator implements DashboardContentCreator {
    private final KotlinJsr223JvmLocalScriptEngine kotlinEngine;

    public KotlinScriptContentCreator(Configuration grafanaConfiguration) {
        kotlinEngine = (KotlinJsr223JvmLocalScriptEngine) new ScriptEngineManager()
                .getEngineByExtension("kts");
        kotlinEngine.getTemplateClasspath().addAll(grafanaConfiguration.getFiles());
    }

    @Override
    public boolean isSupported(@NotNull File file) {
        return file.getName().toLowerCase().endsWith(".kts");
    }

    @Override
    public String createContent(@NotNull File file) {
        String dashboardScript;
        try {
            dashboardScript = new String(Files.readAllBytes(file.toPath()), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("cannot read file: path=" + file.getAbsolutePath(), e);
        }
        try {
            return (String) kotlinEngine.eval(dashboardScript);
        } catch (ScriptException e) {
            throw new RuntimeException("cannot eval kotlin script: file=" + file.getAbsolutePath(), e);
        }
    }
}
