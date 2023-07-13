package com.samares_engineering.omf.omf_gradle_plugin

import com.samares_engineering.omf.omf_gradle_plugin.tasks.PackagePlugin
import com.samares_engineering.omf.omf_gradle_plugin.tasks.RunPlugin
import com.samares_engineering.omf.omf_gradle_plugin.tasks.RunTests
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.compile.JavaCompile

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OmfGradlePlugin implements Plugin<Project> {
    OmfGradlePluginBuildExtension mdPluginBuild;

    void apply(Project project) {
        mdPluginBuild = project.extensions.create('mdPluginBuild', OmfGradlePluginBuildExtension)

        /*
          Declare project properties
         */
        project.ext.isRelease = !project.version.endsWith("-SNAPSHOT")
        project.ext.buildTimestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm").format(LocalDateTime.now())
        project.ext.pluginPackageFolderName = 'packaged-plugin'
        project.ext.testPluginPackageFolderName = 'packaged-test-plugin'


        /*
         Declare dependency configurations. Configurations are dependency 'categories' that are used to separate
         dependencies that are used for different purposes
         */

        project.configurations {
            mdApplicationArchive.extendsFrom(implementation)
            testImplementation.extendsFrom(testPluginLibrary)
            implementation.extendsFrom(pluginLibrary)
            implementation.extendsFrom(mdLibrary)
            zippedMDPlugin
            compileOnly.extendsFrom(otherMDPluginLibrary)
        }

        /*
         Declare plugin usage
         */

        project.getPlugins().apply('java')

        /*
        Register tasks
         */

        registerTasks(project)

        /*
        Configure (mainly third party) tasks
         */

        project.tasks.withType(JavaExec).configureEach {
            classpath = project.configurations.mdLibrary
            workingDir 'build/install'
            args 'TESTER'
        }

        // TODO: Check if needed (might be default)
        project.tasks.withType(JavaCompile).configureEach {
            options.encoding = 'UTF-8'
        }

        // Customize clean task to not remove all the files from build dir
        project.afterEvaluate {
            project.tasks.clean {
                group = '_dev'
                // Don't delete build dir and sub Dir
                delete = []
                // Delete those specific dir :
                delete 'build/classes', 'build/distributions', 'build/generated',
                        "build/${project.pluginPackageFolderName}", 'build/libs',
                        'build/reports', "build/${project.testPluginPackageFolderName}",
                        'build/tmp', 'build/test-reports', 'build/resources'
            }
        }

        /*
        Add dependencies to third party tasks
         */

        project.tasks.compileJava.dependsOn 'installMagicDraw', 'installZippedMDPlugins', 'clean'

        // Publish tasks are generated with custom names starting with "publish" by the maven-publish plugin
        project.tasks.configureEach {
            if (name.startsWith 'publish') {
                dependsOn 'zipPluginLocally'
            }

            if (name.equals 'publish') {
                group = '_delivery'
            }
        }

    }

    private void registerTasks(Project project) {
        registerInstallZippedMDPluginsTask(project)
        registerCleanInstalledPluginsTask(project)
        registerInstallPluginTask(project)
        registerInstallTestPluginTask(project)
        registerRunPluginTask(project)
        registerDebugPluginTask(project)
        registerPackagePluginTask(project)
        registerPackageTestPluginTask(project)
        registerInstallMagicDrawTask(project)
        registerZipPluginLocallyTask(project)
        registerDeliverLocallyTask(project)
        registerZipTestPluginLocallyTask(project)
        registerCleanMagicDrawTask(project)
        registerRunTestsTask(project)
        registerDebugTestsTask(project)
        registerRunTestsNoLogTask(project)
        registerRetrieveModelTask(project)
        registerTestJarTask(project)
        registerSourceJarTask(project)
    }

    private void registerDeliverLocallyTask(Project project) {
        project.tasks.register('deliverLocally', Copy) {
            group = "_delivery"
            description = "Deliver the plugin to the local file system"
            dependsOn 'zipPluginLocally', 'zipTestPluginLocally'

            from "$project.buildDir/builtPlugin/$project.version/${mdPluginBuild.pluginDeliveryName.get()}.zip"
            from "$project.buildDir/builtPlugin/$project.version/${mdPluginBuild.testPluginDeliveryName.get()}.zip"
            into mdPluginBuild.localDeliveryDirectory.get()
            doLast {
                print "Plugin delivered to file:///${mdPluginBuild.localDeliveryDirectory.get()}"
            }
        }
    }

    private void registerZipPluginLocallyTask(Project project) {
        project.tasks.register('zipPluginLocally', Zip) {
            group = "_build"
            description = "Zip the packaged plugin"
            dependsOn 'packagePlugin'

            from "$project.buildDir/$project.pluginPackageFolderName"

            archiveFileName = "${mdPluginBuild.pluginDeliveryName.get()}.zip"
            destinationDirectory = project.file("$project.buildDir/builtPlugin/$project.version")
        }
    }

    private void registerZipTestPluginLocallyTask(Project project) {
        project.tasks.register('zipTestPluginLocally', Zip) {
            group = "_build"
            description = "Zip the packaged test plugin"
            dependsOn 'packageTestPlugin'

            from "$project.buildDir/${project.testPluginPackageFolderName}"

            archiveFileName = "${mdPluginBuild.testPluginDeliveryName.get()}.zip"
            destinationDirectory = project.file("$project.buildDir/builtPlugin/$project.version")
        }
    }

    private void registerInstallMagicDrawTask(Project project) {
        project.tasks.register('installMagicDraw') {
            group = "_install"
            description = "Install MagicDraw from the archive specified with the configuration 'mdApplicationArchive'" +
                    " into the build/install directory"
            dependsOn project.configurations.mdApplicationArchive

            def cameoConf = project.configurations.mdApplicationArchive
            def isAlreadyInstalled = new File("$project.buildDir/install").exists()
            doLast {
                if (cameoConf.isEmpty()) {
                    //throw new GradleException("Can't install Magicdraw as magicdraw dependency has not been configured")
                } else if (isAlreadyInstalled) {
                    print "Skipping installing Magicdraw as it is already installed"
                } else {
                    print "Installing Magicdraw"
                    project.copy {
                        from project.zipTree(cameoConf.first())
                        into "$project.buildDir/install"
                    }
                }
            }
        }
    }

    private void registerRunPluginTask(Project project) {
        project.tasks.register('runPlugin', RunPlugin) {
            group = "_dev"
            description = "Run MagicDraw with the plugin installed. If using IntelliJ you can also run in debug mode. If" +
                    " using Eclipse use the dedicated 'debugPlugin' task"
            dependsOn 'installPlugin', 'installTestPlugin'
        }
    }

    private void registerDebugPluginTask(Project project) {
        project.tasks.register('debugPlugin', RunPlugin) {
            group = "_dev_eclipse"
            description = "Run functional tests with verbose output in debug mode (needed to debug when using Eclipse)." +
                    " Connect with a remote debugger on port 5005 (default)"
            dependsOn 'installPlugin', 'installTestPlugin'

            jvmArgs += [
                    "-Xdebug",
                    "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=$project.properties.javaDebugPort"
            ]
        }
    }

    private void registerInstallTestPluginTask(Project project) {
        project.tasks.register('installTestPlugin') {
            group = "_install"
            description = "Installs the packaged test plugin into MagicDraw"
            dependsOn 'installPlugin', 'packageTestPlugin'

            // Fails on Jenkins if we don't do this for some reason
            doLast {
                project.copy {
                    setFileMode 0755
                    from "$project.buildDir/${project.testPluginPackageFolderName}"
                    into "$project.buildDir/install"
                }
            }
        }
    }

    private void registerInstallPluginTask(Project project) {
        project.tasks.register('installPlugin') {
            group = "_install"
            description = "Installs the packaged plugin into MagicDraw"
            dependsOn 'packagePlugin', 'cleanInstalledPlugins'

            // Fails on Jenkins if we don't do this for some reason
            doLast {
                project.copy {
                    setFileMode 0755
                    from "build/${project.pluginPackageFolderName}"
                    into "$project.buildDir/install"
                }
            }
        }
    }

    private void registerInstallZippedMDPluginsTask(Project project) {
        project.tasks.register('installZippedMDPlugins') {
            group = "_install"
            description = "Installs the plugins declared as dependencies using the 'zippedMDPlugin' configuration into" +
                    " MagicDraw"
            dependsOn project.configurations.zippedMDPlugin

            // Fails on Jenkins if we don't do this for some reason
            doLast {
                project.copy {
                    setFileMode 0755
                    from project.configurations.zippedMDPlugin.collect { project.zipTree(it) }
                    into "$project.buildDir/install"
                }
            }
        }
    }

    private void registerPackageTestPluginTask(Project project) {
        project.tasks.register('packageTestPlugin', PackagePlugin) {
            group = "_build"
            description = "Packages the test plugin into a zip file that can be installed into MagicDraw, containing the " +
                    "plugin, descriptors and other needed resources"

            dependsOn 'testJar', 'packagePlugin'

            humanVersion = mdPluginBuild.humanVersion
            pluginDeliveryName = mdPluginBuild.testPluginDeliveryName

            pluginPackageFolderName = project.testPluginPackageFolderName
            myPluginMainClass = mdPluginBuild.myTestPluginMainClass
            myPackage = mdPluginBuild.myTestPackage
            myPluginName = mdPluginBuild.myTestPluginName
            myPluginId = mdPluginBuild.myTestPluginId
            resolvedArtifacts = project.configurations.testPluginLibrary.resolvedConfiguration.resolvedArtifacts.file

            pluginUnderTestId = mdPluginBuild.myPluginId
            pluginUnderTestName = mdPluginBuild.myPluginName
        }
    }

    private void registerPackagePluginTask(Project project) {
        project.tasks.register('packagePlugin', PackagePlugin) {
            group = "_build"
            description = "Packages the plugin into a zip file that can be installed into MagicDraw, containing the " +
                    "plugin, descriptors and other needed resources"
            dependsOn "jar"

            humanVersion = mdPluginBuild.humanVersion
            pluginDeliveryName = mdPluginBuild.pluginDeliveryName

            pluginPackageFolderName = project.pluginPackageFolderName
            myPluginMainClass = mdPluginBuild.myPluginMainClass
            myPackage = mdPluginBuild.myPackage
            myPluginName = mdPluginBuild.myPluginName
            myPluginId = mdPluginBuild.myPluginId
            resolvedArtifacts = project.configurations.pluginLibrary.resolvedConfiguration.resolvedArtifacts.file
        }
    }

    // Task to delete plugins created before a new build.
    // Previously in custom task clean, it was blocking the "hot debug" mode
    private void registerCleanInstalledPluginsTask(Project project) {
        project.tasks.register('cleanInstalledPlugins', Delete) {
            group = "_dev"
            description = "Deletes the currently installed plugin and test plugin."
            // We need the packagePlugin task to run first so that we know the file structure of the plugin package in
            // order to delete it properly
            dependsOn "packagePlugin", "packageTestPlugin"

            doFirst {

                // TODO : Would be nice to also delete any "zippedMdPlugin" installed as well
                delete 'build/install/plugins/' + mdPluginBuild.myPackage.get(),
                        'build/install/plugins/' + mdPluginBuild.myTestPackage.get()
            }
        }
    }

    private void registerCleanMagicDrawTask(Project project) {
        project.tasks.register('cleanMagicDraw', Delete) {
            group = "_dev"
            description = "Deletes the current MagicDraw installation and reinstalls it. MagicDraw is not reinstalled " +
                    "except when this task is run."
            finalizedBy 'installMagicDraw'

            // TODO : Would be nice to also delete any "zippedMdPlugin" installed as well
            delete 'build/install'

        }
    }

    private void registerRunTestsTask(Project project) {
        project.tasks.register('runTests', RunTests) {
            group = "_dev"
            description = "Run functional tests with verbose output."

            args += '-verbose'
        }
    }

    private void registerDebugTestsTask(Project project) {
        project.tasks.register('debugTests', RunTests) {
            group = "_dev_eclipse"
            description = "Run functional tests with verbose output in debug mode (needed to debug when using Eclipse)." +
                    " Connect with a remote debugger on port 5005 (default)"

            args += '-verbose'
            jvmArgs += [
                    "-Xdebug",
                    "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=$project.properties.javaDebugPort"
            ]
        }
    }

    private void registerRunTestsNoLogTask(Project project) {
        project.tasks.register('runTestsNoLog', RunTests) {
            group = "_dev"
            description = "Run functional tests without MagicDraw logs (much faster)."

            args += '-verbose'
        }
    }

    private void registerRetrieveModelTask(Project project) {
        project.tasks.register('retrieveModelTask', RunTests) {
            group = "_dev"
            // TODO Add description, not sure what the task does
            description = ""

            args += '-verbose'
            jvmArgs += [
                "-Dcom.nomagic.magicdraw.commandline.action=" + project.properties.testRetriever,
                "-Dtest=false",
                    // TODO We shouldn't have hardcoded models here
                "-DprojectInitName=ModelForTestAuto_Init.mdzip",   // Mandatory
                "-DprojectFinalName=ModelForTestAuto_Final.mdzip", // Optional, can be left blanked
                "-DsaveLocation=${System.getProperty("user.dir")}\\..\\..\\src\\test\\resources\\projects"
            ]
        }
    }

    private void registerTestJarTask(Project project) {
        project.tasks.register('testJar', Jar) {
            group "_build"
            description "Creates a jar containing the compiled classes of the test plugin."

            archiveClassifier.set("tests")
            from project.sourceSets.test.output.classesDirs
        }
    }

    private void registerSourceJarTask(Project project) {
        project.tasks.register('sourceJar', Jar) {
            group "_build"
            description "Creates a jar containing the source code of the plugin and test plugin."
            archiveClassifier.set("sources")
            from project.sourceSets.main.allSource

            dependsOn 'classes'
        }
    }
}
