package ru.yandex.money.gradle.plugins.library.helpers;

import org.ajoberstar.grgit.Grgit;
import org.ajoberstar.grgit.operation.OpenOp;

/**
 * Утилитный класс для получения свойств git-репозитория.
 *
 * @author Kirill Bulatov (mail4score@gmail.com)
 * @author Konstantin Rashev (rashev@yamoney.ru)
 * @since 22.12.2016
 */
public class GitRepositoryProperties {
    private final Grgit grgit;

    /**
     * Конструктор класса. Инициализирует работу с git-репозиторием.
     * Поиск git-репозитория начинается с директории, указанной в baseDir.
     *
     * @param baseDir - директория, начиная с которой идет поиск git-репозитория.
     */
    public GitRepositoryProperties(String baseDir) {
        grgit = getGrGit(baseDir);
    }

    /**
     * Получение grGit по пути
     *
     * @param gitDir путь в фс с репкой git
     * @return репозиторий GrGit
     */
    private static Grgit getGrGit(String gitDir) {
        OpenOp grgitOpenOperation = new OpenOp();
        grgitOpenOperation.setCurrentDir(gitDir);
        return grgitOpenOperation.call();
    }

    /**
     * Показывает, является ли текущая ветра dev веткой или нет.
     *
     * @return true, если является, false - если нет.
     */
    public boolean isDevBranch() {
        return "dev".equalsIgnoreCase(getCurrentBranchName());
    }

    private String getCurrentBranchName() {
        return grgit.getBranch().getCurrent().getName();
    }
}