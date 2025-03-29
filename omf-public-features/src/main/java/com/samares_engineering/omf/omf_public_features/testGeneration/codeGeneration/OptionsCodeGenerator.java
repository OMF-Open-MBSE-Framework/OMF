package com.samares_engineering.omf.omf_public_features.testGeneration.codeGeneration;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.options.AbstractPropertyOptionsGroup;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.ElementProperty;
import com.nomagic.magicdraw.properties.Property;
import com.nomagic.magicdraw.properties.StringProperty;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_public_features.testGeneration.utils.OptionsBaseline;
import com.samares_engineering.omf.omf_public_features.testGeneration.utils.OptionsUtils;
import com.squareup.javapoet.MethodSpec;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class OptionsCodeGenerator {

    private String commentSnippet;

    public OptionsCodeGenerator(String commentSnippet) {
        this.commentSnippet = commentSnippet;
    }

    /**
     * Generate code which create for each group's property a SetupOptionsBaseline, and add it to a provided method builder.
     * @param methodBuilder : method builder to which we want to add the code
     * @param groupsIds : groups of which we want to save the properties
     *
     * Example generated code :
     *   Map.of(
     *     	 "groupOptionID1", Arrays.asList(
     *     		 new SetupOptionsBaseline(Boolean.class, "BooleanField", true),
     *     		 new SetupOptionsBaseline(Element.class, "ElementField", findElementByID("_11_5EAPbeta_be00301_1147424179914_458922_958")) // http://localhost:9850/refmodel/?ID=_11_5EAPbeta_be00301_1147424179914_458922_958
     *     	 ),
     *     	 "groupOptionID2", Arrays.asList(
     *           new SetupOptionsBaseline(String.class, "StringField", "C:\\Users\\Calliope\\IdeaProjects\\samaresmbseframework\\omf-example-plugin\\build\\install/plugins/")
     *       )
     *     );
     */
    public void generateAllOptionsProperties(MethodSpec.Builder methodBuilder, List<String> groupsIds) {
        methodBuilder.addCode("$T.of(", Map.class);

        if (groupsIds.size() > 0) {
            for (int i = 0; i < groupsIds.size() - 1; i++) {
                String groupId = groupsIds.get(i);
                List<Property> properties = OptionsUtils.getEnvOptionProperties(groupId);

                if (properties == null)
                    continue;

                generateOneOptionGroupProperties(methodBuilder, groupId, properties);
                methodBuilder.addCode(",");
            }
            // Last Line shall not contains ','
            String groupId = groupsIds.get(groupsIds.size() - 1);
            generateOneOptionGroupProperties(methodBuilder, groupId, OptionsUtils.getEnvOptionProperties(groupId));
        }

        methodBuilder.addCode("\n);\n");
    }


    private void generateOneOptionGroupProperties(MethodSpec.Builder initEnvOptionBuilder, String groupId, List<Property> properties) {
        if (properties == null) {
            return;
        }

        initEnvOptionBuilder.addCode("\n\t \"" + groupId + "\", $T.asList(", Arrays.class);

        Predicate<Property> isLastElement = property -> properties.indexOf(property) < properties.size() - 1;

        properties.stream()
                .map(property -> generateOneProperty(property, groupId, isLastElement.test(property)? ",": ""))
                .forEach(str -> initEnvOptionBuilder.addCode(str, OptionsBaseline.class));

        initEnvOptionBuilder.addCode("\n\t )");
    }


    /**
     * Generate the String code allowing to generate the code associated to the given property.
     * @param property
     * @param groupID
     * @param endWith : end of the java line (coma, colon, comment, ...)
     * @return
     *
     * Examples: 1) new SetupOptionsBaseline(Boolean.class, "BooleanField", true),
     *           2) new SetupOptionsBaseline(String.class, "StringField", "C:\\Users\\Calliope\\IdeaProjects\\samaresmbseframework\\omf-example-plugin\build\install/plugins/")
     *     		 3) new SetupOptionsBaseline(Element.class, "ElementField", findElementByID("_11_5EAPbeta_be00301_1147424179914_458922_958")) // http://localhost:9850/refmodel/?ID=_11_5EAPbeta_be00301_1147424179914_458922_958
     */
    private String generateOneProperty(Property property, String groupID, String endWith) {
        Class createdValueClass = property.getValue().getClass();
        String createPropValue;
        String comment = "";

        if (property instanceof StringProperty) {
            createPropValue = (String) property.getValue();
            createPropValue = createPropValue.replaceAll("\\\\", "\\\\\\\\"); // escape the char '\' (first escape twice for regex)
            createPropValue = "\"" + createPropValue + "\"";
        }
        else if (property instanceof BooleanProperty) {
            createPropValue = (Boolean) property.getValue()? "true": "false";
            //""+property.getValue()
        }
        else if (property instanceof ElementProperty) {
            String elementValueId = ((Element) ((AbstractPropertyOptionsGroup) Application.getInstance().getEnvironmentOptions()
                    .getGroup(groupID))
                    .getProperty(property.getID())
                    .getValue())
                    .getID();

            createdValueClass = Element.class;
            createPropValue = "findElementByID(\"" + elementValueId + "\")";
            comment = this.commentSnippet + elementValueId;
        }

        else {
            String propTypeName = property.toString().split("@")[0];
            createPropValue = "\"//TODO " + propTypeName + " value\"";
            OMFLogger.warnToUIConsole(" type is not yet support for the test generation. " +
                            "You should manually define their value on the test (\"TODO\" value)");
        }

        return "\n\t\t new $T(" + createdValueClass.getSimpleName() +".class, \""
                + property.getID() + "\", "
                + createPropValue + ")"
                + endWith + comment; // TODO : createdValueClass may be created as a $T ?

    }
}
