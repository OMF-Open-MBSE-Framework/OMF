# Open MBSE Framework
This project contains a framework for the development of Magicdraw plugins. As such, and to demonstrate the capabilities of the framework through basic automations and use cases, you can find an example plugin attached. You can also clone the project (source code), or import the jar and take advantage of the developed automations (TBD).


# Dev environment setup

### Importing the project into Eclipse

- Make sure the root project folder name is the same as the root project name defined in setting.gradle
- Import the project into Eclipse as an existing Gradle project
- Check that eclipse uses a JVM 11 for gradle in *Project -> Properties -> Gradle -> Java Home*
- Check that the java compiler used is a JVM 11 in *Project -> Properties -> Java -> Compiler*
- Check that the Gradle build folder (_/build_) is not filtered in the Package Explorer. This is
  necessary to see the results of builds and troubleshoot build problems.
- Copy the contents of the _gradle.properties.secret_ file to you gradle system properties file
  *\<User dir\>/.gradle/gradle.properties* and fill them with the correct values. Create the file if it does not exist.
- If you want to be able to run gradle from the commandline, add the following line to your
  gradle system properties:
  - `org.gradle.java.home=<path_to_jdk_directory>` (make sure to use forward slashes '/' in the path)
- You can then run tasks via de commandline by calling `gradlew <task_name>` from the project root. However, this is not
  necessary most of the time as you can launch tasks from the Eclipse Gradle ui.

### Importing the project into IntelliJ

- Open the project in IntelliJ File -> New -> then either from existing sources if you already cloned the project or from
    version control if you want to clone the project.
- Ctrl+Alt+S to open the settings -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JVM -> Make sure a
  JDK 11 is selected.
- Ctrl+Alt+Shift+S to open the project structure -> Project -> Project SDK -> Make sure a JDK 11 is selected.
- Copy the contents of the _gradle.properties.secret_ file to you gradle system properties file
  *\<User dir\>/.gradle/gradle.properties* and fill them with the correct values. Create the file if it does not exist.
- If you want to be able to run gradle from the commandline, add the following line to your
  gradle system properties:
  - `org.gradle.java.home=<path_to_jdk_directory>` (make sure to use forward slashes '/' in the path)
- You can then run tasks via de commandline by calling `gradlew <task_name>` from the project root. However, this is not
  necessary most of the time as you can launch tasks from the Eclipse Gradle ui.

### Running the plugin
- Run the task "runPlugin" to run the plugin. This will launch MagicDraw with the plugin installed.

### Running the tests
- Run the task "runTests" to run the tests. This will launch MagicDraw in headless mode and with the plugin + test plugin
  installed. The tests will be run, MagicDraw will be automatically closed and a browser window will open with the test
  results.
- For faster tests you can run the task "runTestsNoLog" which will launch the tests with MagicDraw in non-verbose mode.
  Of course if you need to troubleshoot the tests, this is not advised.

### Debugging plugin/tests
#### Eclipse
- Create a new run configuration of type **Remote Java Application** and set the port to **5005**. In the sources tab, add the
  the project as a **Java Project**.
- Place a breakpoint in the code.
- Run the plugin or tests with the "debugPlugin" or "debugTests" tasks respectively.
- Once you see the message "Listening for transport dt_socket at address: 5005", you can connect the debugger to the
  running process.
- Check that the breakpoint is hit.
- 
#### IntelliJ
- Right click the "runTest" task and select "Debug 'runTest'".

### Modifying the plugin packaging
The plugin is packaged from the template in _src/main/resources/dist/template_.

- Modifying the plugin version -> change the version in the gradle.properties file
- Modifying the plugin/resource descriptors -> modify the template descriptor in _template/descriptors_.
  Do not modify the "${}" placeholders as this will mess with the generation.
- Adding/modifying a resource that needs to be installed in MagicDraw (eg profiles) -> Add them to _template/install_
  folder as if the folder was the root of the MagicDraw installation.
- Adding a plugin library -> Add the library in the dependency section of the build.gradle file with the "pluginLibrary"
  configuration. The library will be copied to the _plugin/lib_ folder during the build.
- Adding a plugin resource -> Add the resource in the _template/plugin_ folder as if the folder was the root of the plugin
  folder in MagicDraw.

# Exception handling & logging in plugin using OMF
To log a message, create a OMFLog representing a log message, then use OMFLogger to log the OMFLog to MD console, console, notification etc... 
Any runtime exception in the plugin's business not caught by the plugin is handled by OMF (exception barrier pattern). 
By default, the exception is handled in the following way: the message is logged to the user in Magicdraw with the context
of the plugin & feature, the stack trace 
is printed in sysout (editor console), and when we are in the context of a session a rollback is done cancelling any 
changes to the model.
There are 3 types of exceptions :
- **OMFLogException** is a RuntimeException whose message is a OMFLog. Use this exception in most cases, (or extend it)
especially if you want to have MD specific info in your message (like formatting, links to model elements....). 
- **OMFCriticalException** is an OMFLogException that is used to signal to OMF that the action is broken
(we can't handle/recover from the error). In addition to an OMFLog, we can add some "modifiers" (OMFExceptionModifiers)
to tell OMF how to handle the exception: 
  - NO_ROLLBACK => Changes done to the model are not cancelled
  - DEACTIVATE_FEATURE => Automatically deactivate the feature containing the action that failed
  - SILENT => Don't log message to the user
  - WARNING => Log message to the user as a warning
- **Other RuntimeException** any other RuntimeException (java exceptions like NullPointerExceptions or exceptions thrown
by libraries). These exceptions are handled with the default behaviour explained above.


# Release process (for maintainers)
- Update release notes in `CHANGELOG.md` (see [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)) by moving  [UNRELEASED] changes to a new version
- Commit changes
- Create a new branch from `0-DEV` with the name `RELEASE/<version>` (e.g. `RELEASE/1.0.0`)
- On the release branch, change the version in `gradle.properties`. Version must follow Semver : **3 numbers, be > to previous version. Don't forget to remove SNAPSHOT suffix !**
- Commit and push. 
- Publish to nexus & maven central staging repo => gradle task `publish` The CI/CD will build the plugin and publish it 
to Nexus & Maven Central staging (this is broken at the moment, so you will have to do it manually)

** Manual Publish task **
1) Follow the 'First time setup' section of [Our guide on how to release to maven central](https://samaresengineering.atlassian.net/wiki/spaces/ST/pages/2514026503/Publish+to+Maven+Central)
2) Run the gradle task 'publishing/publishAllPublicationsToMavenCentralRepository'

- If there are validation problems:
  - Fix them on the release branch
  - Commit and push
  - Cherry-pick the fix(es) on `0-DEV`
- Finalise the deployment to Maven Central by doing a manual release. [Our guide on how to release to maven central](https://samaresengineering.atlassian.net/wiki/spaces/ST/pages/2514026503/Publish+to+Maven+Central)
- Update the OMF public repo (is this still worth it?)
- Increment SNAPSHOT version in `gradle.properties` on `0-DEV` branch to the next patch version (SNAPSHOT version should be the next patch version, for example if the latest release is 2.1.0 it shoud be 2.1.1-SNAPSHOT)
- Commit and push

# Naming conventions
We follow the classic Java guidelines (**PascalCase** for classes, **camelCase** for methods/variables, **UPPER_SNAKE_CASE** for constants/enums)

**Packages:**  lowercase all attached as much as possible, can use underscores “_” if it makes things clearer

**Interfaces:** Don't prefix with “I”

**Abstract:** Prefix with A and do not use as type, only in extend. Use the interface instead.

Prefix all classes (or rather interfaces) usable by the framework's user by “**OMF**”, don't prefix for internal framework classes.

# Authors

- ### Samares Engineering
  - Quentin CESPEDES
  - Clément MEZERETTE
  - Hugo STINSON