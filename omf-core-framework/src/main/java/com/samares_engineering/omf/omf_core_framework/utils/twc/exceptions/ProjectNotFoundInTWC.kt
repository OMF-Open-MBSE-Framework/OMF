package com.samares_engineering.omf.omf_core_framework.utils.twc.exceptions

class ProjectNotFoundInTWC(
    message: String = "The current project can't be found in TWC. Please, commit your project to TWC",
    cause: Exception? = null
) : RuntimeException(message, cause) {

}
