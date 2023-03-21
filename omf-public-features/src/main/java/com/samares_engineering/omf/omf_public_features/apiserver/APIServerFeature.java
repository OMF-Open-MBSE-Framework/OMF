/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.apiserver;

import com.samares_engineering.omf.omf_core_framework.feature.AFeature;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.IUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RuleEngine;
import com.samares_engineering.omf.omf_public_features.apiserver.actions.AddHyperlinkToType;
import com.samares_engineering.omf.omf_public_features.apiserver.actions.StartHyperTextServer;
import com.samares_engineering.omf.omf_public_features.apiserver.actions.TestAction;
import com.samares_engineering.omf.omf_public_features.apiserver.creation.HyperlinkPartToBlockLA;
import com.samares_engineering.omf.omf_public_features.apiserver.server.ExtHyperTextServerRouting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class APIServerFeature extends AFeature {

    public static final String HELLO_PORT_ID = "HelloPort";

    public APIServerFeature(){
       super("APIServer Feature");
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return null;
    }

    @Override
    public List<IUIAction> initFeatureActions() {
        return Arrays.asList(
                new StartHyperTextServer(),
                new AddHyperlinkToType(),
                new TestAction()
        );
    }

    @Override
    protected List<IUIAction> initProjectOnlyFeatureActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IRuleEngine> initLiveActions() {
        IRuleEngine creationRE = new RuleEngine(RECategoryEnum.CREATE);
        creationRE.addRule(new HyperlinkPartToBlockLA());
        return List.of(creationRE);
    }

    @Override
    protected List<IRuleEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IOption> initOptions() {
        return Arrays.asList(
        );
    }

    @Override
    protected List<IOption> initProjectOnlyOptions() {
        return Collections.emptyList();
    }


    private void registerRouting() {
        OMFApiServer.getInstance().addRoute("openProject", ExtHyperTextServerRouting.openProject());
        OMFApiServer.getInstance().addRoute("openTWCProject", ExtHyperTextServerRouting.openTWCProject());
        OMFApiServer.getInstance().addRoute("refmodel", ExtHyperTextServerRouting.refModel());
    }

    @Override
    public void onRegistering() {
        OMFApiServer.getInstance().startServer(9850);
        registerRouting();
    }
    @Override
    public void onUnregistering() {
        OMFApiServer.getInstance().stopServer();
    }
}
