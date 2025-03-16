/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 */
package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions

import com.google.common.base.Strings
import com.nomagic.actions.NMAction
import com.nomagic.magicdraw.actions.DiagramAction
import com.nomagic.magicdraw.actions.MDAction
import com.nomagic.magicdraw.core.Project
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction
import com.nomagic.magicdraw.ui.browser.Node
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction
import com.nomagic.magicdraw.uml.BaseElement
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFBarrierExecutor
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.DevelopmentException
import com.samares_engineering.omf.omf_core_framework.feature.OMFAutomationManager
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import java.awt.event.ActionEvent
import java.util.*
import java.util.concurrent.Callable
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.swing.KeyStroke


abstract class AUIAction<E : BaseElement, P> protected constructor(
    private var categoryName: String,
    private var name: String,
    shallDeactivateListenerOnTrigger: Boolean
) : UIAction {
    var browserSelectedNodes: Array<Node> = emptyArray()
        private set
    var browserSelectedElements: List<E> = emptyList()
        private set
    var diagramSelectedPresentationElements: List<P> = emptyList()
        private set
    var diagramSelectedElements: List<E> = emptyList()
        private set

    var browserNMAction: NMAction? = null

    var diagramNMAction: NMAction? = null

    var menuNMAction: NMAction? = null

    private var isActivated = true

    var isDeactivateListenerOnTrigger: Boolean = true

    @JvmField
    protected var feature: OMFFeature? = null

    constructor() : this("", "", false)

    init {
        isDeactivateListenerOnTrigger = hasDeactivateListenerAnnotation()
        initTreeActions()
        initDiagramActions()
        initMenuActions()
    }

    /**
     * Initialize the MenuActions, register the action and set the behavior.
     */
    protected open fun initMenuActions() {
        this.menuNMAction = object : MDAction("", getName(), keyStroke, null) {
            override fun actionPerformed(actionEvent: ActionEvent?) {
                super.actionPerformed(actionEvent)
                init()
                executeMenuAction(browserSelectedElements)
                OMFAutomationManager.getInstance().automationTriggered()
            }

            override fun updateState() {
                super.updateState()
                isEnabled = checkMenuAvailability()
            }
        }
    }

    /**
     * Initialize the DiagramActions, register the action and set the behavior.
     */
    protected open fun initDiagramActions() {
        this.diagramNMAction = object : DefaultDiagramAction("", getName(), keyStroke, null) {
            override fun actionPerformed(actionEvent: ActionEvent?) {
                if (OMFUtils.isProjectVoid() || OMFUtils.getProject().activeDiagram == null) return  //CalledBy ConfiguratorAM on MD startup/Project Opening,

                // for some reason, the action is triggered before the updateState, so we need to check availability here ?
                if (!checkDiagramAvailability()) return  //when called with shortcuts,

                super.actionPerformed(actionEvent)
                init()
                (diagramNMAction as DiagramAction?)!!.setDiagram(this.diagram) //TODO: temporary fix, to be removed when the diagram action will be fixed
                executeDiagramAction(diagramSelectedElements)
                OMFAutomationManager.getInstance().automationTriggered()
            }

            override fun getDiagram(): DiagramPresentationElement? {
                return if (super.getDiagram() == null) OMFUtils.getActiveDiagram() else super.getDiagram()
            }

            override fun updateState() {
                super.updateState()
                if (OMFUtils.isProjectVoid() || OMFUtils.getProject().activeDiagram == null) {
                    isEnabled = false
                    return
                }
                isEnabled = checkDiagramAvailability()
            }
        }
    }

    /**
     * Initialize the BrowserActions, register the action and set the behavior.
     */
    protected open fun initTreeActions() {
        this.browserNMAction = object : DefaultBrowserAction("", getName(), keyStroke, null) {
            override fun actionPerformed(actionEvent: ActionEvent?) {
                if (OMFUtils.isProjectVoid()) return
                if (!checkBrowserAvailability()) return  //when called with shortcuts,

                // action is triggered before updateState, so we need to check availability here
                super.actionPerformed(actionEvent)
                init()
                executeBrowserAction(browserSelectedElements)
                OMFAutomationManager.getInstance().automationTriggered()
            }

            override fun updateState() {
                super.updateState()
                if (OMFUtils.isProjectVoid()) {
                    isEnabled = false
                    return
                }
                isEnabled = checkBrowserAvailability()
            }
        }
    }

    open fun init() {
        browserSelectedNodes = getSelectedBrowserNodes()
        browserSelectedElements = getSelectedBrowserElements()
        diagramSelectedPresentationElements = getSelectedDiagramPresentationElements()
        diagramSelectedElements = getSelectedDiagramElements()
        if (Strings.isNullOrEmpty(categoryName)) categoryName = category
        if (Strings.isNullOrEmpty(name)) name = category
    }

    /**
     * Executes the provided Runnable within a barrier. This method is used to ensure that the UI action
     * is executed within a controlled environment where certain conditions are met before and after execution.
     * The barrier controls the execution of the action, handles exceptions, and manages the state of the action.
     *
     * @param runnable The Runnable representing the UI action to be executed.
     */
    fun executeAUIActionWithinBarrier(runnable: Runnable?) {
        if (OMFUtils.isProjectOpened()) {
            OMFBarrierExecutor.executeInSessionWithinBarrier(
                runnable,
                getName(),
                getFeature(),
                isDeactivateListenerOnTrigger
            )
        } else {
            OMFBarrierExecutor.executeWithinBarrier(runnable, getFeature(), isDeactivateListenerOnTrigger)
        }
    }

    /**
     * Execute the behavior defined for DiagramAction, listener will be deactivated during the action, and it will be executed inside a session.
     * By default, the actionToPerfom() method. Override it if there is a need to distinguish DiagramAction of the other
     *
     * @param selectedElements selected elements
     */
    open fun executeDiagramAction(selectedElements: List<@JvmSuppressWildcards E>) {
        executeAUIActionWithinBarrier((Runnable { actionToPerform(selectedElements) }))
    }


    /**
     * Execute the behavior defined for BrowserAction, listener will be deactivated during the action, and it will be executed inside a session.
     * By default, the actionToPerform() method. Override it if there is a need to distinguish BrowserAction of the other
     *
     * @param selectedElements selected elements
     */
    open fun executeBrowserAction(selectedElements: List<@JvmSuppressWildcards E>) {
        executeAUIActionWithinBarrier { actionToPerform(selectedElements) }
    }

    /**
     * Execute the behavior defined for Menu Action, listener will be deactivated during the action, and it will be executed inside a session.
     * By default, the actionToPerform() method. Override it if there is a need to distinguish Menu Action of the other
     *
     * @param selectedElements selected elements
     */
    open fun executeMenuAction(selectedElements: List<@JvmSuppressWildcards E>) {
        executeAUIActionWithinBarrier { actionToPerform(selectedElements) }
    }

    /**
     * Executed action behavior, listener will be deactivated during the action, and it will be executed inside a session.
     * If there is a need to distinguish behavior from different action type, override the according function.
     *
     * @param selectedElements selected elements
     */
    abstract fun actionToPerform(selectedElements: List<@JvmSuppressWildcards E>)

    /**
     * Evaluate if the action shall appear inside the predefined category for Browser action configurator.
     * If there is a need to distinguish check from different action type, override the according function but do not forget to call the checkWithinOMFBarrier method.
     * see: [.checkWithinOMFBarrier]
     * @return isAvailable
     */
    override fun checkBrowserAvailability(): Boolean {
        return checkWithinOMFBarrier {
            isActivated() && checkAvailability(
                getSelectedBrowserElements()
            )
        }
    }

    /**
     * Evaluate if the action shall appear inside the predefined category for Diagram action configurator.
     * If there is a need to distinguish check from different action type, override the according function but do not forget to call the checkWithinOMFBarrier method.
     * see: [.checkWithinOMFBarrier]
     *
     * @return isAvailable
     */
    override fun checkDiagramAvailability(): Boolean {
        return checkWithinOMFBarrier {
            isActivated() && checkAvailability(
                getSelectedDiagramElements()
            )
        }
    }

    /**
     * Evaluate if the action shall appear inside the predefined category for Menu action configurator.
     * If there is a need to distinguish check from different action type, override the according function but do not forget to call the checkWithinOMFBarrier method.
     * see: [.checkWithinOMFBarrier]
     *
     * @return isAvailable
     */
    override fun checkMenuAvailability(): Boolean {
        return checkWithinOMFBarrier {
            isActivated() && checkAvailability(Stream.of(
                getSelectedBrowserElements(), getSelectedDiagramElements()
            )
                .flatMap { obj: List<E> -> obj.stream() }.collect(Collectors.toList())
            )
        }
    }

    /**
     * Evaluate if the action shall appear inside the predefined category for all configurators (Menu, Diagram, Browser).
     * If there is a need to distinguish check from different action type, override the according function.
     * @param selectedElements selected elements
     * @return isAvailable
     */
    abstract fun checkAvailability(selectedElements: List<@JvmSuppressWildcards E>): Boolean

    /**
     * Check if the action is available within the OMF Barrier. <br></br>
     * Use this method if you override the XXXCheckAvailability method (Browser, Diagram, Menu).
     * @param checkAvailability The Callable to check the availability of the action.
     * @return True if the action is available, false otherwise.
     */
    fun checkWithinOMFBarrier(checkAvailability: Callable<Boolean>): Boolean {
        return java.lang.Boolean.TRUE == OMFBarrierExecutor.executeWithinBarrier<Boolean>({
            try {
                return@executeWithinBarrier checkAvailability.call()
            } catch (e: Exception) {
                throw OMFCriticalException(
                    "Error while checking the availability of the action: " + getName(),
                    e,
                    OMFExceptionModifier.DEACTIVATE_FEATURE
                )
            }
        }, getFeature())
    }
    /**
     * Get the selected Nodes inside the Containment Tree.
     * Hypothesis: Order corresponds to the user E selection one.
     *
     * @return selected node list.
     */
    fun getSelectedBrowserNodes(): Array<Node>{
            if (isProjectVoid) return emptyArray()
            val browser = OMFUtils.getProject().browser ?: return emptyArray()
            val containmentTree = browser.containmentTree ?: return emptyArray()

            return containmentTree.selectedNodes
        }

    /**
     * Get the selected Elements inside the Containment Tree.
     * Hypothesis: Order correspond to the user E selection one.
     *
     * @return selected elements list.
     */
    abstract fun getSelectedBrowserElements(): List<E>

    /**
     * Get the Presentation elements of the selected elements inside the active diagram.
     * Hypothesis: Order correspond to the user E selection one.
     *
     * @return selected Presentation E list.
     */
    abstract fun getSelectedDiagramPresentationElements(): List<P>

    /**
     * Get the selected Elements inside the active diagram.
     * Hypothesis: Order correspond to the user E selection one.
     *
     * @return selected elements list.
     */
    abstract fun getSelectedDiagramElements(): List<E>

    /**
     * get the Browser MDAction called by the user.
     *
     * @return DefaultBrowserAction
     */
    override fun getBrowserAction(): NMAction {
        checkAnnotationPresence()
        return browserNMAction!!
    }

    /**
     * get the Diagram MDAction called by the user.
     *
     * @return DefaultDiagramAction
     */
    override fun getDiagramAction(): NMAction {
        checkAnnotationPresence()

        return diagramNMAction!!
    }

    /**
     * get the Menu MDAction called by the user.
     *
     * @return MDAction
     */
    override fun getMenuAction(): NMAction {
        checkAnnotationPresence()

        return menuNMAction!!
    }


    /**
     * Check if the Annotation is present in the declared classes.
     * //TODO Please deploy a solution to execute the check in the build phase. see: https://stackoverflow.com/questions/19252973/how-do-i-validate-an-annotation-at-compile-time
     */
    private fun checkAnnotationPresence() {
        if (javaClass.isAnnotationPresent(com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction::class.java)) return

        LegacyErrorHandler.handleException(
            DevelopmentException(
                "Annotation " + com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction::class.java.simpleName
                        + " present in the class: " + javaClass.simpleName
                        + ", which is mandatory to register actions"
            )
        )
    }

    override fun activate() {
        isActivated = true
    }

    override fun deactivate() {
        isActivated = false
    }

    override fun isActivated(): Boolean {
        return isActivated
    }

    fun getName(): String {
        return javaClass.getAnnotation(
            com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction::class.java
        ).actionName
    }

    override fun getCategory(): String {
        return javaClass.getAnnotation(
            com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction::class.java
        ).category
    }

    val keyStroke: KeyStroke?
        get() = KeyStroke.getKeyStroke(
            java.lang.String.join(
                "->", Arrays.asList(
                    *javaClass.getAnnotation(
                        com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction::class.java
                    ).keyStroke
                )
            )
        )

    override fun isBrowserAction(): Boolean {
        return javaClass.getAnnotation(
            BrowserAction::class.java
        ) != null
    }

    override fun isDiagramAction(): Boolean {
        return javaClass.getAnnotation(
            com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction::class.java
        ) != null
    }

    override fun isMenuAction(): Boolean {
        return javaClass.getAnnotation(
            MenuAction::class.java
        ) != null
    }

    private fun hasDeactivateListenerAnnotation(): Boolean {
        return javaClass.getAnnotation(DeactivateListener::class.java) != null
    }

    override fun getAllActions(): List<NMAction> {
        return Arrays.asList(
            getBrowserAction(),
            getDiagramAction(),
            getMenuAction()
        )
    }

    override fun getFeature(): OMFFeature {
        return feature!!
    }

    override fun initRegistrableItem(feature: OMFFeature) {
        this.feature = feature
    }

    val project: Project
        /**
         * Retrieves the current project instance.
         *
         * @return The current Project instance.
         */
        get() = OMFUtils.getProject()

    val diagram: DiagramPresentationElement
        /**
         * Retrieves the active diagram instance.
         *
         * @return The active Diagram instance.
         */
        get() = OMFUtils.getActiveDiagram()

    val isProjectVoid: Boolean
        /**
         * Checks if the current project is void.
         *
         * @return True if the project is void, false otherwise.
         */
        get() = OMFUtils.isProjectVoid()

    val isProjectOpened: Boolean
        /**
         * Checks if the current project is opened.
         *
         * @return True if the project is opened, false otherwise.
         */
        get() = OMFUtils.isProjectOpened()


    fun setBrowserAction(browserAction: NMAction?) {
        this.browserNMAction = browserAction
    }

    fun setDiagramAction(diagramAction: NMAction?) {
        this.diagramNMAction = diagramAction
    }

    fun setMenuAction(menuAction: NMAction?) {
        this.menuNMAction = menuAction
    }
}
