/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option;

import com.nomagic.magicdraw.core.options.AbstractPropertyOptionsGroup;
import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.nomagic.magicdraw.core.options.OptionsGroup;
import com.nomagic.magicdraw.properties.Property;
import com.nomagic.magicdraw.properties.PropertyResourceProvider;
import com.samares_engineering.omf.omf_core_framework.feature.RegistrableFeatureItem;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface Option extends RegistrableFeatureItem {
    void addListener(AOptionListener listener);

    /**
     * register listener on Environment options update (when user clicks on ok).
     * @param listener the listener to register
     */
    void addEnvironmentListener(EnvironmentOptions.EnvironmentChangeListener listener);

    /**
     * register listener on Project options update (when user clicks on ok).
     * @param listener the listener to register
     */
    void addProjectListener(PropertyChangeListener listener);

    void removeListener(AOptionListener listener);
    void removeEnvironmentListener(EnvironmentOptions.EnvironmentChangeListener listener);
    void removeProjectListener(PropertyChangeListener listener);

    /**
     * BETA
     * register Generic Listener for both project and environment
     * @param listener the listener to register
     */
    void addListenerToRegister(AOptionListener listener);

    void removeAllListeners();


    /**
     * BETA
     * get all registered Generic Listener for both project and environment
     * @return all registered generic listener
     */
    List<AOptionListener> getRegisteredListener();

    /**
     * get option resource provider, it's role is to Stringify the option for ui.
     * @return the resource provider
     */
    PropertyResourceProvider getResourceProvider();

    /**
     * set option resource provider, it's role is to Stringify the option for ui.
     * @param resourceProvider the resource provider to set
     */
    void setResourceProvider(PropertyResourceProvider resourceProvider);

    Object getDefaultValue();

    void setDefaultValue(Object defaultValue);

    void setActivated(boolean activated);

    /**
     * is it a Project Option or an Environment Option
     * @return option kind
     */
    OptionKind getKind();

    /**
     * set the option kind: Project Option or an Environment Option
     * @param kind Project Option or an Environment Option
     */
    void setKind(OptionKind kind);

    /**
     * Register the option according to its kind (Project/Environment)
     */

    void register();

    /**
     * unregister the option according to its kind (Project/Environment)
     */
    void unregister();

    String getGroupName();

    void setGroupName(String groupName);

    String getUriOptions();

    void setUriOptions(String uriOptions);

    /**
     * get the concrete option.
     * @return the PropertyImpl option
     */
    Property getProperty();

    /**
     * set the concrete option.
     * @param property the PropertyImpl option to set
     */

    void setProperty(Property property);

    OptionsGroup getOptionCategory();

    /**
     * Environment options use AbstractPropertyOptionsGroup to store and manage options.
     * @param optionCategory the category to set
     */
    void setOptionCategory(AbstractPropertyOptionsGroup optionCategory);

    Property getRegisteredProperty();

    String getCategoryName();
    void setCategoryName(String categoryName);
}
