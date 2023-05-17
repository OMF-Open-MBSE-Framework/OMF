/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFRollBackException;

import java.util.ArrayList;

/**
 * BrowserConfigurator: In charge of registering MDActions(right click menu) for browser.
 * When the right click is made, it will call configure(), then will register (display) all actions satisfying the 'checkBrowserAvailability' condition.
 * To add an Action to the List call 'addNewAction'.
 */
public class OMFBrowserMenuConfigurator extends FeatureActionConfigurator implements BrowserContextAMConfigurator, AMConfigurator {
        @Override
    public void configure(ActionsManager actionsManager) {
    }

    public int getPriority() {
        return AMConfigurator.MEDIUM_PRIORITY;
    }

    @Override
    public void configure(ActionsManager actionsManager, Tree tree) {
        try {
            final boolean isPreconditionOk = (tree.getSelectedNode() == null) || Application.getInstance().getProject() == null;
            if (isPreconditionOk) return;
            ArrayList<MDActionsCategory> mdActionsCategories = new ArrayList();
            configureFeatureActions(actionsManager);

            mdActionsCategories.stream().filter(cat -> !cat.isEmpty()).forEach(actionsManager::addCategory);
        } catch (OMFRollBackException rollBackException) {
            OMFErrorHandler.handleException(rollBackException);
        } catch (Exception e) {
            OMFErrorHandler.handleException(e, false);
        }

    }



}