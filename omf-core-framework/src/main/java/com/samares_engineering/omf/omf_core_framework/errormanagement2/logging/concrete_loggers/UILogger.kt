package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.concrete_loggers

import com.nomagic.magicdraw.core.Application
import com.nomagic.magicdraw.ui.notification.Notification
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFConcreteLogger
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin
import java.time.Instant.now

class UILogger(override val plugin: OMFPlugin) : OMFConcreteLogger {

    override fun log(
        prefix: String,
        logMessage: OMFLog,
        plugin: OMFPlugin,
        feature: OMFFeature?,
        logLevel: OMFLogLevel
    ) {
//        Application.getInstance().guiLog.addHyperlinkedText, message.linkActionMapping)

        val notification = Notification(prefix+now(), prefix, logMessage.replaceNewLinesWithBreaks().toString())
        notification.context = Notification.Context.PROJECT
        Application.getInstance().guiLog.log(notification, true) // set false to not activate Notifications Window
    }


}