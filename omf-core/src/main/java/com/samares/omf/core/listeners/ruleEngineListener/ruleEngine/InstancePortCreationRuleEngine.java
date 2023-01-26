 /*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.listeners.ruleEngineListener.ruleEngine;

 import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
 import com.samares.omf.core.listeners.ruleEngineListener.rules.onCreation.DiagramPortCreated_Rule;

 import java.beans.PropertyChangeEvent;
 import java.util.Arrays;
 import java.util.List;

 public class InstancePortCreationRuleEngine extends RuleEngine {

     public InstancePortCreationRuleEngine(){
         super();
         List l_r = Arrays.asList(
                 new DiagramPortCreated_Rule("onPortCreationOnDiagram",   null, null)
         );
         addAllRules(l_r);
     }

     @Override
     public boolean skipRules(PropertyChangeEvent evt) {
         return ((Element) evt.getSource()).getOwner() == null;
     }
 }
