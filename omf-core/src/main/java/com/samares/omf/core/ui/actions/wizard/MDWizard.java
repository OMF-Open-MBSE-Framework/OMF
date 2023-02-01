/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.ui.actions.wizard;


import com.samares.omf.core.utils.OMFConstants;

import javax.swing.*;

public interface MDWizard {

    default void registerInstance (MDWizard instance){
        WizardManager.registerInstance(instance);
    }
    default void unRegisterInstance (MDWizard instance){
        WizardManager.unRegisterInstance(instance);
    }

    default void endOpening(JDialog frame) {
        //INTRODUCED FOR TESTING PURPOSES
        registerInstance(this);
        if(OMFConstants.GUI_REQUIRED) {
            frame.setVisible(true);
        }
    }

    default void closeWizard(JDialog frame) {
        frame.dispose();
        unRegisterInstance(this);
    }


}
