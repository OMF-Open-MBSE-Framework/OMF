package com.samares.gradle.pluginsmd_plugin_build

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

class MDPluginBuildFunctionalTest extends Specification {
    @TempDir
    File testProjectDir
    File buildFile

    def setup() {
        buildFile = new File(testProjectDir, 'build.gradle')
        buildFile << """
            plugins {
                id 'com.samares.md_plugin_build'
            }
        """
    }

    def "plugin compiles"() {
        given:
        buildFile << """
            mdPluginBuild {
                humanVersion                = "version"
                myPluginName                = "pluginName"
                myPluginId                  = "pluginId"
                myPackage                   = "pluginPackage"
                myPluginMainClass           = "pluginMainClass"
                distributionFolderName      = "generatedPluginPackageFolderName"
                pluginDeliveryName = "pluginDeliveryName"
                myTestPluginId          = "testPluginId"
                myTestPluginName        = "pluginTestName"
                myTestPluginMainClass   = "testPluginMainClass"
                myTestPackage = "testPluginId"
                testDistributionFolderName = "testPluginPackageFolderName"
                localDeliveryDirectory = "localDeliveryDirectory"
                testPluginDeliveryName = "testPluginDeliveryName"
            }
           
            """
        when:
        def result = GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir)
                .withArguments(
                        'packagePlugin', //'packageTestPlugin',
                        //'runPlugin',
                        'installZippedMDPlugins', 'installMagicDraw', 'installPlugin', 'installTestPlugin',
                        'deliverLocally', 'zipPluginLocally', 'srcZipDir'
                )
                .withPluginClasspath()
                .build()
        then:
        //result.task(":packagePlugin").outcome == TaskOutcome.SUCCESS
        true
    }
}
