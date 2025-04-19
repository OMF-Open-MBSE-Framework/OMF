package com.samares_engineering.omf.omf_example_plugin.features.generateProfileClassWrapper;

import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_example_plugin.features.generateProfileClassWrapper.uiaction.GenerateProfileClassWrapperAction;

import java.util.List;

public class GenerateProfileClassWrapper extends SimpleFeature {
    private final String profileName;

    public GenerateProfileClassWrapper(Class<? extends Profile> profile){
        this(profile.getSimpleName());
    }
    public GenerateProfileClassWrapper(String profileClassSimpleName){
        super("Generate Profile Class Wrapper for " + profileClassSimpleName);
        this.profileName = profileClassSimpleName;
    }

    @Override
    protected List<UIAction> initFeatureActions() {
        return List.of(new GenerateProfileClassWrapperAction(profileName));
    }
}
