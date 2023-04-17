package com.samares_engineering.omf.omf_public_features.testGeneration.codeGeneration.classGenerator;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.testGeneration.codeGeneration.OptionsCodeGenerator;
import com.samares_engineering.omf.omf_public_features.testGeneration.utils.OptionsBaseline;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Assert;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Map;

public class SnapshotGenerator {

    // Server adress
    private String COMMENT_SNIPPET;// Server adress

    // METHODS NAMES
    private String SNAPSHOT = "snapshot";
    private String FIND_ELEMENT_BY_ID = "findElementByID";

    private String CLASS_NAME = "ReInitOptionsBaseline";

    // GroupIDs to be saved
    private List<String> envOptionGroupId;
    private List<String> projectOptionGroupId;

    public SnapshotGenerator(List<String> envOptionGroupId, List<String> projectOptionGroupId, String serverAdress) {
        this.envOptionGroupId = envOptionGroupId;
        this.projectOptionGroupId = projectOptionGroupId;
        this.COMMENT_SNIPPET = " // " + serverAdress + "?ID=";
    }

    public TypeSpec generateTest() {
        // Method builder
        MethodSpec snapshot = generateSnapshot();
        MethodSpec findElementByID = generateFindElementByID();

        // Class builder
        TypeSpec reInitOptionsBaseline = TypeSpec.classBuilder(CLASS_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(snapshot)
                .addMethod(findElementByID)
                .build();

        return reInitOptionsBaseline;
    }

    private MethodSpec generateSnapshot() {
        // parameterizedMap -> Type Map<String, List<SetupOptionsBaseline>>
        ParameterizedTypeName parametrizedList = ParameterizedTypeName.get(List.class, OptionsBaseline.class);
        ClassName map = ClassName.get(Map.class);
        ClassName string = ClassName.get(String.class);
        ParameterizedTypeName parameterizedMap = ParameterizedTypeName.get(map, string, parametrizedList);


        MethodSpec.Builder snapshotBuilder = MethodSpec.methodBuilder(SNAPSHOT)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(parameterizedMap);

        snapshotBuilder.addCode("return ");

        OptionsCodeGenerator optionCodeGenerator = new OptionsCodeGenerator(COMMENT_SNIPPET);
        List<String> groupsIds = envOptionGroupId;
        optionCodeGenerator.generateAllOptionsProperties(snapshotBuilder, groupsIds);

        return snapshotBuilder.build();
    }

    private MethodSpec generateFindElementByID() {
        MethodSpec findElementByID = MethodSpec.methodBuilder(FIND_ELEMENT_BY_ID)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .returns(Element.class)
                .addParameter(String.class, "id")
                .addStatement("$T foundElement = ($T) $T.currentProject.getElementByID(id)", NamedElement.class, NamedElement.class, OMFUtils.class)
                .addStatement("$T.assertNotNull(\" No element found with ID \" + id, foundElement)", Assert.class)
                .addStatement("return foundElement")
                .build();

        return findElementByID;
    }
}