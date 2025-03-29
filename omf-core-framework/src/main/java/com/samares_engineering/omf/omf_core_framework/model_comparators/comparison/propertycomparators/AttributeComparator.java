package com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.propertycomparators;

import com.nomagic.uml2.ext.jmi.reflect.AbstractRefObject;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.ElementComparator;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.helpers.ComparatorUtils;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.DiffKind;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.PropertyDiff;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AttributeComparator {
    private final ElementComparator elemComparator;

    public AttributeComparator(ElementComparator elementComparator) {
        this.elemComparator = elementComparator;
    }

     public List<PropertyDiff> compareAttributes(Element elemLeft, Element elemRight) {
        return ComparatorUtils.getElementAttributes(elemLeft).stream()
                .filter(attribute -> !elemComparator.getFilterManager().noNeedToCompareAttribute(attribute.getName(), elemRight, elemLeft))
                .map(attribute -> compareAttribute(attribute.getName(), elemLeft, elemRight))
                .collect(Collectors.toList());
    }

    private PropertyDiff compareAttribute(String attributeName, Element elemLeft, Element elemRight) {
        Object valueLeft = ((AbstractRefObject) elemLeft).get(attributeName);
        Object valueRight = ((AbstractRefObject) elemRight).get(attributeName);

        if (Objects.equals(valueLeft, valueRight)) {
            return new PropertyDiff(attributeName, valueLeft, valueRight, DiffKind.IDENTICAL);
        } else if (valueLeft == null) {
            return new PropertyDiff(attributeName, null, valueRight, DiffKind.ADDED);
        } else if (valueRight == null) {
            return new PropertyDiff(attributeName, valueLeft, null, DiffKind.REMOVED);
        } else {
            return new PropertyDiff(attributeName, valueLeft, valueRight, DiffKind.EDITED_OWN);
        }
    }
}
