/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.configurators;


import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.AUIAction;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.List;

public class OMFDiagramConfigurator extends FeatureActionConfigurator implements DiagramContextAMConfigurator, AMConfigurator {

    private MDActionsCategory OMF_category = null;
    List<AUIAction> genericActions = new ArrayList<>();

    public OMFDiagramConfigurator() {

    }

    @Override
    public void configure(ActionsManager actionsManager) {
        actionsManager.addCategory(OMF_category);
    }

    @Override
    public void configure(ActionsManager actionsManager, DiagramPresentationElement diagramPresentationElement, PresentationElement[] presentationElements, @CheckForNull PresentationElement presentationElement) {

        try {
            configureFeatureActions(actionsManager);
        } catch (Exception e) {
            OMFErrorHandler.handleException(e, false);
        }
    }


    @Override
    public int getPriority() {
        return AMConfigurator.MEDIUM_PRIORITY;
    }

}
