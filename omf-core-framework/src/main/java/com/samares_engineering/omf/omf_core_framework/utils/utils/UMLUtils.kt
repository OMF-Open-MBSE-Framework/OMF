/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_core_framework.utils.utils

import com.nomagic.uml2.MagicDrawProfile
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype
import com.nomagic.uml2.ext.magicdraw.metadata.UMLPackage
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EStructuralFeature
import java.util.*

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
        return stereotypes.asSequence()
            .map { str: Stereotype -> getStereotypeMetaClass(str) }
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
//        return StereotypesHelper.getPropertiesWithDerived(stereotype)
        val mdProfile = MagicDrawProfile.getInstance(stereotype);
        return stereotype._elementTaggedValue
            .asSequence()
            .filterNotNull()
            .map { it.owner }
            .filterIsInstance<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class>()
            .filter { mdProfile.customization().`is`(it) }
            .map { it.ownedAttribute }
            .flatten()
            .filter { mdProfile.derivedPropertySpecification().`is`(it) }
            .toSet()

    }

    @JvmStatic
    fun getAllDerivedProperties(element: Element): Set<Property> {
        return element.appliedStereotype
            .map { getAllDerivedProperties(it) }
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

    @JvmStatic
    fun getDerivedPropertyValue(element: Element, derivedProperty: Property): Any? {
        return getDerivedPropertyValueByName(element, derivedProperty)
    }

    @JvmStatic
    private fun getDerivedPropertyValueByName(
        element: Element, derivedProperty: Property
    ) = element.refGetValue(derivedProperty.name)

    @JvmStatic
    fun getDerivedPropertyValue(element: Element, propertyName: String): Any? {
        return getDerivedPropertyByName(element, propertyName)?.let { getDerivedPropertyValue(element, it) }
    }


    //UML Properties
    /**
     * Get all the UML properties of the stereotype
     * @param stereotype the stereotype to get the UML properties from
     * @return a list with all the UML properties of the stereotype
     */
    @JvmStatic
    fun getAllStereotypeUMLProperties(stereotype: Stereotype): List<EStructuralFeature> {
        return StereotypesHelper.getBaseClasses(stereotype)
            .map { UMLPackage.eINSTANCE.getEClassifier(it.name) }
            .filterIsInstance<EClass>()
            .map { it.eAllStructuralFeatures }
            .flatten()
    }

    /**
     * Get all the UML properties of the element
     * @param element the element to get the UML properties from
     * @return a list with all the UML properties of the element
     */
    @JvmStatic
    fun getAllStereotypeUMLProperties(element: Element): List<EStructuralFeature> {
        return element.appliedStereotype
            .map { getAllStereotypeUMLProperties(it) }
            .flatten()
    }


    @JvmStatic
    fun getUMLPropertyValue(property: EStructuralFeature, element: Element): List<*> {
        val value: Any? = element.refGetValue(property.name)
        return if (value is List<*>) value
        else if (value != null) listOf(value)
        else emptyList()
    }

}
