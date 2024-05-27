/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.ILiveAction;

public interface IRule<I, O> {
    boolean matches(I e);

    /**
     * Process the event.
     * @param e the event to process
     * @return the event after processing
     */
    O process(I e);

    boolean isActivated();

    void setActivated(boolean activated);

    String getId();

    boolean isBlocking();

    void setRuleEngine(ILiveAction ruleEngine);

    ILiveAction getRuleEngine();
}
