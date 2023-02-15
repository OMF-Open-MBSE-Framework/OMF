/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.feature.registrables.rule_engines.rule;

import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.IRuleEngine;

public interface IRule<I, O> {
    boolean matches(I e);

    O process(I e);

    boolean isActivated();

    void setActivated(boolean activated);

    String getId();

    boolean isBlocking();

    void setRuleEngine(IRuleEngine ruleEngine);
}
