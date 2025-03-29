/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.utils.PriorityProvider;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.AUIActionConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.UIActionConfiguratorUtils;

import java.util.ArrayList;

/**
 * BrowserConfigurator: In charge of registering MDActions(right click menu) for browser.
 * When the right click is made, it will call configure(), then will register (display) all actions satisfying the
 * 'checkBrowserAvailability' condition.
 * To add an Action to the List call 'addNewAction'.
 */
public class OMFBrowserConfigurator extends AUIActionConfigurator implements BrowserContextAMConfigurator, AMConfigurator {
    @Override
    public int getPriority() {
        return PriorityProvider.MEDIUM_PRIORITY;
    }

    /**
     * Called when MagicDraw is launched when a project is Opened. Not sure if the behavior is useful.
     * @param actionsManager the actions manager
     */
    @Override
    public void configure(ActionsManager actionsManager) {
//        new ArrayList<>(registeredActions).stream()
//                .filter(UIAction::isBrowserAction)
//                .filter(UIAction::checkBrowserAvailability)
//                .forEach(action -> this.registerBrowserAction(actionsManager, action));
    }

    /**
     * Called when the right click is made on the browser, or when "create new XXX" is triggered
     * Register an action into the category, if the category doesn't exist, it will register it.
     */
    @Override
    public void configure(ActionsManager actionsManager, Tree tree) {
        new ArrayList<>(registeredActions).stream()
                .filter(UIAction::isBrowserAction)
                .filter(UIAction::checkBrowserAvailability)
                .forEach(action -> this.registerBrowserAction(actionsManager, action));
    }


    /**
     * Register an action into the category, if the category doesn't exist it will register it.
     */
    private void registerBrowserAction(ActionsManager actionsManager, UIAction action) {
        try {
            UIActionConfiguratorUtils.findOrCreateCategory(actionsManager, action).addAction(action.getBrowserAction());
        }catch (Exception e){
            String actionName = action != null? action.getClass().getSimpleName(): "Unknown";
            OMFErrorHandler.getInstance().handleException(new CoreException2("Error while registering browser action: " + actionName, e));
        }
    }
}
