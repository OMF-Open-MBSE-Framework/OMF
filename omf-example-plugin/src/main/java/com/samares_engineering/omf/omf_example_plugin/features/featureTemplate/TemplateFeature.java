/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.featureTemplate;

import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.ALiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;
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
    public List<LiveActionEngine> initLiveActions() {
        var creationRE = new ALiveActionEngine(LiveActionType.CREATE);
        creationRE.addLiveAction(new LiveActionExample());
        return List.of(creationRE);
    }

}
