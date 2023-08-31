package com.samares_engineering.omf.omf_core_framework.utils.Clone;

import com.nomagic.magicdraw.copypaste.CopyPasting;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;
import com.samares_engineering.omf.omf_core_framework.utils.utils.ConnectorUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used to clone elements and their traceability links
 * Current implementation allows only deep copy of elements.
 */
public class CloneManager {
    public Set<Element> elementsToCopy;
    public String CLONED_ELEMENT_SUFFIX;
    public static final String DEFAULT_CLONED_ELEMENT_SUFFIX = "_CLONED";
    public Map<Element, Element> taggedElementForCopy;

    private final List<Element> allStereotypes;
    public Map<Element, Element> orignialClonedMap;
    private int iTaggedElement;
    private List<Element> clonedElements;

    private ElementGetter elementGetter;

    /**
     * Default constructor
     */
    public CloneManager(){
        this(DEFAULT_CLONED_ELEMENT_SUFFIX);
    }

    /**
     * Constructor with suffix
     * @param suffix the suffix to add to all cloned elements
     */
    public CloneManager(String suffix) {
        elementsToCopy = new HashSet<>();
        CLONED_ELEMENT_SUFFIX = suffix;
        taggedElementForCopy = new HashMap<>();
        orignialClonedMap = new HashMap<>();
        elementGetter = new ElementGetter();
        allStereotypes = Profile._getSysml().getAllStereotypes().stream().collect(Collectors.toList()); //TODO use the previous element to copy to tag the elements
    }


    /**
     * Clone all the Elements inside the owner, and rename them with the PREFIX_CLONE
     * @param owner the owner of the elements
     * @return the list of copied elements
     */
    public List<Element> cloneElements(Element owner) {
        List<Element> listElementToClone = elementsToCopy.stream().collect(Collectors.toList());
        tagsElementForCopy(listElementToClone);
        clonedElements = CopyPasting.copyPasteElements(listElementToClone, owner);

        buildClonedElementMap();

        setSuffix(clonedElements);
        return clonedElements;
    }

    /**
     * Set the suffix to all the copied elements
     * @param copiedElements the list of copied elements
     */
    private void setSuffix(List<Element> copiedElements) {
        copiedElements.stream()
                .filter(NamedElement.class::isInstance)
                .map(NamedElement.class::cast)
                .forEach(namedElement -> namedElement.setName(namedElement.getName() + CLONED_ELEMENT_SUFFIX));
    }


    /**
     * Precondition: the originalElement and the copiedElements must have been initialized setting the syncElement to their owner
     * Retrieve the cloned element from the copied elements list
     * @param orinalElement the original element
     * @param copiedElements the list of copied elements
     * @return the cloned element
     */
    public Element retrieveClonedElement(Element orinalElement, List<Element> copiedElements) {
        Element copiedElement = copiedElements.stream()
                .filter(e -> e.getSyncElement() == orinalElement.getOwner())
                .findFirst().get();

        orinalElement.setSyncElement(null);
        copiedElement.setSyncElement(null);
        return copiedElement;
    }

    /**
     * Set the syncElement of the originalElement to its owner, to be able to retrieve it later
     * @param originalElement the original element
     */
    public void setOriginalElementToClone(Element originalElement) {
        originalElement.setSyncElement(originalElement.getOwner());
    }

    //------------------------------------ GET ELEMENTS TO COPY---------------------------------------------------------

    /**
     * Get all Type Elements to copy, including the relationship links.
     * @param type the type to get the elements from
     * @return the type elements
     */
    public Collection<? extends Element> getTypeElementsToCopy(Type type) {
        if(type == null) return Collections.emptyList();
        List<Element> elementsToCopy = new ArrayList<>(elementGetter.getRelationshipsFromElement(type));
        elementsToCopy.add(type);
        return elementsToCopy;
    }

    /**
     * Get all the Part elements to copy, including the relationship links.
     * It retrieves deeply the classifier of the part.
     * @param part the part to get the elements from
     * @return the part elements
     */
    public List<Element> getPartElementToCopy(Property part) {
        return getPropertyElementToCopy(part);
    }

    /**
     * Get all the property elements to copy, including the relationship links.
     * It retrieves deeply the classifier of the property.
     * @param property the property to get the elements from
     * @return the property elements
     */
    public List<Element> getPropertyElementToCopy(Property property) {
        List<Element> elementsToCopy = new ArrayList<>(elementGetter.getRelationshipsFromElement(property));
        elementsToCopy.add(property);
        Type type = property.getType();
        if (type == null) return elementsToCopy;

        elementsToCopy.addAll(getDeepCopyClassifier(type));
        return elementsToCopy;
    }


    /**
     * Get deeply all the classifier elements to copy, including the relationship links.
     * @param port the port to get the elements from
     * @return the classifier elements
     */
    public List<Element> getPortElementToCopy(Port port) {
        List<Element> elementsToCopy = new ArrayList<>(elementGetter.getRelationshipsFromElement(port));
        elementsToCopy.add(port);
        Type type = port.getType();
        if (type == null) return elementsToCopy;

        elementsToCopy.addAll(getDeepCopyClassifier(type));
        return elementsToCopy;
    }

    /**
     * Get deeply all the classifier elements to copy, including the relationship links.
     * @param type the type to get the elements from
     * @return the classifier elements
     */
    public Collection<Element> getDeepCopyClassifier(Type type) {
        Collection<Element> ownedElement = type.getOwnedElement();
        List<Element> deepCopyElements = Stream.concat(
                        ownedElement.stream()
                                .filter(TypedElement.class::isInstance)
                                .map(TypedElement.class::cast)
                                .map(TypedElement::getType)
                                .filter(Objects::nonNull)
                                .map(this::getDeepCopyClassifier)
                                .flatMap(Collection::stream),
                        ownedElement.stream()
                                .map(elementGetter::getRelationshipsFromElement)
                                .flatMap(Collection::stream))
                .collect(Collectors.toList());
        deepCopyElements.addAll(elementGetter.getRelationshipsFromElement(type));
        deepCopyElements.add(type);
        return deepCopyElements;
    }


    //------------------------------------ FIXING CLONED ELEMENTS ------------------------------------------------------
    /**
     * Fix all the cloned elements (Ownership, property path, ...)
     */
    private void fixAllClonedElements() {
        //TODO: fix ownership of each element
        //TODO: call fixCopiedConnectors
    }
    /**
     * Fix all the copied connectors paths.
     * It will fix the ownership of the connectors and the property path of the connector ends.
     * (When copying a connector, the property path of the connector is set by MagicDraw depending on the copy element list,
     * if a port is copied with its connector, the property path of the connector will be set to the copied port: OK.
     * But if there is a nested port from the same interface with the destination,
     * the destination propertyPathWill be affected, thus the connector will be broken)
     * @param connectorList the list of connectors to fix
     */
    private void fixCopiedConnectors(List<Connector> connectorList) {
        for (Connector originalConnector : connectorList) {
            Connector copiedConnector = (Connector) retrieveClonedElement(originalConnector);

            copiedConnector.setOwner(originalConnector.getOwner()); //FIX Ownership

            int iEndToFix = getEndToFix(copiedConnector);
            if (iEndToFix == -1) continue;
            ConnectorEnd endToFix = copiedConnector.getEnd().get(iEndToFix);

            //Fix property path
            List<Element> propertyPath = Profile._getSysml().elementPropertyPath().getPropertyPath(originalConnector.getEnd().get(iEndToFix));
            Profile._getSysml().elementPropertyPath().setPropertyPath(endToFix, propertyPath);

            //Fix role
            endToFix.setRole(originalConnector.getEnd().get(iEndToFix).getRole());
        }
    }
    /**
     * Compute which end to fix from the connector.
     * It will return the index of the end to fix, or -1 if no end to fix.
     * Algorithm:
     * 1. Get the property path of the first end and the second end
     * 2. Find the first element in the property path that is in the copied elements list
     * 3. If the first element is in the first property path, return 1, else return 0,
     * meaning the one to fiw is the opposite (the one to be reset with original propertyPath)
     * @param copiedConnector the copied connector
     * @return the index of the end to fix, or -1 if no end to fix.
     */
    private int getEndToFix(Connector copiedConnector) {
        ConnectorEnd firstEnd = copiedConnector.getEnd().get(0);
        List<Element> firstPropertyPath = Profile._getSysml().elementPropertyPath().getPropertyPath(firstEnd);
        ConnectorEnd secondEnd = copiedConnector.getEnd().get(1);
        List<Element> secondPropertyPath = Profile._getSysml().elementPropertyPath().getPropertyPath(secondEnd);

        int i = 0;
        int maxSize = Math.max(firstPropertyPath.size(), secondPropertyPath.size());
        while (i < maxSize) {
            boolean firstEndNotExceeded = i < firstPropertyPath.size();
            boolean secondEndNotExceeded = i < secondPropertyPath.size();
            if(firstEndNotExceeded && clonedElements.contains(firstPropertyPath.get(i))) return 1;
            if(secondEndNotExceeded && clonedElements.contains(secondPropertyPath.get(i))) return 0;
            i++;
        }
        return -1;

    }
    /**
     * Fix the cloned connector ownership.
     * @param connector the connector to fix
     */
    private void fixConnectorOwnerShip(Connector connector) {
        List<Property> firstPropertyPath = Profile.getInstance().getSysml().elementPropertyPath().getPropertyPath(ModelHelper.getFirstEnd(connector)).stream().map(Property.class::cast).collect(Collectors.toList());
        List<Property> secondPropertyPath = Profile.getInstance().getSysml().elementPropertyPath().getPropertyPath(ModelHelper.getSecondEnd(connector)).stream().map(Property.class::cast).collect(Collectors.toList());
        Element owner = ConnectorUtils.getCommonAncestorFromPropertyPath(firstPropertyPath, secondPropertyPath);
        connector.setOwner(owner);
    }

    //----------------------------- TAGGING TO RETRIEVE CLONED ELEMENTS -----------------------------


    /**
     * Build the map between the original elements and the cloned elements
     * It will clear the syncElement of the original elements and the cloned elements
     */
    private void buildClonedElementMap() {
        clonedElements.forEach(clonedElement -> taggedElementForCopy.put(clonedElement.getSyncElement(), clonedElement));
        getElementsToCopy()
                .forEach(originalElement -> orignialClonedMap.put(originalElement, retrieveClonedElementFromTag(originalElement)));
    }

    /**
     * Tag the elements to copy using synchElement to be able to retrieve them later
     * It is temporary using the stereotypes from SysML to tag the elements.
     * @param elements the elements to tag
     */
    private void tagsElementForCopy(Collection<? extends Element> elements) {
        //TODO: use the previous element to copy to tag the elements
        for (Element element : elements) {
            Element tagElement = allStereotypes.get(iTaggedElement++);
            tagElementForCopy(element, tagElement);
        }
    }

    /**
     * Tag the element to copy using synchElement to be able to retrieve it later
     * @param originalElement the original element
     * @param tagElement the tag element
     */
    private void tagElementForCopy(Element originalElement, Element tagElement) {
        originalElement.setSyncElement(tagElement);
        taggedElementForCopy.put(tagElement, null);
    }


    /**
     * Get the cloned element from the original element using the tag (syncElement)
     * @param orinalElement the original element
     * @return the cloned element
     */
    private Element retrieveClonedElementFromTag(Element orinalElement) {
        return retrieveClonedElementFromTag(orinalElement, orinalElement.getSyncElement());
    }

    /**
     * Get the cloned element from the original element using the mappedElement as tag.
     * Clean the syncElement of the original element and the mappedElement.
     * @param orinalElement the original element
     * @param mappedElement the tag element
     * @return the cloned element
     */
    private Element retrieveClonedElementFromTag(Element orinalElement, Element mappedElement) {
        Element copiedElement = taggedElementForCopy.get(mappedElement);

        orinalElement.setSyncElement(null);
        copiedElement.setSyncElement(null);
        return copiedElement;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get the elements to copy
     * @return
     */
    public Set<Element> getElementsToCopy() {
        return elementsToCopy;
    }

    /**
     * Set the elements to copy
     * @param elementsToCopy
     */
    public void setElementsToCopy(Set<Element> elementsToCopy) {
        this.elementsToCopy = elementsToCopy;
    }

    /**
     * Get the suffix to add to all cloned elements
     * @return the suffix to add to all cloned elements
     */
    public String getCLONED_ELEMENT_SUFFIX() {
        return CLONED_ELEMENT_SUFFIX;
    }

    /**
     * Set the suffix to add to all cloned elements
     * @param CLONED_ELEMENT_SUFFIX the suffix to add to all cloned elements
     */
    public void setCLONED_ELEMENT_SUFFIX(String CLONED_ELEMENT_SUFFIX) {
        this.CLONED_ELEMENT_SUFFIX = CLONED_ELEMENT_SUFFIX;
    }

    /**
     * Retrieve the cloned element from the original element.
     * @param originalElement the original element
     * @return the cloned element
     */
    public Element retrieveClonedElement(Element originalElement) {
        return orignialClonedMap.get(originalElement);
    }



}
