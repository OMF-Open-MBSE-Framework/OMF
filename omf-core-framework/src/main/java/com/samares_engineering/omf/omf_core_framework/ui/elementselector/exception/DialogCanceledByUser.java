package com.samares_engineering.omf.omf_core_framework.ui.elementselector.exception;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFLogException;

public class DialogCanceledByUser extends OMFLogException {
    public DialogCanceledByUser() {
        super("Dialog canceled by user");
    }
}
