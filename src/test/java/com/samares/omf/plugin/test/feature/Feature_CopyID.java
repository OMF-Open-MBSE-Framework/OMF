package com.samares.omf.plugin.test.feature;

import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.feature.AFeature;
import com.samares.omf.core.feature.options.IOption;
import com.samares.omf.core.feature.ruleengine.IFeatureRuleEngine;
import com.samares.omf.plugin.test.feature.mdActions.F_CopyElementIDAction;
import com.samares.omf.plugin.test.feature.mdActions.SaveToLocal_MDA;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Feature_CopyID extends AFeature {

    public Feature_CopyID() {
        super("Copy Element ID");


    }

    @Override
    public List<AGenericAction> initFeatureActions() {
        return Arrays.asList(
                new F_CopyElementIDAction(),
                new SaveToLocal_MDA());
    }

    @Override
    public List<IFeatureRuleEngine> initLiveActions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<IOption> initOptions() {return Collections.EMPTY_LIST;}


}
