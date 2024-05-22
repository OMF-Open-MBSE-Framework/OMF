/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_public_features.stereotypes.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFWarningException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel2;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.stereotypes.StereotypesFeature;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Refresh stereotypes rules based on config files", category = "OMF")
public class RefreshStereotypesRulesBasedOnConfigFiles extends AUIAction {

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return isProjectOpened();
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            OMFLogger.getInstance().log("Parsing config files", null, OMFLogLevel.INFO);
            ((StereotypesFeature) feature).getRuleUpdater().updateAllRulesBasedOnConfigFiles();
            OMFLogger.getInstance().log("Rules updated based on config files", null, OMFLogLevel.INFO);
        }catch (OMFWarningException e) {
            OMFLogger2.logToUIConsole(new OMFLog2().info(e.getUiMessage()), OMFLogLevel2.WARNING);
        } catch (Exception e) {
            throw new OMFCriticalException2("Error while parsing, please verify the configuration file and try again", e);
        }
    }
}
