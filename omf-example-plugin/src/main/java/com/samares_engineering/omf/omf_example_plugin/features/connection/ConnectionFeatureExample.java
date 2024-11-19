/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.connection;

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.ALiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;
import com.samares_engineering.omf.omf_example_plugin.features.connection.actions.DelegateConnection;
import com.samares_engineering.omf.omf_example_plugin.features.connection.live.DelegateOnConnectionCreation;
import com.samares_engineering.omf.omf_example_plugin.features.connection.options.ConnectionOptionHelper;

import java.util.List;

/**
 * Example of a feature that can be used to delegate connections.
 */
public class ConnectionFeatureExample extends SimpleFeature {

    public ConnectionFeatureExample(){
       super("CONNECTION_FEATURE_EXAMPLE");
    }

    @Override
    protected ConnectionOptionHelper initEnvOptionsHelper() {
        return new ConnectionOptionHelper(this);
    }

    @Override
    protected List<Option> initOptions() {
        return getEnvOptionsHelper().getAllOptions();
    }

    @Override
    public List<UIAction> initFeatureActions() {
        return List.of(
                new DelegateConnection()
        );
    }


    @Override
    public List<LiveActionEngine> initLiveActions() {
        LiveActionEngine creationRE = new ALiveActionEngine(LiveActionType.CREATE);
        creationRE.addLiveAction(new DelegateOnConnectionCreation());
        return List.of(creationRE);
    }

    @Override
    public ConnectionOptionHelper getEnvOptionsHelper() {
        return (ConnectionOptionHelper) super.getEnvOptionsHelper();
    }
}
