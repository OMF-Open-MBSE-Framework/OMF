/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.partblock_hyperttext.creation;

import com.nomagic.magicdraw.hyperlinks.Hyperlink;
import com.nomagic.magicdraw.hyperlinks.HyperlinkUtils;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction;
import com.samares_engineering.omf.omf_core_framework.listeners.EventChecker;

import java.beans.PropertyChangeEvent;

public class HyperlinkPartToBlockLA extends ALiveAction {
    @Override
    protected boolean eventMatches(PropertyChangeEvent evt) {
       return new EventChecker()
                .isElementCreated()
                .isPart()
                .test(evt);
    }


    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        Property part = (Property) e.getSource();
        Hyperlink hp = HyperlinkUtils.createHyperlink("ToBlock", part.getType());
        HyperlinkUtils.addHyperlink(part, hp);
        HyperlinkUtils.makeActive(part, hp);


        return e;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
