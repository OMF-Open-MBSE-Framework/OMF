/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Generate Model Archi", category = "OMF.ArchiGeneration")
public class GenerateArchiModelV2 extends AUIAction {

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.getProject() == null)
            return false;
        if(selectedElements.isEmpty()) return false;

        return true;
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        if(selectedElements == null)
            return;
       Element owner = selectedElements.get(0);

////       generatedPackage = SysMLFactory.getInstance().getMagicDrawFactory().createPackageInstance();
////       generatedPackage.setOwner(owner);
//        generatedPackage = (Package) owner;
//        Class architectureOwner = SysMLFactory.getInstance().createBlock(owner);
//        architectureOwner.setName("ArchitectureOwner");
//
//        magicdrawFactory = Application.getInstance().getProject().getElementsFactory();
//
//       generatePluginBDD(getAllPluginClasses());

    }
//
//    public static Package generatedPackage;
//    ElementsFactory magicdrawFactory;
//
//    public void generatePluginBDD(List<java.lang.Class> pluginClassList) {
//        for (java.lang.Class pluginClass : pluginClassList) {
//            String packageName = pluginClass.getPackage().getName();
//            Class block = findOrCreateClass(packageName, pluginClass.getSimpleName());
//            createAttributes(pluginClass, block);
//            createOperations(pluginClass, block);
//            createGeneralization(pluginClass, block);
//            createCompositionAndAssociation(pluginClass, block);
//        }
//    }
//
//    private void createAttributes(java.lang.Class pluginClass, Class block) {
//        for (Field field : pluginClass.getDeclaredFields()) {
//            String fieldName = field.getName();
//            String fieldType = getFieldType(field);
//            Multiplicity multiplicity = getFieldMultiplicity(field);
//            createAttribute(fieldName, fieldType, multiplicity, block);
//        }
//    }
//
//    private void createAttribute(String fieldName, String fieldType, Multiplicity multiplicity, Class block) {
//        Property attribute = SysMLFactory.getInstance().createProperty(block);
//        attribute.setName(fieldName);
//
//        attribute.setType(findOrCreateClass(, fieldType));
//    }
//
//    private void createOperations(java.lang.Class pluginClass, Class block) {
//        for (Method method : pluginClass.getDeclaredMethods()) {
//            String methodName = method.getName();
//            String returnType = method.getReturnType().getSimpleName();
//            Operation operation = createOperation(methodName, returnType, block);
//            createParameters(method, operation);
//        }
//    }
//
//    private Operation createOperation(String methodName, String returnType, Class block) {
//        Operation operation = magicdrawFactory.createOperationInstance();
//        operation.setName(methodName);
//        operation.setOwner(block);
//        return operation;
//    }
//
//    private void createParameters(Method method, Operation operation) {
//        for (java.lang.reflect.Parameter parameter : method.getParameters()) {
//            String parameterName = parameter.getName();
//            String parameterType = getFieldType(parameter);
//            Multiplicity multiplicity = getFieldMultiplicity(parameter);
//            createParameter(parameterName, parameterType, multiplicity, operation);
//        }
//    }
//
//    private void createParameter(String parameterName, String parameterType, Multiplicity multiplicity, Operation operation) {
//        PluginArchitectureFactory.getInstance().createParameter(operation,
//                parameterName, findOrCreateClass(findOrCreatePackage(packageName), parameterType), ParameterDirectionKindEnum.IN);
//    }
//
//    private void createGeneralization(java.lang.Class pluginClass, Class block) {
//        java.lang.Class superclass = pluginClass.getSuperclass();
//        if (superclass != null) {
//            Generalization generalization = block.createGeneralization(superclass.getSimpleName());
//        }
//    }
//
//    private void createCompositionAndAssociation(java.lang.Class pluginClass, Class block) {
//        for (Field field : pluginClass.getDeclaredFields()) {
//            if (field.isAnnotationPresent(Composition.class)) {
//                String targetType = getFieldType(field);
//                Multiplicity multiplicity = getFieldMultiplicity(field);
//                block.createComposition(targetType, multiplicity);
//            } else if (field.isAnnotationPresent(Association.class)) {
//                String targetType = getFieldType(field);
//                Multiplicity multiplicity = getFieldMultiplicity(field);
//                block.createAssociation(targetType, multiplicity);
//            }
//        }
//    }
//
//    private Type getType(Class type) {
//        Type fieldType = null;
//        String fieldTypeName = type.getSimpleName();
//
//        if (fieldTypeName.endsWith("[]")) {
//            fieldTypeName = fieldTypeName.substring(0, fieldTypeName.length() - 2);
//            fieldTypeName += "<*>";
//        } else if (Collection.class.isAssignableFrom(type)) {
//            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
//            java.lang.reflect.Type[] typeArguments = parameterizedType.getActualTypeArguments();
//            String targetTypeName = ((java.lang.Class) typeArguments[0]).getSimpleName();
//            fieldTypeName = "Collection<" + targetTypeName + ">";
//            fieldTypeName += "<*>";
//        } else if (Map.class.isAssignableFrom(type)) {
//            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
//            java.lang.reflect.Type[] typeArguments = parameterizedType.getActualTypeArguments();
//            String keyTypeName = ((java.lang.Class) typeArguments[0]).getSimpleName();
//            String valueTypeName = ((java.lang.Class) typeArguments[1]).getSimpleName();
//            fieldTypeName = "Map<" + keyTypeName + "," + valueTypeName + ">";
//            fieldTypeName += "<*>";
//            fieldTypeName += "<*>";
//        }
//        return findOrCreateClass(type.getPackageName(), fieldTypeName);
//    }
//
//
//
//    private Multiplicity getFieldMultiplicity(Field field) {
//        if (field.getType().isArray() || Collection.class.isAssignableFrom(field.getType())) {
//            return Multiplicity.ZERO_OR_MORE;
//        }
//        return Multiplicity.ZERO_OR_ONE;
//    }
//
//    private Multiplicity getFieldMultiplicity(Parameter parameter) {
//        if (parameter.getType().isArray() || Collection.class.isAssignableFrom(parameter.getType())) {
//            return Multiplicity.ZERO_OR_MORE;
//        }
//        return Multiplicity.ZERO_OR_ONE;
//    }
//
//
//    private Package findOrCreatePackage(String packageName) {
//    Package rootPackage = generatedPackage;
//    Package currentPackage = rootPackage;
//
//    // Découper le nom du package en parties séparées par des points
//    String[] packageParts = packageName.split("\\.");
//
//    // Parcourir toutes les parties du package
//    for (String packagePart : packageParts) {
//        Optional<Package> optChildPackage = currentPackage.getNestedPackage().stream().filter(p -> p.getName().equals(packagePart)).findFirst();
//
//        Package childPackage = null;
//        if (optChildPackage.isEmpty()) {
//            // Si le sous-package n'existe pas, le créer
//            childPackage = createPackage(packagePart, currentPackage);
//        }else {
//            childPackage = optChildPackage.get();
//        }
//
//        currentPackage = childPackage;
//    }
//
//    return currentPackage;
//    }
//    private Package createPackage(String packageName, Package parentPackage) {
//        // Créer un descripteur de package pour le package à créer
//        Package createdPackage = OMFUtils.getProject().getElementsFactory().createPackageInstance();
//        createdPackage.setName(packageName);
//        createdPackage.setOwner(parentPackage);
//        return createdPackage;
//    }
//
//    private Class findOrCreateClass(String packageName, String fieldType) {
//        Package owner = findOrCreatePackage(packageName);
//        Type type = Finder.byNameRecursively().find(generatedPackage, Class.class, fieldType);
//        if (type == null) {
//            type = SysMLFactory.getInstance().createBlock(owner);
//            type.setName(fieldType);
//
//        }
//        return (Class) type;
//    }
//    public List<java.lang.Class> getAllPluginClasses() {
//        List<java.lang.Class> classes = new ArrayList<>();
//
//        // Get the class loader for the current plugin
//        ClassLoader pluginClassLoader = getClass().getClassLoader();
//
//        // Get all the classes in the current plugin
//        ClassPath classPath;
//        try {
//            classPath = ClassPath.from(pluginClassLoader);
//            for (ClassPath.ClassInfo classInfo : classPath.getAllClasses()) {
//                if (classInfo.getName().startsWith("com.samares_engineering.omf")) {
//                    java.lang.Class<?> clazz = classInfo.load();
//                    classes.add(clazz);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // Get all the classes in the "com.samares_engineering.omf" package of the associated libraries
//        File libraryDir = new File("/path/to/library/dir");
//        if (libraryDir.isDirectory()) {
//            File[] libraryFiles = libraryDir.listFiles(new FilenameFilter() {
//                public boolean accept(File dir, String name) {
//                    return name.endsWith(".jar");
//                }
//            });
//            for (File libraryFile : libraryFiles) {
//                try {
//                    URL libraryUrl = libraryFile.toURI().toURL();
//                    URLClassLoader libraryClassLoader = new URLClassLoader(new URL[] {libraryUrl}, pluginClassLoader);
//
//                    classPath = ClassPath.from(libraryClassLoader);
//                    for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses("com.samares_engineering.omf")) {
//                        java.lang.Class<?> clazz = classInfo.load();
//                        classes.add(clazz);
//                    }
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return classes;
//    }
//
//    private enum Multiplicity {
//        ONE, ZERO_OR_MORE
//
//    }


}