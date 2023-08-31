# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [UNRELEASED]
### Added Public Features
### Added
- [OMFErrorHandler] Improving ErrorManagement: 
    - Take into account the ECriticality on the notification window
    - does not display anything if ECriticality is SILENT
-  [CloneManager - BETA] CloneManager allows to clone deeply elements within a project.
  - Allows to clone ports with interface, relations, and all connectors (including connectors from nested ports)
  - Allows to clone parts with their block, relations, and all connectors (including connectors from nested ports)
  - LIMITATIONS (2021 R1): when cloning nested ports with connections in multi-instance context (nested ports from the same interface),
    nested ports with depth > 2 are cloned, but not displayable. Fix in MCSE 2021 R2.
     

### Fixed
### Changed

## [1.0.25] - 2023-08-17

### Added Public Features
- **Pattern** feature: Allows to define pattern in project and to generate it on its creation
- **TestGeneration** feature that adds an action generates test case code for creation tests from a test case in the model
- **GenerateModelArchitecture** feature which generates a UML model of the OMF project in Magicdraw

### Added
- Eclipse specific gradle tasks **debugPlugin** and **debugTests** to allow debugging on that IDE
- new method isInstanceOf in EventChecker allowing to check if an event is an instance of a given class and isTrue(customPredicate) 
to validate a custom predicate
- OMFSilentException has been added to allow to throw an exception without displaying anything on User Interface (no popup, no console)
- Handling of subcategories for UIActions by using "." as a separator in the action name
- Naming conventions to README.md

### Fixed
- Error messages relating to the API server feature
- Example test plugin was missing an option group
- Deliver plugin task was not working properly
- OMFErrorHandler will no more display user messages when it's blank or empty
- OMFFeatureException one missing constructor has been added to differentiate user message and developer message
- RuleEngine Rule field was not set properly when adding a rule
- AMagicDrawFactory allow to set Null Project but will throw an exception if you try to get the MagicDrawFactory

### Changed
- Gradle build configuration and project properties changed to allow development in environments with 
limited internet access and on the eclipse IDA
- A number of gradle tasks where renamed and reorganized.
- To declare a environment option group in a plugin, you now only have to declare an instance of OMFOptionsGroup instead
  of having to create a class that implements an abstract class from OMF.
- Framework now better handles case when no option group has been declared in a plugin.

### Removed
- The **mavenGroupId** and **testLauncher** gradle properties.
- ARule debug method is now deprecated and will be removed in a future version

## [1.0.0] - 2023-05-11

### Added
- Utils: EventChecker adding hasStereotype clause
- FeatureLibrary: SafeLock is now a Configurable Feature
- FeatureLibrary: FeatureActivationDeactivation is now a Configurable Feature
- Core: Listeners Interface has been refactor to improve their activation and deactivation
- Core: Core listeners can be now registered independently from FeatureListeners
- Core: ErrorHandling is now displaying a popup when an error occurs
- Core: More ErrorHandling has been added
- Core: Error Management handling inside OMF Core, allowing rollback in case of errors.
  - OMFError Handler throws a OMFRollbackException to trigger the rollback inside OMF Core.
  - Rollback can be triggered from LiveAction or UIActions.
- Core: FeatureRegisterer now uses a list of FeatureItemRegisterer allowing developer to customize how features registering are handling. 
 It also allows developer to add their own featureItems.
  
### Known Limitations
- Documentation is not up-to-date with the latest changes.
- OMFExample is not published yet.
- Popup Error needs to be improved, to be able to display more information (Exception tags, etc.)
- Exception tags are not yet implemented
- MainMenuAction: provided selectedElements come from both diagram and browser. Documentation need to be clarified on this point.

### Added
- Error Management handling inside OMF Core, allowing rollback in case of errors.
  - OMFError Handler throws a OMFRollbackException to trigger the rollback inside OMF Core.
  - Rollback can be triggered from LiveAction or UIActions.

### Fixed
- UIAction Menu could some time not be available in the menu bar.

### Changed
- FeatureRegisterer now uses a list of FeatureItemRegisterer allowing developer to customize how features registering are handling. 
 It also allows developer to add their own featureItems. 

### Known Limitations
- Documentation is not up-to-date with the latest changes.
- OMFExample is not published yet.


## [0.0.92] - 2023-04-2

### Added
- This changelog file
- API server feature & functional tests
- HyperText feature

### Fixed
- Activate all features environment option which wasn't working at all

### Changed
- Rename debugJava task to 'runPlugin'
- Change runPlugin to only use MagicDraw libs in classpath to better mimic the production environment
- Rename dependency configuration names to make them more explicit
- Rename omf gradle plugin classes to be in line with other omf projects
- Update Gradle wrapper to 7.5.1

## [0.0.83] - 2023-03-02

### Fixed
- Wrong group for dependencies in generated pom.xml
- Fix broken test batch launcher

### Added
- Capability to publish to Maven Central (artefact signing, javadoc/sources generation, custom pom generation)
- README.MD

### Changed
- Rename java packages to be in line with our maven central namespace
- Consolidate OMF as well as the gradle plugin into a single repository (this one)

