---
title: "OMF User guide"
draft: false
---

[Summary]({{< ref "/presentation" >}})

# Install guide
The current OMF version is **1.0.0**

### Requirements
The following commands are realized with **IntelliJ** IDE, **Gradle** build tool and **JDK 11** or greater.

## Installation
To install OMF as a library, you must add it to your build.gradle (or pom.xml). Once Maven Central address is filled, just add the library as you wish according to the one(s) you want to integrate : 

#### 1. Core
[Core on Maven Central](https://mvnrepository.com/artifact/com.samares-engineering.omf.2021x_R2/omf-core-framework)

	Gradle
	implementation group: 'com.samares-engineering.omf.2021x_R2', name: 'omf-core-framework', version: '1.0.0'

	Maven
	<dependency>
		<groupId>com.samares-engineering.omf.2021x_R2</groupId>
		<artifactId>omf-core-framework</artifactId>
		<version>1.0.0</version>
	</dependency>

#### 2. Public Features
[Public Features on Maven Central](https://mvnrepository.com/artifact/com.samares-engineering.omf.2021x_R2/omf-public-features)

	Gradle
	implementation group: 'com.samares-engineering.omf.2021x_R2', name: 'omf-public-features', version: '1.0.0' 

	Maven
	<dependency>
		<groupId>com.samares-engineering.omf.2021x_R2</groupId>
		<artifactId>omf-public-features</artifactId>
		<version>1.0.0</version>
	</dependency>

#### 3. Test Framework
[Test Framework on Maven Central](https://mvnrepository.com/artifact/com.samares-engineering.omf.2021x_R2/omf-test-framework)

	Gradle
	implementation group: 'com.samares-engineering.omf.2021x_R2', name: 'omf-test-framework', version: '1.0.0' 

	Maven
	<dependency>
		<groupId>com.samares-engineering.omf.2021x_R2</groupId>
		<artifactId>omf-test-framework</artifactId>
		<version>1.0.0</version>
	</dependency>

#### 4. Utils
[Utils on Maven Central](https://mvnrepository.com/artifact/com.samares-engineering.omf.2021x_R2/omf-utils)

	Gradle
	implementation group: 'com.samares-engineering.omf.2021x_R2', name: 'omf-utils', version: '1.0.0' 

	Maven
	<dependency>
		<groupId>com.samares-engineering.omf.2021x_R2</groupId>
		<artifactId>omf-utils</artifactId>
		<version>1.0.0</version>
	</dependency>
	
#### 5. Example Plugin
[Example on Maven Central](https://mvnrepository.com/artifact/com.samares-engineering.omf.2021x_R2/omf-example-plugin)

The plugin can be installed on Cameo System Modeler - *CSM* - and Magic Cyber System Engineer - *MCSE* - for the versions higher than 2019.
To install the example plugin in *CSM* or *MCSE*, click on Help -> Resource/Plugin Manager -> Import


