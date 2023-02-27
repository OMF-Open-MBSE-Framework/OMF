/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.example3;

import com.samares_engineering.omf.omf_core_framework.feature.AFeature;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.IUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_example_plugin.features.example3.actions.ExampleMDAction3;
import com.samares_engineering.omf.omf_example_plugin.features.example3.actions.RegisterFeatureA;
import com.samares_engineering.omf.omf_example_plugin.features.example3.actions.RemoveFeatureA;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExampleFeature3 extends AFeature {

    public ExampleFeature3(){
        super("ExampleFeature3");
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return null;
    }

    @Override
    public List<IUIAction> initFeatureActions() {
        return Arrays.asList(
                new ExampleMDAction3(),
                new RegisterFeatureA(),
                new RemoveFeatureA()
        );
    }

    @Override
    protected List<IUIAction> initProjectOnlyFeatureActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IRuleEngine> initLiveActions() {
        return Collections.emptyList();
    }

    @Override
    protected List<IRuleEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IOption> initOptions() {
        return Collections.emptyList();
    }

    @Override
    protected List<IOption> initProjectOnlyOptions() {
        return Collections.emptyList();
    }
}
