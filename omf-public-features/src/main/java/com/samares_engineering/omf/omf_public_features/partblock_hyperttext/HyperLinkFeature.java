/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.partblock_hyperttext;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.feature.AFeature;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RuleEngine;
import com.samares_engineering.omf.omf_public_features.partblock_hyperttext.creation.HyperlinkPartToBlockLA;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HyperLinkFeature extends AFeature {

    public HyperLinkFeature(){
       super("HYPERLINK FEATURE");
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return null;
    }

    @Override
    public List<UIAction> initFeatureActions() {
        return Arrays.asList(
        );
    }

    @Override
    protected List<UIAction> initProjectOnlyFeatureActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IRuleEngine> initLiveActions() {
        IRuleEngine creationRE = new RuleEngine(RECategoryEnum.AFTER_AUTOMATION);
        creationRE.addRule(new HyperlinkPartToBlockLA());
        return List.of(creationRE);
    }

    @Override
    protected List<IRuleEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IOption> initOptions() {
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

        return Arrays.asList(
                testEnvOption
        );
    }

    @Override
    protected List<IOption> initProjectOnlyOptions() {
        return Collections.emptyList();
    }


}
