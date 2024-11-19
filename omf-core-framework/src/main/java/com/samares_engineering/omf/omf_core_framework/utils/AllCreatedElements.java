/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.SysoutColorPrinter;

import java.util.*;

public class AllCreatedElements {

    public static Set<Port> allPorts = new HashSet<>();
    public static Set<Class> allInterfaceBlock = new HashSet<>();

    public static List<Set<? extends Cloneable>> allElementSets = Arrays.asList(
            allPorts,
            allInterfaceBlock
    );


    public static void registerCreatedElement(Element createdElement) {
        SysoutColorPrinter.warn("[AllCreatedElements] Unknown case: " + createdElement.getHumanName());
    }

    public static void registerCreatedElements(Collection<Element> createdElements) {
        createdElements.stream().forEach(AllCreatedElements::registerCreatedElement);
    }

    public static void emptyAll() {
        allElementSets.stream().forEach(Set::clear);
    }
}
