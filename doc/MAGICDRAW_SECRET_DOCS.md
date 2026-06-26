# MagicDraw Secret Docs

Undocumented or poorly documented MagicDraw API behaviors discovered through decompilation,
experimentation, and DEVG gaps. Each entry records *what* was found and *why* it matters,
so future implementers don't re-derive it.

---

## Properties (`com.nomagic.magicdraw.properties`)

### Complete property type inventory (2026x)

All concrete `Property` subclasses available in the public API:

| Class | Value type | UI widget |
|---|---|---|
| `BooleanProperty` | `Boolean` | Checkbox |
| `StringProperty` | `String` | Text field / text area (controlled by `isMultiline`) |
| `PasswordProperty` | `String` | Masked text field (extends `StringProperty`) |
| `NumberProperty` | `Int`, `Double`, `Float`, or `Long` (selected by numeric type) | Input box with format validation |
| `ColorProperty` | `java.awt.Color` | RGB color picker |
| `FontProperty` | `java.awt.Font` | Font name + style + size picker |
| `FileProperty` | `String` (path) | File-system browser (single path) |
| `ChoiceProperty` | `Any` (one of a provided list) | Dropdown / combo box |
| `ElementProperty` | `Element` (resolved from ID at runtime) | Model-element reference picker |
| `ElementListProperty` | `Array<Element>` (resolved from IDs at runtime) | Multi-element reference picker |
| `DateTimeProperty` | `String` | Date/time picker (extends `StringProperty`, requires a UML `Type` for full validation) |

The following classes exist in the package but should **not** be used in plugin options:

| Class | Reason |
|---|---|
| `ChoiceListProperty` | `@Deprecated @InternalApi` |
| `ElementFilterProperty` | Interface, `@InternalApi` (but methods are still used — see below) |
| `ImageProperty`, `MapProperty`, `ListProperty`, `ExternalToolProperty`, etc. | Internal/diagram-rendering use only; no stable constructor contract |

---

### `StringProperty` — multiline

```java
new StringProperty(String id, String defaultValue)              // multiline = false
new StringProperty(String id, String defaultValue, boolean multiline)
```

`multiline=true` renders a text area in the options dialog. For plugin options that accept
free-form text (paths, notes, expressions), `multiline=true` is the more useful default.

---

### `NumberProperty` — numeric type selection

The constructor overloads are the only way to select the numeric precision; there is no
setter for this after construction:

```java
new NumberProperty(String id, int defaultValue, double lowRange, double highRange)   // INT
new NumberProperty(String id, double defaultValue, double lowRange, double highRange) // DOUBLE
new NumberProperty(String id, float defaultValue, double lowRange, double highRange)  // FLOAT
new NumberProperty(String id, long defaultValue, double lowRange, double highRange)   // LONG
```

`getValue()` returns the typed value directly (`Integer`, `Double`, `Float`, or `Long`)
based on the numeric type set at construction. Use type-specific getters for clarity:
`getInteger()`, `getDouble()`, `getFloat()`, `getLong()`.

Pass `Double.NEGATIVE_INFINITY` / `Double.POSITIVE_INFINITY` for no range limit.

---

### `FileProperty` — extension filtering

Single-path only. There is no multi-file selection variant in the standard property API.

```java
new FileProperty(String id, String defaultPath)                         // FILES_ONLY
new FileProperty(String id, String defaultPath, int selectionMode)      // FILES_ONLY / DIRECTORIES_ONLY / FILES_AND_DIRECTORIES
```

Restrict to specific file extensions after construction:

```kotlin
FileProperty(id, "").also { it.fileExtensions = listOf("xml", "xmi") }
```

Selection mode constants: `FileProperty.FILES_ONLY = 0`, `DIRECTORIES_ONLY = 1`,
`FILES_AND_DIRECTORIES = 2`.

---

### `ElementProperty` and `ElementListProperty` — type filtering

Both classes implement `ElementFilterProperty`, which is `@InternalApi @Deprecated`. However,
the methods themselves are **public and functional on the concrete classes** in 2026x — the
deprecation is on the interface only. Safe to call directly:

```kotlin
// Single element, packages only
ElementProperty(id, null).also {
    it.setSelectableTypes(listOf(Package::class.java))
    it.setDisplayableTypes(listOf(Package::class.java))
}

// Element list: tree shows Packages + Classes, only Classes are selectable
ElementListProperty(id, null).also {
    it.setDisplayableTypes(listOf(Package::class.java, Class::class.java))
    it.setSelectableTypes(listOf(Class::class.java))
}
```

`setDisplayableTypes` controls which element types appear in the browser tree (useful to show
parent packages for navigation without making them selectable).
`setSelectableTypes` restricts which types the user can actually pick.
`setSelectableRestrictedElements` restricts to a specific set of element *instances* rather than types.

The collections take `java.lang.Class<?>` objects (Java metaclasses), **not** EMF `EClass` instances.

Values are stored internally as element IDs (strings), resolved back to `Element` references
via the active project at `getValue()` time. An `ElementProperty` whose project is closed
returns `null` from `getValue()`.

---

### `ChoiceProperty` — construction

```java
new ChoiceProperty(String id, Object defaultValue, List<Object> choices)
```

`defaultValue` must be reference-equal to one of the items in `choices`, otherwise
`getIndex()` returns `-1`. The choice list can contain any `Object`; `toString()` is used
for display.

`setAppendValue(true)` lets users type a free-text value that gets prepended to the list.

---

### Options registration: Environment vs. Project

The `OptionKind` determines **where** the property ends up; both kinds use the same
`OptionImpl(property, groupName, group, kind)` constructor where `group` is the plugin's
`OMFPropertyOptionsGroup`.

| Kind | Registered in | How to read back from code |
|---|---|---|
| `Environment` | `OMFPropertyOptionsGroup` (added via `addProperty`) | `EnvOptionsHelper.getPropertyByName(name)` |
| `Project` | `ProjectOptions.PROJECT_GENERAL_PROPERTIES` (via `FeatureProjectOptionsConfigurator`) | `OptionsHelper.getProjectOptionByCategoryName(ProjectOptions.PROJECT_GENERAL_PROPERTIES, name)` |

**Critical**: `EnvOptionsHelper.getPropertyByName` does **not** work for project options.
Project options are never added to the `OMFPropertyOptionsGroup`, so `getProperty(name)`
returns null and the helper swallows the error. Always use `OptionsHelper` for project options.

Project options are applied to new projects via `FeatureProjectOptionsConfigurator.configure`,
which is called by MagicDraw's `ProjectOptionsConfigurator` machinery on every project open.
If no project is open when `register()` is called, the property is queued and applied on the
next open.

---

## Actions API (`com.nomagic.magicdraw.actions`, `com.nomagic.actions`)

### Visual separators — `ActionsCategory.createSeparatorCategory()`

The only confirmed working mechanism for inserting a visual separator in MagicDraw menus (verified at runtime):

```kotlin
// In a low-priority AMConfigurator / BrowserContextAMConfigurator that runs AFTER OMF's:
val sep = ActionsCategory.createSeparatorCategory()
category.addAction(sep, indexBeforeTargetAction)
```

`createSeparatorCategory()` creates a plain `ActionsCategory` with
`putValue("useAsSeparatorInUI", Boolean.TRUE)`. When the rendering engine encounters this
entry in a category's action list, it draws a horizontal separator line.

**What does NOT work:**
- `NMAction.BELONGS_TO_SEPARATE_GROUP_IN_UI` — the constant exists (`= "belongsToSeparateGroupInUI"`)
  but setting it on an action does NOT produce a visual separator.
- The `group` constructor parameter of `MDAction`/`DefaultBrowserAction` — logical grouping only,
  no visual effect.
- `MDActionsCategory.addSeparatorBefore/After(nmAction)` — these methods do NOT exist.

**Timing requirement:** the separator must be inserted AFTER the target actions are in the
category. Register a second `AMConfigurator` with `LOW_PRIORITY` that runs after OMF's
`MEDIUM_PRIORITY` configurator:

```kotlin
// From feature.onRegistering():
val sep = ShowcaseSeparatorConfigurator()   // implements AMConfigurator + BrowserContextAMConfigurator
ActionsConfiguratorsManager.getInstance().addContainmentBrowserContextConfigurator(sep)
ActionsConfiguratorsManager.getInstance().addMainMenuConfigurator(sep)
```

```kotlin
// ShowcaseSeparatorConfigurator
override fun getPriority() = AMConfigurator.LOW_PRIORITY

private fun insertSeparators(actionsManager: ActionsManager) {
    val cat = actionsManager.getCategories().filterIsInstance<MDActionsCategory>()
        .firstOrNull { it.name == "OMFShowcase" } ?: return
    val idx = cat.getActions().indexOfFirst { it.getValue(Action.NAME) == "target action name" }
    if (idx >= 0) cat.addAction(ActionsCategory.createSeparatorCategory(), idx)
}
```

Example: `ShowcaseSeparatorConfigurator.kt` + `ActionShowcaseFeature.kt`.

---

### MDAction constructor — the `group` parameter (NOT for visual separators)

```java
new MDAction(String id, String name, KeyStroke keyStroke, String group)
```

**The `group` parameter does NOT control visual separators.** It is stored via
`setGroup(String)` / `getValue(NMAction.GROUP)` (`NMAction.GROUP = "group"`) and is used for
logical grouping (e.g. keyboard shortcut conflict resolution), not for menu layout.

`NMAction.getGroup()` is **public** — readable from plugin code. `NMAction.setGroup(String)` is
**protected** — only settable via the constructor's group parameter (or from a subclass).

The correct way to add visual separators is `ActionsCategory.createSeparatorCategory()` (see above).

---

### NMAction / MDAction — icon, mnemonic, and other property-map constants

**Icon** — `NMAction.setSmallIcon(Icon)` is a **public** method. Use Kotlin property syntax:

```kotlin
menuNMAction?.smallIcon = ImageIcon(javaClass.getResource("/icons/my.png"))
```

Using `putValue(Action.SMALL_ICON, ...)` does NOT work; MagicDraw reads `NMAction.smallIcon`.

**Mnemonic** — `NMAction.setMnemonicKey(int)` is **public** (different from the package-private
`setMnemonic(int)` that decompilers may show). Use it directly:

```kotlin
menuNMAction?.setMnemonicKey(KeyEvent.VK_I)
```

To trigger: open the menu by clicking, then press the mnemonic letter **alone** (no Alt).

**Other public NMAction constants** (all `putValue` / `getValue` keys):

| Constant | Value string | Purpose |
|---|---|---|
| `NMAction.BELONGS_TO_SEPARATE_GROUP_IN_UI` | `"belongsToSeparateGroupInUI"` | Logical group flag — does NOT produce a visual separator (see separator section above) |
| `NMAction.GROUP` | `"group"` | Logical group name (not visual) |
| `NMAction.DO_NO_SHOW_ACTION_NAME_IN_UI` | `"doNotShowActionNameInUI"` | Hide action name label |
| `NMAction.DO_NOT_REGISTER_ACTION_TO_COMPONENTS` | `"doNotRegisterActionToComponents"` | Skip component registration |
| `NMAction.ACTION_SHORTCUTS` | `"commandKeys"` | Shortcut key list |
| `NMAction.ID` | `"id"` | Action identifier |
| `NMAction.LARGE_ICON` | `"largeIcon"` | Large icon (toolbar) |
| `NMAction.TINY_ICON` | `"tinyIcon"` | Tiny icon |

**Neither icon nor mnemonic is exposed via OMF annotations.** Override `initMenuActions()`
and configure `menuNMAction` after `super.initMenuActions()`.

---

### `Application.getActionsManager()` — wrapper vs. the real ActionsManager

`Application.getInstance().getActionsManager()` returns
**`com.nomagic.magicdraw.actions.ActionsManager`** — a thin wrapper class, NOT the base
`com.nomagic.actions.ActionsManager` that has `getCategories()`.

The wrapper exposes:
- `getGeneralActionsManager()` → `com.nomagic.actions.ActionsManager` — the real main-menu
  categories tree; has `getCategories()`, `getActionFor(KeyStroke)`, `getAllActions()`.
- `getDiagramActionsManager()` → `DiagramsActionsManager` — diagram contexts
- `getActionsExecuter()` → `ActionsExecuter` — used for things like `layout(true)` (see `LayoutManager.java`)

**Both `getActionsManager()` and `getGeneralActionsManager()` are `@Deprecated`** in 2026x but
no public replacement has been documented. Use with `@Suppress("DEPRECATION")` until a
replacement appears.

Example: `ShowcaseExploreGroupsAction.kt` — logs the full main-menu tree at runtime and checks
whether `Ctrl+Shift+F11` is registered via `getActionFor(KeyStroke)`.

### Configure-time AM vs. general AM — OMF actions are NOT in the general AM

When `OMFMainMenuConfigurator.configure(actionsManager)` runs at startup, it adds plugin
categories and actions to the `actionsManager` argument passed by MagicDraw. This is the
**configure-time AM** — an internal object used during menu construction, separate from the
general AM returned by `getGeneralActionsManager()`.

**Consequence**: `Application.getInstance().getActionsManager().getGeneralActionsManager().getCategories()`
shows OMF plugin categories (e.g. `OMFShowcase`) as empty categories with no actions. The
actions are only visible in the configure-time AM during the `configure()` call window.

This means `getGeneralActionsManager()` is NOT a reliable way to inspect or modify OMF-registered
actions at runtime. To insert separators or modify plugin category contents, register a second
`AMConfigurator` with `LOW_PRIORITY` — it receives the same configure-time AM as OMF's
configurator, with all actions already added.

---

### Keyboard shortcuts — how MagicDraw fires them

MagicDraw does **NOT** use the standard Swing `Action.ACCELERATOR_KEY` property map entry for
keyboard shortcuts. Calling `putValue(ACCELERATOR_KEY, keyStroke)` has no effect on menu shortcut
display or firing. The `"AcceleratorKey"` string is absent from all MagicDraw JARs.

Instead, MagicDraw stores the keystroke in NMAction's own private field (passed via the `MDAction`
constructor's third parameter) and fires the action via its own shortcut dispatch:

**Main menu shortcuts** — the keystroke in `MDAction` is registered when the main menu configurator
runs at startup (`OMFMainMenuConfigurator.configure(actionsManager)`). The shortcut fires globally
regardless of focus.

**Browser shortcuts** — fired when a keyboard shortcut is pressed while the containment tree has
focus. The single-arg `OMFBrowserConfigurator.configure(actionsManager)` is the shortcut path.
This method was previously commented out; it now registers all available browser actions so
MagicDraw can match the keystroke. If the method body is empty, browser shortcuts silently do nothing.

**Diagram shortcuts** — same pattern. `OMFDiagramConfigurator.configure(actionsManager)` (single-arg)
is the shortcut path. Was previously commented out; now registers diagram actions for keystroke matching.

**Shortcut display in menus** — the keystroke label shown next to an action name in a context menu
(e.g. "Ctrl+Shift+F11") is rendered by MagicDraw from NMAction's internal keystroke field.
It does NOT come from `ACCELERATOR_KEY`.

---

### `@MDAction.keyStroke` — correct format

The `keyStroke` field takes a `String[]` that is joined with `"->"` before being passed to
`javax.swing.KeyStroke.getKeyStroke(String)`. That API expects a **space-separated** string
of modifiers followed by the key name (`"ctrl shift F11"`), not `"->"` separators.

**Consequence**: passing multiple elements like `["ctrl", "shift", "F11"]` produces
`"ctrl->shift->F11"`, which `KeyStroke.getKeyStroke()` cannot parse and returns **null** —
no shortcut is registered.

**Correct format**: always use a **single string** element:

```kotlin
keyStroke = ["ctrl shift F11"]   // Ctrl+Shift+F11 ✓
keyStroke = ["ctrl A"]           // Ctrl+A ✓
keyStroke = []                   // no shortcut (default)

// WRONG — produces "ctrl->shift->F11" → null:
keyStroke = ["ctrl", "shift", "F11"]
```

---

### OMF annotation override fields — NOT implemented

`@BrowserAction`, `@DiagramAction`, and `@MenuAction` all declare optional override fields:

```java
@BrowserAction(actionName = "...", category = "...", keyStroke = {"ctrl", "A"})
```

**These fields are never read by the OMF framework.** `AUIAction.getName()` and
`getCategory()` always read from `@MDAction` only. The context-specific annotations
exist in the source but serve no functional purpose today. All three context NMAction objects
receive the same name, category, and keyStroke from `@MDAction`.

---

### Diagram context — the `requestor` PresentationElement

`DiagramContextAMConfigurator.configure()` full signature:

```java
void configure(ActionsManager am,
               DiagramPresentationElement owner,
               PresentationElement[] selected,
               PresentationElement requestor)
```

- `selected[]` — the full current selection on the canvas.
- `requestor` — the **specific PE that was right-clicked**, which may not be in `selected`
  (e.g., user right-clicks a non-selected shape while other shapes are selected).

**OMFDiagramConfigurator discards the requestor.** Actions receive only the selected elements.
To access the requestor: subclass `OMFDiagramConfigurator`, override `configure()`, store the
requestor in a `ThreadLocal<PresentationElement>`, then read it from the action body.

---

### Browser context — the `Tree` object

`BrowserContextAMConfigurator.configure()` full signature:

```java
void configure(ActionsManager am, Tree tree)
```

`Tree` exposes the full containment tree state: selection, expanded nodes, scroll position.
**OMFBrowserConfigurator discards the Tree.** `AUIAction.getSelectedBrowserNodes()` recovers
the selection via `project.browser.containmentTree.selectedNodes`, so selection is still
accessible. To access other tree state (e.g., which nodes are expanded), subclass
`OMFBrowserConfigurator` and store the Tree in a ThreadLocal or pass it to the action.
