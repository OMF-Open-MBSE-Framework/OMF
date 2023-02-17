/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature.registrables.actions.actions.configurators;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.samares.omf.core.feature.registrables.actions.actions.AUIAction;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class FeatureActionConfigurator {

    List<AUIAction> genericActions = new ArrayList<>();


    protected void configureFeatureActions(ActionsManager actionsManager) {
        Predicate<? super AUIAction> check = null;

        resetMDActions(actionsManager);
        if(BrowserContextAMConfigurator.class.isInstance(this)) {
            check = AUIAction::checkBrowserAvailability;
            genericActions.stream()
                    .filter(check)
                    .forEach(action -> this.registerBrowserAction(actionsManager, findOrCreateCategory(actionsManager, action), action));
//            triggerListener(ConfiguratorKind.BROWSER);
        }


        if(DiagramContextAMConfigurator.class.isInstance(this)) {
            check = AUIAction::checkDiagramAvailability;
            genericActions.stream()
                    .filter(check)
                    .forEach(action -> this.registerDiagramAction(actionsManager, findOrCreateCategory(actionsManager, action), action));
        }


        // registering MenuActions
        if(OMFMainMenuConfigurator.class.isInstance(this)) {
            genericActions.stream()
                    .filter(AUIAction::isMenuAction)
                    .forEach(action -> this.registerMenuAction(actionsManager, findOrCreateCategory(actionsManager, action), action, action.checkMenuAvailability()));
//            triggerListener(ConfiguratorKind.MENU);
        }
    }


    public void resetMDActions(ActionsManager actionsManager) {
        genericActions.forEach(action ->
            findCategory(actionsManager, action).ifPresent(category ->
                action.getAllActions().forEach(category::removeAction)
            )
        );
    }


    private void registerMenuAction(ActionsManager actionsManager, MDActionsCategory category, AUIAction menuAction,
                                    boolean isEnabled) {
        if(!actionsManager.getCategories().contains(category) ) {
            actionsManager.addCategory(category);
            category.setNested(true);
        }

        MDAction action = menuAction.getMenuAction();
        if(!category.getActions().contains(menuAction.getMenuAction()))
            category.addAction(menuAction.getMenuAction());

        action.setEnabled(isEnabled);
    }

    /**
     * register an action into the category, If the category doesn't exist it will register it.
     * @param actionsManager
     * @param category
     * @param action
     */
    protected void registerBrowserAction(ActionsManager actionsManager, MDActionsCategory category, AUIAction action) {
        if(!actionsManager.getCategories().contains(category) ) {
            actionsManager.addCategory(category);
            category.setNested(true);
        }

        if(action.isBrowserAction())
            category.addAction(action.getBrowserAction());

    }
    /**
     * register an action into the category, If the category doesn't exist it will register it.
     * @param actionsManager
     * @param category
     * @param action
     */
    protected void registerDiagramAction(ActionsManager actionsManager, MDActionsCategory category, AUIAction action) {
        if(!actionsManager.getCategories().contains(category) ) {
            actionsManager.addCategory(category);
            category.setNested(true);
        }

        if(action.isDiagramAction())
            category.addAction(action.getDiagramAction());
    }

    protected MDActionsCategory findOrCreateCategory(ActionsManager actionsManager, AUIAction AUIAction) {
        String categoryName = AUIAction.getCategory();

        Optional<MDActionsCategory> optCategory = findCategory(actionsManager, AUIAction);
        if(optCategory.isPresent())
            return optCategory.get();

        return new MDActionsCategory("FeatureCategoryID-"+ categoryName, categoryName){
            @Override
            public void updateState() {
//                super.updateState();
                //refresh MenuActionState
                getActions().stream()
                        .filter(AUIAction.class::isInstance)
                        .map(AUIAction.class::cast)
                        .forEach( action -> action.getMenuAction().setEnabled(action.checkMenuAvailability()));
                this.setEnabled(getActions().stream().anyMatch(NMAction::isEnabled));
            }
        };
    }

    private Optional<MDActionsCategory> findCategory(ActionsManager actionsManager, AUIAction AUIAction) {
        String categoryName = AUIAction.getCategory();
        return actionsManager.getCategories().stream()
                .filter(MDActionsCategory.class::isInstance)
                .map(MDActionsCategory.class::cast)
                .filter(cat -> cat.getName().equals(categoryName)).findAny();
    }


    public void addNewAction(AUIAction action){
        genericActions.add(action);
    }
    public void removeAction(AUIAction action){
        genericActions.remove(action);
    }
    public void addNewActions(List<AUIAction> actions){
        genericActions.addAll(actions);
    }
    public void removeActions(List<AUIAction> actions){
        genericActions.removeAll(actions);
    }


    public enum ConfiguratorKind{
        BROWSER,
        DIAGRAM,
        MENU

    }
}
