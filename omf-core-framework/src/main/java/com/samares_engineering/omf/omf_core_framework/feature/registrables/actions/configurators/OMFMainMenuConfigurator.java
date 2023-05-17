/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators;


import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;

public class OMFMainMenuConfigurator extends FeatureActionConfigurator implements AMConfigurator {

    String menuName = "OMF";

    /**
     * Action will be added to manager.
     */
    @Override
    public void configure(ActionsManager actionsManager) {
        configureFeatureActions(actionsManager);
    }


    @Override
    public int getPriority() {
        return AMConfigurator.MEDIUM_PRIORITY;
    }
}
