/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic;

import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.ILiveAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.LiveAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.actions.SyncAllNameAction;
import com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.live.creation.CreateAutoInterface_OnPortCreation;
import com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.options.SysMLBasicOptionHelper;

import java.util.List;

public class SysMLBasicFeature extends SimpleFeature {
    public SysMLBasicFeature() {
        super( "SysML Basic");
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return new SysMLBasicOptionHelper(this);
    }

    @Override
    public List<UIAction> initFeatureActions() {
        return List.of(
//             new ResetListeners()
            new SyncAllNameAction()
        );
    }

    @Override
    public List<ILiveAction> initLiveActions() {
        LiveAction creationRE = new LiveAction(RECategoryEnum.CREATE);
        creationRE.addRule(new CreateAutoInterface_OnPortCreation());

        return List.of(
            creationRE
        );
    }


    @Override
    public List<IOption> initOptions() {
        return ((SysMLBasicOptionHelper) getEnvOptionsHelper()).getAllOptions();
    }

}
