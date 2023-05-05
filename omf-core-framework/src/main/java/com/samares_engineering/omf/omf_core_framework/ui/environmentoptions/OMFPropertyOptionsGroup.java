/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.ui.environmentoptions;

import com.nomagic.magicdraw.properties.BooleanProperty;

import java.util.Objects;

public class OMFPropertyOptionsGroup extends APropertyOptionsGroup {

    /**
     * ID of the example options group.
     */
    public static final String defaultID = "env.options.omf.conf";

    public static OMFPropertyOptionsGroup instance = null;

    /**
     * ID of property group 1.
     */
    public static final String GR_1_AUTOMATION_MNGT = "Automation activation";
    public static final String ID_ACTIVATE_AUTOMATION = "Activate OMF Automations";

    /**
     * Constructs this options group.
     */
    public OMFPropertyOptionsGroup() {
        this(defaultID, "OMF");
    }
    public OMFPropertyOptionsGroup(String name) {
        this(defaultID, name);
    }
    public OMFPropertyOptionsGroup(String ID, String categoryName) {
        super(ID, categoryName);
    }

    public static OMFPropertyOptionsGroup getInstance() {
        if (null == instance){
            instance = new OMFPropertyOptionsGroup();
        }
        return instance;
    }

    @Override
    public void setDefaultValues() {
        setGroup1_defaultValue();
    }

    public void setGroup1_defaultValue() {
        //DEACTIVATE AUTOMATION
        BooleanProperty propertyAA = new BooleanProperty(ID_ACTIVATE_AUTOMATION, true);
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
