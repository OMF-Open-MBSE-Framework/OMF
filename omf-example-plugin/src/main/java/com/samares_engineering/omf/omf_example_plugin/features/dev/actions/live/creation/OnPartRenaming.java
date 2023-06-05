/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.dev.actions.live.creation;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule.ARule;
import com.samares_engineering.omf.omf_public_features.stereotypes.utils.EventChecker;

import java.beans.PropertyChangeEvent;

public class OnPartRenaming extends ARule {
    @Override
    protected boolean eventMatches(PropertyChangeEvent evt) {
       boolean partRenaming = new EventChecker()
                .isElementRenamed()
                .isPart()
                .test(evt);
       if (!partRenaming) return false;
       Property part = (Property) evt.getSource();
       if(part.getType() == null) return false;
       return true;
    }

    @Override
    public void debug(Object o) {

    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        try {
            Property part = (Property) e.getSource();
            if(part.getType() == null) return e;
            part.getType().setName("RENAMED BLOCK");
        }catch (Exception uncheckedException){
            OMFErrorHandler.handleException(uncheckedException);
        }

        return e;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
