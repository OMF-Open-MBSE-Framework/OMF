package com.samares_engineering.omf.omf_gradle_plugin.tasks

import org.gradle.api.GradleException;
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService;

abstract class RunPlugin extends JavaExec {
    RunPlugin() {
        classpath project.configurations.mdLibrary
        workingDir "$project.buildDir/install"
        mainClass = 'com.nomagic.magicdraw.Main'
        args 'DEVELOPER', '-verbose'
        jvmArgs = ["-Xmx16000M", "-Xss1024K", "-Dmd.development=true", "-Dmd.class.path=" + javaVersion]
        javaLauncher.set(
                project.extensions.getByType(JavaToolchainService).launcherFor {
                    def jvmVersion = project.findProperty('jvmVersion')
                    if (jvmVersion == null) {
                        throw new GradleException("You need to specify the 'jvmVersion' property in your gradle.properties file")
                    }
                    it.languageVersion.set(JavaLanguageVersion.of(jvmVersion))
                }
        )
    }
}
