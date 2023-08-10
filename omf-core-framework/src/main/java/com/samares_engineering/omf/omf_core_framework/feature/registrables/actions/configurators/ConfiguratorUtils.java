package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators;

import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;

import java.util.Optional;

public class ConfiguratorUtils {
    private ConfiguratorUtils() {}


    public static MDActionsCategory findOrCreateCategory(ActionsManager actionsManager, UIAction action) {
        String categoryName = action.getCategory();

        Optional<MDActionsCategory> optCategory = findCategory(actionsManager, action);

        return optCategory.orElseGet(() -> new MDActionsCategory("FeatureCategoryID-" + categoryName, categoryName) {
            @Override
            public void updateState() {
                boolean shallBeEnabled = getActions().stream().anyMatch(NMAction::isEnabled);
                if (isEnabled() && shallBeEnabled) setEnabled(false); //force refresh when value
                this.setEnabled(shallBeEnabled);
            }
        });

    }

    static Optional<MDActionsCategory> findCategory(ActionsManager actionsManager, UIAction action) {
        String categoryName = action.getCategory();
        return actionsManager.getCategories().stream()
                .filter(MDActionsCategory.class::isInstance)
                .map(MDActionsCategory.class::cast)
                .filter(cat -> cat.getName().equals(categoryName)).findAny();
    }
}
