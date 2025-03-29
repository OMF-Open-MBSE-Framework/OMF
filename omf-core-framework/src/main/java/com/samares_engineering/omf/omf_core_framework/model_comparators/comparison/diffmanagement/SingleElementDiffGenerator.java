package com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.diffmanagement;

import com.nomagic.uml2.ext.jmi.reflect.AbstractRefObject;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TaggedValue;
import com.nomagic.uml2.impl.jmi.UML2ModelHelper;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.ElementComparator;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.helpers.ComparatorUtils;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.DiffKind;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.ElementDiff;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.PropertyDiff;
import org.omg.mof.model.MofAttribute;
import org.omg.mof.model.Reference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create property/element diffs recursively for diffs with a single element (added, removed, unmatched). It is faster
 * to have a separate algorithm to create them here than to create them in the respective property/element comparators.
 */
public class SingleElementDiffGenerator {
    private final DiffManager diffManager;
    private final ElementComparator elemComparator;

    public SingleElementDiffGenerator(DiffManager diffManager, ElementComparator elementComparator) {
        this.diffManager = diffManager;
        this.elemComparator = elementComparator;
    }

    List<PropertyDiff> createSingleElementPropertyDiffs(Element elemLeft, Element elemRight, DiffKind diffKind) {
        if ((elemLeft == null) == (elemRight == null)) {
            throw new IllegalArgumentException("One of the elements should be null and the other not");
        }

        Element nonNullElem = elemLeft != null ? elemLeft : elemRight;
        List<PropertyDiff> propertyDiffs = new ArrayList<>();

        // Create attribute diffs
        propertyDiffs.addAll(ComparatorUtils.getElementAttributes(nonNullElem).stream()
                .map(MofAttribute::getName)
                .filter(attributeName -> elemComparator.getFilterManager().noNeedToCompareAttribute(attributeName, elemRight, elemLeft))
                .map(attributeName -> createAttributePropertyDiffsForSingleElementDiff(attributeName, elemLeft, elemRight, diffKind))
                .collect(Collectors.toList()));

        // Create reference diffs
        propertyDiffs.addAll(ComparatorUtils.getElementReferences(nonNullElem).stream()
                .map(Reference::getName)
                .filter(refName -> !UML2ModelHelper.isPrivatePropertyName(refName))
                .filter(refName -> !elemComparator.getFilterManager().noNeedToCompareAttribute(refName, elemRight, elemLeft))
                .map(refName -> createReferencePropertyDiffForSingleElementDiff(elemLeft, elemRight, diffKind, refName))
                .collect(Collectors.toList()));

        // Create tagged value diffs
        List<TaggedValue> filteredTaggedValues = elemLeft == null ?
                elemComparator.getFilterManager().getFilteredTaggedValues(elemRight, null) :
                elemComparator.getFilterManager().getFilteredTaggedValues(elemLeft, null);
        propertyDiffs.addAll(filteredTaggedValues.stream()
                .map(taggedValue -> createTaggedValuePropertyDiffForSingleElementDiff(elemLeft, elemRight, diffKind, taggedValue))
                .collect(Collectors.toList()));
        return propertyDiffs;
    }

    static PropertyDiff createAttributePropertyDiffsForSingleElementDiff(String attributeName, Element elemLeft, Element elemRight, DiffKind diffKind) {
        if (elemLeft != null) {
            Object value = ((AbstractRefObject) elemLeft).get(attributeName);
            return new PropertyDiff(attributeName, value, null, diffKind);
        } else {
            Object value = ((AbstractRefObject) elemRight).get(attributeName);
            return new PropertyDiff(attributeName, null, value, diffKind);
        }
    }

    PropertyDiff createTaggedValuePropertyDiffForSingleElementDiff(Element elemLeft, Element elemRight, DiffKind diffKind, TaggedValue taggedValue) {
        if (elemLeft != null) {
            return new PropertyDiff(taggedValue, null, diffKind)
                    .addReferencedElementDiffs(createTaggedValueReferencedElementDiffs(taggedValue.getValue(), elemLeft, null, diffKind));
        } else {
            return new PropertyDiff(null, taggedValue, diffKind)
                    .addReferencedElementDiffs(createTaggedValueReferencedElementDiffs(taggedValue.getValue(), null, elemRight, diffKind));
        }
    }

    List<ElementDiff> createTaggedValueReferencedElementDiffs(List<?> taggedValueValues, Element elemLeft,
                                                              Element elemRight, DiffKind diffKind) {
        // Normally if one value is an element, all other values are elements, but we check just in case
        List<Element> taggedValueElementReferences = taggedValueValues.stream()
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .collect(Collectors.toList());
        return createReferencedElementDiffs(taggedValueElementReferences, elemLeft, elemRight, diffKind);
    }

    /*
     * References
     */

    PropertyDiff createReferencePropertyDiffForSingleElementDiff(Element elemLeft, Element elemRight, DiffKind diffKind, String refName) {
        if (elemLeft != null) {
            List<Element> referencedElements = ComparatorUtils.getReferencedElements(refName, elemLeft);
            return new PropertyDiff(refName, referencedElements, Collections.emptyList(), diffKind)
                    .addReferencedElementDiffs(createReferencedElementDiffs(referencedElements, elemLeft, null, diffKind));
        } else {
            List<Element> referencedElements = ComparatorUtils.getReferencedElements(refName, elemRight);
            return new PropertyDiff(refName, referencedElements, Collections.emptyList(), diffKind)
                    .addReferencedElementDiffs(createReferencedElementDiffs(referencedElements, null, elemRight, diffKind));
        }
    }

    List<ElementDiff> createReferencedElementDiffs(List<Element> referencedElements, Element elemLeft, Element elemRight, DiffKind diffKind) {
        if ((elemLeft == null) == (elemRight == null)) {
            throw new IllegalArgumentException("One of the elements should be null and the other not");
        }
        return referencedElements.stream()
                .filter(elem -> !elemComparator.getFilterManager().noNeedToCompareElement(elem))
                .map(elem -> elemLeft == null ? diffManager.createDiff(null, elem, diffKind) : diffManager.createDiff(elem, null, diffKind))
                .collect(Collectors.toList());
    }
}