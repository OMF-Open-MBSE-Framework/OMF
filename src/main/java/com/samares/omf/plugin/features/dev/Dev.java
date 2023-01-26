package com.samares.omf.plugin.features.dev;

import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.feature.AFeature;
import com.samares.omf.core.feature.options.IOption;
import com.samares.omf.core.feature.ruleengine.IFeatureRuleEngine;
import com.samares.omf.plugin.features.dev.actions.ResetListeners;

import java.util.Collections;
import java.util.List;

public class Dev extends AFeature {
    public Dev() {
        super("Dev");
    }

    @Override
    public List<AGenericAction> initFeatureActions() {
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
