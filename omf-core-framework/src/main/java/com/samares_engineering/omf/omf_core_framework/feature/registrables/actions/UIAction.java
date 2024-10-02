package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions;

import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.RegistrableFeatureItem;

import java.util.List;

public interface UIAction extends RegistrableFeatureItem {
    boolean checkBrowserAvailability();

    boolean checkDiagramAvailability();

    boolean isMenuAction();

    boolean checkMenuAvailability();

    List<NMAction> getAllActions();

    NMAction getMenuAction();

    boolean isBrowserAction();

    NMAction getBrowserAction();

    boolean isDiagramAction();

    NMAction getDiagramAction();

    String getCategory();

    void initRegistrableItem(OMFFeature OMFFeature);

}
