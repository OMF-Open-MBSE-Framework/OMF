/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.errors.cancelsession;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.notification.Notification;
import com.nomagic.magicdraw.ui.notification.NotificationManager;
import com.nomagic.magicdraw.ui.notification.NotificationSeverity;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFNotificationManager;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.SysoutColorPrinter;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

/**
 * Encapsulate all undo redo management
 */
public class UndoManager {

    private Thread thread;
    private static UndoManager instance;

    public static UndoManager getInstance() {
        if (instance == null)
            instance = new UndoManager();
        return instance;
    }

    public UndoManager() {

    }

    /**
     * Will wait for the Session to be closed before triggering Undo.
     * This could be useful to "cancel" an action performed in a session that does not belong to you.
     * NOTE: Undo will be executed inside the OMFUtils.getProject()
     */
    public void requestRedo(){
        requestRedo(OMFUtils.getProject());
    }

    /**
     * Will wait for the Session to be closed before triggering Undo.
     * This could be useful to "cancel" an action performed in a session that does not belong to you.
     *
     * @param project project where the undo shall be processed
     */
    private void requestRedo(Project project) {
        startThead(() -> redoOnSessionClosure(project));
    }
    public void requestUndo(){
        requestUndo(OMFUtils.getProject());
    }

    private void requestUndo(Project project) {
        startThead(() -> undoOnSessionClosure(project));
    }

    /**
     * Will wait for the Session to be closed before triggering Undo.
     * It will also prevent the User to redo the canceled action.
     * This could be useful to "cancel" an action performed in a session that does not belong to you.
     * NOTE: Undo will be executed inside the OMFUtils.getProject() .
     * NOTE 2: the redo will still be available but will not do anything.
     */
    public void requestHardUndo() {
        requestHardUndo(OMFUtils.getProject());
    }

    /**
     * Will wait for the Session to be closed before triggering Undo.
     * It will also prevent the User to redo the canceled action.
     * This could be useful to "cancel" an action performed in a session that does not belong to you.
     * NOTE 1: the redo will still be available but will not do anything.
     *
     * @param project where the undo shall be processed
     */
    private void requestHardUndo(Project project) {
        startThead(() -> {
            undoOnSessionClosure(project);
            UndoManager.deactivateFirstRedo();
        });
    }

    /**
     * a Thread is created waiting for the session to be closed before triggering the UNDO.
     *
     * @param project
     */
    private void undoOnSessionClosure(Project project) {
        int i = 0;
        while (SessionManager.getInstance().isSessionCreated(project)) {
            if (i++ > 100) {//avoid infinite loop
                LegacyErrorHandler.handleException(new LegacyOMFException("[UNDO] Undo request did not succeed in time, please undo the action manually", GenericException.ECriticality.CRITICAL), false);
                return;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        SysoutColorPrinter.success("CANCELED!!");
        undo();

    }

    /**
     * a Thread is created waiting for the session to be closed before triggering the REDO.
     *
     * @param project project where the undo shall be processed
     */
    private void redoOnSessionClosure(Project project) {
        int i = 0;
        while (SessionManager.getInstance().isSessionCreated(project)) {
            if (i++ > 100) {//avoid infinite loop
                LegacyErrorHandler.handleException(new LegacyOMFException("[UNDO] Undo request did not succeed in time, please undo the action manually", GenericException.ECriticality.CRITICAL), false);
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        SysoutColorPrinter.success("CANCELED!!");
        undo();
    }

    /**
     * Performing directly the undo on the current project
     */
    public static void undo() {
        undo(OMFUtils.getProject());
    }

    /**
     * Performing directly the undo on the given project
     * @param project project where the undo shall be processed
     */
    public static void undo(Project project) {
        project.getCommandHistory().undo();
        if (project.getCommandHistory().getCommandForRedo() == null) // UNDO COMMAND WAS NOT PROCESSED CORRECTLY..
            OMFNotificationManager.getInstance().showNotification(new Notification(
                    " [LOCK/AUTO UNDO]",
                    "AUTO UNDO Couldn't be processed please execute UNDO manually ",
                    "AUTO UNDO Couldn't be processed please execute UNDO manually ",
                    NotificationSeverity.ERROR));
    }

    /**
     * Performing directly the redo on the current project
     */
    public static void redo() {
        redo(OMFUtils.getProject());
    }

    /**
     * Performing directly the redo on the given project
     *
     * @param project project where the redo shall be processed
     */
    public static void redo(Project project) {
        project.getCommandHistory().redo();
    }

    /**
     * Deactivate the first redo command actions for the current project, it will still be available for the user but will not do anything
     */
    public static void deactivateFirstRedo(){
        deactivateFirstRedo(OMFUtils.getProject());
    }

    /**
     * Deactivate the first redo command actions for the given project, it will still be available for the user but will not do anything
     @param project project where the redo shall be processed
     */
    public static void deactivateFirstRedo(Project project) {
        if (project.getCommandHistory().getCommandForRedo() != null)
            project.getCommandHistory().getCommandForRedo().clearCommands();
    }


    //********************************************************//

    /**
     * Start a new Thread executing the given runnable. Only one thread managing the UNDO/REDO can be running at a time.
     *
     * @param runnable
     */
    private void startThead(Runnable runnable) {
        boolean isThreadStillAlive = thread != null && thread.isAlive();
        if (isThreadStillAlive) {
            SysoutColorPrinter.warn("[DO/UNDO] Thread interrupted");
            thread.interrupt();
        }

        thread = new Thread(runnable);
        thread.start();
    }


}
