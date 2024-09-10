package com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.diffmanagement;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.ElementComparator;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.DiffKind;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.ElementDiff;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages the creation of diffs. Crucially it avoids infinite recursion/repetition by logging already created element diffs.
 */
public class DiffManager {
    // Used to log already created diffs and avoid infinite recursion/repetition
    private final Map<String, ElementDiff> diffSignatureToDiff = new HashMap<>();
    private final ElementComparator elemComparator;
    private final SingleElementDiffGenerator singleElementDiffGenerator;

    public DiffManager(ElementComparator elementComparator) {
        this.elemComparator = elementComparator;
        singleElementDiffGenerator = new SingleElementDiffGenerator(this, elemComparator);
    }

    public ElementDiff createUnmatchedDiff(Element elementLeft, Element elementRight) {
        return createDiff(elementLeft, elementRight, DiffKind.UNMATCHED);
    }

    public ElementDiff createIdenticalDiff(Element elementLeft, Element elementRight) {
        return createDiff(elementLeft, elementRight, DiffKind.IDENTICAL);
    }

    public ElementDiff createEditedDiff(Element elementLeft, Element elementRight) {
        return createDiff(elementLeft, elementRight, DiffKind.EDITED_OWN);
    }

    public ElementDiff createAddedDiff(Element elementLeft, Element elementRight) {
        return createDiff(elementLeft, elementRight, DiffKind.ADDED);
    }

    public ElementDiff createRemovedDiff(Element elementLeft, Element elementRight) {
        return createDiff(elementLeft, elementRight, DiffKind.REMOVED);
    }

    /**
     * If an equivalent diff already has been created, return it. Otherwise, create it and return it.
     *
     * @param elementLeft  the left element
     * @param elementRight the right element
     * @param diffKind     the kind of diff to create
     * @return the diff
     */
    public ElementDiff createDiff(Element elementLeft, Element elementRight, @Nonnull DiffKind diffKind) {
        if (elementLeft != null && elementRight != null && diffKind.isSingleElementDiffKind()) {
            throw new IllegalArgumentException("Both elements cannot be null when creating an element diff of kind " + diffKind);
        }
        return getAlreadyExistingDiff(elementLeft, elementRight)
                .orElseGet(() -> createNewDiff(elementLeft, elementRight, diffKind));
    }

    public Optional<ElementDiff> getAlreadyExistingDiff(Element elementLeft, Element elementRight) {
        ElementDiff elementDiff = new ElementDiff(elementLeft, elementRight);
        if (diffSignatureToDiff.containsKey(elementDiff.getSignature())) {
            return Optional.of(diffSignatureToDiff.get(elementDiff.getSignature()));
        }
        return Optional.empty();
    }


    private ElementDiff createNewDiff(Element elementLeft, Element elementRight, DiffKind diffKind) {
        ElementDiff elementDiff = new ElementDiff(elementLeft, elementRight, diffKind);
        diffSignatureToDiff.put(elementDiff.getSignature(), elementDiff);
        if (diffKind.isSingleElementDiffKind()) {
            elementDiff.addPropertyDiffs(singleElementDiffGenerator.createSingleElementPropertyDiffs(elementLeft, elementRight, diffKind));
        }
        return elementDiff;
    }
}
