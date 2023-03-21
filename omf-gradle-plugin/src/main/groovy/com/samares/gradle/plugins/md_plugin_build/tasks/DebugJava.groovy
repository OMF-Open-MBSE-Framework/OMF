package com.samares.gradle.plugins.md_plugin_build.tasks;

import org.gradle.api.tasks.JavaExec;

class DebugJava extends JavaExec {
    DebugJava() {
        classpath project.fileTree(dir: "$project.buildDir/install", include: ['lib/**/*.jar'])

        workingDir "$project.buildDir/install"
        setIgnoreExitValue false

        standardOutput = System.out
        errorOutput = System.err

        main = 'com.nomagic.magicdraw.Main'

        args 'DEVELOPER', '-verbose'
        group = "_dev"
        jvmArgs = ["-Xmx4000M", "-Xss1024K", "-Dmd.development=true", "-Dmd.class.path=" + javaVersion]
    }
}
