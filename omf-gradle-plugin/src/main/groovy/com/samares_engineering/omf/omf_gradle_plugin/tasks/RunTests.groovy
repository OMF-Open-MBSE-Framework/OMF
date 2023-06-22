package com.samares_engineering.omf.omf_gradle_plugin.tasks;

import org.gradle.api.tasks.JavaExec;

abstract class RunTests extends JavaExec {
    // Args for jvm (used with commandLineActionLauncher from CAMEO)
    def testArgs = [
            '-Desi.system.config=data/application.conf',
            '-Dcom.nomagic.magicdraw.commandline.action=' + testLauncher,
            '-Dtests.resources=' + resourcesDir
    ]

    def twcArgs = [
            "-DserverIp=$ipServer",
            "-DuserName=$twcUsername",
            "-DuserPwd=$twcPassword",
            "-Dtest=true"
    ]

    RunTests() {
        classpath project.configurations.mdLibrary
        workingDir "$project.buildDir/install"
        mainClass = 'com.nomagic.magicdraw.commandline.CommandLineActionLauncher'
        jvmArgs += testArgs
        jvmArgs += twcArgs
    }
}
