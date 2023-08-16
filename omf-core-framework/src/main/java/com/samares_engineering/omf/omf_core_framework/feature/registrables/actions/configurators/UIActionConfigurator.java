package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators;

import com.nomagic.actions.ActionsManager;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;

import java.util.List;

public interface UIActionConfigurator {
    void removeActionsFromMD(ActionsManager actionsManager);
    public void addAction(AUIAction action);
    public void removeAction(AUIAction action);
    public void addActions(List<AUIAction> actions);
    public void removeActions(List<AUIAction> actions);
}
