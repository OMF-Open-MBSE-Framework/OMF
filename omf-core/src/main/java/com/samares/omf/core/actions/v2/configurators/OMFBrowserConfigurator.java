/*
 * @copyright Copyright (c) 2021 Airbus SAS
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.1.0
 */
package com.samares.omf.core.actions.v2.configurators;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.ui.browser.Tree;

/**
 * BrowserConfigurator: In charge of registering MDActions(right click menu) for browser.
 * When the right click is made, it will call configure(), then will register (display) all actions satisfying the 'checkBrowserAvailability' condition.
 * To add an Action to the List call 'addNewAction'.
 */
public class OMFBrowserConfigurator extends FeatureActionConfigurator implements BrowserContextAMConfigurator, AMConfigurator {


    @Override
    public void configure(ActionsManager actionsManager) {
    }

    public int getPriority() {
        return AMConfigurator.MEDIUM_PRIORITY;
    }

    @Override
    public void configure(ActionsManager actionsManager, Tree tree) {
        //Check if Automations are Activated
        configureFeatureActions(actionsManager);
    }






}
