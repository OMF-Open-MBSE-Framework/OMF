package com.samares_engineering.omf.omf_public_features.apiserver;

import com.nomagic.magicdraw.properties.StringProperty;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

public class APIEnvOptionsHelper extends EnvOptionsHelper {
    private static APIEnvOptionsHelper instance;

    public APIEnvOptionsHelper(OMFFeature feature) {
        super(feature);
    }

    public static APIEnvOptionsHelper getInstance(OMFFeature feature) {
        if(instance == null)
            instance = new APIEnvOptionsHelper(feature);
        return instance;
    }

    /*
        **** API SERVER CONFIGURATION GROUP ****
     */
    public static final String API_SERVER_CONFIGURATION_GROUP = "API Server configuration";
    public static final String API_SERVER_URL = "API Server URL (ex: 'http://localhost') :" ;
    public static final String API_SERVER_PORT = "API Server Port: (ex: '9856')";

    public static final String API_SERVER_ACTIVATED = "is Server Activated:";

    public static String getOrganizerConfigFilePathDefaultValue() {
        return OMFUtils.getUserDir() + "/plugins/com.samares-engineering.omf.plugin/resources/organizer_config.csv";
    }

    public String getServerURL() {
        StringProperty p = (StringProperty) getPropertyByName(API_SERVER_URL);
        return p.getString();
    }

    public void setServerURL(String path) {
        getPropertyByName(API_SERVER_URL).setValue(path);
    }

    public int getServerPort() {
        String portStringValue = (String) getPropertyByName(API_SERVER_PORT).getValue();
        return Integer.parseInt(portStringValue);
    }

    public void setServerPort(boolean isActivated) {
        getPropertyByName(API_SERVER_PORT).setValue(isActivated);
    }

    public boolean isServerActivated() {
        return (boolean) getPropertyByName(API_SERVER_ACTIVATED).getValue();
    }
    public void setServerActivated(boolean isActivated) {
        getPropertyByName(API_SERVER_ACTIVATED).setValue(isActivated);
    }

}
