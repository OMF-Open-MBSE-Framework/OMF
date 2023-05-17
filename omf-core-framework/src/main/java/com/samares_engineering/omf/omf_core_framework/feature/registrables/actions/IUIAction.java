package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions;

import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.RegistrableFeatureItem;

import java.util.List;

public interface IUIAction extends RegistrableFeatureItem {
    boolean checkBrowserAvailability();

    boolean checkDiagramAvailability();

    boolean isMenuAction();

    boolean checkMenuAvailability();

    List<MDAction> getAllActions();

    MDAction getMenuAction();

    boolean isBrowserAction();

    NMAction getBrowserAction();

    boolean isDiagramAction();

    NMAction getDiagramAction();

    String getCategory();

    void initRegistrableItem(MDFeature mdFeature);
}
