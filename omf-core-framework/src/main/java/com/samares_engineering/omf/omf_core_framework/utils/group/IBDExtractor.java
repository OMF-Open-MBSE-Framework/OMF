package com.samares_engineering.omf.omf_core_framework.utils.group;

import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.ConvertElementInfo;
import com.nomagic.magicdraw.uml.ElementWrapper;
import com.nomagic.magicdraw.uml.Refactoring;
import com.nomagic.magicdraw.uml.refactor.extract.*;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;

import java.util.ArrayList;
import java.util.List;

/**
 * Inspired from the MagicDraw right-click refactor.extract
 * Provides methods to extract and group elements usages into a new one, with flow delegation, a diagram to access it.
 */
public class IBDExtractor {


    /**
     * Group the provided parts into a new Block with port delegation and a diagram to access it, creating in the diagram. (see right-click refactor.extract from MagicDraw)
     * The createdBlock will be replaced by the targetPartOwner, and the strPartsToApply will be applied to its part.
     * @param partsPEToExtract the parts to extract and group
     * @param targetPartOwner the new owner of the grouped parts
     * @param strPartsToApply the stereotype to apply to the ownerPart
     * @return the new owner of the grouped parts.
     */
    public Element extractParts(List<PresentationElement> partsPEToExtract, Class targetPartOwner, Stereotype strPartsToApply) {
        ExtractManager extractManager = extractParts(partsPEToExtract, targetPartOwner.getPackage(), strPartsToApply, null);
        targetPartOwner = (Class) replaceWithTargetElement(extractManager.getExtractTarget().getElement(), targetPartOwner);

        targetPartOwner.get_typedElementOfType().stream().forEach(part -> StereotypesHelper.addStereotype(part, strPartsToApply));
        return targetPartOwner;
    }

    /**
     * Group the provided parts into a new Block with port delegation and a diagram to access it, creating in the diagram. (see right-click refactor.extract from MagicDraw)
     * The createdBlock will be created depending on the strPartOwnerToApply.
     * @param partsPEToExtract the parts to extract and group
     * @param ownerPackage the package used to set the namespace of the new block
     * @param strPartsToApply unsure what it does... TODO: check what it does
     * @param strPartOwnerToApply the stereotype to apply to the createdElement grouping the parts
     * @return the createdElement grouping the parts
     */
    public ExtractManager extractParts(List<PresentationElement> partsPEToExtract, Package ownerPackage, Stereotype strPartsToApply, Stereotype strPartOwnerToApply) {
        PresentationElement[] arrayPE = partsPEToExtract.toArray(new PresentationElement[0]);
        ExtractManager extractManager = Refactoring.Extracting.createExtractManager(arrayPE);


        String diagramType = partsPEToExtract.get(0).getDiagramPresentationElement().getDiagramType().getType();

        ExtractSource extractSource = refactorSourcesReferences(strPartsToApply, extractManager);
        ExtractTarget extractTarget = refactorTargetReferences(strPartOwnerToApply, extractManager, ownerPackage, diagramType);

        renameAllReferences(extractTarget);

        extractManager.extract();
        return extractManager;
    }

    /**
     * Refactor the target references of the extractManager.
     * @param strPartOwnerToApply the stereotype to apply to the createdElement grouping the parts
     * @param extractManager the extractManager to refactor
     * @param targetNameSpacePackage the package used to set the namespace of the new block
     * @param diagramType the diagram type of the diagram to create
     * @return the extractTarget
     */
    private static ExtractTarget refactorTargetReferences(Stereotype strPartOwnerToApply, ExtractManager extractManager, Package targetNameSpacePackage, String diagramType) {
        ExtractTarget extractTarget = extractManager.getExtractTarget();
        extractTarget.setTargetNamespace(targetNameSpacePackage);

        extractTarget.setTargetDiagramType(diagramType);
        extractTarget.setElementName("TMP - THIS ELEMENT WILL BE DELETED");

        ExtractRefactorTarget refactorTarget = (ExtractRefactorTarget) extractManager.getExtractTarget();

        if(strPartOwnerToApply!= null) {
            refactorTarget.setElementWrapper(ElementWrapper.getWrapper(strPartOwnerToApply));
        }
        return extractTarget;
    }

    /**
     * Refactor the source references of the extractManager.
     * @param strPartsToApply unsure what it does... TODO: check what it does
     * @param extractManager the extractManager to refactor
     * @return the extractSource
     */
    private static ExtractSource refactorSourcesReferences(Stereotype strPartsToApply, ExtractManager extractManager) {
        ExtractSource extractSource = extractManager.getExtractSource();
        ExtractRefactorSource refactorSource = (ExtractRefactorSource) extractSource;

        if(strPartsToApply!= null) {
//            refactorSource.setElementWrapper(ModelElementWrapper.getWrapper(strPartsToApply));
        }
        extractSource.setElementName("");

        return extractSource;
    }

    /**
     * Rename all the references of the extractTarget.
     * @param extractTarget the extractTarget to rename
     */
    private static void renameAllReferences(ExtractTarget extractTarget) {
        List<? extends ExtractReference> references = extractTarget.getReferences();
        for (int i = 0; i < references.size(); i++) {
            ExtractReference reference = references.get(i);
            reference.setName("p" + i);
        }
    }

    /**
     * Replace the extractedTarget by the newTargetElement.
     * @param extractedTarget the extractedTarget to replace
     * @param newTargetElement the newTargetElement to replace with
     * @return the newTargetElement
     */
    private Element replaceWithTargetElement(Element extractedTarget, Element newTargetElement) {

        new ArrayList<>(extractedTarget.getOwnedElement()).forEach(elem -> elem.setOwner(newTargetElement));
        try {
            return Refactoring.Replacing.replace(newTargetElement,extractedTarget, new ConvertElementInfo(newTargetElement.getClass()));
        } catch (ReadOnlyElementException e) {
            LegacyErrorHandler.handleException(
                    new LegacyOMFException("Error while extracting, it seems some element related to the extract are readOnly",
                            e, GenericException.ECriticality.CRITICAL), true);
        }

        return null;
    }
}
