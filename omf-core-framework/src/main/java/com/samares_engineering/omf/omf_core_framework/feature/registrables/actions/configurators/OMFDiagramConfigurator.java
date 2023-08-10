/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators;


import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.utils.PriorityProvider;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;

import javax.annotation.CheckForNull;

public class OMFDiagramConfigurator extends FeatureActionConfigurator implements DiagramContextAMConfigurator {
    @Override
    public void configure(ActionsManager actionsManager, DiagramPresentationElement diagramPresentationElement,
                          PresentationElement[] presentationElements, @CheckForNull PresentationElement presentationElement) {
        resetMDActions(actionsManager);
        genericActions.stream()
                .filter(AUIAction::checkDiagramAvailability)
                .forEach(action -> this.registerDiagramAction(actionsManager, action));

    }

    /**
     * register an action into the category, If the category doesn't exist it will register it.
     * @param actionsManager
     * @param action
     */
    private void registerDiagramAction(ActionsManager actionsManager, AUIAction action) {
        MDActionsCategory category = ConfiguratorUtils.findOrCreateCategory(actionsManager, action);

        if(!actionsManager.getCategories().contains(category) ) {
            actionsManager.addCategory(category);
            category.setNested(true);
        }

        if(action.isDiagramAction())
            category.addAction(action.getDiagramAction());
    }

    @Override
    public int getPriority() {
        return PriorityProvider.MEDIUM_PRIORITY;
    }
}
