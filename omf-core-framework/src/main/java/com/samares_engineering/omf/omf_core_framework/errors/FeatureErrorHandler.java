package com.samares_engineering.omf.omf_core_framework.errors;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.CriticalFeatureException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.OMFFeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;

public class FeatureErrorHandler {
    public void handleException(CriticalFeatureException criticalFeatureException) {
        MDFeature brokenFeature = criticalFeatureException.getFeature();
        try {
            FeatureRegisterer featureRegisterer = brokenFeature.getPlugin().getFeatureRegister();
            featureRegisterer.unregisterFeature(brokenFeature);
            OMFLogger.getInstance().info("Feature " + brokenFeature.getName() + " has been unregistered due to a critical error");
        }catch (OMFFeatureRegisteringException featureNotUnregister) {
           OMFErrorHandler.handleException(featureNotUnregister);
        }
    }
}
