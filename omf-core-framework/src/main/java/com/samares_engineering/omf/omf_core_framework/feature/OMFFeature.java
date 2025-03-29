/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;

import java.util.List;

/**
 * This interface represents a feature that can be registered in the OMF.
 * A feature is a set of functionalities that can be activated or deactivated.
 * A feature can be registered in the OMF and can be project specific.
 * A feature contains FeatureItem such as UI actions, live action engines, options and life cycle hooks.
 * see {@link RegistrableFeatureItem}
 * see {@link UIAction}
 * see {@link LiveActionEngine}
 * see {@link Hook}
 * see {@link Option}
 */
public interface OMFFeature {
    String getName();

    void setIsRegistered(boolean isRegistered);
    boolean isRegistered();

    OMFPlugin getPlugin();


    List<UIAction> getUIActions();
    List<LiveActionEngine> getLiveActionEngines();
    List<Option> getOptions();
    List<Hook> getLifeCycleHooks();

    List<UIAction> getProjectOnlyUIActions();
    List<LiveActionEngine> getProjectOnlyLiveActionEngines();
    List<Option> getProjectOnlyOptions();

    void initFeature(OMFPlugin plugin);
    void initFeatureItems();
    void initProjectOnlyFeatureItems();

    EnvOptionsHelper getEnvOptionsHelper();

    void register();
    void unregister();
}
