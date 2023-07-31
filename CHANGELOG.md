# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [UNRELEASED]
### Added
- "generate model architecture" feature which generates a UML model of the OMF project in Magicdraw
- Eclipse specific gradle tasks **debugPlugin** and **debugTests** to allow debugging on that IDE
- TestGeneration feature that add an action generates test case code for creation tests from a test case in the model
- EventChecker new method isInstanceOf allowing to check if an event is an instance of a given class and isTrue(customPredicate) to validate a custom predicate
- OMFSilentException has been added to allow to throw an exception without displaying anything on User Interface (no popup, no console)
- 
### Fixed
- Error messages relating to the API server feature
- Example test plugin was missing an option group
- OMFErrorHandler will no more display user messages when it's blank or empty

### Changed
- Gradle build configuration and project properties changed to allow development in environments with 
limited internet access and on the eclipse IDA
- To declare a environment option group in a plugin, you now only have to declare an instance of OMFOptionsGroup instead
of having to create a class that implements an abstract class from OMF.
- Framework now better handles case when no option group has been declared in a plugin.
- A number of gradle tasks where renamed and reorganized.
- 

### Removed
- The **mavenGroupId** and **testLauncher** gradle properties.
- ARule debug method is now deprecated and will be removed in a future version

## [1.0.0]

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

