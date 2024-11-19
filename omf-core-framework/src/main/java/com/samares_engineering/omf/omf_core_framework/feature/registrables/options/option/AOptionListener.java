/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option;

import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.SysoutColorPrinter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public abstract class AOptionListener implements EnvironmentOptions.EnvironmentChangeListener, PropertyChangeListener {
    @Override
    public void updateByEnvironmentProperties(List<Property> list) {
//        list.forEach(p -> ColorPrinter.status("Update" + p.getName()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SysoutColorPrinter.print("PropChange: " + evt.getPropertyName()
                + " prop: " + evt.getSource()
                + " val: " + evt.getNewValue());
    }
}
