package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin

interface OMFConcreteLogger {
    val plugin: OMFPlugin
    fun log(prefix: String, logMessage: OMFLog, plugin: OMFPlugin, feature: OMFFeature?, logLevel: OMFLogLevel)
}