/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.stateactions;


import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFBarrierExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.OMFAutomationManager;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.stateactions.stateMDAction.StateBrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.stateactions.stateMDAction.StateDiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.stateactions.stateMDAction.StateMenuAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckForNull;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * OMF StateAction
 */
public abstract class StateAction extends AUIAction {

    boolean isChecked = false;
//    private StateBrowserAction browserAction;
    private NMAction browserAction;
    private NMAction diagramAction;
    private NMAction menuAction;

    @Override
    public AUIAction init() {
        return super.init();
    }

    /**
     * Initialize the StateBrowserAction, register the action, update the state and set the behavior.
     */
    @Override
    protected void initTreeActions() {

        browserAction = new StateBrowserAction("", getName(), getKeyStroke(), null) {
            @Override
            public void actionPerformed(@CheckForNull ActionEvent actionEvent) {
                if (OMFUtils.isProjectVoid()) return;
                if (!checkBrowserAvailability()) return; //when called with shortcuts,
                // action is triggered before updateState, so we need to check availability here
                super.actionPerformed(actionEvent);
                init();
                executeBrowserAction(getBrowserSelectedElements());
                OMFAutomationManager.getInstance().automationTriggered();
            }

            @Override
            public void updateState() {
                super.updateState();
                if (OMFUtils.isProjectVoid()) {
                    setEnabled(false);
                    return;
                }
                setState(checkBrowserState(getBrowserSelectedElements()));
                setEnabled(checkBrowserAvailability());
            }

            @Override
            public boolean getState() {
                return checkBrowserState(getBrowserSelectedElements());
            }
        };
        setBrowserAction(browserAction);
    }

    /**
     * Initialize the StateDiagramAction, register the action, update the state and set the behavior.
     */
    @Override
    protected void initDiagramActions() {
        diagramAction = new StateDiagramAction("", getName(), getKeyStroke(), null) {
            @Override
            public void actionPerformed(@CheckForNull ActionEvent actionEvent) {
                if(OMFUtils.isProjectVoid() || OMFUtils.getProject().getActiveDiagram() == null) return;//CalledBy ConfiguratorAM on MD startup/Project Opening,
                // for some reason, the action is triggered before the updateState, so we need to check availability here ?
                if (!checkDiagramAvailability()) return; //when called with shortcuts,
                super.actionPerformed(actionEvent);
                init();
                setDiagram(this.getDiagram()); //TODO: temporary fix, to be removed when the diagram action will be fixed
                executeDiagramAction(getDiagramSelectedElements());
                OMFAutomationManager.getInstance().automationTriggered();
            }

            @CheckForNull
            @Override
            public DiagramPresentationElement getDiagram() {
                return super.getDiagram() == null ? OMFUtils.getActiveDiagram() : super.getDiagram();
            }

            @Override
            public void setDiagram(@Nullable DiagramPresentationElement diagramPresentationElement) {
                super.setDiagram(diagramPresentationElement == null ? OMFUtils.getActiveDiagram().getDiagramPresentationElement(): diagramPresentationElement);
            }

            @Override
            public void updateState() {
                super.updateState();
                if(OMFUtils.isProjectVoid() || OMFUtils.getProject().getActiveDiagram() == null) {
                    setEnabled(false);
                    return;
                }
                setState(checkBrowserState(getDiagramSelectedElements()));
                setEnabled(checkDiagramAvailability());
            }

            @Override
            public boolean getState() {
                return checkMenuState(getDiagramSelectedElements());
            }
        };

        setDiagramAction(diagramAction);
    }

    /**
     * Initialize the StateMenuAction, register the action, update the state and set the behavior.
     */
    @Override
    protected void initMenuActions() {
        menuAction = new StateMenuAction("", getName(), getKeyStroke(), null) {
            @Override
            public void actionPerformed(@CheckForNull ActionEvent actionEvent) {
                super.actionPerformed(actionEvent);
                init();
                executeMenuAction(getBrowserSelectedElements());
                OMFAutomationManager.getInstance().automationTriggered();
            }

            @Override
            public void updateState() {
                super.updateState();
                setState(checkBrowserState(getBrowserSelectedElements()));
                setEnabled(checkMenuAvailability());
            }

            @Override
            public boolean getState() {
                return checkDiagramState(getBrowserSelectedElements());
            }
        };

        setMenuAction(menuAction);
    }

    /**
     * Check the state of the selected elements when using StateBrowserAction then return boolean
     * @param selectedElement List&lt;Element&gt;
     * @return boolean
     */
    protected boolean checkBrowserState(List<Element> selectedElement){
        return OMFBarrierExecutor.executeWithinBarrier(() -> checkState(selectedElement));
    }

    /**
     * Check the state of the selected elements when using DiagramStateAction then return boolean
     * @param selectedElement List&lt;Element&gt;
     * @return boolean
     */
    protected boolean checkDiagramState(List<Element> selectedElement){
       return OMFBarrierExecutor.executeWithinBarrier(() -> checkState(selectedElement));
    }

    /**
     * Check the state of the selected elements when using MenuStateAction then return boolean
     * @param selectedElement List&lt;Element&gt;
     * @return boolean
     */
    protected boolean checkMenuState(List<Element> selectedElement){
        return OMFBarrierExecutor.executeWithinBarrier(() -> checkState(selectedElement));
    }

    /**
     * Check the state of the selected elements when using StateAction then return boolean
     * @param selectedElement List&lt;Element&gt;
     * @return boolean
     */
    public abstract boolean checkState(List<Element> selectedElement);

    public StateBrowserAction getStateBrowserAction() {
        return (StateBrowserAction) browserAction;
    }

    public StateDiagramAction getStateDiagramAction() {
        return (StateDiagramAction) diagramAction;
    }

    public StateMenuAction getStateMenuAction() {
        return (StateMenuAction) menuAction;
    }
}


