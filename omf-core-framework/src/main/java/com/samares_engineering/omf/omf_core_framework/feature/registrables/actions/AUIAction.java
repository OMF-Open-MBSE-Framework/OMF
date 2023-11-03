/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions;


import com.google.common.base.Strings;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.ui.browser.Browser;
import com.nomagic.magicdraw.ui.browser.ContainmentTree;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.ErrorHandler2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.RollbackException2;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.DevelopmentException;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.OMFAutomationManager;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.listeners.ListenerManager;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AUIAction implements UIAction {

    private Node[] browserSelectedNodes;
    private List<Element> browserSelectedElements;
    private List<PresentationElement> diagramSelectedPresentationElements;
    private List<Element> diagramSelectedElements;

    private DefaultBrowserAction browserAction;

    private DefaultDiagramAction diagramAction;

    private com.nomagic.magicdraw.actions.MDAction menuAction;

    private String name;
    private String categoryName;

    private boolean isActivated = true;

    private boolean deactivateListenerOnTrigger = true;

    protected MDFeature feature;

    public AUIAction() {
        this("", "", false);
    }

    protected AUIAction(String categoryName, String name, boolean shallDeactivateListenerOnTrigger) {
        this.categoryName = categoryName;
        this.name = name;

        deactivateListenerOnTrigger = hasDeactivateListenerAnnotation();
        this.browserAction = new DefaultBrowserAction("", getName(), getKeyStroke(), null) {
            @Override
            public void actionPerformed(@CheckForNull ActionEvent actionEvent) {
                super.actionPerformed(actionEvent);
                init();
                executeBrowserAction(browserSelectedElements);
                OMFAutomationManager.getInstance().automationTriggered();
            }

            @Override
            public void updateState() {
                super.updateState();
                setEnabled(checkBrowserAvailability());
            }
        };
        this.diagramAction = new DefaultDiagramAction("", getName(), getKeyStroke(), null) {
            @Override
            public void actionPerformed(@CheckForNull ActionEvent actionEvent) {
                super.actionPerformed(actionEvent);
                init();
                diagramAction.setDiagram(this.getDiagram()); //TODO: temporary fix, to be removed when the diagram action will be fixed
                executeDiagramAction(diagramSelectedElements);
                OMFAutomationManager.getInstance().automationTriggered();
            }

            @Override
            public void updateState() {
                super.updateState();
                setEnabled(checkDiagramAvailability());
            }
        };
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
     * Execute the behavior defined for DiagramAction, listener will be deactivated during the action, and it will be executed inside a session.
     * By default the actionToPerfom() method. Override it if there is a need to distinguish DiagramAction of the other
     *
     * @param selectedElements selected elements
     */
    protected void executeDiagramAction(List<Element> selectedElements) {
        if (deactivateListenerOnTrigger)
            ListenerManager.getInstance().deactivateAllListeners();
        try {
            SessionManager.getInstance().executeInsideSession(OMFUtils.getProject(), getName(), () -> {
                try {
                    actionToPerform(selectedElements);
                } catch (OMFException2 e) {
                    ErrorHandler2.getInstance().handleException(e, getFeature());
                } catch (RuntimeException e) {
                    ErrorHandler2.getInstance().handleException(e, getFeature());
                }
            });
        } catch (RollbackException2 rollbackException) {
            OMFErrorHandler.handleException(rollbackException);
        }
    }

    /**
     * Execute the behavior defined for BrowserAction, listener will be deactivated during the action, and it will be executed inside a session.
     * By default the actionToPerform() method. Override it if there is a need to distinguish BrowserAction of the other
     *
     * @param selectedElements selected elements
     */
    protected void executeBrowserAction(List<Element> selectedElements) {
        if (deactivateListenerOnTrigger)
            ListenerManager.getInstance().deactivateAllListeners();
        try {
            SessionManager.getInstance().executeInsideSession(OMFUtils.getProject(), getName(), () -> {
                try {
                    actionToPerform(selectedElements);
                } catch (OMFException2 e) {
                    ErrorHandler2.getInstance().handleException(e, getFeature());
                } catch (RuntimeException e) {
                    ErrorHandler2.getInstance().handleException(e, getFeature());
                }
            });
        } catch (RollbackException2 rollbackException) {
            //OMFErrorHandler.handleException(rollbackException);
        }
    }

    /**
     * Execute the behavior defined for Menu Action, listener will be deactivated during the action, and it will be executed inside a session.
     * By default the actionToPerform() method. Override it if there is a need to distinguish Menu Action of the other
     *
     * @param selectedElements selected elements
     */
    protected void executeMenuAction(List<Element> selectedElements) {
        if (deactivateListenerOnTrigger)
            ListenerManager.getInstance().deactivateAllListeners();
        try {
            SessionManager.getInstance().executeInsideSession(OMFUtils.getProject(), getName(), () -> {
                try {
                    actionToPerform(selectedElements);
                } catch (OMFException2 e) {
                    ErrorHandler2.getInstance().handleException(e, getFeature());
                } catch (RuntimeException e) {
                    ErrorHandler2.getInstance().handleException(e, getFeature());
                }
            });
        } catch (RollbackException2 rollbackException) {
            //OMFErrorHandler.handleException(rollbackException);
        }
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
     * If there is a need to distinguish check from different action type, override the according function.
     *
     * @return isAvailable
     */
    public boolean checkBrowserAvailability() {
        return isActivated() && checkAvailability(getSelectedBrowserElements());
    }

    /**
     * Evaluate if the action shall appear inside the predefined category for Diagram action configurator.
     * If there is a need to distinguish check from different action type, override the according function.
     *
     * @return isAvailable
     */
    public boolean checkDiagramAvailability() {
        return isActivated() && checkAvailability(getSelectedDiagramElements());
    }

    /**
     * Evaluate if the action shall appear inside the predefined category for Menu action configurator.
     * If there is a need to distinguish check from different action type, override the according function.
     *
     * @return isAvailable
     */
    public boolean checkMenuAvailability() {
        return isActivated() && checkAvailability(Stream.of(getSelectedBrowserElements(), getSelectedDiagramElements())
                .flatMap(Collection::stream).collect(Collectors.toList()));
    }

    /**
     * Evaluate if the action shall appear inside the predefined category for all configurators (Menu, Diagram, Browser).
     * If there is a need to distinguish check from different action type, override the according function.
     *
     * @return isAvailable
     */
    public abstract boolean checkAvailability(List<Element> selectedElements);

    /**
     * Get the selected Nodes inside the Containment Tree.
     * Hypothesis: Order correspond to the user element selection one.
     *
     * @return selected node list.
     */
    public Node[] getSelectedBrowserNodes() {
        if(OMFUtils.getProject() == null)
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
        if(OMFUtils.getProject() == null)
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
    public DefaultBrowserAction getBrowserAction() {
        checkAnnotationPresence();
        return browserAction;
    }

    /**
     * get the Diagram MDAction called by the user.
     *
     * @return DefaultBrowserAction
     */
    public DefaultDiagramAction getDiagramAction() {
        checkAnnotationPresence();

        return diagramAction;
    }

    /**
     * get the Menu MDAction called by the user.
     *
     * @return MDAction
     */
    public com.nomagic.magicdraw.actions.MDAction getMenuAction() {
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

        OMFErrorHandler.handleException(new DevelopmentException(
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

    public List<com.nomagic.magicdraw.actions.MDAction> getAllActions() {
        return Arrays.asList(
                getBrowserAction(),
                getDiagramAction(),
                getMenuAction());
    }

    @Override
    public MDFeature getFeature() {
        return feature;
    }

    @Override
    public void initRegistrableItem(MDFeature feature) {
        this.feature = feature;
    }
}
