package com.samares.omf.plugin.features.feature_C;

import com.samares.omf.plugin.features.feature_C.actions.F_C_MDAction;
import com.samares.omf.plugin.features.feature_C.actions.RegisterFeatureA;
import com.samares.omf.plugin.features.feature_C.actions.RemoveFeatureA;
import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.feature.AFeature;
import com.samares.omf.core.feature.options.IOption;
import com.samares.omf.core.feature.ruleengine.IFeatureRuleEngine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Feature_C extends AFeature {

    public Feature_C(){
        super("Feature C");
    }


    @Override
    public List<AGenericAction> initFeatureActions() {
        return Arrays.asList(
                new F_C_MDAction(),
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
