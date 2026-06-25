# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Open MBSE Framework (OMF) is a framework for developing MagicDraw/Cameo plugins. It provides:
- Feature registration (actions, menus, listener rules, environment options)
- Exception handling with rollback support
- Model comparison utilities
- Test framework for functional testing inside MagicDraw

See [README.md](README.md) for the developer guide (how to build features) and [ARCHITECTURE.md](ARCHITECTURE.md) for internal implementation details.

## Build Commands

This is a Gradle multi-project build. Run from project root:

```bash
# Run MagicDraw with the plugin installed
./gradlew runPlugin

# Run tests (launches MagicDraw headless, runs tests, opens HTML report)
./gradlew runTests

# Run tests without MagicDraw logs (faster)
./gradlew runTestsNoLog

# Debug plugin (Eclipse - connect remote debugger on port 5005)
./gradlew debugPlugin

# Debug tests (Eclipse)
./gradlew debugTests

# Build/package the plugin
./gradlew packagePlugin

# Clean and reinstall MagicDraw
./gradlew cleanMagicDraw

# Publish to Maven repositories
./gradlew publish
```

## Project Structure

```
open-mbse-framework/
├── omf-core-framework/    # Core framework: logging, error handling, listeners,
│                          # feature registration, plugin state management
├── omf-utils/             # Static helper classes, non-OMF specific utilities
├── omf-test-framework/    # Functional testing framework for MagicDraw plugins
├── omf-public-features/   # Open source features (API server, test generation, patterns)
├── omf-example-plugin/    # Example plugin demonstrating framework capabilities
├── omf-gradle-plugin/     # Custom Gradle plugin for building MD plugins
├── omf-magicdraw-dependencies/ # MagicDraw dependency stubs
└── smart-private/         # Private features
```

## Configuration Setup

1. Copy `gradle.properties.secret` to `~/.gradle/gradle.properties`
2. Fill in Maven repository credentials and other required values
3. For command-line Gradle: add `org.gradle.java.home=<path_to_jdk>` (use forward slashes)

## Key Concepts

### Features
Features are the main extension point. Extend `AFeature` or `SimpleFeature` to create plugin functionality. Features can register:
- UI Actions (browser, diagram, menu actions)
- Hooks (MagicDraw lifecycle, project lifecycle, feature lifecycle)
- Listeners

### Exception Handling
Three exception types with different behaviors:
- `OMFLogException` - Standard runtime exception with OMFLog message
- `OMFCriticalException` - Signals unrecoverable errors, supports modifiers:
  - `NO_ROLLBACK` - Don't cancel model changes
  - `DEACTIVATE_FEATURE` - Auto-disable the failing feature
  - `SILENT` - Don't show user message
  - `WARNING` - Show as warning instead of error
- Other `RuntimeException` - Default handling: log to user, print stacktrace, rollback session

### Gradle Plugin Dependency Configurations
When configuring `omf-example-plugin/build.gradle`:
- `mdApplicationArchive` - MagicDraw zip archive (use once)
- `mdLibrary` - Dependencies for MagicDraw classpath (lib folder jars)
- `pluginLibrary` - Plugin runtime dependencies
- `testPluginLibrary` - Test plugin runtime dependencies
- `zippedMDPlugin` - Plugin zips to auto-install in MagicDraw
- `otherMDPluginLibrary` - Dependencies of required plugins

## Naming Conventions

- **Interfaces**: No "I" prefix
- **Abstract classes**: Prefix with "A", don't use as types (use interface instead)
- **Public framework classes**: Prefix with "OMF"
- **Internal classes**: No prefix
- **Packages**: lowercase, underscores allowed for clarity

## Guidelines

- Use Koltin for all new code unless otherwise specified
- You should have access to the IntelliJ IDEA MCP server, if not, remind the user to activate it
- When unsure about the Magicdraw API (in order of priority):
  - look for examples in the existing code
  - check the doc/MAGICDRAW_SECRET_DOCS.md file
  - check the MagicDraw API documentation at https://docs.nomagic.com/DEVG/2026x
  - look at the decompiled MagicDraw classes in IntelliJ external libraries
- When you are sure about some obscure/undocumented MagicDraw aspect, update the doc/MAGICDRAW_SECRET_DOCS.md file

## Requirements

- JDK 17
- Kotlin 1.9.23
- A MagicDraw 2026x version (configurable via `mdVersionShort` in gradle.properties)
