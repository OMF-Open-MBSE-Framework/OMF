package com.samares_engineering.omf.omf_core_framework.feature;

import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OptionNotFound;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;

public abstract class EnvOptionsHelper {
    private final OMFFeature feature;
    private final OMFPropertyOptionsGroup optionsGroup;

    protected EnvOptionsHelper(OMFFeature feature) {
        this(feature, feature.getPlugin().getEnvironmentOptionsGroup()
                .orElseThrow(() -> new FeatureRegisteringException("No environment options groups have been declared" +
                        "for this plugin")));
    }
    protected EnvOptionsHelper(OMFFeature feature, OMFPropertyOptionsGroup optionsGroup) {
        this.feature = feature;
        this.optionsGroup = optionsGroup;
    }

    public final OMFPropertyOptionsGroup getOptionGroup() {
        return optionsGroup;
    }

    public final Property getPropertyByName(String name) {
         try {
             return optionsGroup.getPropertyByName(name);
        }catch (Exception e){
            LegacyErrorHandler.handleException(new OptionNotFound(name), false);
        }
         return null;
    }

    public OMFFeature getFeature() {
        return feature;
    }

    public void addProperty(Property property) {
        optionsGroup.addProperty(property);
    }


}
