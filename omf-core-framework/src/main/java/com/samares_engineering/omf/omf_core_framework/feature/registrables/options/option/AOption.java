/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option;


import com.google.common.base.Strings;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.options.AbstractPropertyOptionsGroup;
import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.nomagic.magicdraw.core.options.OptionsGroup;
import com.nomagic.magicdraw.core.options.ProjectOptions;
import com.nomagic.magicdraw.properties.Property;
import com.nomagic.magicdraw.properties.PropertyResourceProvider;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_core_framework.ui.projectoptions.FeatureProjectOptionsConfigurator;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public abstract class AOption implements IOption {
    String groupName;
    String uriOptions;
    String categoryName;
    Property property;
    AbstractPropertyOptionsGroup optionCategory;
    PropertyResourceProvider resourceProvider = (requestedLabel, prop) -> (requestedLabel);
    Object defaultValue;
    boolean isActivated;
    OptionKind kind;

    private MDFeature feature;

    public void initRegistrableItem(MDFeature feature) {
        this.feature = feature;
    }

    private final List<AOptionListener> registeredListener = new ArrayList<>();
    private final List<AOptionListener> listenerToRegister = new ArrayList<>();

    public void addListenerToRegister(AOptionListener listener) {
        listenerToRegister.add(listener);
    }

    public Property getRegisteredProperty() {
        return optionCategory.getProperty(property.getID());
    }

    @Override
    public void addListener(AOptionListener listener){
        if(kind == OptionKind.Environment)
            addEnvironmentListener(listener);
        if(kind == OptionKind.Project)
            addProjectListener(listener);
    }

    public void addEnvironmentListener(EnvironmentOptions.EnvironmentChangeListener listener){
            Application.getInstance().getEnvironmentOptions().addEnvironmentChangeListener(listener);
    }
    public void addProjectListener(PropertyChangeListener listener){
            OMFUtils.getProject().getOptions().addPropertyChangeListener(listener);
    }
    @Override
    public void removeListener(AOptionListener listener){
        if(kind == OptionKind.Environment)
            removeEnvironmentListener(listener);
        if(kind == OptionKind.Project)
            removeProjectListener(listener);
    }
    public void removeEnvironmentListener(EnvironmentOptions.EnvironmentChangeListener listener){
        Application.getInstance().getEnvironmentOptions().removeEnvironmentChangeListener(listener);
    }
    public void removeProjectListener(PropertyChangeListener listener){
        OMFUtils.getProject().getOptions().removePropertyChangeListener(listener);
    }
    @Override
    public void removeAllListeners(){
        registeredListener.forEach(this::removeListener);
        registeredListener.clear();
    }

    @Override
    public List<AOptionListener> getRegisteredListener(){
        return registeredListener;
    }

    @Override
    public PropertyResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    @Override
    public void setResourceProvider(PropertyResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean isActivated() {
        return isActivated;
    }

    @Override
    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    @Override
    public OptionKind getKind() {
        return kind;
    }

    @Override
    public void setKind(OptionKind kind) {
        this.kind = kind;
    }

    @Override
    public void register(){
        if(kind == OptionKind.Environment)
            registerEnvOption();
        if (kind == OptionKind.Project)
            registerProjectOption();
    }

    private void registerProjectOption() {
        if(defaultValue != null)
            property.setValue(defaultValue);
        if(resourceProvider != null)
            property.setResourceProvider(resourceProvider);
        property.setGroup(groupName);
//        boolean searchExistingOptionCategoryByID = OMFUtils.getProject() != null && optionCategory == null && !Strings.isNullOrEmpty(uriOptions);
//        if(searchExistingOptionCategoryByID)
//            optionCategory = getOrCreateProjectCategory(uriOptions, categoryName);
        if(OMFUtils.getProject() != null)
            OMFUtils.getProject().getOptions().addProperty(ProjectOptions.PROJECT_GENERAL_PROPERTIES, property);
        FeatureProjectOptionsConfigurator.getInstance().addOption(this);
//        optionCategory.addProperty(property);
        listenerToRegister.forEach(this::addListener);
    }

    private void registerEnvOption() {
        if(defaultValue != null)
            property.setValue(defaultValue);
        if(resourceProvider != null)
            property.setResourceProvider(resourceProvider);
        property.setGroup(groupName);
        boolean isOptionsCategory = optionCategory != null;
        boolean isUriOptions = !Strings.isNullOrEmpty(uriOptions);
        if(isOptionsCategory) {
            optionCategory.addProperty(property);
        } else if (isUriOptions) {
            optionCategory = getOrCreateEnvCategory(uriOptions, categoryName);
        }

        listenerToRegister.forEach(this::addListener);
    }

    private AbstractPropertyOptionsGroup getOrCreateEnvCategory(String uriOptions, String categoryName) {
        AbstractPropertyOptionsGroup category = getOptionGroupFromURI(uriOptions);
        return category != null? category: createNewEnvOptionCategory(uriOptions, categoryName);
    }

    private OMFPropertyOptionsGroup createNewEnvOptionCategory(String URI, String categoryName) {
        OMFPropertyOptionsGroup envCategory = new OMFPropertyOptionsGroup(URI, categoryName);
        Application.getInstance().getEnvironmentOptions()
                        .addGroup(envCategory);
        return envCategory;
    }

    @Override
    public void unregister(){
        if(optionCategory == null)
            return;
        optionCategory.removeProperty(property.getName());
        registeredListener.forEach(this::removeListener);
    }

    @Override
    public String getGroupName() {
        return groupName;
    }

    @Override
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String getUriOptions() {
        return uriOptions;
    }

    @Override
    public void setUriOptions(String uriOptions) {
        this.uriOptions = uriOptions;
    }

    @Override
    public Property getProperty() {
        return property;
    }

    @Override
    public void setProperty(Property property) {
        this.property = property;
    }

    @Override
    public OptionsGroup getOptionCategory() {
        return optionCategory;
    }

    @Override
    public void setOptionCategory(AbstractPropertyOptionsGroup optionCategory) {
        this.optionCategory = optionCategory;
    }

    public List<AOptionListener> getListenerToRegister() {
        return listenerToRegister;
    }

    @Override
    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public static AbstractPropertyOptionsGroup getOptionGroupFromURI(String uri){
        return (AbstractPropertyOptionsGroup) Application.getInstance().getEnvironmentOptions().getGroup(uri);
    }

    @Override
    public MDFeature getFeature() {
        return feature;
    }
}
