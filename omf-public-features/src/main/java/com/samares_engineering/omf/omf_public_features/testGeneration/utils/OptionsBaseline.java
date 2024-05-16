package com.samares_engineering.omf.omf_public_features.testGeneration.utils;

import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_public_features.testGeneration.generatedCode.ReInitOptionsBaseline;

import java.util.List;
import java.util.Map;

public class OptionsBaseline {

    private Class valueClass;
    private String propertyId;
    private Object propertyValue;

    public OptionsBaseline(Class valueClass, String propertyId, Object propertyValue) {
        this.valueClass = valueClass;
        this.propertyId = propertyId;
        this.propertyValue = propertyValue;
    }

    public Class getValueClass() {
        return valueClass;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }

    private Property getProperty(String groupId) {
        return OptionsUtils.getEnvOptionProperty(groupId, getPropertyId());
    }

    /**
     * Set option environment value according to values stored in the generated ReInitOptionsBaseline class
     */
    public static void reInitBaseline() {
        OptionsBaseline.editEnvOptions(ReInitOptionsBaseline.snapshot());
    }

    /**
     * Set option environment value according to given values
     * @param initBaseline the values to set
     */
    public static void initBaseline(Map<String, List<OptionsBaseline>> initBaseline) {
        OptionsBaseline.editEnvOptions(initBaseline);
    }

    private static void editEnvOptions(Map<String, List<OptionsBaseline>> newBaseLine) {
        for (Map.Entry<String, List<OptionsBaseline>> group : newBaseLine.entrySet()) {
            group.getValue().stream()
                    .forEach(optionBaseline ->
                            optionBaseline.getProperty(group.getKey())
                                    .setValue(optionBaseline.getValueClass().cast(optionBaseline.getPropertyValue()))
                    );
        }
    }

}
