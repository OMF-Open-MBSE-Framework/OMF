## Usage

To use one of our custom plugins in your project, you need to :
- in *settings.gradle*, add:
```groovy
pluginManagement {
    repositories {
        maven {
            credentials {
              username nexusUsername
              password nexusPassword
            }
            url = "$nexusUrl/maven-snapshots"
            name 'Samares Nexus snapshots'
        }
    }
}
```
- In *build.gradle*, add:
```groovy
plugins {
    id 'com.samares.greeting' version '1.0-SNAPSHOT'
}
```