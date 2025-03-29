/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.ui.environmentoptions;

import com.nomagic.magicdraw.core.options.AbstractPropertyOptionsGroup;
import com.nomagic.magicdraw.properties.Property;
import com.nomagic.magicdraw.properties.PropertyResourceProvider;
import com.nomagic.magicdraw.resources.ResourceManager;

import java.util.Objects;

public class OMFPropertyOptionsGroup extends AbstractPropertyOptionsGroup {

    public String ID;
    public String name;


    public static final PropertyResourceProvider PROPERTY_RESOURCE_PROVIDER = (key, property) -> ResourceManager.getStringFor(key, OMFPropertyOptionsGroup.class.getName(), OMFPropertyOptionsGroup.class.getClassLoader());


    public OMFPropertyOptionsGroup(String ID, String categoryName){
        super(ID);
        this.ID = ID;
        this.name = categoryName;
    }


    @Override
    public String getName() {
        return ResourceManager.getStringFor(name, OMFPropertyOptionsGroup.class.getName(), OMFPropertyOptionsGroup.class.getClassLoader());
    }

    public PropertyResourceProvider getResourceProvider(){
        return PROPERTY_RESOURCE_PROVIDER;
    }

    public static PropertyResourceProvider getPropertyResourceProvider(){
        return PROPERTY_RESOURCE_PROVIDER;
    }

    public Property getPropertyByName(String name) {
        return Objects.requireNonNull(getProperty(name), "Can't find environment property " + name);
    }

    public String getID() {
        return ID;
    }
}
