/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test.ui;

import com.nomagic.actions.NMAction;
import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.core.errors.exceptions.GenericException;
import com.samares.omf.core.errors.exceptions.NotImplementedException;

import java.awt.event.ActionEvent;

public class TestAction extends NMAction {
    public TestAction() {
        super("", "Run test", null, null);
    }

    public void actionPerformed(ActionEvent e) {
        // TODO Import/Reimplement this from the other Interface Plugin
        //We do this to tell Wizards to run without UI
//        OMFConstants.GUI_REQUIRED = false;
//        new TestMain(FunctionalBatch.class).execute(new String[0]);
//        OMFConstants.GUI_REQUIRED = true;

        OMFErrorHandler.handleException(
                new NotImplementedException("Running tests from user interface is not currently fully implemented",
                GenericException.ECriticality.ALERT));
    }


}
