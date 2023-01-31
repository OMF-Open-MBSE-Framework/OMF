/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.listeners.ruleEngineListener.rules.onCreation;

import com.nomagic.magicdraw.copypaste.CopyPasteManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares.omf.core.listeners.ruleEngineListener.rules.A_Rule;
import com.samares.omf.core.listeners.util.ListenerConfig;
import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.core.errors.OMFLogLevel;
import com.samares.omf.core.utils.ColorPrinter;

import java.beans.PropertyChangeEvent;

public class PortCreatedFromConnection_Rule extends A_Rule {
    public Stereotype strInstance = null;

    private PortCreatedFromConnection_Rule(String id) {
        super(id);
    }
    public PortCreatedFromConnection_Rule(String id, Stereotype strInstance) {
        this.id = id;
        this.strInstance = strInstance;
    }

    @Override
    public boolean eventMatches(PropertyChangeEvent evt) {
        try{
            Element src = (Element) evt.getSource();
            final boolean isPort = src instanceof Port;
            final boolean isPortCreatedFromConnection = isPort && ((Port) src).getEnd().size() > 0;
            final boolean isPasting = CopyPasteManager.isPasting();
            return isPortCreatedFromConnection && !isPasting;
        }catch (Exception e) {
            OMFErrorHandler.displayDEVMessage("[" + id + "]", e, OMFLogLevel.WARNING);
            return false;
        }
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent evt) {
        ListenerConfig.getInstance().setHasPortCreatedFromConnection(true);

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
