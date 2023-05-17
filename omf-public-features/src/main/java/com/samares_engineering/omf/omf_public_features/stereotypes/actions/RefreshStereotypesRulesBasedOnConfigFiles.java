/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_public_features.stereotypes.actions;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogger;
import com.samares_engineering.omf.omf_public_features.stereotypes.StereotypesFeature;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Refresh stereotypes rules based on config files", category = "Stereotypes")
public class RefreshStereotypesRulesBasedOnConfigFiles extends AUIAction {
    protected Project project = null;

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            OMFLogger.getInstance().log("Parsing config files", null, OMFLogLevel.INFO);
            ((StereotypesFeature) feature).getRuleUpdater().updateAllRulesBasedOnConfigFiles();
            OMFLogger.getInstance().log("Rules updated based on config files", null, OMFLogLevel.INFO);
        } catch (Exception e) {
            OMFLogger.getInstance().log("[Error] While parsing, please verify the configuration file and try again", null, OMFLogLevel.ERROR);
            OMFErrorHandler.handleException(e, false);
        }
    }

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return OMFUtils.currentProject != null;
    }
}
