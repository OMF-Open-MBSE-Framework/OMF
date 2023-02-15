/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.features.example1;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.Property;
import com.nomagic.magicdraw.properties.StringProperty;
import com.samares.omf.core.feature.registrables.actions.actions.AUIAction;
import com.samares.omf.core.feature.AFeature;
import com.samares.omf.core.feature.registrables.actions.actions.IUIAction;
import com.samares.omf.core.feature.registrables.options.option.AOptionListener;
import com.samares.omf.core.feature.registrables.options.option.IOption;
import com.samares.omf.core.feature.registrables.options.option.OptionImpl;
import com.samares.omf.core.feature.registrables.options.option.OptionKind;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.FeatureRuleEngine;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.IFeatureRuleEngine;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares.omf.core.plugin.APlugin;
import com.samares.omf.plugin.features.example1.actions.ExampleMDAction1;
import com.samares.omf.plugin.features.example1.actions.RemoveProjectOptions;
import com.samares.omf.plugin.features.example1.rules.creation.BlockCreation;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;

public class ExampleFeature1 extends AFeature {

    public ExampleFeature1(APlugin plugin){
       super(plugin, "ExampleFeature1");
    }

    @Override
    public List<IUIAction> initFeatureActions() {
        return Arrays.asList(
                new ExampleMDAction1(),
                new RemoveProjectOptions()
        );
    }

    @Override
    public List<IFeatureRuleEngine> initLiveActions() {
        IFeatureRuleEngine creationRE = newRuleEngine(RECategoryEnum.CREATE);
        creationRE.addRule(new BlockCreation());
        return List.of(creationRE);
    }

    @Override
    public List<IOption> initOptions() {
        OptionImpl testEnvOption = new OptionImpl(
                new BooleanProperty("BooleanField", true),
                "FEATURE GROUP 1",
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );
        OptionImpl testNewCatEnvOption = new OptionImpl(
                new BooleanProperty("BooleanField 2", true),
                "FEATURE GROUP 2",
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Environment
        );

        OptionImpl testProjectOption = new OptionImpl(
                new StringProperty("OMF STRING PROPERTY 1", "DEFAULT VALUE"),
                "PROJECT FEATURE GROUP 1",
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Project
        );

        OptionImpl testProjectOption2 = new OptionImpl(
                new StringProperty("OMF STRING PROPERTY 2", "DEFAULT VALUE"),
                "PROJECT FEATURE GROUP 2",
                OMFPluginEnvOptionsGroup.getInstance(),
                OptionKind.Project
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
                testProjectOption,
                testProjectOption2,
                testNewCatEnvOption
        );
    }


}
