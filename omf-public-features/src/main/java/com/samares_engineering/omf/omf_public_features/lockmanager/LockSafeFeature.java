/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.lockmanager;

import com.nomagic.magicdraw.core.Application;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.OMFFeatureException;
import com.samares_engineering.omf.omf_core_framework.feature.AFeature;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.RestrictedElementCheckerListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Feature that ensure that the model is not modified by OMF based automation.
 * Checks are made on the lock status of the elements and the readOnly status of the elements.
 * If the elements are restricted and modified an error will be thrown, and rollback will be performed.
 * This feature is activated by default, and can be deactivated from the options, same for the rollback.
 */
public class LockSafeFeature extends AFeature {

    private RestrictedElementCheckerListener restrictedElementListener;

    public LockSafeFeature() {
        super("LockManager Feature");
        restrictedElementListener = new RestrictedElementCheckerListener();
    }

    @Override
    protected void onMagicdrawStartup() {
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
    public List<UIAction> initFeatureActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IRuleEngine> initLiveActions() {
        return Collections.emptyList();
    }

    @Override
    protected List<IRuleEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IOption> initOptions() {
        LockerManagerOptionHelper envOptionsHelper = (LockerManagerOptionHelper) getEnvOptionsHelper();
        OptionImpl twcSafeModeOption = envOptionsHelper.twcSafeModeOption(restrictedElementListener);
        OptionImpl rollbackOption = envOptionsHelper.rollbackOption(restrictedElementListener);
        return Arrays.asList(twcSafeModeOption, rollbackOption);
    }

    @Override
    protected List<IOption> initProjectOnlyOptions() {
        return Collections.emptyList();
    }
}
