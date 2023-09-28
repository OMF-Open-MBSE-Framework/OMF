/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;

import java.util.List;

public interface MDFeature {
    String getName();

    void setIsRegistered(boolean isRegistered);
    boolean isRegistered();
    void onRegistering();
    void onUnregistering();
    void onProjectOpen();
    void onProjectClose();

    APlugin getPlugin();

    List<UIAction> getUIActions();
    List<IRuleEngine> getRuleEngines();
    List<IOption> getOptions();

    List<UIAction> getProjectOnlyUIActions();
    List<IRuleEngine> getProjectOnlyRuleEngines();
    List<IOption> getProjectOnlyOptions();

    void initFeature(APlugin plugin);
    void initProjectOnlyFeatureItems();

    EnvOptionsHelper getEnvOptionsHelper();

    void register();

    void unregister();
}
