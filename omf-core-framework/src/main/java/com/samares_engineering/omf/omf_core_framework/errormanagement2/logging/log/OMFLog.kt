package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.utils.ElementAction
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

class OMFLog {
    private val messageComponents: MutableList<String> = ArrayList()
    val linkActionMapping: MutableMap<String, Runnable> = HashMap()
    private var expandedLog: OMFLog? = null

    constructor()

    constructor(string: String?) {
        text(string)
    }

    fun text(string: String?): OMFLog {
        messageComponents.add(string ?: "")
        return this
    }

    fun text(string: String, logLevel: OMFLogLevel?): OMFLog {
        return when (logLevel) {
            OMFLogLevel.INFO -> info(string)
            OMFLogLevel.WARNING -> warn(string)
            OMFLogLevel.ERROR -> err(string)
            else -> text(string)
        }
    }

    fun expandText(expandedLog: OMFLog?): OMFLog {
        this.expandedLog = expandedLog
        return this
    }

    fun breakLine(): OMFLog {
        return text("<BR>")
    }


    fun bold(string: String): OMFLog {
        return text("<B>$string</B>")
    }


    fun italic(string: String): OMFLog {
        return text("<I>$string</I>")
    }


    fun underline(string: String): OMFLog {
        return text("<U>$string</U>")
    }


    fun strike(string: String): OMFLog {
        return text("<S>$string</S>")
    }


    fun color(string: String, color: String): OMFLog {
        return text("<font color=$color>$string</font>")
    }


    fun colorAll(color: String): OMFLog {
        messageComponents.replaceAll { s: String -> "<font color=$color>$s</font>" }
        return this
    }


    fun warn(string: String): OMFLog {
        return color(string, OMFColors.WARN)
    }


    fun info(string: String): OMFLog {
        return color(string, OMFColors.INFO)
    }


    fun err(string: String): OMFLog {
        return color(string, OMFColors.ERROR)
    }


    fun linkElement(linkText: String, elementToLink: Element?): OMFLog {
        linkActionMapping[linkText] = Runnable {
            ElementAction(elementToLink).selectInBrowser()
        }
        return text("<A>$linkText</A>")
    }

    fun linkElementAndParent(elementToLink: Element?): OMFLog {
        var linkElementName = ""
        var linkOwnerElementName = "DELETED"
        if (elementToLink != null) {
            linkElementName = elementToLink.humanName
            linkActionMapping[linkElementName] = Runnable {
                ElementAction(elementToLink).selectInBrowser()
            }
            if (elementToLink.owner != null) {
                linkOwnerElementName = elementToLink.owner!!.humanName
                linkActionMapping[linkOwnerElementName] = Runnable {
                    ElementAction(elementToLink.owner).selectInBrowser()
                }
            }
        }
        return text("<A>$linkElementName</A>::<A>$linkOwnerElementName</A>")
    }


    fun link(linkText: String, url: String): OMFLog {
        return text("<A href=$url>$linkText</A>")
    }


    fun linkAction(linkText: String, action: Runnable): OMFLog {
        linkActionMapping[linkText] = action
        return text("<A>$linkText</A>")
    }

    fun bold(log: OMFLog): OMFLog {
        return bold(log.toString())
    }

    fun italic(log: OMFLog): OMFLog {
        return italic(log.toString())
    }

    fun underline(log: OMFLog): OMFLog {
        return underline(log.toString())
    }

    fun strike(log: OMFLog): OMFLog {
        return strike(log.toString())
    }

    fun color(log: OMFLog, color: String): OMFLog {
        return color(log.toString(), color)
    }

    fun colorAll(log: OMFLog): OMFLog {
        return colorAll(log.toString())
    }

    fun warn(log: OMFLog): OMFLog {
        return warn(log.toString())
    }

    fun info(log: OMFLog): OMFLog {
        return info(log.toString())
    }

    fun err(log: OMFLog): OMFLog {
        return err(log.toString())
    }

    fun linkElement(log: OMFLog, elementToLink: Element?): OMFLog {
        return linkElement(log.toString(), elementToLink)
    }

    fun link(log: OMFLog, url: String): OMFLog {
        return link(log.toString(), url)
    }

    fun linkAction(log: OMFLog, action: Runnable): OMFLog {
        return linkAction(log.toString(), action)
    }

    /*
     * Log message formatting
     */
    fun toHTMLFormat(logLevel: OMFLogLevel): String {
        val expandedLogString = if (expandedLog != null) "<BR>$expandedLog" else ""

        return ("<font color=" + getMessageColor(logLevel) + ">"
                + OMFLogger.getPrefix(logLevel)
                + " " + toString(" ")
                + expandedLogString
                + "</font>")
    }

    fun toHTMLFormat(logLevel: OMFLogLevel, featureName: String?): String {
        return ("<font color=" + getMessageColor(logLevel) + ">"
                + OMFLogger.getPrefix(logLevel, featureName) + " "
                + toString(" ")
                + "</font>")
    }

    fun toString(delimiter: String?): String {
        val message = StringBuilder()
        messageComponents.forEach(Consumer { component: String? -> message.append(component).append(delimiter) })
        return message.toString()
    }

    override fun toString(): String {
        return toString(" ")
    }

    /*
     * Syntaxic sugar to reduced boilerplate of logging
     */
    fun logToUiConsole(logLevel: OMFLogLevel?): OMFLog {
        OMFLogger.logToUIConsole(this, logLevel)
        return this
    }

    fun logToUiConsole(logLevel: OMFLogLevel?, feature: OMFFeature?): OMFLog {
        OMFLogger.logToUIConsole(this, logLevel, feature)
        return this
    }

    fun logToNotification(logLevel: OMFLogLevel?): OMFLog {
        OMFLogger.logToNotification(this, logLevel)
        return this
    }

    fun logToNotification(logLevel: OMFLogLevel?, feature: OMFFeature?): OMFLog {
        OMFLogger.logToNotification(this, logLevel, feature)
        return this
    }

    fun logToSystemConsole(logLevel: OMFLogLevel?): OMFLog {
        OMFLogger.logToSystemConsole(this, logLevel)
        return this
    }

    fun logToSystemConsole(logLevel: OMFLogLevel?, feature: OMFFeature?): OMFLog {
        OMFLogger.logToSystemConsole(this, logLevel, feature)
        return this
    }

    fun logWarn(): OMFLog {
        OMFLogger.warn(this)
        return this
    }

    fun logErr(): OMFLog {
        OMFLogger.err(this)
        return this
    }

    fun logWarnWithCause(e: Exception?): OMFLog {
        OMFLogger.warn(this, e)
        return this
    }

    fun logErrWithCause(e: Exception?): OMFLog {
        OMFLogger.err(this, e)
        return this
    }

    fun replaceNewLinesWithBreaks(): OMFLog {
        messageComponents.replaceAll { s: String -> s.replace("\n".toRegex(), "<BR>") }
        return this
    }

    fun replaceNewLinesWithBreaksInExpandLog(): OMFLog? {
        if (expandedLog == null) {
            return null
        }

        expandedLog!!.messageComponents.stream()
            .filter { obj: String? -> Objects.nonNull(obj) }
            .collect(Collectors.toList())
            .replaceAll { s: String -> s.replace("\n".toRegex(), "<BR>") }
        return this
    }

    private fun getMessageColor(logLevel: OMFLogLevel): String {
        return when (logLevel) {
            OMFLogLevel.WARNING -> OMFColors.WARN
            OMFLogLevel.ERROR -> OMFColors.ERROR
            OMFLogLevel.INFO -> OMFColors.INFO
            else -> OMFColors.INFO
        }
    }
}


