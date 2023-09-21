Custom Plugin I made off of SonarQube Custom Plugin Example [![Build Status](https://travis-ci.org/SonarSource/sonar-custom-plugin-example.svg?branch=7.x)](https://travis-ci.org/SonarSource/sonar-custom-plugin-example)
==========

An example SonarQube plugin compatible with SonarQube 10.x.

Back-end
--------

This plugin was built to take a report file from a third party SCA tool, and create issues within SonarQube from this report.  The code is assuming that I am using Maven, so it will find and mark the vulnerability within the pom.xml

### Building

To build the plugin JAR file, call:

```
mvn clean package
```

The JAR will be deployed to `target/sonar-mergebase-plugin-1.0.jar`. Copy this to your SonarQube's `extensions/plugins/` directory, and re-start SonarQube.

Front-end
---------

I did not create any custom front end pages for this plugin.  The results will just show up within the list of SonarQube vulnerabilities and will show where within the pom.xml file where a jar with a vulnerability is present.

