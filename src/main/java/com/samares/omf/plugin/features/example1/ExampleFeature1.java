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
import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.feature.AFeature;
import com.samares.omf.core.feature.options.AOptionListener;
import com.samares.omf.core.feature.options.IOption;
import com.samares.omf.core.feature.options.OptionImpl;
import com.samares.omf.core.feature.options.OptionKind;
import com.samares.omf.core.feature.ruleengine.FeatureRuleEngine;
import com.samares.omf.core.feature.ruleengine.IFeatureRuleEngine;
import com.samares.omf.core.feature.ruleengine.RECategoryEnum;
import com.samares.omf.core.ui.environmentoptions.OMFEnvironmentOptionsGroup;
import com.samares.omf.plugin.features.example1.actions.ExampleMDAction1;
import com.samares.omf.plugin.features.example1.actions.RemoveProjectOptions;
import com.samares.omf.plugin.features.example1.rules.creation.BlockCreation;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;

public class ExampleFeature1 extends AFeature {

    public ExampleFeature1(){
       super("FEATURE A");
    }

    @Override
    public List<AGenericAction> initFeatureActions() {
        return Arrays.asList(
                new ExampleMDAction1(),
                new RemoveProjectOptions()
        );
    }

    @Override
    public List<IFeatureRuleEngine> initLiveActions() {
        IFeatureRuleEngine creationRE = new FeatureRuleEngine(RECategoryEnum.CREATE);
        creationRE.addRule(new BlockCreation());
        return List.of(creationRE);
    }

    @Override
    public List<IOption> initOptions() {
        OptionImpl testEnvOption = OptionImpl.createEnvOptionWithURI(
                new BooleanProperty("BooleanField", true),
                "FEATURE GROUP",
                OMFEnvironmentOptionsGroup.defaultID
        );

        OptionImpl testProjectOption =new OptionImpl(
                new StringProperty("OMF STRING PROPERTY ", "DEFAULT VALUE"),
                "OMF",
                OMFEnvironmentOptionsGroup.defaultID,
                "FEATURE GROUP",
                OptionKind.Project
        );

        OptionImpl testProjectOption2 = OptionImpl.createProjectOption(
                new StringProperty("OMF STRING PROPERTY ", "DEFAULT VALUE"),
                OMFEnvironmentOptionsGroup.defaultID,
                "OMF",
                "FEATURE GROUP"
        );


        OptionImpl testNewCatEnvOption = new OptionImpl(
                new BooleanProperty("BooleanField", true),
                "OMF",
                OMFEnvironmentOptionsGroup.defaultID,
                "FEATURE GROUP",
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
                testProjectOption,
                testProjectOption2,
                testNewCatEnvOption
        );
    }


}
