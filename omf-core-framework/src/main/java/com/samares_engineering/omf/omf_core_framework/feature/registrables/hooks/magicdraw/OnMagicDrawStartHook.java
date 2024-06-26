package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw;

public interface OnMagicDrawStartHook extends MagicdrawLifeCycleHook {

    /**
     * NOTE: This method is called by the framework, do not call it directly or override it.
     * This method is used to trigger the hook when magicdraw is started.
     */
    default void triggerOnMagicDrawStartHook() {
        executeHook(this::onMagicDrawStart, "onMagicDrawStart");
    }

    /**
     * This method is called when magicdraw is started.<br>
     * Developers should implement this method to add custom logic.
     */
     void onMagicDrawStart();
}
