# Open MBSE Framework
This project contains a framework for the development of Magicdraw plugins. As such, and to demonstrate the capabilities of the framework through basic automations and use cases, you can find an example plugin attached. You can also clone the project (source code), or import the jar and take advantage of the developed automations (TBD).

# Install guide
### Requirements
- The following commands are realized with **IntelliJ** IDE, **Gradle** build tool and **JDK 11** or greater.
- The plugin can be installed on Cameo System Modeler - *CSM* - and Magic Cyber System Engineer - *MCSE* - for the versions higher than 2019.

## Installation
TODO

# Release process (for maintainers)
- Update release notes in `CHANGELOG.md` (see [Keep a Changelog](https://keepachangelog.com/en/1.0.0/))
- Commit changes
- Create a new branch from `0-DEV` with the name `RELEASE/<version>`
- On the release branch, change the version in `gradle.properties` (remove SNAPSHOT suffix)
- Commit and push. The CI/CD will build the plugin and publish it to Nexus & Maven Central staging
- If there are validation problems:
  - Fix them on the release branch
  - Commit and push
  - Cherry-pick the fix(es) on `0-DEV`
- Finalise the deployment to Maven Central by doing a manual release. [Our guide on how to release to maven central](https://samaresengineering.atlassian.net/wiki/spaces/ST/pages/2514026503/Publish+to+Maven+Central)
- Update the OMF public repo
- Increment SNAPSHOT version in `gradle.properties` on `0-DEV` branch
- Commit and push

# Naming conventions
We follow the classic Java guidelines (**PascalCase** for classes, **camelCase** for methods/variables, **UPPER_SNAKE_CASE** for constants/enums)

**Packages:**  lowercase all attached as much as possible, can use underscores “_” if it makes things clearer

**Interfaces:** Don't prefix with “I”

**Abstract:** Prefix with A and do not use in the code. Use the interface instead.

Prefix all classes usable by the framework's user by “**OMF**”, don't prefix for internal framework classes.

# Authors

- ### Samares Engineering
  - Quentin CESPEDES
  - Clément MEZERETTE
  - Hugo STINSON