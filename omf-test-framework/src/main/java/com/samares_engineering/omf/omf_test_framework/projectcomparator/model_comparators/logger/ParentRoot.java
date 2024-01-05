package com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.logger;

public enum ParentRoot {
    LEFT,
    RIGHT;

    public ParentRoot getOtherSide(){
        if (this == LEFT) {
            return RIGHT;
        } else {
            return LEFT;
        }
    }
}
