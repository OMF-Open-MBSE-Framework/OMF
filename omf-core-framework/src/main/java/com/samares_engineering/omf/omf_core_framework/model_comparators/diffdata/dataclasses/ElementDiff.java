package com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.DiffKind;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ElementDiff {
    @Nullable
    private Element elementLeft;
    @Nullable
    private Element elementRight;
    // Property diffs between the respective properties (tagged values, attributes & references) of elements A and B
    private final List<PropertyDiff> propertyDiffs = new ArrayList<>();
    private DiffKind diffKind;

    public ElementDiff(Element elementLeft, Element elementRight, DiffKind diffKind) {
        this(elementLeft, elementRight);
        this.setDiffKind(diffKind);
    }

    public ElementDiff(Element elementLeft, Element elementRight) {
        this.elementLeft = elementLeft;
        this.elementRight = elementRight;
        if (elementLeft == null && elementRight == null) {
            throw new IllegalArgumentException("Both elementLeft and elementRight cannot be null");
        }
    }

    public Optional<Element> getElementLeft() {
        return Optional.ofNullable(elementLeft);
    }

    public void setElementLeft(Element elementLeft) {
        this.elementLeft = elementLeft;
    }

    public Optional<Element> getElementRight() {
        return Optional.ofNullable(elementRight);
    }

    public void setElementRight(Element elementRight) {
        this.elementRight = elementRight;
    }

    public List<PropertyDiff> getPropertyDiffs() {
        return propertyDiffs;
    }

    public DiffKind getDiffKind() {
        return diffKind;
    }

    public void setDiffKind(DiffKind diffKind) {
        this.diffKind = diffKind;
    }

    public boolean isDiffIdentical() {
        return diffKind == DiffKind.IDENTICAL;
    }

    public void addPropertyDiff(PropertyDiff propertyDiff) {
        this.propertyDiffs.add(propertyDiff);
        propertyDiff.setOwningElementDiff(this);
    }

    public ElementDiff addPropertyDiffs(List<PropertyDiff> propertyDiffs) {
        this.propertyDiffs.addAll(propertyDiffs);
        propertyDiffs.forEach(propertyDiff -> propertyDiff.setOwningElementDiff(this));
        return this;
    }

    /**
     * @return A unique signature of the diff composed of the concatenation of ids of element left and right in that order, separated by a dash.
     * For a null element we use the string "_" as id.
     */
    public String getSignature() {
        return (elementLeft == null ? "_" : elementLeft.getID()) + "-" + (elementRight == null ? "_" : elementRight.getID());
    }

    public boolean isSingleElementDiff() {
        return diffKind.isSingleElementDiffKind();
    }

    public String toString() {
        return (elementLeft == null ? "_" : elementLeft.getHumanName())
                + " / " + (elementRight == null ? "_" : elementRight.getHumanName())
                + " " + propertyDiffs.size() + " propertyDiffs"
                + " " + diffKind;
    }
}
