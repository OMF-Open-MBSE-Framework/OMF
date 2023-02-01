/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.ui;


import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.actions.v2.configurators.FeatureActionConfigurator;
import com.samares.omf.core.ui.actions.diagram.debug.DebugOnOffOptionsDiagram;
import com.samares.omf.core.ui.environmentoptions.OMFEnvironmentOptionsGroup;
import com.samares.omf.core.errors.OMFErrorHandler;

import javax.annotation.CheckForNull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OMFDiagramConfigurator extends FeatureActionConfigurator implements DiagramContextAMConfigurator, AMConfigurator {

    private MDActionsCategory OMF_category = null;
//


//    /** BETA    **/
//    private MDActionsCategory betaCategory = null;
//    private MDActionsCategory debugCategory = null;

    List<AGenericAction> genericActions = new ArrayList<>();


    public OMFDiagramConfigurator() {

    }

    @Override
    public void configure(ActionsManager actionsManager) {
        actionsManager.addCategory(OMF_category);
    }

    @Override
    public void configure(ActionsManager actionsManager, DiagramPresentationElement diagramPresentationElement, PresentationElement[] presentationElements, @CheckForNull PresentationElement presentationElement) {
        configureFeatureActions(actionsManager);
        try {
            final boolean isPreconditionOk = Application.getInstance().getProject() == null;
//            final boolean isPreconditionOk = (presentationElements == null) || presentationElements.length < 1 || Application.getInstance().getProject() == null;

            if (isPreconditionOk)
                return;


//            ArrayList<MDActionsCategory> categories = new ArrayList();

//            betaCategory = new MDActionsCategory("[BETA] ", "[BETA] OMF");
//            betaCategory.setNested(true);
//            categories.add(betaCategory);
//
//            debugCategory = new MDActionsCategory("[Debug] ", "[Debug] OMF");
//            debugCategory.setNested(true);
//            categories.add(debugCategory);
//
//            final boolean isPresentationListEmpty = presentationElements.length > 0;
//
//
//            //ACTIVATE BETA FEATURES
//            final boolean betaFeaturesActivated = true;
//
//
//            if (betaFeaturesActivated)
//                addDebugOptionsAction(debugCategory);


//            categories.stream().filter(cat -> !cat.isEmpty()).forEach(cat -> actionsManager.addCategory(cat));
        } catch (Exception e) {
            OMFErrorHandler.handleException(e, false);
        }
    }


    @Override
    public int getPriority() {
        return AMConfigurator.MEDIUM_PRIORITY;
    }

    private void addDebugOptionsAction(MDActionsCategory betaCategory) {
        for (Method setter : OMFEnvironmentOptionsGroup.class.getDeclaredMethods()) {
            if (setter.getName().startsWith("set")) {
                Optional<Method> getter = Arrays.stream(OMFEnvironmentOptionsGroup.class.getDeclaredMethods())
                        .filter(method -> method.getName().startsWith("get" + setter.getName().replaceFirst("set", "")))
                        .findFirst();
                if (getter.isPresent())
                    betaCategory.addAction(new DebugOnOffOptionsDiagram(setter.getName(), OMFEnvironmentOptionsGroup.getInstance(), setter, getter.get()));
            }
        }


    }
}
