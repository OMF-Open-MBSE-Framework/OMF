/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils;

import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.errors.OMFErrorHandler;

import java.util.ArrayList;
import java.util.Collection;

public class GarbageCollector {

    public static void collectGarbage() {
        removeAllUnusedCreatedType();
    }

    public static void removeAllUnusedCreatedType() {
        Collection<Element> toRemove = new ArrayList<>();

        AllCreatedElements.allInterfaceBlock.stream()
                .filter(fiType -> fiType.get_typedElementOfType().size() == 0)
                .forEach(fiType -> toRemove.add(fiType));

        toRemove.stream()
                .forEach(fiType -> {
                    AllCreatedElements.allInterfaceBlock.remove(fiType);
                    try {
                        ModelElementsManager.getInstance().removeElement(fiType);
                    } catch (ReadOnlyElementException e) {
                        OMFErrorHandler.handleException(e, false);
                    }
                });
    }


}
