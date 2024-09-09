package com.samares_engineering.omf.omf_public_features.testGeneration.codeGeneration.classGenerator;

import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.impl.ElementsFactory;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.utils.StringUtils;
import com.samares_engineering.omf.omf_public_features.testGeneration.codeGeneration.OptionsCodeGenerator;
import com.samares_engineering.omf.omf_public_features.testGeneration.utils.OptionsBaseline;
import com.samares_engineering.omf.omf_test_framework.templates.AbstractTestCase;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CreationTestGenerator {

    // Server adress
    private String COMMENT_SNIPPET;

    // METHODS NAMES
    private String INIT_VARIABLES = "initVariables";
    private String INIT_ENV_OPTIONS = "initOptions";
    private String TEST_ACTION = "testAction";
    private String REINIT_ENV_OPTION = "reInitEnvOptions";
    private String VERIFY_RESULTS = "verifyResults";

    // Element's package, whitch is stereotyped <<TestPackage>>
    private Package testPackage;

    // Tested element
    private NamedElement testedElement;

    // GroupIDs to be saved
    private List<String> envOptionGroupId;
    private List<String> projectOptionGroupId;


    public CreationTestGenerator(Package pack, NamedElement selectedElement, List<String> envOptionGroupId, List<String> projectOptionGroupId, String serverAdress) {
        this.testPackage = pack;
        this.testedElement = selectedElement;
        this.envOptionGroupId = envOptionGroupId;
        this.projectOptionGroupId = projectOptionGroupId;
        this.COMMENT_SNIPPET = " // " + serverAdress + "?ID=";
    }

    ////////// METHODS GENERATION //////////
    public TypeSpec generateTest() {
        // Method builder
        MethodSpec initVariables = generateInitVariablesMethod();
        MethodSpec initEnvOption = generateInitEnvOptions();
        MethodSpec testAction = generateTestActionMethod();
        MethodSpec reInitEnvOptions = generateReInitEnvOptionsMethod();
        MethodSpec verifyResults = generateVerifyResults();

        // Class builder
        TypeSpec testClass = TypeSpec.classBuilder(StringUtils.toCamelCase(getTestPackageName()))
                .addModifiers(Modifier.PUBLIC)
                .superclass(AbstractTestCase.class)
                .addMethod(initVariables)
                .addMethod(initEnvOption)
                .addMethod(testAction)
                .addMethod(reInitEnvOptions)
                .addMethod(verifyResults)
                .build();

        return testClass;
    }





    private MethodSpec generateVerifyResults() {
        MethodSpec verifyResults = MethodSpec.methodBuilder(VERIFY_RESULTS)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .build();
        return verifyResults;
    }




    private MethodSpec generateReInitEnvOptionsMethod() {
        MethodSpec reInitEnvOptions = MethodSpec.methodBuilder(REINIT_ENV_OPTION)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement("$T.reInitBaseline()", OptionsBaseline.class)  // TODO : gérer si existe pas
                .build();
        return reInitEnvOptions;
    }




    private MethodSpec generateTestActionMethod() {
        MethodSpec.Builder testActionBuilder = MethodSpec.methodBuilder(TEST_ACTION)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class);

        addInitState(testActionBuilder);
        addElementCreation(testActionBuilder);
        addStereotypes(testActionBuilder);
        addElementConfiguration(testActionBuilder);

        MethodSpec testAction = testActionBuilder.build();

        return testAction;
    }

    private void addInitState(MethodSpec.Builder methodBuilder) {
        // Get owner element
        String ownerID = this.getTestedElementOwner().getLocalID();
        String comment = COMMENT_SNIPPET + ownerID;
        methodBuilder.addCode("$T owner = findTestedElementByID(\"" + ownerID + "\");" + comment + "\n", Element.class);

        // Open the current diagram if any
        DiagramPresentationElement activeDiagram = OMFUtils.getProject().getActiveDiagram();
        if (activeDiagram != null) {
            //TODO: reuse if possible findTestedElementByID
            Diagram diagram = activeDiagram.getDiagram();
            String diagramID = diagram.getLocalID();
            comment = COMMENT_SNIPPET + diagramID;
            methodBuilder.addCode("$T diagramElement = findTestedElementByID(\"" + diagramID + "\");" + comment + "\n", Element.class)
                    .beginControlFlow("if (diagramElement != null)")
                    .addStatement("openDiagram(\"" + diagramID + "\")")
                    .endControlFlow()
            ;
        }
        methodBuilder.addCode("\n");
    }

    private void addElementCreation(MethodSpec.Builder methodBuilder) {
        ElementsFactory factory = OMFUtils.getProject().getElementsFactory();

        Optional<Method> creationMethod = Arrays.stream(factory.getClass().getMethods())
                .filter(m -> m.getReturnType().equals(this.getTestedElementClass())) // 1 creation class / metaclass, return type is determinist
                .findFirst();

        if (creationMethod.isEmpty()) {
           throw new OMFCriticalException(new OMFLog().text("Can't find a suitable creation method for the provided element").linkElement(testedElement.getName(), testedElement));
        }

        methodBuilder.addStatement("$T " + this.getTestedElementName() + " = $T.currentProject.getElementsFactory()." + creationMethod.get().getName() + "()", this.getTestedElementClass(), OMFUtils.class);
        methodBuilder.addCode("\n");
    }

    private void addStereotypes(MethodSpec.Builder methodBuilder) {
        if (!this.getTestedElementStereotypes().isEmpty()) {
            methodBuilder.addStatement("$T profile", Profile.class);
            methodBuilder.addStatement("$T stereotype", Stereotype.class);

            Map<String, String> mapProfileName = StereotypesHelper.getStereotypes(this.testedElement)
                    .stream()
                    .collect(Collectors.toMap(Stereotype::getName, str -> str.getProfile().getName()));

            for (Map.Entry<String, String> entry : mapProfileName.entrySet()) {
                methodBuilder.addStatement("profile = $T.getProfile($T.currentProject, \"" + entry.getValue() + "\")", StereotypesHelper.class, OMFUtils.class);
                methodBuilder.addStatement("stereotype = $T.getStereotype($T.currentProject, \"" + entry.getKey() + "\", profile)", StereotypesHelper.class, OMFUtils.class);
                methodBuilder.addStatement("$T.addStereotype(" + this.getTestedElementName() + ", stereotype)", StereotypesHelper.class);
            }
            methodBuilder.addCode("\n");
        }
    }

    private void addElementConfiguration(MethodSpec.Builder methodBuilder) {
        methodBuilder.addStatement(this.getTestedElementName() + ".setOwner(owner)");
        methodBuilder.addStatement(this.getTestedElementName() + ".setName(\"" + this.getTestedElementName() + "\")");
    }





    private MethodSpec generateInitEnvOptions() {
        MethodSpec.Builder initEnvOptionBuilder = MethodSpec.methodBuilder(INIT_ENV_OPTIONS)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class);

        //// Environment options
        // parameterizedMap -> Type Map<String, List<SetupOptionsBaseline>>
        ParameterizedTypeName parametrizedList = ParameterizedTypeName.get(List.class, OptionsBaseline.class);
        ClassName map = ClassName.get(Map.class);
        ClassName string = ClassName.get(String.class);
        ParameterizedTypeName parameterizedMap = ParameterizedTypeName.get(map, string, parametrizedList);

        initEnvOptionBuilder.addCode("$T initBaseline = ", parameterizedMap);

        OptionsCodeGenerator optionCodeGenerator = new OptionsCodeGenerator(COMMENT_SNIPPET);
        List<String> groupsIds = envOptionGroupId;
        optionCodeGenerator.generateAllOptionsProperties(initEnvOptionBuilder, groupsIds);

        initEnvOptionBuilder.addStatement("$T.initBaseline(initBaseline)", OptionsBaseline.class);

        //// Project options
        // TODO

        MethodSpec initEnvOption = initEnvOptionBuilder.build();

        return initEnvOption;
    }




    private MethodSpec generateInitVariablesMethod() {
        MethodSpec initVariables = MethodSpec.methodBuilder(INIT_VARIABLES)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addCode("testCaseID = \"" + getTestPackageID() + "\";")
                .addCode(COMMENT_SNIPPET + getTestPackageID() + "\n")
                .addStatement("testPackageName = \"" + getTestPackageName() + "\"")
                .build();
        return initVariables;
    }



    ////////// GETTERS //////////
    public Package getTestPackage() {
        return this.testPackage;
    }

    public String getTestPackageName() {
        return this.testPackage.getName();
    }

    public String getTestPackageID() {
        return this.testPackage.getID();
    }

    public NamedElement getTestedElement() {
        return this.testedElement;
    }

    public Class getTestedElementClass() {
        return this.testedElement.getClassType();
    }

    public List<Stereotype> getTestedElementStereotypes() {
        return this.testedElement.getAppliedStereotype();
    }

    public String getTestedElementName() {
        return "testedElement_" + cleanString(this.testedElement.getName()) ;
    }

    private String cleanString(String str) {
        return str.replaceAll("[^a-zA-Z0-9_]", "");
    }

    public Element getTestedElementOwner() {
        return this.testedElement.getOwner();
    }

}
