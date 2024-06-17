package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators;

import com.nomagic.magicdraw.actions.ConfiguratorWithPriority;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;

import java.util.List;

public interface UIActionConfigurator extends ConfiguratorWithPriority {
    public void addRegisteredAction(UIAction action);
    public void removeRegisteredAction(UIAction action);
    public void addRegisteredActions(List<UIAction> actions);
    public void removeRegisteredActions(List<UIAction> actions);
}
