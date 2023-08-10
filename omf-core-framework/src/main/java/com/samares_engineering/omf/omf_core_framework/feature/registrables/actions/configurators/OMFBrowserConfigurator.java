/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators;

import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.utils.PriorityProvider;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;

/**
 * BrowserConfigurator: In charge of registering MDActions(right click menu) for browser.
 * When the right click is made, it will call configure(), then will register (display) all actions satisfying the 'checkBrowserAvailability' condition.
 * To add an Action to the List call 'addNewAction'.
 */
public class OMFBrowserConfigurator extends FeatureActionConfigurator implements BrowserContextAMConfigurator {
    public int getPriority() {
        return PriorityProvider.MEDIUM_PRIORITY;
    }

    @Override
    public void configure(ActionsManager actionsManager, Tree tree) {
        resetMDActions(actionsManager);
        genericActions.stream()
                .filter(AUIAction::checkBrowserAvailability)
                .forEach(action -> this.registerBrowserAction(actionsManager, action));
    }

    /**
     * register an action into the category, if the category doesn't exist it will register it.
     * @param actionsManager
     * @param action
     */
    private void registerBrowserAction(ActionsManager actionsManager, AUIAction action) {
        MDActionsCategory category = ConfiguratorUtils.findOrCreateCategory(actionsManager, action);

        if(action.isBrowserAction())
            category.addAction(action.getBrowserAction());
    }
}
