/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.stereotypes;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.StringProperty;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.AOnProjectOpenedHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.ALiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;
import com.samares_engineering.omf.omf_public_features.stereotypes.actions.RefreshStereotypesRulesBasedOnConfigFiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StereotypesFeature extends SimpleFeature {
    public static final String FEATURE_NAME = "Stereotypes";
    private final StereotypesRuleUpdater ruleUpdater;
    private final char delimiter;

    public StereotypesFeature() {
        this(';');
    }
    public StereotypesFeature(char delimiter) {
        this(FEATURE_NAME, delimiter);
    }
    public StereotypesFeature(String featureName, char delimiter) {
        super(featureName);
        this.delimiter = delimiter;
        ruleUpdater = new StereotypesRuleUpdater(this, delimiter);
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
    public List<UIAction> initFeatureActions() {
        return Arrays.asList(
                new RefreshStereotypesRulesBasedOnConfigFiles()
        );
    }

    @Override
    public List<LiveActionEngine> initLiveActions() {
        var creationRE = new ALiveActionEngine(LiveActionType.CREATE);
        return Arrays.asList(creationRE);
    }

    @Override
    public List<Option> initOptions() {
        List<Option> options = new ArrayList<>();

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
    protected List<Hook> initLifeCycleHooks() {
        return List.of(new AOnProjectOpenedHook() {
            @Override
            public void onProjectOpened(Project project) {
                // We delegate management of rules to OrganizeListenerConfig
                ruleUpdater.setOrganizerRuleEngine(getLiveActionEngines().get(0));
                ruleUpdater.initAllRulesBasedOnConfigFiles();
            }
        });
    }

    public StereotypesRuleUpdater getRuleUpdater() {
        return ruleUpdater;
    }

    public char getDelimiter() {
        return delimiter;
    }
}
