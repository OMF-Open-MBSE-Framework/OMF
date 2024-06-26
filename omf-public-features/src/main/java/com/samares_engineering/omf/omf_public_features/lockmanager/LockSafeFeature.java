/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.lockmanager;

import com.nomagic.magicdraw.core.Project;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.BaseHookFeatureItem;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.OnMagicDrawStartHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.OnProjectClosedHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.OnProjectOpenedHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.RestrictedElementCheckerListener;

import java.util.Arrays;
import java.util.List;

/**
 * Feature that ensure that the model is not modified by OMF based automation.
 * Checks are made on the lock status of the elements and the readOnly status of the elements.
 * If the elements are restricted and modified an error will be thrown, and rollback will be performed.
 * This feature is activated by default, and can be deactivated from the options, same for the rollback.
 */
public class LockSafeFeature extends SimpleFeature {

    private final RestrictedElementCheckerListener restrictedElementListener;

    public LockSafeFeature() {
        super("LockManager Feature");
        restrictedElementListener = new RestrictedElementCheckerListener();
    }
    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return new LockerManagerOptionHelper(this);
    }

    @Override
    protected List<Hook> initLifeCycleHooks() {
        return List.of(new FeatureConfigurationHooks(this));
    }



    @Override
    public List<Option> initOptions() {
        LockerManagerOptionHelper envOptionsHelper = (LockerManagerOptionHelper) getEnvOptionsHelper();
        OptionImpl twcSafeModeOption = envOptionsHelper.twcSafeModeOption(restrictedElementListener);
        OptionImpl rollbackOption = envOptionsHelper.rollbackOption(restrictedElementListener);
        return Arrays.asList(twcSafeModeOption, rollbackOption);
    }

    /**
     * Hooks to register for the feature to configure the feature on project open and close and MagicDraw start.
     */
    private static class FeatureConfigurationHooks extends BaseHookFeatureItem implements
            OnProjectOpenedHook,
            OnProjectClosedHook,
            OnMagicDrawStartHook {

        private final LockSafeFeature lockSafeFeature;

        public FeatureConfigurationHooks(LockSafeFeature feature) {
            super();
            this.lockSafeFeature = feature;
        }

        @Override
        public void onMagicDrawStart() {
            LockerManagerOptionHelper envOptionsHelper = (LockerManagerOptionHelper) lockSafeFeature.getEnvOptionsHelper();
            lockSafeFeature.restrictedElementListener.setActivated(envOptionsHelper.isLockManagerEnabled());
            lockSafeFeature.restrictedElementListener.setRollBackEnabling(envOptionsHelper.isRollbackAutoEnabled());

        }

        @Override
        public void onProjectClosed(Project project) {
            getPlugin().getListenerManager().removeCoreListener(lockSafeFeature.restrictedElementListener);

        }

        @Override
        public void onProjectOpened(Project project) {
            getPlugin().getListenerManager().addCoreListener(lockSafeFeature.restrictedElementListener);
        }
    }


}
