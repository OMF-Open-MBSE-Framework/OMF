package com.samares_engineering.omf.omf_core_framework.model_comparators.logger;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.DiffKind;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.ElementDiff;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.PropertyDiff;

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

        // Don't log identical, unmatched, added, deleted elements
        if (elementDiff.getDiffKind().isSingleElementDiffKind() ||
            elementDiff.getDiffKind().equals(DiffKind.IDENTICAL)) {
            return "";
        }

        Element elementLeft = elementDiff.getElementLeft().orElse(null);
        Element elementRight = elementDiff.getElementRight().orElse(null);

        String elementLeftName = LoggerUtils.getElementName(elementLeft);
        String elementRightName = LoggerUtils.getElementName(elementRight);

        String displayNamesText = elementLeftName.equals(elementRightName) ?
                                      "TEST AND ORACLE \"" + elementLeftName + "\"" :
                                      "TEST \"" +  elementLeftName + "\" AND ORACLE \"" + elementRightName + "\"";


        stringBuilder.append("\n- [DIFFERENCES BETWEEN " + displayNamesText + "] \n"
        );

        stringBuilder.append(logDifferencesBetweenProperties(elementDiff));
        addToCompareListDifferencesBetweenInnerElements(elementDiff);

        return stringBuilder.toString();
    }

    ////////// LOG DIFFERENCES BETWEEN PROPERTIES //////////
    private String logDifferencesBetweenProperties(ElementDiff elementDiff) {
        StringBuilder stringBuilder = new StringBuilder();

        List<PropertyDiff> propertyDiffs = elementDiff.getPropertyDiffs();
        propertyDiffs.stream()
                .filter(propertyDiff -> !propertyDiff.getDiffKind().equals(DiffKind.IDENTICAL))
                .forEach(propertyDiff -> stringBuilder.append(propertyText(propertyDiff)));

        return stringBuilder.toString();
    }

    private String propertyText(PropertyDiff propertyDiff) {
        String start = " ---- property ";
        String propertyValueLeft = getPropertyValuesLeft(propertyDiff);
        String propertyValueRight = getPropertyValuesRight(propertyDiff);

        String ADDED_TEXT = start + "\"" + propertyDiff.getPropertyName()
                            + "\" with value \"" + propertyValueRight + "\" has been added.\n";
        String REMOVED_TEXT = start + "[\"" + propertyDiff.getPropertyName()
                            + "\" with value \"" + propertyValueLeft + "\" has been removed.\n";
        String EDITED_TEXT = start + "\"" + propertyDiff.getPropertyName() + "\" " +
                             logPropertyValuesEdited(propertyDiff) + "\n";
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

    private String getPropertyValuesLeft(PropertyDiff propertyDiff) {
        if (propertyDiff.getReferencedElementDiffs().isEmpty()) {
            return propertyDiff.getPropertyValueLeft().orElse("");
        }
        String elementHumanNameValues = propertyDiff.getReferencedElementDiffs().stream()
                                          .filter(elementDiff -> elementDiff.getElementLeft().isPresent())
                                          .map(elementDiff -> elementDiff.getElementLeft().get())
                                          .map(LoggerUtils::getElementName)
                                          .collect(Collectors.joining(", "));

        return elementHumanNameValues;
    }

    private String getPropertyValuesRight(PropertyDiff propertyDiff) {
        if (propertyDiff.getReferencedElementDiffs().isEmpty()) {
            return propertyDiff.getPropertyValueRight().orElse("");
        }

        String elementHumanNameValues = propertyDiff.getReferencedElementDiffs().stream()
                .filter(elementDiff -> elementDiff.getElementRight().isPresent())
                .map(elementDiff -> elementDiff.getElementRight().get())
                .map(LoggerUtils::getElementName)
                .collect(Collectors.joining(", "));

        return elementHumanNameValues;
    }

    private String logPropertyValuesEdited(PropertyDiff propertyDiff) {
        String propertyValueLeft = getPropertyValuesLeft(propertyDiff);
        String propertyValueRight = getPropertyValuesRight(propertyDiff);

        if (propertyDiff.getReferencedElementDiffs().size() > 1) {
            return displayElementList(propertyDiff);
        }

        if (propertyValueLeft.contains(",") || propertyValueRight.contains(",")) {
            return displayStringList(propertyValueLeft, propertyValueRight);
        }

        if (propertyValueLeft.equals(propertyValueRight)) {
            return "with value \"" + propertyValueRight + "\" has been edited.";
        }

        return "changed from \"" + propertyValueLeft + "\" to \"" + propertyValueRight + "\".";
    }

    private String displayElementList(PropertyDiff propertyDiff) {

        List<ElementDiff> listDiffs = propertyDiff.getReferencedElementDiffs().stream()
                                                 .filter(elementDiff -> !elementDiff.getDiffKind().equals(DiffKind.IDENTICAL))
                                                 .collect(Collectors.toList());


        String addedElements = listDiffs.stream()
                                        .filter(elementDiff -> (elementDiff.getDiffKind().equals(DiffKind.ADDED)) ||
                                                               (elementDiff.getDiffKind().equals(DiffKind.UNMATCHED) &&
                                                                elementDiff.getElementRight().isPresent()
                                                               ))
                                        .map(elementDiff -> elementDiff.getElementRight().get())
                                        .map(LoggerUtils::getElementName)
                                        .collect(Collectors.joining(", "));

        String removedElements = listDiffs.stream()
                                          .filter(elementDiff -> (elementDiff.getDiffKind().equals(DiffKind.REMOVED)) ||
                                                  (elementDiff.getDiffKind().equals(DiffKind.UNMATCHED) &&
                                                   elementDiff.getElementLeft().isPresent()
                                                  ))
                                          .map(elementDiff -> elementDiff.getElementLeft().get())
                                          .map(LoggerUtils::getElementName)
                                          .collect(Collectors.joining(", "));

        String editedElements = listDiffs.stream()
                                         .filter(elementDiff -> (!elementDiff.getDiffKind().isSingleElementDiffKind()))
                                         .map(this::getEditedElementString)
                                         .collect(Collectors.joining(", "));

        String stringAdded = addedElements.isEmpty() ? "" :
                                                       " \"[" + addedElements + "]\" were added from list \n\t and ";
        String stringRemoved = removedElements.isEmpty() ? "" :
                                                           " \"[" + removedElements + "]\" were removed from list \n\t and ";
        String stringEdited = editedElements.isEmpty() ? "" :
                                                         " \"[" + editedElements + "]\" were edited \n\t and ";
        long numberUnchanged = propertyDiff.getReferencedElementDiffs().stream()
                                           .filter(elementDiff -> elementDiff.getDiffKind().equals(DiffKind.IDENTICAL))
                                           .count();

        return "changed. Element(s) " + stringAdded + stringRemoved + stringEdited + numberUnchanged + " were unchanged.";
    }

    private String getEditedElementString(ElementDiff elementDiff) {
        Element elementLeft = elementDiff.getElementLeft().get();
        Element elementRight = elementDiff.getElementRight().get();

        return LoggerUtils.getElementName(elementLeft) + " -> " + ((NamedElement) elementRight).getName();
    }

    private String displayStringList(String propertyValueLeft, String propertyValueRight) {
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

        return "changed. Element(s) " + stringAdded + stringRemoved + numberUnchanged + " were unchanged.";
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

    ////////// LOG DIFFERENCES BETWEEN INNER ELEMENTS //////////
    private void addToCompareListDifferencesBetweenInnerElements(ElementDiff elementDiff) {

        elementDiff.getPropertyDiffs().stream()
                .filter(elementDifference -> !elementDifference.getDiffKind().equals(DiffKind.IDENTICAL))
                .map(PropertyDiff::getReferencedElementDiffs)
                .flatMap(List::stream)
                .distinct()
                .filter(referencedElement -> !referencedElement.getDiffKind().isSingleElementDiffKind() ||
                                             !referencedElement.getDiffKind().equals(DiffKind.IDENTICAL)) // Don't be redundant with property
                .sorted(sortByDiffKind())
                .forEach(this::addNewDifferenceToCompare);
    }


    private void addNewDifferenceToCompare(ElementDiff elementDiff) {
        if(!this.alreadyLogged.contains(elementDiff)) {
            this.toBeLogged.add(elementDiff);}
    }

    private static Comparator<ElementDiff> sortByDiffKind() {
        return Comparator.comparing(child -> child.getDiffKind().ordinal());
    }
}
