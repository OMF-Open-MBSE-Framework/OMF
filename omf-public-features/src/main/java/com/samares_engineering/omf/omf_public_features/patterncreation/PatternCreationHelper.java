package com.samares_engineering.omf.omf_public_features.patterncreation;

import com.nomagic.magicdraw.copypaste.CopyPasting;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.ConvertElementInfo;
import com.nomagic.magicdraw.uml.Refactoring;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DirectedRelationship;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
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

    /** //NOT TESTED
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
     * @param createdPatternElement
     * @param templateElement
     * @return
     * @throws NoPatternFoundOnTemplateElementException
     */
    public static Element generatePatternFromElement(Element createdPatternElement, Element templateElement) throws NoPatternFoundOnTemplateElementException {
        Optional<Element> optTemplateElementOwner = getAccordingTemplateOwner(createdPatternElement, templateElement);
        if(optTemplateElementOwner.isEmpty()) throw new NoPatternFoundOnTemplateElementException(templateElement); //TODO: add a message to the exception "No Pattern corresponding to the template element was found, owners don't match"

        Element templateElementOwner = optTemplateElementOwner.get(); //MODEL READ ONLY ELEMENT

        templateElement.setSyncElement(createdPatternElement); //To retrieve the copied element later

        if(templateElementOwner == templateElement){ //if template element is THE element defining the pattern structure
            return copyPattern(createdPatternElement, templateElement);
        }else{

            PatternCreatorProfile.PatternTemplateStereotype patternTemplate = PatternCreatorProfile.getInstance().patternTemplate();
            if(patternTemplate.is(templateElementOwner)){ //The pattern structure is defined by its owner.
                return copyPatternFromTemplateOwner(createdPatternElement, templateElementOwner);
            }else{
                throw new NoPatternFoundOnTemplateElementException(templateElement);
            }
        }
    }

    private static Element copyPatternFromTemplateOwner(Element createdPatternElement, Element templateElementOwner) {
        //Copying the pattern structure, putting in a temp place to allow the refactoring.replace to work without loosing all elements

        Element srcOwner = getSourceOwner(createdPatternElement);
        Collection<Element> patternElements = copyPatternFromTemplateOwner(templateElementOwner, OMFUtils.currentProject.getPrimaryModel()).getOwnedElement();
        Element patternTemplateImpl = getTargetPatternElementFromCopiedElements(patternElements, createdPatternElement);

        Element patternTemplateOwner = patternTemplateImpl.getOwner();
        new ArrayList<>(patternTemplateOwner.getOwnedElement()).forEach(e -> e.setOwner(srcOwner));//new Arraylist due to concurrent modification exception

        Element newSrcOwner = replaceModelElement(srcOwner, patternTemplateOwner);

        removePatternSTR(newSrcOwner);
        removePatternSTR(patternTemplateImpl);

        //Removing the sync element on the Pattern definition elements
        templateElementOwner.setSyncElement(null);
        patternTemplateImpl.setSyncElement(null);

        return patternTemplateImpl;
    }

    private static Element replaceModelElement(Element srcOwner, Element patternTemplateOwner) {
        try {
            //Replacing the pattern structure element with the owner of the created element (for diagram and relations consistency)
            Refactoring.Replacing.replace(srcOwner, patternTemplateOwner, new ConvertElementInfo(srcOwner.getClass()));
            return patternTemplateOwner; //srcOwner is now the pattern structure element
        }catch (ReadOnlyElementException e){
            OMFErrorHandler.handleException(
                    new OMFException("Cannot replace element with generated pattern, the element "
                            + srcOwner.getHumanName() + " is read only",
                            e, GenericException.ECriticality.CRITICAL), true);
        }
        return srcOwner;
    }

    private static Element getTargetPatternElementFromCopiedElements(Collection<Element> elements, Element createdPatternElement) {
        return elements.stream()
                .filter(element -> element.getSyncElement() != null && element.getSyncElement().equals(createdPatternElement))
                        .findFirst().orElse(null);
    }

    private static Element copyPatternFromTemplateOwner(Element templateElementOwner, NamedElement tmp) {
        List<Element> allTemplateElements = templateElementOwner.get_directedRelationshipOfTarget().stream()
                .filter(PatternCreatorProfile.getInstance().possiblePatternCreationOwner()::is)
                .map(DirectedRelationship::getSource)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        allTemplateElements.add(templateElementOwner);

        List<Element> copiedTemplateElements = CopyPasting.copyPasteElements(allTemplateElements, tmp);
        Element copyTemplateOwner = findTemplateElementInCopiedElements(templateElementOwner, copiedTemplateElements);
        copiedTemplateElements.remove(copyTemplateOwner);
        copiedTemplateElements.forEach(element -> element.setOwner(copyTemplateOwner));
        return copyTemplateOwner;
    }


    private static Element getSourceOwner(Element createdPatternElement) {
        Element srcOwner = createdPatternElement.getOwner();

        boolean isOwnerRootModel = srcOwner.equals(OMFUtils.currentProject.getPrimaryModel());
        if(isOwnerRootModel){
            srcOwner = SysMLFactory.getInstance().createPackage(((NamedElement) createdPatternElement).getName(), srcOwner); //TODO it's a fix, but the package shall not stay in the end?
        }
        return srcOwner;
    }

    private static Element copyPattern(Element createdElement, Element templateElement) {
        return removePatternSTR(CopyPasting.copyPasteElement(templateElement, createdElement.getOwner()));
    }

    /**
     * Retrieve the compatible PatternTemplate element from the created Pattern:
     * - From the PatternTemplate, retrieve all PossibleOwners;
     * - if one has the same class as the created element, return it;
     * - else if the PatternTemplate itself if it is a Property and has the same class as the created element, return it;
     * @param createdElement
     * @param templateElement
     * @return the compatible PatternTemplate element, or Optional.empty() if none was found
     */
    private static Optional<Element> getAccordingTemplateOwner(Element createdElement, Element templateElement) {
        PatternCreatorProfile.PatternTemplateStereotype patternTemplate = PatternCreatorProfile.getInstance().patternTemplate();
        //if the template element is the pattern template itself, return it
        if(patternTemplate.is(templateElement)) return Optional.of(templateElement);

        //If the owner is a PossibleOwner, return it
        Element owner = createdElement.getOwner();
        Optional<Element> optOwner = templateElement.get_directedRelationshipOfSource().stream()
                .filter(PatternCreatorProfile.getInstance().possiblePatternCreationOwner()::is)
                .map(DirectedRelationship::getTarget)
                .flatMap(Collection::stream)
                .filter(owner.getClass()::isInstance)
                .findFirst();
        if(optOwner.isPresent()) return optOwner;

        //If the owner is a property, the template element can be the pattern template itself
        Element templateOwner = templateElement.getOwner();
        boolean matchTemplateWithProperties = createdElement instanceof Property && patternTemplate.is(templateOwner);
        if(matchTemplateWithProperties) {
            return templateOwner.getClass().isInstance(owner) ? Optional.of(templateOwner) : Optional.empty();
        }
        return Optional.empty();
    }

    /**
     * Remove the pattern template stereotype from the elements.
     * @param list
     * @return
     */
    private static List<Element> removePatternSTR(List<Element> list) {
        return list.stream()
                .map(PatternCreationHelper::removePatternSTR)
                .collect(Collectors.toList());
    }

    /**
     * Remove the pattern template stereotype from the element.
     * @param element
     * @return the element without the pattern template stereotype
     */
    private static Element removePatternSTR(Element element) {
        PatternCreatorProfile.PatternInstanceStereotype patternInstance = PatternCreatorProfile.getInstance().patternInstance();
        PatternCreatorProfile.PatternTemplateStereotype patternTemplate = PatternCreatorProfile.getInstance().patternTemplate();
        element.getAppliedStereotype().remove(patternInstance.getStereotype());
        element.getAppliedStereotype().remove(patternTemplate.getStereotype());
        return element;
    }

    /**
     * Find the template element in the copied elements using stereotype.
     * @param templateElement
     * @param copiedElements
     * @return the template element in the copied elements
     */
    private static Element findTemplateElementInCopiedElements(Element templateElement, List<Element> copiedElements) {
        return copiedElements.stream()
                .filter(element -> element.getHumanName().equals(templateElement.getHumanName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Template element not found in copied elements")); //TODO: create an exception, which will be catched by the caller
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
