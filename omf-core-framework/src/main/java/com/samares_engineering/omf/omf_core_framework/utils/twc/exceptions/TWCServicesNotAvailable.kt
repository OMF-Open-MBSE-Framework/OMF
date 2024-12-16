package com.samares_engineering.omf.omf_core_framework.utils.twc.exceptions

class TWCServicesNotAvailable(
    message: String = "TWC Services are not available, please TWC connection and try again",
    cause: Exception? = null
) : RuntimeException(message, cause) {

}
