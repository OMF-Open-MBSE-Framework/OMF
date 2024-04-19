/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.errorexample;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.feature.AFeature;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RuleEngine;
import com.samares_engineering.omf.omf_example_plugin.features.errorexample.actions.CriticalFeatureExampleAction;
import com.samares_engineering.omf.omf_example_plugin.features.errorexample.actions.KotlinUIAction;
import com.samares_engineering.omf.omf_example_plugin.features.errorexample.actions.UIActionErrorExample;
import com.samares_engineering.omf.omf_example_plugin.features.errorexample.creation.LiveActionErrorExample;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ErrorManagementFeatureExample extends SimpleFeature {
    public static String ACTIVATE_ERROR_LIVE_ACTION;
    public static String OMF_ERROR_EXAMPLE;

    public ErrorManagementFeatureExample(){
       super("ERROR MANAGEMENT FEATURE");
    }


    @Override
    public List<UIAction> initFeatureActions() {
        return Arrays.asList(
                new CriticalFeatureExampleAction(),
                new UIActionErrorExample(),
                new KotlinUIAction()
        );

    }

    @Override
    public List<IRuleEngine> initLiveActions() {
        IRuleEngine creationRE = new RuleEngine(RECategoryEnum.CREATE);
        creationRE.addRule(new LiveActionErrorExample());
        return List.of(creationRE);
    }


    @Override
    public List<IOption> initOptions() {
        ACTIVATE_ERROR_LIVE_ACTION = "[TEST ERROR] Activate Live Action:";
        OMF_ERROR_EXAMPLE = "OMF Errors Example";
        OptionImpl testEnvOption = createEnvOption(
                new BooleanProperty(ACTIVATE_ERROR_LIVE_ACTION, false),
                OMF_ERROR_EXAMPLE
        );


        testEnvOption.addListenerToRegister(new AOptionListener() {
            @Override
            public void updateByEnvironmentProperties(List<Property> list) {
                super.updateByEnvironmentProperties(list);
            }

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                super.propertyChange(evt);
            }
        });

        return List.of(
                testEnvOption
        );
    }



}
