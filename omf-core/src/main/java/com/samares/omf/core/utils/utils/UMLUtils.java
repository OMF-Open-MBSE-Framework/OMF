package com.samares.omf.core.utils.utils;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.errors.exceptions.GenericException;
import com.samares.omf.core.errors.exceptions.OMFException;

import java.util.List;

public class UMLUtils {
    private UMLUtils() {}

    public static List<Class> getStereotypeMetaClass(Stereotype str) {
        return StereotypesHelper.getBaseClasses(str);
    }

    public static boolean isStereotypeMetaClassMatchClass(Stereotype str, java.lang.Class clazz) {
        return getStereotypeMetaClass(str).stream()
                .anyMatch(metaClass -> metaClass == StereotypesHelper.getMetaClassByClass(OMFUtils.currentProject, clazz));
    }

    public static boolean isInstanceOfMetaClass(Element type, java.lang.Class metaClass) throws OMFException {
        try {
            if (type == null)
                return false;

            if (type instanceof Stereotype)
                return isStereotypeMetaClassMatchClass((Stereotype) type, metaClass);

            return type instanceof NamedElement && ((NamedElement) type).getName().equals(metaClass.getSimpleName());
        } catch (Exception e) {
            throw new OMFException("Impossible to determine MetaClass of selected type",
                    e, GenericException.ECriticality.CRITICAL);
        }
    }
}
