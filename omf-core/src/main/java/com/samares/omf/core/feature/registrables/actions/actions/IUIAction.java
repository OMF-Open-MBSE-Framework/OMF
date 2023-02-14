package com.samares.omf.core.feature.registrables.actions.actions;

import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.MDAction;
import com.samares.omf.core.feature.RegistrableFeatureItem;

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
}
