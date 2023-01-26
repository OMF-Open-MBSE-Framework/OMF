package com.samares.omf.core.feature.ruleengine;

public enum RECategoryEnum {
    ANALYSE("ANALYSE"),
    CREATE("CREATE"),
    DELETE("DELETE"),
    UPDATE("UPDATE"),
    AFTER_AUTOMATION("AFTER_AUTOMATION");

    private final String name;

    RECategoryEnum(String name){
        this.name = name;
    }

}
