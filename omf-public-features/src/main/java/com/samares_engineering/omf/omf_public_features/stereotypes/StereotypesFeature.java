/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.stereotypes;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.StringProperty;
import com.samares_engineering.omf.omf_core_framework.feature.AFeature;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.IUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RuleEngine;
import com.samares_engineering.omf.omf_public_features.stereotypes.actions.RefreshStereotypesRulesBasedOnConfigFiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StereotypesFeature extends AFeature {
    public static final String FEATURE_NAME = "Stereotypes";
    private final StereotypesRuleUpdater ruleUpdater = new StereotypesRuleUpdater(this);

    public StereotypesFeature() {
        super(FEATURE_NAME);
    }

    /**
     * @return Feature's env option helper cast to the correct subtype
     */
    public StereotypesEnvOptionsHelper getOptionsHelper() {
        return (StereotypesEnvOptionsHelper) getEnvOptionsHelper();
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return new StereotypesEnvOptionsHelper(this);
    }

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
    public List<IRuleEngine> initLiveActions() {
        IRuleEngine creationRE = new RuleEngine(RECategoryEnum.CREATE);
        return Arrays.asList(creationRE);
    }

    @Override
    protected List<IRuleEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IOption> initOptions() {
        List<IOption> options = new ArrayList<>();

        // Organizer
        StringProperty organizerConfigFilePathProp = new StringProperty(StereotypesEnvOptionsHelper.ORGANIZER_CONFIG_FILE_PATH_ID,
                StereotypesEnvOptionsHelper.getOrganizerConfigFilePathDefaultValue());
        options.add(createEnvOption(
                organizerConfigFilePathProp,
                StereotypesEnvOptionsHelper.ORGANIZER_CONFIG_GRP)
        );

        options.add(createEnvOption(
                new BooleanProperty(StereotypesEnvOptionsHelper.ORGANIZER_ACTIVATION_ID, true),
                StereotypesEnvOptionsHelper.ORGANIZER_CONFIG_GRP)
        );

        // Type to instance
        StringProperty instanceConfigFilePathProp = new StringProperty(StereotypesEnvOptionsHelper.INSTANCE_CONFIG_FILE_PATH_ID,
                StereotypesEnvOptionsHelper.getInstanceConfigFilePathDefaultValue());
        options.add(createEnvOption(
                instanceConfigFilePathProp,
                StereotypesEnvOptionsHelper.INSTANCE_CONFIG_GRP)
        );

        options.add(createEnvOption(
                new BooleanProperty(StereotypesEnvOptionsHelper.INSTANCE_ACTIVATION_ID, true),
                StereotypesEnvOptionsHelper.INSTANCE_CONFIG_GRP)
        );

        // Instance to type
        StringProperty typeConfigFilePathProp = new StringProperty(StereotypesEnvOptionsHelper.TYPE_CONFIG_FILE_PATH_ID,
                StereotypesEnvOptionsHelper.getTypeConfigFilePathDefaultValue());
        options.add(createEnvOption(
                typeConfigFilePathProp,
                StereotypesEnvOptionsHelper.TYPE_CONFIG_GRP)
        );

        options.add(createEnvOption(
                new BooleanProperty(StereotypesEnvOptionsHelper.TYPE_ACTIVATION_ID, true),
                StereotypesEnvOptionsHelper.TYPE_CONFIG_GRP)
        );

        return options;
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
