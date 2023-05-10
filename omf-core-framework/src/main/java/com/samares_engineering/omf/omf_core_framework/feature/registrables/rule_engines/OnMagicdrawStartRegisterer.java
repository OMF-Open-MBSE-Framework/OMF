package com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines;

import com.nomagic.magicdraw.core.Application;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.IFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.errors.FeatureException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.OnMagicDrawStart;

import java.util.ArrayList;
import java.util.List;

public class OnMagicdrawStartRegisterer implements IFeatureItemRegisterer<OnMagicDrawStart> {
    private FeatureRegisterer featureRegisterer;
    private List<OnMagicDrawStart> onMagicDrawStartFeatures;

    public OnMagicdrawStartRegisterer(FeatureRegisterer featureRegisterer) {
        this.featureRegisterer = featureRegisterer;
        this.onMagicDrawStartFeatures = new ArrayList<>();
        Application.getInstance().insertActivityAfterStartup(() -> {
            for (OnMagicDrawStart feature : onMagicDrawStartFeatures) {
                try {
                    feature.getOnMagicDrawStartRunnable().run();
                }catch (Exception e){
                    OMFErrorHandler.handleException(new FeatureException("[Feature " + feature.getFeature().getName() + "] Error during MagicDraw start behavior", e, FeatureException.ECriticality.CRITICAL), false);
                }
            }
        });
    }

    @Override
    public void init(FeatureRegisterer featureRegisterer) throws FeatureException {

    }

    @Override
    public void registerFeatureItems(List<OnMagicDrawStart> item) throws FeatureException {
        item.forEach(this::registerFeatureItem);
    }

    @Override
    public void unregisterFeatureItems(List<OnMagicDrawStart> mdFeature) throws FeatureException {
        mdFeature.forEach(this::unregisterFeatureItem);
    }

    @Override
    public void registerFeatureItem(OnMagicDrawStart item) {
        try {
            onMagicDrawStartFeatures.add(item);
        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException("[Feature " + item.getFeature().getName() + "] Error during MagicDraw start behavior Registering", e, FeatureException.ECriticality.CRITICAL), false);
        }
    }

    @Override
    public void unregisterFeatureItem(OnMagicDrawStart item) {
        try {
            onMagicDrawStartFeatures.remove(item);
        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException("[Feature " + item.getFeature().getName() + "] Error during MagicDraw start behavior Unregistering", e, FeatureException.ECriticality.CRITICAL), false);
        }
    }

    @Override
    public void registerFeature(MDFeature feature) throws FeatureException {

    }

    @Override
    public void unregisterFeature(MDFeature feature) throws FeatureException {

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
