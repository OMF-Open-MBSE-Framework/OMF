package com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.logger;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.diffdata.DiffKind;
import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.diffdata.dataclasses.ElementDiff;
import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.diffdata.dataclasses.PropertyDiff;
import com.samares_engineering.omf.omf_test_framework.utils.TestLogger;

import java.util.*;

/**
 * This class is used to log the differences between two elements
 *
 * Log a difference one difference tree for the two compared elements.
 * Use only one depth : log properties differences, and element differences status only.
 * For each element different, log a new differences tree.
 */

public class DifferencesLogger {

    private ElementDiff rootElementDiff;
    private Set<ElementDiff> toBeLogged;
    private Set<ElementDiff> alreadyLogged;

    public DifferencesLogger(ElementDiff rootElementDiff) {
        this.rootElementDiff = rootElementDiff;

        this.toBeLogged = new HashSet<ElementDiff>();
        this.toBeLogged.add(rootElementDiff);

        this.alreadyLogged = new HashSet<ElementDiff>();
    }

    public String logDifferencesInformation() {
        StringBuilder stringBuilder = new StringBuilder();

        boolean keepComparing = true;

        do {
            ElementDiff elementDiff = this.toBeLogged.iterator().next();
            this.toBeLogged.remove(elementDiff);
            this.alreadyLogged.add(elementDiff);
            stringBuilder.append(logOneElementDifferences(elementDiff));

            if (this.toBeLogged.isEmpty()) {
                keepComparing = false;
            }
        } while (keepComparing);

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

        stringBuilder.append("- [DIFFERENCES BETWEEN TEST \"" +
                             LoggerUtils.getElementName(elementLeft) +
                             "\" AND ORACLE \""
                             + LoggerUtils.getElementName(elementLeft) + "\"] \n"
        );

        stringBuilder.append(logDifferencesBetweenProperties(elementDiff));
        stringBuilder.append(logDifferencesBetweenInnerElements(elementDiff));

        return stringBuilder.toString();
    }

    private String logDifferencesBetweenProperties(ElementDiff elementDiff) {
        StringBuilder stringBuilder = new StringBuilder();

        List<PropertyDiff> propertyDiffs = this.rootElementDiff.getPropertyDiffs();
        propertyDiffs.stream()
                .filter(propertyDiff -> !propertyDiff.getPropertyName().equals("ownedElement"))
                .filter(propertyDiff -> !propertyDiff.getDiffKind().equals(DiffKind.IDENTICAL))
                .forEach(propertyDiff -> stringBuilder.append(propertyText(propertyDiff)));

        return stringBuilder.toString();
    }

    private String propertyText(PropertyDiff propertyDiff) {
        String start = " ---- property ";
        String ADDED_TEXT = start + "\"" + propertyDiff.getPropertyName() + "\" has been added as \"" + propertyDiff.getPropertyValueRight().orElse("") + ".\n";
        String REMOVED_TEXT = start + "[\"" + propertyDiff.getPropertyName() + "\" has been removed as \"" + propertyDiff.getPropertyValueLeft().orElse("") + ".\n";
        String EDITED_TEXT = start + "\"" + propertyDiff.getPropertyName() + "\" change from \"" +
                propertyDiff.getPropertyValueLeft().orElse("") + "\" to \"" + propertyDiff.getPropertyValueRight().orElse("") + "\".\n";
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

        elementDiff.getPropertyDiffs().stream()
                .filter(elementDifference -> !elementDifference.getDiffKind().equals(DiffKind.IDENTICAL))
                .map(PropertyDiff::getReferencedElementDiffs)
                .flatMap(List::stream)
                .distinct()
                .sorted(sortByDiffKind())
                .forEach(elementDifference -> stringBuilder.append(processElementDiff(elementDifference)));

        return stringBuilder.toString();
    }

    private String processElementDiff(ElementDiff elementDiff) {
        String start = " ---- referenced element ";
        Element elementLeft = elementDiff.getElementLeft().orElse(null);
        Element elementRight = elementDiff.getElementRight().orElse(null);

        String ADDED_TEXT = start + LoggerUtils.getElementName(elementRight) + " has been added.\n";
        String REMOVED_TEXT = start + LoggerUtils.getElementName(elementLeft) + "\" has been removed.\n";
        String EDITED_TEXT = start + LoggerUtils.getElementName(elementLeft) + " and "
                                   + LoggerUtils.getElementName(elementRight)
                                   + " are differents. And extra tree will be created to compare them below.\n";

        switch (elementDiff.getDiffKind()) {
            case EDITED_REFERENCE:
            case EDITED_OWN_AND_REFERENCE:
                break;
            case ADDED:
                return ADDED_TEXT;
            case REMOVED:
                return REMOVED_TEXT;
            case UNMATCHED:
                return (elementLeft == null) ?  ADDED_TEXT : REMOVED_TEXT;
            default:
                return "";
        }
        addNewDifferenceToCompare(elementDiff);
        return EDITED_TEXT;
    }

    private void addNewDifferenceToCompare(ElementDiff elementDiff) {
        if(!this.alreadyLogged.contains(elementDiff)) {
            this.toBeLogged.add(elementDiff);}
    }

    private static Comparator<ElementDiff> sortByDiffKind() {
        return Comparator.comparing(child -> child.getDiffKind().ordinal());
    }


    public void logResult(TestLogger logger) {
        if(this.rootElementDiff.isDiffIdentical())
            logger.success("\n**** PROJECT COMPARE: PASSED ***" + "\n ");
        else
            logger.err("\n**** PROJECT COMPARE: FAILED ***" + "\n ");

        logger.log("\nPROJECT COMPARE: " + this.rootElementDiff + "\n\n " + logDifferencesInformation());
    }
}
