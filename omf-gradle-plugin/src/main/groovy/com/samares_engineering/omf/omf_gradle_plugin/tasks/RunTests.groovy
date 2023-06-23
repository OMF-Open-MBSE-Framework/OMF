package com.samares_engineering.omf.omf_gradle_plugin.tasks;

import org.gradle.api.tasks.JavaExec;

abstract class RunTests extends JavaExec {
    RunTests() {
        dependsOn 'installTestPlugin'

        classpath project.configurations.mdLibrary
        workingDir "$project.buildDir/install"
        mainClass = 'com.nomagic.magicdraw.commandline.CommandLineActionLauncher'

        // Test args
        jvmArgs += [
                "-Desi.system.config=data/application.conf",
                "-Dcom.nomagic.magicdraw.commandline.action=$project.properties.testLauncher",
                "-Dtests.resources=${System.getProperty("user.dir")}\\..\\..\\src\\test\\resources\\projects"
        ]
        // TWC args
        jvmArgs += [
                "-DserverIp=$project.properties.ipServer",
                "-DuserName=$project.properties.twcUsername",
                "-DuserPwd=$project.properties.twcPassword",
                "-Dtest=true"
        ]
    }
}
