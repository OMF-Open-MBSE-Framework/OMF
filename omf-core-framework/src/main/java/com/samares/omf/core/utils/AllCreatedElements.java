/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares.omf.core.errors.OMFErrorHandler;

import java.util.*;

public class AllCreatedElements {

    public static Set<Port> allPorts = new HashSet<>();
    public static Set<Class> allInterfaceBlock = new HashSet<>();

    public static List<Set<? extends Cloneable>> allElementSets = Arrays.asList(
            allPorts,
            allInterfaceBlock
    );


    public static void registerCreatedElement(Element createdElement) {
        try {

            ColorPrinter.warn("[AllCreatedElements] Unknown case: " + createdElement.getHumanName());
        } catch (Exception e) {
            OMFErrorHandler.handleException(e, false);
        }
    }

    public static void registerCreatedElements(Collection<Element> createdElements) {
        createdElements.stream().forEach(AllCreatedElements::registerCreatedElement);
    }

    public static void emptyAll() {
        allElementSets.stream().forEach(Set::clear);
    }
}
