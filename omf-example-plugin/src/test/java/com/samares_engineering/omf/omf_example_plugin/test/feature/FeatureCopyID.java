/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.feature;

import com.samares_engineering.omf.omf_core_framework.feature.AFeature;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.IUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_example_plugin.test.feature.mdActions.CopyElementIDAction;
import com.samares_engineering.omf.omf_example_plugin.test.feature.mdActions.SaveToLocalMDA;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FeatureCopyID extends AFeature {

    public FeatureCopyID() {
        super("Copy Element ID");
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return null;
    }

    @Override
    public List<IUIAction> initFeatureActions() {
        return Arrays.asList(
                new CopyElementIDAction(),
                new SaveToLocalMDA());
    }

    @Override
    protected List<IUIAction> initProjectOnlyFeatureActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IRuleEngine> initLiveActions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    protected List<IRuleEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IOption> initOptions() {return Collections.EMPTY_LIST;}

    @Override
    protected List<IOption> initProjectOnlyOptions() {
        return Collections.emptyList();
    }


}
