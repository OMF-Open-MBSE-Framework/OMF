/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.apiserver.actions;

import com.google.common.base.Strings;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.options.ProjectOptions;
import com.nomagic.magicdraw.properties.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.apiserver.OMFApiServer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Start Server", category = "SERVER")
public class StartHyperTextServer extends AUIAction {


    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null) return false;
        return true;
    }



    @Override
    public void actionToPerform(List<Element> selectedElements) {
//        HyperlinkUtils
        OMFApiServer.getInstance().startServer(9850);
        ProjectOptions options = OMFUtils.currentProject.getOptions();
        Optional<Property> optOption;
        optOption = Arrays.stream(ProjectOptions.class.getFields())
                .map(field -> {
                    try {
                        return field.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(cat -> !Strings.isNullOrEmpty(cat))
                .map(cat -> getProjectOptionsByCategory(cat, "BooleanField", OMFUtils.currentProject))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

    }
    private Optional<Property> getProjectOptionsByCategory(String category, String optionName, Project project){
        try {
            return Optional.ofNullable(project.getOptions().getProperty(category, optionName));
        }catch (Exception e){
            return Optional.empty();
        }
    }



}