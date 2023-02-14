/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.features.example3;

import com.samares.omf.core.plugin.APlugin;
import com.samares.omf.plugin.features.example3.actions.ExampleMDAction3;
import com.samares.omf.plugin.features.example3.actions.RegisterFeatureA;
import com.samares.omf.plugin.features.example3.actions.RemoveFeatureA;
import com.samares.omf.core.feature.registrables.actions.actions.AUIAction;
import com.samares.omf.core.feature.AFeature;
import com.samares.omf.core.feature.registrables.options.option.IOption;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.IFeatureRuleEngine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExampleFeature3 extends AFeature {

    public ExampleFeature3(APlugin plugin){
        super(plugin, "ExampleFeature3");
    }


    @Override
    public List<AUIAction> initFeatureActions() {
        return Arrays.asList(
                new ExampleMDAction3(),
                new RegisterFeatureA(),
                new RemoveFeatureA()
        );
    }

    @Override
    public List<IFeatureRuleEngine> initLiveActions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<IOption> initOptions() {
        return Collections.EMPTY_LIST;
    }
}
