/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature.ruleengine;

import com.nomagic.magicdraw.utils.PriorityProvider;
import com.samares.omf.core.listeners.ruleEngineListener.ruleEngine.IRuleEngine;

public interface IFeatureRuleEngine extends IRuleEngine, PriorityProvider {

    void setPriority(int priority);

    String getCategory();
    void setCategory(String category);
}
