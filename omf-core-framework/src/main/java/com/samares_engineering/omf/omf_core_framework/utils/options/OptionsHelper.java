/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils.options;

import com.google.common.base.Strings;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.options.AbstractPropertyOptionsGroup;
import com.nomagic.magicdraw.core.options.ProjectOptions;
import com.nomagic.magicdraw.properties.Property;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public class OptionsHelper {

    /**
     * Search in all the Project options the given property in the given category.
     *
     * @param category   name of the category, list available in ProjectOptions staticField (PROJECT_GENERAL_PROPERTIES, ...)
     * @param optionName name of the option
     * @return Property option
     */
    public static Optional<Property> getProjectOptionByCategoryName(String category, String optionName) {
        Optional<Property> option = getProjectOptionsByCategory(category, optionName, OMFUtils.getProject());
        return option;
    }

    /**
     * Search in all the Project options the given property in the given category. Case and White space non-sensitive.
     *
     * @param optionName name of the option
     * @return Property option
     */
    public static Optional<Property> getProjectOptionByOptionName(String optionName) {
        ProjectOptions options = OMFUtils.getProject().getOptions();
        Optional<Property> optOption;
        optOption = Arrays.stream(ProjectOptions.class.getFields())
                .map(field -> {
                    try {
                        return field.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(cat -> !Strings.isNullOrEmpty(cat))
                .map(cat -> getProjectOptionsByCategory(cat, optionName, OMFUtils.getProject()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();


        return optOption;
    }

    /**
     * Search in Project options the given property in the given category. If absent it returns Null.
     *
     * @param category   name of the category (General, OMF, OMF ORGANIZER)
     * @param optionName name of the option
     * @param project    project containing the options
     * @return Property option
     */
    private static Optional<Property> getProjectOptionsByCategory(String category, String optionName, Project project) {
        try {
            return Optional.ofNullable(project.getOptions().getProperty(category, optionName));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Search in all the Environment options the given property in the given category. Case and White space non-sensitive.
     *
     * @param category   name of the category (General, OMF, OMF ORGANIZER)
     * @param optionName name of the option
     * @return Property option
     */
    public static Optional<Property> getEnvironmentOptionByCategoryName(String category, String optionName) {
        Optional<AbstractPropertyOptionsGroup> optCategory = Application.getInstance().getEnvironmentOptions().getGroups()
                .stream()
                .filter(AbstractPropertyOptionsGroup.class::isInstance)
                .map(AbstractPropertyOptionsGroup.class::cast)
                .filter(cat -> cat.getName().equals(category))
                .findAny();

        if (optCategory.isEmpty())
            return Optional.empty();

        Optional<Property> optOption = optCategory.get().getOptions().getProperties().stream()
                .filter(property -> compareStringsNoCaseNoSpace(property.getName(), optionName))
                .findAny();


        return optOption;
    }

    /**
     * Search in all the Environment options the given property in the given ID. Case and White space non-sensitive.
     *
     * @param id         name of the category (General, OMF, OMF ORGANIZER)
     * @param optionName name of the option
     * @return Property option
     */
    public static Optional<Property> getEnvironmentOptionByID(String id, String optionName) {
        Optional<PropertyManager> optCategory = Application.getInstance().getEnvironmentOptions().getGroups()
                .stream()
                .filter(AbstractPropertyOptionsGroup.class::isInstance)
                .map(AbstractPropertyOptionsGroup.class::cast)
                .map(AbstractPropertyOptionsGroup::getOptions)
                .filter(cat -> cat.getName().equals(id))
                .findAny();

        if (optCategory.isEmpty())
            return Optional.empty();

        Optional<Property> optOption = optCategory.get().getProperties().stream()
                .filter(property -> compareStringsNoCaseNoSpace(property.getName(), optionName))
                .findAny();

        return optOption;
    }

    /**
     * Compare the option actual value with the given value.
     *
     * @param option property to compare
     * @param value  value to compare
     * @return true if the value is the same, false otherwise
     */
    protected static boolean compareOptionValue(Property option, Object value) {
        return option.getValue().equals(value);
    }

    /**
     * Search for the environment option and compare it with the given value.
     * Test will fail if not found. Case and White space non-sensitive.
     * See: getEnvironmentOption for more detail
     *
     * @param category   name of the category (General, OMF, OMF ORGANIZER)
     * @param optionName name of the option
     * @param value      value to compare
     * @return true if the value is the same, false otherwise
     * @throws LegacyOMFException if the option is not found
     */
    protected static boolean compareOptionValueByCategoryName(String category, String optionName, Object value) throws LegacyOMFException {
        Optional<Property> optOption = getEnvironmentOptionByCategoryName(category, optionName);
        if (optOption.isEmpty())
            throw new LegacyOMFException("Option: " + optionName + " not found in category: " + category, GenericException.ECriticality.ALERT);
        return optOption.get().getValue().equals(value);
    }

    /**
     * Set a new value to the given property
     *
     * @param option property to set
     * @param value  new value
     */
    protected static void setOptionValue(Property option, Object value) {
        option.setValue(value);
    }

    /**
     * Search for the environment option and will set the given value.
     * Test will fail if not found. Case and White space non-sensitive.
     * See: getEnvironmentOption for more detail
     *
     * @param category   name of the category (General, OMF, OMF ORGANIZER)
     * @param optionName name of the option
     * @param value      new value to set
     * @throws LegacyOMFException if the option is not found
     */
    public static void setEnvironmentOptionValueByCategoryName(String category, String optionName, Object value) throws LegacyOMFException {
        Optional<Property> optOption = getEnvironmentOptionByCategoryName(category, optionName);
        if (optOption.isEmpty())
            throw new LegacyOMFException("Option: " + optionName + " not found in category: " + category, GenericException.ECriticality.ALERT);

        setOptionValue(optOption.get(), value);
    }

    public static boolean compareStringsNoCaseNoSpace(String s1, String s2) {
        return StringUtils.deleteWhitespace(s1).equalsIgnoreCase(StringUtils.deleteWhitespace(s2));
    }
}
