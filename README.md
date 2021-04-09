[![Build Status](https://api.travis-ci.com/yoomoney-gradle-plugins/grafana-dashboard-plugin.svg?branch=master)](https://travis-ci.com/yoomoney-gradle-plugins/grafana-dashboard-plugin)
[![codecov](https://codecov.io/gh/yoomoney-gradle-plugins/grafana-dashboard-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/yoomoney-gradle-plugins/grafana-dashboard-plugin)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# Grafana Dashboard Plugin

Plugin for automatic dashboards creation in [Grafana](https://grafana.com)

# Motivation

The primary goal of this project is to ease support and maintenance of dashboards in grafana. 

We, a team at YooMoney, have many dashboards for our projects, and it's often hard to answer these questions:

* Who created the dashboard?
* What it's purpose?
* Is it working correctly?
* How can i reuse some dashboards?

So, to come up to the solution to these answers, we do as following:

* Store dashboards in vcs with application code
* Have CI jobs for updating dashboards contents
* Use [Grafana Dashboard Dsl](https://github.com/yoomoney-tech/grafana-dashboard-dsl) for declaring dashboards

These methods of creating and maintaining dashboards allow us:

* See author and changes made in particular dashboards
* Ease of understanding what metrics used for monitoring application
* In case of broken dashboard quickly understand and fix the problems
* Reuse parts of whole contents of dashboards in other application

# Usage

```groovy
plugins {
    id 'ru.yoomoney.gradle.plugins.grafana-dashboard-plugin' version '2.0.5'
}

grafana {
    // Required, URL to Grafana
    url = 'https://grafana.yooteam.ru'
    
    // Required, Grafana authentication API token 
    apiToken = 'apiToken'
    
    // Directory with dashboards descriptions, default is: 'grafana'
    dir = 'grafana'
    
    // Folder id to save to, default is: '0'
    folderId = '0'
    
    // Overwrite existing dashboards, default is: true    
    overwrite = true
    
    // Additional classpath to use during dashboard scripts evaluation
    classpath = null
   
    //Print collected dashboards to stdout, default is: false
    printCollectedDashboards = false
}
```

# How does it work?

The plugin supports two work scenarios.
1. Scans the folder, configured in settings (grafana.dir)
2. Scans the artifacts, declare in grafanaDashboardsCompile configuration

Scans for files with dashboards description, in following formats:

* JSON (file extension `.json`)
* Kotlin Script (file extension `.kts`)

Then use task called `uploadGrafanaDashboards` to publish your dashboards, or `collectGrafanaDashboards` for print 
your dashboards to stdout.

## JSON

Simple format, uploaded to grafana as is.
You can check up description in Grafana -> Dashboard -> Settings -> JSON Model.

## Kotlin Script

For this format, plugin executes files with kotlin code and expects that
an output is a description of dashboards in JSON format.

It is most useful when used with another one of our projects: [Grafana Dashboard Dsl](https://github.com/yoomoney-tech/grafana-dashboard-dsl)

SourceSet was separated into grafanaFromArtifact and grafanaFromDir for the purpoce to announce different versions of dsl.
Just add a dependency to build script, in the `grafanaFromDirCompile` and `grafanaFromArtifactCompile` source set, 
as follows:

```groovy
dependencies {
    grafanaFromDirCompile 'ru.yoomoney.gradle,plugins:grafana-dashboard-dsl:1.2.0'
    grafanaFromArtifactCompile 'ru.yoomoney.gradle,plugins:grafana-dashboard-dsl:1.1.0'
}
```

# How to contribute?

Just fork the repo and send us a pull request.

Make sure your branch builds without any warnings/issues.