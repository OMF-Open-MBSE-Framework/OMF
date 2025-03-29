package com.samares_engineering.omf.omf_core_framework.model_comparators.comparison;

import com.nomagic.magicdraw.tests.common.comparators.ModelComparatorFilter;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.diffmanagement.DiffManager;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.helpers.ComparatorUtils;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.helpers.ElementMatcher;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.propertycomparators.AttributeComparator;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.propertycomparators.ReferenceComparator;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.propertycomparators.TaggedValueComparator;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.DiffKind;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.ElementDiff;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.PropertyDiff;

import java.util.*;

public class ElementComparator {
    private final FilterManager filterManager;
    private final DiffManager diffManager = new DiffManager(this);
    private final AttributeComparator attributeComparator = new AttributeComparator(this);
    private final ElementMatcher elementMatcher = new ElementMatcher(this);
    private final ReferenceComparator referenceComparator = new ReferenceComparator(this);
    private final TaggedValueComparator taggedValueComparator = new TaggedValueComparator(this);

    public ElementComparator(List<ModelComparatorFilter> filters) {
        filterManager = new FilterManager(filters);
    }

    public ElementDiff compareElements(Element elementLeft, Element elementRight) {
        if (elementLeft == null || elementRight == null) {
            throw new IllegalArgumentException("Can't compare as one of the elements is null");
        }

        // If a diff has already been created with same signatures (same elements) then we don't regenerate it and
        // instead fetch the reference to it
        Optional<ElementDiff> alreadyExistingDiff = diffManager.getAlreadyExistingDiff(elementLeft, elementRight);
        if (alreadyExistingDiff.isPresent()) {
            return alreadyExistingDiff.get();
        }

        // TODO Check if this is really what we want
        if (filterManager.noNeedToCompareElement(elementLeft) || filterManager.noNeedToCompareElement(elementRight)) {
            return diffManager.createIdenticalDiff(elementLeft, elementRight);
        }

        ElementDiff diff = diffManager.createDiff(elementLeft, elementRight, DiffKind.IDENTICAL);
        List<PropertyDiff> propertyDiffs = new ArrayList<>();
        propertyDiffs.addAll(attributeComparator.compareAttributes(elementLeft, elementRight));
        propertyDiffs.addAll(referenceComparator.compareReferences(elementLeft, elementRight));
        propertyDiffs.addAll(taggedValueComparator.compareTaggedValues(elementLeft, elementRight));
        diff.setDiffKind(ComparatorUtils.computeElementDiffKind(propertyDiffs));
        return diff.addPropertyDiffs(propertyDiffs);
    }

    /**
     * Matches the elements of the two given lists of elements between each other as well as possible. If some of the
     * elements are not matched, "UNMATCHED", "ADDED" or "REMOVED" element diffs are created for them. "ADDED" or "REMOVED"
     * diffs are created when the unmatched elements are only present in one of the lists (and we can thus be sure they
     * have been created or removed rather than modified beyond our ability to match them).
     *
     * @param elemsLeft  The list of elements to compare (the elements of this list will be compared to the elements of the right list)
     * @param elemsRight The other list of elements to compare
     * @return The list of element diffs created for the elements of the two lists
     */
    public List<ElementDiff> compareElementLists(List<Element> elemsLeft, List<Element> elemsRight) {

        List<Element> remainingElemsLeft = new ArrayList<>(elemsLeft);
        List<Element> remainingElemsRight = new ArrayList<>(elemsRight);
        Map<Element, Element> matchedElements = new HashMap<>();

        // TODO Refactor this into a better element matching system
        // We pre-check all combinations of elements to see if they are mapped by user
        for (Element elemLeft : elemsLeft) {
            ComparatorUtils.getIsMappedToElementInList(elemLeft, remainingElemsRight).ifPresent(matchedElem -> {
                matchedElements.put(elemLeft, matchedElem);
                remainingElemsLeft.remove(elemLeft);
                remainingElemsRight.remove(matchedElem);
            });
        }
        for (Element elemRight : elemsRight) {
            ComparatorUtils.getIsMappedToElementInList(elemRight, remainingElemsLeft).ifPresent(matchedElem -> {
                matchedElements.put(elemRight, matchedElem);
                remainingElemsLeft.remove(elemRight);
                remainingElemsRight.remove(matchedElem);
            });
        }

        for (Element elemLeft : elemsLeft) {
            elementMatcher.findBestMatchingElement(elemLeft, remainingElemsRight).ifPresent(matchedElem -> {
                matchedElements.put(elemLeft, matchedElem);
                remainingElemsLeft.remove(elemLeft);
                remainingElemsRight.remove(matchedElem);
            });
        }

        // Match any remaining elements when both are the only remaining element with same type (metaclass/stereotypes)
        for (Element elemLeft : new ArrayList<>(remainingElemsLeft)) {
            // We need to check that the left element is the only one of its type,
            // the same way we checked that the right matching element is the only one of its type.
            boolean elemLeftIsOnlyElementOfItsType = elementMatcher.findUniqueElementOfSameType(
                    elemLeft, remainingElemsLeft).isPresent();
            if (elemLeftIsOnlyElementOfItsType) {
                elementMatcher.findUniqueElementOfSameType(elemLeft, remainingElemsRight)
                        .ifPresent(matchedElemRight -> {
                            matchedElements.put(elemLeft, matchedElemRight);
                            remainingElemsLeft.remove(elemLeft);
                            remainingElemsRight.remove(matchedElemRight);
                        });
            }
        }

        //
        // Construct the diff structure & propagate the comparison to the matched elements
        //

        List<ElementDiff> referencedElementDiffs = new ArrayList<>();

        // PROPAGATE THE COMPARISON to the matched elements and add the resulting diffs to the list
        matchedElements.forEach((matchedElemLeft, matchedElemRight) ->
        referencedElementDiffs.add(compareElements(matchedElemLeft, matchedElemRight)));

        // Create ADDED, REMOVED or UNMATCHED diffs for the remaining elements and add them to the list
        if (remainingElemsLeft.isEmpty() && !remainingElemsRight.isEmpty()) {
            remainingElemsRight.forEach(elem -> referencedElementDiffs.add(diffManager.createAddedDiff(null, elem)));
        } else if (!remainingElemsLeft.isEmpty() && remainingElemsRight.isEmpty()) {
            remainingElemsLeft.forEach(elem -> referencedElementDiffs.add(diffManager.createRemovedDiff(elem, null)));
        } else {
            remainingElemsLeft.forEach(elem -> referencedElementDiffs.add(diffManager.createUnmatchedDiff(elem, null)));
            remainingElemsRight.forEach(elem -> referencedElementDiffs.add(diffManager.createUnmatchedDiff(null, elem)));
        }
        return referencedElementDiffs;
    }

    /*
     Getters
     */
    public DiffManager getDiffFactory() {
        return diffManager;
    }

    public FilterManager getFilterManager() {
        return filterManager;
    }

    public AttributeComparator getAttributeComparator() {
        return attributeComparator;
    }

    public ElementMatcher getElementMatcher() {
        return elementMatcher;
    }

    public ReferenceComparator getReferenceComparator() {
        return referenceComparator;
    }

    public TaggedValueComparator getTaggedValueComparator() {
        return taggedValueComparator;
    }
}
