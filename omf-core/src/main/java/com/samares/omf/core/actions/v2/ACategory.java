/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.actions.v2;

import com.nomagic.magicdraw.actions.MDActionsCategory;

import java.util.ArrayList;
import java.util.List;

public class ACategory extends MDActionsCategory {
    private List<AGenericAction> actions;

    public ACategory(){
        this("", new ArrayList<>());
    }
    public ACategory(String name, List<AGenericAction> allActions){
        super(name, name);
        this.actions = allActions;
    }

    public ACategory registerBrowserActions(){
        this.setNested(true);
        actions.stream()
                .filter(AGenericAction::checkBrowserAvailability)
                .map(AGenericAction::getBrowserAction)
                .forEach(this::addAction);
        return this;
    }
    public ACategory registerDiagramActions(){
        actions.stream()
                .map(AGenericAction::getDiagramAction)
                .forEach(this::addAction);
        return this;
    }
    public ACategory registerMenuActions(){
        actions.stream()
                .map(AGenericAction::getMenuAction)
                .forEach(this::addAction);
        return this;
    }
}
