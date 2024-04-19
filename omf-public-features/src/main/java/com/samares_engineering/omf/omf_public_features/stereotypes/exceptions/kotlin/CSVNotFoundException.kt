package com.samares_engineering.omf.omf_public_features.stereotypes.exceptions.kotlin

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFWarningException
import java.io.FileNotFoundException

class CSVNotFoundException(csvConfigFilePath: String, originalException: OMFWarningException)
    : Exception( "Can't find .csv config file " + csvConfigFilePath
        + ", make sure the path defined in "
        + "environment options is correct", originalException) {
}