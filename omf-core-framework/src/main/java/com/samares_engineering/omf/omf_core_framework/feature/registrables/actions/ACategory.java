/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions;

import com.nomagic.magicdraw.actions.MDActionsCategory;

import java.util.ArrayList;
import java.util.List;

public class ACategory extends MDActionsCategory {
    private List<AUIAction> actions;

    public ACategory(){
        this("", new ArrayList<>());
    }
    public ACategory(String name, List<AUIAction> allActions){
        super(name, name);
        this.actions = allActions;
    }

    public ACategory registerBrowserActions(){
        this.setNested(true);
        actions.stream()
                .filter(AUIAction::checkBrowserAvailability)
                .map(AUIAction::getBrowserAction)
                .forEach(this::addAction);
        return this;
    }
    public ACategory registerDiagramActions(){
        actions.stream()
                .map(AUIAction::getDiagramAction)
                .forEach(this::addAction);
        return this;
    }
    public ACategory registerMenuActions(){
        actions.stream()
                .map(AUIAction::getMenuAction)
                .forEach(this::addAction);
        return this;
    }
}
