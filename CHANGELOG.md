# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
## [1.0.1] - 2023-05-02

### Added
- EventChecker adding hasStereotype clause

### Fixed


### Changed

### Known Limitations
- Documentation is not up-to-date with the latest changes.
- OMFExample is not published yet.

## [RELEASE]
## [1.0.0] - 2023-05-02

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

## [Unreleased]

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

