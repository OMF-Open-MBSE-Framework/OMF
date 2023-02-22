/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature.registrables.rule_engines.rule_engine;


import com.nomagic.magicdraw.utils.PriorityProvider;
import com.samares.omf.core.feature.RegistrableFeatureItem;
import com.samares.omf.core.feature.registrables.rule_engines.rule.IRule;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Optional;

public interface IRuleEngine extends PriorityProvider, RegistrableFeatureItem {
    Optional<IRule> getMatchingRule(PropertyChangeEvent evt);

    List<IRule> getAllMatchingRules(PropertyChangeEvent evt);

    boolean processFirstMatchingRule(PropertyChangeEvent evt);

    boolean processAllMatchingRule(PropertyChangeEvent evt);

    boolean skipRules(PropertyChangeEvent evt);

    void addRule(IRule rule);

    void addAllRules(List<IRule> lRules);

    List<IRule> getRules();

    void removeRule(IRule rule);

    void removeRules(List<IRule> lRules);

    void removeAllRules();

    void setPriority(int priority);

    String getCategory();
    void setCategory(String category);
}
