package com.samares.omf.core.feature.options;

import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.nomagic.magicdraw.properties.Property;
import com.samares.omf.core.utils.ColorPrinter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public abstract class AOptionListener implements EnvironmentOptions.EnvironmentChangeListener, PropertyChangeListener {
    @Override
    public void updateByEnvironmentProperties(List<Property> list) {
        list.forEach(p -> ColorPrinter.status("Update" + p.getName()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ColorPrinter.print("PropChange: " + evt.getPropertyName()
                + " prop: " + evt.getSource()
                + " val: " + evt.getNewValue());
    }
}
