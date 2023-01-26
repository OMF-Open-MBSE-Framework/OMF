package com.samares.omf.core.feature;

import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.feature.options.IOption;
import com.samares.omf.core.feature.ruleengine.IFeatureRuleEngine;

import java.util.ArrayList;
import java.util.List;

public abstract class AFeature implements MDFeature{

    protected String name;
    protected boolean isActivated;
    private List<AGenericAction> l_mdActions;
    private List<IFeatureRuleEngine> l_liveActions;

    private List<IOption> l_options;

    public AFeature(String name){
        this.name = name;
        this.l_options      = initOptions();
        this.l_mdActions    = initFeatureActions();
        this.l_liveActions  = initLiveActions();
    }

    public AFeature(String name, List<AGenericAction> l_mdActions,List<IFeatureRuleEngine> l_liveActions, List<IOption> l_options){
        this.name = name;
        this.l_options = new ArrayList<>( l_options);
        this.l_mdActions = new ArrayList<>(l_mdActions);
        this.l_liveActions = new ArrayList<>( l_liveActions);
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
        return l_mdActions;
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
        return l_liveActions;
    }

    @Override
    public List<IOption> getOptions() {
        return l_options;
    }


}
