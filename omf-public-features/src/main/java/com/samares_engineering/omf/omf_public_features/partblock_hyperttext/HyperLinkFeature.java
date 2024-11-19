/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.partblock_hyperttext;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.ALiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_public_features.partblock_hyperttext.creation.HyperlinkPartToBlockLA;

import java.beans.PropertyChangeEvent;
import java.util.List;

public class HyperLinkFeature extends SimpleFeature {

    public HyperLinkFeature(){
       super("HYPERLINK FEATURE");
    }

    @Override
    public List<LiveActionEngine> initLiveActions() {
        var creationRE = new ALiveActionEngine(LiveActionType.AFTER_AUTOMATION);
        creationRE.addLiveAction(new HyperlinkPartToBlockLA());
        return List.of(creationRE);
    }

    @Override
    public List<Option> initOptions() {
        OptionImpl testEnvOption = createEnvOption(
                new BooleanProperty("Activate autoLink from part to Block (add hyperlink to access Block Specification with double click on part:", true),
                "Feature Activation");

        testEnvOption.addListenerToRegister(new AOptionListener() {
            @Override
            public void updateByEnvironmentProperties(List<Property> list) {
                super.updateByEnvironmentProperties(list);
            }

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                super.propertyChange(evt);
            }
        });

        return List.of(testEnvOption);
    }


}
