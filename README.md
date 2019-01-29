[![Build Status](https://travis-ci.org/yandex-money-tech/grafana-dashboard-plugin.svg?branch=master)](https://travis-ci.org/yandex-money-tech/grafana-dashboard-plugin)
[![Build status](https://ci.appveyor.com/api/projects/status/pljxjuc9gjdqprt8?svg=true)](https://ci.appveyor.com/project/f0y/grafana-dashboard-plugin)
[![codecov](https://codecov.io/gh/yandex-money-tech/grafana-dashboard-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/yandex-money-tech/grafana-dashboard-plugin)
[![codebeat badge](https://codebeat.co/badges/c91a7632-c469-4cfd-be62-6a1840dc347b)](https://codebeat.co/projects/github-com-yandex-money-tech-grafana-dashboard-plugin-master)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Javadoc](https://img.shields.io/badge/javadoc-latest-blue.svg)](https://yandex-money-tech.github.io/grafana-dashboard-plugin/)
[![Download](https://api.bintray.com/packages/yandex-money-tech/maven/grafana-dashboard-plugin/images/download.svg)](https://bintray.com/yandex-money-tech/maven/grafana-dashboard-plugin/_latestVersion)

# Grafana Dashboard Plugin

Plugin for automatic dashboards creation in [Grafana](https://grafana.com)

# Motivation

The primary goal of this project is to ease support and maintenance of dashboards in grafana. 

We, a team at Yandex.Money, have many dashboards for our projects, and it's often hard to answer these questions:

* Who created the dashboard?
* What it's purpose?
* Is it working correctly?
* How can i reuse some dashboards?

So, to come up to the solution to these answers, we do as following:

* Store dashboards in vcs with application code
* Have CI jobs for updating dashboards contents
* Use [Grafana Dashboard Dsl](https://github.com/yandex-money-tech/grafana-dashboard-dsl) for declaring dashboards

These methods of creating and maintaining dashboards allow us:

* See author and changes made in particular dashboards
* Ease of understanding what metrics used for monitoring application
* In case of broken dashboard quickly understand and fix the problems
* Reuse parts of whole contents of dashboards in other application

# Usage

```groovy
plugins {
    id 'com.yandex.money.tech.grafana-dashboard-plugin' version '2.0.5'
}

grafana {
    // Required, URL to Grafana
    url = 'https://grafana.yamoney.ru'
    
    // Required, Grafana username
    user = 'testUser'
    
    // Required, Grafana user password
    password = 'test'
    
    // Directory with dashboards descriptions, default is: 'grafana'
    dir = 'grafana'
    
    // Folder id to save to, default is: '0'
    folderId = '0'
    
    // Overwrite existing dashboards, default is: true    
    overwrite = true
}
```

# How does it work?

Plugin scans the folder, configured in settings, for files with dashboards description, in following formats:

* JSON (file extension `.json`)
* Kotlin Script (file extension `.kts`)

Then use task called `uploadGrafanaDashboards` to publish your dashboards

## JSON

Simple format, uploaded to grafana as is.
You can check up description in Grafana -> Dashboard -> Settings -> JSON Model.

## Kotlin Script

For this format, plugin executes files with kotlin code and expects that
an output is a description of dashboards in JSON format.

It is most useful when used with another one of our projects: [Grafana Dashboard Dsl](https://github.com/yandex-money-tech/grafana-dashboard-dsl)
Just add a dependency to build script, in the `grafanaCompile` source set, as follows:

```groovy
dependencies {
    grafanaCompile 'com.yandex.money.tech:grafana-dashboard-dsl:1.2.0'
}
```

# How to contribute?

Just fork the repo and send us a pull request.

Make sure your branch builds without any warnings/issues.

# How to build?

See configuration for Travis (`.travis.yml`) or AppVeyor (`appveyor.yml`).
There are two gradle projects in this repository:

* Files `build.gradle`, `gradlew`, `gradle/wrapper` is for internal use in Yandex.Money infrastructure
* Files `build-public.gradle`, `gradlew-public`, `gradle-public/wrapper` are for public use

# Importing into IntelliJ IDEA

Unfortunately, at this moment, intellij does not support this build configuration,
so you have to change some files before importing:

* Move `gradle-public/wrapper/gradle-wrapper.properties` into `gradle/wrapper/gradle-wrapper.properties`
* Move `build-public.gradle` into `build.gradle`

Vote for this issue [IDEA-199116](https://youtrack.jetbrains.net/issue/IDEA-199116), to make intellij support these types of configuration.
