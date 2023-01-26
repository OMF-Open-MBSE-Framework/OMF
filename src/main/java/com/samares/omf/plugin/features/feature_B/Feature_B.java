package com.samares.omf.plugin.features.feature_B;

import com.samares.omf.plugin.features.feature_B.creation.ConcurrentBlockCreation;
import com.samares.omf.plugin.features.feature_B.actions.F_B_MDAction;
import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.feature.AFeature;
import com.samares.omf.core.feature.options.IOption;
import com.samares.omf.core.feature.ruleengine.FeatureRuleEngine;
import com.samares.omf.core.feature.ruleengine.IFeatureRuleEngine;
import com.samares.omf.core.feature.ruleengine.RECategoryEnum;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Feature_B extends AFeature {

    public Feature_B(){
        super("Feature B");
    }

    @Override
    public List<AGenericAction> initFeatureActions() {
        return Arrays.asList(
                new F_B_MDAction()
        );
    }
    @Override
    public List<IFeatureRuleEngine> initLiveActions() {
        IFeatureRuleEngine creationRE = new FeatureRuleEngine(RECategoryEnum.CREATE, 0);
        creationRE.addRule(new ConcurrentBlockCreation());

        return List.of(creationRE);
    }

    @Override
    public List<IOption> initOptions() {
        return Collections.EMPTY_LIST;
    }


}
