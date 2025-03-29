package com.samares_engineering.omf.omf_core_framework.model_comparators.comparison;

import com.nomagic.magicdraw.tests.common.comparators.ModelComparatorFilter;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TaggedValue;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilterManager {
    final List<ModelComparatorFilter> filters;

    public FilterManager(List<ModelComparatorFilter> filters) {
        this.filters = new ArrayList<>(filters);
    }

    public boolean noNeedToCompareElement(Element elem) {
        return filters.stream().anyMatch(filter -> !filter.needToCompare(elem)) || elem instanceof TaggedValue;
    }

    public boolean noNeedToCompareAttribute(String attributeName, Element elem1, Element elem2) {
        return filters.stream().anyMatch(filter -> !filter.needToCompareAttribute(attributeName, elem1, elem2));
    }

    public boolean noNeedToCompareTaggedValue(TaggedValue tv, Element elem1, Element elem2) {
        return noNeedToCompareAttribute(tv.getTagDefinition().getName(), elem1, elem2);
    }

    public List<TaggedValue> getFilteredTaggedValues(@Nonnull Element taggedValueOwningElement, Element comparedElement) {
        return taggedValueOwningElement.getTaggedValue().stream()
                .filter(tv -> !noNeedToCompareTaggedValue(tv, taggedValueOwningElement, comparedElement)).collect(Collectors.toList());
    }
}
