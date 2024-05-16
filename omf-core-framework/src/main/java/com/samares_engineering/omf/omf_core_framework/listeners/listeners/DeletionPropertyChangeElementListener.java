/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.listeners.listeners;

import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.RollbackException2;
import com.samares_engineering.omf.omf_core_framework.errors.cancelsession.UndoManager;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFRollBackException;
import com.samares_engineering.omf.omf_core_framework.listeners.AElementListener;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DeletionPropertyChangeElementListener extends AElementListener implements PropertyChangeListener {
    public DeletionPropertyChangeElementListener() {
        super();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!isActivated()) return;

        boolean isInstanceDeleted = (UML2MetamodelConstants.BEFORE_DELETE.equals(evt.getPropertyName()));
        if (isInstanceDeleted) {
            try {
                manageDeletion(evt);
            } catch (OMFRollBackException | RollbackException2 e){
                UndoManager.getInstance().requestHardUndo();
            }
        }
    }

    @Override
    public void addingListener() {
        OMFUtils.getProject().getRepositoryListenerRegistry().addPropertyChangeListener(this,
                UML2MetamodelConstants.BEFORE_DELETE);
    }

    @Override
    public void removingListener() {
        OMFUtils.getProject().getRepositoryListenerRegistry().removePropertyChangeListener(this,
                UML2MetamodelConstants.BEFORE_DELETE);
    }

}
