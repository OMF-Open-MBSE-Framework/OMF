package com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.helpers;

import com.google.common.base.Strings;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml2.Elements;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.interactions.mdbasicinteractions.Message;
import com.nomagic.uml2.ext.magicdraw.interactions.mdbasicinteractions.MessageOccurrenceSpecification;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.ElementComparator;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.DiffKind;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ElementMatcher {
    private final ElementComparator elementComparator;

    public ElementMatcher(ElementComparator elementComparator) {
        this.elementComparator = elementComparator;
    }

    /**
     * Returns the first element in the list of elements to match that is similar to the element.
     * Two elements are considered similar if they have the same meta-class and the same name.
     *
     * @param element         The element we are trying to match for
     * @param elementsToMatch The list of elements we are trying to match to
     * @return The first similar element if one exists
     */

    public Optional<Element> findUniqueElementOfSameType(Element element, List<Element> elementsToMatch) {
        // We compare IDs as the elements compared might be in different projects
        List<Element> candidateElems = elementsToMatch.stream()
                .filter(elemToMatch -> StereotypesHelper.getBaseClass(elemToMatch).getID().equals(StereotypesHelper.getBaseClass(element).getID())
                        && ComparatorUtils.haveSameStereotypes(element, elemToMatch))
                .collect(Collectors.toList());
        return candidateElems.size() == 1 ? Optional.of(candidateElems.get(0)) : Optional.empty();
    }

    /**
     * Returns best potential match between the element and the elements to match if one exists.
     * If the element has been manually mapped to an element in the list of remaining values, that element is returned.
     *
     * @param element         The element we are trying to match for
     * @param elementsToMatch The list of elements we are trying to match to
     * @param <T>             The type of the element
     * @return The best matching element if one exists
     */
    public <T extends Element> Optional<T> findBestMatchingElement(T element, List<T> elementsToMatch) {
        return ComparatorUtils.getIsMappedToElementInList(element, elementsToMatch).or(() ->
                elementsToMatch.stream()
                        .filter(Objects::nonNull)
                        .filter(elemRight -> areElementsSimilar(element, elemRight))
                        .filter(elemToMatch -> areAttributesEqual(element, elemToMatch))
                        .filter(elemToMatch -> areChildStructuresSimilar(element, elemToMatch))
                        .findFirst()); // If we still have multiple candidate return the first one arbitrarily
    }

    private <T extends Element> boolean areAttributesEqual(T element, T elemToMatch) {
        return elementComparator.getAttributeComparator().compareAttributes(element, elemToMatch).stream()
                .allMatch(propertyDiff -> propertyDiff.getDiffKind() == DiffKind.IDENTICAL);
    }

    /**
     * Checks if the child structure of two elements are equal. Ie that the two elements have the same amount of children,
     * grand-children, great-grand children etc. BUT DOES NOT CHECK IF THE CHILDREN ARE EQUAL.
     */
    private static boolean areChildStructuresSimilar(Element element1, Element element2) {
        List<Element> generationOfDescendantsOfElem1 = List.of(element1);
        List<Element> generationOfDescendantsOfElem2 = List.of(element2);

        while (generationOfDescendantsOfElem1.size() == generationOfDescendantsOfElem2.size()) {
            // We end the search successfully when both lists are empty
            if (generationOfDescendantsOfElem1.isEmpty()) {
                return true;
            }

            // Create list of the next generation of descendants
            generationOfDescendantsOfElem1 = generationOfDescendantsOfElem1.stream()
                    .flatMap(elem -> elem.getOwnedElement().stream())
                    .collect(Collectors.toList());
            generationOfDescendantsOfElem2 = generationOfDescendantsOfElem2.stream()
                    .flatMap(elem -> elem.getOwnedElement().stream())
                    .collect(Collectors.toList());
        }

        return false;
    }

    private boolean areElementsSimilar(@CheckForNull Element elem1, @CheckForNull Element elem2) {
        if (elem1 == null || elem2 == null)
            return elem1 == null && elem2 == null;  //if bothNull => same

        if (!elementComparator.getFilterManager().noNeedToCompareAttribute("id", elem1, elem2) && elem1.getID().equals(elem2.getID()))
            return true;

//        if (existsInSameProject(elem1, elem2))
//            return false;

        boolean sharedSameMetaClass = elem1.getClassType().equals(elem2.getClassType());
        if (!sharedSameMetaClass)
            return false;

        // Named elements
        if (elem1 instanceof NamedElement) {
            final boolean hasSameName = ((NamedElement) elem1).getName().equals(((NamedElement) elem2).getName());
            if (!hasSameName) return false;

            if (elem1 instanceof Diagram) {
                DiagramPresentationElement var4 = Project.getProject(elem1).getDiagram((Diagram) elem1);
                DiagramPresentationElement var5 = Project.getProject(elem2).getDiagram((Diagram) elem2);

                final boolean sameDiagramType = var4.getDiagramType().isEqualType(var5.getDiagramType());
                if (!sameDiagramType)
                    return false;
            }
        }

        // Non-connector relations
        if (Elements.isRelationship(elem1) && !(elem1 instanceof Connector)
                && (!areElementsSimilar(Elements.getClientElement(elem1), Elements.getClientElement(elem2))
                || !areElementsSimilar(Elements.getSupplierElement(elem1), Elements.getSupplierElement(elem2))))
            return false;


        // Slots
        if (elem1 instanceof Slot) {
            StructuralFeature attrE1 = ((Slot) elem1).getDefiningFeature();
            StructuralFeature attrE2 = ((Slot) elem2).getDefiningFeature();
            if (!areElementsSimilar(attrE1, attrE2))
                return false;
        }

        // Tagged values
        if (elem1 instanceof TaggedValue) {
            Property propE1 = ((TaggedValue) elem1).getTagDefinition();
            Property propE2 = ((TaggedValue) elem2).getTagDefinition();
            if (!areElementsSimilar(propE1, propE2))
                return false;
        }

        // Properties
        if (elem1 instanceof Property) {
            Property propE1 = (Property) elem1;
            Property propE2 = (Property) elem2;
            if (Strings.isNullOrEmpty(propE1.getName())) {
                Type propE1Type = propE1.getType();
                Type propE2Type = propE2.getType();
                if (!areElementsSimilar(propE1Type, propE2Type))
                    return false;

                Collection<ConnectorEnd> e1Ends = propE1.getEnd();
                Collection<ConnectorEnd> e2Ends = propE2.getEnd();
                if (e1Ends.size() != e2Ends.size())
                    return false;

                if (!e1Ends.isEmpty()) {
                    ConnectorEnd ceE1 = e1Ends.iterator().next();
                    ConnectorEnd ceE2 = e2Ends.iterator().next();
                    if (!areElementsSimilar(ceE1, ceE2))
                        return false;
                }
            }
        }

        // MessageOccurrenceSpecifications
        if (elem1 instanceof MessageOccurrenceSpecification) {
            Message var15 = ((MessageOccurrenceSpecification) elem1).getMessage();
            Message var20 = ((MessageOccurrenceSpecification) elem2).getMessage();
            if (!areElementsSimilar(var15, var20))
                return false;
        }

        if (elem1 instanceof Message && !Objects.equals(((Message) elem1).getMessageSort(), ((Message) elem2).getMessageSort()))
            return false;

        if (elem1 instanceof ElementValue) {
            ElementValue var19 = (ElementValue) elem1;
            ElementValue var22 = (ElementValue) elem2;
            return areElementsSimilar(var19.getElement(), var22.getElement());
        }

        //ADDED IF NO DIFF BUT SAME NAME THEN RETURN TRUE
        if (elem1 instanceof NamedElement
                && elem2 instanceof NamedElement
                && ((NamedElement) elem1).getName().equals(((NamedElement) elem2).getName()))
            return true;

        if (elementComparator.getFilterManager().noNeedToCompareAttribute("owner", elem1, elem2))
            return true;

        return true;
    }

    private boolean existInSameProject(BaseElement var1, BaseElement var2) {
        return Project.getProject(var2).getElementByID(var1.getID()) != null;
    }
}