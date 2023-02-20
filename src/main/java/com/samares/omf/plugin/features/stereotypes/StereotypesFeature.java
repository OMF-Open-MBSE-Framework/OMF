/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.features.stereotypes;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.StringProperty;
import com.samares.omf.core.feature.AFeature;
import com.samares.omf.core.feature.registrables.actions.actions.IUIAction;
import com.samares.omf.core.feature.registrables.options.option.IOption;
import com.samares.omf.core.feature.registrables.options.option.OptionImpl;
import com.samares.omf.core.feature.registrables.options.option.OptionKind;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.FeatureRuleEngine;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.IFeatureRuleEngine;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares.omf.plugin.features.stereotypes.actions.RefreshStereotypesRulesBasedOnConfigFiles;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StereotypesFeature extends AFeature {
    public static final String FEATURE_NAME = "Stereotypes";
    public StereotypesFeature() {
        super(FEATURE_NAME);
    }
    private final StereotypesRuleUpdater ruleUpdater = new StereotypesRuleUpdater();

    @Override
    public List<IUIAction> initFeatureActions() {
        return Arrays.asList(
                new RefreshStereotypesRulesBasedOnConfigFiles()
        );
    }

    @Override
    protected List<IUIAction> initProjectOnlyFeatureActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IFeatureRuleEngine> initLiveActions() {
        IFeatureRuleEngine creationRE = new FeatureRuleEngine(RECategoryEnum.CREATE);
        return Arrays.asList(creationRE);
    }

    @Override
    protected List<IFeatureRuleEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
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
        StringProperty instanceConfigFilePathProp = new StringProperty(OMFPluginEnvOptionsGroup.INSTANCE_CONFIG_FILE_PATH_ID,
                OMFPluginEnvOptionsGroup.getInstanceConfigFilePathDefaultValue());
        var t2iConfigFilePath = new OptionImpl(
                instanceConfigFilePathProp,
                OMFPluginEnvOptionsGroup.INSTANCE_CONFIG_GRP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );


        var instanceActivation = new OptionImpl(
                new BooleanProperty(OMFPluginEnvOptionsGroup.INSTANCE_ACTIVATION_ID, true),
                OMFPluginEnvOptionsGroup.INSTANCE_CONFIG_GRP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );

        // Instance to type
        StringProperty typeConfigFilePathProp = new StringProperty(OMFPluginEnvOptionsGroup.TYPE_CONFIG_FILE_PATH_ID,
                OMFPluginEnvOptionsGroup.getTypeConfigFilePathDefaultValue());
        var typeConfigFilePath = new OptionImpl(
                typeConfigFilePathProp,
                OMFPluginEnvOptionsGroup.TYPE_CONFIG_GRP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );


        var typeActivation = new OptionImpl(
                new BooleanProperty(OMFPluginEnvOptionsGroup.TYPE_ACTIVATION_ID, true),
                OMFPluginEnvOptionsGroup.TYPE_CONFIG_GRP,
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );

        return List.of(
                organizerConfigFilePath,
                organizerActivation,
                t2iConfigFilePath,
                instanceActivation,
                typeConfigFilePath,
                typeActivation
        );
    }

    @Override
    protected List<IOption> initProjectOnlyOptions() {
        return Collections.emptyList();
    }

    @Override
    public void onProjectOpen() {
        // We delegate management of rules to OrganizeListenerConfig
        ruleUpdater.setOrganizerRuleEngine(getRuleEngines().get(0));
        ruleUpdater.initAllRulesBasedOnConfigFiles();
    }

    public StereotypesRuleUpdater getRuleUpdater() {
        return ruleUpdater;
    }
}
