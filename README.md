# <img src="omf-logo.png" align="right" width="100">Open MBSE Framework

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

### Importing the project into IntelliJ (recommended)

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
#### IntelliJ (recommended)
- Right click the "runTest" task and select "Debug 'runTest'".

# Naming conventions
We follow the classic Java guidelines (**PascalCase** for classes, **camelCase** for methods/variables, **UPPER_SNAKE_CASE** for constants/enums)

**Packages:**  lowercase all attached as much as possible, can use underscores “_” if it makes things clearer

**Interfaces:** Don't prefix with “I”

**Abstract:** Prefix with A and do not use as type, only in extend. Use the interface instead.

Prefix all classes (or rather interfaces) usable by the framework's user by “**OMF**”, don't prefix for internal framework classes.

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


# Release process (for maintainers) NEEDS UPDATE AFTER CHANGES TO MAVEN CENTRAL PUBLISHING
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

# 2026x Migration

This section documents breaking changes and temporary deactivations due to MagicDraw API changes in the 2026x release.

### Environment Changes
- **JVM Version**: Updated from JVM 17 to JVM 21

### Removed APIs

#### PriorityProvider Interface
The `PriorityProvider` interface and associated `getPriority()`/`setPriority()` methods have been removed from the MagicDraw API. Affected classes:
- `OMFBrowserConfigurator`
- `OMFDiagramConfigurator`
- `OMFMainMenuConfigurator`
- `LiveActionEngine` interface and all implementations (`ALiveActionEngine`, `LiveActionEngineCharacterized`, `LiveActionEngineImpl`, `LiveActionEngineSession`)
- `AListener`

As time was limited, I just removed the method, didn't check if it breaks anything. Probably replaced by some other system which should be investigated.

#### Diagram Layout API
Auto layouting has changed, new pattern:
```java
// Old API
diagramPE.layout(false, new CompositeStructureDiagramLayouter());

// New API
diagramPE.open();
Layouting.layout(diagramPE, Layouting.COMPOSITE_DIAGRAM_LAYOUTER);
```

#### SymbolElementMap Access Pattern
Method to access diagram from presentation element has been removed. Fixed by change logic to iterating over diagrams
first then fetching their presentation elements.

#### Other API Changes
- `PresentationElement.getDiagramPresentationElement().getDiagramType().getType()` → `getAbstractDiagramPresentationElement().getDiagramTypeAsString()`
- `OMFUtils.getActiveDiagram().getDiagramPresentationElement()` → `OMFUtils.getActiveDiagram()` (already returns DiagramPresentationElement)

### Temporarily Deactivated Features

As I did not have enough time, I commented out code relying on breaking api changes. Throw an exception on usage to warn
about this. Will have to be migrated in future.

#### UndoManager
- `deactivateFirstRedo()`: Command history clearing disabled (TODO: migrate to new API). Affects testing framework.

#### TwcAccessor (Teamwork Cloud)
The following methods now throw `OMFLogException("Needs to be migrated to 2026x+")`:
- `openProject(String projectName)`
- `openBranchProject(String projectName, String branchName)`
- `createBranch(String projectName, String branchName, String branchDescription)`
- `createProject(String projectName)`
- `getExistingProjectDescriptor(...)` (both overloads)

These methods relied on `ITeamworkService.getProjectDescriptorByQualifiedName()` and `EsiUtils` methods that have changed in 2026x.

### V2ElementUIAction
Changed from using Dassault KerML types (`com.dassault_systemes.modeler.kerml`) to standard MagicDraw types. Untested.

# Plugin Developer Guide

This section explains how to **use OMF** to build your MagicDraw plugin. For internal implementation details, see [ARCHITECTURE.md](ARCHITECTURE.md).

## Module Overview

| Subproject | Role |
|---|---|
| `omf-core-framework` | All framework abstractions: features, actions, hooks, live actions, options, error handling, logging, model comparison |
| `omf-test-framework` | Functional testing infrastructure that runs inside a headless MagicDraw instance |
| `omf-public-features` | Ready-to-use feature implementations you can pull in as dependencies |
| `omf-example-plugin` | Reference plugin demonstrating every framework capability |
| `omf-gradle-plugin` | Custom Gradle plugin providing all build/run/test/package tasks |
| `omf-magicdraw-dependencies` | Extracts the MagicDraw ZIP and provides its JARs as a classpath |
| `smart-private` | Private proprietary features (PatternCreation) |

## Setting Up a Plugin Project using OMF

This section walks through configuring a new Gradle project that consumes OMF. Use `omf-example-plugin` as a reference.

### 1. Credentials (`gradle.properties`)

Copy `gradle.properties.secret` to `gradle.properties` at the project root and fill in the values. This file is gitignored — do not commit it.

**Which Maven repo to use:**
- **Private repo** (Nexus / Artifactory) — required for SNAPSHOT versions and any internal releases. Set all four `maven*` properties and keep the private `maven {}` blocks in `settings.gradle` and `build.gradle`.
- **Maven Central** — public release versions of OMF are published there. If you only need a release version, leave `mavenRepoUrl` and `mavenSnapshotRepoUrl` empty **and remove the private `maven {}` blocks** from `settings.gradle` and `build.gradle`. Gradle will error if those blocks are present with an empty URL.

```properties
# Private Maven repository (Nexus, Artifactory, etc.) — leave blank if using Maven Central only
mavenRepoUsername =
mavenRepoPassword =
mavenRepoUrl =
mavenSnapshotRepoUrl =   # same URL as release if using a single repo

# Team Work Cloud (only needed for TWC-based tests)
twcUsername =
twcPassword =
ipServer =
```

### 2. `settings.gradle` — apply the Gradle plugin

Register repos and the OMF Gradle plugin in `pluginManagement`. Remove the private `maven {}` blocks if using Maven Central only (see step 1).

```groovy
pluginManagement {
    repositories {
        // Remove these two blocks if using Maven Central only
        maven {
            allowInsecureProtocol = true
            credentials { username mavenRepoUsername; password mavenRepoPassword }
            url = mavenRepoUrl
        }
        maven {
            allowInsecureProtocol = true
            credentials { username mavenRepoUsername; password mavenRepoPassword }
            url = mavenSnapshotRepoUrl
        }
        gradlePluginPortal()
    }

    // When developing inside the OMF monorepo, load the plugin from source:
    includeBuild 'omf-gradle-plugin'
}

rootProject.name = 'my-plugin'
include 'my-plugin'
```

### 3. `gradle.properties` — plugin metadata and versions

```properties
# Versions
version        = 1.0.0-SNAPSHOT
kotlinVersion  = 1.9.23
jvmVersion     = 21

# MagicDraw version to build against
# Supported: 2021x_R1, 2021x_R2, 2022x_Refresh2, 2024x, 2024x_Refresh2,
#            2024x_Refresh2_SysmlV2, MSoSA_2026x, 2026x
mdVersionShort = 2026x

# Plugin metadata (used by mdPluginBuild{} and to generate plugin.xml)
pluginName     = MyPlugin
pluginId       = 012345          # unique ID registered with MagicDraw
pluginMainClass = com.example.myplugin.MyPlugin

# Test plugin (separate plugin that installs the test runner)
testPluginName      = MyPlugin Tests
testPluginId        = 012346
testPluginPackage   = com.example.myplugin.test
testPluginMainClass = com.example.myplugin.test.MyTestPlugin
```

### 4. `build.gradle` — apply plugin, declare dependencies, configure extension

```groovy
plugins {
    id 'com.samares-engineering.omf.omf-gradle-plugin'
    id 'org.jetbrains.kotlin.jvm'
}

repositories {
    // Remove these two blocks if using Maven Central only (see step 1)
    maven {
        allowInsecureProtocol = true
        credentials { username mavenRepoUsername; password mavenRepoPassword }
        url = mavenRepoUrl
    }
    maven {
        allowInsecureProtocol = true
        credentials { username mavenRepoUsername; password mavenRepoPassword }
        url = mavenSnapshotRepoUrl
    }
    mavenCentral()
}

dependencies {
    // --- MagicDraw installation ---
    // Declares which MagicDraw ZIP to extract. Resolved by mdVersionShort.
    mdApplicationArchive "com.nomagic:mcse:${mdVersionShort}@zip"

    // MagicDraw JARs on the compile/runtime classpath (extracted from the ZIP above)
    mdLibrary fileTree(dir: 'build/install/lib', include: ['*.jar', '**/*.jar'])

    // --- OMF artifacts ---
    pluginLibrary "com.samares-engineering.omf.${mdVersionShort}:omf-core-framework:<version>"
    pluginLibrary "com.samares-engineering.omf.${mdVersionShort}:omf-public-features:<version>" // optional

    // --- Test plugin ---
    testPluginLibrary "com.samares-engineering.omf.${mdVersionShort}:omf-test-framework:<version>"
    testPluginLibrary 'ant:ant-junit:1.6.5'
    testPluginLibrary 'org.apache.ant:ant:1.8.2'

    // --- Other plugins that MagicDraw loads automatically (compile only) ---
    // otherMDPluginLibrary fileTree(dir: 'build/install/plugins/com.nomagic.requirements', include: ['lib/**.jar'])

    // --- Additional plugin ZIPs auto-installed into MagicDraw ---
    // zippedMDPlugin 'com.nomagic.magicdraw:devtools:2026x@zip'
}

// Tell the OMF Gradle plugin about your plugin
mdPluginBuild {
    humanVersion          = version
    myPluginName          = pluginName
    myPackage             = 'com.example.myplugin'
    myPluginId            = pluginId
    myPluginMainClass     = pluginMainClass

    myTestPluginId        = testPluginId
    myTestPluginName      = testPluginName
    myTestPluginMainClass = testPluginMainClass
    myTestPackage         = testPluginPackage
}

java   { toolchain { languageVersion = JavaLanguageVersion.of(jvmVersion) } }
kotlin { jvmToolchain { languageVersion = JavaLanguageVersion.of(jvmVersion) } }

// Kotlin compile needs MagicDraw JARs on the classpath first
compileKotlin.dependsOn 'installMagicDraw', 'installZippedMDPlugins', 'clean'
```

**Dependency configuration summary:**

| Configuration | What to put in it |
|---|---|
| `mdApplicationArchive` | The MagicDraw ZIP — exactly one per build |
| `mdLibrary` | MagicDraw's own JARs (from `build/install/lib`) — compile/runtime classpath |
| `pluginLibrary` | Your plugin's runtime dependencies; bundled into the plugin ZIP |
| `testPluginLibrary` | Test plugin runtime dependencies (ant-junit, omf-test-framework, etc.) |
| `zippedMDPlugin` | Plugin ZIPs to auto-install into MagicDraw before launch (devtools, etc.) |
| `otherMDPluginLibrary` | JARs from required plugins that MagicDraw loads automatically — compile-only classpath |

### 5. Plugin entry point

Create a class extending `AOMFPlugin` and implement `initFeatures()`:

```kotlin
class MyPlugin : AOMFPlugin() {
    override fun initFeatures(): List<OMFFeature> = listOf(
        MyFeature(),
        // add more features here
    )
}
```

Set `myPluginMainClass` in `gradle.properties` to the fully-qualified name of this class. The `PackagePlugin` Gradle task auto-generates the `plugin.xml` descriptor from `mdPluginBuild {}` — no manual XML editing needed.

---

## Feature System

A **Feature** is the primary extension point. It is a self-contained unit of plugin functionality that groups together related actions, automations, options, and lifecycle hooks. The framework handles all the wiring — registration with MagicDraw's UI, session management, error handling — so a feature only needs to declare what it provides.

Extend `SimpleFeature` for most cases (all `init*()` methods return empty lists by default, so you only override what you need). Use `AFeature` directly if you need to customise the initialisation logic itself.

```kotlin
class MyFeature : SimpleFeature() {
    override fun getName() = "My Feature"
    override fun initActions(): List<UIAction> = listOf(MyAction())
    override fun initLiveActions(): List<LiveActionEngine> = listOf(myEngine())
    override fun initOptions(): List<Option> = myOptionsHelper.allOptions
    override fun initHooks(): List<Hook> = listOf(MyOnProjectOpenedHook())
}
```

Register it in your plugin's `initFeatures()`:
```java
@Override
protected List<OMFFeature> initFeatures() {
    return List.of(new MyFeature());
}
```

Features go through a two-phase initialisation so that items requiring an open project are not created prematurely:

| Phase | Method | When called |
|---|---|---|
| 1 | `initFeature(plugin)` | On feature registration — bind to plugin, store reference |
| 2 | `initFeatureItems()` via `initActions/LiveActions/Options/Hooks()` | Right after phase 1 — create global items |
| 2b | `initProjectOnlyFeatureItems()` via `initProject*()` overrides | Each time a project opens — create items needing an open project |

## UI Actions

A UI Action is something the user triggers explicitly — via a right-click context menu in the browser, on a diagram element, or from the main menu. Each action class can expose itself in all three places or just one.

```kotlin
class MyAction : ElementUIAction() {
    override fun getName() = "Do Something"

    @BrowserAction
    override fun checkBrowserAvailability(elements: List<Element>): Boolean =
        elements.size == 1 && elements[0] is Class

    @BrowserAction
    override fun executeBrowserAction(elements: List<Element>) {
        val target = elements[0] as Class
        // Model mutations here — already inside an MD session, do NOT open one manually
    }
}
```

**Key points:**
- Extend `ElementUIAction` (not `AUIAction` directly) — it provides default implementations of `getSelectedBrowserElements()`, `getSelectedDiagramElements()`, and `getSelectedDiagramPresentationElements()` typed to `Element`/`PresentationElement`, which covers the vast majority of use cases. Use `AUIAction<E, PE>` directly only when you need custom element types.
- Annotate `check*` and `execute*` overrides with `@BrowserAction`, `@DiagramAction`, or `@MenuAction`. Without the annotation the method is ignored by that context. A single action class can carry multiple annotations if it should appear in multiple contexts.
- `checkAvailability` is called by MagicDraw every time the context menu is rebuilt — keep it fast and side-effect free.
- `executeAction` is always called inside an MD session wrapped by OMF's error barrier. Never open a `SessionManager` session manually inside an action.
- Add `@DeactivateListener` on the class to prevent your feature's own listeners from reacting to model changes made by this action (avoids cascading events). Omit it when listeners should react normally.

## Live Actions

A Live Action is an automation that reacts to model mutations **without user interaction**. Whenever MagicDraw fires a `PropertyChangeEvent` (element created, deleted, or a property updated), OMF evaluates all registered live actions and executes those that match.

Each live action belongs to an engine typed by `LiveActionType`, which determines which category of mutation event it listens to:

| `LiveActionType` | Fires when… |
|---|---|
| `CREATE` | An element is created in the model |
| `DELETE` | An element is deleted from the model |
| `UPDATE` | A property or reference on an element changes |
| `ANALYSE` | A read-only analysis pass runs (no model change expected) |
| `HISTORY` | MagicDraw records a change in the command history (after the fact) |
| `AFTER_AUTOMATION` | After another automation (live action) has already executed — useful for chaining |
| `CREATE_UNDO_REDO` | An element creation is undone or redone |
| `DELETE_UNDO_REDO` | An element deletion is undone or redone |
| `UPDATE_UNDO_REDO` | A property change is undone or redone |
| `ANALYSE_UNDO_REDO` | An analysis pass is undone or redone |
| `HISTORY_UNDO_REDO` | A history entry is undone or redone |
| `AFTER_AUTOMATION_UNDO_REDO` | A chained automation is undone or redone |

A live action has two responsibilities: **matching** (should this event trigger me?) and **executing** (what do I do?):

```kotlin
class OnClassCreatedLA : ALiveAction<PropertyChangeEvent>() {

    // Called for every event of the engine's type — return true to execute
    override fun matches(event: PropertyChangeEvent): Boolean =
        event.newValue is Class

    // Called only when matches() returned true — already inside an MD session
    override fun execute(event: PropertyChangeEvent) {
        val newClass = event.newValue as Class
        // React to the creation
    }
}
```

Wire it up in your feature:
```kotlin
override fun initLiveActions(): List<LiveActionEngine> {
    val engine = ALiveActionEngine(LiveActionType.CREATE)
    engine.addLiveAction(OnClassCreatedLA())
    return listOf(engine)
}
```

**Key points:**
- `matches()` is called very frequently — keep it cheap. Throw an exception from `matches()` only if something is genuinely broken; it will be caught and wrapped as `ErrorWhileEvaluationLiveActionException`.
- `execute()` runs inside an MD session with the error barrier active, same as UI actions.
- By default listeners are **deactivated** during `execute()` to prevent your own `UPDATE` live actions from re-triggering on the changes you make. Add `@KeepListenerActivated` on the live action class if you specifically need listeners to stay active.
- Multiple live actions can share the same engine; they are evaluated in order.

## Hooks

Hooks let your feature respond to lifecycle events from MagicDraw, the open project, or the feature itself. Unlike live actions (which react to model data changes), hooks react to application-level state transitions.

Always extend the **abstract base class** (prefixed `A`), not the interface directly — the base handles listener deactivation and the error barrier for you.

| Hook base class | Fires when… |
|---|---|
| `AOnMagicDrawStartHook` | MagicDraw has fully started up (before any project is opened) |
| `AOnProjectOpenedHook` | A project is opened (including on startup if a project auto-loads) |
| `AOnProjectClosedHook` | A project is closed |
| `AOnProjectCreatedHook` | A new project is created (not just opened from disk) |
| `AOnProjectSavedHook` | A project is saved |
| `AOnFeatureRegisteringHook` | This feature is being registered/activated |
| `AOnFeatureUnregisteringHook` | This feature is being unregistered/deactivated |

```kotlin
class RefreshOnProjectOpen : AOnProjectOpenedHook() {
    override fun executeHook() {
        val project = Application.getInstance().project ?: return
        // Called each time a project opens — safe to read the model here
    }
}
```

Register via `initHooks()` (for hooks that should always be active) or `initProjectOnlyHooks()` (for hooks that should only be active while a project is open):

```kotlin
override fun initHooks(): List<Hook> = listOf(RefreshOnProjectOpen())
```

**Key points:**
- `executeHook()` runs outside an MD session. If your hook needs to mutate the model, override `executeInSessionHook()` instead — OMF will wrap it in a session automatically.
- `AOnFeatureRegisteringHook` and `AOnFeatureUnregisteringHook` are useful for setup/teardown logic that should run exactly once per feature lifecycle (e.g. loading a config file, releasing resources).

## Environment Options

Options expose configuration to the user through MagicDraw's Environment Options or Project Options panel. Group related options into a helper class that extends `EnvOptionsHelper`:

```kotlin
class MyOptionsHelper(group: OMFPropertyOptionsGroup) : EnvOptionsHelper(group) {

    val serverPort = OptionImpl(
        "MyPlugin.serverPort",   // unique key — use reverse-DNS style to avoid clashes
        OptionKind.Environment,  // or OptionKind.Project for per-project settings
        "8080",                  // default value
        "Server Port",           // display name in the options panel
        "Port for the embedded server",  // tooltip
        group
    )

    override val allOptions get() = listOf(serverPort)
}
```

Return `myOptionsHelper.allOptions` from `Feature.initOptions()`. OMF registers each option with MagicDraw's options system and the value persists across sessions.

Read the current value at any time:
```kotlin
val port = myOptionsHelper.serverPort.property.value
```

React to changes with a listener:
```kotlin
myOptionsHelper.serverPort.addListener { property ->
    restartServer(property.value)
}
```

Use `OptionKind.Project` for settings that are per-project (stored in the `.mdzip` file) and `OptionKind.Environment` for settings that are per-user/machine (stored in MagicDraw's environment options).

## Logging & Error Handling

### Logging

Use `OMFLog` to build a rich log message, then deliver it with `OMFLogger`:

```kotlin
// Build a message with formatting
val log = OMFLog()
    .text("Could not process ")
    .bold(element.name)
    .text(" — property ")
    .italic("myProp")
    .text(" was null.")
    .breakLine()
    .linkElement("Navigate to element", element)

// Send to MagicDraw's notification centre
OMFLogger.logToNotification(log, OMFLogLevel.WARNING, this)

// Or send to both notification centre and system console
OMFLogger.log(log, OMFLogLevel.ERROR, this)
```

`OMFLog` formatting methods: `text()`, `bold()`, `italic()`, `underline()`, `strike()`, `color(text, OMFColors.RED)`, `warn()`, `info()`, `err()`, `breakLine()`, `linkElement(label, element)`, `linkAction(label, action)`.

Log levels: `OMFLogLevel.INFO`, `WARNING`, `ERROR`.

### Throwing Exceptions

OMF's error barrier catches all unchecked exceptions thrown inside actions, live actions, and hooks, and handles them automatically. You do not need to catch exceptions yourself (unless you have a specific reason/can recover) — just throw the right type:

**`OMFLogException`** — the standard choice. Wraps an `OMFLog` so the user sees a rich formatted message in the notification centre. OMF rolls back any open session automatically.

```kotlin
throw OMFLogException(
    OMFLog().text("Element ").bold(el.name).text(" has no owner.")
)
```

**`OMFCriticalException`** — for situations the feature cannot recover from. Adds modifiers that tell OMF how to react beyond just logging:

```kotlin
throw OMFCriticalException(
    OMFLog().err("CSV config file not found — feature cannot operate."),
    OMFExceptionModifier.DEACTIVATE_FEATURE,  // auto-disable this feature
    OMFExceptionModifier.NO_ROLLBACK          // keep any partial changes
)
```

| Modifier | Effect |
|---|---|
| _(none)_ | Log message to user + roll back the current session |
| `NO_ROLLBACK` | Do not roll back model changes after handling |
| `DEACTIVATE_FEATURE` | Automatically unregister the feature that threw |
| `SILENT` | Do not show anything to the user |
| `WARNING` | Show the message as a warning instead of an error |

Modifiers can be combined — pass multiple as varargs.

**Other `RuntimeException`** — any standard Java/Kotlin exception (e.g. `NullPointerException`) is caught by the barrier and handled with the default behaviour: log a generic error message to the user, print the stack trace to stdout, roll back the session.

### The Error Barrier

You generally don't need to call the barrier yourself — it is applied automatically around all action execution, live action execution, and hook execution. The rule is simple: **throw, don't catch**. Let OMF handle it.

If you need to run code outside those contexts (e.g. in a background thread or a listener you registered directly with MagicDraw), wrap it yourself:

```kotlin
OMFBarrierExecutor.executeWithinBarrier({ myFeature }, {
    // code that might throw OMFLogException
})

// Or if you need a model session:
OMFBarrierExecutor.executeInSessionWithinBarrier({ myFeature }, {
    // code that mutates the model
})
```

## Writing Tests

Extend `AModelComparatorTestCase` for snapshot-based regression tests. The pattern is: load an init project, run your action, then let the framework compare the resulting project against a saved oracle.

```java
public class MyFeatureTest extends AModelComparatorTestCase {

    @Override
    protected String getInitProjectPath() { return "init_my_Test.mdzip"; }

    @Override
    protected String getOracleProjectPath() { return "oracle_my_Test.mdzip"; }

    @Test
    public void testMyAction() {
        // Trigger your action — the framework provides findElement(), getProject(), etc.
        new MyAction().executeBrowserAction(List.of(findElement("MyBlock")));
        // On tearDown, OMF automatically compares the open project to oracle_my_Test.mdzip
        // Any structural difference fails the test
    }
}
```

`init_*.mdzip` is loaded before the test body runs. `oracle_*.mdzip` is compared against the live project in `tearDown()` using `OMFModelComparator` — any difference (added, removed, or modified element) fails the test.

Run tests with `./gradlew runTests` (opens an HTML report) or `./gradlew runTestsNoLog` (faster, suppresses MagicDraw logs).

## Available Public Features

Import these as `pluginLibrary` dependencies to get ready-to-use functionality:

| Feature class | What it provides |
|---|---|
| `APIServerFeature` | Embedded Jetty REST API server — exposes the open model over HTTP. Port configurable via env options. |
| `TestGenerationFeature` | Generates JUnit test stubs from model snapshots using JavaPoet + JavaParser |
| `StereotypesFeature` | CSV-driven stereotype application rules (`type_config.csv`, `instance_config.csv`, `organizer_config.csv`) |
| `CloneElementFeature` | Browser actions to clone Part, Port, Property, or Type elements |
| `HyperLinkFeature` | Adds a navigable hyperlink from a part usage to its block definition in diagrams |
| `LockSafeFeature` | Wraps model operations with TWC lock acquisition/release |
| `FeaturesDeactivationFeature` | Adds env options to enable/disable individual features at runtime without rebuilding |
| `FeatureActivationFromOptionFeature` | Ties a feature's registered/unregistered state to an env option toggle |

## Dependency & Version Matrix

| Component | Version | Used for |
|---|---|---|
| Kotlin | 1.9.23 | Primary language for all new code |
| JVM target | 21 | Runtime requirement (updated from 17 in the 2026x migration) |
| MagicDraw | 2026x | Target platform — set `mdVersionShort` in `gradle.properties` to switch versions |
| Jetty | 9.4.38 | Embedded HTTP server backing `APIServerFeature` |
| JUnit | 4.12 | Test runner used by `omf-test-framework` for headless MagicDraw tests |
| JavaPoet | 1.13.0 | Generates Java source files programmatically — used by `TestGenerationFeature` to emit test stubs |
| JavaParser | 3.25.4 | Parses existing Java source files — used by `TestGenerationFeature` to analyse and update test code |
| JDOM2 | 2.0.6 | XML processing — used for reading and writing MagicDraw plugin/resource descriptor XML files |

---

# Authors

- ### Samares Engineering
  - Quentin CESPEDES
  - Clément MEZERETTE
  - Hugo STINSON
  - Calliopé DANTON LALOY