package com.samares_engineering.omf.omf_example_plugin.features.sandbox;

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions.CreateStateMachineAction;
import com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions.SandboxUIAction;
import com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions.TestCompartmentsUIAction;
import com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions.TestErrorHandling;

import java.util.List;

public class SandboxFeature extends SimpleFeature  {

    public SandboxFeature() {
        super("Sandbox");
    }

    @Override
    public List<UIAction> initFeatureActions() {
        return List.of(
                new SandboxUIAction(),
                new TestErrorHandling(),
                new TestCompartmentsUIAction(),
                new CreateStateMachineAction()
        );
    }

}
