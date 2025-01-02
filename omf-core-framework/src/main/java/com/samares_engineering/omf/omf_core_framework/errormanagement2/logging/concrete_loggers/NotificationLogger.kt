package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.concrete_loggers

import com.nomagic.magicdraw.ui.notification.Notification
import com.nomagic.magicdraw.ui.notification.NotificationSeverity
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFConcreteLogger
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFNotificationManager
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin

class NotificationLogger(override val plugin: OMFPlugin) : OMFConcreteLogger {
    override fun log(
        prefix: String,
        logMessage: OMFLog,
        plugin: OMFPlugin,
        feature: OMFFeature?,
        logLevel: OMFLogLevel
    ) {
        createNotification(logLevel, logMessage, feature)
    }

    private fun createNotification(logLevel: OMFLogLevel, logMessage: OMFLog, feature: OMFFeature?): Notification {
        val featureName = feature?.name ?: ""
        val title = OMFLogger2.getPrefix(logLevel, featureName)
        val notification = Notification(
            "[Plugin Error]", //id (not sure what is does)
            title,
            logMessage.replaceNewLinesWithBreaks().toString(),
            getNotificationSeverity(logLevel)
        )
        val expandedMessage = logMessage.replaceNewLinesWithBreaksInExpandLog()
        if (expandedMessage != null) notification.longText = expandedMessage.toString()

        OMFNotificationManager.getInstance().showNotification(notification)
        return notification
    }

    private fun getNotificationSeverity(logLevel: OMFLogLevel): NotificationSeverity {
        return when (logLevel) {
            OMFLogLevel.WARNING -> NotificationSeverity.WARNING
            OMFLogLevel.ERROR -> NotificationSeverity.ERROR
            OMFLogLevel.INFO -> NotificationSeverity.INFO
            else -> NotificationSeverity.INFO
        }
    }
}