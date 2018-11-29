package ru.yandex.money.gradle.plugins.grafana.dashboard.impl;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * Интерфейс для реализации способа создания контента
 *
 * @author Oleg Kandaurov
 * @since 29.11.2018
 */
public interface DashboardContentCreator {

    /**
     * Поддерживается-ли заданный тип файла
     *
     * @param file файл
     * @return признак, что возможно создать контент из заданного файла
     */
    boolean isSupported(@Nonnull File file);

    /**
     * Создать содержимое dashboard на основе заданного файла
     *
     * @param file файл
     * @return содержимое dashboard
     */
    String createContent(@Nonnull File file);
}
