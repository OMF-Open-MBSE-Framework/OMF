package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.exception;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;

import static com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier.SILENT;

public class NoOwnerSelectedException extends OMFCriticalException {
    public NoOwnerSelectedException() {
        super("No Element Owner selected by user, operation aborted.", SILENT);
    }
}
