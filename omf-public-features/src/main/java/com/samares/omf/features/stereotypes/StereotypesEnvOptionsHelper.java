package com.samares.omf.features.stereotypes;

import com.nomagic.magicdraw.properties.ElementProperty;
import com.nomagic.magicdraw.properties.StringProperty;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.feature.EnvOptionsHelper;
import com.samares.omf.core.feature.MDFeature;
import com.samares.omf.core.ui.environmentoptions.APropertyOptionsGroup;

public class StereotypesEnvOptionsHelper extends EnvOptionsHelper {
    public StereotypesEnvOptionsHelper(StereotypesFeature stereotypesFeature) {
        super(stereotypesFeature);
    }

    public static StereotypesEnvOptionsHelper getInstance(MDFeature feature) {
        return ((StereotypesFeature) feature).getOptionsHelper();
    }

    /*
    Organizer group
     */

    public static final String ORGANIZER_CONFIG_GRP = "Organizer";
    public static final String ORGANIZER_CONFIG_FILE_PATH_ID = "Automation organizer configuration file path";
    public static final String ORGANIZER_ACTIVATION_ID = "Activate automatic element organization";

    public static String getOrganizerConfigFilePathDefaultValue() {
        return System.getProperty("user.dir") + "/plugins/com.samares.omf.plugin/resources/organizer_config.csv";
    }

    public String getOrganizerConfigFilePath() {
        StringProperty p = (StringProperty) getPropertyByName(ORGANIZER_CONFIG_FILE_PATH_ID);
        return p.getString();
    }

    public void setOrganizerConfigFilePath(String path) {
        getPropertyByName(ORGANIZER_CONFIG_FILE_PATH_ID).setValue(path);
    }

    public boolean isOrganizerActivated() {
        return (boolean) getPropertyByName(ORGANIZER_ACTIVATION_ID).getValue();
    }

    public void setOrganizerActivated(boolean isActivated) {
        getPropertyByName(ORGANIZER_ACTIVATION_ID).setValue(isActivated);
    }

    public void addOwnerPropertyOption(String name, Element owner) {
        ElementProperty property = new ElementProperty(name, owner);
        property.setResourceProvider(APropertyOptionsGroup.PROPERTY_RESOURCE_PROVIDER);
        property.setGroup(ORGANIZER_CONFIG_GRP);
        getOptionGroup().addProperty(property, true);
    }
    public Element getOwnerPropertyOption(String name) {
        return (Element) getPropertyByName(name).getValue();
    }

    public void setOwnerPropertyOption(String idOption, Element owner) {
        getPropertyByName(idOption).setValue(owner);
    }

    /*
    Type group
     */

    public static final String INSTANCE_CONFIG_GRP = "Instance";
    public static final String INSTANCE_CONFIG_FILE_PATH_ID = "Automation type to instance configuration file path";
    public static final String INSTANCE_ACTIVATION_ID = "Activate automatic instance stereotype application";

    public static String getInstanceConfigFilePathDefaultValue() {
        return System.getProperty("user.dir") + "/plugins/com.samares.omf.plugin/resources/instance_config.csv";
    }

    public String getInstanceConfigFilePath() {
        StringProperty p = (StringProperty) getPropertyByName(INSTANCE_CONFIG_FILE_PATH_ID);
        return p.getString();
    }

    public void setInstanceConfigFilePath(String path) {
        getPropertyByName(INSTANCE_CONFIG_FILE_PATH_ID).setValue(path);
    }

    public boolean isInstanceActivated() {
        return (boolean) getPropertyByName(INSTANCE_ACTIVATION_ID).getValue();
    }

    public void setInstanceActivated(boolean isActivated) {
        getPropertyByName(INSTANCE_ACTIVATION_ID).setValue(isActivated);
    }

    /*
    Instance group
     */
    public static final String TYPE_CONFIG_GRP = "Type";
    public static final String TYPE_CONFIG_FILE_PATH_ID = "Automation instance to type creation configuration file path";

    public static final String TYPE_ACTIVATION_ID = "Activate automatic type creation on instance creation";

    public static String getTypeConfigFilePathDefaultValue() {
        return System.getProperty("user.dir") + "/plugins/com.samares.omf.plugin/resources/type_config.csv";
    }

    public boolean isTypeActivated() {
        return (boolean) getPropertyByName(TYPE_ACTIVATION_ID).getValue();
    }

    public void setTypeActivated(boolean isActivated) {
        getPropertyByName(TYPE_ACTIVATION_ID).setValue(isActivated);
    }

    public String getTypeConfigFilePath() {
        StringProperty p = (StringProperty) getPropertyByName(TYPE_CONFIG_FILE_PATH_ID);
        return p.getString();
    }

    public void setTypeConfigFilePath(String path) {
        getPropertyByName(TYPE_CONFIG_FILE_PATH_ID).setValue(path);
    }
}
