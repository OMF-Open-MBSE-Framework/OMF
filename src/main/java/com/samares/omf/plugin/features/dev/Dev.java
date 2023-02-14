/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.features.dev;

import com.samares.omf.core.feature.registrables.actions.actions.AUIAction;
import com.samares.omf.core.feature.AFeature;
import com.samares.omf.core.feature.registrables.options.option.IOption;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.IFeatureRuleEngine;
import com.samares.omf.core.plugin.APlugin;
import com.samares.omf.plugin.features.dev.actions.ResetListeners;

import java.util.Collections;
import java.util.List;

public class Dev extends AFeature {
    public Dev(APlugin plugin) {
        super(plugin,"Dev");
    }

    @Override
    public List<AUIAction> initFeatureActions() {
        return List.of(
             new ResetListeners()
        );
    }

    @Override
    public List<IFeatureRuleEngine> initLiveActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IOption> initOptions() {
        return Collections.emptyList();
    }
}
