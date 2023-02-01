/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.plugin.options;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.properties.ElementProperty;
import com.nomagic.magicdraw.properties.PropertyResourceProvider;
import com.nomagic.magicdraw.properties.StringProperty;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.ui.environmentoptions.OMFEnvOptionResources;
import com.samares.omf.core.ui.environmentoptions.OMFEnvironmentOptionsGroup;

import java.util.Objects;

public class OMFPluginEnvOptionsGroup extends OMFEnvironmentOptionsGroup {
    public static final String ID = "env.options.omf.plugin";
    private static final String OMF_PLUGIN_CATEGORY_NAME = "OMF Plugin";

    @SuppressWarnings("ConstantConditions")
    public static final PropertyResourceProvider PROPERTY_RESOURCE_PROVIDER = (key, property) -> OMFEnvOptionResources.getString(key);

    public static OMFPluginEnvOptionsGroup getInstance() {
        return Objects.requireNonNull(
                (OMFPluginEnvOptionsGroup) Application.getInstance().getEnvironmentOptions().getGroup(OMFPluginEnvOptionsGroup.ID),
                "Trying to access options group before it has been instantiated");
    }
    public OMFPluginEnvOptionsGroup() {
        super(ID, OMF_PLUGIN_CATEGORY_NAME);
    }

    @Override
    public String getName() {
        return OMFEnvOptionResources.getString(OMF_PLUGIN_CATEGORY_NAME);
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

    public void setOrganizerActivation(boolean isActivated) {
        getPropertyByName(ORGANIZER_ACTIVATION_ID).setValue(isActivated);
    }

    public void addOwnerPropertyOption(String name, Element owner) {
        ElementProperty property = new ElementProperty(name, owner);
        property.setResourceProvider(PROPERTY_RESOURCE_PROVIDER);
        property.setGroup(ORGANIZER_CONFIG_GRP);
        addProperty(property, true);
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
    public static final String INSTANCE_CONFIG_FILE_PATH = "Automation type to instance configuration file path";
    public static final String INSTANCE_ACTIVATION_ID = "Activate automatic instance stereotype application";

    public static String getInstanceConfigFilePathDefaultValue() {
        return System.getProperty("user.dir") + "/plugins/com.samares.omf.plugin/resources/instance_config.csv";
    }

    public String getInstanceConfigFilePath() {
        StringProperty p = (StringProperty) getPropertyByName(INSTANCE_CONFIG_FILE_PATH);
        return p.getString();
    }

    public void setInstanceConfigFilePath(String path) {
        getPropertyByName(INSTANCE_CONFIG_FILE_PATH).setValue(path);
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
