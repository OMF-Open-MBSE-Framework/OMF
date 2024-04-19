package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.news.ex;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.news.Hook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.news.annotation.OnProjectClosed;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.news.annotation.OnProjectOpened;

public class HookExample extends Hook {

    @OnProjectOpened
    public void onProjectOpened() {
        System.out.println("Project opened");
    }
    @OnProjectClosed
    public void OnProjectClosed() {
        System.out.println("Project opened");
    }
}
