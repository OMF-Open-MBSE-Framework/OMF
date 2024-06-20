/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils.utils;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.List;

public class UMLUtils {
    private UMLUtils() {}

    public static List<Class> getStereotypeMetaClass(Stereotype str) {
        return StereotypesHelper.getBaseClasses(str);
    }

    public static boolean isStereotypeMetaClassMatchClass(Stereotype str, java.lang.Class clazz) {
        return getStereotypeMetaClass(str).stream()
                .anyMatch(metaClass -> metaClass == StereotypesHelper.getMetaClassByClass(OMFUtils.getProject(), clazz));
    }

    public static boolean isInstanceOfMetaClass(Element type, java.lang.Class metaClass) throws LegacyOMFException {
        try {
            if (type == null)
                return false;

            if (type instanceof Stereotype)
                return isStereotypeMetaClassMatchClass((Stereotype) type, metaClass);

            return type instanceof NamedElement && ((NamedElement) type).getName().equals(metaClass.getSimpleName());
        } catch (Exception e) {
            throw new LegacyOMFException("Impossible to determine MetaClass of selected type",
                    e, GenericException.ECriticality.CRITICAL);
        }
    }
}
