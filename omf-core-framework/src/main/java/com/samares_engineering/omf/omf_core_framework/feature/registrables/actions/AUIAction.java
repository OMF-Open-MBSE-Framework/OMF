/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions;


import com.google.common.base.Strings;
import com.nomagic.actions.NMAction;
import com.nomagic.actions.NMStateAction;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.ui.browser.Browser;
import com.nomagic.magicdraw.ui.browser.ContainmentTree;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFBarrierExecutor;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.DevelopmentException;
import com.samares_engineering.omf.omf_core_framework.feature.OMFAutomationManager;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AUIAction implements UIAction {

    private Node[] browserSelectedNodes;
    private List<Element> browserSelectedElements;
    private List<PresentationElement> diagramSelectedPresentationElements;
    private List<Element> diagramSelectedElements;

    NMAction browserAction;

    NMAction diagramAction;

    NMAction menuAction;

    private String name;
    private String categoryName;

    private boolean isActivated = true;

    private boolean deactivateListenerOnTrigger = true;

    protected OMFFeature feature;

    public AUIAction() {
        this("", "", false);
    }

    protected AUIAction(String categoryName, String name, boolean shallDeactivateListenerOnTrigger) {
        this.categoryName = categoryName;
        this.name = name;

        deactivateListenerOnTrigger = hasDeactivateListenerAnnotation();
        initTreeActions();
        initDiagramActions();
        initMenuActions();
    }

    /**
     * Initialize the MenuActions, register the action and set the behavior.
     */
    protected void initMenuActions() {
        this.menuAction = new com.nomagic.magicdraw.actions.MDAction("", getName(), getKeyStroke(), null) {
            @Override
            public void actionPerformed(@CheckForNull ActionEvent actionEvent) {
                super.actionPerformed(actionEvent);
                init();
                executeMenuAction(browserSelectedElements);
                OMFAutomationManager.getInstance().automationTriggered();
            }

            @Override
            public void updateState() {
                super.updateState();
                setEnabled(checkMenuAvailability());
            }
        };
    }

    /**
     * Initialize the DiagramActions, register the action and set the behavior.
     */
    protected void initDiagramActions() {
        this.diagramAction = new DefaultDiagramAction("", getName(), getKeyStroke(), null) {
            @Override
            public void actionPerformed(@CheckForNull ActionEvent actionEvent) {
                if(OMFUtils.isProjectVoid() || OMFUtils.getProject().getActiveDiagram() == null) return;//CalledBy ConfiguratorAM on MD startup/Project Opening,
                // for some reason, the action is triggered before the updateState, so we need to check availability here ?
                if (!checkDiagramAvailability()) return; //when called with shortcuts,
                super.actionPerformed(actionEvent);
                init();
                ((com.nomagic.magicdraw.actions.DiagramAction) diagramAction).setDiagram(this.getDiagram()); //TODO: temporary fix, to be removed when the diagram action will be fixed
                executeDiagramAction(diagramSelectedElements);
                OMFAutomationManager.getInstance().automationTriggered();
            }

            @CheckForNull
            @Override
            public DiagramPresentationElement getDiagram() {
                return super.getDiagram() == null ? OMFUtils.getActiveDiagram() : super.getDiagram();
            }

            @Override
            public void updateState() {
                super.updateState();
                if(OMFUtils.isProjectVoid() || OMFUtils.getProject().getActiveDiagram() == null) {
                    setEnabled(false);
                    return;
                }
                setEnabled(checkDiagramAvailability());
            }
        };
    }

    /**
     * Initialize the BrowserActions, register the action and set the behavior.
     */
    protected void initTreeActions() {
        this.browserAction = new DefaultBrowserAction("", getName(), getKeyStroke(), null) {
            @Override
            public void actionPerformed(@CheckForNull ActionEvent actionEvent) {
                if(OMFUtils.isProjectVoid()) return;
                if(!checkBrowserAvailability()) return; //when called with shortcuts,
                // action is triggered before updateState, so we need to check availability here
                super.actionPerformed(actionEvent);
                init();
                executeBrowserAction(browserSelectedElements);
                OMFAutomationManager.getInstance().automationTriggered();
            }

            @Override
            public void updateState() {
                super.updateState();
                if(OMFUtils.isProjectVoid()) {
                    setEnabled(false);
                    return;
                }
                setEnabled(checkBrowserAvailability());
            }
        };
    }

    public AUIAction init() {
        browserSelectedNodes = getSelectedBrowserNodes();
        browserSelectedElements = getSelectedBrowserElements();
        diagramSelectedPresentationElements = getSelectedDiagramPresentationElements();
        diagramSelectedElements = getSelectedDiagramElements();
        if (Strings.isNullOrEmpty(categoryName))
            categoryName = getCategory();
        if (Strings.isNullOrEmpty(name))
            name = getCategory();
        return this;
    }

    /**
     * Executes the provided Runnable within a barrier. This method is used to ensure that the UI action
     * is executed within a controlled environment where certain conditions are met before and after execution.
     * The barrier controls the execution of the action, handles exceptions, and manages the state of the action.
     *
     * @param runnable The Runnable representing the UI action to be executed.
     */
    public void executeAUIActionWithinBarrier(Runnable runnable) {
        if (OMFUtils.isProjectOpened()) {
            OMFBarrierExecutor.executeInSessionWithinBarrier(runnable, getName(), getFeature(), isDeactivateListenerOnTrigger());
        } else {
            OMFBarrierExecutor.executeWithinBarrier(runnable, getFeature(), isDeactivateListenerOnTrigger());
        }
    }

    /**
     * Execute the behavior defined for DiagramAction, listener will be deactivated during the action, and it will be executed inside a session.
     * By default, the actionToPerfom() method. Override it if there is a need to distinguish DiagramAction of the other
     *
     * @param selectedElements selected elements
     */
    public void executeDiagramAction(List<Element> selectedElements) {
        executeAUIActionWithinBarrier((() -> actionToPerform(selectedElements)));
    }


    /**
     * Execute the behavior defined for BrowserAction, listener will be deactivated during the action, and it will be executed inside a session.
     * By default, the actionToPerform() method. Override it if there is a need to distinguish BrowserAction of the other
     *
     * @param selectedElements selected elements
     */
    public void executeBrowserAction(List<Element> selectedElements) {
       executeAUIActionWithinBarrier(() -> actionToPerform(selectedElements));
    }

    /**
     * Execute the behavior defined for Menu Action, listener will be deactivated during the action, and it will be executed inside a session.
     * By default, the actionToPerform() method. Override it if there is a need to distinguish Menu Action of the other
     *
     * @param selectedElements selected elements
     */
    public void executeMenuAction(List<Element> selectedElements) {
       executeAUIActionWithinBarrier(() -> actionToPerform(selectedElements));
    }

    /**
     * Executed action behavior, listener will be deactivated during the action, and it will be executed inside a session.
     * If there is a need to distinguish behavior from different action type, override the according function.
     *
     * @param selectedElements selected elements
     */
    public abstract void actionToPerform(List<Element> selectedElements);

    /**
     * Evaluate if the action shall appear inside the predefined category for Browser action configurator.
     * If there is a need to distinguish check from different action type, override the according function but do not forget to call the checkWithinOMFBarrier method.
     * see: {@link #checkWithinOMFBarrier(Callable)}
     * @return isAvailable
     */
    public boolean checkBrowserAvailability() {
        return checkWithinOMFBarrier(() -> isActivated() && checkAvailability(getSelectedBrowserElements()));
    }

    /**
     * Evaluate if the action shall appear inside the predefined category for Diagram action configurator.
     * If there is a need to distinguish check from different action type, override the according function but do not forget to call the checkWithinOMFBarrier method.
     * see: {@link #checkWithinOMFBarrier(Callable)}
     *
     * @return isAvailable
     */
    public boolean checkDiagramAvailability() {
        return checkWithinOMFBarrier(() -> isActivated() && checkAvailability(getSelectedDiagramElements()));
    }

    /**
     * Evaluate if the action shall appear inside the predefined category for Menu action configurator.
     * If there is a need to distinguish check from different action type, override the according function but do not forget to call the checkWithinOMFBarrier method.
     * see: {@link #checkWithinOMFBarrier(Callable)}
     *
     * @return isAvailable
     */
    public boolean checkMenuAvailability() {
        return checkWithinOMFBarrier(() -> isActivated() && checkAvailability(Stream.of(getSelectedBrowserElements(), getSelectedDiagramElements())
                .flatMap(Collection::stream).collect(Collectors.toList())));
    }

    /**
     * Evaluate if the action shall appear inside the predefined category for all configurators (Menu, Diagram, Browser).
     * If there is a need to distinguish check from different action type, override the according function.
     * @param selectedElements selected elements
     * @return isAvailable
     */
    public abstract boolean checkAvailability(List<Element> selectedElements);

    /**
     * Check if the action is available within the OMF Barrier. <br>
     * Use this method if you override the XXXCheckAvailability method (Browser, Diagram, Menu).
     * @param checkAvailability The Callable to check the availability of the action.
     * @return True if the action is available, false otherwise.
     */
    public boolean checkWithinOMFBarrier(Callable<Boolean> checkAvailability) {
        return Boolean.TRUE.equals(OMFBarrierExecutor.<Boolean>executeWithinBarrier(() ->{
            try {
                return checkAvailability.call();
            }catch (Exception e){
                throw new OMFCriticalException("Error while checking the availability of the action: " + getName(), e, OMFExceptionModifier.DEACTIVATE_FEATURE);
            }
        }, getFeature()));
    }

    /**
     * Get the selected Nodes inside the Containment Tree.
     * Hypothesis: Order corresponds to the user element selection one.
     *
     * @return selected node list.
     */
    public Node[] getSelectedBrowserNodes() {
        if(isProjectVoid())
            return null;
        Browser browser = OMFUtils.getProject().getBrowser();
        if(browser == null)
            return null;
        ContainmentTree containmentTree = browser.getContainmentTree();
        if (containmentTree == null)
            return null;

        return containmentTree.getSelectedNodes();
    }

    /**
     * Get the selected Elements inside the Containment Tree.
     * Hypothesis: Order correspond to the user element selection one.
     *
     * @return selected elements list.
     */
    public List<Element> getSelectedBrowserElements() {
        if (getSelectedBrowserNodes() == null)
            return Collections.emptyList();
        return Arrays.stream(getSelectedBrowserNodes())
                .map(Node::getUserObject)
                .filter(Objects::nonNull)
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Get the Presentation elements of the selected elements inside the active diagram.
     * Hypothesis: Order correspond to the user element selection one.
     *
     * @return selected Presentation Element list.
     */
    public List<PresentationElement> getSelectedDiagramPresentationElements() {
        if(isProjectVoid())
            return Collections.emptyList();
        DiagramPresentationElement activeDiagram = OMFUtils.getProject().getActiveDiagram();
        return Objects.nonNull(activeDiagram)? activeDiagram.getSelected(): new ArrayList<>();
    }

    /**
     * Get the selected Elements inside the active diagram.
     * Hypothesis: Order correspond to the user element selection one.
     *
     * @return selected elements list.
     */
    public List<Element> getSelectedDiagramElements() {
        return getSelectedDiagramPresentationElements().stream()
                .map(PresentationElement::getElement)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * get the Browser MDAction called by the user.
     *
     * @return DefaultBrowserAction
     */
    public NMAction getBrowserAction() {
        checkAnnotationPresence();
        return browserAction;
    }

    /**
     * get the Diagram MDAction called by the user.
     *
     * @return DefaultDiagramAction
     */
    public NMAction getDiagramAction() {
        checkAnnotationPresence();

        return diagramAction;
    }

    /**
     * get the Menu MDAction called by the user.
     *
     * @return MDAction
     */
    public NMAction getMenuAction() {
        checkAnnotationPresence();

        return menuAction;
    }


    /**
     * Check if the Annotation is present in the declared classes.
     * //TODO Please deploy a solution to execute the check in the build phase. see: https://stackoverflow.com/questions/19252973/how-do-i-validate-an-annotation-at-compile-time
     */
    private void checkAnnotationPresence() {
        if (getClass().isAnnotationPresent(MDAction.class))
            return;

        LegacyErrorHandler.handleException(new DevelopmentException(
                "Annotation " + MDAction.class.getSimpleName()
                        + " present in the class: " + getClass().getSimpleName()
                        + ", which is mandatory to register actions"));

    }

    public void activate() {
        isActivated = true;
    }

    public void deactivate() {
        isActivated = false;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public boolean isDeactivateListenerOnTrigger() {
        return deactivateListenerOnTrigger;
    }

    public void setDeactivateListenerOnTrigger(boolean deactivateListenerOnTrigger) {
        this.deactivateListenerOnTrigger = deactivateListenerOnTrigger;
    }

    public String getName() {
        return getClass().getAnnotation(MDAction.class).actionName();
    }

    public String getCategory() {
        return getClass().getAnnotation(MDAction.class).category();
    }

    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(String.join("->", Arrays.asList(getClass().getAnnotation(MDAction.class).keyStroke())));
    }

    public boolean isBrowserAction() {
        return getClass().getAnnotation(BrowserAction.class) != null;
    }

    public boolean isDiagramAction() {
        return getClass().getAnnotation(DiagramAction.class) != null;
    }

    public boolean isMenuAction() {
        return getClass().getAnnotation(MenuAction.class) != null;
    }

    private boolean hasDeactivateListenerAnnotation() {
        return getClass().getAnnotation(DeactivateListener.class) != null;
    }

    public List<NMAction> getAllActions() {
        return Arrays.asList(
                getBrowserAction(),
                getDiagramAction(),
                getMenuAction());
    }

    @Override
    public OMFFeature getFeature() {
        return feature;
    }

    @Override
    public void initRegistrableItem(OMFFeature feature) {
        this.feature = feature;
    }

    public Node[] getBrowserSelectedNodes() {
        return browserSelectedNodes;
    }

    public List<Element> getBrowserSelectedElements() {
        return browserSelectedElements;
    }

    public List<PresentationElement> getDiagramSelectedPresentationElements() {
        return diagramSelectedPresentationElements;
    }

    public List<Element> getDiagramSelectedElements() {
        return diagramSelectedElements;
    }

    /**
     * Retrieves the current project instance.
     *
     * @return The current Project instance.
     */
    public Project getProject() {
        return OMFUtils.getProject();
    }

    /**
     * Retrieves the active diagram instance.
     *
     * @return The active Diagram instance.
     */
    public DiagramPresentationElement getDiagram() {
        return OMFUtils.getActiveDiagram();
    }

    /**
     * Checks if the current project is void.
     *
     * @return True if the project is void, false otherwise.
     */
    public boolean isProjectVoid() {
        return OMFUtils.isProjectVoid();
    }

    /**
     * Checks if the current project is opened.
     *
     * @return True if the project is opened, false otherwise.
     */
    public boolean isProjectOpened() {
        return OMFUtils.isProjectOpened();
    }


    public void setBrowserAction(NMAction browserAction) {
        this.browserAction = browserAction;
    }

    public void setDiagramAction(NMAction diagramAction) {
        this.diagramAction = diagramAction;
    }

    public void setMenuAction(NMAction menuAction) {
        this.menuAction = menuAction;
    }
}
