package com.samares_engineering.omf.omf_core_framework.utils.twc.exceptions

class NotConnectedToTWC(
    message: String = "TWC access is impossible. Please check that you are connected.",
    cause: Exception? = null
) : RuntimeException(message, cause) {

}
