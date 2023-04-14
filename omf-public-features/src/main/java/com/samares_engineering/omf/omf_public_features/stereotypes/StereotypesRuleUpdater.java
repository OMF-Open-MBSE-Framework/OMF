/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_public_features.stereotypes;


import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_core_framework.utils.utils.CSVParseUtils;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFException;
import com.samares_engineering.omf.omf_public_features.stereotypes.rules.instance.InstanceCallBehaviorCreatedRule;
import com.samares_engineering.omf.omf_public_features.stereotypes.rules.instance.InstancePropertyCreatedRule;
import com.samares_engineering.omf.omf_public_features.stereotypes.rules.type.ActivityToCreateRule;
import com.samares_engineering.omf.omf_public_features.stereotypes.rules.type.ClassToCreateRule;
import com.samares_engineering.omf.omf_public_features.stereotypes.utils.String2Class;


import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

/**
 * Updates rules based on configuration in CSV files
 */
public class StereotypesRuleUpdater {
    private final StereotypesFeature feature;
    private IRuleEngine organizerEngine;

    public StereotypesRuleUpdater(StereotypesFeature feature) {
        this.feature = feature;
    }

    public void updateAllRulesBasedOnConfigFiles(){
        organizerEngine.removeAllRules();
        createInstanceRules((feature.getOptionsHelper().getInstanceConfigFilePath()));
        //TODO
        //StereotypesHelper.getAllStereotypes(OMFUtils.currentProject).stream().map(Stereotype::getName).collect(Collectors.toList())
        //createTypeRules(feature.getOptionsHelper().getTypeConfigFilePath()); //TODO : parameter throw NullPointerException
        createOrganizerRules(feature.getOptionsHelper().getOrganizerConfigFilePath());
    }

    /*
     * We need a separate init method from the update method as when the rules are created, environment options have
     * not been created yet, so we fetch the corresponding default values directly.
     */
    public void initAllRulesBasedOnConfigFiles(){
        organizerEngine.removeAllRules();
        createInstanceRules(StereotypesEnvOptionsHelper.getInstanceConfigFilePathDefaultValue());
        createTypeRules(StereotypesEnvOptionsHelper.getTypeConfigFilePathDefaultValue());
        createOrganizerRules(StereotypesEnvOptionsHelper.getOrganizerConfigFilePathDefaultValue());
    }

    private void createInstanceRules(String configFilePath) {
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
                            new InstanceCallBehaviorCreatedRule(ruleId, Activity.class, typeStereotype,
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

    private void createTypeRules(String configFilePath) {
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
            String viewpointPackageStereotypeName = line.get(2);
            String viewpointPackageAMTid = line.get(3);
            List<String> ownersPossiblesList = OMFUtils.getValuesWithinLine(line.get(4), "/");
            String futureStorageStereotype = line.get(5);
            String futureStorageAMTid = line.get(6);
            String classOfAMTid = line.get(7);

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
                storageClass = String2Class.valueOf(classOfAMTid.toUpperCase()).getClassValue();
            } catch (Exception e) {
                OMFErrorHandler.handleException(new OMFException("[Organizer] While parsing file configuration." +
                        "\n classOfAMT_id: \"" + classOfAMTid + "\" unknown", OMFException.ECriticality.ALERT), false);
                continue;
            }

            //TODO ADD THE NEW RULE
//            organizerEngine.addRule(new OrganizerRule(id, createdElementStereotype, classElementCreated,
//                    futurStorageAMTid, storageClass,
//                    futurStorageStereotype, viewpointPackageStereotypeName, viewpointPackageAMTid, ownersPossiblesList));
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

    public void setOrganizerRuleEngine(IRuleEngine organizerEngine) {
        this.organizerEngine = organizerEngine;
    }
}
