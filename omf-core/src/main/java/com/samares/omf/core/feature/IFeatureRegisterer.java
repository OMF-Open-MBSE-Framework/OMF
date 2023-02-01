package com.samares.omf.core.feature;

import java.util.ArrayList;
import java.util.List;

public interface IFeatureRegisterer {

    void registerFeature(MDFeature mdFeature);
    void unregisterFeature(MDFeature mdFeature);

    default void registerAllFeatures(List<MDFeature> features){
        if(features != null) new ArrayList<>(features).forEach(this::registerFeature);
    }
    default void unregisterAllFeatures(List<MDFeature> features){
        if(features != null) new ArrayList<>(features).forEach(this::unregisterFeature);
    }
}
