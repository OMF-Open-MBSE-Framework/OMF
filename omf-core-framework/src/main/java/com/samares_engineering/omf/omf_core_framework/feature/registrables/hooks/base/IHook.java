package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base;

import com.samares_engineering.omf.omf_core_framework.feature.RegistrableFeatureItem;

public interface IHook extends RegistrableFeatureItem {
    void executeHook(Runnable runnable, String event);

    void executeInSessionHook(Runnable runnable, String event);
}
