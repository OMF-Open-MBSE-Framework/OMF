/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature;

import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.feature.options.IOption;
import com.samares.omf.core.feature.ruleengine.IFeatureRuleEngine;

import java.util.List;

public interface MDFeature {
    String getName();

    void activate();
    void deactivate();
    boolean isActivated();
    void onActivation();
    void onDeactivation();
    void onProjectOpen();
    void onProjectClose();

    List<AGenericAction> getMDActions();
    List<IFeatureRuleEngine> getLiveActions();

    List<IOption> getOptions();
}
