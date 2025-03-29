/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.apiserver;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.StringProperty;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_public_features.apiserver.actions.RestartAPIServerAction;
import com.samares_engineering.omf.omf_public_features.apiserver.actions.StopAPIServerAction;
import com.samares_engineering.omf.omf_public_features.apiserver.server.ExtHyperTextServerRouting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class APIServerFeature extends SimpleFeature {
    private final String serverURL;
    private final int serverPort;


    public APIServerFeature(String serverURL, int serverPort) {
        super("APIServer Feature");
        this.serverURL = serverURL;
        this.serverPort = serverPort;
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return null;
    }

    @Override
    public List<UIAction> initFeatureActions() {
        return Arrays.asList(
                new RestartAPIServerAction(),
                new StopAPIServerAction()
        );
    }

    @Override
    public List<LiveActionEngine> initLiveActions() {
        return Collections.emptyList();
    }

    @Override
    protected List<LiveActionEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
    }

    @Override
    public List<Option> initOptions() {
        OptionImpl serverURLOption = createEnvOption(
                new StringProperty(APIEnvOptionsHelper.API_SERVER_URL, serverURL),
                APIEnvOptionsHelper.API_SERVER_CONFIGURATION_GROUP
        );

        OptionImpl serverPortOption = createEnvOption(
                new StringProperty(APIEnvOptionsHelper.API_SERVER_PORT, "" + serverPort),
                APIEnvOptionsHelper.API_SERVER_CONFIGURATION_GROUP);

        OptionImpl serverActivationOption = createEnvOption(
                new BooleanProperty(APIEnvOptionsHelper.API_SERVER_ACTIVATED, true),
                APIEnvOptionsHelper.API_SERVER_CONFIGURATION_GROUP);

        return Arrays.asList(
                serverURLOption,
                serverPortOption,
                serverActivationOption
        );
    }

    @Override
    protected List<Option> initProjectOnlyOptions() {
        return Collections.emptyList();
    }

    private void registerRouting() {
        OMFApiServer.getInstance().addRoute("openProject", ExtHyperTextServerRouting.openProject());
        OMFApiServer.getInstance().addRoute("openTWCProject", ExtHyperTextServerRouting.openTWCProject());
        OMFApiServer.getInstance().addRoute("refmodel", ExtHyperTextServerRouting.refModel());
        OMFApiServer.getInstance().addRoute("openSpecification", ExtHyperTextServerRouting.openSpecification());
    }

    @Override
    public void onRegistering() {
        OMFApiServer.getInstance(getPlugin()).startServer(serverPort);
        registerRouting();
    }

    @Override
    public void onUnregistering() {
        OMFApiServer.getInstance(getPlugin()).stopServer();
    }
}
