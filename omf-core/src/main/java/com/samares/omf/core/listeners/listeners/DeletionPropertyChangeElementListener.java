/*
 * @copyright Copyright (c) 2021 Airbus SAS
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.1.0
 */
package com.samares.omf.core.listeners.listeners;

import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants;
import com.samares.omf.core.listeners.AElementListener;
import com.samares.omf.core.utils.OMFUtils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

public class DeletionPropertyChangeElementListener extends AElementListener implements PropertyChangeListener {
    public DeletionPropertyChangeElementListener() {
        super();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!isActivated()) return;

        boolean isInstanceDeleted = (UML2MetamodelConstants.BEFORE_DELETE.equals(evt.getPropertyName()));
        if (isInstanceDeleted) {
            manageDeletion(evt);
        }
    }

    @Override
    public void addListener() {
        OMFUtils.currentProject.getRepositoryListenerRegistry().addPropertyChangeListener(this,
                UML2MetamodelConstants.BEFORE_DELETE);
    }

    @Override
    public void removeListener() {
        OMFUtils.currentProject.getRepositoryListenerRegistry().removePropertyChangeListener(this,
                UML2MetamodelConstants.BEFORE_DELETE);
    }

    @Override
    public void manageAfterAutomation(Collection<PropertyChangeEvent> collection) {

    }
}
