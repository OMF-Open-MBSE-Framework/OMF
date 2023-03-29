package com.samares_engineering.omf.omf_example_plugin.features.testGeneration.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_example_plugin.features.testGeneration.TestGeneration;
import com.samares_engineering.omf.omf_example_plugin.features.testGeneration.codeGeneration.CodeGenerationUtils;
import com.samares_engineering.omf.omf_example_plugin.features.testGeneration.codeGeneration.classGenerator.CreationTestGenerator;
import com.samares_engineering.omf.omf_example_plugin.features.testGeneration.profile.TestProfile;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

@DiagramAction
@BrowserAction
@DeactivateListener
@MDAction(actionName = "Generate Creation Test", category = "[TEST GENERATION]")
public class GenerateCreationTest extends AUIAction {

    // Where the test file should be generated
    public String generationPath;

    // Package of the generated class
    private String CREATIONTEST_CLASS_PACKAGE = "com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.testGeneration";

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null)
            return false;

        if(selectedElements.isEmpty())
            return false;

        return selectedElements.size() == 1 && isInTestPackage(selectedElements.get(0));
    }



    @Override
    public void actionToPerform(List<Element> selectedElements) {
        if(selectedElements == null)
            return;

        this.generationPath = ((TestGeneration) getFeature()).GENERATION_PATH;

        selectedElements.stream()
                .filter(NamedElement.class::isInstance)
                .map(this::createCreationTestGenerator)
                .map(CreationTestGenerator::generateTest)
                .forEach(this::writeToFile);
    }



    private CreationTestGenerator createCreationTestGenerator(Element testContext) {
        TestGeneration feature = (TestGeneration) getFeature();
        return new CreationTestGenerator(getTestPackage(testContext), (NamedElement) testContext, feature.ENV_OPTION_GROUPIDS, feature.PROJECT_OPTION_GROUPIDS);
    }

    private void writeToFile(TypeSpec classBuilder) {
        CodeGenerationUtils.writeToFile(classBuilder, generationPath, CREATIONTEST_CLASS_PACKAGE);
    }



    /**
     * Determine if an element is in a package stereotyped <<TestPackage>>
     * @param e : the element
     * @return
     */
    private boolean isInTestPackage(Element e) {
        Element parent = e.getOwner();

        // If parent is a package stereotyped <<TestPackage>>
        if (TestProfile.getInstanceByProject().testPackage().is(parent)) {
            return true;
        }
        // If parent is a package or model (a model is a package so the frst condition is enought)
        if (parent instanceof Package) {
            return false;
        }

        return isInTestPackage(parent);
    }



    /**
     * Obtain the package of the element, only if it's stereotyped <<TestPackage>>
     * @param e : the element
     * @return the package if its stereotyped <<TestPackage>>, null otherwise
     */
    private Package getTestPackage(Element e) {
        Element parent = e.getOwner();

        // If parent is a package stereotyped <<TestPackage>>
        if (TestProfile.getInstanceByProject().testPackage().is(parent)) {
            return (Package) parent;
        }
        // If parent is a package or model
        if (parent instanceof Package) {
            OMFErrorHandler.handleException(new OMFException("The selected element must be contained by a package stereotyped <<TestPackage>>.", GenericException.ECriticality.ALERT), true);
            //OMFLogger.getInstance().log("A selected element isn't contain by a package stereotyped <<TestPackage>>. Test will not be generated forn this element.", parent, OMFLogLevel.ERROR);
            return null;
        }

        return getTestPackage(parent);
    }

}

