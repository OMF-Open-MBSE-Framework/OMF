package com.samares_engineering.omf.omf_gradle_plugin.tasks

import org.gradle.api.GradleException;
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService;

abstract class RunTests extends JavaExec {
    RunTests() {
        dependsOn 'installTestPlugin'

        classpath project.configurations.mdLibrary
        workingDir "$project.buildDir/install"
        mainClass = 'com.nomagic.magicdraw.commandline.CommandLineActionLauncher'

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
                "@bin/vm.options",
                "-Djdk.util.zip.disableZip64ExtraFieldValidation=true"
        ]
        // Test args
        jvmArgs += [
                "-Desi.system.config=data/application.conf",
                "-Dcom.nomagic.magicdraw.commandline.action=com.samares_engineering.omf.omf_test_framework.BatchLauncher",
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
