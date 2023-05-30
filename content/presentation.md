---
title: "Open MBSE Framework"
date: 2023-03-24T11:10:59+01:00
draft: false
---

# Presentation
OMF (Open MBSE Framework) is an open-source project, whose goal is to facilitate the development of plugins for Cameo System Modeler / Magic Cyber System. The framework integrates both mechanics to accelerate the development of features, automations, business aids and a set of tools ranging from error management to automatic testing. As such, and to demonstrate the capabilities of the framework through basic automations and use cases, you can find an example plugin attached. You can use it as library and import the jar to take advantage of the developed automations ; you can also clone the project (source code), or even participate.


## The Modules
This part will focus on the use of OMF modules as a library.  

### 1. OMF Core
This library contains the common functionalities of the plugin (minus static helpers which are in OMF Utils) such as : 
- Log management 
- Error management 
- Listeners 
- Project listener 
- Feature registration (actions, menus, listener rules, environment options...) 
- Feature toggling 
- Plugin state (stored data e.g. current project) 
	
### 2. OMF Public Features
A collection of open source features that can be integrated to third-party plugins. A feature meets a business need and must be, as such, independent and activatable / deactivable. For example: update a name, move elements created in a package, etc. 

### 3. OMF Test Framework
A framework for developing functional tests for MagicDraw plugins. Brings an abstraction layer to MagicDraw and allows the creation of tests locally and/or with Teamwork Cloud. Allows the use of test projects/models to validate that automations are working properly and ensure there is no code regressions. 

### 4. OMF Utils
A collection of utilities methods used in the framework as well as helpers for the MagicDraw public API that can be useful for plugin regardless of whether they use the framework or not. It contains all non-omf specific helper classes and functions. 

### 5. OMF Example Plugin
It is not a library but an example plugin demonstrating the use of the framework. You can still install it as a demo but no need to implement it in another plugin. It is a plugin template integrating examples of features already implemented.
 
 
## Installation
### 1. As a Java archive (JAR)
You can download the jars of the differents modules from the Maven Repository at the following adress [Samares Engineering OMF](https://mvnrepository.com/artifact/com.samares-engineering.omf.2021x_R2)

### 2. As a Cameo System Modeler Plugin
The plugin can be installed on Cameo System Modeler - *CSM* - and Magic Cyber System Engineer - *MCSE* - for the versions higher than 2019.
To install the example plugin in *CSM* or *MCSE*, click on Help -> Resource/Plugin Manager -> Import

### 3. If you want to participate
You can send an email to Quentin : *quentin.cespedes@samares-engineering.com*

### 4. User guide
A user guide is avalaible [here]({{< ref "/documentation/userguide" >}})



## Authors
### Samares Engineering
  - Quentin CESPEDES
  - Clément MEZERETTE
  - Hugo STINSON
