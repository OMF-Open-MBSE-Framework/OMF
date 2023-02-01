/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature;

import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.feature.options.IOption;
import com.samares.omf.core.feature.ruleengine.IFeatureRuleEngine;

import java.util.ArrayList;
import java.util.List;

public abstract class AFeature implements MDFeature{

    protected String name;
    protected boolean isActivated;
    private List<AGenericAction> mdActions;
    private List<IFeatureRuleEngine> liveActions;

    private List<IOption> options;

    public AFeature(String name){
        this.name = name;
        this.options      = initOptions();
        this.mdActions = initFeatureActions();
        this.liveActions = initLiveActions();
    }

    public AFeature(String name, List<AGenericAction> mdActions, List<IFeatureRuleEngine> liveActions, List<IOption> options){
        this.name = name;
        this.options = new ArrayList<>( options);
        this.mdActions = new ArrayList<>(mdActions);
        this.liveActions = new ArrayList<>(liveActions);
    }

    /**
     * Define all the feature action there, it will be automatically registered with the feature.
     * @return list of MDAction to register
     */
    public abstract List<AGenericAction> initFeatureActions();

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
    public List<AGenericAction> getMDActions() {
        return mdActions;
    }

    @Override
    public void activate() {
        isActivated = true;
    }
    @Override
    public void deactivate() {
        isActivated = false;
    }

    @Override
    public boolean isActivated(boolean b) {
        return isActivated;
    }

    @Override
    public List<IFeatureRuleEngine> getLiveActions() {
        return liveActions;
    }

    @Override
    public List<IOption> getOptions() {
        return options;
    }


}
