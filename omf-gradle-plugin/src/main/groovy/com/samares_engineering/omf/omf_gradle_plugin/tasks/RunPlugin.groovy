package com.samares_engineering.omf.omf_gradle_plugin.tasks;

import org.gradle.api.tasks.JavaExec;

abstract class RunPlugin extends JavaExec {
    RunPlugin() {
        classpath project.configurations.mdLibrary
        workingDir "$project.buildDir/install"
        mainClass = 'com.nomagic.magicdraw.Main'
        args 'DEVELOPER', '-verbose'
        jvmArgs = ["-Xmx4000M", "-Xss1024K", "-Dmd.development=true", "-Dmd.class.path=" + javaVersion]
    }
}
