/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.ui.environmentoptions;

import com.nomagic.magicdraw.properties.BooleanProperty;

import java.util.Objects;

public class OMFEnvironmentOptionsGroup extends AEnvironmentFeatureConfigurator {

    /**
     * ID of the example options group.
     */
    public static final String defaultID = "env.options.omf.conf";

    public static OMFEnvironmentOptionsGroup instance = null;

    /**
     * ID of property group 1.
     */
    public static final String GR_1_AUTOMATION_MNGT = "Automation activation";
    public static final String ID_ACTIVATE_AUTOMATION = "Activate OMF Automations";

    /**
     * Constructs this options group.
     */
    public OMFEnvironmentOptionsGroup() {
        this(defaultID, "OMF");
    }
    public OMFEnvironmentOptionsGroup(String name) {
        this(defaultID, name);
    }
    public OMFEnvironmentOptionsGroup(String ID, String categoryName) {
        super(ID, categoryName);
    }

    public static OMFEnvironmentOptionsGroup getInstance() {
        if (null == instance){
            instance = new OMFEnvironmentOptionsGroup();
        }
        return instance;
    }

    @Override
    public void setDefaultValues() {
        setGroup1_defaultValue();
    }

    public void setGroup1_defaultValue() {
        //DEACTIVATE AUTOMATION
        BooleanProperty propertyAA = new BooleanProperty(ID_ACTIVATE_AUTOMATION, false);
        propertyAA.setValue(false);
        propertyAA.setResourceProvider(PROPERTY_RESOURCE_PROVIDER);
        propertyAA.setGroup(GR_1_AUTOMATION_MNGT);
        addProperty(propertyAA, true);
    }

    public boolean isActivateAutomationValue() {
        BooleanProperty p = (BooleanProperty) Objects.requireNonNull(getPropertyByName(ID_ACTIVATE_AUTOMATION), "");
        return p.getBoolean();
    }

    public void setAutomationsActivated(boolean isAutomationsActivated) {
        getPropertyByName(ID_ACTIVATE_AUTOMATION).setValue(isAutomationsActivated);
    }

    public void setDeactivateAutomationValue(boolean shallWizardBeTriggered) {
        getPropertyByName(ID_ACTIVATE_AUTOMATION).setValue(shallWizardBeTriggered);
    }
}
