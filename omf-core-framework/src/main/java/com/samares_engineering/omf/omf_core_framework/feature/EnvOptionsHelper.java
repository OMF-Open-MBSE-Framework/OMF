package com.samares_engineering.omf.omf_core_framework.feature;

import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;

public abstract class EnvOptionsHelper {
    private final MDFeature feature;
    private final OMFPropertyOptionsGroup optionsGroup;

    protected EnvOptionsHelper(MDFeature feature) {
        this.feature = feature;
        this.optionsGroup = feature.getPlugin().getEnvironmentOptionsGroup();
    }

    public final OMFPropertyOptionsGroup getOptionGroup() {
        return optionsGroup;
    }

    public final Property getPropertyByName(String name) {
        return optionsGroup.getPropertyByName(name);
    }

    public MDFeature getFeature() {
        return feature;
    }

    public void addProperty(Property property) {
        optionsGroup.addProperty(property);
    }
}
