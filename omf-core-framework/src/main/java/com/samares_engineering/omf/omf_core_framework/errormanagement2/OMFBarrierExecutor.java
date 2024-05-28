package com.samares_engineering.omf.omf_core_framework.errormanagement2;

import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFDevException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFUnhandledNonSessionFeatureException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.RollbackException2;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFRollBackException;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.listeners.ListenerManager;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import javax.annotation.CheckForNull;
import java.util.concurrent.Callable;

public class OMFBarrierExecutor {


    //------------------------------------------------------------------------------------------------------------------
    //---------------------------------------- Execute Outside Session -------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------


    public static Object executeWithinBarrier(Runnable runnable) {
        return executeWithinBarrier(runnable, "Action performed by the plugin", null);
    }

    public static Object executeWithinBarrier(Runnable runnable, MDFeature feature) {
        return executeWithinBarrier(runnable, feature.getName() + " action", feature);
    }

    public static Object executeWithinBarrier(Runnable runnable, String sessionName, @CheckForNull MDFeature feature) {
        return executeWithinBarrier(runnable, feature, true);
    }

    public static Object executeWithinBarrier(Runnable runnable, @CheckForNull MDFeature feature, boolean deactivateListener) {
        return executeWithinBarrier(() ->{runnable.run(); return null;}, feature, deactivateListener);
    }

    public static <V> V executeWithinBarrier(Callable<V> callable) {
        return executeWithinBarrier(callable, null);
    }

    public static <V> V executeWithinBarrier(Callable<V> callable, @CheckForNull MDFeature feature) {
        return executeWithinBarrier(callable, feature, false);
    }


    public static <V> V executeWithinBarrier(Callable<V> callable, @CheckForNull MDFeature feature, boolean deactivateListener) {
        if (deactivateListener)
            ListenerManager.getInstance().deactivateAllListeners();

        try {
            return callable.call();
        } catch (OMFDevException devException) {
            if(feature != null)
                ErrorHandler2.getInstance().handleException(
                        new OMFUnhandledNonSessionFeatureException(
                                feature,
                                devException,
                                devException.getModifiers()),
                        feature);
            else
                ErrorHandler2.getInstance().handleException( new OMFUnhandledNonSessionFeatureException(
                                devException,
                                devException.getModifiers()));
        } catch (Exception uncauchtException) {
            if(feature != null)
                ErrorHandler2.getInstance().handleException(
                        new OMFUnhandledNonSessionFeatureException(
                                feature,
                                uncauchtException),
                        feature);
            else
                ErrorHandler2.getInstance().handleException( new OMFUnhandledNonSessionFeatureException(uncauchtException));
        }
       return null;
    }


    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------- Execute In Session ---------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------


    public static Object executeInSessionWithinBarrier(Runnable runnable) {
        return executeInSessionWithinBarrier(runnable, "Action performed by the plugin", null);
    }

    public static Object executeInSessionWithinBarrier(Runnable runnable, MDFeature feature) {
        return executeInSessionWithinBarrier(runnable, feature.getName() + " action", feature);
    }

    public static Object executeInSessionWithinBarrier(Runnable runnable, String sessionName, @CheckForNull MDFeature feature) {
        return executeInSessionWithinBarrier(runnable, sessionName, feature, true);
    }

    public static Object executeInSessionWithinBarrier(Runnable runnable, String sessionName, @CheckForNull MDFeature feature, boolean deactivateListener) {
        return executeInSessionWithinBarrier(() ->{runnable.run(); return null;}, sessionName, feature, deactivateListener);
    }



    public static <V> V executeInSessionWithinBarrier(Callable<V> callable) {
        return executeInSessionWithinBarrier(callable, "Action performed by the plugin", null);
    }

    public static <V> V executeInSessionWithinBarrier(Callable<V> callable, MDFeature feature) {
        return executeInSessionWithinBarrier(callable, feature.getName() + " action", feature);
    }

    public static <V> V executeInSessionWithinBarrier(Callable<V> callable, String sessionName, @CheckForNull MDFeature feature) {
        return executeInSessionWithinBarrier(callable, sessionName, feature, true);
    }


    public static <V> V executeInSessionWithinBarrier(Callable<V> callable, String sessionName, @CheckForNull MDFeature feature, boolean deactivateListener) {
        if (deactivateListener)
            ListenerManager.getInstance().deactivateAllListeners();
        try {
            return SessionManager.getInstance().callInsideSession(OMFUtils.getProject(), sessionName, () -> {
                try {
                    return callable.call();
                } catch (OMFDevException e) {
                    if(feature != null)
                        ErrorHandler2.getInstance().handleException(e, feature); //Could Throw a RollbackException2
                    else
                        ErrorHandler2.getInstance().handleException(e); //Could Throw a RollbackException2
                } catch (Exception e) {
                    if(feature != null)
                        ErrorHandler2.getInstance().handleException(e, feature); //Could Throw a RollbackException2
                    else
                        ErrorHandler2.getInstance().handleException(e); //Could Throw a RollbackException2
                }
                return null;
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
        return null;
    }
    
}
