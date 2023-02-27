/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.utils;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectableElement;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

import java.util.*;
import java.util.stream.Collectors;

public class OMFUtils {
    public static Project currentProject = null;

    /**
     * Split a String with regex given as parameter
     *
     * @param lineToSplit String
     * @param regex       String
     * @return List<String>
     */
    public static List<String> getValuesWithinLine(String lineToSplit, String regex) {
        assert lineToSplit != null;
        assert regex != null;
        List<String> result = new ArrayList<>();
        String[] strArray = null;

        if (lineToSplit.matches(regex)) {
            strArray = lineToSplit.split(regex);
            Collections.addAll(result, strArray);
        } else {
            result.add(lineToSplit);
        }
        return result;
    }

    public static List<Property> getPropertyListFromElementList(List<Element> elementList) {
        return elementList.stream()
                .filter(Objects::nonNull)
                .filter(Property.class::isInstance)
                .map(Property.class::cast)
                .collect(Collectors.toList());
    }


    /**
     * Gets all parts in context.
     *
     * @param currentElement       the current element
     * @param listAllPartInContext the list all part in context
     * @return the all parts in context
     */
    public static List<Property> getAllPartsInContext(Class currentElement, List<Property> listAllPartInContext) {
        if (null == listAllPartInContext)
            listAllPartInContext = new ArrayList();

        List<Property> properties = currentElement.getOwnedAttribute().stream()
                .filter(Profile.getSysmlAdditionalStereotypes().partProperty()::is)
                .filter(property -> Objects.nonNull(property.getType()))
                .filter(property -> Profile.getInstance().getSysml().block().is(property.getType()))
                .collect(Collectors.toList());

        for (Property p : properties) {
            listAllPartInContext.add(p);
            getAllPartsInContext((Class) p.getType(), listAllPartInContext);
        }

        return listAllPartInContext;
    }

    public static boolean isTypeOut(Type type) {
        return type.getOwnedElement().stream().filter(Property.class::isInstance).anyMatch(flow -> Objects.equals(Profile.getInstance().getSysml().flowProperty().getDirection(flow), SysMLProfile.FlowDirectionKindEnum.OUT));
    }

    public static boolean isTypeIn(Type type) {
        return type.getOwnedElement().stream().filter(Property.class::isInstance).anyMatch(flow -> Objects.equals(Profile.getInstance().getSysml().flowProperty().getDirection(flow), SysMLProfile.FlowDirectionKindEnum.IN));
    }

    public static Property getPartFromPropertyPath(Element partType, List<Property> listPropertyPath) {
        Optional<Property> optPart = listPropertyPath.stream().filter(property -> partType.equals((property).getType())).findFirst();
        return optPart.orElse(null);
    }

    /**
     * Gets get Part In Context.
     *
     * @param partType the part type
     * @return the part
     */
    public static Property getPartInContext(Element partType, List<Property> availableParts) {
        return availableParts.stream()
                .filter(property -> partType.equals((property).getType()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets get Part In Context.
     *
     * @param partType the part type
     * @return the part
     */
    public static Property getPartInContextWithID(Element partType, String id, List<Property> availableParts) {
        return availableParts.stream().filter(property -> partType.equals((property).getType()) && Profile.getInstance().getSysml().block().is(property.getOwner())).iterator().next();
    }

    public static List<Property> getPropertyPathListFromConnectorEnd(ConnectorEnd ce) {
        ConnectableElement end = ce.getRole();
        ArrayList<Element> elementPath = new ArrayList(Profile.getInstance().getSysml().elementPropertyPath().getPropertyPath(ce));
        List<Property> propertyPath = OMFUtils.getPropertyListFromElementList(elementPath);
//

        if (!(end instanceof Port) && end instanceof Property)  //if end == part add it to the list
            propertyPath.add((Property) end);
        else if (null != ce.getPartWithPort() && !(ce.getPartWithPort() instanceof Port))   //could be redundant if end is a part
            propertyPath.add(ce.getPartWithPort());

        return new ArrayList<>(propertyPath);
    }

    public static String getUserDir() {
        return System.getProperty("user.dir");
    }
}