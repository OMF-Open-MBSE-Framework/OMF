package com.samares_engineering.omf.omf_gradle_plugin.tasks

import org.gradle.api.GradleException
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService

abstract class RunPlugin extends JavaExec {
    RunPlugin() {
        classpath project.configurations.mdLibrary
        workingDir "$project.buildDir/install"
        mainClass = 'com.nomagic.magicdraw.Main'
        args 'DEVELOPER', '-verbose'
        javaLauncher.set(
                project.extensions.getByType(JavaToolchainService).launcherFor {
                    def jvmVersion = project.findProperty('jvmVersion')
                    if (jvmVersion == null) {
                        throw new GradleException("You need to specify the 'jvmVersion' property in your gradle.properties file")
                    }
                    it.languageVersion.set(JavaLanguageVersion.of(jvmVersion))
                }
        )
        jvmArgs = [
                "-XX:+IgnoreUnrecognizedVMOptions",
                "-Xmx4000M",
                "-Xss1024K",
                "-DLOCALCONFIG=true",
                "-splash:data/splash.png",
                "-Desi.system.config=data/application.conf",
                "-Djdk.attach.allowAttachSelf=true",
                "-Dsun.locale.formatasdefault=true",
                "-XX:-OmitStackTraceInFastThrow",
                "--illegal-access=permit",
                "-Dfile.encoding=UTF-8",
//                "@bin/vm.options",
                "-Djdk.util.zip.disableZip64ExtraFieldValidation=true",
                "-Dmd.development=true",
                "-Dmd.class.path=" + javaVersion
        ]
    }
}
