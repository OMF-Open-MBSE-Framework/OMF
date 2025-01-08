package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log

import com.google.common.base.Strings
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.utils.ElementAction
import java.util.*
import java.util.function.Consumer
import java.util.function.UnaryOperator
import java.util.stream.Collectors

class OMFLog {
    private val messageComponents: MutableList<String> = ArrayList()
    private val linkActionMapping: MutableMap<String, Runnable> = HashMap()
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

    @OMFLogEquivalent
    fun bold(string: String): OMFLog {
        return text("<B>$string</B>")
    }

    @OMFLogEquivalent
    fun italic(string: String): OMFLog {
        return text("<I>$string</I>")
    }

    @OMFLogEquivalent
    fun underline(string: String): OMFLog {
        return text("<U>$string</U>")
    }

    @OMFLogEquivalent
    fun strike(string: String): OMFLog {
        return text("<S>$string</S>")
    }

    @OMFLogEquivalent
    fun color(string: String, color: String): OMFLog {
        return text("<font color=$color>$string</font>")
    }

    @OMFLogEquivalent
    fun colorAll(color: String): OMFLog {
        messageComponents.replaceAll { s: String -> "<font color=$color>$s</font>" }
        return this
    }


    @OMFLogEquivalent
    fun warn(string: String): OMFLog {
        return color(string, OMFColors.WARN)
    }

    @OMFLogEquivalent
    fun info(string: String): OMFLog {
        return color(string, OMFColors.INFO)
    }

    @OMFLogEquivalent
    fun err(string: String): OMFLog {
        return color(string, OMFColors.ERROR)
    }

    @OMFLogEquivalent
    fun linkElement(linkText: String, elementToLink: Element?): OMFLog {
        linkActionMapping[linkText] = Runnable {
            ElementAction(elementToLink).selectInBrowser()
        }
        return text("<A>$linkText</A>")
    }

    //<--GENERATED METHOD -->

    // Méthodes générées automatiquement
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
fun colorAll(log: OMFLog, color: String): OMFLog {
    return colorAll(log.toString(), color)
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
fun linkElement(log: OMFLog, linkText: String, elementToLink: Element?): OMFLog {
    return linkElement(log.toString(), linkText, elementToLink)
}
fun link(log: OMFLog, linkText: String, url: String): OMFLog {
    return link(log.toString(), linkText, url)
}
fun linkAction(log: OMFLog, linkText: String, action: Runnable): OMFLog {
    return linkAction(log.toString(), linkText, action)
}


    //--------------------------------------------------------------------------------------------------

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

    @OMFLogEquivalent
    fun link(linkText: String, url: String): OMFLog {
        return text("<A href=$url>$linkText</A>")
    }

    @OMFLogEquivalent
    fun linkAction(linkText: String, action: Runnable): OMFLog {
        linkActionMapping[linkText] = action
        return text("<A>$linkText</A>")
    }

    /*
     * Log message formatting
     */
    fun toHTMLFormat(logLevel: OMFLogLevel): String {
        return ("<font color=" + getMessageColor(logLevel) + ">" + getPrefix(logLevel)
                + " " + toString(" ") + "</font>")
    }

    fun toHTMLFormat(logLevel: OMFLogLevel, pluginName: String): String {
        val expandedLogString = if (expandedLog != null) "<BR>$expandedLog" else ""

        return ("<font color=" + getMessageColor(logLevel) + ">"
                + getPrefix(logLevel, pluginName)
                + " " + toString(" ")
                + expandedLogString
                + "</font>")
    }

    fun toHTMLFormat(logLevel: OMFLogLevel, pluginName: String, featureName: String?): String {
        return ("<font color=" + getMessageColor(logLevel) + ">"
                + getPrefix(logLevel, pluginName, featureName) + " "
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


    /*
     * Getters
     */
    fun getLinkActionMapping(): Map<String, Runnable> {
        return linkActionMapping
    }

    fun replaceNewLinesWithBreaks(): OMFLog {
        messageComponents.replaceAll { s: String -> s.replace("\n".toRegex(), "<BR>") }
        return this
    }

    fun replaceNewLinesWithBreaksInExpandLog(): OMFLog? {
        if (expandedLog == null) {
            return null
        }

        expandedLog!!.messageComponents
            .stream()
            .filter { obj: String? -> Objects.nonNull(obj) }
            .collect(Collectors.toList<String>())
            .replaceAll(UnaryOperator<String> { s: String -> s.replace("\n".toRegex(), "<BR>") })
        return this
    }

    companion object {
        fun getPrefix(logLevel: OMFLogLevel): String {
            return "[" + getLogLevelPrefix(logLevel) + "]"
        }

        @JvmStatic
        fun getPrefix(logLevel: OMFLogLevel, pluginName: String): String {
            return getPrefix(logLevel) + "[" + pluginName + "]"
        }

        @JvmStatic
        fun getPrefix(logLevel: OMFLogLevel, pluginName: String, featureName: String?): String {
            val featureTag = if (Strings.isNullOrEmpty(featureName)) "" else "[$featureName]"
            return getPrefix(logLevel, pluginName) + featureTag
        }

        private fun getLogLevelPrefix(logLevel: OMFLogLevel): String {
            return when (logLevel) {
                OMFLogLevel.WARNING -> "Warning"
                OMFLogLevel.ERROR -> "Error"
                OMFLogLevel.INFO -> "Info"
                else -> "Info"
            }
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
}


