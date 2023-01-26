# Open MBSE Framework
This project contains a framework for the development of Magicdraw plugins, as well as an example plugin demonstrating the usage of the framework through
basic automations and use cases.

# Install guide

### Secret properties

To avoid sharing credentials & other sensitive properties, we store them outside of the project in the .gradle folder.

- Copy the contents of the *gradle.properties.secret* file into *\<User folder\>/.gradle/gradle.properties* (create the
  file if it does not exist)
- Fill properties with the correct values/credentials (ask an other dev for the info if needed)

### Running the project

- From command line :

> gradle debugJava

- From IntelliJ :

  - Open project as a gradle project
  - In the gradle tool window, right-click *Tasks/_dev/debugJava* and select the *debug* option 

# Authors

- ### Samares Engineering
  - Quentin CESPEDES
  - Clément MEZERETTE
  - Hugo STINSON