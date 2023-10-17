/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations;


import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.utils.PriorityProvider;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.AUIActionConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.UIActionConfiguratorUtils;

import java.util.ArrayList;
import java.util.List;

public class OMFMainMenuConfigurator extends AUIActionConfigurator implements AMConfigurator {
    @Override
    public int getPriority() {
        return PriorityProvider.MEDIUM_PRIORITY;
    }

    /**
     * Action will be added to manager.
     * Menu actions are kind of bugged : unregistering/registering them after magicdraw init leads to weird behaviors.
     * Until we can find a better solution, we register all actions at startup and then activate/deactivate them
     * (grayed out in the UI) instead of
     */

    /**
     * This method is called by magicdraw at startup.
     * @param actionsManager action manager provided by magicdraw.
     */
    @Override
    public void configure(ActionsManager actionsManager) {
        registerMenuActions(actionsManager);
    }

    public void registerMenuActions(ActionsManager actionsManager) {
        registeredActions.stream()
                .filter(UIAction::isMenuAction)
                .forEach(action -> registerMenuAction(actionsManager, action));
    }

    private void registerMenuAction(ActionsManager actionsManager, UIAction menuAction) {
        UIActionConfiguratorUtils.findOrCreateCategory(actionsManager, menuAction).addAction(menuAction.getMenuAction());
    }

    @Override
    public void addRegisteredAction(UIAction action) {
        super.addRegisteredAction(action);
        action.activate();
    }

    @Override
    public void removeRegisteredAction(UIAction action) {
        super.removeRegisteredAction(action);
        action.deactivate();
    }
}