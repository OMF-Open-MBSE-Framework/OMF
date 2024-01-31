package com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata;

public enum DiffKind {
    // Values are ordered by display order
    IDENTICAL,
    EDITED_OWN,
    EDITED_REFERENCE,
    EDITED_OWN_AND_REFERENCE,
    ADDED,
    REMOVED,
    UNMATCHED
    ;

    public boolean isMatched() {
        switch (this) {
            case IDENTICAL:
            case EDITED_OWN:
            case EDITED_REFERENCE:
            case EDITED_OWN_AND_REFERENCE:
                return true;
            default:
                return false;
        }
    }

    public boolean isDifferent() {
        switch (this) {
            case IDENTICAL:
            case UNMATCHED:
                return false;
            default:
                return true;
        }
    }

    public DiffKind getReverse() {
        switch (this) {
            case ADDED:
                return REMOVED;
            case REMOVED:
                return ADDED;
            default:
                return this;
        }
    }

    public boolean isSingleElementDiffKind() {
        switch (this) {
            case ADDED:
            case REMOVED:
            case UNMATCHED:
                return true;
            default:
                return false;
        }
    }
}
