# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [UNRELEASED]
### Added
- OMFDiagramConfigurator is now available for any diagram type
- AutoLayout Utility: Allows to apply the auto-layout on a diagram
- Parametric Elements available in Factory
- ElementAction add a new method to open specification dialog for a given element
- API Server: Add a new method to open the specification dialog for a given element
- TWC Utils: Adding new helper to deal with TWC, such as checking TWC accessibility, commits, etc.
- Limitation to the frequency of notifications 

Examples
- Parametric Diagram Generation example

### Changed
### Fixed

## [2.1.1] 2024-09-08
### Added
- UMLUtils: 
  - DerivedProperty: Allows getting the value of a derived property from an element or stereotype instance
  - MetaClass: Allows getting the metaclass of an element or stereotype

### Changed
### Fixed
- CloneManager: 
  - Fixed an issue where the clone manager would not clone the connectors anymore
  - Fixed the limitation of 93 max elements to clone

## [2.0.1] 2024-09-08
### Added
- display expanded text to ui console as well
- new notification method in OMFLog allowing to expand extra text
### Changed
### Fixed

## [2.0.0] - 2024-09-07
### Added
- IntelliJ install info in README
- Error management documentation in README
- Several new hooks

### Changed
- Feature hooks are now generic feature items
- Error management & logging 2.0 
- OMF Comparator was moved from omf-test-framework to omf-core-framework, and dependencies from omf-test were removed
- Revert to using the legacy comparator/test logger in the OMF Test Framework as the new one was causing issues
- Renamed a number of classes to follow OMF Conventions
- Fully renamed Rules to LiveActions
- Seperated normal vs test plugin classes : extend AOMFPlugin for normal plugin & AOMFTestPlugin for test plugins
- Use "AOMFPlugin.initPlugin()" rather than "AOMFPlugin.init()" now, and don't call super.init()

### Fixed
- Cleaned some legacy code & unused resources

## [1.2.0] - 2024-01-17
### Added
- OMFTestFramework : New class to deal with KO Test, AModelComparatorTestCaseKO
- Add 6 KO tests
- 2 new function to get test elements by name and human name in AbtractTestCase

### Changed
- OMFTestFramework : New comparator logger for OMF Test Framework, not nested anymore but taking the comparator output.

### Fixed
- If Stereotype instance file as empty lines, it will not crash anymore
- Stereotypes Test T1-T5 pass now. With new subcategories feature, StereotypeFeature path to trigger changed


## [1.1.0] - 2023-11-03
### Removed
- The concept of project only features due to limitations with open API rendering the concept unworkable.

### Added
- A delimiter option for the Stereotypes feature config files (defaults to `;`).
- A filter for the clone manager

### Fixed
- Added better error management for FeatureRegisterer.
- Error while right-clicking on non element object was returning a list of null elements
- Null point exception when right clicking in empty space in browser when using the example plugin.
- Fix issues with registering/unregistering menu actions.

### Changed
- OMF actions moved to "OMF/OMF Test" parent category to tidy up the example plugin

## [1.0.60] - 2023-10-05
### Added
- filter to clone manager allow to specify which elements should be concerned by the cloning


## [1.0.54] - 2023-09-27
### Added Public Features
- **[FeaturesDeactivationFeature.java](omf-public-features%2Fsrc%2Fmain%2Fjava%2Fcom%2Fsamares_engineering%2Fomf%2Fomf_public_features%2Ffeaturedeactivation%2FFeaturesDeactivationFeature)** Create an Environment Option Group to allow to deactivate features
### Added
- [OMFErrorHandler] Improving ErrorManagement:
  - CriticalFeatureException: Allows to throw an exception which will disable the feature


## [1.0.50] - 2023-09-08

### Added Public Features
### Added
- [OMFErrorHandler] Improving ErrorManagement: 
    - Take into account the ECriticality on the notification window
    - does not display anything if ECriticality is SILENT
-  [CloneManager] CloneManager allows to clone deeply elements within a project.
  - Allows to clone ports with interface, relations, and all connectors (including connectors from nested ports)
  - Allows to clone parts with their block, relations, and all connectors (including connectors from nested ports)
  - Allow
- [GroupManager] GroupManager allows to group elements within a project.
  - Allows to group ports inside a new one updating connectors (including connectors from nested ports)
  - Allows to group parts inside a new one updating connectors (including connectors from nested ports)

- [InternalDiagramManagement] 
  - Display Path method added: Exactly as the action available in MagicDraw diagram, it allows to display all connection path from presentation elements. 
     

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

