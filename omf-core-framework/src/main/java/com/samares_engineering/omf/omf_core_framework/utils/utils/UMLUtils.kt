/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_core_framework.utils.utils

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_core_framework.utils.utils.UMLUtils.getAllDerivedProperties
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

object UMLUtils {
    @JvmStatic
    fun getStereotypeMetaClass(str: Stereotype?): List<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class> {
        return StereotypesHelper.getBaseClasses(str)
    }

    fun isStereotypeMetaClassMatchClass(str: Stereotype?, clazz: Class<*>?): Boolean {
        return getStereotypeMetaClass(str).stream()
            .anyMatch { metaClass: com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class ->
                metaClass === StereotypesHelper.getMetaClassByClass(
                    OMFUtils.getProject(),
                    clazz
                )
            }
    }

    @JvmStatic
    @Throws(LegacyOMFException::class)
    fun isInstanceOfMetaClass(type: Element?, metaClass: Class<*>): Boolean {
        try {
            if (type == null) return false

            if (type is Stereotype) return isStereotypeMetaClassMatchClass(type as Stereotype?, metaClass)

            return type is NamedElement && type.name == metaClass.simpleName
        } catch (e: Exception) {
            throw LegacyOMFException(
                "Impossible to determine MetaClass of selected type",
                e, GenericException.ECriticality.CRITICAL
            )
        }
    }

    @JvmStatic
    fun getAllMetaClasses(stereotypes: Collection<Stereotype>): List<Class<*>> {
        return  stereotypes.asSequence()
            .map { str: Stereotype -> UMLUtils.getStereotypeMetaClass(str) }
            .flatten()
            .distinct()
            .filter(Objects::nonNull)
            .map { mdClass: com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class ->
                StereotypesHelper.getClassOfMetaClass(
                    mdClass
                )
            }
            .toList()
    }

    @JvmStatic
    fun getAlMetaClassesAsArray(stereotypes: Collection<Stereotype>): Array<Class<*>> {
        return getAllMetaClasses(stereotypes).toTypedArray()
    }



    /* ********************************************************************************************************************
     *  Get all derived properties of a stereotype
     * ********************************************************************************************************************/

    @JvmStatic
    fun getAllDerivedProperties(stereotype: Stereotype): Set<Property> {
        return StereotypesHelper.getPropertiesWithDerived(stereotype)
    }

    @JvmStatic
    fun getAllDerivedProperties(element: Element): Set<Property> {
        return element.appliedStereotype
            .map{getAllDerivedProperties(it)}
            .flatten()
            .toSet()
    }

    @JvmStatic
    fun getDerivedPropertyByName(stereotype: Stereotype, propertyName: String): Property? {
        return getAllDerivedProperties(stereotype)
            .find { property: Property -> property.name == propertyName }

    }

    @JvmStatic
    fun getDerivedPropertyByName(element: Element, propertyName: String): Property? {
        return getAllDerivedProperties(element)
            .find { property: Property -> property.name == propertyName }
    }
}
