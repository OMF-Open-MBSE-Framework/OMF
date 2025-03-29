/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.utils;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectableElement;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.NoElementFoundException;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

import java.util.*;
import java.util.stream.Collectors;

public class OMFUtils {
    public static Project getProject() {
        return Application.getInstance().getProject();
    }

    public static boolean isProjectVoid() {
        return Application.getInstance().getProject() == null;
    }


    public static boolean isProjectOpened() {
        return Application.getInstance().getProject() != null;
    }

    /**
     * Split a String with regex given as parameter
     *
     * @param lineToSplit String
     * @param regex       String
     * @return List of String
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
                .filter(Profile._getSysmlAdditionalStereotypes().partProperty()::is)
                .filter(property -> Objects.nonNull(property.getType()))
                .filter(property -> Profile.getInstance().getSysml().block().is(property.getType()))
                .collect(Collectors.toList());

        for (Property p : properties) {
            listAllPartInContext.add(p);
            getAllPartsInContext((Class) p.getType(), listAllPartInContext);
        }

        return listAllPartInContext;
    }

    /**
     * return true if all flow properties of the type are OUT;
     * @param type the type
     * @return true if all flow properties of the type are OUT
     */
    public static boolean isTypeOut(Type type) {
        return type.getOwnedElement().stream().filter(Property.class::isInstance).anyMatch(flow -> Objects.equals(Profile.getInstance().getSysml().flowProperty().getDirection(flow), SysMLProfile.FlowDirectionKindEnum.OUT));
    }

    /**
     * return true if all flow properties of the type are IN;
     * @param type the type
     * @return true if all flow properties of the type are IN
     */
    public static boolean isTypeIn(Type type) {
        return type.getOwnedElement().stream().filter(Property.class::isInstance).anyMatch(flow -> Objects.equals(Profile.getInstance().getSysml().flowProperty().getDirection(flow), SysMLProfile.FlowDirectionKindEnum.IN));
    }


    public static Property getPartFromPropertyPath(Element partType, List<Property> listPropertyPath) {
        Optional<Property> optPart = listPropertyPath.stream().filter(property -> partType.equals((property).getType())).findFirst();
        return optPart.orElse(null);
    }

    /**
     * Gets get all parts typed by partType in a given context.
     * @param partType the part type
     * @param availableParts the available parts
     * @return the part
     * @deprecated This method sustains legacy code and will be removed in the near future.
     */
//    @Deprecated(since = "1.0.0", forRemoval = true)
    @Deprecated
    public static Property getPartInContext(Element partType, List<Property> availableParts) {
        return availableParts.stream()
                .filter(property -> partType.equals((property).getType()))
                .findFirst()
                .orElse(null);
    }


    /**
     * Compute the property path from a connector end, including the partWithPort if it is not a port.
     * @param ce the connector end
     * @return the property path
     */
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

    /**
     * Select an element in the containment tree by its id.
     * MagicDraw containment tree shall be accessible in the API.
     * @param id id of the element to select
     * @throws NoElementFoundException if the element is not found
     */
    public static void selectElementInContainmentTree(String id) throws NoElementFoundException {
        BaseElement element = OMFUtils.getProject().getElementByID(id);
        if(element == null)
            throw new NoElementFoundException("[API SELECT ELEMENT] ELEMENT NOT FOUND WITH ID: " + id);

        selectElementInContainmentTree(element);
    }


    /**
     * Select an element in the containment tree.
     * MagicDraw containment tree shall be accessible in the API.
     * @param element: element to select
     */
    public static void selectElementInContainmentTree(BaseElement element) {
        try {
            new ElementAction((Element) element).selectInBrowser();
        }catch (Exception e){
            throw new OMFCriticalException("SelectElementInContainmentTree failed cause: MagicDraw containment tree is not accessible", e);
        }
    }


    public static void openSpecification(String id) {
        try {
            BaseElement element = OMFUtils.getProject().getElementByID(id);
            if(element == null)
                throw new NoElementFoundException("[API OPEN SPECIFICATION] ELEMENT NOT FOUND WITH ID: " + id);
            openSpecification(element);
        }catch (NoElementFoundException e){
            throw new OMFCriticalException("OpenSpecification failed cause: Element not found", e);
        }
    }

    public static void openSpecification(BaseElement element) {
        try {
            new ElementAction((Element) element).openSpecification();
        }catch (Exception e){
            throw new OMFCriticalException("OpenSpecification failed cause: MagicDraw containment tree is not accessible", e);
        }
    }

    public static DiagramPresentationElement getActiveDiagram() {
        return getProject().getActiveDiagram();
    }

    public static boolean isDevMode() {
        return Objects.equals(System.getProperty("DEVELOPER"), "true");
    }

    public static boolean isTestMode() {
        return Objects.equals(System.getProperty("test"), "true");
    }
}