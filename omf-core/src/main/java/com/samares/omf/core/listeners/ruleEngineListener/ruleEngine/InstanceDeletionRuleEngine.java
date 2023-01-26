/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.listeners.ruleEngineListener.ruleEngine;

import java.util.Arrays;
import java.util.List;

public class InstanceDeletionRuleEngine extends RuleEngine {

    public InstanceDeletionRuleEngine(){
        super();
        List l_r = Arrays.asList(
//                new Rule("ruleName", "stereotypeName")
        );
        addAllRules(l_r);
    }
}
