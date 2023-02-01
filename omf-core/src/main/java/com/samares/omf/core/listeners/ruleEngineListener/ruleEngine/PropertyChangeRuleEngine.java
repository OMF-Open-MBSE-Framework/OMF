/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.listeners.ruleEngineListener.ruleEngine;

import java.util.Arrays;
import java.util.List;

public class PropertyChangeRuleEngine extends RuleEngine {

    public PropertyChangeRuleEngine(){
        super();
        List rules = Arrays.asList(
//                new Rule("ruleName", "stereotypeName")
        );
        addAllRules(rules);
    }
}
