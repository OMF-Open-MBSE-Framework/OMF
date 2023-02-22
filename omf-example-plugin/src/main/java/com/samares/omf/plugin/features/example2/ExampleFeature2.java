/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 *
 ******************************************************************************/

package com.samares.omf.plugin.features.example2;

import com.samares.omf.core.feature.AFeature;
import com.samares.omf.core.feature.EnvOptionsHelper;
import com.samares.omf.core.feature.registrables.actions.actions.IUIAction;
import com.samares.omf.core.feature.registrables.options.option.IOption;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.RuleEngine;
import com.samares.omf.plugin.features.example2.actions.ExampleMDAction2;
import com.samares.omf.plugin.features.example2.creation.ConcurrentBlockCreation;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExampleFeature2 extends AFeature {

    public ExampleFeature2(){
        super("ExampleFeature2");
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return null;
    }

    @Override
    public List<IUIAction> initFeatureActions() {
        return Arrays.asList(
                new ExampleMDAction2()
        );
    }

    @Override
    protected List<IUIAction> initProjectOnlyFeatureActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IRuleEngine> initLiveActions() {
        IRuleEngine creationRE = new RuleEngine(RECategoryEnum.CREATE, 0);
        creationRE.addRule(new ConcurrentBlockCreation());

        return List.of(creationRE);
    }

    @Override
    protected List<IRuleEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IOption> initOptions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    protected List<IOption> initProjectOnlyOptions() {
        return Collections.emptyList();
    }


}
