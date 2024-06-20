/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.featureTemplate;

import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.ALiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType;
import com.samares_engineering.omf.omf_example_plugin.features.featureTemplate.actions.ExampleMDAction1;
import com.samares_engineering.omf.omf_example_plugin.features.featureTemplate.actions.ExampleMDAction2;
import com.samares_engineering.omf.omf_example_plugin.features.featureTemplate.actions.ExampleMDAction3;
import com.samares_engineering.omf.omf_example_plugin.features.featureTemplate.creation.LiveActionExample;

import java.util.Arrays;
import java.util.List;

public class TemplateFeature extends SimpleFeature {

    public TemplateFeature(){
       super("FEATURE NAME");
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return null;
    }

    @Override
    public List<UIAction> initFeatureActions() {
        return Arrays.asList(
                new ExampleMDAction1(),
                new ExampleMDAction2(),
                new ExampleMDAction3()
        );
    }

    @Override
    public List<ALiveActionEngine> initLiveActions() {
        ALiveActionEngine creationRE = new LiveAction(LiveActionType.CREATE);
        creationRE.addRule(new LiveActionExample());
        return List.of(creationRE);
    }

}
