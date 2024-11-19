/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;

import java.util.ArrayList;
import java.util.List;

public abstract class AUIActionConfigurator implements UIActionConfigurator {
    protected List<UIAction> registeredActions = new ArrayList<>();

    public void addRegisteredAction(UIAction action){
        registeredActions.add(action);
    }
    public void removeRegisteredAction(UIAction action){
        registeredActions.remove(action);
    }
    public void addRegisteredActions(List<UIAction> actions){
        registeredActions.forEach(this::addRegisteredAction);
    }
    public void removeRegisteredActions(List<UIAction> actions){
        registeredActions.forEach(this::removeRegisteredAction);
    }
}
