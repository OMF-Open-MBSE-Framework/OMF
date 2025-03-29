package com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TaggedValue;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.helpers.ComparatorUtils;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.DiffKind;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class PropertyDiff {
    private ElementDiff owningElementDiff;
    private final List<ElementDiff> referencedElementDiffs = new ArrayList<>();
    private String propertyName;
    @Nullable
    private String propertyValueLeft;
    @Nullable
    private String propertyValueRight;
    private DiffKind diffKind;

    public PropertyDiff(@Nullable TaggedValue taggedValueA, @Nullable TaggedValue taggedValueB, @Nonnull DiffKind diffKind) {
        if (taggedValueA != null) {
            propertyName = taggedValueA.getTagDefinition().getName();
            propertyValueLeft = "[" + ComparatorUtils.taggedValueValuesToString(taggedValueA.getValue()) + "]";
        }
        if (taggedValueB != null) {
            propertyName = taggedValueB.getTagDefinition().getName();
            propertyValueRight = "[" + ComparatorUtils.taggedValueValuesToString(taggedValueB.getValue()) + "]";
        }
        this.diffKind = diffKind;
    }

    public PropertyDiff(@Nonnull String attributeName,  @Nullable Object attributeValueLeft, @Nullable Object attributeValueRight, @Nonnull DiffKind diffKind) {
        propertyName = attributeName;
        propertyValueLeft = attributeValueLeft == null ? null : ComparatorUtils.toString(attributeValueLeft);
        propertyValueRight = attributeValueRight == null ? null : ComparatorUtils.toString(attributeValueRight);
        this.diffKind = diffKind;
    }

    public PropertyDiff(@Nonnull String referenceName, @Nonnull Collection<Element> referencedElementsLeft,
                        @Nonnull Collection<Element> referencedElementsRight, @Nonnull DiffKind diffKind) {
        propertyName = referenceName;
        propertyValueLeft = ComparatorUtils.elementsToString(referencedElementsLeft);
        propertyValueRight = ComparatorUtils.elementsToString(referencedElementsRight);
        this.diffKind = diffKind;

    }

    public Optional<String> getPropertyValueLeft() {
        return Optional.ofNullable(propertyValueLeft);
    }

    public void setPropertyValueLeft(@Nullable String propertyValueLeft) {
        this.propertyValueLeft = propertyValueLeft;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Optional<String> getPropertyValueRight() {
        return Optional.ofNullable(propertyValueRight);
    }

    public void setPropertyValueRight(@Nullable String propertyValueRight) {
        this.propertyValueRight = propertyValueRight;
    }

    public ElementDiff getOwningElementDiff() {
        return owningElementDiff;
    }

    public void setOwningElementDiff(ElementDiff owningElementDiff) {
        this.owningElementDiff = owningElementDiff;
    }

    public List<ElementDiff> getReferencedElementDiffs() {
        return referencedElementDiffs;
    }

    public DiffKind getDiffKind() {
        return diffKind;
    }

    public boolean isNoDiff() {
        return diffKind == DiffKind.IDENTICAL;
    }

    public PropertyDiff addReferencedElementDiff(ElementDiff elementDiff) {
        referencedElementDiffs.add(elementDiff);
        return this;
    }

    public PropertyDiff addReferencedElementDiffs(List<ElementDiff> elementDiffs) {
        referencedElementDiffs.addAll(elementDiffs);
        return this;
    }

    public void setDiffKind(DiffKind diffKind) {
        this.diffKind = diffKind;
    }


    @Override
    public String toString() {
        return propertyName + " " + referencedElementDiffs.size() + " references " +
                diffKind.toString();
    }
}
