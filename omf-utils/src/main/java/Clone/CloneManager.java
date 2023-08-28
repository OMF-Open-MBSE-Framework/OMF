package Clone;

import com.nomagic.magicdraw.copypaste.CopyPasting;
import com.nomagic.magicdraw.uml2.Connectors;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;

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
    }


    /**
     * Clone all the Elements inside the owner, and rename them with the PREFIX_CLONE
     * @param owner the owner of the elements
     * @return the list of copied elements
     */
    public List<Element> cloneElements(Element owner) {
        List<Element> listElementToClone = elementsToCopy.stream().collect(Collectors.toList());
        List<Element> copiedElements = CopyPasting.copyPasteElements(listElementToClone, owner);

        copiedElements.stream()
                .filter(NamedElement.class::isInstance)
                .map(NamedElement.class::cast)
                .forEach(namedElement -> namedElement.setName(namedElement.getName() + CLONED_ELEMENT_SUFFIX));
        return copiedElements;
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
    public void setOriginalElementToClone(Property originalElement) {
        originalElement.setSyncElement(originalElement.getOwner());
    }

    /**
     * Get the all the connector elements from the part, including the relationship links.
     * @param part the part to get the connector elements from
     * @return the connector elements
     */
    public Collection<? extends Element> getConnectorElementsFromPart(Property part) {
        Element sharedOwner = part.getOwner();
        Collection<Connector> connectors = part.get_connectorEndOfPartWithPort().stream()
                .map(ConnectorEnd::get_connectorOfEnd)
                .filter(connector -> sharedOwner == connector.getOwner())
                .collect(Collectors.toList());

        return getConnectorElements(connectors);
    }


    /**
     * Get the all the connector elements from the port, including the relationship links.
     * @param port the port to get the connector elements from
     * @return the connector elements
     */
    public Collection<? extends Element> getConnectorFromPort(Port port) {
        Element sharedOwner = port.getOwner();
        Collection<Connector> connectors = Connectors.collectConnectors(port).stream()
                .filter(connector -> sharedOwner == connector.getOwner())
                .collect(Collectors.toList());
        return getConnectorElements(connectors);
    }

    /**
     * Get All connector elements from the connector, including the relationship links.
     * @param connector the connector to get the elements from
     * @return the connector elements
     */
    public List<Element> getConnectorElements(Connector connector) {
        List<Element> connectorElements = getRelationshipsFromElement(connector);
        connectorElements.add(connector);
        return connectorElements;
    }

    /**
     * Get All connector elements from the connectors, including the relationship links.
     * @param connectors the connectors to get the elements from
     * @return the connector elements
     */
    public List<Element> getConnectorElements(Collection<Connector> connectors) {
        return Stream.concat(
                        connectors.stream()
                                .map(this::getRelationshipsFromElement)
                                .flatMap(Collection::stream),
                        connectors.stream())
                .collect(Collectors.toList());
    }

    /**
     * Get all Type Elements to copy, including the relationship links.
     * @param type the type to get the elements from
     * @return the type elements
     */
    public Collection<? extends Element> getTypeElementsToCopy(Type type) {
        if(type == null) return Collections.emptyList();
        List<Element> elementsToCopy = new ArrayList<>(getRelationshipsFromElement(type));
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
        List<Element> elementsToCopy = new ArrayList<>(getRelationshipsFromElement(part));
        elementsToCopy.add(part);
        Type type = part.getType();
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
                                .map(this::getRelationshipsFromElement)
                                .flatMap(Collection::stream))
                .collect(Collectors.toList());
        deepCopyElements.addAll(getRelationshipsFromElement(type));
        deepCopyElements.add(type);
        return deepCopyElements;
    }

    /**
     * Get deeply all the classifier elements to copy, including the relationship links.
     * @param port the port to get the elements from
     * @return the classifier elements
     */
    public List<Element> getPortElementToCopy(Port port) {
        List<Element> elementsToCopy = new ArrayList<>(getRelationshipsFromElement(port));
        elementsToCopy.add(port.getType());
        elementsToCopy.add(port);
        return elementsToCopy;
    }

    /**
     * Get all the relationship links from the element (source and target)
     * @param element
     * @return
     */
    public List<Element> getRelationshipsFromElement(Element element) {
        List<Element> traceability = new ArrayList<>(element.get_directedRelationshipOfSource());
        traceability.addAll(element.get_directedRelationshipOfTarget());
        return traceability;
    }

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



}
