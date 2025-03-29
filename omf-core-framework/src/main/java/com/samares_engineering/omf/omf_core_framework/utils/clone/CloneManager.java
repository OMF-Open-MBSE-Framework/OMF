package com.samares_engineering.omf.omf_core_framework.utils.clone;

import com.nomagic.magicdraw.copypaste.CopyPasting;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.impl.ElementsFactory;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.factory.OMFFactory;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.clone.exceptions.CloneFailedException;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;
import com.samares_engineering.omf.omf_core_framework.utils.utils.ConnectorUtils;
import org.apache.commons.collections4.MapUtils;

import java.lang.Class;
import java.util.*;
import java.util.function.Predicate;
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

    private Map<Element, Element> orignialClonedMap;
    private Map<Element, Element> reversedMap;
    private int iTaggedElement; //Incremental index to tag the elements with the stereotypes (to map the original and the cloned elements)
    private List<Element> clonedElements;
    private final List<java.lang.Class<? extends Element>> metaClassToFilter;
    private final List<Stereotype> stereotypeToFilter;

    private Property originalElementTag;
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
        initAllInternalVariables(suffix);
        metaClassToFilter = new ArrayList<>();
        stereotypeToFilter = new ArrayList<>();
    }

    /**
     * Initialize all the internal variables
     * @param suffix the suffix to add to all cloned elements
     */
    private void initAllInternalVariables(String suffix) {
        elementsToCopy = new HashSet<>();
        CLONED_ELEMENT_SUFFIX = suffix;
        taggedElementForCopy = new HashMap<>();
        orignialClonedMap = new HashMap<>();
        reversedMap = new HashMap<>();
        elementGetter = new ElementGetter();

        createTMPTagValue();

    }

    private void createTMPTagValue() {
        ElementsFactory factory = OMFFactory.getInstance().getMagicDrawFactory();
        originalElementTag = factory.createPropertyInstance();
        originalElementTag.setName("originalElementID - WILL BE DELETED");
    }

    /**
     * Reset all the internal variables
     */
    public void reset() {
        initAllInternalVariables(CLONED_ELEMENT_SUFFIX);
    }

    //------------------------------------ PRECONFIGURED COPY METHODS --------------------------------------------------

    /**
     * Make a deep copy of the provided port and all its elements, including ports, parts, interfaces, connectors, relationship links.
     * @param port the port to copy
     * @return the map between the original elements and the cloned elements
     */
    public Map<Element, Element> clonePort(Port port) {
        reset();
        setOriginalElementToClone(port);
        addAllElementsToCopy(getPortElementToCopy(port));
        addAllElementsToCopy(elementGetter.getAllConnectorsFromPort(port));

        List<Port> list = elementGetter.getAllNestedPortFromPort(port);
        List<Connector> connectorList = elementGetter.getAllConnectorsFromPorts(list);
        addAllElementsToCopy(connectorList);
        addAllElementsToCopy(elementGetter.getAllRelationFromConnectors(connectorList));

        removeAllFilteredElements();

        cloneElements(port.getOwner());

        fixAllCopiedConnectors();

        return getOrignialClonedMap();
    }

    /**
     * Make a deep copy of the provided property and all its elements, including ports, parts, interfaces, connectors, relationship links.
     * @param property the property to copy
     * @return the map between the original elements and the cloned elements
     */
    public Map<Element, Element> cloneProperty(Property property) {
        try {
            reset();
            setOriginalElementToClone(property);
            addAllElementsToCopy(getPropertyElementToCopy(property));
            addAllElementsToCopy(getTypeElementsToCopy(property.getType()));

            addAllElementsToCopy(elementGetter.getAllRelationFromConnectors(getAllConnectorsToCopy()));
            removeAllFilteredElements();

            cloneElements(property.getOwner());
        }catch (Exception e){
            LegacyErrorHandler.handleException(new CloneFailedException("Error while cloning property: " + property.getHumanName(), e, GenericException.ECriticality.CRITICAL), true);
        }
        return getOrignialClonedMap();
    }

    /**
     * Make a deep copy of the provided part and all its elements, including ports, parts, interfaces, connectors, relationship links.
     * @param part the part to copy
     * @return the map between the original elements and the cloned elements
     */
    public Map<Element, Element> clonePart(Property part) {
        try {
            reset();
            setOriginalElementToClone(part);
            addAllElementsToCopy(getPartElementToCopy(part));
            addAllElementsToCopy(getTypeElementsToCopy(part.getType()));
            addAllElementsToCopy(elementGetter.getAllRelationFromConnectors(getAllConnectorsToCopy()));
            removeAllFilteredElements();

            cloneElements(part.getOwner());

            fixAllCopiedConnectors();
        }catch (Exception e){
            LegacyErrorHandler.handleException(new CloneFailedException("Error while cloning part: " + part.getHumanName(), e, GenericException.ECriticality.CRITICAL), true);
        }

        return getOrignialClonedMap();
    }

    /**
     * Remove all the elements according to the filters: metaClassToFilter and stereotypeToFilter.
     */
    public void removeAllFilteredElements() {
        elementsToCopy = new HashSet<>(filterElements(elementsToCopy));
    }

    /**
     * Remove all the elements according to the filters: metaClassToFilter and stereotypeToFilter.
     * @param elements the elements to filter
     * @return the filtered elements
     */
    private List<Element> filterElements(Collection<Element> elements) {
        Predicate<Element> hasMetaClassToFilter = element -> metaClassToFilter.stream()
                .noneMatch(metaClass -> metaClass.isInstance(element));

        Predicate<Element> hasStereotypeToFilter = element -> stereotypeToFilter.stream().noneMatch(stereotype ->
                StereotypesHelper.hasStereotypeOrDerived(element, stereotype));

        return elements.stream()
                .filter(hasMetaClassToFilter)
                .filter(hasStereotypeToFilter)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Remove all the elements according to the filters: metaClassToFilter and stereotypeToFilter.
     * @param elementToCheck the element to check
     * @return true if the element is not filtered, false otherwise
     */
    private boolean isNOTFilteredElement(Element elementToCheck) {
        Predicate<Element> hasMetaClassToFilter = element -> metaClassToFilter.stream()
                .noneMatch(metaClass -> metaClass.isInstance(element));

        Predicate<Element> hasStereotypeToFilter = element -> stereotypeToFilter.stream().noneMatch(stereotype ->
                StereotypesHelper.hasStereotypeOrDerived(element, stereotype));

        return hasMetaClassToFilter.and(hasStereotypeToFilter).test(elementToCheck);
    }

    /**
     * Make a deep copy of the provided type and all its elements, including ports, parts,
     * interfaces, connectors, relationship links.
     * @param type the type to copy
     * @return the map between the original elements and the cloned elements
     */
    public Map<Element, Element> cloneType(Type type) {
        try {
            reset();
            setOriginalElementToClone(type);
            addAllElementsToCopy(getTypeElementsToCopy(type));

            removeAllFilteredElements();

            cloneElements(type.getOwner());

            fixAllCopiedConnectors();
        }catch (Exception e){
            LegacyErrorHandler.handleException(new CloneFailedException("Error while cloning type: " + type.getHumanName(), e, GenericException.ECriticality.CRITICAL), true);
        }
        return getOrignialClonedMap();
    }


    /**
     * Make a deep copy of the provided connector and all relationship links.
     * @param connector the connector to copy
     * @return the map between the original elements and the cloned elements
     */
    public Map<Element, Element> cloneConnector(Connector connector) {
        reset();
        setOriginalElementToClone(connector);
        addElementToCopy(connector);
        removeAllFilteredElements();

        addAllElementsToCopy(elementGetter.getAllRelationFromConnector(connector));

        cloneElements(connector.getOwner());

        fixAllCopiedConnectors();
        return getOrignialClonedMap();
    }


    //------------------------------------ ADD ELEMENTS TO COPY---------------------------------------------------------

    /**
     * Clone all the Elements inside the owner, and rename them with the PREFIX_CLONE
     * Careful, the cloned elements are not yet fixed (ownership, property path, ...), and filtered (metaClassToFilter, stereotypeToFilter) are not applied
     * @param owner the owner of the elements
     * @return the list of copied elements
     */
    public List<Element> cloneElements(Element owner) {
        List<Element> listElementToClone = new ArrayList<>(elementsToCopy);
        tagsElementForCopy(listElementToClone);
        clonedElements = CopyPasting.copyPasteElements(listElementToClone, owner);
        buildClonedElementMap();

        setSuffix(clonedElements);
        setOwnerCopiedElementOwnerShip();

        removeTMPElementTag();
        return clonedElements;
    }

    private void removeTMPElementTag() {
        try {
            ModelElementsManager.getInstance().removeElement(originalElementTag);
        } catch (ReadOnlyElementException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the owner of the copied elements to the owner of the original elements
     * As the CopyPasting.copyPasteElements() method put each copied element in a given owner, this method will reset the ownership
     */
    public void setOwnerCopiedElementOwnerShip() {
        clonedElements.forEach(element -> {
            Element originalElement = retrieveOriginalElement(element);
            Element newOwner = originalElement.getOwner();
            if(retrieveClonedElement(newOwner) != null) {//If parent was copied, set the new owner to the copied one
                newOwner = retrieveClonedElement(newOwner);
            }

            element.setOwner(newOwner);
        });
    }

    /**
     * Set the suffix to all the copied elements
     * @param copiedElements the list of copied elements
     */
    public void setSuffix(List<Element> copiedElements) {
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
        Set<Element> elementsToCopy = getDeepCopyClassifier(type);
//        Set<Element> elementsToCopy = new HashSet<>(elementGetter.getRelationshipsFromElement(type));
//        elementsToCopy.add(type);

        Set<Connector> collect = type.getOwnedElement().stream()
                .filter(Port.class::isInstance)
                .map(Port.class::cast)
                .filter(this::isNOTFilteredElement)
                .map(port -> {
                    List<Port> portsToClone = elementGetter.getAllNestedPortFromPort(port);
                    portsToClone.add(port);
                    return portsToClone;
                })
                .flatMap(Collection::stream)
                .filter(this::isNOTFilteredElement)
                .map(elementGetter::getAllConnectorsFromPort)
                .flatMap(Collection::stream)
                .filter(this::isNOTFilteredElement)
                .collect(Collectors.toSet());
        addAllElementsToCopy(collect);

        return elementsToCopy;
    }

    /**
     * Get all the Part elements to copy, including the relationship links.
     * It retrieves deeply the classifier of the part.
     * @param part the part to get the elements from
     * @return the part elements
     */
    public List<Element> getPartElementToCopy(Property part) {return filterElements(getPropertyElementToCopy(part));
    }

    /**
     * Get all the property elements to copy, including the relationship links.
     * It retrieves deeply the classifier of the property.
     * @param property the property to get the elements from
     * @return the property elements
     */
    public List<Element> getPropertyElementToCopy(Property property) {
        List<Element> elementsToCopy = filterElements(elementGetter.getRelationshipsFromElement(property));
        addAllElementsToCopy(elementGetter.getAllConnectorsFromProperty(property));
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
        List<Element> elementsToCopy = filterElements(elementGetter.getRelationshipsFromElement(port));
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
    public Set<Element> getDeepCopyClassifier(Type type) {
        Collection<Element> ownedElement = type.getOwnedElement();
        Set<Element> deepCopyElements = Stream.concat(
                        ownedElement.stream()
                                .filter(TypedElement.class::isInstance)
                                .filter(this::isNOTFilteredElement)
                                .map(TypedElement.class::cast)
                                .map(TypedElement::getType)
                                .filter(Objects::nonNull)
                                .filter(this::isNOTFilteredElement)
                                .map(this::getDeepCopyClassifier)
                                .flatMap(Collection::stream),
                        ownedElement.stream()
                                .map(elementGetter::getRelationshipsFromElement)
                                .flatMap(Collection::stream))
                                .filter(this::isNOTFilteredElement)
                .collect(Collectors.toSet());
        deepCopyElements.addAll(filterElements(elementGetter.getRelationshipsFromElement(type)));
        deepCopyElements.add(type);
        return deepCopyElements;
    }




    //------------------------------------ FIXING CLONED ELEMENTS ------------------------------------------------------
    /**
     * Fix all the cloned elements (Ownership, property path, ...)
     */
    public void fixAllClonedElements() {
        //TODO: fix ownership of each element
        //TODO: call fixCopiedConnectors
    }
    /**
     * Fix all the cloned connectors paths.
     * It will fix the ownership of the connectors and the property path of the connector ends.
     * (When copying a connector, the property path of the connector is set by MagicDraw depending on the copy element list,
     * if a port is copied with its connector, the property path of the connector will be set to the copied port: OK.
     * But if there is a nested port from the same interface with the destination,
     * the destination propertyPathWill be affected, thus the connector will be broken)
     */
    public void fixAllCopiedConnectors() {
        getClonedElements().stream()
                .filter(Connector.class::isInstance)
                .map(Connector.class::cast)
                .forEach(this::fixClonedConnector);

    }
    /**
     *
     * Fix all the cloned connectors paths.
     * It will fix the ownership of the connectors and the property path of the connector ends.
     * (When copying a connector, the property path of the connector is set by MagicDraw depending on the copy element list,
     * if a port is copied with its connector, the property path of the connector will be set to the copied port: OK.
     * But if there is a nested port from the same interface with the destination,
     * the destination propertyPathWill be affected, thus the connector will be broken)
     * @param connectorList the list of connectors to fix
     */
    public void fixCopiedConnectors(List<Connector> connectorList) {
        connectorList.forEach(this::fixClonedConnector);

    }

    /**
     * Fix the cloned connector paths.
     * It will fix the ownership of the connector and the property path of the connector ends.
     * (When copying a connector, the property path of the connector is set by MagicDraw depending on the copy element list,
     * if a port is copied with its connector, the property path of the connector will be set to the copied port: OK.
     * But if there is a nested port from the same interface with the destination,
     * the destination propertyPathWill be affected, thus the connector will be broken
     * @param clonedConnector the cloned connector to fix
     */
    public void fixClonedConnector(Connector clonedConnector) {
        Connector originalConnector = (Connector) retrieveOriginalElement(clonedConnector);

        clonedConnector.setOwner(originalConnector.getOwner()); //FIX Ownership

        int iEndToFix = getEndToFix(clonedConnector);
        if (iEndToFix == -1) return;
        ConnectorEnd endToFix = clonedConnector.getEnd().get(iEndToFix);

        //Fix property path
        ConnectorEnd originalEnd = originalConnector.getEnd().get(iEndToFix);
        List<Element> propertyPath = Profile._getSysml().elementPropertyPath().getPropertyPath(originalEnd);
        Profile._getSysml().elementPropertyPath().setPropertyPath(endToFix, propertyPath);

        //fixPartWithPort
        endToFix.setPartWithPort(originalEnd.getPartWithPort());
        //Fix role
        endToFix.setRole(originalEnd.getRole());
    }

    public Element retrieveOriginalElement(Element clonedElement) {
        return reversedMap.get(clonedElement);
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
//        clonedElements.forEach(clonedElement ->
//                taggedElementForCopy.put(clonedElement.getSyncElement(), clonedElement));

//        getElementsToCopy()
//                .forEach(originalElement -> orignialClonedMap.put(originalElement, retrieveClonedElementFromTag(originalElement)));
        getClonedElements()
            .forEach(originalElement -> orignialClonedMap.put(getElementFromTag(originalElement), originalElement));
        reversedMap = MapUtils.invertMap(orignialClonedMap);
    }

    private Element getElementFromTag(Element clonedElement) {
        Optional<TaggedValue> taggedValue = clonedElement.getTaggedValue().stream()
                .filter(tag -> tag.getTagDefinition() == originalElementTag)
                .findAny();

        String originalID = (String) taggedValue.get().getValue().get(0);

        return (Element) OMFUtils.getProject().getElementByID(originalID);
    }

    /**
     * Tag the elements to copy using synchElement to be able to retrieve them later
     * It is temporary using the stereotypes from SysML to tag the elements.
     * //TODO Store first then set back the syncElement property
     * @param elements the elements to tag
     */
    private void tagsElementForCopy(Collection<? extends Element> elements) {

        for (Element element : elements) {
//            Element tagElement = getNextElementToTag(elementsToTagRef);
            tagElementForCopy(element);
        }
    }

    private Element getNextElementToTag(List<Element> elementsToTagRef) {
        return elementsToTagRef.get(iTaggedElement++);
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
     * Tag the element to copy using synchElement to be able to retrieve it later
     * @param originalElement the original element
     */
    private void tagElementForCopy(Element originalElement) {
        ElementsFactory factory = OMFFactory.getInstance().getMagicDrawFactory();
        StringTaggedValue stringTaggedValue = factory.createStringTaggedValueInstance();
        stringTaggedValue.setTagDefinition(originalElementTag);
        stringTaggedValue.getValue().add(originalElement.getID());
        stringTaggedValue.setOwner(originalElement);
//        taggedElementForCopy.put(tagElement, null);
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

        if (copiedElement != null) {
            copiedElement.setSyncElement(null);
        }
        return copiedElement;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get all the connectors to copy
     * @return the connectors to copy
     */
    public List<Connector> getAllConnectorsToCopy(){
        return elementsToCopy.stream()
                .filter(Connector.class::isInstance)
                .map(Connector.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Add all the elements to copy to the copy list
     * @param elements the elements to add
     */
    public void addAllElementsToCopy(Collection<? extends Element> elements) {
        elements.forEach(this::addElementToCopy);
    }

    /**
     * Add the element to the copy list, if it is not filtered by the metaClassToFilter and stereotypeToFilter
     * @param elementToCopy the element to add
     */
    public void addElementToCopy(Element elementToCopy) {
        if(isNOTFilteredElement(elementToCopy)) elementsToCopy.add(elementToCopy);
    }

    /**
     * add Classes to filter, the elements of these classes will not be copied
     * @param metaClasses: each metaClass to add to the filter
     */
    public void addMetaClassesToFilter(Class<? extends Element>... metaClasses) {
        metaClassToFilter.addAll(Arrays.asList(metaClasses));
    }

    /**
     * add all Classes to filter, the elements of these classes will not be copied
     * @param metaClassList the list of classes to filter
     */
    public void addMetaClassesToFilter(List<java.lang.Class<? extends Element>> metaClassList) {
        metaClassToFilter.addAll(metaClassList);
    }

    /**
     * add a Class to filter, the elements of this class will not be copied
     * @param metaClass the class to filter
     */
    public void addMetaClassToFilter(java.lang.Class<? extends Element> metaClass) {
        metaClassToFilter.add(metaClass);
    }

    /**
     * add all Stereotypes to filter, the elements with one of these stereotypes will not be copied
     * @param stereotypeList the list of stereotypes to filter
     */
    public void addStereotypesToFilter(List<Stereotype> stereotypeList) {
        stereotypeToFilter.addAll(stereotypeList);
    }

    /**
     * add a Stereotype to filter, the elements with this stereotype will not be copied
     * @param stereotype the stereotype to filter
     */
    public void addStereotypeToFilter(Stereotype stereotype) {
        stereotypeToFilter.add(stereotype);
    }

    /**
     * Get the elements to copy
     * @return the elements to copy
     */
    public Set<Element> getElementsToCopy() {
        return elementsToCopy;
    }

    /**
     * Set the elements to copy
     * @param elementsToCopy the elements to copy
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

    public Map<Element, Element> getOrignialClonedMap() {
        return orignialClonedMap;
    }

    public List<Element> getClonedElements() {
        return clonedElements;
    }

    public ElementGetter getElementGetter() {
        return elementGetter;
    }

    public Map<Element, Element> getTaggedElementForCopy() {
        return taggedElementForCopy;
    }

    public void setTaggedElementForCopy(Map<Element, Element> taggedElementForCopy) {
        this.taggedElementForCopy = taggedElementForCopy;
    }

//    public List<Element> getTagsList() {
//        return tagsList;
//    }
//
//    public void setTagsList(List<Element> tagsList) {
//        this.tagsList = tagsList;
//    }

    public void setOriginalClonedMap(Map<Element, Element> orignialClonedMap) {
        this.orignialClonedMap = orignialClonedMap;
    }

    public Map<Element, Element> getReversedMap() {
        return reversedMap;
    }

    public void setReversedMap(Map<Element, Element> reversedMap) {
        this.reversedMap = reversedMap;
    }

    public int getITaggedElement() {
        return iTaggedElement;
    }

    public void setITaggedElement(int iTaggedElement) {
        this.iTaggedElement = iTaggedElement;
    }

    public void setClonedElements(List<Element> clonedElements) {
        this.clonedElements = clonedElements;
    }

    public void setElementGetter(ElementGetter elementGetter) {
        this.elementGetter = elementGetter;
    }
}
