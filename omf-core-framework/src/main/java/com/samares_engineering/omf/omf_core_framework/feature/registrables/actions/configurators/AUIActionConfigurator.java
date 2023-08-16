/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators;

import com.nomagic.actions.ActionsManager;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;

import java.util.ArrayList;
import java.util.List;

public abstract class AUIActionConfigurator implements UIActionConfigurator {

    protected List<UIAction> genericActions = new ArrayList<>();

    public void unregisterActionsFromMD(ActionsManager actionsManager) {
        genericActions.forEach(action ->
            UIActionConfiguratorUtils.findCategory(actionsManager, action.getCategory()).ifPresent(category ->
                action.getAllActions().forEach(category::removeAction)
            )
        );
    }

    public void addAction(UIAction action){
        genericActions.add(action);
    }
    public void removeAction(UIAction action){
        genericActions.remove(action);
    }
    public void addActions(List<UIAction> actions){
        genericActions.forEach(this::addAction);
    }
    public void removeActions(List<UIAction> actions){
        genericActions.forEach(this::removeAction);
    }
}
