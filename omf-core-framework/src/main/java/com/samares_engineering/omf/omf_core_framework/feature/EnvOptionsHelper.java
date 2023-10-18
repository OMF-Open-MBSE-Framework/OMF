package com.samares_engineering.omf.omf_core_framework.feature;

import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.OMFFeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.OptionNotFound;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;

public abstract class EnvOptionsHelper {
    private final MDFeature feature;
    private final OMFPropertyOptionsGroup optionsGroup;

    protected EnvOptionsHelper(MDFeature feature) {
        this(feature, feature.getPlugin().getEnvironmentOptionsGroup()
                .orElseThrow(() -> new OMFFeatureRegisteringException("No environment options groups have been declared" +
                        "for this plugin")));
    }
    protected EnvOptionsHelper(MDFeature feature, OMFPropertyOptionsGroup optionsGroup) {
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
            OMFErrorHandler.handleException(new OptionNotFound(name), false);
        }
         return null;
    }

    public MDFeature getFeature() {
        return feature;
    }

    public void addProperty(Property property) {
        optionsGroup.addProperty(property);
    }


}
