/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.apiserver;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.StringProperty;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFFeatureException;
import com.samares_engineering.omf.omf_core_framework.feature.AFeature;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_public_features.apiserver.actions.RestartAPIServerAction;
import com.samares_engineering.omf.omf_public_features.apiserver.actions.StopAPIServerAction;
import com.samares_engineering.omf.omf_public_features.apiserver.server.ExtHyperTextServerRouting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class APIServerFeature extends AFeature {
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
    protected List<UIAction> initProjectOnlyFeatureActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IRuleEngine> initLiveActions() {
        return Collections.emptyList();
    }

    @Override
    protected List<IRuleEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IOption> initOptions() {
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
        try {
            OMFApiServer.getInstance(getPlugin()).startServer(serverPort);
        } catch (Exception e) {
            OMFErrorHandler.handleException(new OMFFeatureException("Error while starting API server, this will strongly impact features using API Server." +
                    "\nPlease try to restart the API Server using OMF Advanced Menu", this, e, GenericException.ECriticality.CRITICAL));
            return;
        }

        registerRouting();


    }

    @Override
    public void onUnregistering() {
        try {
            OMFApiServer.getInstance(getPlugin()).stopServer();
        } catch (Exception e) {
            OMFErrorHandler.handleException(new OMFFeatureException("Error while stopping API server, this will strongly impact features using API Server." +
                    "\nPlease try to restart the API Server using OMF Advanced Menu", this, e, GenericException.ECriticality.CRITICAL));
        }
    }
}
