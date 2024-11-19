/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.partblock_hyperttext.action;

import com.nomagic.magicdraw.hyperlinks.Hyperlink;
import com.nomagic.magicdraw.hyperlinks.HyperlinkUtils;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Add HyperLink to Block", category = "OMF.Dev")
public class AddHyperlinkToType extends AUIAction {


    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(isProjectVoid()) return false;
        if(selectedElements.size() != 1) return false;
        if(!selectedElements.stream().allMatch(Profile._getSysmlAdditionalStereotypes().partProperty()::is)) return false;
        return true;
    }



    @Override
    public void actionToPerform(List<Element> selectedElements) {
        Property part = (Property) selectedElements.get(0);
        Hyperlink hp = HyperlinkUtils.createHyperlink("ToBlock", part.getType());
        HyperlinkUtils.addHyperlink(part, hp);

    }



}