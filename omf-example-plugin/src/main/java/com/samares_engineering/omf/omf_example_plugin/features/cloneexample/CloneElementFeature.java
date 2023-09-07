/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.cloneexample;

import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_example_plugin.features.cloneexample.actions.*;
import com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.options.SysMLBasicOptionHelper;

import java.util.List;

public class CloneElementFeature extends SimpleFeature {
    public CloneElementFeature() {
        super( "Clone Element Feature");
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return new SysMLBasicOptionHelper(this);
    }

    @Override
    public List<UIAction> initFeatureActions() {
        return List.of(
                new ClonePart(),
                new ClonePort(),
                new CloneProperty(),
                new CloneType(),
                new GroupPortAction()
        );
    }
}
