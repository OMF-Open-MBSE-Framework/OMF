package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging

import com.google.common.base.Strings
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.concrete_loggers.NotificationLogger
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.concrete_loggers.SystemLogger
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.concrete_loggers.UILogger
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFColors
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin

class OMFLogger2 private constructor(
    private val plugin: OMFPlugin
) {

    private val mapLogger: MutableMap<String, OMFConcreteLogger> = mutableMapOf(
        LogTarget.UI_CONSOLE.toString() to UILogger(plugin),
        LogTarget.SYSTEM_CONSOLE.toString() to SystemLogger(plugin),
        LogTarget.NOTIFICATION.toString() to NotificationLogger(plugin)
    )
    private val logLevel: OMFLogLevel = OMFLogLevel.INFO
    private var currentTarget: LogTarget? = null
    private var feature: OMFFeature? = null

    val uiLogger
        get() = mapLogger["UI_CONSOLE"]
    val systemLogger
        get() = mapLogger["SYSTEM_CONSOLE"]
    val notificationLogger
        get() = mapLogger["NOTIFICATION"]

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

        // Utility methods
        fun getPrefix(logLevel: OMFLogLevel): String {
            val pluginPrefix = if (instance?.plugin != null) "[${instance?.plugin?.name}]" else ""
            return "[" + getLogLevelPrefix(logLevel) + "]" + pluginPrefix
        }

        fun getPrefix(logLevel: OMFLogLevel, featureName: String): String {
            val featureTag = if (Strings.isNullOrEmpty(featureName)) "" else "[$featureName]"
            return getPrefix(logLevel) + featureTag
        }

        private fun getLogLevelPrefix(logLevel: OMFLogLevel): String {
            return when (logLevel) {
                OMFLogLevel.WARNING -> "Warning"
                OMFLogLevel.ERROR -> "Error"
                OMFLogLevel.INFO -> "Info"
                else -> "Info"
            }
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
        log(message.colorAll(OMFColors.GREEN), OMFLogLevel.SUCCESS)
    }

    fun warning(message: String) {
        log(OMFLog().color(message, OMFColors.WARN), OMFLogLevel.WARNING)
    }

    fun warning(message: OMFLog) {
        log(message.colorAll(OMFColors.WARN), OMFLogLevel.WARNING)
    }

    fun error(message: String) {
        log(OMFLog(message).colorAll(OMFColors.ERROR), OMFLogLevel.ERROR)
    }

    fun error(message: OMFLog) {
        log(message.colorAll(OMFColors.ERROR), OMFLogLevel.ERROR)
    }

    private fun log(logMessage: OMFLog, logLevel: OMFLogLevel) {
        if (logLevel.ordinal >= this.logLevel.ordinal) {
            val formattedLog = logMessage.toHTMLFormat(logLevel, feature?.name)
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
        uiLogger?.log(formattedLog ?: "", logMessage, plugin, feature, logLevel)
    }

    private fun logToNotification(
        logLevel: OMFLogLevel,
        logMessage: OMFLog
    ) {
        notificationLogger?.log("", logMessage, plugin, feature, logLevel)
    }

    private fun printToSystemConsole(logLevel: OMFLogLevel, message: String) {
        val log = OMFLog(message)
        systemLogger?.log("", log, plugin, feature, logLevel)
    }

    @GenerateLegacyMethods
    class OMFLogger2LegacyMethods {

    }
}