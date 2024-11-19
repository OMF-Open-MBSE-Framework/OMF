/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.clonefeature;

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_public_features.clonefeature.actions.*;

import java.util.List;

public class CloneElementFeature extends SimpleFeature {
    public CloneElementFeature() {
        super( "Clone Element Feature");
    }


    @Override
    public List<UIAction> initFeatureActions() {
        return List.of(
                new ClonePart(),
                new ClonePort(),
                new CloneProperty(),
                new CloneType()
        );
    }
}
