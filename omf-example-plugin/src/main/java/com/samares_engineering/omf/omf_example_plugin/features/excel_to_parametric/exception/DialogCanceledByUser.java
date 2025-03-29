package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.exception;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFLogException;

public class DialogCanceledByUser extends OMFLogException {
    public DialogCanceledByUser() {
        super("Dialog canceled by user");
    }
}
