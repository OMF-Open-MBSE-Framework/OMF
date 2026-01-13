package com.samares_engineering.omf.omf_example_plugin.features.generateProfileClassWrapper;

import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_example_plugin.features.generateProfileClassWrapper.uiaction.GenerateProfileClassWrapperAction;

import java.util.List;

// TODO : Move this to the public features plugin. However, need to figure out how to manage the dependency with the dev tools plugin.
public class GenerateProfileClassWrapper extends SimpleFeature {
    private String profileName = null;

    public GenerateProfileClassWrapper(Class<? extends Profile> profile){
        super("Generate Profile Class Wrapper for " + profile.getName());
        this.profileName = profile.getName();
    }

    @Override
    protected List<UIAction> initFeatureActions() {
        return List.of(new GenerateProfileClassWrapperAction(profileName));
    }
}
