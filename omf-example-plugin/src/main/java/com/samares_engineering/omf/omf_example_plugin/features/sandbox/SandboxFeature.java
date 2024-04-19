package com.samares_engineering.omf.omf_example_plugin.features.sandbox;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFExceptionModifier2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel2;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions.SandboxUIAction;
import com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions.TestCompartmentsUIAction;
import com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions.TestErrorHandling;

import java.util.List;

import static com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFExceptionModifier2.*;
import static com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel2.*;

public class SandboxFeature extends SimpleFeature {

    public SandboxFeature() {
        super("Sandbox");
    }

    @Override
    public List<UIAction> initFeatureActions() {
        return List.of(
                new SandboxUIAction(),
                new TestErrorHandling(),
                new TestCompartmentsUIAction()
        );
    }
}
