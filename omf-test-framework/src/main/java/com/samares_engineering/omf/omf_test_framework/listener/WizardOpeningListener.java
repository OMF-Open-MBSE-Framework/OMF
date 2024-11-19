/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.listener;

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
