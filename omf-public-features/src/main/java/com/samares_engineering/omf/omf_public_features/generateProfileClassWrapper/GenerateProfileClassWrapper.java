package com.samares_engineering.omf.omf_public_features.generateProfileClassWrapper;

import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_public_features.generateProfileClassWrapper.uiaction.GenerateProfileClassWrapperAction;

import java.util.List;

public class GenerateProfileClassWrapper extends SimpleFeature {
    public GenerateProfileClassWrapper(Class<? extends Profile> profile){
        super("Generate Profile Class Wrapper for " + profile.getName());
    }

    @Override
    protected List<UIAction> initFeatureActions() {
        return List.of(new GenerateProfileClassWrapperAction());
    }
}
