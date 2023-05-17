package com.samares_engineering.omf.omf_gradle_plugin

import org.gradle.api.provider.Property

interface OmfGradlePluginBuildExtension {
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

    Property<String> getTestDistributionFolderName()

    Property<String> getMyTestPluginMainClass()

    Property<String> getMyTestPluginName()

    Property<String> getMyTestPackage()

    Property<String> getMyTestPluginId()

    Property<String> getLocalDeliveryDirectory()
}
