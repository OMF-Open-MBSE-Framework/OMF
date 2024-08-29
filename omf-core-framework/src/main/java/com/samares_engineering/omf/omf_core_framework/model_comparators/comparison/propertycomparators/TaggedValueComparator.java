package com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.propertycomparators;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TaggedValue;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.ElementComparator;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.helpers.ComparatorUtils;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.DiffKind;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.ElementDiff;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.PropertyDiff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaggedValueComparator {
    private final ElementComparator elemComparator;

    public TaggedValueComparator(ElementComparator elementComparator) {
        this.elemComparator = elementComparator;
    }

    /**
     * Checks if the tagged values of two elements are equal. Tagged values are considered equal if their tag definitions
     * are equal and there is an isomorphic (one to one) match of their values (which can be either Elements of primitive
     * objects like Boolean or String).
     * Logs the changes if they are not equal.
     * @param elemLeft  the first element.
     * @param elemRight the second element.
     * @return a list of PropertyDiff objects representing the differences between the tagged values.
     */
    public List<PropertyDiff> compareTaggedValues(Element elemLeft, Element elemRight) {
        // During comparison, equal tagged values are removed from the list of unmatched tagged values, leaving only the
        // unmatched ones.
        List<TaggedValue> unmatchedTaggedValuesLeft = elemComparator.getFilterManager().getFilteredTaggedValues(elemLeft, elemRight);
        List<TaggedValue> unmatchedTaggedValuesRight = elemComparator.getFilterManager().getFilteredTaggedValues(elemRight, elemLeft);
        Map<TaggedValue, TaggedValue> matchedTaggedValues = new HashMap<>();


        // Find the best matching tagged values and remove them from the lists of unmatched tagged values.
        for (TaggedValue taggedValueLeft : elemLeft.getTaggedValue()) {
            elemComparator.getElementMatcher().findBestMatchingElement(taggedValueLeft, unmatchedTaggedValuesRight)
                    .ifPresent(matchedTaggedValueRight -> {
                        matchedTaggedValues.put(taggedValueLeft, matchedTaggedValueRight);
                        unmatchedTaggedValuesLeft.remove(taggedValueLeft);
                        unmatchedTaggedValuesRight.remove(matchedTaggedValueRight);
                    });
        }

        List<PropertyDiff> taggedValuesPropertyDiffs = new ArrayList<>();
        // Check if the matched tagged values own values are equal. If not, the tagged values are not equal and the
        // differences are logged.
        // Property diffs are created and added to the list of property diffs of the element diff.
        taggedValuesPropertyDiffs.addAll(matchedTaggedValues.entrySet().stream()
                .map(entry -> compareTaggedValue(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));

        // Create diffs for removed tagged values and add them to the list of property diffs
        taggedValuesPropertyDiffs.addAll(unmatchedTaggedValuesLeft.stream()
                .map(taggedValue -> new PropertyDiff(taggedValue, null, DiffKind.REMOVED))
                .collect(Collectors.toList()));

        // Create diffs for added tagged values and add them to the list of property diffs
        taggedValuesPropertyDiffs.addAll(unmatchedTaggedValuesRight.stream()
                .map(taggedValue -> new PropertyDiff(null, taggedValue, DiffKind.ADDED))
                .collect(Collectors.toList()));

        return taggedValuesPropertyDiffs;
    }

    /**
     * Compares the values of two tagged values. The values are considered equal if they are isomorphic (one to one)
     * between the two sets of values.
     * Equality of values is determined using the "compareElements(Element, Element)" method for Elements and the
     * "Object::equals" method for other types.
     *
     * @param taggedValueLeft  the tagged value of the first element.
     * @param taggedValueRight the corresponding tagged value of the second element.
     * @return a PropertyDiff objects representing the differences between the tagged values.
     */
    private PropertyDiff compareTaggedValue(TaggedValue taggedValueLeft, TaggedValue taggedValueRight) {
        boolean areTaggedValueValuesElements = taggedValueLeft.getValue().stream().allMatch(Element.class::isInstance)
                && taggedValueRight.getValue().stream().allMatch(Element.class::isInstance);
        if (areTaggedValueValuesElements) {
            return compareElementTaggedValueValue(taggedValueLeft, taggedValueRight);
        } else {
            return compareNonElementTaggedValueValue(taggedValueLeft, taggedValueRight);
        }
    }

    private PropertyDiff compareElementTaggedValueValue(TaggedValue taggedValueLeft, TaggedValue taggedValueRight) {
        List<Element> elementsLeft = taggedValueLeft.getValue().stream().map(Element.class::cast).collect(Collectors.toList());
        List<Element> elementsRight = taggedValueRight.getValue().stream().map(Element.class::cast).collect(Collectors.toList());

        // Match the elements, compare them BY PROPAGATING ELEMENT COMPARISON and then create element diffs for all of
        // them (including the unmatched ones)
        List<ElementDiff> referencedElementDiffs = elemComparator.compareElementLists(elementsLeft, elementsRight);

        // Create the PropertyDiff object
        return new PropertyDiff(taggedValueLeft, taggedValueRight, ComparatorUtils.computePropertyDiffKind(referencedElementDiffs));
    }

    private PropertyDiff compareNonElementTaggedValueValue(TaggedValue taggedValueLeft, TaggedValue taggedValueRight) {
        List<Object> remainingValuesLeft = new ArrayList<>(taggedValueLeft.getValue());
        List<Object> remainingValuesRight = new ArrayList<>(taggedValueRight.getValue());

        for (Object valueLeft : taggedValueLeft.getValue()) {
            if (remainingValuesRight.contains(valueLeft)) { // Just compare using Object.equals() if not an Element
                remainingValuesLeft.remove(valueLeft);
                remainingValuesRight.remove(valueLeft);
            }
        }

        if (remainingValuesRight.isEmpty() && remainingValuesLeft.isEmpty()) {
            return new PropertyDiff(taggedValueLeft, taggedValueRight, DiffKind.IDENTICAL);
        } else if (remainingValuesRight.isEmpty()) {
            return new PropertyDiff(taggedValueLeft, taggedValueRight, DiffKind.REMOVED);
        } else if (remainingValuesLeft.isEmpty()) {
            return new PropertyDiff(taggedValueLeft, taggedValueRight, DiffKind.ADDED);
        } else {
            return new PropertyDiff(taggedValueLeft, taggedValueRight, DiffKind.EDITED_OWN);
        }
    }
}

