/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators;

import com.nomagic.actions.ActionsManager;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;

import java.util.ArrayList;
import java.util.List;

public abstract class AUIActionConfigurator implements UIActionConfigurator {

    protected List<AUIAction> genericActions = new ArrayList<>();

    public void removeActionsFromMD(ActionsManager actionsManager) {
        genericActions.forEach(action ->
            UIActionConfiguratorUtils.findCategory(actionsManager, action.getCategory()).ifPresent(category ->
                action.getAllActions().forEach(category::removeAction)
            )
        );
    }

    public void addAction(AUIAction action){
        genericActions.add(action);
    }
    public void removeAction(AUIAction action){
        genericActions.remove(action);
    }
    public void addActions(List<AUIAction> actions){
        genericActions.forEach(this::addAction);
    }
    public void removeActions(List<AUIAction> actions){
        genericActions.forEach(this::removeAction);
    }
}
