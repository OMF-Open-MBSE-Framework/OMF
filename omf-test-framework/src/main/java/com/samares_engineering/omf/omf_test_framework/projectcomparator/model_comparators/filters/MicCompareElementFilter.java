/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin CespÃ©dÃ¨s, ClÃ©ment Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.filters;

import com.nomagic.magicdraw.tests.common.comparators.ModelComparatorFilter;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MicCompareElementFilter implements ModelComparatorFilter {
    @Override
    public boolean needToCompare(Element element) {
        if (element instanceof Element) return true;
//        if (MBSIProfile.getInstance().mic().is(element)) return true;
//        if (MBSIProfile.getInstance().micPort().is(element)) return true;
//        if (MBSIProfile.getInstance().micControlInterface().is(element)) return true;
//        if (MBSIProfile.getInstance().micPhysicalInterface().is(element)) return true;
//        if (MBSIProfile.getInstance().micVariableGroup().is(element)) return true;
//        if (MBSIProfile.getInstance().micVariable().is(element)) return true;
//        if (MBSIProfile.getInstance().parameters().is(element)) return true;
//        if (MBSIProfile.getInstance().micParameterGroup().is(element)) return true;
//        if (MBSIProfile.getInstance().micParameter().is(element)) return true;
//        if (MBSIProfile.getInstance().usage().is(element)) return true;
        return false;
    }

    @Override
    //TODO Replace with a more generic solution using lists or config file
    public boolean needToCompareAttribute(String attributeName, Element elementA, Element elementB) {

        // We want to propagate to children
        if (attributeName.equals("ownedElement")) return true;
        if (attributeName.equals("type")) return true;


        //TODO
//        if (MBSIProfile.getInstance().mic().is(elementA) && attributeMatches(attributeName,
//                "Original Platform",
//                "OriginalId",
//                "MIC Owner",
//                "SourceCodeLink",
//                "DeveloperName",
//                "Artifact Link",
//                "ModelVersion",
//                "ModelBehaviorId",
//                "ModelID",
//                "SpecificName",
//                "GranularityLevel",
//                "CreationDate",
//                "Notes",
//                "SimulationGoal",
//                "BehavioralModel",
//                "isSUT",
//                "CoveredFunctions",
//                "ModelVerification",
//                "ModelValidation",
//                "Uncertainty",
//                "InputPedigree",
//                "Robustness",
//                "History",
//                "Complexity Level",
//                "isDraft",
//                "ModelDimension",
//                "HardwareRequirements",
//                "TimeStepUnit",
//                "Linearity",
//                "ToolVersion",
//                "OperatingSystem",
//                "Real-Time Simulation",
//                "TimeStepType",
//                "CompilerName",
//                "DistributedSimulation",
//                "TimeStepValue",
//                "EquationsType",
//                "ToolName",
//                "ResolutionMethod",
//                "base_Class",
//                "documentation"
//                )) {
//            return true;
//        }
//
//        if (MBSIProfile.getInstance().micPort().is(elementA)
//                && attributeMatches(attributeName,
//                "Name",
//                "Direction",
//                "Is Used",
//                "Is Conjugated")) {
//            return true;
//        }
//
//        if (MBSIProfile.getInstance().micControlInterface().is(elementA) && attributeMatches(attributeName,
//                "Name",
//                "Causality")) {
//            return true;
//        }
//        if (MBSIProfile.getInstance().micPhysicalInterface().is(elementA) && attributeMatches(attributeName,
//                "Name",
//                "Causality",
//                "Physical Domain")) {
//            return true;
//        }
//
//        if (MBSIProfile.getInstance().micVariableGroup().is(elementA) && attributeMatches(attributeName,
//                "Name")) {
//            return true;
//        }
//
//        if (MBSIProfile.getInstance().micVariable().is(elementA) && attributeMatches(attributeName,
//                "Name",
//                "Is Observable",
//                "Default Value",
//                "Unit",
//                "Min",
//                "Max",
//                "Size",
//                "Type",
//                "Direction",
//                "Frequency",
//                "Resolution",
//                "Max Rate",
//                "Is Used")) {
//            return true;
//        }
//        if (MBSIProfile.getInstance().parameters().is(elementA) && attributeMatches(attributeName,
//                "Name")) {
//            return true;
//        }
//
//        if (MBSIProfile.getInstance().micParameterGroup().is(elementA) && attributeMatches(attributeName,
//                "Name")) {
//            return true;
//        }
//
//        if (MBSIProfile.getInstance().micParameter().is(elementA) && attributeMatches(attributeName,
//                "Name",
//                "Default Value",
//                "Unit",
//                "Min",
//                "Max",
//                "Size",
//                "Type",
//                "Comments")) {
//            return true;
//        }
        return false;
    }

    private static boolean attributeMatches(String attributeName, String... validAttributes) {
        return Arrays.stream(validAttributes).anyMatch(validAttribute -> convertStringToFullLowerCase(validAttribute)
                .equals(convertStringToFullLowerCase(attributeName)));
    }

    /**
     * Remove all special characters and convert to lower case for all strings in the list
     */
    private static List<String> convertStringsToFullLowerCase(List<String> strings) {
        return strings.stream().map(MicCompareElementFilter::convertStringToFullLowerCase).collect(Collectors.toList());
    }

    /**
     * Remove all special characters and convert to lower case
     */
    private static String convertStringToFullLowerCase(String string) {
        return string.replaceAll("[^a-zA-Z]", "").toLowerCase();
    }
}
