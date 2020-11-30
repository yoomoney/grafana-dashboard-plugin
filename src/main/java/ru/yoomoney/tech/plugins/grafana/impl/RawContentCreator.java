package ru.yoomoney.tech.plugins.grafana.impl;

import kotlin.text.Charsets;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Create content without additional modifications
 *
 * @author Oleg Kandaurov
 * @since 29.11.2018
 */
public class RawContentCreator implements DashboardContentCreator {

    @Override
    public boolean isSupported(@NotNull File file) {
        return file.getName().toLowerCase().endsWith(".json");
    }

    @Override
    public String createContent(@NotNull File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("cannot read file: path=" + file.getAbsolutePath(), e);
        }
    }
}
