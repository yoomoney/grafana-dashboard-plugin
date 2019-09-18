### NEXT_VERSION_TYPE=MINOR
### NEXT_VERSION_DESCRIPTION_BEGIN
* Added task `collectGrafanaDashboards` that collects generated dashboards and prints them to stdout.
### NEXT_VERSION_DESCRIPTION_END
## [3.0.1]() (15-07-2019)

* Added empty dashboard filtering for upload.

## [3.0.0]() (10-07-2019)

1. Added grafanaDashboardsCompile sourceSet, where you can specify libraries which contains additional
dashboards.
2. SourceSet was separated into grafanaFromArtifact and grafanaFromDir for the purpoce to announce different versions of dsl.

## [2.2.2]() (22-05-2019)

* Build with yamoney-gradle-project-plugin=5.+

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