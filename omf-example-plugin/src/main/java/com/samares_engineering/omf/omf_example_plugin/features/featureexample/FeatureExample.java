package com.samares_engineering.omf.omf_example_plugin.features.featureexample;

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_example_plugin.features.featureexample.action.DifferentBehaviorFomContextAction;
import com.samares_engineering.omf.omf_example_plugin.features.featureexample.action.LoggingAction;
import com.samares_engineering.omf.omf_example_plugin.features.featureexample.action.NotificationClusterBomb;

import java.util.List;

public class FeatureExample extends SimpleFeature {
    public FeatureExample() {
        super("feature example");
    }

    @Override
    protected List<UIAction> initFeatureActions() {
        return List.of(
                new DifferentBehaviorFomContextAction(),
                new LoggingAction(),
                new NotificationClusterBomb()
        );
    }
}
