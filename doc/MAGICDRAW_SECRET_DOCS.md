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
