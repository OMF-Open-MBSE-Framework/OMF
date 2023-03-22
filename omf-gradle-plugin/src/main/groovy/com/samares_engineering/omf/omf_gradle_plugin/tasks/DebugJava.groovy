package com.samares_engineering.omf.omf_gradle_plugin.tasks;

import org.gradle.api.tasks.JavaExec;

class DebugJava extends JavaExec {
    DebugJava() {
        classpath project.configurations.cameoDependencies

        workingDir "$project.buildDir/install"
        setIgnoreExitValue false

        standardOutput = System.out
        errorOutput = System.err

        mainClass = 'com.nomagic.magicdraw.Main'

        args 'DEVELOPER', '-verbose'
        group = "_dev"
        jvmArgs = ["-Xmx4000M", "-Xss1024K", "-Dmd.development=true", "-Dmd.class.path=" + javaVersion]
    }
}
