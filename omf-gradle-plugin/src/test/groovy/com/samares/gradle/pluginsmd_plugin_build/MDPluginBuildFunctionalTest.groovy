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
                buildTimestamp              = "timestamp"
                myPluginName                = "pluginName"
                myPluginId                  = "pluginId"
                myPackage                   = "pluginPackage"
                myPluginMainClass           = "pluginMainClass"
                distributionFolderName      = "generatedDistFolderName"
                humanVersionCore            = "coreVersion"
                pluginDeliveryName = "pluginDeliveryName"
                descriptorFile = "descriptorFile"
                testDescriptorFile = "descriptorTestFile"
                myTestPluginId          = "testPluginId"
                myTestPluginName        = "pluginTestName"
                myTestPluginMainClass   = "testPluginMainClass"
                myTestPackage = "testPluginId"
                testDistributionFolderName = "testDistFolderName"
                localDeliveryDirectory = "localDeliveryDirectory"
                testPluginDeliveryName = "testPluginDeliveryName"
            }
           
            """
        when:
        def result = GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir)
                .withArguments(
                        'buildDist', //'buildTestDist',
                        //'debugJava',
                        'installZippedMDPlugins', 'installMagicDraw', 'installPlugin', 'installTestPlugin',
                        'deliverLocally', 'zipPluginLocally', 'srcZipDir'
                )
                .withPluginClasspath()
                .build()
        then:
        //result.task(":buildDist").outcome == TaskOutcome.SUCCESS
        true
    }
}
