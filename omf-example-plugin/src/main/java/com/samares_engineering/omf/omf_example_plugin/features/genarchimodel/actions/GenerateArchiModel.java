/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.actions;

import com.google.common.reflect.ClassPath;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.impl.ElementsFactory;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ParameterDirectionKindEnum.RETURN;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Generate Model Archi", category = "ArchiGeneration")
public class GenerateArchiModel extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null)
            return false;
        if(selectedElements.isEmpty()) return false;

        return !selectedElements.isEmpty();
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        if(selectedElements == null)
            return;
       Element owner = selectedElements.get(0);

//       generatedPackage = SysMLFactory.getInstance().getMagicDrawFactory().createPackageInstance();
//       generatedPackage.setOwner(owner);
        generatedPackage = (Package) owner;
        Class architectureOwner = SysMLFactory.getInstance().createBlock(owner);
        architectureOwner.setName("ArchitectureOwner");

        magicdrawFactory = Application.getInstance().getProject().getElementsFactory();

       generatePluginBDD(getAllPluginClasses(), architectureOwner);

    }

    public static Package generatedPackage;
    ElementsFactory magicdrawFactory;

    public void generatePluginBDD(List<java.lang.Class> pluginClassList, Element owner) {
        for (java.lang.Class pluginClass : pluginClassList) {
            // Create a new Block for the class
            String packageName = pluginClass.getPackage().getName();
            Class clazz = findOrCreateClass(findOrCreatePackage(packageName), pluginClass.getSimpleName());
            clazz.setName(pluginClass.getSimpleName());
            // Add fields for each attribute in the class
            generateAttributes(pluginClass, packageName, clazz);

            // Check if the class has a superclass
            java.lang.Class superclass = pluginClass.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                // Create a new Generalization relationship between the class and its superclass
                Generalization generalization = magicdrawFactory.
                        createGeneralizationInstance();
                Class upperClass = findOrCreateClass(findOrCreatePackage(packageName), superclass.getSimpleName());
                generalization.setSpecific(clazz);
                generalization.setGeneral(upperClass);
            }

        }
    }

    private void generateAttributes(java.lang.Class pluginClass, String packageName, Class clazz) {
        for (Field field : pluginClass.getDeclaredFields()) {
            String fieldName = field.getName();
            String fieldType = field.getType().getSimpleName();

            Property attribute = SysMLFactory.getInstance().createProperty(clazz);
            attribute.setName(fieldName);
            Multiplicity multiplicity = Multiplicity.ONE;

            // Check if the field is an array
            if (fieldType.endsWith("[]")) {
                fieldType = fieldType.substring(0, fieldType.length() - 2);
                multiplicity = Multiplicity.ZERO_OR_MORE;
            }

            // Check if the field is a Collection
            if (Collection.class.isAssignableFrom(field.getType())) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                java.lang.reflect.Type[] typeArguments = parameterizedType.getActualTypeArguments();
                String targetTypeName = ((java.lang.Class<?>) typeArguments[0]).getSimpleName();
                fieldType = targetTypeName;
                multiplicity = Multiplicity.ZERO_OR_MORE;
            }

            // Check if the field is a Map
            if (Map.class.isAssignableFrom(field.getType())) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                java.lang.reflect.Type[] typeArguments = parameterizedType.getActualTypeArguments();
                String keyTypeName = ((java.lang.Class<?>) typeArguments[0]).getSimpleName();
                java.lang.reflect.Type typeArgument = typeArguments[1];
                String valueTypeName = ((java.lang.Class<?>) typeArgument).getSimpleName();
                fieldType = valueTypeName;
                multiplicity = Multiplicity.ZERO_OR_MORE;
            }

            attribute.setType(findOrCreateClass(findOrCreatePackage(packageName), fieldType));

        }

        // Add methods for each method in the class
        for (Method method : pluginClass.getDeclaredMethods()) {
            String methodName = method.getName();
            String returnType = method.getReturnType().getSimpleName();

            // Create a new Operation for the method
            Operation operation = magicdrawFactory.createOperationInstance();
            operation.setName(methodName);
            operation.setOwner(clazz);
            PluginArchitectureFactory.getInstance().createParameter(operation, "return", findOrCreateClass(findOrCreatePackage(packageName), returnType), RETURN);

            // Add parameters for each parameter in the method
            for (java.lang.reflect.Parameter parameter : method.getParameters()) {
                String parameterName = parameter.getName();
                String parameterType = parameter.getType().getSimpleName();

                PluginArchitectureFactory.getInstance().createParameter(operation,
                        parameterName, findOrCreateClass(findOrCreatePackage(packageName), parameterType), ParameterDirectionKindEnum.IN);
            }
        }
    }

    private Element findOrCreatePackage(String packageName) {
        Package rootPackage = generatedPackage;
        Package currentPackage = rootPackage;

        // Découper le nom du package en parties séparées par des points
        String[] packageParts = packageName.split("\\.");

        // Parcourir toutes les parties du package
        for (String packagePart : packageParts) {
            Optional<Package> optChildPackage = currentPackage.getNestedPackage().stream().filter(p -> p.getName().equals(packagePart)).findFirst();

            Package childPackage = null;
            if (optChildPackage.isEmpty()) {
                // Si le sous-package n'existe pas, le créer
                childPackage = createPackage(packagePart, currentPackage);
            }else {
                childPackage = optChildPackage.get();
            }

            currentPackage = childPackage;
        }

        return currentPackage;
    }
    private Package createPackage(String packageName, Package parentPackage) {
        // Créer un descripteur de package pour le package à créer
        Package createdPackage = OMFUtils.currentProject.getElementsFactory().createPackageInstance();
        createdPackage.setName(packageName);
        createdPackage.setOwner(parentPackage);
        return createdPackage;
    }

    private Class findOrCreateClass(Element owner, String fieldType) {

        Type type = Finder.byNameRecursively().find(generatedPackage, Class.class, fieldType);
        if (type == null) {
            type = SysMLFactory.getInstance().createBlock(owner);
            type.setName(fieldType);

        }
        return (Class) type;
    }




    public List<java.lang.Class> getAllPluginClasses() {
        List<java.lang.Class> classes = new ArrayList<>();

        // Get the class loader for the current plugin
        ClassLoader pluginClassLoader = getClass().getClassLoader();

        // Get all the classes in the current plugin
        ClassPath classPath;
        try {
            classPath = ClassPath.from(pluginClassLoader);
            for (ClassPath.ClassInfo classInfo : classPath.getAllClasses()) {
                if (classInfo.getName().startsWith("com.samares_engineering.omf")) {
                    java.lang.Class<?> clazz = classInfo.load();
                    classes.add(clazz);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get all the classes in the "com.samares_engineering.omf" package of the associated libraries
        File libraryDir = new File("/path/to/library/dir");
        if (libraryDir.isDirectory()) {
            File[] libraryFiles = libraryDir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });
            for (File libraryFile : libraryFiles) {
                try {
                    URL libraryUrl = libraryFile.toURI().toURL();
                    URLClassLoader libraryClassLoader = new URLClassLoader(new URL[] {libraryUrl}, pluginClassLoader);

                    classPath = ClassPath.from(libraryClassLoader);
                    for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses("com.samares_engineering.omf")) {
                        java.lang.Class<?> clazz = classInfo.load();
                        classes.add(clazz);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return classes;
    }

    private enum Multiplicity {
        ONE, ZERO_OR_MORE

    }
}