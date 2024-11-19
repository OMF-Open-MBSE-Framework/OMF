/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.feature;

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_example_plugin.test.feature.mdActions.CopyElementIDAction;
import com.samares_engineering.omf.omf_example_plugin.test.feature.mdActions.SaveToLocalMDA;

import java.util.Arrays;
import java.util.List;

public class FeatureCopyID extends SimpleFeature {

    public FeatureCopyID() {
        super("Copy Element ID");
    }

    @Override
    public List<UIAction> initFeatureActions() {
        return Arrays.asList(
                new CopyElementIDAction(),
                new SaveToLocalMDA());
    }

}
