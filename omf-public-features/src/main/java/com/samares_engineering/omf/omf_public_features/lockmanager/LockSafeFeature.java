/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.lockmanager;

import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
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
    public void onMagicdrawStartup() {
        LockerManagerOptionHelper envOptionsHelper = (LockerManagerOptionHelper) getEnvOptionsHelper();
        restrictedElementListener.setActivated(envOptionsHelper.isLockManagerEnabled());
        restrictedElementListener.setRollBackEnabling(envOptionsHelper.isRollbackAutoEnabled());
    }

    @Override
    public void onUnregistering() {
        super.onUnregistering();
    }

    @Override
    public void onProjectOpen() {
        super.onProjectOpen();
        getPlugin().getListenerManager().addCoreListener(restrictedElementListener);
    }

    @Override
    public void onProjectClose() {
        super.onUnregistering();
        getPlugin().getListenerManager().removeCoreListener(restrictedElementListener);
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return new LockerManagerOptionHelper(this);
    }


    @Override
    public List<IOption> initOptions() {
        LockerManagerOptionHelper envOptionsHelper = (LockerManagerOptionHelper) getEnvOptionsHelper();
        OptionImpl twcSafeModeOption = envOptionsHelper.twcSafeModeOption(restrictedElementListener);
        OptionImpl rollbackOption = envOptionsHelper.rollbackOption(restrictedElementListener);
        return Arrays.asList(twcSafeModeOption, rollbackOption);
    }

}
