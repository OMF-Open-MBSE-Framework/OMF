package com.samares_engineering.omf.omf_core_framework.feature;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;

import java.util.Collections;
import java.util.List;

public abstract class SimpleFeature extends AFeature{
    protected SimpleFeature(String name) {
        super(name);
    }

    @Override
    protected List<UIAction> initFeatureActions() {
        return Collections.emptyList();
    }

    @Override
    protected List<LiveActionEngine> initLiveActions() {
        return Collections.emptyList();
    }

    @Override
    protected List<LiveActionEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
    }

    @Override
    protected List<Option> initOptions() {
        return Collections.emptyList();
    }

    @Override
    protected List<Option> initProjectOnlyOptions() {
        return Collections.emptyList();
    }

    @Override
    protected List<Hook> initLifeCycleHooks() {
        return Collections.emptyList();
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {return null;}


}
