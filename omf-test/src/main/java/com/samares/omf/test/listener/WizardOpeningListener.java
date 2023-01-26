/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.test.listener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class WizardOpeningListener implements PropertyChangeListener {
    Runnable wizardActions;
    public WizardOpeningListener(Runnable doWizardActions) {
        this.wizardActions = doWizardActions;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if("wizardOpened".equals(evt.getPropertyName())) {
            wizardActions.run();
        }

    }
}
