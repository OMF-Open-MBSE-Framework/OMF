/*
 * Copyright (c) 2021. Samares-Engineering for Renault.
 * All rights reserved and granted to Renault.
 */

package com.samares_engineering.omf.omf_public_features.generateProfileClassWrapper.uiaction;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.profiles.ProfileImplementation;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generate a wrapper class for a profile, using the original wrapper class from DevelopmentTools plugin.
 * Add a default getInstance() method, using OMFUtils.getProject().
 * Update all references to nested classes from the profile, e.g. FlowDirectionKindEnum, RiskKindEnum, ...
 * The wrapper class is copied to the clipboard.
 */
@DeactivateListener
@BrowserAction
@MDAction(actionName = "Generate Profile Wrapper", category = "OMF Profile Wrapper")
public class GenerateProfileClassWrapperAction extends AUIAction {

    private String simpleName;

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {

        if(selectedElements.size() != 1) return false;
        if(selectedElements.get(0) instanceof Profile) return true;
        return false;
    }
    @Override
    public void actionToPerform(List<Element> selectedElements) {
        Class<? extends ProfileImplementation> sysMLProfileClass = SysMLProfile.class;
        String originalWrapper = getOriginalGenerateProfileWrapper((Profile) selectedElements.get(0));

        ParseResult<CompilationUnit> result = new JavaParser().parse(originalWrapper);

        if (result.isSuccessful() && result.getResult().isEmpty()) {
            LegacyErrorHandler.handleException(new LegacyOMFException("Error while parsing the original wrapper", GenericException.ECriticality.CRITICAL), false);
        }

        CompilationUnit cu = result.getResult().get();

        addCustomsImports(cu, List.of(sysMLProfileClass, OMFUtils.class));

        addDefaultOMFGetInstance(cu);

        List<String> sysMLClasses = getAllNestedClassesFromClass(sysMLProfileClass);
        updateSysMLProfileReferences(cu, sysMLClasses, sysMLProfileClass.getSimpleName());

        copyToClipBoard(cu);

    }

    private static List<String> getAllNestedClassesFromClass(Class<? extends ProfileImplementation> profileClass) {
        try {
            return Arrays.stream(profileClass.getDeclaredClasses())
                    .filter(aClass -> java.lang.reflect.Modifier.isStatic(aClass.getModifiers()))
                    .map(Class::getSimpleName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LegacyErrorHandler.handleException(new LegacyOMFException("Error while retrieving nested classes/enums", GenericException.ECriticality.CRITICAL), true);
            return Collections.emptyList();
        }
    }
    private static void copyToClipBoard(CompilationUnit cu) {
        String updatedCode = cu.toString();
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(updatedCode);
        clip.setContents(tText, null);
    }

    private void updateSysMLProfileReferences(CompilationUnit cu, List<String> nestedProfileClass, String profile) {
        String profileReference = profile + ".";
        //1. Tackle Return Nested Class type (FlowDirectionKindEnum, RiskKindEnum, ...)
        cu.findAll(MethodDeclaration.class)
                .forEach(method -> {
                    if (nestedProfileClass.contains(method.getType().asString()) && !method.getType().asString().startsWith(profile)) {
                        method.setType(new ClassOrInterfaceType(profileReference +  method.getType().asString()));
                    }
                });

        // 2. Tackle method parameters
        cu.findAll(Parameter.class)
                .forEach(param -> {
                    if (nestedProfileClass.contains(param.getType().asString()) && !param.getType().asString().startsWith(profile)) {
                        param.setType(new ClassOrInterfaceType(profileReference + param.getType().asString()));
                    }
                });

        // 3. Tackle method calls, e.g. SysMLProfile.FlowDirectionKindEnum.from()
        cu.findAll(MethodCallExpr.class, call -> call.getNameAsString().equals("from"))
                .stream()
                .filter(call -> call.getScope().isPresent() && call.getScope().get() instanceof NameExpr)
                .map(call -> (NameExpr) call.getScope().get())
                .filter(name -> nestedProfileClass.contains(name.getNameAsString()) && !name.getNameAsString().startsWith(profile))
                .forEach(name -> name.setName(profileReference + name.getNameAsString()));
    }

    private void addDefaultOMFGetInstance(CompilationUnit cu) {
        // Ajouter la méthode getInstance()
        MethodDeclaration getInstanceMethod = addGetInstance();
        ClassOrInterfaceDeclaration yourClass = cu.getClassByName(simpleName).get();
        yourClass.addMember(getInstanceMethod);
    }

    private static void addCustomsImports(CompilationUnit cu, List<Class> profileClasses) {
        profileClasses.forEach( classToImport -> cu.addImport(new ImportDeclaration(classToImport.getName(), false, false)));

    }

    private MethodDeclaration addGetInstance() {
        // Définir la méthode
        MethodDeclaration getInstanceMethod = new MethodDeclaration();

        getInstanceMethod.setModifiers(NodeList.nodeList(Modifier.publicModifier(), Modifier.staticModifier()));

        getInstanceMethod.setType("MBSIProfile");
        getInstanceMethod.setName("getInstance");

        BlockStmt body = new BlockStmt();
        body.addStatement("return getInstance(OMFUtils.getProject());");
        getInstanceMethod.setBody(body);

        return getInstanceMethod;
    }

    private String getOriginalGenerateProfileWrapper(Profile profile) {
//        return new DevToolso(profile).DevToolsa();
        return "";
    }


}
