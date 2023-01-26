package com.samares.omf.core.feature;

import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.feature.options.IOption;
import com.samares.omf.core.feature.ruleengine.IFeatureRuleEngine;

import java.util.List;

public interface MDFeature {


    String getName();

    void activate();
    void deactivate();
    boolean isActivated(boolean b);


    List<AGenericAction>  getMDActions();
    List<IFeatureRuleEngine> getLiveActions();

    List<IOption> getOptions();




}
