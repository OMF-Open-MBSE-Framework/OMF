package com.samares_engineering.omf.omf_public_features.patterncreation;

import com.nomagic.magicdraw.copypaste.CopyPasting;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.ConvertElementInfo;
import com.nomagic.magicdraw.uml.Refactoring;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PatternCreation {

    public static void generatePatternsFromElements(Element createdElement, List<Dependency> configuredOnCreationDependencies) throws ReadOnlyElementException, NoPatternFoundOnTemplateElementException {
        for (Dependency onCreationDependency: configuredOnCreationDependencies) {
            Optional<Element> optTarget = onCreationDependency.getTarget().stream().findFirst();
            if(optTarget.isEmpty()) continue;

            Element templateElement = optTarget.get();
            generatePatternFromElement(createdElement, templateElement);
        }
    }

    public static void replaceElementWithGeneratedPatterns(Element createdElement, List<Dependency> configuredOnCreationDependencies) throws ReadOnlyElementException, NoPatternFoundOnTemplateElementException {
        for (Dependency onCreationDependency: configuredOnCreationDependencies) {
            Optional<Element> optTarget = onCreationDependency.getTarget().stream().findFirst();
            if(optTarget.isEmpty()) continue;

            Element templateElement = optTarget.get();
            replaceElementWithGeneratedPattern(createdElement, templateElement);
        }
    }

    public static void replaceElementWithGeneratedPattern(Element createdElement, Element templateElement) throws ReadOnlyElementException, NoPatternFoundOnTemplateElementException {
        Element copiedTemplateElement = generatePatternFromElement(createdElement, templateElement);
        Refactoring.Replacing.replace(createdElement, copiedTemplateElement, new ConvertElementInfo(createdElement.getClass()));
    }
    public static Element generatePatternFromElement(Element createdElement, Element templateElement) throws NoPatternFoundOnTemplateElementException {
        PatternCreatorProfile.PatternStereotype patternStr = PatternCreatorProfile.getInstance().pattern();

        if(patternStr.is(templateElement)){
            return copyPattern(createdElement, templateElement);
        }else{
            if(patternStr.is(templateElement.getOwner())){
                List<Element> allElementFromPattern = (List) templateElement.getOwner().getOwnedElement();
                List<Element> copiedElements = copyPattern(createdElement, allElementFromPattern);
                return findTemplateElementInCopiedElements(templateElement, copiedElements);
            }else{
                throw new NoPatternFoundOnTemplateElementException(templateElement);
            }
        }
    }

    private static Element copyPattern(Element createdElement, Element templateElement) {
        return removePatternSTR(CopyPasting.copyPasteElement(templateElement, createdElement.getOwner()));
    }

    private static List<Element> copyPattern(Element createdElement, List<Element> allElementFromPattern) {
        return removePatternSTR(CopyPasting.copyPasteElements(allElementFromPattern, createdElement.getOwner()));
    }

    private static List<Element> removePatternSTR(List<Element> list) {
        return list.stream()
                .map(PatternCreation::removePatternSTR)
                .collect(Collectors.toList());
    }

    private static Element removePatternSTR(Element element) {
        PatternCreatorProfile.PatternStereotype patternStr = PatternCreatorProfile.getInstance().pattern();
        element.getAppliedStereotype().remove(patternStr);
        return element;
    }

    private static Element findTemplateElementInCopiedElements(Element templateElement, List<Element> copiedElements) {
        return copiedElements.stream()
                .filter(element -> element.getHumanName().equals(templateElement.getHumanName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Template element not found in copied elements"));
    }

    public static List<Dependency> getAllOnCreationDependencyFromElement(Element createdElement, Set<Stereotype> configuredSTR) {
        return createdElement.getAppliedStereotype().stream()
                .filter(configuredSTR::contains)
                .map(Stereotype::getClientDependency)
                .flatMap(Collection::stream)
                .filter(PatternCreatorProfile.getInstance().onCreation()::is)
                .collect(Collectors.toList());
    }

}
