package com.samares.omf.plugin.test.ui;

import com.nomagic.actions.NMAction;
import com.samares.omf.core.utils.errorManagement.OMFErrorHandler;
import com.samares.omf.core.utils.errorManagement.exceptions.GenericException;
import com.samares.omf.core.utils.errorManagement.exceptions.NotImplementedException;

import java.awt.event.ActionEvent;

public class TestAction extends NMAction {
    public TestAction() {
        super("", "Run test", null, null);
    }

    public void actionPerformed(ActionEvent e) {
        // TODO Import/Reimplement this from DDMS Interface Plugin
        //We do this to tell Wizards to run without UI
//        OMFConstants.GUI_REQUIRED = false;
//        new TestMain(FunctionalBatch.class).execute(new String[0]);
//        OMFConstants.GUI_REQUIRED = true;

        OMFErrorHandler.handleException(
                new NotImplementedException("Running tests from user interface is not currently fully implemented",
                GenericException.ECriticality.ALERT));
    }


}
