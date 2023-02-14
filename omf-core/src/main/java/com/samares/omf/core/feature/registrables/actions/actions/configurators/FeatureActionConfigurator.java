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
import com.samares.omf.core.feature.registrables.actions.actions.IUIAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class FeatureActionConfigurator {

    List<IUIAction> genericActions = new ArrayList<>();


    protected void configureFeatureActions(ActionsManager actionsManager) {
        Predicate<? super IUIAction> check = null;

        resetMDActions(actionsManager);
        if(this instanceof BrowserContextAMConfigurator) {
            check = IUIAction::checkBrowserAvailability;
            genericActions.stream()
                    .filter(check)
                    .forEach(action -> this.registerBrowserAction(actionsManager, findOrCreateCategory(actionsManager, action), action));
//            triggerListener(ConfiguratorKind.BROWSER);
        }


        if(this instanceof DiagramContextAMConfigurator) {
            check = IUIAction::checkDiagramAvailability;
            genericActions.stream()
                    .filter(check)
                    .forEach(action -> this.registerDiagramAction(actionsManager, findOrCreateCategory(actionsManager, action), action));
        }


        // registering MenuActions
        if(this instanceof OMFMainMenuConfigurator) {
            genericActions.stream()
                    .filter(IUIAction::isMenuAction)
                    .forEach(action -> this.registerMenuAction(actionsManager, findOrCreateCategory(actionsManager, action), action, action.checkMenuAvailability()));
//            triggerListener(ConfiguratorKind.MENU);
        }
    }


    public void resetMDActions(ActionsManager actionsManager) {
        List<ActionsCategory> registeredCategories = actionsManager.getCategories();

        genericActions.stream()
                        .forEach(action -> findCategory(actionsManager, action).ifPresent(category ->
                            action.getAllActions().forEach(category::removeAction)));

    }


    private void registerMenuAction(ActionsManager actionsManager, MDActionsCategory category, IUIAction menuAction,
                                    boolean test) {
        if(!actionsManager.getCategories().contains(category) ) {
            actionsManager.addCategory(category);
            category.setNested(true);
        }

        MDAction action = menuAction.getMenuAction();
        if(!category.getActions().contains(menuAction.getMenuAction()))
            category.addAction(menuAction.getMenuAction());

        action.setEnabled(test);
    }

    /**
     * register an action into the category, If the category doesn't exist it will register it.
     * @param actionsManager
     * @param category
     * @param action
     */
    protected void registerBrowserAction(ActionsManager actionsManager, MDActionsCategory category, IUIAction action) {
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
    protected void registerDiagramAction(ActionsManager actionsManager, MDActionsCategory category, IUIAction action) {
        if(!actionsManager.getCategories().contains(category) ) {
            actionsManager.addCategory(category);
            category.setNested(true);
        }

        if(action.isDiagramAction())
            category.addAction(action.getDiagramAction());
    }

    protected MDActionsCategory findOrCreateCategory(ActionsManager actionsManager, IUIAction IUIAction) {
        String categoryName = IUIAction.getCategory();

        Optional<MDActionsCategory> optCategory = findCategory(actionsManager, IUIAction);
        if(optCategory.isPresent())
            return optCategory.get();

        return new MDActionsCategory("FeatureCategoryID-"+ categoryName, categoryName){
            @Override
            public void updateState() {
//                super.updateState();
                //refresh MenuActionState
                getActions().stream()
                        .filter(IUIAction.class::isInstance)
                        .map(IUIAction.class::cast)
                        .forEach( action -> action.getMenuAction().setEnabled(action.checkMenuAvailability()));
                this.setEnabled(getActions().stream().anyMatch(NMAction::isEnabled));
            }
        };
    }

    private Optional<MDActionsCategory> findCategory(ActionsManager actionsManager, IUIAction IUIAction) {
        String categoryName = IUIAction.getCategory();
        return actionsManager.getCategories().stream()
                .filter(MDActionsCategory.class::isInstance)
                .map(MDActionsCategory.class::cast)
                .filter(cat -> cat.getName().equals(categoryName)).findAny();
    }

    public void addNewAction(IUIAction action){
        genericActions.add(action);
    }
    public void removeNewAction(IUIAction action){
        genericActions.remove(action);
    }
    public void addNewActions(List<IUIAction> actions){
        genericActions.addAll(actions);
    }
    public void removeActions(List<IUIAction> actions){
        genericActions.removeAll(actions);
    }

    public enum ConfiguratorKind{
        BROWSER,
        DIAGRAM,
        MENU
    }
}
