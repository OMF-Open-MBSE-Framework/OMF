package com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.comparison.propertycomparators;

import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.comparison.ElementComparator;
import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.comparison.helpers.ComparatorUtils;
import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.diffdata.dataclasses.ElementDiff;
import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.diffdata.dataclasses.PropertyDiff;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.impl.jmi.UML2ModelHelper;
import org.omg.mof.model.Reference;

import java.util.List;
import java.util.stream.Collectors;

public class ReferenceComparator {
    private final ElementComparator elemComparator;

    public ReferenceComparator(ElementComparator elementComparator) {
        this.elemComparator = elementComparator;
    }

    /**
     * This method compares all references between elem1 and elem2 like ownedElements, ownedProperties, owner etc.
     * References are element properties that refer to another element. They are equal if they refer to exactly the same
     * set of elements.
     */
    public List<PropertyDiff> compareReferences(Element elemLeft, Element elemRight) {
        return ComparatorUtils.getElementReferences(elemLeft).stream()
                .map(Reference::getName)
                .filter(refName -> !UML2ModelHelper.isPrivatePropertyName(refName))
                .filter(refName -> !elemComparator.getFilterManager().noNeedToCompareAttribute(refName, elemRight, elemLeft))
                .map(refName -> compareReference(refName, getFilteredReferencedElements(elemLeft, refName),
                        getFilteredReferencedElements(elemRight, refName)))
                .collect(Collectors.toList());
    }

    private List<Element> getFilteredReferencedElements(Element element, String refName) {
        return ComparatorUtils.getReferencedElements(refName, element).stream()
                .filter(elem -> !elemComparator.getFilterManager().noNeedToCompareElement(elem))
                .collect(Collectors.toList());
    }

    /**
     * This method compares a reference of the compared elements.
     * To compare the references, the method checks if the referenced elements are the same.
     * The method also adds the differences to the list of changes.
     */
    private PropertyDiff compareReference(String refName, List<Element> referencedElementsLeft,
                                            List<Element> referencedElementsRight) {
        // Match the elements, compare them by PROPAGATING ELEMENT COMPARISON and then create element diffs for all of
        // them (including the unmatched ones)
        List<ElementDiff> referencedElementDiffs = elemComparator.compareElementLists(referencedElementsLeft, referencedElementsRight);

        // Create the PropertyDiff object
        return new PropertyDiff(refName, referencedElementsLeft, referencedElementsRight,
                ComparatorUtils.computePropertyDiffKind(referencedElementDiffs))
                .addReferencedElementDiffs(referencedElementDiffs);
    }
}
