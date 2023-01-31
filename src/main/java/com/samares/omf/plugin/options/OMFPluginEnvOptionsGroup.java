/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.plugin.options;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.ElementProperty;
import com.nomagic.magicdraw.properties.PropertyResourceProvider;
import com.nomagic.magicdraw.properties.StringProperty;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.ui.environmentoptions.EnvOptionResources_OMF;
import com.samares.omf.core.ui.environmentoptions.OMFEnvironmentOptionsGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OMFPluginEnvOptionsGroup extends OMFEnvironmentOptionsGroup {
    /**
     * ID of the example options group.
     */
    public static final String ID = "env.options.omf.plugin";

    private static final String ENV_OPTION_GROUP_NAME = "OMF Plugin";

    /**
     * ID of property group 1.
     */
    public static final String ORGANIZER_CONFIG_GRP = "Organizer";
    public static final String ORGANIZER_CONFIG_FILE_PATH_ID = "Automation organizer configuration file path";
    public static final String ORGANIZER_ACTIVATION_ID = "Activate automatic element organization";

    public static final String T2I_CONFIG_GRP = "Type";
    public static final String I2T_CONFIG_GRP = "Instance";

    public static final String T2I_CONFIG_FILE_PATH_ID = "Automation type to instance configuration file path";
    public static final String I2T_CONFIG_FILE_PATH_ID = "Automation instance to type creation configuration file path";
    public static final String T2I_ACTIVATION_ID = "Activate automatic instance stereotype application";
    public static final String I2T_ACTIVATION_ID = "Activate automatic type creation on instance creation";
    @SuppressWarnings("ConstantConditions")
    public static final PropertyResourceProvider PROPERTY_RESOURCE_PROVIDER = (key, property) -> EnvOptionResources_OMF.getString(key);

    public static List<String> l_unchangedPropertyID = new ArrayList<>();


    public static OMFPluginEnvOptionsGroup getInstance() {
        return Objects.requireNonNull(
                (OMFPluginEnvOptionsGroup) Application.getInstance().getEnvironmentOptions().getGroup(OMFPluginEnvOptionsGroup.ID),
                "Trying to access options group before it has been instantiated");
    }

    public OMFPluginEnvOptionsGroup() {
        super(ID, ORGANIZER_CONFIG_GRP);
        l_unchangedPropertyID.add(ORGANIZER_CONFIG_FILE_PATH_ID);
        l_unchangedPropertyID.add(ORGANIZER_ACTIVATION_ID);
    }

    public static String getOrganizerPathListenerConfigurationDefaultValue() {
        return System.getProperty("user.dir") + "/plugins/com.samares.omf.plugin/resources/" +
                "organizer_config.csv";
    }

    public boolean isOrganizerActivated() {
        return (boolean) getPropertyByName(ORGANIZER_ACTIVATION_ID).getValue();
    }

    public void setOrganizerActivation(boolean isActivated) {
        getPropertyByName(ORGANIZER_ACTIVATION_ID).setValue(isActivated);
    }

    // TODO This env option is set programmatically, should investigate how to integrate with OMF Feature framework
    public void addOwnerPropertyOption(String id, String name, Element owner) {
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

    public String getOrganizerConfigFilePath() {
        StringProperty p = (StringProperty) getPropertyByName(ORGANIZER_CONFIG_FILE_PATH_ID);
        return p.getString();
    }

    public void setOrganizerConfigFilePath(String path) {
        getPropertyByName(ORGANIZER_CONFIG_FILE_PATH_ID).setValue(path);
    }

    public void resetOptions() {
        getOptions().getProperties().stream()
                .filter(property -> l_unchangedPropertyID.contains(property.getID()))
                .forEach(property -> getOptions().removeProperty(property));
    }

    @Override
    public String getName() {
        return EnvOptionResources_OMF.getString(ENV_OPTION_GROUP_NAME);
    }

    public static String getT2IPathListenerConfigurationDefaultValue() {
        return System.getProperty("user.dir") + "/plugins/com.samares.omf.plugin/resources/instance_config.csv";
    }

    public static String getI2TPathListenerConfigurationDefaultValue() {
        return System.getProperty("user.dir") + "/plugins/com.samares.omf.plugin/resources/type_config.csv";
    }

    public void setT2IActivation(boolean isActivated) {
        BooleanProperty isActivatedProperty = new BooleanProperty(T2I_ACTIVATION_ID, isActivated);
        isActivatedProperty.setValue(isActivated);
        isActivatedProperty.setResourceProvider(PROPERTY_RESOURCE_PROVIDER);
        isActivatedProperty.setGroup(T2I_CONFIG_GRP);
        addProperty(isActivatedProperty, true);
    }

    public void setI2TActivation(boolean isActivated) {
        BooleanProperty isActivatedProperty = new BooleanProperty(I2T_ACTIVATION_ID, isActivated);
        isActivatedProperty.setValue(isActivated);
        isActivatedProperty.setResourceProvider(PROPERTY_RESOURCE_PROVIDER);
        isActivatedProperty.setGroup(T2I_CONFIG_GRP);
        addProperty(isActivatedProperty, true);
    }

    public String getT2IConfigFilePath() {
        StringProperty p = (StringProperty) getPropertyByName(T2I_CONFIG_FILE_PATH_ID);
        return p.getString();
    }

    public void setT2IPathListenerConfiguration(String path) {
        StringProperty property = new StringProperty(T2I_CONFIG_FILE_PATH_ID, path);
        property.setValue(path);
        property.setResourceProvider(PROPERTY_RESOURCE_PROVIDER);
        property.setGroup(T2I_CONFIG_GRP);
        addProperty(property, true);
    }

    public String getI2TConfigFilePath() {
        StringProperty p = (StringProperty) getPropertyByName(I2T_CONFIG_FILE_PATH_ID);
        return p.getString();
    }

    public void setI2TPathListenerConfiguration(String path) {
        StringProperty property = new StringProperty(I2T_CONFIG_FILE_PATH_ID, path);
        property.setValue(path);
        property.setResourceProvider(PROPERTY_RESOURCE_PROVIDER);
        property.setGroup(T2I_CONFIG_GRP);
        addProperty(property, true);
    }

    public boolean isT2IActivated() {
        return (boolean) getPropertyByName(T2I_ACTIVATION_ID).getValue();
    }

    public boolean isI2TActivated() {
        return (boolean) getPropertyByName(I2T_ACTIVATION_ID).getValue();
    }

}
