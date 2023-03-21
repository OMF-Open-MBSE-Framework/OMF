package com.samares.gradle.plugins.md_plugin_build

import org.gradle.api.provider.Property

interface MDPluginBuildExtension {
    Property<String> getHumanVersion()

    Property<String> getBuildTimestamp()

    Property<String> getMyPluginName()

    Property<String> getMyPackage()

    Property<String> getMyPluginId()

    Property<String> getMyPluginMainClass()

    Property<String> getDistributionFolderName()

    Property<String> getHumanVersionCore()

    Property<String> getPluginDeliveryName()

    Property<String> getTestPluginDeliveryName()

    Property<String> getDescriptorFile()

    Property<String> getTestDescriptorFile()

    Property<String> getTestDistributionFolderName()

    Property<String> getMyTestPluginMainClass()

    Property<String> getMyTestPluginName()

    Property<String> getMyTestPackage()

    Property<String> getMyTestPluginId()

    Property<String> getLocalDeliveryDirectory()
}
