package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.concrete_loggers

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFConcreteLogger
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.SysoutColorPrinter
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin
import java.time.Instant.now

class SystemLogger(override val plugin: OMFPlugin) : OMFConcreteLogger {

    override fun log(
        prefix: String,
        logMessage: OMFLog,
        plugin: OMFPlugin,
        feature: OMFFeature?,
        logLevel: OMFLogLevel
    ) {
        val message = prefix + now() + logMessage.toString()
        when (logLevel) {
            OMFLogLevel.WARNING -> SysoutColorPrinter.warn(message)
            OMFLogLevel.ERROR -> SysoutColorPrinter.err(message)
            OMFLogLevel.SUCCESS -> SysoutColorPrinter.success(message)
            OMFLogLevel.INFO -> SysoutColorPrinter.print(message)
        }
    }


}