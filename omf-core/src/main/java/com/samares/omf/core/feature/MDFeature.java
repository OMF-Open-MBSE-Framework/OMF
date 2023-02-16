/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature;

import com.samares.omf.core.feature.registrables.actions.actions.IUIAction;
import com.samares.omf.core.feature.registrables.options.option.IOption;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.IFeatureRuleEngine;
import com.samares.omf.core.plugin.APlugin;

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

    List<IUIAction> getUIActions();
    List<IFeatureRuleEngine> getRuleEngines();
    List<IOption> getOptions();

    List<IUIAction> getDelayedUIActions();
    List<IFeatureRuleEngine> getDelayedRuleEngines();
    List<IOption> getDelayedOptions();

    void initFeature(APlugin plugin);
    void initDelayedFeatureItems();
}
