/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.example1;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.ElementProperty;
import com.nomagic.magicdraw.properties.Property;
import com.nomagic.magicdraw.properties.StringProperty;
import com.samares_engineering.omf.omf_core_framework.feature.AFeature;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.IUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RuleEngine;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;
import com.samares_engineering.omf.omf_example_plugin.features.example1.actions.ExampleMDAction1;
import com.samares_engineering.omf.omf_example_plugin.features.example1.actions.RemoveProjectOptions;
import com.samares_engineering.omf.omf_example_plugin.features.example1.rules.creation.BlockCreation;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExampleFeature1 extends AFeature {

    public ExampleFeature1(){
       super("ExampleFeature1");
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return null;
    }

    @Override
    public List<IUIAction> initFeatureActions() {
        return Arrays.asList(
                new ExampleMDAction1(),
                new RemoveProjectOptions()
        );
    }

    @Override
    protected List<IUIAction> initProjectOnlyFeatureActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IRuleEngine> initLiveActions() {
        IRuleEngine creationRE = new RuleEngine(RECategoryEnum.CREATE);
        creationRE.addRule(new BlockCreation());
        return List.of(creationRE);
    }

    @Override
    protected List<IRuleEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IOption> initOptions() {
        OptionImpl testEnvOption = new OptionImpl(
                new BooleanProperty("BooleanField", true),
                "FEATURE GROUP 1",
                plugin.getEnvironmentOptionsGroup(),
                OptionKind.Environment
        );
        OptionImpl testNewCatEnvOption = new OptionImpl(
                new BooleanProperty("BooleanField 2", true),
                "FEATURE GROUP 2",
                plugin.getEnvironmentOptionsGroup(),
                OptionKind.Environment
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

        return Arrays.asList(
                testEnvOption,
                testNewCatEnvOption
        );
    }

    @Override
    protected List<IOption> initProjectOnlyOptions() {
        OptionImpl testEnvOption = new OptionImpl(
                new ElementProperty("Test profile property", Profile.getInstance().getSysml().block().getStereotype()),
                "FEATURE GROUP 1",
                plugin.getEnvironmentOptionsGroup(),
                OptionKind.Environment
        );

        OptionImpl testProjectOption2 = new OptionImpl(
                new StringProperty("OMF STRING PROPERTY 2", "DEFAULT VALUE"),
                "PROJECT FEATURE GROUP 2",
                plugin.getEnvironmentOptionsGroup(),
                OptionKind.Project
        );

        OptionImpl testProjectOption = new OptionImpl(
                new StringProperty("OMF STRING PROPERTY 1", "DEFAULT VALUE"),
                "PROJECT FEATURE GROUP 1",
                plugin.getEnvironmentOptionsGroup(),
                OptionKind.Project
        );


        return Arrays.asList(
                testEnvOption,
                testProjectOption,
                testProjectOption2
        );
    }


}
