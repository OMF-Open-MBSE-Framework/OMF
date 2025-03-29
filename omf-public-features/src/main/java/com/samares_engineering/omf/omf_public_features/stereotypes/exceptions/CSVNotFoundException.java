package com.samares_engineering.omf.omf_public_features.stereotypes.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFLogException;

import java.io.FileNotFoundException;

public class CSVNotFoundException extends OMFLogException {

    public CSVNotFoundException(String csvConfigFilePath, FileNotFoundException originalException) {
        super( "Can't find .csv config file " + csvConfigFilePath
                + ", make sure the path defined in "
                + "environment options is correct", originalException);
    }

}
