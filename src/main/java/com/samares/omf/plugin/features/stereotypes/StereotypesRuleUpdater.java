/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.plugin.features.stereotypes;


import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares.omf.core.feature.ruleengine.IFeatureRuleEngine;
import com.samares.omf.core.utils.utils.CSVParseUtils;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.core.errors.OMFLogLevel;
import com.samares.omf.core.errors.OMFLogger;
import com.samares.omf.core.errors.exceptions.GenericException;
import com.samares.omf.core.errors.exceptions.OMFException;
import com.samares.omf.plugin.features.stereotypes.rules.type2instance.InstanceCBACreatedRule;
import com.samares.omf.plugin.features.stereotypes.utils.String2Class;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;
import com.samares.omf.plugin.features.stereotypes.rules.instance2type.ActivityToCreateRule;
import com.samares.omf.plugin.features.stereotypes.rules.instance2type.ClassToCreateRule;
import com.samares.omf.plugin.features.stereotypes.rules.type2instance.InstancePropertyCreatedRule;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

/**
 * Updates rules based on configuration in CSV files
 */
public class StereotypesRuleUpdater {
    private IFeatureRuleEngine organizerEngine;
    private static StereotypesRuleUpdater instance = null;

    private StereotypesRuleUpdater() {}

    public static StereotypesRuleUpdater getInstance() {
        if (null == instance)
            instance = new StereotypesRuleUpdater();
        return instance;
    }

    public void updateAllRulesBasedOnConfigFiles(){
        organizerEngine.removeAllRules();
        createType2InstanceRules((OMFPluginEnvOptionsGroup.getInstance().getT2IConfigFilePath()));
        createInstance2TypeRules(OMFPluginEnvOptionsGroup.getInstance().getI2TConfigFilePath());
        createOrganizerRules(OMFPluginEnvOptionsGroup.getInstance().getOrganizerConfigFilePath());
    }

    /*
     * We need a separate init method from the update method as when the rules are created, environment options have
     * not been created yet, so we fetch the corresponding default values directly.
     */
    public void initAllRulesBasedOnConfigFiles(){
        organizerEngine.removeAllRules();
        createType2InstanceRules((OMFPluginEnvOptionsGroup.getT2IPathListenerConfigurationDefaultValue()));
        createInstance2TypeRules(OMFPluginEnvOptionsGroup.getI2TPathListenerConfigurationDefaultValue());
        createOrganizerRules(OMFPluginEnvOptionsGroup.getOrganizerPathListenerConfigurationDefaultValue());
    }



    private void createType2InstanceRules(String configFilePath) {
        List<List<String>> linesToParse = getLinesToParseFromConfigFile(configFilePath, ';');
        for (List<String> line : linesToParse) {
            String typeListener = line.get(0);
            String typeStereotype = line.get(1);
            String instanceStereotype = line.get(2);
            String instanceOwner = line.get(3);
            String trigger = line.get(4);
            String ruleId = line.get(5);
            String activated = line.get(6);

            if (Finder.byNameRecursively().find(OMFUtils.currentProject, Stereotype.class, instanceStereotype) == null) {
                OMFErrorHandler.handleException(new OMFException("[InstanceCreator] While parsing file configuration." +
                        "\n instanceStereotype: \"" + instanceStereotype + "\" unknown", OMFException.ECriticality.ALERT), false);
                continue;
            }
            if (Finder.byNameRecursively().find(OMFUtils.currentProject, Stereotype.class, typeStereotype) == null) {
                OMFErrorHandler.handleException(new OMFException("[InstanceCreator] While parsing file configuration." +
                        "\n typeStereotype: \"" + typeStereotype + "\" unknown", OMFException.ECriticality.ALERT), false);
                continue;
            }

            switch (typeListener) {
                case "Class2Property":
                    organizerEngine.addRule(
                            new InstancePropertyCreatedRule(ruleId, Class.class, typeStereotype,
                                    Property.class, instanceStereotype, instanceOwner)
                    );
                    break;
                case "Activity2CallBehavior":
                    organizerEngine.addRule(
                            new InstanceCBACreatedRule(ruleId, Activity.class, typeStereotype,
                                    CallBehaviorAction.class, instanceStereotype, instanceOwner)
                    );
                    break;
                default:
                    System.err.println("[DEV] NO ruleEngine correspond to this ruleEngineType: " + typeListener);
                    OMFErrorHandler.handleException(new OMFException("[InstanceCreator] While parsing file configuration." +
                            "\n typeListener: \"" + typeListener + "\" unknown", GenericException.ECriticality.ALERT), false);
                    break;
            }
        }
    }

    private void createInstance2TypeRules(String configFilePath) {
        List<List<String>> linesToParse = getLinesToParseFromConfigFile(configFilePath, ';');
        for (List<String> line : linesToParse) {
            String typeListener = line.get(0);
            String instance = line.get(1);
            String definition = line.get(2);
            String instanceOwner = line.get(3);
            String ownerToStoreType = line.get(4);
            String trigger = line.get(5);
            String id = line.get(6);
            String activated = line.get(7);

            if (isStereotypeExistingByName(instance)) {
                OMFErrorHandler.handleException(new OMFException("[TypeCreator] While parsing file configuration." +
                        "\n instance: \"" + instance + "\" unknown", OMFException.ECriticality.ALERT), false);
                continue;
            }
            if (isStereotypeExistingByName(definition)) {
                OMFErrorHandler.handleException(new OMFException("[TypeCreator] While parsing file configuration." +
                        "\n definition: \"" + definition + "\" unknown", OMFException.ECriticality.ALERT), false);
                continue;
            }

            switch (typeListener) {
                case "Property2Class":
                    organizerEngine.addRule(new ClassToCreateRule(id, instance, definition, null));
                    break;
                case "CallBehavior2Activity":
                    organizerEngine.addRule(new ActivityToCreateRule(id, instance, definition, null));
                    break;
                default:
                    System.err.println("[DEV] NO ruleEngine correspond to this ruleEngineType: " + typeListener);
                    OMFErrorHandler.handleException(new OMFException("[TypeCreator] While parsing file configuration." +
                            "\n typeListener: \"" + typeListener + "\" unknown", OMFException.ECriticality.ALERT), false);
                    break;
            }
        }
    }

    private void createOrganizerRules(String configFilePath) {
        List<List<String>> linesToParse = getLinesToParseFromConfigFile(configFilePath, ';');
        for (List<String> line : linesToParse) {
            String createdElementStereotype = line.get(0);
            String classOfElement = line.get(1);
            String viewpointPackage_StereotypeName = line.get(2);
            String viewpointPackage_AMT_id = line.get(3);
            List<String> ownersPossiblesList = OMFUtils.getValuesWithinLine(line.get(4), "/");
            String futurStorageStereotype = line.get(5);
            String futurStorageAMT_id = line.get(6);
            String classOfAMT_id = line.get(7);

            if (isStereotypeExistingByName(createdElementStereotype)) {
                OMFErrorHandler.handleException(new OMFException("[Organizer] While parsing file configuration." +
                        "\n CreatedElementStereotype: \"" + createdElementStereotype + "\" unknown", OMFException.ECriticality.ALERT), false);
                continue;
            }

            java.lang.Class classElementCreated = null;
            try {
                classElementCreated = String2Class.valueOf(classOfElement.toUpperCase()).getClassValue();
            } catch (Exception e) {
                OMFErrorHandler.handleException(new OMFException("[Organizer] While parsing file configuration." +
                        "\n classOfElement: \"" + classOfElement + "\" unknown", OMFException.ECriticality.ALERT), false);
                continue;
            }

            java.lang.Class storageClass = null;
            try {
                storageClass = String2Class.valueOf(classOfAMT_id.toUpperCase()).getClassValue();
            } catch (Exception e) {
                OMFErrorHandler.handleException(new OMFException("[Organizer] While parsing file configuration." +
                        "\n classOfAMT_id: \"" + classOfAMT_id + "\" unknown", OMFException.ECriticality.ALERT), false);
                continue;
            }

            //TODO ADD THE NEW RULE
//            engine_organizer.addRule(new Organizer_Rule(id, createdElementStereotype, classElementCreated,
//                    futurStorageAMT_id, storageClass,
//                    futurStorageStereotype, viewpointPackage_StereotypeName, viewpointPackage_AMT_id, ownersPossiblesList));
        }
    }

    private List<List<String>> getLinesToParseFromConfigFile(String csvConfigFilePath, char delimiter) {
        List<List<String>> lines = Collections.emptyList();
        try {
            lines = CSVParseUtils.getParsedLines(csvConfigFilePath, delimiter);
        } catch (FileNotFoundException e) {
            OMFErrorHandler.handleException(
                    new OMFException("Can't find .csv config file " + csvConfigFilePath + ", make sure the path defined in " +
                            "environment options is correct", e, GenericException.ECriticality.CRITICAL),
                    false
            );
        } catch (OMFException e) {
            OMFErrorHandler.handleException(e, false);
        }
        if (!lines.isEmpty()) {
            // Skip first line which contains header info
            lines.remove(0);
        }
        if (lines.isEmpty()) {
            OMFLogger.getInstance().log("No info parsed from config file " + csvConfigFilePath + " ",
                    null, OMFLogLevel.WARNING);
        } else {
            OMFLogger.getInstance().log(lines.size() + " rules parsed from config file " + csvConfigFilePath + " ",
                    null, OMFLogLevel.INFO);
        }
        return lines;
    }

    private boolean isStereotypeExistingByName(String instance) {
        return null == Finder.byNameRecursively().find(OMFUtils.currentProject, Stereotype.class, instance);
    }

    public void setOrganizerRuleEngine(IFeatureRuleEngine organizerEngine) {
        this.organizerEngine = organizerEngine;
    }
}
