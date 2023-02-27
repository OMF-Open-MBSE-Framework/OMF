/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.listeners.listeners;

import com.nomagic.magicdraw.ui.notification.Notification;
import com.nomagic.magicdraw.ui.notification.NotificationManager;
import com.nomagic.magicdraw.ui.notification.NotificationSeverity;
import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.transaction.TransactionCommitListener;
import com.nomagic.uml2.transaction.TransactionManager;
import com.samares_engineering.omf.omf_core_framework.listeners.AElementListener;
import com.samares_engineering.omf.omf_core_framework.listeners.ListenerManager;
import com.samares_engineering.omf.omf_core_framework.utils.LockerManager;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.errors.cancelsession.UndoManager;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFLockException;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.CheckForNull;
import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.stream.Collectors;

public class LockManagerListener extends AElementListener implements TransactionCommitListener {
    public LockManagerListener() {
        super();
        lockExceptions = new ArrayList<>();
    }

    List<OMFLockException> lockExceptions;

    @CheckForNull
    @Override
    public Runnable transactionCommited(Collection<PropertyChangeEvent> collection) {
        try {
            //if listeners is activated then no automation has been triggered
            if (ListenerManager.getInstance().isListenersActivated())
                return null;

            Set<Element> checkedElements = new HashSet<>();

            Map<EVT_TYPE, List<PropertyChangeEvent>> groups = collection.stream()
                    .collect(Collectors.groupingBy(pce -> this.getGroup(pce)));

            boolean hasDeletedEvent = !CollectionUtils.isEmpty(groups.get(EVT_TYPE.DELETE));
            boolean hasUpdatedEvent = !CollectionUtils.isEmpty(groups.get(EVT_TYPE.CREATION));
            boolean hasCreatedEvent = !CollectionUtils.isEmpty(groups.get(EVT_TYPE.UPDATE));
            Collection<? extends OMFLockException> deletions = Collections.emptyList();
            Collection<? extends OMFLockException> creations = Collections.emptyList();
            Collection<? extends OMFLockException> updates = Collections.emptyList();
            if (hasDeletedEvent)
                deletions = LockerManager.getInstance().checkDelete(groups.get(EVT_TYPE.DELETE), checkedElements);
            if (hasUpdatedEvent)
                creations = LockerManager.getInstance().checkCreation(groups.get(EVT_TYPE.CREATION), checkedElements);
            if (hasCreatedEvent)
                updates = LockerManager.getInstance().checkUpdate(groups.get(EVT_TYPE.UPDATE), checkedElements);

            deletions.forEach(e -> e.setUserMessage("D]-" + e.getUserMessage()));
            creations.forEach(e -> e.setUserMessage("C]-" + e.getUserMessage()));
            updates.forEach(e -> e.setUserMessage("U]-" + e.getUserMessage()));

            lockExceptions.addAll(deletions);
            lockExceptions.addAll(creations);
            lockExceptions.addAll(updates);


        } catch (Exception e) {
            OMFErrorHandler.handleException(e, false);
        }

        return null;
    }

    private EVT_TYPE getGroup(PropertyChangeEvent pce) {

        switch (pce.getPropertyName()) {
            case UML2MetamodelConstants.INSTANCE_CREATED:
                return EVT_TYPE.CREATION;

            case UML2MetamodelConstants.INSTANCE_DELETED:
            case UML2MetamodelConstants.BEFORE_DELETE:
                return EVT_TYPE.DELETE;

            default:
                return EVT_TYPE.UPDATE;
        }
    }

    @Override
    public void allTransactionsCommitted() {
        try {

            boolean isThereLockExceptionTriggered = !lockExceptions.isEmpty();

            if (!isThereLockExceptionTriggered)
                return;

            OMFLogger.getInstance().log("[LOCK ERROR] Errors happened during the transaction," +
                    " some element are locked by other, are not locked," +
                    " or are not editable (e.g. project usages access)?", null, OMFLogLevel.ERROR);

            NotificationManager.getInstance().showNotification(new Notification(
                    "[LOCK/Ownership Error]",
                    "[LOCK/Ownership Error]",
                    "[LOCK ERROR] Errors happened during the transaction," +
                            " some element are locked by other, are not locked," +
                            " or are not editable (e.g. project usages access)?",
                    NotificationSeverity.ERROR));

            lockExceptions.stream()
                    .forEach(OMFErrorHandler::handleException);
            lockExceptions.clear();
            UndoManager.getInstance().requestHardUndo();

        } catch (Exception e) {
            OMFErrorHandler.handleException(e, false);
        }
    }

    @Override
    public void addListener() {
        TransactionManager transactionManager = OMFUtils.currentProject.getRepository().getTransactionManager();
        transactionManager.addTransactionCommitListenerForExecute(this);
    }

    @Override
    public void removeListener() {
        final boolean isListenerRemovable = (null != OMFUtils.currentProject);
        if (isListenerRemovable) {
            try {
                OMFUtils.currentProject.getRepository().getTransactionManager().removeTransactionCommitListener(this);
            } catch (Exception e) {
                OMFErrorHandler.handleException(new OMFException("[RemoveListener] unable to unregister this listener", 
                        GenericException.ECriticality.ALERT));
            }
        }
    }

    @Override
    public void manageAfterAutomation(Collection<PropertyChangeEvent> collection) {

    }

    public enum EVT_TYPE {
        CREATION,
        UPDATE,
        DELETE
    }
}
