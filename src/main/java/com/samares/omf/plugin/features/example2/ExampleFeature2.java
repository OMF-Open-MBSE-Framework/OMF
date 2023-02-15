/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.features.example2;

import com.samares.omf.core.feature.AFeature;
import com.samares.omf.core.feature.registrables.actions.actions.IUIAction;
import com.samares.omf.core.feature.registrables.options.option.IOption;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.IFeatureRuleEngine;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares.omf.core.plugin.APlugin;
import com.samares.omf.plugin.features.example2.actions.ExampleMDAction2;
import com.samares.omf.plugin.features.example2.creation.ConcurrentBlockCreation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExampleFeature2 extends AFeature {

    public ExampleFeature2(APlugin plugin){
        super(plugin, "ExampleFeature2");
    }

    @Override
    public List<IUIAction> initFeatureActions() {
        return Arrays.asList(
                new ExampleMDAction2()
        );
    }
    @Override
    public List<IFeatureRuleEngine> initLiveActions() {
        IFeatureRuleEngine creationRE = newRuleEngine(RECategoryEnum.CREATE, 0);
        creationRE.addRule(new ConcurrentBlockCreation());

        return List.of(creationRE);
    }

    @Override
    public List<IOption> initOptions() {
        return Collections.EMPTY_LIST;
    }


}
