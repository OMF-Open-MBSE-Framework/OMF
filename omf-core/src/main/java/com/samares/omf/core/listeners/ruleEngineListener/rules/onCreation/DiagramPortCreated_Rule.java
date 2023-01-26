/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.listeners.ruleEngineListener.rules.onCreation;

import com.nomagic.magicdraw.copypaste.CopyPasteManager;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares.omf.core.listeners.listeners.OrchestratorListener;
import com.samares.omf.core.listeners.ruleEngineListener.rules.A_Rule;
import com.samares.omf.core.listeners.util.ListenerConfig;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.utils.errorManagement.OMFErrorHandler;
import com.samares.omf.core.utils.errorManagement.OMFLogLevel;
import com.samares.omf.core.utils.ColorPrinter;

import java.beans.PropertyChangeEvent;

public class DiagramPortCreated_Rule extends A_Rule {
    public Stereotype strToApply = null;
    public Stereotype strOwner   = null;

    private DiagramPortCreated_Rule(String id) {
        super(id);
    }
    public DiagramPortCreated_Rule(String id, Stereotype strToApply, Stereotype strOwner) {
        this.id         = id;
        this.strToApply = strToApply;
        this.strOwner   = strOwner;
    }

    @Override
    public boolean eventMatches(PropertyChangeEvent evt) {
        try {
            Element src = (Element) evt.getSource();

            final boolean isPortCreatedOnDiagramBorder = (src instanceof Port) && src.getOwner() == OMFUtils.getOpenedDiagram().getOwner();
            final boolean isPortCreatedFromConnection = ListenerConfig.getInstance().hasPortCreatedFromConnection();
            final boolean isPasting = CopyPasteManager.isPasting();

            return isPortCreatedOnDiagramBorder && !isPortCreatedFromConnection && !isPasting;
        }catch (Exception e) {
            OMFErrorHandler.displayDEVMessage("[" + id + "]", e, OMFLogLevel.WARNING);
            return false;
        }
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent evt) {
        Element src = (Element) evt.getSource();
        StereotypesHelper.addStereotype(src, strToApply);

        debug(evt.getSource());
        return evt;
    }

    @Override
    public void debug(Object o) {
        Port port = (Port) o;
        ColorPrinter.warn("[" + id + "]- Matches for port: " + port.getHumanName());
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
