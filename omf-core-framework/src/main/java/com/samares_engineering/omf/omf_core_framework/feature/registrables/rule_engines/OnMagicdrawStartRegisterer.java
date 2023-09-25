package com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines;

import com.nomagic.magicdraw.core.Application;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.OMFFeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.FeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.OnMagicDrawStart;

import java.util.ArrayList;
import java.util.List;

public class OnMagicdrawStartRegisterer implements FeatureItemRegisterer<OnMagicDrawStart> {
    private FeatureRegisterer featureRegisterer;
    private List<OnMagicDrawStart> onMagicDrawStartFeatures;

    public OnMagicdrawStartRegisterer(FeatureRegisterer featureRegisterer) {
        this.featureRegisterer = featureRegisterer;
        this.onMagicDrawStartFeatures = new ArrayList<>();
        Application.getInstance().insertActivityAfterStartup(() -> {
            for (OnMagicDrawStart featureItem : onMagicDrawStartFeatures) {
                try {
                    featureItem.getOnMagicDrawStartRunnable().run();
                } catch (Exception e){
                    throw new OMFFeatureRegisteringException("Error during MagicDraw start behavior", e);
                }
            }
        });
    }

    @Override
    public void init(FeatureRegisterer featureRegisterer){

    }

    @Override
    public void registerFeatureItems(List<OnMagicDrawStart> item){
        item.forEach(this::registerFeatureItem);
    }

    @Override
    public void unregisterFeatureItems(List<OnMagicDrawStart> mdFeature){
        mdFeature.forEach(this::unregisterFeatureItem);
    }

    @Override
    public void registerFeatureItem(OnMagicDrawStart item) {
        try {
            onMagicDrawStartFeatures.add(item);
        }catch (Exception e){
            throw new OMFFeatureRegisteringException("Error while registering OnMagicDraw start feature item", e);
        }
    }

    @Override
    public void unregisterFeatureItem(OnMagicDrawStart item) {
        try {
            onMagicDrawStartFeatures.remove(item);
        }catch (Exception e){
            throw new OMFFeatureRegisteringException("Error while unregistering OnMagicDraw start feature item", e);
        }
    }

    @Override
    public void registerFeatureItems(MDFeature feature){

    }

    @Override
    public void unregisterFeatureItems(MDFeature feature){

    }

    @Override
    public FeatureRegisterer getFeatureRegisterer() {
        return featureRegisterer;
    }

    @Override
    public void setFeatureRegisterer(FeatureRegisterer featureRegisterer) {
        this.featureRegisterer = featureRegisterer;
    }
}
