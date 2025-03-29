package com.samares_engineering.omf.omf_core_framework.listeners;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;

import java.util.HashMap;
import java.util.List;

public abstract class AListener implements IListener{
    protected boolean activated;
    protected boolean isRegistered;
    protected HashMap<String, List<LiveActionEngine>> liveActionEngines = new HashMap<>();
    protected int priority = 0;

    public boolean isActivated() {
        return activated;
    }

    public void activate() {
        this.activated = true;
    }

    public void deactivate() {
        this.activated = false;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    @Override
    public boolean isRegistered() {
        return isRegistered;
    }
    @Override
    public boolean isNotRegistered(){
        return !isRegistered;
    }

    @Override
    public void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    @Override
    public HashMap<String, List<LiveActionEngine>> getLiveActionEngineMap() {
        return liveActionEngines;
    }

    @Override
    public void setLiveActionEngineMap(HashMap<String, List<LiveActionEngine>> liveActionEngines) {
        this.liveActionEngines = liveActionEngines;
    }
}
