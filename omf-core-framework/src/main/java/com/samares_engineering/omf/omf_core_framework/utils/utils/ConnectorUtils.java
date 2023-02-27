/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils.utils;

import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectableElement;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFException;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.DiagramUtils;

import java.util.*;

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
     */
    public static Class getCommonAncestor(Property part1, Property part2, Class untilObject, List<Property> availableParts) throws OMFException {
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
     */
    public static List<Property> oldCalculateNestedPath(List<Property> nestedPath, Property currentPart,
                                                        Class untilObject,
                                                        List<Property> availableParts) throws OMFException {
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
                throw new OMFException("[FullConnectionPath]-calculateNestedPath cannot find part: " + currentPart.getHumanName(),
                        GenericException.ECriticality.CRITICAL);

            return calculateNestedPath(nestedPath, nestedPart, untilObject, availableParts);
        }
    }

    public static List<Property> calculateNestedPath(List<Property> nestedPath, Property currentPart, Class untilObject, List<Property> availableParts) throws OMFException {

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
                throw new OMFException("[FullConnectionPath]-calculateNestedPath cannot find part: " + currentPart.getHumanName(), GenericException.ECriticality.CRITICAL);

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
}
