/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

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
