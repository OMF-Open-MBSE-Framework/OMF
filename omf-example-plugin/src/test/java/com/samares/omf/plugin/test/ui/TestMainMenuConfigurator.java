/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test.ui;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.samares.omf.core.feature.registrables.actions.actions.configurators.OMFMainMenuConfigurator;

public class TestMainMenuConfigurator extends OMFMainMenuConfigurator {
    private static final String menuName = "OMF TEST";

    private final NMAction testAction = new TestAction();

    @Override
    public void configure(ActionsManager actionsManager) {
        ActionsCategory category = (ActionsCategory) actionsManager.getActionFor(menuName);

        if (category == null) {
            // creating new category
            category = new MDActionsCategory(menuName, menuName);
            category.setNested(true);
            category.addAction(testAction);

            actionsManager.addCategory(category);
        }
    }
}
