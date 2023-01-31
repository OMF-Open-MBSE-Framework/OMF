/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.ui;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.actions.v2.configurators.FeatureActionConfigurator;
import com.samares.omf.core.ui.actions.browser.debug.DebugCreateElement;
import com.samares.omf.core.ui.actions.browser.debug.DebugOnOffOptionsBrowser;
import com.samares.omf.core.builders.BetaFactory;
import com.samares.omf.core.ui.environmentoptions.OMFEnvironmentOptionsGroup;
import com.samares.omf.core.errors.OMFErrorHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * BrowserConfigurator: In charge of registering MDActions(right click menu) for browser.
 * When the right click is made, it will call configure(), then will register (display) all actions satisfying the 'checkBrowserAvailability' condition.
 * To add an Action to the List call 'addNewAction'.
 */
public class OMFBrowserMenuConfigurator extends FeatureActionConfigurator implements BrowserContextAMConfigurator, AMConfigurator {

    private MDActionsCategory GenericCategory = null;
    private MDActionsCategory devCategory = null;

    /** BETA    **/
    private MDActionsCategory betaCategory = null;

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
            if (isPreconditionOk)
                return;
            ArrayList<MDActionsCategory> l_category = new ArrayList();


            betaCategory = new MDActionsCategory("[Beta] beta features", "[Beta] beta features");
            betaCategory.setNested(true);
            l_category.add(betaCategory);

            devCategory = new MDActionsCategory("Dev", "Dev");
            devCategory.setNested(true);

            Node[] selectedNodes = tree.getSelectedNodes();
            final boolean selectedItemsAreElement = Arrays.stream(selectedNodes).map(Node::getUserObject).allMatch(Element.class::isInstance);

            //ACTIVATE BETA FEATURES
//            final boolean betaFeaturesActivated = OMFEnvironmentOptionsGroup_beta.getInstance().isBetaFeatureActivated();
//
//            if (betaFeaturesActivated)
//                addDebugAction(betaCategory, selectedNodes);

            configureFeatureActions(actionsManager);

            l_category.stream().filter(cat -> !cat.isEmpty()).forEach(cat -> actionsManager.addCategory(cat));
        } catch (Exception e) {
            OMFErrorHandler.handleException(e, false);
        }

    }

    private List<NMAction> addDebugAction(MDActionsCategory betaCategory, Node[] selectedNodes) {

        final boolean selectedItemsAreElement = Arrays.stream(selectedNodes).map(Node::getUserObject).allMatch(Element.class::isInstance);

        if (selectedItemsAreElement)
            Arrays.stream(BetaFactory.class.getDeclaredMethods())
                    .filter(method -> method.getName().startsWith("Test_"))
                    .forEach(method -> betaCategory.addAction(new DebugCreateElement(method.getName(), method)));


        return null;
    }

    private void addDebug_optionsAction(MDActionsCategory betaCategory) {
        for (Method setter : OMFEnvironmentOptionsGroup.class.getDeclaredMethods()) {
            if (setter.getName().startsWith("set")) {
                Optional<Method> getter = Arrays.stream(OMFEnvironmentOptionsGroup.class.getDeclaredMethods())
                        .filter(method -> method.getName().startsWith("get" + setter.getName().replaceFirst("set", "")))
                        .findFirst();
                if (getter.isPresent())
                    betaCategory.addAction(new DebugOnOffOptionsBrowser(setter.getName(), OMFEnvironmentOptionsGroup.getInstance(), setter, getter.get()));
            }
        }


    }
}