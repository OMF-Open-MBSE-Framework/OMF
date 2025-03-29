package com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.propertycomparators;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.impl.jmi.UML2ModelHelper;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.ElementComparator;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.helpers.ComparatorUtils;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.ElementDiff;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.PropertyDiff;
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
     *
     * @param elemLeft  the first element to compare
     * @param elemRight the second element to compare
     * @return a list of PropertyDiff objects that represent the differences between the references
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

    /**
     * This method returns a list of elements that are referenced by the element and that are not filtered out.
     *
     * @param element the element that references other elements
     * @param refName the name of the reference
     * @return a list of elements that are referenced by the element and that are not filtered out
     */
    private List<Element> getFilteredReferencedElements(Element element, String refName) {
        return ComparatorUtils.getReferencedElements(refName, element).stream()
                .filter(elem -> !elemComparator.getFilterManager().noNeedToCompareElement(elem))
                .collect(Collectors.toList());
    }

    /**
     * This method compares a reference of the compared elements.
     * To compare the references, the method checks if the referenced elements are the same.
     * The method also adds the differences to the list of changes.
     *
     * @param refName                the name of the reference
     * @param referencedElementsLeft  the list of elements referenced by the left element
     * @param referencedElementsRight the list of elements referenced by the right element
     * @return a PropertyDiff object that represents the differences between the references
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
