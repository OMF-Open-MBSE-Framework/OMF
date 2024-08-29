package com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.helpers;

import com.nomagic.uml2.ext.jmi.reflect.AbstractRefObject;
import com.nomagic.uml2.ext.jmi.reflect.ModelReflection;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.DiffKind;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.ElementDiff;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.PropertyDiff;
import org.omg.mof.model.Class;
import org.omg.mof.model.MofAttribute;
import org.omg.mof.model.Reference;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class ComparatorUtils {
    // Prevent instantiation as this is a static utility class
    private ComparatorUtils() {
    }

    /**
     * Uses human name if the value is an element, the Java string representation otherwise
     *
     * @param values taggedValue values
     * @return a string representation of the given list of tagged value values
     */
    public static String taggedValueValuesToString(List<?> values) {
        return values.stream()
                .map(value -> value instanceof Element ? ((Element) value).getHumanName() : value.toString())
                .collect(Collectors.joining(", "));
    }

    public static String elementsToString(Collection<Element> elements) {
        return elements.stream()
                .filter(element -> element instanceof NamedElement)
                .map(NamedElement.class::cast)
                .map(NamedElement::getName)
                .collect(Collectors.joining(", "));
    }

    public static String toString(Object var0) {
        return var0 == null ? "null" : var0.toString();
    }

    public static Collection<Element> getReferencedElements(Object ref1Value) throws LegacyOMFException {
        if (ref1Value == null)
            return Collections.emptyList();
        if (ref1Value instanceof Collection) {
            Collection<?> ref1ValuesCollection = (Collection<?>) ref1Value;
            if (ref1ValuesCollection.isEmpty() || ref1ValuesCollection.iterator().next() instanceof Element) {
                return (Collection<Element>) ref1ValuesCollection;
            }
        }
        if (ref1Value instanceof Element) {
            return Collections.singletonList((Element) ref1Value);
        }
        throw new LegacyOMFException("Unexpected type of referenced element: " + ref1Value.getClass().getName(), GenericException.ECriticality.CRITICAL);
    }

    public static void toFullName(Element element, StringBuilder sBuilder) {
        sBuilder.append("[")
                .append(getNameOfElementOrOwner(element))
                .append("] ");
    }

    private static String getNameOfElementOrOwner(Element element) {
        if (element instanceof NamedElement) {
            return element.getHumanType() + " \"" + ((NamedElement) element).getName() + "\"";
        } else {
            if (element.getOwner() != null && element.getOwner() instanceof NamedElement) {
                return "of " + element.getOwner().getHumanName();
            }
        }
        return "";
    }

    public static void toQualifiedName(Element element, StringBuilder stringBuilder) {
        for (int size = stringBuilder.length(); element != null; element = element.getOwner()) {
            if (stringBuilder.length() > size) {
                stringBuilder.insert(size, "::");
            }

            String res = "";
            res += element instanceof NamedElement && ((NamedElement) element).getName().length() > 0
                    ? ((NamedElement) element).getName().replace("\n", "E") : "$" + element.getClassType().getSimpleName();
            stringBuilder.insert(size, res);
        }
    }

    /**
     * Returns true if the provided elements are linked via a isMappedTo relation. The purpose of
     * this relation is manually mapping elements to be compared to each other in the compare MICs feature UI.
     *
     * @param elementA the first element
     * @param elementB the second element
     * @return true if the elements are mapped to each other, false otherwise
     */
    public static boolean areElementsMapped(Element elementA, Element elementB) {
        return elementA.get_relationshipOfRelatedElement().stream()
//                .filter(MBSIProfile.getInstance().isMappedTo()::is) //TODO
                .flatMap(rel -> rel.getRelatedElement().stream())
                .anyMatch(elementB::equals);
    }

    public static <T extends Element> Optional<T> getIsMappedToElementInList(T sourceElement, List<T> targetElements) {
        return targetElements.stream()
                .filter(targetElement -> areElementsMapped(sourceElement, targetElement))
                .findFirst();
    }

    public static DiffKind computeElementDiffKind(List<PropertyDiff> propertyDiffs) {
        boolean isEditedOwn = false;
        boolean isEditedReference = false;
        for (PropertyDiff propertyDiff : propertyDiffs) {
            if (propertyDiff.getDiffKind() == DiffKind.EDITED_OWN ||
                    propertyDiff.getDiffKind() == DiffKind.ADDED ||
                    propertyDiff.getDiffKind() == DiffKind.REMOVED
            ) {
                isEditedOwn = true;
            } else if (propertyDiff.getDiffKind() == DiffKind.EDITED_REFERENCE) {
                isEditedReference = true;
            } else if (propertyDiff.getDiffKind() == DiffKind.EDITED_OWN_AND_REFERENCE) {
                isEditedOwn = true;
                isEditedReference = true;
            }
        }

        if (isEditedOwn && isEditedReference) {
            return DiffKind.EDITED_OWN_AND_REFERENCE;
        } else if (isEditedOwn) {
            return DiffKind.EDITED_OWN;
        } else if (isEditedReference) {
            return DiffKind.EDITED_REFERENCE;
        } else {
            return DiffKind.IDENTICAL;
        }
    }

    public static DiffKind computePropertyDiffKind(List<ElementDiff> referencedElementDiffs) {
        boolean isEditedOwn = false;
        boolean isEditedReference = false;
        for (ElementDiff elementDiff : referencedElementDiffs) {
            if (elementDiff.getDiffKind() == DiffKind.ADDED
                    || elementDiff.getDiffKind() == DiffKind.REMOVED
                    || elementDiff.getDiffKind() == DiffKind.UNMATCHED) {
                isEditedOwn = true;
            } else if (elementDiff.getDiffKind() == DiffKind.EDITED_REFERENCE
                    || elementDiff.getDiffKind() == DiffKind.EDITED_OWN_AND_REFERENCE
                    || elementDiff.getDiffKind() == DiffKind.EDITED_OWN) {
                isEditedReference = true;
            }
        }
        if (isEditedOwn && isEditedReference) {
            return DiffKind.EDITED_OWN_AND_REFERENCE;
        } else if (isEditedOwn) {
            return DiffKind.EDITED_OWN;
        } else if (isEditedReference) {
            return DiffKind.EDITED_REFERENCE;
        } else {
            return DiffKind.IDENTICAL;
        }
    }

    public static List<MofAttribute> getElementAttributes(Element elemLeft) {
        Class elementLeft = (Class) elemLeft.refClass().refMetaObject();
        List<MofAttribute> attributesLeft = ModelReflection.getInstance(elemLeft).getAttributes(elementLeft);
        return attributesLeft;
    }

    public static List<Element> getReferencedElements(String refName, Element sourceElement) {
        AbstractRefObject ref1 = (AbstractRefObject) sourceElement;
        Collection<Element> referencedElements1 = null;
        try {
            referencedElements1 = getReferencedElements(ref1.get(refName));
        } catch (LegacyOMFException e) {
            LegacyErrorHandler.handleException(e);
        }
        return new ArrayList<>(referencedElements1);
    }

    public static List<Reference> getElementReferences(Element element) {
        AbstractRefObject elementRef = (AbstractRefObject) element;
        return ModelReflection.getReferences((Class) elementRef.refClass().refMetaObject());
    }

    public static boolean haveSameStereotypes(@Nonnull Element element, @Nonnull Element elemToMatch) {
        // We compare IDs as the elements compared might be in different projects
        return element.getAppliedStereotype().stream().map(Element::getID).collect(Collectors.toList())
                .equals(elemToMatch.getAppliedStereotype().stream().map(Element::getID).collect(Collectors.toList()));
    }
}
