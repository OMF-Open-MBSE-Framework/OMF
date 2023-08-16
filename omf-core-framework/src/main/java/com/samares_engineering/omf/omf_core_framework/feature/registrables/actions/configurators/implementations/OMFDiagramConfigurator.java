/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations;


import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.utils.PriorityProvider;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.AUIActionConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.UIActionConfiguratorUtils;

import javax.annotation.CheckForNull;

public class OMFDiagramConfigurator extends AUIActionConfigurator implements DiagramContextAMConfigurator {
    @Override
    public void configure(ActionsManager actionsManager, DiagramPresentationElement diagramPresentationElement,
                          PresentationElement[] presentationElements, @CheckForNull PresentationElement presentationElement) {
        unregisterActionsFromMD(actionsManager);
        genericActions.stream()
                .filter(UIAction::checkDiagramAvailability)
                .forEach(action -> this.registerDiagramAction(actionsManager, action));

    }

    /**
     * register an action into the category, If the category doesn't exist it will register it.
     * @param actionsManager
     * @param action
     */
    private void registerDiagramAction(ActionsManager actionsManager, UIAction action) {
        MDActionsCategory category = UIActionConfiguratorUtils.findOrCreateCategory(actionsManager, action);

        if(action.isDiagramAction())
            category.addAction(action.getDiagramAction());
    }

    @Override
    public int getPriority() {
        return PriorityProvider.MEDIUM_PRIORITY;
    }
}
