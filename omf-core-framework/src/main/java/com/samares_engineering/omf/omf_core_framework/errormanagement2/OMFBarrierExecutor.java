package com.samares_engineering.omf.omf_core_framework.errormanagement2;

import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFLogException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.RollbackException;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
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

    public static Object executeWithinBarrier(Runnable runnable, OMFFeature feature) {
        return executeWithinBarrier(runnable, feature.getName() + " action", feature);
    }

    public static Object executeWithinBarrier(Runnable runnable, String sessionName, @CheckForNull OMFFeature feature) {
        return executeWithinBarrier(runnable, feature, true);
    }

    public static Object executeWithinBarrier(Runnable runnable, @CheckForNull OMFFeature feature, boolean deactivateListener) {
        return executeWithinBarrier(() ->{runnable.run(); return null;}, feature, deactivateListener);
    }

    public static <V> V executeWithinBarrier(Callable<V> callable) {
        return executeWithinBarrier(callable, null);
    }

    public static <V> V executeWithinBarrier(Callable<V> callable, @CheckForNull OMFFeature feature) {
        return executeWithinBarrier(callable, feature, false);
    }


    public static <V> V executeWithinBarrier(Callable<V> callable, @CheckForNull OMFFeature feature, boolean deactivateListener) {
        if (deactivateListener)
            ListenerManager.getInstance().deactivateAllListeners();
        try {
            return callable.call();
        } catch (OMFLogException omfLogException) {
            if(feature != null)
                OMFErrorHandler.getInstance().handleException(omfLogException, feature);
            else
                OMFErrorHandler.getInstance().handleException(omfLogException);
        } catch (Exception uncaughtException) {
            if(feature != null)
                OMFErrorHandler.getInstance().handleException(uncaughtException, feature);
            else
                OMFErrorHandler.getInstance().handleException(uncaughtException);
        } finally {
            ListenerManager.getInstance().activateAllListeners();
        }
       return null;
    }


    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------- Execute In Session ---------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    public static Object executeInSessionWithinBarrier(Runnable runnable) {
        return executeInSessionWithinBarrier(runnable, "Action performed by the plugin", null);
    }

    public static Object executeInSessionWithinBarrier(Runnable runnable, OMFFeature feature) {
        return executeInSessionWithinBarrier(runnable, feature.getName() + " action", feature);
    }

    public static Object executeInSessionWithinBarrier(Runnable runnable, String sessionName, @CheckForNull OMFFeature feature) {
        return executeInSessionWithinBarrier(runnable, sessionName, feature, true);
    }

    public static Object executeInSessionWithinBarrier(Runnable runnable, String sessionName, @CheckForNull OMFFeature feature, boolean deactivateListener) {
        return executeInSessionWithinBarrier(() ->{runnable.run(); return null;}, sessionName, feature, deactivateListener);
    }

    public static <V> V executeInSessionWithinBarrier(Callable<V> callable) {
        return executeInSessionWithinBarrier(callable, "Action performed by the plugin", null);
    }

    public static <V> V executeInSessionWithinBarrier(Callable<V> callable, OMFFeature feature) {
        return executeInSessionWithinBarrier(callable, feature.getName() + " action", feature);
    }

    public static <V> V executeInSessionWithinBarrier(Callable<V> callable, OMFFeature feature, boolean deactivateListener) {
        return executeInSessionWithinBarrier(callable, feature.getName() + " action", feature, deactivateListener);
    }

    public static <V> V executeInSessionWithinBarrier(Callable<V> callable, String sessionName, @CheckForNull OMFFeature feature) {
        return executeInSessionWithinBarrier(callable, sessionName, feature, true);
    }

    public static <V> V executeInSessionWithinBarrier(Callable<V> callable, String sessionName, @CheckForNull OMFFeature feature, boolean deactivateListeners) {
        if (deactivateListeners)
            ListenerManager.getInstance().deactivateAllListeners();
        try {
            return SessionManager.getInstance().callInsideSession(OMFUtils.getProject(), sessionName, () -> {
                try {
                    return callable.call();
                } catch (OMFLogException e) {
                    if(feature != null)
                        OMFErrorHandler.getInstance().handleException(e, feature); //Could Throw a RollbackException2
                    else
                        OMFErrorHandler.getInstance().handleException(e); //Could Throw a RollbackException2
                } catch (Exception e) {
                    if(feature != null)
                        OMFErrorHandler.getInstance().handleException(e, feature); //Could Throw a RollbackException2
                    else
                        OMFErrorHandler.getInstance().handleException(e); //Could Throw a RollbackException2
                } catch (StackOverflowError e) {
                    if(feature != null)
                        OMFErrorHandler.getInstance().handleException(e, feature);
                    else
                        OMFErrorHandler.getInstance().handleException(e);
                }
                return null;
            });
        } catch (RollbackException rollbackException){ // 2021x, error handling system
            OMFErrorHandler.getInstance().handleException(rollbackException);
        }catch (Exception uncaughtException){
            Throwable cause = uncaughtException.getCause();
            if(cause instanceof RollbackException) // 2022x, error handling system
                OMFErrorHandler.getInstance().handleException((RollbackException) cause);
            else
                OMFErrorHandler.getInstance().handleException(new CoreException2("[Core] Exception dodged the framework exception handling", uncaughtException));
        }
        return null;
    }
}
