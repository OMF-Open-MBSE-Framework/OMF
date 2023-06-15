package com.samares_engineering.omf.omf_gradle_plugin

import com.samares_engineering.omf.omf_gradle_plugin.tasks.BuildDist
import com.samares_engineering.omf.omf_gradle_plugin.tasks.RunPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.JavaExec
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
        project.ext.buildNumber = project.hasProperty('buildNumber') ? project.getProperty('buildNumber') : System.currentTimeSeconds()

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
                        "build/${mdPluginBuild.distributionFolderName.get()}", 'build/libs',
                        'build/reports', "build/${mdPluginBuild.testDistributionFolderName.get()}",
                        'build/tmp', 'build/test-reports', 'build/resources'
            }
        }

        /*
        Add dependencies to third party tasks
         */

        project.tasks.compileJava.dependsOn 'installMagicDraw', 'installZippedMDPlugins'

        // Publish tasks are generated with custom names starting with "publish" by the maven-publish plugin
        project.tasks.configureEach {
            if (name.startsWith('publish')) {
                dependsOn 'zipPluginLocally'
            }
        }

    }

    private void registerTasks(Project project) {
        registerInstallZippedMDPluginsTask(project)
        registerDeletePluginsTask(project)
        registerInstallPluginTask(project)
        registerInstallTestPluginTask(project)
        registerRunPluginTask(project)
        registerBuildDistTask(project)
        registerBuildTestDistTask(project)
        registerInstallMagicDrawTask(project)
        registerSrcZipDirTask(project)
        registerZipPluginLocallyTask(project)
        registerDeliverLocallyTask(project)
        registerZipTestPluginLocallyTask(project)
    }

    private void registerDeliverLocallyTask(Project project) {
        project.tasks.register('deliverLocally', Copy) {
            group = "_dev"
            dependsOn 'zipPluginLocally', 'zipTestPluginLocally', 'scrZipDir'

            from "$project.buildDir/builtPlugin/$project.version/${mdPluginBuild.pluginDeliveryName.get()}.zip"
            from "$project.buildDir/builtPlugin/$project.version/${mdPluginBuild.testPluginDeliveryName.get()}.zip"
            project.print "Delivering plugin to ${mdPluginBuild.localDeliveryDirectory.get()}"
            into mdPluginBuild.localDeliveryDirectory.get()
            doLast {
                print "Plugin delivered to file:///${mdPluginBuild.localDeliveryDirectory.get()}"
            }
        }
    }

    private void registerZipPluginLocallyTask(Project project) {
        project.tasks.register('zipPluginLocally', Zip) {
            group = "_delivery"
            dependsOn 'buildDist'

            from "$project.buildDir/${mdPluginBuild.distributionFolderName.get()}"

            archiveFileName = "${mdPluginBuild.pluginDeliveryName.get()}.zip"
            destinationDirectory = project.file("$project.buildDir/builtPlugin/$project.version")
        }
    }

    private void registerZipTestPluginLocallyTask(Project project) {
        project.tasks.register('zipTestPluginLocally', Zip) {
            group = "_delivery"
            dependsOn 'buildTestDist'

            from "$project.buildDir/${mdPluginBuild.testDistributionFolderName.get()}"

            archiveFileName = "${mdPluginBuild.testPluginDeliveryName.get()}.zip"
            destinationDirectory = project.file("$project.buildDir/builtPlugin/$project.version")
        }
    }

    private void registerSrcZipDirTask(Project project) {
        project.tasks.register('srcZipDir', Zip) {
            group = "_delivery"

            from "src"

            archiveFileName = "SRC_${mdPluginBuild.pluginDeliveryName.get()}.zip"
            destinationDirectory = project.file(mdPluginBuild.localDeliveryDirectory.get())
        }
    }

    private void registerInstallMagicDrawTask(Project project) {
        project.tasks.register('installMagicDraw') {
            group = "_install"
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
            dependsOn 'installPlugin', 'installTestPlugin'
        }
    }

    private void registerInstallTestPluginTask(Project project) {
        project.tasks.register('installTestPlugin') {
            group = "_install"
            dependsOn 'installPlugin', 'buildTestDist'

            doLast {
                project.copy {
                    setFileMode(0755)
                    from "$project.buildDir/${mdPluginBuild.testDistributionFolderName.get()}"
                    into "$project.buildDir/install"
                }
            }
        }
    }

    private void registerInstallPluginTask(Project project) {
        project.tasks.register('installPlugin') {
            group = "_install"
            dependsOn 'buildDist', 'deletePlugins'

            doLast {
                project.copy {
                    setFileMode(0755)
                    from "build/${mdPluginBuild.distributionFolderName.get()}"
                    into "$project.buildDir/install"
                }
            }
        }
    }

    private void registerInstallZippedMDPluginsTask(Project project) {
        project.tasks.register('installZippedMDPlugins') {
            group = "_install"
            dependsOn project.configurations.zippedMDPlugin

            doLast {
                project.copy {
                    setFileMode(0755)
                    from project.configurations.zippedMDPlugin.collect { project.zipTree(it) }
                    into "$project.buildDir/install"
                }
            }
        }
    }

    private void registerBuildTestDistTask(Project project) {
        project.tasks.register('buildTestDist', BuildDist) {
            group = "_install"
            dependsOn 'testJar', 'buildDist'

            humanVersion = mdPluginBuild.humanVersion
            pluginDeliveryName = mdPluginBuild.testPluginDeliveryName

            distributionFolderName = mdPluginBuild.testDistributionFolderName
            myPluginMainClass = mdPluginBuild.myTestPluginMainClass
            myPackage = mdPluginBuild.myTestPackage
            myPluginName = mdPluginBuild.myTestPluginName
            myPluginId = mdPluginBuild.myTestPluginId
            resolvedArtifacts = project.configurations.testPluginLibrary.resolvedConfiguration.resolvedArtifacts.file

            pluginUnderTestId = mdPluginBuild.myPluginId
            pluginUnderTestName = mdPluginBuild.myPluginName
        }
    }

    private void registerBuildDistTask(Project project) {
        project.tasks.register('buildDist', BuildDist) {
            group = "_install"
            dependsOn 'jar'

            humanVersion = mdPluginBuild.humanVersion
            pluginDeliveryName = mdPluginBuild.pluginDeliveryName

            distributionFolderName = mdPluginBuild.distributionFolderName
            myPluginMainClass = mdPluginBuild.myPluginMainClass
            myPackage = mdPluginBuild.myPackage
            myPluginName = mdPluginBuild.myPluginName
            myPluginId = mdPluginBuild.myPluginId
            resolvedArtifacts = project.configurations.pluginLibrary.resolvedConfiguration.resolvedArtifacts.file
        }
    }

    // Task to delete plugins created before a new build.
    // Previously in custom task clean, it was blocking the "hot debug" mode
    private void registerDeletePluginsTask(Project project) {
        project.tasks.register('deletePlugins', Delete) {
            group = "_dev"

            // TODO : Would be nice to also delete any "zippedMdPlugin" installed as well
            delete 'build/install/plugins/' + mdPluginBuild.myPackage.get(),
                    'build/install/plugins/' + mdPluginBuild.myTestPackage.get()
        }
    }
}
