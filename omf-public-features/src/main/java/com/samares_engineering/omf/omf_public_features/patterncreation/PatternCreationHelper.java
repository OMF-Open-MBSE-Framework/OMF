package com.samares_engineering.omf.omf_public_features.patterncreation;

import com.nomagic.magicdraw.copypaste.CopyPasting;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.ConvertElementInfo;
import com.nomagic.magicdraw.uml.Refactoring;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFException;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.patterncreation.exception.NoPatternFoundOnTemplateElementException;
import com.samares_engineering.omf.omf_public_features.patterncreation.profile.PatternCreatorProfile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper class to create patterns from Pattern Template elements.
 * Pattern shall be defined in the project using the PatternProfile, using OnCreation links, and Pattern Template stereotype.
 */
public class PatternCreationHelper {

    /**
     *  Generate the pattern from the template element to the element owner.
     *  Pattern shall be defined in the project using the PatternProfile, using OnCreation links, and Pattern Template stereotype.
     * @param createdElement
     * @param configuredOnCreationDependencies
     * @throws ReadOnlyElementException
     * @throws NoPatternFoundOnTemplateElementException
     */
    public static void generatePatternsFromElements(Element createdElement, List<Dependency> configuredOnCreationDependencies) throws ReadOnlyElementException, NoPatternFoundOnTemplateElementException {
        for (Dependency onCreationDependency: configuredOnCreationDependencies) {
            Optional<Element> optTarget = onCreationDependency.getTarget().stream().findFirst();
            if(optTarget.isEmpty()) continue;

            Element templateElement = optTarget.get();
            generatePatternFromElement(createdElement, templateElement);
        }
    }

    /**
     *  Generate the pattern from the template element, and replace the created element with the generated pattern.
     *  Pattern shall be defined in the project using the PatternProfile, using OnCreation links, and Pattern Template stereotype.
     * @param createdElement
     * @param configuredOnCreationDependencies
     * @throws ReadOnlyElementException
     * @throws NoPatternFoundOnTemplateElementException
     */
    public static void replaceElementWithGeneratedPatterns(Element createdElement, List<Dependency> configuredOnCreationDependencies) throws ReadOnlyElementException, NoPatternFoundOnTemplateElementException {
        for (Dependency onCreationDependency: configuredOnCreationDependencies) {
            Optional<Element> optTarget = onCreationDependency.getTarget().stream().findFirst();
            if(optTarget.isEmpty()) continue;

            Element templateElement = optTarget.get();
            replaceElementWithGeneratedPattern(createdElement, templateElement);
        }
    }

    /**
     * Generate the pattern from the template element, and replace the created element with the generated pattern.
     * Pattern shall be defined in the project using the PatternProfile, using OnCreation links, and Pattern Template stereotype.
     * @param createdElement
     * @param templateElement
     * @throws ReadOnlyElementException
     * @throws NoPatternFoundOnTemplateElementException
     */
    public static void replaceElementWithGeneratedPattern(Element createdElement, Element templateElement) throws ReadOnlyElementException, NoPatternFoundOnTemplateElementException {
        Element copiedTemplateElement = generatePatternFromElement(createdElement, templateElement);
        removePatternSTR(createdElement);
        Refactoring.Replacing.replace(createdElement, copiedTemplateElement, new ConvertElementInfo(createdElement.getClass()));
    }

    /**
     *  Generate the pattern from the template element, and replace the created element with the generated pattern.
     *  Pattern shall be defined in the project using the PatternProfile, using OnCreation links, and Pattern Template stereotype.
     * @param createdElement
     * @param templateElement
     * @return
     * @throws NoPatternFoundOnTemplateElementException
     */
    public static Element generatePatternFromElement(Element createdElement, Element templateElement) throws NoPatternFoundOnTemplateElementException {
        PatternCreatorProfile.PatternTemplateStereotype patternStr = PatternCreatorProfile.getInstance().patternTemplate();
        removePatternSTR(createdElement);
        if(patternStr.is(templateElement)){ //if template element is THE element defining the pattern structure
            return copyPattern(createdElement, templateElement);
        }else{
            if(patternStr.is(templateElement.getOwner())){ //The pattern structure is defined by its owner.
                //Copying the pattern structure, putting in a temp place to allow the refactoring.replace to work without loosing all elements
                NamedElement tmp = SysMLFactory.getInstance().createBlock(OMFUtils.currentProject.getPrimaryModel());
                Element patternOwner = removePatternSTR(CopyPasting.copyPasteElement(templateElement.getOwner(), tmp));
                new ArrayList<>(tmp.getOwnedElement()).forEach(e -> e.setOwner(createdElement.getOwner()));//new Arraylist due to concurrent modification exception
                patternOwner.setOwner(tmp);
                try {
                    //Replacing the pattern structure element with the owner of the created element (for diagram and relations consistency)
                    Refactoring.Replacing.replace(createdElement.getOwner(), patternOwner, new ConvertElementInfo(createdElement.getClass()));
                    ModelElementsManager.getInstance().removeElement(tmp);
                }catch (ReadOnlyElementException e){
                    OMFErrorHandler.handleException(
                            new OMFException("Cannot replace element with generated pattern, the element "
                                    + createdElement.getOwner().getHumanName() + " is read only",
                                    e, GenericException.ECriticality.CRITICAL), true);
                }
                return findTemplateElementInCopiedElements(templateElement, (List<Element>) createdElement.getOwner().getOwnedElement());
            }else{
                throw new NoPatternFoundOnTemplateElementException(templateElement);
            }
        }
    }

    /**
     * Copy the pattern from the template element to the created element owner, then remove the pattern template stereotype.
     * @param createdElement
     * @param templateElement
     * @return
     */
    private static Element copyPattern(Element createdElement, Element templateElement) {
        return removePatternSTR(CopyPasting.copyPasteElement(templateElement, createdElement.getOwner()));
    }

    /**
     * Copy the pattern from the template element to the created element owner, then remove the pattern template stereotype.
     * @param createdElement
     * @param allElementFromPattern
     * @return
     */
    private static List<Element> copyPattern(Element createdElement, List<Element> allElementFromPattern) {
        return removePatternSTR(CopyPasting.copyPasteElements(allElementFromPattern, createdElement.getOwner()));
    }

    private static List<Element> removePatternSTR(List<Element> list) {
        return list.stream()
                .map(PatternCreationHelper::removePatternSTR)
                .collect(Collectors.toList());
    }

    /**
     * Remove the pattern template stereotype from the element.
     * @param element
     * @return
     */
    private static Element removePatternSTR(Element element) {
        PatternCreatorProfile.PatternTemplateStereotype patternStr = PatternCreatorProfile.getInstance().patternTemplate();
        element.getAppliedStereotype().remove(patternStr);
        return element;
    }

    /**
     * Find the template element in the copied elements using stereotype.
     * @param templateElement
     * @param copiedElements
     * @return
     */
    private static Element findTemplateElementInCopiedElements(Element templateElement, List<Element> copiedElements) {
        return copiedElements.stream()
                .filter(element -> element.getHumanName().equals(templateElement.getHumanName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Template element not found in copied elements"));
    }

    /**
     * Get all the OnCreation dependencies from the created element.
     * @param createdElement
     * @param configuredSTR
     * @return
     */
    public static List<Dependency> getAllOnCreationDependencyFromElement(Element createdElement, Set<Stereotype> configuredSTR) {
        return createdElement.getAppliedStereotype().stream()
                .filter(configuredSTR::contains)
                .map(Stereotype::getClientDependency)
                .flatMap(Collection::stream)
                .filter(PatternCreatorProfile.getInstance().onCreation()::is)
                .collect(Collectors.toList());
    }

}
