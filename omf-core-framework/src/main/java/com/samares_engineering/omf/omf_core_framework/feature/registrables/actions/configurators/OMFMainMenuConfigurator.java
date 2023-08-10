/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators;


import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.utils.PriorityProvider;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;

public class OMFMainMenuConfigurator extends FeatureActionConfigurator implements AMConfigurator {
    /**
     * Action will be added to manager.
     */
    @Override
    public void configure(ActionsManager actionsManager) {
        resetMDActions(actionsManager);
        genericActions.stream()
                .filter(AUIAction::isMenuAction)
                .forEach(action -> this.registerMenuAction(actionsManager, action));

    }

    private void registerMenuAction(ActionsManager actionsManager, AUIAction menuAction) {
        MDActionsCategory category = ConfiguratorUtils.findOrCreateCategory(actionsManager, menuAction);

        MDAction action = menuAction.getMenuAction();
        if(!category.getActions().contains(menuAction.getMenuAction()))
            category.addAction(menuAction.getMenuAction());

        action.setEnabled(menuAction.checkMenuAvailability());
    }

    @Override
    public int getPriority() {
        return PriorityProvider.MEDIUM_PRIORITY;
    }
}
