 /*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.listeners.ruleEngineListener.ruleEngine;


 import com.samares.omf.core.listeners.ruleEngineListener.rules.onCreation.PortCreatedFromConnectionRule;

 import java.util.Arrays;
 import java.util.List;

 /**
  * Rules that don't have any concrete effect, they just gather data from event to pass to other event. Used to analyse
  * event batch before running it through the real rule engine
  */
 public class AnalysisRuleEngine extends RuleEngine {
     public AnalysisRuleEngine(){
         super();
         List l_r = Arrays.asList(
                 new PortCreatedFromConnectionRule("onPortCreationFromConnector",   null)
         );
         addAllRules(l_r);
     }
 }
