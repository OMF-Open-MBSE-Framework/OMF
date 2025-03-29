/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_public_features.stereotypes.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFLogException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;
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
            OMFLogger.infoToUIConsole("Parsing config files");
            ((StereotypesFeature) feature).getRuleUpdater().updateAllRulesBasedOnConfigFiles();
            OMFLogger.infoToUIConsole("Rules updated based on config files");
        }catch (OMFLogException e) {
            OMFLogger.warnToUIConsole(e.getLog());
        } catch (Exception e) {
            throw new OMFCriticalException("Error while parsing, please verify the configuration file and try again", e);
        }
    }
}
