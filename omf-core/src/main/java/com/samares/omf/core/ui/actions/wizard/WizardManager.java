/*
 * @copyright Copyright (c) 2021 Airbus SAS
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.1.0
 */

package com.samares.omf.core.ui.actions.wizard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class WizardManager {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public static WizardManager instance;

    public static WizardManager getInstance() {
        if(instance == null)
            instance = new WizardManager();
        return instance;
    }

    public MDWizard getCurrentWizard() {
        return currentWizard;
    }

    public void setCurrentWizard(MDWizard currentWizard) {
        this.currentWizard = currentWizard;
        if(currentWizard != null)
            this.pcs.firePropertyChange("wizardOpened", null, currentWizard);
    }

    public MDWizard currentWizard = null;


    public static synchronized void registerInstance(MDWizard instance) {
        getInstance().setCurrentWizard(instance);
    }

    public static void unRegisterInstance(MDWizard instance) {
        getInstance().setCurrentWizard(null);
    }

    public static MDWizard getOpenedWizard() {
        return getInstance().getCurrentWizard();
    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
}
