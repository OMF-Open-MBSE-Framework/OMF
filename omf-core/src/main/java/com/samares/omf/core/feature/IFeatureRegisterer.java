package com.samares.omf.core.feature;

import java.util.ArrayList;
import java.util.List;

public interface IFeatureRegisterer {

    void registerFeature(MDFeature mdFeature);
    void unregisterFeature(MDFeature mdFeature);

    default void registerAllFeatures(List<MDFeature> l_feature){
        if(l_feature != null) new ArrayList<>(l_feature).forEach(this::registerFeature);
    }
    default void unregisterAllFeatures(List<MDFeature> l_feature){
        if(l_feature != null) new ArrayList<>(l_feature).forEach(this::unregisterFeature);
    }
}
