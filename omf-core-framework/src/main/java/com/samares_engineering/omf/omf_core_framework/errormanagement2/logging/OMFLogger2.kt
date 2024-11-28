package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging

import com.nomagic.magicdraw.core.Application
import com.nomagic.magicdraw.ui.notification.Notification
import com.nomagic.magicdraw.ui.notification.NotificationManager
import com.nomagic.magicdraw.ui.notification.NotificationSeverity
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFColors
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin

fun main() {
    OMFLogger2.toAll().warning("This is a warning message.")
    OMFLogger2.toAll().warning(OMFLog("This is a warning message."))

    OMFLogger2.toUI().log(OMFLog().text("This is a success message."))

    OMFLogger.warn("This is a warning message.")
    OMFLogger.logToUIConsole(OMFLog().text("This is a success message."), OMFLogLevel.INFO)
}

class OMFLogger2 private constructor(private val plugin: OMFPlugin) {

    private val logLevel: OMFLogLevel = OMFLogLevel.INFO
    private var currentTarget: LogTarget? = null
    private var feature: OMFFeature? = null

    enum class LogTarget {
        UI_CONSOLE, SYSTEM_CONSOLE, NOTIFICATION, ALL
    }

    companion object {
        @JvmStatic
        private var instance: OMFLogger2? = null

        @JvmStatic
        fun init(plugin: OMFPlugin) {
            if (instance != null) {
                throw CoreException2("OMFLogger2 has already been initialized.")
            }
            instance = OMFLogger2(plugin)
        }

        @JvmStatic
        private fun getInstance(): OMFLogger2 {
            if (instance == null) {
                throw CoreException2("OMFLogger2 not initialized. Call init() first.")
            }
            return instance as OMFLogger2
        }

        @JvmStatic
        fun toUI(): OMFLogger2 {
            return getInstance().apply { currentTarget = LogTarget.UI_CONSOLE }
        }

        @JvmStatic
        fun toSystem(): OMFLogger2 {
            return getInstance().apply { currentTarget = LogTarget.SYSTEM_CONSOLE }
        }

        @JvmStatic
        fun toNotification(): OMFLogger2 {
            return getInstance().apply { currentTarget = LogTarget.NOTIFICATION }
        }

        @JvmStatic
        fun toAll(): OMFLogger2 {
            return getInstance().apply { currentTarget = LogTarget.ALL }
        }
    }

    fun feature(feature: OMFFeature): OMFLogger2 {
        return getInstance().apply { this.feature = feature }
    }
    fun log(message: OMFLog) {
        log(message, OMFLogLevel.INFO)
    }

    fun success(message: String) {
        log(OMFLog().color(message, OMFColors.GREEN), OMFLogLevel.SUCCESS)
    }

    fun success(message: OMFLog) {
        log(OMFLog().colorAll(OMFColors.GREEN), OMFLogLevel.SUCCESS)
    }

    fun warning(message: String) {
        log(OMFLog().color(message, OMFColors.WARN), OMFLogLevel.WARNING)
    }

    fun warning(message: OMFLog) {
        log(message.colorAll(OMFColors.WARN), OMFLogLevel.WARNING)
    }

    fun error(message: String) {
        log(OMFLog().colorAll(OMFColors.ERROR), OMFLogLevel.ERROR)
    }

    fun error(message: OMFLog) {
        log(OMFLog().colorAll(OMFColors.ERROR), OMFLogLevel.ERROR)
    }

    private fun log(logMessage: OMFLog, logLevel: OMFLogLevel) {
        if (logLevel.ordinal >= this.logLevel.ordinal) {
            val formattedLog = logMessage.toHTMLFormat(logLevel, plugin.name, feature?.name)
            when (currentTarget) {
                LogTarget.UI_CONSOLE -> logToMDConsole(formattedLog, logMessage)
                LogTarget.NOTIFICATION -> logToNotification(logLevel, logMessage)
                LogTarget.SYSTEM_CONSOLE -> printToSystemConsole(logLevel, logMessage.toString())
                LogTarget.ALL -> {
                    logToMDConsole(formattedLog, logMessage)
                    logToNotification(logLevel, logMessage)
                    printToSystemConsole(logLevel, logMessage.toString())
                }
                null -> {}
            }
        }
    }

    private fun logToMDConsole(
        formattedLog: String?,
        logMessage: OMFLog
    ) {
        Application.getInstance().guiLog.addHyperlinkedText(formattedLog, logMessage.linkActionMapping)
    }

    private fun logToNotification(
        logLevel: OMFLogLevel,
        logMessage: OMFLog
    ) {
        NotificationManager.getInstance().showNotification(createNotification(logLevel, logMessage))
    }

    private fun printToSystemConsole(logLevel: OMFLogLevel, message: String) {
        when (logLevel) {
            OMFLogLevel.WARNING -> SysoutColorPrinter.warn(message)
            OMFLogLevel.ERROR -> SysoutColorPrinter.err(message)
            OMFLogLevel.SUCCESS -> SysoutColorPrinter.success(message)
            OMFLogLevel.INFO -> SysoutColorPrinter.print(message)
        }
    }

    private fun createNotification(logLevel: OMFLogLevel, logMessage: OMFLog): Notification {
        val title = OMFLog.getPrefix(logLevel, plugin.name, feature?.name)
        return Notification(
            "[Plugin Error]",
            title,
            logMessage.replaceNewLinesWithBreaks().toString(),
            getNotificationSeverity(logLevel)
        )
    }

    private fun getNotificationSeverity(logLevel: OMFLogLevel): NotificationSeverity {
        return when (logLevel) {
            OMFLogLevel.WARNING -> NotificationSeverity.WARNING
            OMFLogLevel.ERROR -> NotificationSeverity.ERROR
            OMFLogLevel.INFO -> NotificationSeverity.INFO
            else -> NotificationSeverity.INFO
        }
    }

    @GenerateLegacyMethods
    class OMFLogger2LegacyMethods {

    }
}