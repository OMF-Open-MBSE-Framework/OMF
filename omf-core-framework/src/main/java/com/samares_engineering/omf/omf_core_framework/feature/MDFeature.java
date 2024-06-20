/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.IHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.ALiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;

import java.util.List;

/**
 * This interface represents a feature that can be registered in the OMF.
 * A feature is a set of functionalities that can be activated or deactivated.
 * A feature can be registered in the OMF and can be project specific.
 * A feature contains FeatureItem such as UI actions, rule engines, options and life cycle hooks.
 * see {@link RegistrableFeatureItem}
 * see {@link UIAction}
 * see {@link ALiveActionEngine}
 * see {@link IHook}
 * see {@link IOption}
 */
public interface MDFeature{
    String getName();

    void setIsRegistered(boolean isRegistered);
    boolean isRegistered();

    APlugin getPlugin();


    List<UIAction> getUIActions();
    List<ALiveActionEngine> getRuleEngines();
    List<IOption> getOptions();
    List<IHook> getLifeCycleHooks();

    List<UIAction> getProjectOnlyUIActions();
    List<ALiveActionEngine> getProjectOnlyRuleEngines();
    List<IOption> getProjectOnlyOptions();

    void initFeature(APlugin plugin);
    void initFeatureItems();
    void initProjectOnlyFeatureItems();

    EnvOptionsHelper getEnvOptionsHelper();

    void register();
    void unregister();
}
