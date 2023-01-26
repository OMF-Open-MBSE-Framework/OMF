package com.samares.omf.plugin.features.organizer;

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
import com.samares.omf.plugin.features.organizer.actions.RefreshRulesBasedOnConfigFiles;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;

import java.util.List;

public class Organizer extends AFeature {
    public Organizer() {
        super("Organizer");
    }

    @Override
    public List<AGenericAction> initFeatureActions() {
        return List.of(
                new RefreshRulesBasedOnConfigFiles()
        );
    }

    @Override
    public List<IFeatureRuleEngine> initLiveActions() {
        IFeatureRuleEngine creationRE = new FeatureRuleEngine(RECategoryEnum.CREATE);
        // We delegate management of rules to OrganizeListenerConfig
        OrganizerRuleUpdater.getInstance().setOrganizerRuleEngine(creationRE);
        OrganizerRuleUpdater.getInstance().initAllRulesBasedOnConfigFiles();
        return List.of(creationRE);
    }

    @Override
    public List<IOption> initOptions() {
        // Organizer
        StringProperty organizerConfigFilePathProp = new StringProperty(OMFPluginEnvOptionsGroup.ORGANIZER_CONFIG_FILE_PATH_ID,
                OMFPluginEnvOptionsGroup.getOrganizerPathListenerConfigurationDefaultValue());
        var organizerConfigFilePath = new OptionImpl(
                organizerConfigFilePathProp,
                OMFPluginEnvOptionsGroup.OWNER_CONFIGURATION_GROUP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );


        var isActivatedProperty = new BooleanProperty(OMFPluginEnvOptionsGroup.ORGANIZER_ACTIVATION_ID, true);
        var organizerActivation = new OptionImpl(
                isActivatedProperty,
                OMFPluginEnvOptionsGroup.OWNER_CONFIGURATION_GROUP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );

        // Type to instance
        StringProperty t2IConfigFilePathProp = new StringProperty(OMFPluginEnvOptionsGroup.T2I_CONFIG_FILE_PATH_ID,
                OMFPluginEnvOptionsGroup.getT2IPathListenerConfigurationDefaultValue());
        var t2iConfigFilePath = new OptionImpl(
                t2IConfigFilePathProp,
                OMFPluginEnvOptionsGroup.CONFIGURATION_GROUP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );


        var t2IActivation = new OptionImpl(
                new BooleanProperty(OMFPluginEnvOptionsGroup.T2I_ACTIVATION_ID, true),
                OMFPluginEnvOptionsGroup.CONFIGURATION_GROUP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );

        // Instance to type
        StringProperty i2TConfigFilePathProp = new StringProperty(OMFPluginEnvOptionsGroup.I2T_CONFIG_FILE_PATH_ID,
                OMFPluginEnvOptionsGroup.getI2TPathListenerConfigurationDefaultValue());
        var i2TConfigFilePath = new OptionImpl(
                i2TConfigFilePathProp,
                OMFPluginEnvOptionsGroup.CONFIGURATION_GROUP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );


        var i2TActivation = new OptionImpl(
                new BooleanProperty(OMFPluginEnvOptionsGroup.I2T_ACTIVATION_ID, true),
                OMFPluginEnvOptionsGroup.CONFIGURATION_GROUP,
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
