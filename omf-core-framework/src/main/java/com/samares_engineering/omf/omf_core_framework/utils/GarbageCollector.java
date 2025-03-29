/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils;

import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;

import java.util.ArrayList;
import java.util.Collection;

// TODO Check if this class is still useful
public class GarbageCollector {

    public static void collectGarbage() {
        removeAllUnusedCreatedType();
    }

    public static void removeAllUnusedCreatedType() {
        Collection<Element> toRemove = new ArrayList<>();

        AllCreatedElements.allInterfaceBlock.stream()
                .filter(interfaceType -> interfaceType.get_typedElementOfType().size() == 0)
                .forEach(interfaceType -> toRemove.add(interfaceType));

        toRemove.stream()
                .forEach(interfaceType -> {
                    AllCreatedElements.allInterfaceBlock.remove(interfaceType);
                    try {
                        ModelElementsManager.getInstance().removeElement(interfaceType);
                    } catch (ReadOnlyElementException e) {
                        throw new OMFCriticalException("Can't delete readonly element in garbage collector", e);
                    }
                });
    }


}
