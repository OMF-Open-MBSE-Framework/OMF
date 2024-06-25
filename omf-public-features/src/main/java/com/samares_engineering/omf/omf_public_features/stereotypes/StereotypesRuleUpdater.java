/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_public_features.stereotypes;


import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFLogException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.utils.CSVParseUtils;
import com.samares_engineering.omf.omf_public_features.stereotypes.exceptions.CSVNotFoundException;
import com.samares_engineering.omf.omf_public_features.stereotypes.liveactions.instance.InstanceCallBehaviorCreatedLiveAction;
import com.samares_engineering.omf.omf_public_features.stereotypes.liveactions.instance.InstancePropertyCreatedLiveAction;
import com.samares_engineering.omf.omf_public_features.stereotypes.liveactions.type.ActivityToCreateLiveAction;
import com.samares_engineering.omf.omf_public_features.stereotypes.liveactions.type.ClassToCreateLiveAction;
import com.samares_engineering.omf.omf_public_features.stereotypes.utils.String2Class;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

/**
 * Updates rules based on configuration in CSV files
 */
public class StereotypesRuleUpdater {
    private final StereotypesFeature feature;
    private LiveActionEngine organizerEngine;
    private char delimiter;

    public StereotypesRuleUpdater(StereotypesFeature feature) {
        this(feature, ';');
    }

    public StereotypesRuleUpdater(StereotypesFeature feature, char delimiter) {
        this.feature = feature;
        this.delimiter = delimiter;
    }

    public void updateAllRulesBasedOnConfigFiles() {
        organizerEngine.removeAllLiveActions();
        createInstanceRules((feature.getOptionsHelper().getInstanceConfigFilePath()));
        createTypeRules(feature.getOptionsHelper().getTypeConfigFilePath());
        createOrganizerRules(feature.getOptionsHelper().getOrganizerConfigFilePath());
    }

    /*
     * We need a separate init method from the update method as when the rules are created, environment options have
     * not been created yet, so we fetch the corresponding default values directly.
     */
    public void initAllRulesBasedOnConfigFiles() {
        try {
            organizerEngine.removeAllLiveActions();
            createInstanceRules(StereotypesEnvOptionsHelper.getInstanceConfigFilePathDefaultValue());
            createTypeRules(StereotypesEnvOptionsHelper.getTypeConfigFilePathDefaultValue());
            createOrganizerRules(StereotypesEnvOptionsHelper.getOrganizerConfigFilePathDefaultValue());
        } catch (OMFLogException warningException) {
            OMFErrorHandler.getInstance().handleException(
                    new OMFCriticalException(warningException.getMessage()
                            + " The feature will be deactivated.",
                            warningException, OMFExceptionModifier.DEACTIVATE_FEATURE
                    ));
        }
    }

    private void createInstanceRules(String configFilePath) {
        List<List<String>> linesToParse = getLinesToParseFromConfigFile(configFilePath, delimiter);
        for (List<String> line : linesToParse) {
            if (line.size() < 7) {
                continue;
            }


            String typeListener = line.get(0);
            String typeStereotype = line.get(1);
            String instanceStereotype = line.get(2);
            String instanceOwner = line.get(3);
            String trigger = line.get(4);
            String ruleId = line.get(5);
            String activated = line.get(6);

            if (Finder.byNameRecursively().find(OMFUtils.getProject(), Stereotype.class, instanceStereotype) == null) {
                OMFLogger.logToUIConsole("[InstanceCreator] While parsing file configuration." +
                        "\n instanceStereotype: \"" + instanceStereotype + "\" unknown", OMFLogLevel.WARNING, feature);
                continue;
            }
            if (Finder.byNameRecursively().find(OMFUtils.getProject(), Stereotype.class, typeStereotype) == null) {
                OMFLogger.logToUIConsole("[InstanceCreator] While parsing file configuration." +
                        "\n typeStereotype: \"" + typeStereotype + "\" unknown", OMFLogLevel.WARNING, feature);
                continue;
            }

            switch (typeListener) {
                case "Class2Property":
                    organizerEngine.addLiveAction(
                            new InstancePropertyCreatedLiveAction(ruleId, Class.class, typeStereotype,
                                    Property.class, instanceStereotype, instanceOwner)
                    );
                    break;
                case "Activity2CallBehavior":
                    organizerEngine.addLiveAction(
                            new InstanceCallBehaviorCreatedLiveAction(ruleId, Activity.class, typeStereotype,
                                    CallBehaviorAction.class, instanceStereotype, instanceOwner)
                    );
                    break;
                default:
                    OMFLogger.logToUIConsole("[InstanceCreator] While parsing file configuration." +
                            "\n typeListener: \"" + typeListener + "\" unknown", OMFLogLevel.WARNING, feature);
                    break;
            }
        }
    }

    private void createTypeRules(String configFilePath) {
        List<List<String>> linesToParse = getLinesToParseFromConfigFile(configFilePath, delimiter);
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
                OMFLogger.logToUIConsole("While parsing file configuration." +
                        "\n instance: \"" + instance + "\" unknown", OMFLogLevel.WARNING, feature);
                continue;
            }
            if (isStereotypeExistingByName(definition)) {
                OMFLogger.logToUIConsole("[TypeCreator] While parsing file configuration." +
                        "\n definition: \"" + definition + "\" unknown", OMFLogLevel.WARNING, feature);

                continue;
            }

            switch (typeListener) {
                case "Property2Class":
                    organizerEngine.addLiveAction(new ClassToCreateLiveAction(id, instance, definition, null));
                    break;
                case "CallBehavior2Activity":
                    organizerEngine.addLiveAction(new ActivityToCreateLiveAction(id, instance, definition, null));
                    break;
                default:
                    OMFLogger.logToUIConsole("[TypeCreator] While parsing file configuration." +
                            "\n typeListener: \"" + typeListener + "\" unknown", OMFLogLevel.WARNING, feature);
                    break;
            }
        }
    }

    private void createOrganizerRules(String configFilePath) {
        List<List<String>> linesToParse = getLinesToParseFromConfigFile(configFilePath, delimiter);
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
                OMFLogger.logToUIConsole("[Organizer] While parsing file configuration." +
                        "\n CreatedElementStereotype: \"" + createdElementStereotype + "\" unknown", OMFLogLevel.WARNING, feature);
                continue;
            }

            java.lang.Class classElementCreated = null;
            try {
                classElementCreated = String2Class.valueOf(classOfElement.toUpperCase()).getClassValue();
            } catch (Exception e) {
                OMFLogger.logToUIConsole("[Organizer] While parsing file configuration." +
                        "\n classOfElement: \"" + classOfElement + "\" unknown", OMFLogLevel.WARNING, feature);
                continue;
            }

            java.lang.Class storageClass = null;
            try {
                storageClass = String2Class.valueOf(classOfAMTid.toUpperCase()).getClassValue();
            } catch (Exception e) {
                OMFLogger.logToUIConsole("[Organizer] While parsing file configuration." +
                        "\n classOfAMT_id: \"" + classOfAMTid + "\" unknown", OMFLogLevel.WARNING, feature);
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
            throw new CSVNotFoundException(csvConfigFilePath, e);
        } catch (LegacyOMFException e) {
            throw new OMFCriticalException("Error while loading csv config file " + csvConfigFilePath, e);
        }
        if (!lines.isEmpty()) {
            // Skip first line which contains header info
            lines.remove(0);
        }
        if (lines.isEmpty()) {
            OMFLogger.logToUIConsole("No info parsed from config file " + csvConfigFilePath + " ",
                    OMFLogLevel.WARNING, feature);
        } else {
            OMFLogger.logToUIConsole(lines.size() + " rules parsed from config file " + csvConfigFilePath + " ", OMFLogLevel.INFO, feature);
        }
        return lines;
    }

    private boolean isStereotypeExistingByName(String instance) {
        return null == Finder.byNameRecursively().find(OMFUtils.getProject(), Stereotype.class, instance);
    }

    public void setOrganizerRuleEngine(LiveActionEngine organizerEngine) {
        this.organizerEngine = organizerEngine;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }
}
