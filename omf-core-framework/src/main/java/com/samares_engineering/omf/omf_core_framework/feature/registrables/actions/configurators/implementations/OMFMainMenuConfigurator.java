/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations;


import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsProvider;
import com.nomagic.magicdraw.utils.PriorityProvider;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.AUIActionConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.UIActionConfiguratorUtils;

public class OMFMainMenuConfigurator extends AUIActionConfigurator implements AMConfigurator {
    @Override
    public int getPriority() {
        return PriorityProvider.MEDIUM_PRIORITY;
    }

    /**
     * Action will be added to manager.
     */
    @Override
    public void configure(ActionsManager actionsManager) {
        registeredActions.stream()
                .filter(UIAction::isMenuAction)
                .filter(UIAction::checkMenuAvailability)
                .forEach(action -> this.registerMenuAction(actionsManager, action));
    }

    private void registerMenuAction(ActionsManager actionsManager, UIAction menuAction) {
        UIActionConfiguratorUtils.findOrCreateCategory(actionsManager, menuAction).addAction(menuAction.getMenuAction());
    }

    @Override
    public void removeRegisteredAction(UIAction action) {
        super.removeRegisteredAction(action);
        ActionsProvider.getInstance().getMainMenuActions().removeAction(action.getMenuAction());
    }
}
