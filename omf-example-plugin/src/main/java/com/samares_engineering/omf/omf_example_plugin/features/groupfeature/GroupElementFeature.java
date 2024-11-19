/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.groupfeature;

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_example_plugin.features.groupfeature.actions.GroupPartsAction;
import com.samares_engineering.omf.omf_example_plugin.features.groupfeature.actions.GroupPortsAction;
import com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.options.SysMLBasicOptionHelper;

import java.util.List;

public class GroupElementFeature extends SimpleFeature {
    public GroupElementFeature() {
        super( "Group Element Feature");
    }

    @Override
    protected SysMLBasicOptionHelper initEnvOptionsHelper() {
        return new SysMLBasicOptionHelper(this);
    }

    @Override
    public List<UIAction> initFeatureActions() {
        return List.of(
                new GroupPartsAction(),
                new GroupPortsAction()
        );
    }
}
