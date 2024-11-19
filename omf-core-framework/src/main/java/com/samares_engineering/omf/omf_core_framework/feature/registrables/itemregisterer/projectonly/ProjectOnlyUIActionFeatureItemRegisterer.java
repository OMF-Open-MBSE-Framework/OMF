/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly;

import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.ProjectOnlyFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.UIActionFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;

/**
 * This class is used to register and unregister UIActions from MDFeatures.
 * It is used by the {@link OMFFeature} class.
 * It is used to register MDActions that are only available in the context of a project.
 * It is used to register MDActions that are available in the context of a project and a diagram.
 * It is used to register MDActions that are available in the context of a project and a browser.
 * It is used to register MDActions that are available in the context of a project and a menu.
 */
public class ProjectOnlyUIActionFeatureItemRegisterer extends UIActionFeatureItemRegisterer implements ProjectOnlyFeatureItemRegisterer<UIAction> {
    public ProjectOnlyUIActionFeatureItemRegisterer(OMFPlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerFeatureItems(OMFFeature feature) {
        registerFeatureItems(feature.getProjectOnlyUIActions());
    }

    @Override
    public void unregisterFeatureItems(OMFFeature feature) {
        unregisterFeatureItems(feature.getProjectOnlyUIActions());
    }
}
