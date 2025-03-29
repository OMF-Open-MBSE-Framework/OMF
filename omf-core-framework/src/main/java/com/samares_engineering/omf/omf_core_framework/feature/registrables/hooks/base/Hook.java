package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base;

import com.samares_engineering.omf.omf_core_framework.feature.RegistrableFeatureItem;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;

public interface Hook extends RegistrableFeatureItem {

    void executeHook(Runnable runnable, String event);

    void executeInSessionHook(Runnable runnable, String event, boolean deactivateListener);

    default boolean shallDeactivateListener() {
        return hasDeactivateListenerAnnotation();
    }

    default boolean hasDeactivateListenerAnnotation() {
        return getClass().getAnnotation(DeactivateListener.class) != null;
    }
}
