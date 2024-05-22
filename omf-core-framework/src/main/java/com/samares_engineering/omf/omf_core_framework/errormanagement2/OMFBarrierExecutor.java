package com.samares_engineering.omf.omf_core_framework.errormanagement2;

import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFDevException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.RollbackException2;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFRollBackException;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.listeners.ListenerManager;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import javax.annotation.CheckForNull;

public class OMFBarrierExecutor {


    public static void executeAUIActionWithinBarrier(Runnable runnable) {
        executeAUIActionWithinBarrier(runnable, "Action performed by the plugin", null);
    }

    public static void executeAUIActionWithinBarrier(Runnable runnable, MDFeature feature) {
        executeAUIActionWithinBarrier(runnable, feature.getName() + " action", feature);
    }

    public static void executeAUIActionWithinBarrier(Runnable runnable, String sessionName,@CheckForNull MDFeature feature) {
        executeAUIActionWithinBarrier(runnable, sessionName, feature, true);
    }


    public static void executeAUIActionWithinBarrier(Runnable runnable, String sessionName, @CheckForNull MDFeature feature, boolean deactivateListener) {
        if (deactivateListener)
            ListenerManager.getInstance().deactivateAllListeners();
        try {
            SessionManager.getInstance().executeInsideSession(OMFUtils.getProject(), sessionName, () -> {
                try {
                    runnable.run();
                } catch (OMFDevException e) {
                    if(feature != null)
                        ErrorHandler2.getInstance().handleException(e, feature); //Could Throw a RollbackException2
                    else
                        ErrorHandler2.getInstance().handleException(e); //Could Throw a RollbackException2
                } catch (RuntimeException e) {
                    if(feature != null)
                        ErrorHandler2.getInstance().handleException(e, feature); //Could Throw a RollbackException2
                    else
                        ErrorHandler2.getInstance().handleException(e); //Could Throw a RollbackException2
                }
            });
        } catch (OMFRollBackException rollbackException){// 2021x, OMF < 2.0
            OMFErrorHandler.handleException(rollbackException);
        } catch (RollbackException2 rollbackException){ // 2021x, error handling system
            ErrorHandler2.getInstance().handleException(rollbackException);
        }catch (Exception uncaughtException){
            Throwable cause = uncaughtException.getCause();
            if(cause instanceof OMFRollBackException)   // 2022x, OMF < 2.0
                OMFErrorHandler.handleException((OMFRollBackException) cause);
            else if(cause instanceof RollbackException2) // 2022x, error handling system
                ErrorHandler2.getInstance().handleException((RollbackException2) cause);
            else
                ErrorHandler2.getInstance().handleException(new CoreException2("[Core] Exception dodged the framework exception handling", uncaughtException));
        }
    }
}
