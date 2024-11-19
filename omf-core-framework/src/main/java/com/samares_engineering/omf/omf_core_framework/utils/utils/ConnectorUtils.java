/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils.utils;

import com.nomagic.magicdraw.sysml.util.MDCustomizationForSysMLProfile;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectableElement;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.DiagramUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ConnectorUtils {
    private ConnectorUtils() {}

    /**
     * Gets common ancestor.
     *
     * @param part1          the part 1
     * @param part2          the part 2
     * @param untilObject    the until object
     * @param availableParts the available parts
     * @return the common ancestor
     * @throws LegacyOMFException the legacy omf exception
     */
    public static Class getCommonAncestor(Property part1, Property part2, Class untilObject, List<Property> availableParts) throws LegacyOMFException {
        Class commonAncestor = null;
        List<Property> nestedPart1List = oldCalculateNestedPath(new ArrayList<>(), part1, untilObject, availableParts);
        List<Property> nestedPart2List = oldCalculateNestedPath(new ArrayList<>(), part2, untilObject, availableParts);

        for (Property p : nestedPart1List) {
            if (nestedPart2List.contains(p)) {
                commonAncestor = (Class) p.getType();
                return commonAncestor;
            }
        }

        return untilObject;
    }

    public static Element getCommonAncestorFromPropertyPath(List<Property> propertyPathSRC, List<Property> propertyPathDST) {

        if (propertyPathSRC.isEmpty() && propertyPathDST.isEmpty())  // Default;
            return DiagramUtils.getOpenedDiagram().getOwner();


        if (propertyPathSRC.isEmpty()) //Mother port to Son
            return propertyPathDST.get(0).getOwner();


        if (propertyPathDST.isEmpty()) //Son to Mother Port
            return propertyPathSRC.get(0).getOwner();

        boolean isConnectingTwoSiblingsElements = propertyPathSRC.get(0).getOwner() == propertyPathDST.get(0).getOwner();
        if (isConnectingTwoSiblingsElements)
            return propertyPathSRC.get(0).getOwner();

        boolean isMotherToSon = propertyPathSRC.get(0).getType() == propertyPathDST.get(0).getOwner();
        if (isMotherToSon)
            return propertyPathSRC.get(0).getType();

        //isSonToMother
        return propertyPathDST.get(0).getType();
    }

    /**
     * Calculate nested path list.
     *
     * @param nestedPath     the nested path
     * @param currentPart    the current part
     * @param untilObject    the until object
     * @param availableParts the available parts
     * @return the list
     * @throws LegacyOMFException the legacy omf exception
     */
    public static List<Property> oldCalculateNestedPath(List<Property> nestedPath, Property currentPart,
                                                        Class untilObject,
                                                        List<Property> availableParts) throws LegacyOMFException {
        if (untilObject.equals(currentPart.getOwner())) {
            nestedPath.add(currentPart);
            return nestedPath;
        } else if (untilObject.equals(currentPart.getType())) {
            return nestedPath;
        } else {
            nestedPath.add(currentPart);
            Element partOwner = currentPart.getOwner();
            Property nestedPart = availableParts.stream()
                    .filter(property -> partOwner.equals(property.getType()))
                    .findFirst()
                    .orElseThrow();

            if (!availableParts.contains(currentPart))
                throw new LegacyOMFException("[FullConnectionPath]-calculateNestedPath cannot find part: " + currentPart.getHumanName(),
                        GenericException.ECriticality.CRITICAL);

            return calculateNestedPath(nestedPath, nestedPart, untilObject, availableParts);
        }
    }

    public static List<Property> calculateNestedPath(List<Property> nestedPath, Property currentPart, Class untilObject, List<Property> availableParts) {

        if (untilObject.equals(currentPart.getOwner())) {
            nestedPath.add(currentPart);
            return nestedPath;
        } else if (untilObject.equals(currentPart.getType())) {
            return nestedPath;
        } else {
            nestedPath.add(currentPart);
            Element partOwner = currentPart.getOwner();

            Optional<Property> nestedPart = availableParts.stream().filter(property -> partOwner.equals((property).getType())).findFirst();
            if (nestedPart.isEmpty())
                throw new OMFCriticalException("[FullConnectionPath]-calculateNestedPath cannot find part: " + currentPart.getHumanName());

            return calculateNestedPath(nestedPath, nestedPart.get(), untilObject, availableParts);

        }
    }

    public static ConnectableElement getHighestConnectableElementFromConnectorList(List<Connector> inConnectors, Element commonAncestor, List<Property> listPropertyPath) {
        Optional<ConnectorEnd> optCE = inConnectors.stream()
                .map(Connector::getEnd)
                .flatMap(Collection::stream)
                .filter(ce -> ce.getPartWithPort() == null)
                .filter(ce -> commonAncestor.getOwnedElement().contains(OMFUtils.getPartInContext(Objects.requireNonNull(ce.getRole()).getOwner(), listPropertyPath)))
                .findFirst();

        if (optCE.isPresent()) {
            return optCE.get().getRole();
        } else {
            System.err.println("[PART FINDER] SRC Ancestor not found");
            return null;
        }
    }

    public static boolean areFlowPropertyDirectionCompatible(Property a, Property b, boolean isMotherToSon) {
        SysMLProfile.FlowDirectionKindEnum dirA = Profile.getInstance().getSysml().flowProperty().getDirection(a);
        SysMLProfile.FlowDirectionKindEnum dirB = Profile.getInstance().getSysml().flowProperty().getDirection(b);

        if (isMotherToSon) {
            return dirA == dirB;
        }

        if (dirA == dirB && dirA == SysMLProfile.FlowDirectionKindEnum.INOUT)
            return true;

        if (dirA == SysMLProfile.FlowDirectionKindEnum.IN && dirB == SysMLProfile.FlowDirectionKindEnum.OUT)
            return true;

        return dirA == SysMLProfile.FlowDirectionKindEnum.OUT && dirB == SysMLProfile.FlowDirectionKindEnum.IN;
    }

    public static List<Property> getFullPropertyPath(ConnectorEnd end) {
        List<Property> propertyPath = getPropertyPath(end);
        ConnectableElement role = end.getRole();
        if (!(role instanceof Port) && role instanceof Property && !propertyPath.contains(role))  //if end == part add it to the list
            propertyPath.add((Property) role);
        else if (null != end.getPartWithPort() && !(end.getPartWithPort() instanceof Port) && !propertyPath.contains(end.getPartWithPort()))   //could be redundant if end is a part
            propertyPath.add(end.getPartWithPort());
        return propertyPath;
    }



    public static List<Property> getPropertyPath(ConnectorEnd connectorEnd) {
        return Profile._getSysml().elementPropertyPath().getPropertyPath(connectorEnd).stream()
                .filter(Property.class::isInstance)
                .map(Property.class::cast)
                .collect(Collectors.toList());
    }

    public static Property getPartFromPropertyPath(Element partType, List<Property> listPropertyPath) {
        Optional<Property> optPart = listPropertyPath.stream().filter(property -> partType.equals((property).getType())).findFirst();
        return optPart.orElse(null);
    }

    public static List<ConnectableElement> getRoleFromConnector(Connector connector) {
        return connector.getEnd().stream()
                .map(ConnectorEnd::getRole)
                .collect(Collectors.toList());
    }
    public static List<Property> getPartsFromConnector(Connector connector) {
        ConnectorEnd end1 = connector.getEnd().get(0);
        ConnectorEnd end2 = connector.getEnd().get(1);
        ArrayList<Property> parts = new ArrayList<>();

        MDCustomizationForSysMLProfile.PartPropertyStereotype partStr = Profile._getSysmlAdditionalStereotypes().partProperty();
        if (partStr.is(end1.getRole())) parts.add((Property) end1.getRole());
        if (partStr.is(end2.getRole())) parts.add((Property) end2.getRole());

        if (partStr.is(end1.getPartWithPort())) parts.add(end1.getPartWithPort());
        if (partStr.is(end2.getPartWithPort())) parts.add(end2.getPartWithPort());

        return parts;
    }
}
