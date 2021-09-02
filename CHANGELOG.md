### NEXT_VERSION_TYPE=MAJOR|MINOR|PATCH
### NEXT_VERSION_DESCRIPTION_BEGIN
### NEXT_VERSION_DESCRIPTION_END
## [5.3.0](https://github.com/yoomoney/grafana-dashboard-plugin/pull/6) (02-09-2021)

* Added support for ignoring SSL validation errors. Use `trustAllSslCertificates` = `true` to enable this feature.

## [5.2.0](https://github.com/yoomoney/grafana-dashboard-plugin/pull/5) (26-08-2021)

* The repository moved yoomoney-gradle-plugins -> yoomoney

## [5.1.0](https://github.com/yoomoney/grafana-dashboard-plugin/pull/3) (13-04-2021)

* Token based authentication added (https://grafana.com/docs/grafana/latest/http_api/auth/)
* Username and password authentication deprecated

## [5.0.1](https://github.com/yoomoney/grafana-dashboard-plugin/pull/4) (12-04-2021)

* Добавлены CODEOWNERS

## [5.0.0](https://github.com/yoomoney/grafana-dashboard-plugin/pull/2) (30-03-2021)

* ***breaking_changes*** Renamed package ru.yoomoney.tech.plugins -> ru.yoomoney.gradle.plugins
* ***breaking_changes*** Renamed group of artifact ru.yoomoney.tech -> ru.yoomoney.gradle.plugins
* Build on gradle-project-plugin

## [4.3.0]() (04-02-2021)

* Print all collected dashboards to stdout is disabled by default. Use printCollectedDashboards to enable on purpose.

## [4.2.3]() (03-12-2020)

В `dependencies.gradle` напрямую указана версия 1.3.50 для
* kotlin-compiler-embeddable
* kotlin-scripting-compiler-embeddable
* kotlin-script-util

## [4.2.2]() (30-11-2020)

* Обновлена версия kotlin 1.3.71 -> 1.3.50

## [4.2.1]() (23-11-2020)

* Замена доменов email @yamoney.ru -> @yoomoney.ru

## [4.2.0]() (03-07-2020)

* Up gradle version: 6.0.1 -> 6.4.1.

## [4.1.1]() (27-02-2020)

* Don't add bibucket pull request link into changelog.md on release

## [4.1.0]() (07-02-2020)

* Build on Java11

## [4.0.0]() (30-01-2020)

* Update gradle version `4.10.2` -> `6.0.1`
* Update versions of dependencies

## [3.1.0]() (18-09-2019)

* Added task `collectGrafanaDashboards` that collects generated dashboards and prints them to stdout.

## [3.0.1]() (15-07-2019)

* Added empty dashboard filtering for upload.

## [3.0.0]() (10-07-2019)

1. Added grafanaDashboardsCompile sourceSet, where you can specify libraries which contains additional
dashboards.
2. SourceSet was separated into grafanaFromArtifact and grafanaFromDir for the purpoce to announce different versions of dsl.

## [2.2.2]() (22-05-2019)

* Build with yoomoney-gradle-project-plugin=5.+

## [2.2.1]() (14-05-2019)

* Added gradle-plugins repository to private build

## [2.2.0]() (01-03-2019)

Added extension configuration property 'classpath'
to customize classpath used during dashboard-scripts evaluation

## [2.1.0]() (26-02-2019)

* Improve CHANGELOG.md next version description format

## [2.0.5]() (29-01-2019)

Translate README, CHANGELOG and comments to english

## [2.0.4]() (10-01-2019)

Downgrade kotlin version (1.2.71 -> 1.2.61)

## [2.0.3]() (30-11-2018)

Add license

## [2.0.2]() (24-11-2018)

Complete rework, see README.md

## [1.0.10]() (19-11-2018)

Build with gradle-project-plugin 2.x

## [1.0.9]() (14-11-2018)

Build with gradle-project-plugin 1.x

## [1.0.4]() (16-07-2017)

Upserter -> Sender

## [1.0.3]() (16-07-2017)

dashboard wrapper

## [1.0.2]() (16-07-2017)

Remove git dependency

## [1.0.1]() (06-12-2017)

Fix crLf

## [1.0.0]() (21-11-2017)

First release