[![Build Status](https://travis-ci.org/yandex-money-tech/grafana-dashboard-plugin.svg?branch=master)](https://travis-ci.org/yandex-money-tech/grafana-dashboard-plugin)
[![Build status](https://ci.appveyor.com/api/projects/status/pljxjuc9gjdqprt8?svg=true)](https://ci.appveyor.com/project/f0y/grafana-dashboard-plugin)
[![codecov](https://codecov.io/gh/yandex-money-tech/grafana-dashboard-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/yandex-money-tech/grafana-dashboard-plugin)
[![codebeat badge](https://codebeat.co/badges/c91a7632-c469-4cfd-be62-6a1840dc347b)](https://codebeat.co/projects/github-com-yandex-money-tech-grafana-dashboard-plugin-master)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Javadoc](https://img.shields.io/badge/javadoc-latest-blue.svg)](https://yandex-money-tech.github.io/grafana-dashboard-plugin/)
[![Download](https://api.bintray.com/packages/yandex-money-tech/maven/grafana-dashboard-plugin/images/download.svg)](https://bintray.com/yandex-money-tech/maven/grafana-dashboard-plugin/_latestVersion)

# Grafana Dashboard Plugin

Плагин для автоматизированного создания дашбордов в [Grafana](https://grafana.com)

# Зачем?

Главная цель - это облегчение поддержки и сопровождения дашбордов в grafana.

При большом количестве созданных дашбордов, довольно сложно ответить на вопросы: 
"Кто создал дашборд?", "Для чего?", "Не сломан ли он?", "Как мне переиспользовать дашборд?".

Для того чтобы уверенно отвечать на данные вопросы, мы, в Яндекс.Деньгах делаем следующее:
 * Храним описания дашбордов в системе контроля версий вместе с кодом микросервиса;
 * На периодической основе, при помощи CI сервера, обновляем содержимое дашборда на основе хранимого описания;
 * Описываем дашборды при помощи [Grafana Dashboard Dsl](https://github.com/yandex-money-tech/grafana-dashboard-dsl).

Данные способ создания и хранения дашбордов позволяет нам:
* Видеть автора изменений дашборда;
* Легко понимать по каким метрикам производится мониторинг микросервиса;
* В случае поломки дашборда - легко восстановить его работоспособность;
* Переиспользовать дашборды.

# Как подключить?

```groovy
plugins {
    id 'com.yandex.money.tech.grafana-dashboard-plugin' version '2.0.1'
}

grafana {
    // Адрес графаны, обязательное поле.
    url = 'https://grafana.yamoney.ru'
    
    // Имя пользователя для подключения к Grafana, обязательное поле.
    user = 'testUser'
    
    // Пароль для подключения к Grafana, обязательное поле.
    password = 'test'
    
    // Директория, в которой лежат описания дашбордов, по умолчанию: 'grafana'
    dir = 'grafana'
    
    // Идентификатор папки для сохранения дашборда http://docs.grafana.org/http_api/folder/, по умолчанию: '0'
    folderId = '0'
    
    // Перезаписывать-ли содержимое дашборда, по умолчанию: true    
    overwrite = true
}
```

# Как работает?

Плагин ищет файлы с описанием дашбордов в соответствующей папке и поддерживает публикацию дашбордов в двух форматах:

* JSON (расширение файлов `.json`)
* Kotlin Script (расширение файлов `.kts`)

Для публикации дашбордов следует вызвать таску `uploadGrafanaDashboards`

## JSON

Простой формат, загружается в grafana as is.
Получить описание в данном формате можно через Dashboard -> Settings -> JSON Model.

## Kotlin Script

В случае данного формата описания, происходит выполнение Kotlin Script, 
строковый результат выполнения которого должен являться описанием дашборда в JSON формате.

Наибольшая польза от данного формата достигается при помощи 
[Grafana Dashboard Dsl](https://github.com/yandex-money-tech/grafana-dashboard-dsl)

Подключить его можно путем добавления соответствующей зависимости в `grafanaCompile` конфигурацию.

```groovy
dependencies {
    grafanaCompile 'com.yandex.money.tech:grafana-dashboard-dsl:1.0.5'
}
```

# Сборка проекта

См. конфигурации Travis (`.travis.yml`) или AppVeyor (`appveyor.yml`).
В репозитории находятся два gradle-проекта:
- файлы `build.gradle`, `gradlew`, `gradle/wrapper` относятся к проекту для работы во внутренней инфраструктуре Яндекс.Денег;
- файлы `build-public.gradle`, `gradlew-public`, `gradle-public/wrapper` относятся к проекту для работы извне.

# Импорт проекта в IDE

К сожалению на данный момент необходимо перед импортом проекта в Idea заменить файлы:
- `gradle-public/wrapper/gradle-wrapper.properties` на `gradle/wrapper/gradle-wrapper.properties`,
- `build-public.gradle` на `build.gradle`.
Это вызвано багом в Idea: https://github.com/f0y/idea-two-gradle-builds.