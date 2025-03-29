package com.samares_engineering.omf.omf_example_plugin.features.stateactionfeature;

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_example_plugin.features.stateactionfeature.actions.CreateStateAction;

import java.util.List;

public class StateActionExample extends SimpleFeature  {

    public StateActionExample() {
        super("StateAction");
    }

    @Override
    public List<UIAction> initFeatureActions() {
        return List.of(
                new CreateStateAction()
        );
    }

}
