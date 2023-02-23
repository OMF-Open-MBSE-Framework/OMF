# Open MBSE Framework
This project contains a framework for the development of Magicdraw plugins. As such, and to demonstrate the capabilities of the framework through basic automations and use cases, you can find an example plugin attached. You can also clone the project (source code), or import the jar and take advantage of the developed automations (TBD).

# Install guide
### Requirements
- The following commands are realized with **IntelliJ** IDE, **Gradle** build tool and **JDK 11** or greater.
- The plugin can be installed on Cameo System Modeler - *CSM* - and Magic Cyber System Engineer - *MCSE* - for the versions higher than 2019.

## Installation
### 1. As a Java archive (JAR)
You can download the jar from the Maven Central Repository at the following adress (TBD)

### 2. As a Gradle Project
1. Clone the project from GitLab
2. Create a new IntelliJ Project from existing source (*File → New → Project From Existing Sources*)
3. Select the folder where you downloaded the project from GitLab
4. You may want to fill the gradle.properties
5. Complete the gradle.properties.secret if you need to (to avoid sharing credentials & other sensitive properties, we store them outside of the project in the .gradle folder)
   - Copy the contents of the *gradle.properties.secret* file into *\<User folder\>/.gradle/gradle.properties* (create the file if it does not exist)
   - Fill properties with the correct values/credentials (ask an other dev for the info if needed)

### 3. As a Cameo System Modeler Plugin

To install the plugin in *CSM* or *MCSE*, click on Help -> Resource/Plugin Manager -> Import

### Running the project

- From command line :

> gradle debugJava

- From IntelliJ :

  - Open project as a gradle project
  - In the gradle tool window, right-click *Tasks/_dev/debugJava* and select the *debug* option

# Authors

- ### Samares Engineering
  - Quentin CESPEDES
  - Clément MEZERETTE
  - Hugo STINSON