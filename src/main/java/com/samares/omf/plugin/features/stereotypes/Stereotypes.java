package com.samares.omf.plugin.features.stereotypes;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.StringProperty;
import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.feature.AFeature;
import com.samares.omf.core.feature.options.IOption;
import com.samares.omf.core.feature.options.OptionImpl;
import com.samares.omf.core.feature.options.OptionKind;
import com.samares.omf.core.feature.ruleengine.FeatureRuleEngine;
import com.samares.omf.core.feature.ruleengine.IFeatureRuleEngine;
import com.samares.omf.core.feature.ruleengine.RECategoryEnum;
import com.samares.omf.plugin.features.stereotypes.actions.RefreshStereotypesRulesBasedOnConfigFiles;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;

import java.util.List;

public class Stereotypes extends AFeature {
    public Stereotypes() {
        super("Organizer");
    }

    @Override
    public List<AGenericAction> initFeatureActions() {
        return List.of(
                new RefreshStereotypesRulesBasedOnConfigFiles()
        );
    }

    @Override
    public List<IFeatureRuleEngine> initLiveActions() {
        IFeatureRuleEngine creationRE = new FeatureRuleEngine(RECategoryEnum.CREATE);
        // We delegate management of rules to OrganizeListenerConfig
        StereotypesRuleUpdater.getInstance().setOrganizerRuleEngine(creationRE);
        StereotypesRuleUpdater.getInstance().initAllRulesBasedOnConfigFiles();
        return List.of(creationRE);
    }

    @Override
    public List<IOption> initOptions() {
        // Organizer
        StringProperty organizerConfigFilePathProp = new StringProperty(OMFPluginEnvOptionsGroup.ORGANIZER_CONFIG_FILE_PATH_ID,
                OMFPluginEnvOptionsGroup.getOrganizerConfigFilePathDefaultValue());
        var organizerConfigFilePath = new OptionImpl(
                organizerConfigFilePathProp,
                OMFPluginEnvOptionsGroup.ORGANIZER_CONFIG_GRP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );


        var isActivatedProperty = new BooleanProperty(OMFPluginEnvOptionsGroup.ORGANIZER_ACTIVATION_ID, true);
        var organizerActivation = new OptionImpl(
                isActivatedProperty,
                OMFPluginEnvOptionsGroup.ORGANIZER_CONFIG_GRP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );

        // Type to instance
        StringProperty t2IConfigFilePathProp = new StringProperty(OMFPluginEnvOptionsGroup.T2I_CONFIG_FILE_PATH_ID,
                OMFPluginEnvOptionsGroup.getT2IConfigFilePathDefaultValue());
        var t2iConfigFilePath = new OptionImpl(
                t2IConfigFilePathProp,
                OMFPluginEnvOptionsGroup.T2I_CONFIG_GRP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );


        var t2IActivation = new OptionImpl(
                new BooleanProperty(OMFPluginEnvOptionsGroup.T2I_ACTIVATION_ID, true),
                OMFPluginEnvOptionsGroup.T2I_CONFIG_GRP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );

        // Instance to type
        StringProperty i2TConfigFilePathProp = new StringProperty(OMFPluginEnvOptionsGroup.I2T_CONFIG_FILE_PATH_ID,
                OMFPluginEnvOptionsGroup.getI2TConfigFilePathDefaultValue());
        var i2TConfigFilePath = new OptionImpl(
                i2TConfigFilePathProp,
                OMFPluginEnvOptionsGroup.I2T_CONFIG_GRP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );


        var i2TActivation = new OptionImpl(
                new BooleanProperty(OMFPluginEnvOptionsGroup.I2T_ACTIVATION_ID, true),
                OMFPluginEnvOptionsGroup.I2T_CONFIG_GRP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );

        return List.of(
                organizerConfigFilePath,
                organizerActivation,
                t2iConfigFilePath,
                t2IActivation,
                i2TConfigFilePath,
                i2TActivation
        );
    }
}
