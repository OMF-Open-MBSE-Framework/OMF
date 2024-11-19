/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.templates;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.ActionsProvider;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.ui.browser.ContainmentTree;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.uml.ElementIcon;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import org.junit.jupiter.api.Assertions;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO This should probably be removed as launching actions is now done with a method in AbstractTestCase
public abstract class MDActionsBrowserMenuTestCase extends AModelComparatorTestCase {

    protected String elementToTestID;
    protected String actionToTestName;
    protected String mdActionsCategoryName;

    @Override
    public void testAction() {

        Element element = setElementToTest(elementToTestID);
        setNameActionToTest(actionToTestName);
        setNameMDActionCategoryToTest(mdActionsCategoryName);

        ActionsManager actionManager = ActionsProvider.getContainmentBrowserContextActions(OMFUtils.getProject().getBrowser().getContainmentTree());
        ContainmentTree tree = OMFUtils.getProject().getBrowser().getContainmentTree();
        tree.setSelectedNodes(new Node[]{new Node(element, ElementIcon.getIcon(element))});

        List<BrowserContextAMConfigurator> browserMenuConfigurators = getBrowserMenus();
        browserMenuConfigurators.forEach(configurator -> configurator.configure(actionManager, tree));
        ActionsCategory mdActionsCategory = getCategory(actionManager, mdActionsCategoryName);

        executeAction(actionToTestName, element, mdActionsCategory);
    }

    /**
     * from the actionManager will search for all the registered Category. If absent the test will fail.
     * @param actionManager ActionsManager
     * @param mdActionsCategoryName String
     * @return ActionsCategory
     */
    public ActionsCategory getCategory(ActionsManager actionManager, String mdActionsCategoryName) {
        Optional<ActionsCategory> optCategory = actionManager.getCategories().stream().filter(cat -> cat.getName().equals(mdActionsCategoryName)).findFirst();

        if (optCategory.isEmpty())
            Assertions.fail("[MDActionTesting] " + testCaseID + " browser menu category : " + mdActionsCategoryName + " was not found");

        return optCategory.get();
    }

    /**
     * Get the element to test with his name
     * @param elementId String
     * @return elementToTest Element
     */
    public Element setElementToTest(String elementId){
        return findTestedElementByID(elementId);
    }

    /**
     * Set the name of the action to test
     * @param actionToTestName String
     */
    public void setNameActionToTest(String actionToTestName){
        this.actionToTestName = actionToTestName;
    }

    /**
     * Set the name of the MDActionCategory to test
     * @param mdActionsCategoryName String
     */
    public void setNameMDActionCategoryToTest(String mdActionsCategoryName){
        this.mdActionsCategoryName = mdActionsCategoryName;
    }

    /**
     * Get all  BrowserMenuConfigurators registered. BrowserMenuConfigurator hosts the Categories and the Actions
     * @return List of all the browserMenuConfigurator registered
     */
    public List<BrowserContextAMConfigurator> getBrowserMenus(){
        ActionsConfiguratorsManager actionsConfiguratorsManager = ActionsConfiguratorsManager.getInstance();

        Optional<Method> optMethod = Arrays.stream(actionsConfiguratorsManager.getClass().getDeclaredMethods()).filter(m -> m.getName().equals("collectConfigurators")).findAny();
        if(optMethod.isEmpty())
            Assertions.fail("[Technical error] Cannot retrieved the Browser configurator");


        List accessibleBrowserMenu = null;
        try {
            Method method = optMethod.get();
            method.setAccessible(true);
            Object res = method.invoke(actionsConfiguratorsManager,"ContainmentBrowserMenu");
            if(res != null && res instanceof List)
                accessibleBrowserMenu = (List) res;
        } catch (IllegalAccessException | InvocationTargetException e) {
            Assertions.fail("[Technical error] Cannot retrieved the Browser configurator");
            throw new RuntimeException(e);
        }

        return (List<BrowserContextAMConfigurator>) accessibleBrowserMenu.stream()
                .filter(BrowserContextAMConfigurator.class::isInstance)
                .map(BrowserContextAMConfigurator.class::cast)
                .collect(Collectors.toList());


    }

    /**
     * Execute an NMAction on a related Element
     * @param actionToTestName String
     * @param element Element
     * @param mdActionsCategory MDActionsCategory
     */
    public void executeAction(String actionToTestName, Element element, ActionsCategory mdActionsCategory){
        long now = System.currentTimeMillis();
        ActionEvent actionEvent = new ActionEvent(ActionEvent.ACTION_PERFORMED,
                1001,
                actionToTestName,
                now,
                16);
        actionEvent.setSource(element);

        mdActionsCategory.getActions().stream().filter(nmAction -> nmAction.getName().equalsIgnoreCase(actionToTestName))
                .findFirst()
                .ifPresentOrElse(action -> action.actionPerformed(actionEvent), () -> fail("Action: " + actionToTestName + " was not found in category: " + mdActionsCategory));

    }

}

