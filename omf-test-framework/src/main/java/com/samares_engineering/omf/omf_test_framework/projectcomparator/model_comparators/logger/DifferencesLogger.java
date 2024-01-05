package com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.logger;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.diffdata.DiffKind;
import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.diffdata.dataclasses.ElementDiff;
import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.diffdata.dataclasses.PropertyDiff;
import com.samares_engineering.omf.omf_test_framework.utils.TestLogger;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class is used to log the differences between two elements
 *
 * Log a difference one difference tree for the two compared elements.
 * Use only one depth : log properties differences, and element differences status only.
 * For each element different, log a new differences tree.
 */

public class DifferencesLogger {

    private ElementDiff elementDiff;
    private Set<ElementDiff> toBeLogged;
    private Set<ElementDiff> alreadyLogged;

    public DifferencesLogger(ElementDiff elementDiff) {
        this.elementDiff = elementDiff;

        this.toBeLogged = new HashSet<ElementDiff>();
        this.toBeLogged.add(elementDiff);

        this.alreadyLogged = new HashSet<ElementDiff>();
    }

    public String logDifferencesInformation() {
        StringBuilder stringBuilder = new StringBuilder();

        // Display left element
        stringBuilder.append(logOneElementDifferences(this.elementDiff));

        return stringBuilder.toString();
    }

    private String logOneElementDifferences(ElementDiff elementDiff) {
        StringBuilder stringBuilder = new StringBuilder();

        Element elementLeft = elementDiff.getElementLeft().orElse(null);
        Element elementRight = elementDiff.getElementRight().orElse(null);
        String ADDED_TEXT = "[\"" + LoggerUtils.getElementName(elementRight) + "\" HAS BEEN ADDED].\n";
        String REMOVED_TEXT = "[\"" + LoggerUtils.getElementName(elementLeft) + "  \" HAS BEEN REMOVED].\n";
        switch (elementDiff.getDiffKind()) {
            case IDENTICAL:
                return "";
            case ADDED:
                return ADDED_TEXT;
            case REMOVED:
                return REMOVED_TEXT;
            case UNMATCHED:
                return (elementLeft == null) ?  ADDED_TEXT : REMOVED_TEXT;
        }

        stringBuilder.append("- [DIFFERENCES BETWEEN \"" +
                             LoggerUtils.getElementName(elementLeft) +
                             "\" AND \""
                             + LoggerUtils.getElementName(elementLeft) + "\"] \n"
        );

        stringBuilder.append(logDifferencesBetweenProperties(elementDiff));


        return stringBuilder.toString();
    }

    private String logDifferencesBetweenProperties(ElementDiff elementDiff) {
        StringBuilder stringBuilder = new StringBuilder();

        List<PropertyDiff> propertyDiffs = this.elementDiff.getPropertyDiffs();
        propertyDiffs.stream()
                .filter(propertyDiff -> !propertyDiff.getDiffKind().equals(DiffKind.IDENTICAL))
                .forEach(propertyDiff -> stringBuilder.append(propertyText(propertyDiff)));

        return stringBuilder.toString();
    }

    private String propertyText(PropertyDiff propertyDiff) {
        String start = " ---- property ";
        String ADDED_TEXT = start + "\"" + propertyDiff.getPropertyName() + "\" has been added as \"" + propertyDiff.getPropertyValueRight() + ".\n";
        String REMOVED_TEXT = start + "[\"" + propertyDiff.getPropertyName() + "\" has been removed as \"" + propertyDiff.getPropertyValueLeft() + ".\n";
        String EDITED_TEXT = start + "\"" + propertyDiff.getPropertyName() + "change from \"" +
                propertyDiff.getPropertyValueLeft() + "\" to \"" + propertyDiff.getPropertyValueRight() + "\".\n";
        switch (propertyDiff.getDiffKind()) {
            case ADDED:
                return ADDED_TEXT;
            case REMOVED:
                return REMOVED_TEXT;
            case UNMATCHED:
                return (propertyDiff.getPropertyValueLeft().equals("")) ?  ADDED_TEXT : REMOVED_TEXT;
            default:
                return EDITED_TEXT;
        }
    }

    private String logDifferencesBetweenInnerElements(ElementDiff elementDiff) {
        StringBuilder stringBuilder = new StringBuilder();



        return stringBuilder.toString();
    }


    public void logResult(TestLogger logger) {
        if(this.elementDiff.isDiffIdentical())
            logger.success("\n**** PROJECT COMPARE: PASSED ***" + "\n ");
        else
            logger.err("\n**** PROJECT COMPARE: FAILED ***" + "\n ");

        logger.log("\nPROJECT COMPARE: " + this.elementDiff + "\n\n " + logDifferencesInformation());
    }
}
