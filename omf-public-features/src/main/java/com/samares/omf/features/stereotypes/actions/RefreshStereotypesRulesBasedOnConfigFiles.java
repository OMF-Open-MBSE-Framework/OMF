/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.features.stereotypes.actions;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.feature.registrables.actions.actions.AUIAction;
import com.samares.omf.core.feature.registrables.actions.actions.annotations.*;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.core.errors.OMFLogLevel;
import com.samares.omf.core.errors.OMFLogger;
import com.samares.omf.features.stereotypes.StereotypesFeature;
import com.samares.omf.features.stereotypes.StereotypesRuleUpdater;

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
