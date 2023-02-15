/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature;

import com.samares.omf.core.feature.registrables.actions.actions.IUIAction;
import com.samares.omf.core.feature.registrables.options.option.IOption;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.FeatureRuleEngine;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.IFeatureRuleEngine;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares.omf.core.plugin.APlugin;

import java.util.List;

public abstract class AFeature implements MDFeature{

    protected APlugin plugin;

    protected String name;
    protected boolean isRegistered;
    private final List<IUIAction> mdActions;
    private final List<IFeatureRuleEngine> liveActions;
    private final List<IOption> options;

    protected AFeature(APlugin plugin, String name){
        this.plugin = plugin;
        this.name = name;
        this.options = initOptions();
        this.mdActions = initFeatureActions();
        this.liveActions = initLiveActions();
    }


    /**
     * Define all the feature action there, it will be automatically registered with the feature.
     * @return list of MDAction to register
     */
    public abstract List<IUIAction> initFeatureActions();

    /**
     * Define all the feature live actions (RuleEngines) there, it will be automatically registered with the feature.
     * @return list of IFeatureRuleEngine to register
     */
    public abstract List<IFeatureRuleEngine> initLiveActions();

    /**
     * Define all the feature options (Environment && Project) there, it will be automatically registered with the feature.
     * @return list of IOption to register
     */
    public abstract List<IOption> initOptions();
    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<IUIAction> getUIActions() {
        return mdActions;
    }

    protected IFeatureRuleEngine newRuleEngine(RECategoryEnum category) {
        return new FeatureRuleEngine(this, category);
    }

    protected IFeatureRuleEngine newRuleEngine(RECategoryEnum category, int priority) {
        return new FeatureRuleEngine(this, category, priority);
    }

    public final void setRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public final boolean isRegistered() {
        return isRegistered;
    }

    /**
     * Override this to inject code to be run on feature activation
     */
    public void onRegistering() {}

    /**
     * Override this to inject code to be run on feature deactivation
     */
    public void onUnregistering() {}

    /**
     * Override this to inject code to be run on project opening
     */
    public void onProjectOpen() {}

    /**
     * Override this to inject code to be run on project closing
     */
    public void onProjectClose() {}

    @Override
    public List<IFeatureRuleEngine> getRuleEngines() {
        return liveActions;
    }

    @Override
    public List<IOption> getOptions() {
        return options;
    }

    public APlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(APlugin plugin) {
        this.plugin = plugin;
    }
}
