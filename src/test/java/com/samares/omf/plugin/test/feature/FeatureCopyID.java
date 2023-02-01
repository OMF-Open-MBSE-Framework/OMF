package com.samares.omf.plugin.test.feature;

import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.feature.AFeature;
import com.samares.omf.core.feature.options.IOption;
import com.samares.omf.core.feature.ruleengine.IFeatureRuleEngine;
import com.samares.omf.plugin.test.feature.mdActions.CopyElementIDAction;
import com.samares.omf.plugin.test.feature.mdActions.SaveToLocalMDA;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FeatureCopyID extends AFeature {

    public FeatureCopyID() {
        super("Copy Element ID");
    }

    @Override
    public List<AGenericAction> initFeatureActions() {
        return Arrays.asList(
                new CopyElementIDAction(),
                new SaveToLocalMDA());
    }

    @Override
    public List<IFeatureRuleEngine> initLiveActions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<IOption> initOptions() {return Collections.EMPTY_LIST;}


}
