package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators;

import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ConfiguratorWithPriority;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;

import java.util.List;

public interface UIActionConfigurator extends ConfiguratorWithPriority {
    void unregisterActionsFromMD(ActionsManager actionsManager);
    public void addAction(UIAction action);
    public void removeAction(UIAction action);
    public void addActions(List<UIAction> actions);
    public void removeActions(List<UIAction> actions);
}
