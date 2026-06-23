# OMF Architecture Reference

Internal implementation details for contributors and maintainers. For the developer-facing guide (how to build features), see [README.md](README.md).

---

## 1. Module Map & Subproject Dependencies

```
omf-magicdraw-dependencies      (classpath provider — extracts MagicDraw JARs)
        ↑
omf-core-framework               (all framework abstractions)
        ↑
 ┌──────┴────────────────────────────────────┐
 │                                           │
omf-test-framework               omf-public-features
        ↑                              ↑
        │                         smart-private
        └──────────┬───────────────────┘
               omf-example-plugin    (reference plugin, wires everything together)

omf-gradle-plugin                (build tooling — applied via includeBuild)
```

Base package: `com.samares_engineering.omf`

Key versions: OMF 2.2.1-SNAPSHOT · Kotlin 1.9.23 · JVM 21 · MagicDraw default 2026x

---

## 2. Plugin Initialisation Pipeline

**Entry point:** `AOMFPlugin.init()` — called once by MagicDraw's plugin loader.

Every concrete plugin extends `AOMFPlugin` (which extends MagicDraw's `Plugin`). The `init()` method runs a fixed 10-step pipeline:

```
1.  initLogger()                        — boot OMF logging subsystem
2.  OMFErrorHandler.init(this)          — create error-handler singleton bound to this plugin
3.  configureMagicDrawHookExecutor()    — register MagicDraw lifecycle hook executor
4.  configureListenerManager()          — initialise ListenerManager singleton
5.  configureActions()                  — register OMFBrowserConfigurator / OMFDiagramConfigurator
                                          / OMFMainMenuConfigurator with MagicDraw
6.  configureProjectListener()          — attach ProjectListener for project open/close events
7.  configureEnvironmentOptions()       — register OMFPropertyOptionsGroup in MD preferences UI
8.  configureProjectOptions()           — register project-scoped options
9.  configureFeatureRegisterer()        — create 8 FeatureItemRegisterer instances (4 global + 4 project-only)
10. configureFeatures() / registerAllFeatures()  — call user-defined initFeatures(),
                                                   register every returned feature
```

Singletons initialised during this pipeline:
- `OMFErrorHandler.getInstance()`
- `ListenerManager.getInstance()`
- `OMFAutomationManager.getInstance()`

**Concrete example:** `omf-example-plugin/…/OMFExamplePlugin.java` — implements `initFeatures()` returning ~20 feature instances, some deactivated on startup via explicit `feature.deactivate()` calls.

---

## 3. Feature Registration Internals

### Type Hierarchy

```
OMFFeature (interface)
  └── AFeature (abstract) — maintains four registrable-item lists + project-only variants
        └── SimpleFeature — all list methods return empty by default (use when only a subset is needed)
```

### Two-Phase Initialisation

| Phase | Method | When called | Purpose |
|---|---|---|---|
| 1 | `initFeature(OMFPlugin)` | On registration | Bind to plugin; store reference |
| 2 | `initFeatureItems()` | After `initFeature` | Create global actions, options, live actions, hooks |
| 2b | `initProjectOnlyFeatureItems()` | When a project opens | Create items that require an open project |

### Registration Flow via `FeatureRegisterer`

```
plugin.registerAllFeatures()
  → FeatureRegisterer.registerFeature(feature)
      → feature.initFeature(plugin)
      → feature.register()
      → registerFeatureItems(feature)           // delegates to 8 FeatureItemRegisterer instances
      → if project open: registerProjectOnlyFeatureItems(feature)
      → fires FeatureRegisteringEventHandler
```

### The 8 `FeatureItemRegisterer` Instances

| # | Registerer | Scope |
|---|---|---|
| 1 | `UIActionFeatureItemRegisterer` | global |
| 2 | `LiveActionEngineFeatureItemRegisterer` | global |
| 3 | `OptionFeatureItemRegisterer` | global |
| 4 | Hook registerers (Feature / MD / Project) | global |
| 5–8 | Project-only variants of 1–4 | project-only |

`RegistrableFeatureItem` interface (implemented by every registrable): `activate()` / `deactivate()` / `isActivated()`.

---

## 4. UI Action Execution Pipeline

### Type Hierarchy

```
UIAction (interface)
  └── AUIAction<E, P> (abstract Kotlin) — generic over element type E and presentation type P
```

Each `AUIAction` holds three internal MagicDraw `NMAction` instances (`browserNMAction`, `diagramNMAction`, `menuNMAction`), created lazily by `initTreeActions()` / `initDiagramActions()` / `initMenuActions()`.

### Execution Guard

All action execution routes through `executeAUIActionWithinBarrier()`:

```
if project open  → OMFBarrierExecutor.executeInSessionWithinBarrier()  (creates MD session)
if no project    → OMFBarrierExecutor.executeWithinBarrier()            (no session)
```

Both paths deactivate listeners before running the body and restore them in `finally`.

### Availability Checks

`checkBrowserAvailability()`, `checkDiagramAvailability()`, `checkMenuAvailability()` each call `checkWithinOMFBarrier()`. If the check throws `OMFCriticalException` with `DEACTIVATE_FEATURE`, the feature is automatically deregistered.

### Configurators (registered at plugin init)

| Configurator | MagicDraw attachment point |
|---|---|
| `OMFBrowserConfigurator` | Browser context-menu |
| `OMFDiagramConfigurator` | Diagram context-menu |
| `OMFMainMenuConfigurator` | Main menu bar |

### Action Annotations

| Annotation | Effect |
|---|---|
| `@BrowserAction` | Tags a browser action method for the configurator |
| `@DiagramAction` | Tags a diagram action method |
| `@MenuAction` | Tags a menu action method |
| `@DeactivateListener` | Listeners are deactivated during execution |
| `@KeepListenerActivated` | Listeners stay active during execution (live actions only) |

---

## 5. Session & Rollback Mechanism

### `OMFBarrierExecutor` — Two Execution Paths

**Path 1 — `executeWithinBarrier(block)`** (no MD session):
```
→ deactivate listeners
→ run block
→ catch OMFLogException / Exception  → OMFErrorHandler.handleException()
→ finally: reactivate listeners
```

**Path 2 — `executeInSessionWithinBarrier(block)`** (inside MD session):
```
→ SessionManager.callInsideSession(
      deactivate listeners
      run block
      catch OMFLogException / Exception  → OMFErrorHandler.handleException()
      if handleException() throws RollbackException → abort session
  )
→ finally: reactivate listeners
```

`RollbackException` is internal only — thrown by `OMFErrorHandler.rollbackChanges()` and caught by the barrier's outer `callInsideSession` to signal MD to abort the transaction.

---

## 6. Error Handler Decision Tree

`OMFErrorHandler.handleException(exception, impactedFeature)`:

```
if OMFCriticalException
  ├─ if !SILENT          → log to MD notification centre
  ├─ if DEACTIVATE_FEATURE → FeatureRegisterer.unregisterFeature(impactedFeature)
  └─ if !NO_ROLLBACK     → rollbackChanges() → throw RollbackException

if OMFLogException (non-critical)
  └─ log to MD notification centre (always)

if other RuntimeException
  └─ log generic message + print stack trace + rollbackChanges()
```

### Full Error Data-Flow

```
User clicks action
  → executeAUIActionWithinBarrier()
      → executeInSessionWithinBarrier()
          [inside MD session]
          → throw new OMFCriticalException(log, DEACTIVATE_FEATURE)
          → caught by barrier
          → OMFErrorHandler.handleException()
              → log to UI (not SILENT)
              → unregister feature (DEACTIVATE_FEATURE)
              → throw RollbackException (not NO_ROLLBACK)
          → RollbackException propagates out of callInsideSession
          → MD aborts session transaction
      → finally: listeners reactivated
```

---

## 7. Listener Management

`ListenerManager` (singleton) holds two lists:
- `coreListeners` — registered at plugin init, always present
- `featureListeners` — added dynamically as features register

State toggle (called by the barrier):
- `deactivateAllListeners()` — before every action / live-action execution
- `activateAllListeners()` — in `finally` after execution

`IListener` interface: `activate()` / `deactivate()` / `isActivated()` / `isRegistered()` / `getLiveActionEngineMap()` / `setLiveActionEngineMap()`.

---

## 8. Live Action Engine Internals

### Type Hierarchy

```
LiveActionEngine<EVT> (interface)
  └── ALiveActionEngine (concrete) — holds List<LiveAction>, priority field
```

### `LiveActionType` Enum (LiveActionType.kt)

```
CREATE, DELETE, UPDATE, ANALYSE, HISTORY, AFTER_AUTOMATION
CREATE_UNDO_REDO, DELETE_UNDO_REDO, UPDATE_UNDO_REDO,
ANALYSE_UNDO_REDO, HISTORY_UNDO_REDO, AFTER_AUTOMATION_UNDO_REDO
```

### Execution Loop — `processAllMatchingLiveActions(event)`

```
1. getAllMatchingLiveActions(event)
     → filter by liveAction.matches(event) [wrapped in barrier for safety]
     → filter by liveAction.isActivated()
2. for each match:
     OMFBarrierExecutor.executeInSessionWithinBarrier(liveAction::execute, feature)
     if !@KeepListenerActivated → listeners deactivated during execute
```

If `matches()` throws, the exception is caught and wrapped as `ErrorWhileEvaluationLiveActionException`.

---

## 9. Hook System Internals

### Base Interface: `Hook`

- `executeHook()` — outside a session
- `executeInSessionHook()` — inside an MD session
- `shallDeactivateListener()` / `hasDeactivateListenerAnnotation()` — listener helpers

### Lifecycle Families

| Family | Hooks | Abstract Base | Executor |
|---|---|---|---|
| Feature | `OnFeatureRegisteringHook`, `OnFeatureUnregisteringHook` | `AFeatureLifeCycleHook` | `FeatureHookExecutor` |
| MagicDraw | `OnMagicDrawStartHook` | `AOnMagicDrawStartHook` | `MagicDrawHookExecutor` |
| Project | `OnProjectOpenedHook`, `OnProjectClosedHook`, `OnProjectCreatedHook`, `OnProjectSavedHook` | `AProjectLifeCycleHook` + per-event abstract | `ProjectHookExecutor` |

Always extend the abstract base (`AOnProjectOpenedHook`, etc.) — it handles listener deactivation and barrier wrapping automatically.

### Registration

Features return hook instances from `initHooks()` or `initProjectOnlyHooks()`. The framework stores them and calls the appropriate executor when the lifecycle event fires via MD's native listener/hook callbacks.

---

## 10. Options System Internals

### Key Types

| Class | Role |
|---|---|
| `Option` (interface) | Wraps a MagicDraw `Property`; knows its `OptionKind` |
| `OptionKind` (enum) | `Environment` or `Project` |
| `EnvOptionsHelper` (abstract) | Helper base holding an `OMFPropertyOptionsGroup` reference |
| `OMFPropertyOptionsGroup` | Groups options under a named category in MD preferences UI |

### Registration Path

```
Feature.initOptions() → List<Option>
  → OptionFeatureItemRegisterer.register(option)
      → if Environment: registers with MD's EnvironmentOptions
      → if Project: registers with MD's ProjectOptions
```

Listening to changes: `Option.addListener()` / `addEnvironmentListener()` / `addProjectListener()`.

---

## 11. Model Comparison Internals

Used by the test framework and by `AModelComparatorTestCase` for snapshot regression.

### Class Relationships

```
OMFModelComparator   implements MagicDraw ModelComparator
  → compareElements(e1, e2)
      → ElementComparator
            → AttributeComparator      (UML attribute values)
            → ReferenceComparator      (UML reference targets)
            → TaggedValueComparator    (stereotype tagged values)
      → DiffManager                   (caches pair comparisons to avoid re-work)
      → FilterManager + ModelComparatorFilter  (exclude element types)
```

### `DiffKind` Enum

`IDENTICAL` · `MODIFIED` · `ADDED` · `REMOVED` · `UNMATCHED`

---

## 12. Builder System

Generic infrastructure for creating MagicDraw model elements inside sessions.

| Class | Role |
|---|---|
| `IGenericBuilder` / `AGenericBuilder` | Interface + abstract base for all builders |
| `BetaFactory` | Factory wrapping MagicDraw element-creation API (session-aware) |
| `InterfaceBlockBuilder` | Creates SysML Interface Blocks |
| `ConnectorBuilder` | Creates UML/SysML connectors |
| `FlowPropertyBuilder` | Creates SysML flow properties |
| `ProxyPortBuilder` | Creates SysML proxy ports |
| `PropertyBuilder` / `PortBuilder` | UML property/port creation |

All builders live in `omf-core-framework/…/builders/`.

---

## 13. Test Framework Internals

### Class Hierarchy

```
MagicDrawTestCase  (MagicDraw API)
  └── AbstractTestCase
        ├── AModelComparatorTestCase   — snapshot regression tests
        │     └── AModelComparatorTestCaseKO   — tests that expect a diff
        └── MDActionsBrowserMenuTestCase        — tests for action availability
```

### Batch Execution

```
BatchLauncher.launch(batch)
  → ATestBatch.run()
      → ATestBatchLocal    — local machine execution
      → ATestBatchTWC      — Team Work Cloud projects
```

### Test Project Naming Convention

| Pattern | Role |
|---|---|
| `init_*.mdzip` | Project loaded before test runs |
| `oracle_*.mdzip` | Expected project state compared on tearDown |

`AModelComparatorTestCase` automatically runs `OMFModelComparator.compareModels()` in `tearDown()` and fails the test if differences are found.

### Reporting

- `XMLJUnitResultFormatter` — JUnit XML output for CI pipelines
- `JUnitResultFormatterAsRunListener` — bridges JUnit 4's `RunListener` to the formatter

---

## 14. Gradle Plugin Internals

The `omf-gradle-plugin` is consumed via `includeBuild 'omf-gradle-plugin'` in `settings.gradle`.

### Plugin Extension

```groovy
// OmfGradlePluginBuildExtension fields
humanVersion, myPluginName, myPackage, myPluginId, myPluginMainClass
localDeliveryDirectory, pluginDeliveryName, testPluginDeliveryName
myTestPluginId, myTestPluginName, myTestPluginMainClass, myTestPackage
```

### Task Dependency Graph (simplified)

```
compileJava
  ↑
installMagicDraw          ← extracts mdApplicationArchive ZIP to build/install
installZippedMDPlugins    ← installs zippedMDPlugin ZIPs into MagicDraw
clean

installPlugin             ← copies compiled plugin into MD plugin directory
  ↑
runPlugin / debugPlugin   ← launches MagicDraw

installTestPlugin
  ↑
runTests / runTestsNoLog / debugTests

packagePlugin             ← PackagePlugin task: auto-generates plugin.xml + descriptors
  ↑
zipPluginLocally / deliverPluginLocally / publishToSamaresNexus
```

`PackagePlugin` reads `mdPluginBuild {}` extension values to generate `plugin.xml` and resource-manager descriptor files — no manual XML editing needed.

---

## 15. Private Features (smart-private)

### PatternCreationFeature

**Package:** `…smart_private.privatefeaturelibrary.patterncreation`

Discovers reusable model patterns from project dependencies — elements marked with a stereotype from `PatternProfile.mdzip` — and automatically instantiates them when matching elements are created.

**Mechanism:**
1. `AOnProjectOpenedHook.executeHook()` → `refreshPatternConfiguration()` scans project dependency tree for stereotyped `Dependency` elements using MagicDraw's Finder API → populates `Set<Stereotype> configuredSTR`
2. `ALiveActionEngine(CREATE)` + `ElementCreatorFromPattern` checks each newly created element against `configuredSTR` and applies the pattern if matched

**Key classes:**

| Class | Role |
|---|---|
| `PatternCreationFeature` | Feature entry point; wires hook + live action |
| `ElementCreatorFromPattern` | Live action that applies a pattern to a new element |
| `PatternCreatorProfile` | Manages the pattern-marking stereotype |
| `PatternCreationHelper` | Utility methods for pattern discovery and application |
| `NoPatternFoundOnTemplateElementException` | Thrown when a dependency lacks a valid pattern |

---

## 16. Key Source Paths

```
omf-core-framework/src/main/java/com/samares_engineering/omf/omf_core_framework/
  plugin/                     AOMFPlugin.java, OMFPlugin.java (interface)
  feature/                    AFeature.java, SimpleFeature.java, FeatureRegisterer.java
  feature/registrables/
    actions/                  UIAction.java, AUIAction.kt
    hooks/                    Hook.java, On{Project,MagicDraw,Feature}*.java
    liveactions/              LiveActionEngine.java, ALiveActionEngine.java, LiveActionType.kt
    options/                  Option.java, EnvOptionsHelper.java, OptionKind.java
  errormanagement2/           OMFBarrierExecutor.java, OMFErrorHandler.java
    exceptions/               OMFLogException.java, OMFCriticalException.java, OMFExceptionModifier.java
    logging/log/              OMFLog.kt, OMFLogLevel.java
  listeners/                  IListener.java, ListenerManager.java
  model_comparators/          OMFModelComparator.java, ElementComparator.java, DiffKind.java
  builders/                   AGenericBuilder.java, BetaFactory.java, sysml/*, uml/*

omf-test-framework/src/main/java/com/samares_engineering/omf/omf_test_framework/
  templates/                  AbstractTestCase.java, AModelComparatorTestCase.java
  templates/batches/          ATestBatch.java, ATestBatchLocal.java, ATestBatchTWC.java
  BatchLauncher.java

omf-public-features/src/main/java/com/samares_engineering/omf/omf_public_features/
  apiserver/                  APIServerFeature.java
  testGeneration/             TestGenerationFeature.java
  stereotypes/                StereotypesFeature.java
  clonefeature/               CloneElementFeature.java
  lockmanager/                LockSafeFeature.java
  featuredeactivation/        FeaturesDeactivationFeature.java
  activablefeatureoption/     FeatureActivationFromOptionFeature.java
  partblock_hyperttext/       HyperLinkFeature.java

smart-private/src/main/java/com/samares_engineering/omf/smart_private/
  privatefeaturelibrary/patterncreation/  PatternCreationFeature.java

omf-example-plugin/src/main/java/…/    OMFExamplePlugin.java + example feature packages
omf-gradle-plugin/src/main/groovy/…/   OmfGradlePlugin.groovy, PackagePlugin.groovy
```
