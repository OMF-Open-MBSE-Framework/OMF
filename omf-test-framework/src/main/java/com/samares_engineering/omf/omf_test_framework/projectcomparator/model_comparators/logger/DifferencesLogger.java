package com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.logger;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.diffdata.DiffKind;
import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.diffdata.dataclasses.ElementDiff;
import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.diffdata.dataclasses.PropertyDiff;
import com.samares_engineering.omf.omf_test_framework.utils.TestLogger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is used to log the differences between two elements
 *
 * Log a difference tree for the two compared elements.
 * Use only one depth : log properties differences, and referenced element difference status only.
 * For each referenced element different, log a new differences tree.
 */

public class DifferencesLogger {

    private final ElementDiff rootElementDiff;

    // Comparisons to be logged
    private Set<ElementDiff> toBeLogged;

    // Comparisons already logged, we should not log them again
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
            // We assume that the set is not empty for the first iteration as we put rootElementDiff in it
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

        // Deal with non-matched elements
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
        String elementLeftName = LoggerUtils.getElementName(elementLeft);
        String elementRightName = LoggerUtils.getElementName(elementRight);

        String displayNamesText = elementLeftName.equals(elementRightName) ?
                                      "TEST AND ORACLE \"" + elementLeftName + "\"" :
                                      "TEST \"" +  elementLeftName + "\" AND ORACLE \"" + elementRightName + "\"";


        // Deal with matched elements
        stringBuilder.append("\n- [DIFFERENCES BETWEEN " + displayNamesText + "] \n"
        );

        stringBuilder.append(logDifferencesBetweenProperties(elementDiff));
        stringBuilder.append(logDifferencesBetweenInnerElements(elementDiff));

        return stringBuilder.toString();
    }

    private String logDifferencesBetweenProperties(ElementDiff elementDiff) {
        StringBuilder stringBuilder = new StringBuilder();

        List<PropertyDiff> propertyDiffs = elementDiff.getPropertyDiffs();
        propertyDiffs.stream()
                .filter(propertyDiff -> !propertyDiff.getPropertyName().equals("ownedElement"))
                .filter(propertyDiff -> !propertyDiff.getDiffKind().equals(DiffKind.IDENTICAL))
                .forEach(propertyDiff -> stringBuilder.append(propertyText(propertyDiff)));

        return stringBuilder.toString();
    }

    private String propertyText(PropertyDiff propertyDiff) {
        String start = " ---- property ";
        String propertyValueLeft = propertyDiff.getPropertyValueLeft().orElse("");
        String propertyValueRight = propertyDiff.getPropertyValueRight().orElse("");

        String ADDED_TEXT = start + "\"" + propertyDiff.getPropertyName()
                            + "\" with value \"" + propertyValueRight + "\" has been added.\n";
        String REMOVED_TEXT = start + "[\"" + propertyDiff.getPropertyName()
                            + "\" with value \"" + propertyValueLeft + "\" has been removed.\n";
        String EDITED_TEXT = start + "\"" + propertyDiff.getPropertyName() + "\" changed" +
                             logPropertyValuesEdited(propertyValueLeft, propertyValueRight) + "\n";
        switch (propertyDiff.getDiffKind()) {
            case ADDED:
                return ADDED_TEXT;
            case REMOVED:
                return REMOVED_TEXT;
            case UNMATCHED:
                return (propertyDiff.getPropertyValueLeft().equals("")) ?  ADDED_TEXT : REMOVED_TEXT;
            default:
                if (propertyValueRight.isEmpty()) return REMOVED_TEXT;
                if (propertyValueLeft.isEmpty()) return ADDED_TEXT;
                return EDITED_TEXT;
        }
    }

    private String logPropertyValuesEdited(String propertyValueLeft, String propertyValueRight) {
        if (oneIsList(propertyValueLeft, propertyValueRight)) {
            return displayList(propertyValueLeft, propertyValueRight);
        }

        return " from \"" + propertyValueLeft + "\" to \"" + propertyValueRight + "\".";
    }

    private boolean oneIsList(String propertyValueLeft, String propertyValueRight) {
        return propertyValueLeft.contains(",") || propertyValueRight.contains(",");
    }

    private String displayList(String propertyValueLeft, String propertyValueRight) {
        List<String> listLeft = stringToList(propertyValueLeft);
        List<String> listRight = stringToList(propertyValueRight);

        List<String> absentFromRight = findAbsentFromTargetWithCount(listLeft, listRight);
        List<String> absentFromLeft = findAbsentFromTargetWithCount(listRight, listLeft);
        int numberUnchanged = listLeft.size() - absentFromRight.size();


        String stringRemoved = absentFromRight.isEmpty() ? "" :
                                                           " \"["
                                                           + String.join(", ", absentFromRight)
                                                           + "]\" were removed from list and ";
        String stringAdded = absentFromLeft.isEmpty() ? "" :
                                                        " \"["
                                                        + String.join(", ", absentFromLeft)
                                                        + "]\" were added to list and ";

        return ". Element(s) " + stringAdded + stringRemoved + numberUnchanged + " were unchanged.";
    }

    private List<String> findAbsentFromTargetWithCount(List<String> sourceList, List<String> targetList) {
        return sourceList.stream()
                .collect(Collectors.groupingBy(Function.identity(),
                        Collectors.counting())) // Get Map<String, Long> with each value and their number of occurrences
                .entrySet()                      // Set<Map.Entry<String, Long>>
                .stream()
                .filter(entry -> entry.getValue() > targetList.stream()
                        .filter(entry.getKey()::equals)
                        .count()) // Get entry with occurrences Source > Target
                .flatMap(entry -> Collections.nCopies((int) (entry.getValue() - targetList.stream()
                        .filter(entry.getKey()::equals)
                        .count()), entry.getKey()).stream())// Create a list where count x became x values
                .collect(Collectors.toList());
    }

    private List<String> stringToList(String listAsString) {
        String[] stringList = listAsString.split(",\\s*");
        return Arrays.asList(stringList);
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
        String start = " ---- property \"ownedElement\" ";
        Element elementLeft = elementDiff.getElementLeft().orElse(null);
        Element elementRight = elementDiff.getElementRight().orElse(null);

        String ADDED_TEXT = start + "with value " + LoggerUtils.getElementName(elementRight) + " has been added.\n";
        String REMOVED_TEXT = start + "with value " + LoggerUtils.getElementName(elementLeft) + " has been removed.\n";
        String EDITED_TEXT = start + "changed from " + LoggerUtils.getElementName(elementLeft) + " to "
                                   + LoggerUtils.getElementName(elementRight)
                                   + ".\n";

        switch (elementDiff.getDiffKind()) {
            case EDITED_OWN:
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
