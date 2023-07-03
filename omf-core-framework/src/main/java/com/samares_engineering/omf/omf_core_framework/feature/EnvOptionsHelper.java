package com.samares_engineering.omf.omf_core_framework.feature;

import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFFeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFFrameworkException;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;

public abstract class EnvOptionsHelper {
    private final MDFeature feature;
    private final OMFPropertyOptionsGroup optionsGroup;

    protected EnvOptionsHelper(MDFeature feature) {
        this.feature = feature;
        this.optionsGroup = feature.getPlugin().getEnvironmentOptionsGroup()
                .orElseThrow(() -> new OMFFeatureRegisteringException("No environment options groups have been declared" +
                "for this plugin"));
    }

    public final OMFPropertyOptionsGroup getOptionGroup() {
        return optionsGroup;
    }

    public final Property getPropertyByName(String name) {
         try {
             return optionsGroup.getPropertyByName(name);
        }catch (Exception e){
            OMFErrorHandler.handleException(new OMFFrameworkException("Could not find property with name: " + name, e, GenericException.ECriticality.ALERT), false);
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
