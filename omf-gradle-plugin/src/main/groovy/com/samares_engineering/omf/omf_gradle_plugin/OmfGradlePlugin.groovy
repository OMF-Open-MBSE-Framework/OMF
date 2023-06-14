package com.samares_engineering.omf.omf_gradle_plugin

import com.samares_engineering.omf.omf_gradle_plugin.tasks.BuildDist
import com.samares_engineering.omf.omf_gradle_plugin.tasks.RunPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Zip

class OmfGradlePlugin implements Plugin<Project> {
    OmfGradlePluginBuildExtension mdPluginBuild;

    void apply(Project project) {
        mdPluginBuild = project.extensions.create('mdPluginBuild', OmfGradlePluginBuildExtension)

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
            dependsOn 'buildDist'

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
            buildTimestamp = mdPluginBuild.buildTimestamp
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
            buildTimestamp = mdPluginBuild.buildTimestamp
            pluginDeliveryName = mdPluginBuild.pluginDeliveryName

            distributionFolderName = mdPluginBuild.distributionFolderName
            myPluginMainClass = mdPluginBuild.myPluginMainClass
            myPackage = mdPluginBuild.myPackage
            myPluginName = mdPluginBuild.myPluginName
            myPluginId = mdPluginBuild.myPluginId
            resolvedArtifacts = project.configurations.pluginLibrary.resolvedConfiguration.resolvedArtifacts.file
        }
    }
}
