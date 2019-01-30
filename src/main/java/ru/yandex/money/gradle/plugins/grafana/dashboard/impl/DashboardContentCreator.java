package ru.yandex.money.gradle.plugins.grafana.dashboard.impl;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * Interface for content creation
 *
 * @author Oleg Kandaurov
 * @since 29.11.2018
 */
public interface DashboardContentCreator {

    /**
     * Is file type supported by this format
     *
     * @param file File
     * @return Is able to create content from file
     */
    boolean isSupported(@Nonnull File file);

    /**
     * Create dashboard content from specified file
     *
     * @param file File
     * @return Dashboard content
     */
    String createContent(@Nonnull File file);
}
