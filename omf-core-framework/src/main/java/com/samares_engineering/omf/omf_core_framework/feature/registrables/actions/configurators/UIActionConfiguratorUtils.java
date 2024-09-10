package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators;

import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;

import java.util.*;

public class UIActionConfiguratorUtils {

    public static final String CATEGORY_SEPARATOR_REGEX = "\\.";

    private UIActionConfiguratorUtils() {
    }

    /**
     * Finds the parent category of the action, as defined by the user in the MDAction annotation.
     * Subcategories are separated by a dot in the category string; in that case, the last category is returned.
     * The category and any parent categories are created if they do not exist.
     *
     * @param actionsManager the actions manager
     * @param action         the action
     * @return the parent category of the action
     */
    public static MDActionsCategory findOrCreateCategory(ActionsManager actionsManager, UIAction action) {
        List<String> subCategoryNames = Arrays.asList(action.getCategory().split(CATEGORY_SEPARATOR_REGEX));
        if (subCategoryNames.isEmpty()) {
            LegacyErrorHandler.handleException(new LegacyOMFException("Trying to create or find a category with an empty " +
                    "name", GenericException.ECriticality.CRITICAL), true);
        }

        MDActionsCategory firstCategory = findOrCreateCategory(actionsManager, subCategoryNames.get(0));
        List<String> remainingSubCategoriesNames = subCategoryNames.subList(1, subCategoryNames.size());
        List<MDActionsCategory> subCategories = findOrCreateSubCategoriesRecursively(firstCategory, remainingSubCategoriesNames);

        return subCategories.get(subCategories.size() - 1);
    }

    /**
     * @param parentCategory     the parent category of the subcategories we want to find or create
     * @param subCategoriesNames names of the subcategories we want to find or create
     * @return The list of subcategories found or created, including the parent category which is the first element of the list.
     */
    private static List<MDActionsCategory> findOrCreateSubCategoriesRecursively(MDActionsCategory parentCategory,
                                                                                List<String> subCategoriesNames) {
        if (subCategoriesNames.isEmpty()) {
            // This list needs to be mutable so we can add grand-parent categories to it later if needed !
            return new ArrayList<>(Collections.singletonList(parentCategory));
        }
        MDActionsCategory subCategory = findOrCreateSubCategory(parentCategory, subCategoriesNames.get(0));
        List<String> remainingSubCategoriesNames = subCategoriesNames.subList(1, subCategoriesNames.size());
        List<MDActionsCategory> subCategories = findOrCreateSubCategoriesRecursively(subCategory, remainingSubCategoriesNames);
        subCategories.add(0, parentCategory);
        return subCategories;
    }

    /**
     * Find the top level category with the given name if it exists, if not it is created
     *
     * @return the category
     */
    private static MDActionsCategory findOrCreateCategory(ActionsManager actionsManager, String categoryName) {
        Optional<MDActionsCategory> optCategory = findCategory(actionsManager, categoryName);
        MDActionsCategory category = optCategory.orElseGet(() -> instantiateNewCategory(categoryName));
        if (!actionsManager.getCategories().contains(category)) {
            actionsManager.addCategory(category);
            category.setNested(true);
        }
        return category;
    }

    /**
     * Find the subcategory with the given name in the given category if it exists, if not it is created
     *
     * @return the subcategory
     */
    private static MDActionsCategory findOrCreateSubCategory(MDActionsCategory category, String subCategoryName) {
        Optional<MDActionsCategory> optCategory = findSubCategory(category, subCategoryName);
        MDActionsCategory subCategory = optCategory.orElseGet(() -> instantiateNewCategory(subCategoryName));
        if (!category.getCategories().contains(subCategory)) {
            // To nest a category we use the addAction method, I know it's weird, but categories are derived from actions
            category.addAction(subCategory);
            subCategory.setNested(true);
        }
        return subCategory;
    }

    /**
     * Find the top level category with the given name if it exists
     *
     * @param actionsManager the actions manager
     * @param categoryName   the name of the category to find (the name of the category as defined in the MDAction annotation)
     * @return an optional containing the category if it exists
     */
    public static Optional<MDActionsCategory> findCategory(ActionsManager actionsManager, String categoryName) {
        return actionsManager.getCategories().stream()
                .filter(MDActionsCategory.class::isInstance)
                .map(MDActionsCategory.class::cast)
                .filter(cat -> cat.getName().equals(categoryName)).findAny();
    }

    /**
     * Find the subcategory with the given name in the given category if it exists
     *
     * @param category        the category in which to search for the subcategory
     * @param subCategoryName the name of the subcategory to find
     * @return an optional containing the subcategory if it exists
     */
    public static Optional<MDActionsCategory> findSubCategory(MDActionsCategory category, String subCategoryName) {
        return category.getCategories().stream()
                .filter(MDActionsCategory.class::isInstance)
                .map(MDActionsCategory.class::cast)
                .filter(cat -> cat.getName().equals(subCategoryName)).findAny();
    }

    /**
     * Instantiates a new category (but does not create it in MagicDraw)
     * The category is disabled by default, and its state is updated based on the state of its actions.
     *
     * @param categoryName the name of the category (as defined in the MDAction annotation)
     * @return the new category
     */
    public static MDActionsCategory instantiateNewCategory(String categoryName) {
        return new MDActionsCategory("FeatureCategoryID-"
                + categoryName, categoryName) {
            @Override
            public void updateState() {
                boolean shallBeEnabled = getActions().stream().anyMatch(NMAction::isEnabled);
                if (isEnabled() && shallBeEnabled) setEnabled(false); //force refresh when value
                this.setEnabled(shallBeEnabled);
            }
        };
    }
}
