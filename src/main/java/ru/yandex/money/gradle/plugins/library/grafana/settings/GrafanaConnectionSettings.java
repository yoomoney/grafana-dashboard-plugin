package ru.yandex.money.gradle.plugins.library.grafana.settings;

/**
 * Настройки подключения к Grafana
 */
public class GrafanaConnectionSettings {
    private final String url;
    private final String user;
    private final String password;

    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    private GrafanaConnectionSettings(Builder builder) {
        url = builder.url;
        user = builder.user;
        password = builder.password;
    }

    /**
     * Url для подключения к Grafana
     */
    public String getUrl() {
        return url;
    }

    /**
     * пользователь для подключения к Grafana
     */
    public String getUser() {
        return user;
    }

    /**
     * пароль для подключения к Grafana
     */
    public String getPassword() {
        return password;
    }

    /**
     * Билдер
     */
    public static final class Builder {
        private String url;
        private String user;
        private String password;

        public Builder withUrl(String val) {
            url = val;
            return this;
        }

        public Builder withUser(String val) {
            user = val;
            return this;
        }

        public Builder withPassword(String val) {
            password = val;
            return this;
        }

        public GrafanaConnectionSettings build() {
            return new GrafanaConnectionSettings(this);
        }
    }
}