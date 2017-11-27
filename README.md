# GrafanaPlugin

Плагин для автоматизированного создания дашбордов в Grafana.

Пример настройки скрипта создания RL:
```
grafana {
    url = 'https://grafana.yamoney.ru'
    user = 'testUser'
    password = 'test'
}
```

Если пользователь и пароль не настроены, то по умолчнию они берутся из переменных окружения GRAFANA_USER и GRAFANA_PASSWORD.