/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option;

import com.nomagic.magicdraw.core.options.AbstractPropertyOptionsGroup;
import com.nomagic.magicdraw.properties.Property;

import javax.annotation.CheckForNull;

public class OptionImpl extends AOption {
    public OptionImpl(@CheckForNull Property property, String categoryName, String uri, String groupName, OptionKind kind) {
        this.property = property;
        this.groupName = groupName;
        this.categoryName = categoryName;
        this.uriOptions = uri;
        this.kind = kind;
        this.isActivated = true;
    }

    public OptionImpl(@CheckForNull Property property, String groupName, @CheckForNull AbstractPropertyOptionsGroup group,OptionKind kind) {
        this.property = property;
        this.groupName = groupName;
        this.optionCategory = group;
        this.kind = kind;
        this.isActivated = true;
    }

    public static OptionImpl createEnvOptionWithCategoryName(@CheckForNull Property property, String groupURI,  String groupName, String categoryName){
        return new OptionImpl(property, groupName, groupURI, categoryName, OptionKind.Environment);
    }
    public static OptionImpl createEnvOptionWithURI(@CheckForNull Property property, String groupName, String uriOptions){
        return new OptionImpl(property, groupName, getOptionGroupFromURI(uriOptions) , OptionKind.Environment);
    }
    public static OptionImpl createProjectOption(@CheckForNull Property property, String groupURI,  String categoryName, String groupName){
        return new OptionImpl(property, categoryName, groupURI, groupName, OptionKind.Project);
    }

    @Override
    public void activate() {
        isActivated = true;
    }
    @Override
    public void deactivate() {
        isActivated = false;
    }
}
