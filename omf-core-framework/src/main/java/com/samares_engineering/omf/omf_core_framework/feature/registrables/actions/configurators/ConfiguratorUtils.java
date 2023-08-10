package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators;

import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFFrameworkException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;

import java.util.*;

public class ConfiguratorUtils {
    private ConfiguratorUtils() {}

    public static MDActionsCategory findOrCreateCategory(ActionsManager actionsManager, UIAction action) {
        List<String> subCategoryNames = Arrays.asList(action.getCategory().split("\\."));
        if (subCategoryNames.isEmpty()) {
            OMFErrorHandler.handleException(new OMFFrameworkException("Trying to create or find a category with an empty " +
                    "name", GenericException.ECriticality.CRITICAL), true);
        }

        MDActionsCategory firstCategory = findOrCreateCategory(actionsManager, subCategoryNames.get(0));
        List<String> remainingSubCategoriesNames = subCategoryNames.subList(1, subCategoryNames.size());
        List<MDActionsCategory> subCategories = findOrCreateSubCategoriesRecursively(firstCategory, remainingSubCategoriesNames);

        return subCategories.get(subCategories.size() - 1);
    }

    /**
     * @param parentCategory the parent category of the subcategories we want to find or create
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

    private static MDActionsCategory findOrCreateCategory(ActionsManager actionsManager, String categoryName) {
        Optional<MDActionsCategory> optCategory = findCategory(actionsManager, categoryName);
        MDActionsCategory category = optCategory.orElseGet(() -> createNewCategory(categoryName));
        if(!actionsManager.getCategories().contains(category) ) {
            actionsManager.addCategory(category);
            category.setNested(true);
        }
        return category;
    }

    private static MDActionsCategory findOrCreateSubCategory(MDActionsCategory category, String subCategoryName) {
        Optional<MDActionsCategory> optCategory = findSubCategory(category, subCategoryName);
        MDActionsCategory subCategory = optCategory.orElseGet(() -> createNewCategory(subCategoryName));
        if(!category.getCategories().contains(subCategory) ) {
            // To nest a category we use the addAction method, I know it's weird, but it works
            category.addAction(subCategory);
            subCategory.setNested(true);
        }
        return subCategory;
    }

    static Optional<MDActionsCategory> findCategory(ActionsManager actionsManager, String categoryName) {
        return actionsManager.getCategories().stream()
                .filter(MDActionsCategory.class::isInstance)
                .map(MDActionsCategory.class::cast)
                .filter(cat -> cat.getName().equals(categoryName)).findAny();
    }

    static Optional<MDActionsCategory> findSubCategory(MDActionsCategory category, String subCategoryName) {
        return category.getCategories().stream()
                .filter(MDActionsCategory.class::isInstance)
                .map(MDActionsCategory.class::cast)
                .filter(cat -> cat.getName().equals(subCategoryName)).findAny();
    }

    private static MDActionsCategory createNewCategory(String categoryName) {
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
