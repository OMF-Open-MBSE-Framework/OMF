package com.samares_engineering.omf.omf_example_plugin.features.testGeneration.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.MenuAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_example_plugin.features.testGeneration.TestGeneration;
import com.samares_engineering.omf.omf_example_plugin.features.testGeneration.codeGeneration.CodeGenerationUtils;
import com.samares_engineering.omf.omf_example_plugin.features.testGeneration.codeGeneration.classGenerator.SnapshotGenerator;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

@MenuAction
@DeactivateListener
@MDAction(actionName = "Snapshot", category = "[TEST GENERATION]")
public class GenerateSnapshotTest extends AUIAction {

    // Server adress
    private final String COMMENT_SNIPPET = " // " + TestGeneration.SERVER_ADRESS + "?ID=";

    // Package of the generated class
    private String SNAPSHOT_CLASS_PACKAGE = "com.samares_engineering.omf.omf_example_plugin.features.testGeneration.generatedCode";

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return OMFUtils.currentProject != null;
    }



    @Override
    public void actionToPerform(List<Element> selectedElements) {
        String generationPath = ((TestGeneration) getFeature()).GENERATION_PATH;

        TestGeneration feature = (TestGeneration) getFeature();
        SnapshotGenerator snapshotGenerator = new SnapshotGenerator(feature.ENV_OPTION_GROUPIDS, feature.PROJECT_OPTION_GROUPIDS);
        TypeSpec classBuilder = snapshotGenerator.generateTest();
        CodeGenerationUtils.writeToFile(classBuilder, generationPath, SNAPSHOT_CLASS_PACKAGE);
    }

}
