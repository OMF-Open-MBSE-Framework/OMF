/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.apiserver.actions;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.expressions.ExpressionHelper;
import com.nomagic.magicdraw.expressions.ParameterizedExpression;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdbasicbehaviors.OpaqueBehavior;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "TEST", category = "DEV")
public class TestAction extends AUIAction {


    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null) return false;
        if(selectedElements.size() != 1) return false;
        return true;
    }



    @Override
    public void actionToPerform(List<Element> selectedElements) {
//        executeOpaqueBehavior(selectedElements);
        unused(selectedElements);

    }

    private void unused(List<Element> selectedElements) {

    }

    private void executeOpaqueBehavior(List<Element> selectedElements) {
        OpaqueBehavior finder = (OpaqueBehavior) selectedElements.get(0);
        ParameterizedExpression opaqueBehaviorExpression = ExpressionHelper.getBehaviorExpression(finder);

        Set<Element> findResult = new HashSet<>();
        try {
            findResult = (Set<Element>) ExpressionHelper.call(opaqueBehaviorExpression);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Project project = Application.getInstance().getProject();
        Profile profile = StereotypesHelper.getProfile(project, "SysML");
        Stereotype flowPropertySTR = StereotypesHelper.getStereotype(project, "FlowProperty", profile);

        findResult.stream()
                .filter(flow -> flow instanceof Property)
                .map(Property.class::cast)
                .forEach(flow -> StereotypesHelper.setStereotypePropertyValue(flow, flowPropertySTR, "direction", "in"));
    }


}