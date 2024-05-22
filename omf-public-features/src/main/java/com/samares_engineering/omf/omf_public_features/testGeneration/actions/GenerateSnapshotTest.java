package com.samares_engineering.omf.omf_public_features.testGeneration.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.testGeneration.TestGenerationFeature;
import com.samares_engineering.omf.omf_public_features.testGeneration.codeGeneration.CodeGenerationUtils;
import com.samares_engineering.omf.omf_public_features.testGeneration.codeGeneration.classGenerator.SnapshotGenerator;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

@MenuAction
@DeactivateListener
@MDAction(actionName = "Snapshot", category = "OMF.[TEST GENERATION]")
public class GenerateSnapshotTest extends AUIAction {

    // Package of the generated class
    private String SNAPSHOT_CLASS_PACKAGE = getCurrentPackageRoot() + ".generatedCode";

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return isProjectOpened();
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        //String generationPath = ((TestGeneration) getFeature()).getOptionsHelper().getTestGenerationRootPath();
        String generationPath = getSnapshotPathDefaultValue();

        TestGenerationFeature feature = (TestGenerationFeature) getFeature();
        SnapshotGenerator snapshotGenerator = new SnapshotGenerator(feature.ENV_OPTION_GROUPIDS, feature.PROJECT_OPTION_GROUPIDS, feature.getServerAddress());
        TypeSpec classBuilder = snapshotGenerator.generateTest();
        CodeGenerationUtils.writeToFile(classBuilder, generationPath, SNAPSHOT_CLASS_PACKAGE);
    }

    /*
    Return the root package, ie testGeneration ie the current class package, minus the class name, minus the "actions" package
     */
    private String getCurrentPackageRoot() {
        String currentPackage = this.getClass().getCanonicalName();
        return currentPackage.substring(0, currentPackage.lastIndexOf(".", currentPackage.lastIndexOf(".") - 1));
    }

    /*
        Default Value for ReInitOptionBaseline code generation path.
        Be careful as this class is used in package code, and the good one should be use
     */
    private String getSnapshotPathDefaultValue() {
        String currentDirectory = OMFUtils.getUserDir();
        int lastIndexOfSlash = currentDirectory.lastIndexOf("\\");
        int secondLastIndexOfSlash = currentDirectory.lastIndexOf("\\", lastIndexOfSlash - 1);
        int thirdLastIndexOfSlash = currentDirectory.lastIndexOf("\\", secondLastIndexOfSlash - 1);
        String newStr = currentDirectory.substring(0, thirdLastIndexOfSlash + 1);
        return newStr + "omf-public-features/src/main/java";
    }

}
