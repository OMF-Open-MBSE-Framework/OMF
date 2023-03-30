package com.samares_engineering.omf.omf_gradle_plugin

import com.samares_engineering.omf.omf_gradle_plugin.tasks.BuildDist
import com.samares_engineering.omf.omf_gradle_plugin.tasks.RunPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Zip

class OmfGradlePlugin implements Plugin<Project> {
    OmfGradlePluginBuildExtension mdPluginBuild;

    void apply(Project project) {
        mdPluginBuild = project.extensions.create('mdPluginBuild', OmfGradlePluginBuildExtension)

        project.configurations {
            mdApplicationArchive.extendsFrom(implementation)
            testImplementation.extendsFrom(testPluginLibrary)
            implementation.extendsFrom(pluginLibrary)
            implementation.extendsFrom(mdLibrary)
            zippedMDPlugin
            compileOnly.extendsFrom(otherMDPluginLibrary)
        }

        project.getPlugins().apply('java')

        registerTasks(project)
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
            it.group = "_dev"

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
            it.group = "_delivery"

            from "$project.buildDir/${mdPluginBuild.distributionFolderName.get()}"

            it.archiveFileName = "${mdPluginBuild.pluginDeliveryName.get()}.zip"
            it.destinationDirectory = project.file("$project.buildDir/builtPlugin/$project.version")
        }
    }

    private void registerZipTestPluginLocallyTask(Project project) {
        project.tasks.register('zipTestPluginLocally', Zip) {
            it.group = "_delivery"

            from "$project.buildDir/${mdPluginBuild.testDistributionFolderName.get()}"

            it.archiveFileName = "${mdPluginBuild.testPluginDeliveryName.get()}.zip"
            it.destinationDirectory = project.file("$project.buildDir/builtPlugin/$project.version")
        }
    }

    private void registerSrcZipDirTask(Project project) {
        project.tasks.register('srcZipDir', Zip) {
            it.group = "_delivery"

            from "src"

            it.archiveFileName = "SRC_${mdPluginBuild.pluginDeliveryName.get()}.zip"
            it.destinationDirectory = project.file(mdPluginBuild.localDeliveryDirectory.get())
        }
    }

    private void registerInstallMagicDrawTask(Project project) {
        project.tasks.register('installMagicDraw') {
            it.group = "_install"
            def cameoConf = project.configurations.mdApplicationArchive
            def isAlreadyInstalled = new File("$project.buildDir/install").exists()
            it.doLast {
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
        project.tasks.register('runPlugin', RunPlugin)
    }

    private void registerInstallTestPluginTask(Project project) {
        project.tasks.register('installTestPlugin') {
            it.group = "_install"
            it.doLast {
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
            it.group = "_install"
            it.doLast {
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
            it.group = "_install"
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
            it.group = "_install"
            it.humanVersion = mdPluginBuild.humanVersion
            it.buildTimestamp = mdPluginBuild.buildTimestamp
            it.humanVersionCore = mdPluginBuild.humanVersionCore
            it.pluginDeliveryName = mdPluginBuild.testPluginDeliveryName
            it.descriptorFile = mdPluginBuild.testDescriptorFile

            it.distributionFolderName = mdPluginBuild.testDistributionFolderName
            it.myPluginMainClass = mdPluginBuild.myTestPluginMainClass
            it.myPackage = mdPluginBuild.myTestPackage
            it.myPluginName = mdPluginBuild.myTestPluginName
            it.myPluginId = mdPluginBuild.myTestPluginId
            it.resolvedArtifacts = project.configurations.testPluginLibrary.resolvedConfiguration.resolvedArtifacts.file

            it.pluginUnderTestId = mdPluginBuild.myPluginId
            it.pluginUnderTestName = mdPluginBuild.myPluginName
        }
    }

    private void registerBuildDistTask(Project project) {
        project.tasks.register('buildDist', BuildDist) {
            it.group = "_install"
            it.humanVersion = mdPluginBuild.humanVersion
            it.buildTimestamp = mdPluginBuild.buildTimestamp
            it.humanVersionCore = mdPluginBuild.humanVersionCore
            it.pluginDeliveryName = mdPluginBuild.pluginDeliveryName
            it.descriptorFile = mdPluginBuild.descriptorFile

            it.distributionFolderName = mdPluginBuild.distributionFolderName
            it.myPluginMainClass = mdPluginBuild.myPluginMainClass
            it.myPackage = mdPluginBuild.myPackage
            it.myPluginName = mdPluginBuild.myPluginName
            it.myPluginId = mdPluginBuild.myPluginId
            it.resolvedArtifacts = project.configurations.pluginLibrary.resolvedConfiguration.resolvedArtifacts.file
        }
    }
}
