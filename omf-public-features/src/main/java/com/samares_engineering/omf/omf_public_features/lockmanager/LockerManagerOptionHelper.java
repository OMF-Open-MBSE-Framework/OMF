package com.samares_engineering.omf.omf_public_features.lockmanager;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.RestrictedElementCheckerListener;

import java.util.List;

public class LockerManagerOptionHelper extends EnvOptionsHelper {

    public static final String LOCK_MANAGER_ENABLED = "Activate TWC Automation Safe mode :";
    static final String LOCKER_MANAGER_ENABLED_DESC = "If true, the TWC Automation Safe mode will be activated." +
            " This mode will prevent any OMF based automation" +
            " from modifying the model on readOnly or locked elements.";
    static final String ROLLBACK_AUTO_ENABLED = "Activate rollback auto when modifying restricted elements :";
    static final String ROLLBACK_AUTO_ENABLED_DESC = "If true, the rollback will be performed automatically" +
                    " when an error is thrown by the LockManager.";

    protected LockerManagerOptionHelper(OMFFeature feature) {
        super(feature);
    }

    public OptionImpl rollbackOption(RestrictedElementCheckerListener restrictedElementListener) {
        BooleanProperty rollbackWhenFailing = new BooleanProperty(LockerManagerOptionHelper.ROLLBACK_AUTO_ENABLED, true);
        rollbackWhenFailing.setDescription(LockerManagerOptionHelper.ROLLBACK_AUTO_ENABLED_DESC);
        OptionImpl rollbackOption = new OptionImpl(
                rollbackWhenFailing,
                "OMF Features",
                getFeature().getPlugin().getEnvironmentOptionsGroup()
                        .orElseThrow(() -> new FeatureRegisteringException(
                                "No environment options groups have been declared for this plugin")),
                OptionKind.Environment
        );

        rollbackOption.addListenerToRegister(new AOptionListener() {
            @Override
            public void updateByEnvironmentProperties(List<Property> list) {
                super.updateByEnvironmentProperties(list);
                list.stream()
                    .filter(option -> option.getName().equals(LockerManagerOptionHelper.ROLLBACK_AUTO_ENABLED))
                    .findFirst()
                    .ifPresent(option -> restrictedElementListener.setRollBackEnabling((Boolean) option.getValue()));
            }
        });

        return rollbackOption;
    }

    public OptionImpl twcSafeModeOption(RestrictedElementCheckerListener restrictedElementListener) {
        BooleanProperty twcSafeMode = new BooleanProperty(LockerManagerOptionHelper.LOCK_MANAGER_ENABLED, true);

        twcSafeMode.setDescription(LockerManagerOptionHelper.LOCKER_MANAGER_ENABLED_DESC);
        OptionImpl twcSafeModeOption = new OptionImpl(
                twcSafeMode,
                "OMF Features",
                getFeature().getPlugin().getEnvironmentOptionsGroup()
                        .orElseThrow(() -> new FeatureRegisteringException(
                                "No environment options groups have been declared for this plugin")),
                OptionKind.Environment
        );


        twcSafeModeOption.addListenerToRegister(new AOptionListener() {
            @Override
            public void updateByEnvironmentProperties(List<Property> list) {
                super.updateByEnvironmentProperties(list);
                list.stream()
                    .filter(option -> option.getName().equals(LockerManagerOptionHelper.LOCK_MANAGER_ENABLED))
                    .findFirst()
                    .ifPresent(option -> restrictedElementListener.setActivated((Boolean) option.getValue()));
            }
        });
        return twcSafeModeOption;
    }

    public boolean isLockManagerEnabled() {
        return (boolean) getPropertyByName(LOCK_MANAGER_ENABLED).getValue();
    }
    public boolean isRollbackAutoEnabled() {
        return (boolean) getPropertyByName(ROLLBACK_AUTO_ENABLED).getValue();
    }
}
