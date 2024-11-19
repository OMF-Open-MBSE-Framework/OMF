/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
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
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_core_framework.ui.projectoptions.FeatureProjectOptionsConfigurator;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing an OMF Option.<br>
 * Options could be of two kinds: Environment or Project.<br>
 * This class provides the basic functionality for registering, unregister,
 * finding option in magicdraw, and getting and setting various properties of the option.
 * Listeners can be added to the option to listen to changes in the option.
 */
public abstract class AOption implements Option {
    String groupName;
    String uriOptions;
    String categoryName;
    Property property;
    AbstractPropertyOptionsGroup optionCategory;
    PropertyResourceProvider resourceProvider = (requestedLabel, prop) -> (requestedLabel);
    Object defaultValue;
    boolean isActivated;
    OptionKind kind;

    private final List<AOptionListener> registeredListener = new ArrayList<>();
    private final List<AOptionListener> listenerToRegister = new ArrayList<>();
    private OMFFeature feature;


    /**
     * Initializes the registrable item with the provided feature.
     *
     * @param feature The feature to initialize the registrable item with.
     */
    public void initRegistrableItem(OMFFeature feature) {
        this.feature = feature;
    }

    /**
     * Adds a listener to the list of listeners to be registered.
     *
     * @param listener The listener to add.
     */
    public void addListenerToRegister(AOptionListener listener) {
        listenerToRegister.add(listener);
    }

    /**
     * Gets the registered property of the option.
     *
     * @return The registered property of the option.
     */
    public Property getRegisteredProperty() {
        return optionCategory.getProperty(property.getID());
    }
    /**
     * Adds a listener to the option. The type of listener added depends on the kind of the option.
     *
     * @param listener The listener to be added.
     */
    @Override
    public void addListener(AOptionListener listener){
        if(kind == OptionKind.Environment)
            addEnvironmentListener(listener);
        if(kind == OptionKind.Project)
            addProjectListener(listener);
    }

    /**
     * Adds an environment change listener to the environment options.
     *
     * @param listener The environment change listener to be added.
     */
    public void addEnvironmentListener(EnvironmentOptions.EnvironmentChangeListener listener){
            Application.getInstance().getEnvironmentOptions().addEnvironmentChangeListener(listener);
    }

    /**
     * Adds a property change listener to the project options.
     *
     * @param listener The property change listener to be added.
     */
    public void addProjectListener(PropertyChangeListener listener){
            OMFUtils.getProject().getOptions().addPropertyChangeListener(listener);
    }

    /**
     * Removes a listener from the option. The type of listener removed depends on the kind of the option.
     *
     * @param listener The listener to be removed.
     */
    @Override
    public void removeListener(AOptionListener listener){
        if(kind == OptionKind.Environment)
            removeEnvironmentListener(listener);
        if(kind == OptionKind.Project)
            removeProjectListener(listener);
    }

    /**
     * Removes an environment change listener from the environment options.
     *
     * @param listener The environment change listener to be removed.
     */
    public void removeEnvironmentListener(EnvironmentOptions.EnvironmentChangeListener listener){
        Application.getInstance().getEnvironmentOptions().removeEnvironmentChangeListener(listener);
    }

    /**
     * Removes a property change listener from the project options.
     *
     * @param listener The property change listener to be removed.
     */
    public void removeProjectListener(PropertyChangeListener listener){
        OMFUtils.getProject().getOptions().removePropertyChangeListener(listener);
    }

    /**
     * Removes all listeners from the option and clears the list of registered listeners.
     */
    @Override
    public void removeAllListeners(){
        registeredListener.forEach(this::removeListener);
        registeredListener.clear();
    }

    /**
     * Gets the list of registered listeners for the option.
     *
     * @return The list of registered listeners.
     */
    @Override
    public List<AOptionListener> getRegisteredListener(){
        return registeredListener;
    }

    /**
     * Gets the resource provider for the option.
     *
     * @return The resource provider for the option.
     */
    @Override
    public PropertyResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    /**
     * Sets the resource provider for the option.
     *
     * @param resourceProvider The resource provider to be set.
     */
    @Override
    public void setResourceProvider(PropertyResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    /**
     * Gets the default value for the option.
     *
     * @return The default value for the option.
     */
    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }
    /**
     * Sets the default value for the option.
     *
     * @param defaultValue The default value to be set for the option.
     */
    @Override
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Checks if the option is activated.
     *
     * @return True if the option is activated, false otherwise.
     */
    @Override
    public boolean isActivated() {
        return isActivated;
    }

    /**
     * Sets the activation status of the option.
     *
     * @param activated The activation status to be set for the option.
     */
    @Override
    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    /**
     * Retrieves the kind of the option.
     *
     * @return The kind of the option.
     */
    @Override
    public OptionKind getKind() {
        return kind;
    }

    /**
     * Sets the kind of the option.
     *
     * @param kind The kind to be set for the option.
     */
    @Override
    public void setKind(OptionKind kind) {
        this.kind = kind;
    }

    /**
     * Registers the option. The method of registration depends on the kind of the option.
     */
    @Override
    public void register(){
        if(kind == OptionKind.Environment)
            registerEnvOption();
        if (kind == OptionKind.Project)
            registerProjectOption();
    }

    /**
     * Registers the option as a project option.
     */
    private void registerProjectOption() {
        if(defaultValue != null)
            property.setValue(defaultValue);
        if(resourceProvider != null)
            property.setResourceProvider(resourceProvider);
        property.setGroup(groupName);
        if(OMFUtils.isProjectOpened())
            OMFUtils.getProject().getOptions().addProperty(ProjectOptions.PROJECT_GENERAL_PROPERTIES, property);
        FeatureProjectOptionsConfigurator.getInstance().addOption(this);
        listenerToRegister.forEach(this::addListener);
    }

    /**
     * Registers the option as an environment option.
     */
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

    /**
     * Retrieves or creates an environment category.
     *
     * @param uriOptions The URI of the options.
     * @param categoryName The name of the category.
     * @return The environment category.
     */
    private AbstractPropertyOptionsGroup getOrCreateEnvCategory(String uriOptions, String categoryName) {
        AbstractPropertyOptionsGroup category = getOptionGroupFromURI(uriOptions);
        return category != null? category: createNewEnvOptionCategory(uriOptions, categoryName);
    }

    /**
     * Creates a new environment option category.
     *
     * @param URI The URI of the category.
     * @param categoryName The name of the category.
     * @return The new environment option category.
     */
    private OMFPropertyOptionsGroup createNewEnvOptionCategory(String URI, String categoryName) {
        OMFPropertyOptionsGroup envCategory = new OMFPropertyOptionsGroup(URI, categoryName);
        Application.getInstance().getEnvironmentOptions()
                        .addGroup(envCategory);
        return envCategory;
    }

    /**
     * Unregisters the option.
     */
    @Override
    public void unregister(){
        if(optionCategory == null)
            return;
        optionCategory.removeProperty(property.getName());
        registeredListener.forEach(this::removeListener);
    }

    /**
     * Retrieves the group name of the option.
     *
     * @return The group name of the option.
     */
    @Override
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the group name of the option.
     *
     * @param groupName The group name to be set for the option.
     */
    @Override
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Retrieves the URI of the options.
     *
     * @return The URI of the options.
     */
    @Override
    public String getUriOptions() {
        return uriOptions;
    }

    /**
     * Sets the URI of the options.
     *
     * @param uriOptions The URI to be set for the options.
     */
    @Override
    public void setUriOptions(String uriOptions) {
        this.uriOptions = uriOptions;
    }

    /**
     * Retrieves the property of the option.
     *
     * @return The property of the option.
     */
    @Override
    public Property getProperty() {
        return property;
    }

    /**
     * Sets the property of the option.
     *
     * @param property The property to be set for the option.
     */
    @Override
    public void setProperty(Property property) {
        this.property = property;
    }

    /**
     * Retrieves the option category.
     *
     * @return The option category.
     */
    @Override
    public OptionsGroup getOptionCategory() {
        return optionCategory;
    }

    /**
     * Sets the option category.
     *
     * @param optionCategory The option category to be set.
     */
    @Override
    public void setOptionCategory(AbstractPropertyOptionsGroup optionCategory) {
        this.optionCategory = optionCategory;
    }

    /**
     * Retrieves the list of listeners to be registered.
     *
     * @return The list of listeners to be registered.
     */
    public List<AOptionListener> getListenerToRegister() {
        return listenerToRegister;
    }

    /**
     * Retrieves the category name of the option.
     *
     * @return The category name of the option.
     */
    @Override
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Sets the category name of the option.
     *
     * @param categoryName The category name to be set for the option.
     */
    @Override
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * Retrieves the option group from the URI.
     *
     * @param uri The URI of the option group.
     * @return The option group.
     */
    public static AbstractPropertyOptionsGroup getOptionGroupFromURI(String uri){
        return (AbstractPropertyOptionsGroup) Application.getInstance().getEnvironmentOptions().getGroup(uri);
    }

    /**
     * Retrieves the feature of the option.
     *
     * @return The feature of the option.
     */
    @Override
    public OMFFeature getFeature() {
        return feature;
    }
}
