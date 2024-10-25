/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations;


import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.utils.PriorityProvider;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.stateactions.StateAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.AUIActionConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.UIActionConfiguratorUtils;

import javax.annotation.CheckForNull;
import java.util.ArrayList;

public class OMFDiagramConfigurator extends AUIActionConfigurator implements DiagramContextAMConfigurator, AMConfigurator {
    @Override
    public int getPriority() {
        return PriorityProvider.MEDIUM_PRIORITY;
    }

    /**
     * Called when MagicDraw is launched when a project is Opened. Not sure if the behavior is useful.
     * @param actionsManager the actions manager
     */
    @Override
    public void configure(ActionsManager actionsManager){
//        new ArrayList<>(registeredActions).stream()
//                .filter(UIAction::isDiagramAction)
//                .filter(UIAction::checkDiagramAvailability)
//                .forEach(action -> this.registerDiagramAction(actionsManager, action));
    }
    @Override
    public void configure(ActionsManager actionsManager, DiagramPresentationElement diagramPresentationElement,
                          PresentationElement[] presentationElements, @CheckForNull PresentationElement presentationElement) {
        new ArrayList<>(registeredActions).stream()
                .filter(UIAction::isDiagramAction)
                .filter(UIAction::checkDiagramAvailability)
                .forEach(action -> this.registerDiagramAction(actionsManager, action));

    }

    /**
     * register an action into the category, If the category doesn't exist it will register it.
     */
    private void registerDiagramAction(ActionsManager actionsManager, UIAction action) {
       try {
            UIActionConfiguratorUtils.findOrCreateCategory(actionsManager, action).addAction(action.getDiagramAction());
        }catch (Exception e){
            String actionName = action != null? action.getClass().getSimpleName(): "Unknown";
            OMFErrorHandler.getInstance().handleException(new CoreException2("Error while registering Diagram action: " + actionName, e));
        }
    }
}
