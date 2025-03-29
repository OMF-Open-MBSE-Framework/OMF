/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_core_framework.utils

import com.nomagic.magicdraw.core.Project
import com.nomagic.magicdraw.core.ProjectUtilities
import com.nomagic.magicdraw.teamwork2.locks.ILockProjectService
import com.nomagic.magicdraw.teamwork2.locks.LockInfo
import com.nomagic.magicdraw.teamwork2.locks.LockService
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ElementTaggedValue
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TaggedValue
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype
import com.nomagic.uml2.transaction.ModelValidationResult
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.LockException
import com.samares_engineering.omf.omf_core_framework.utils.twc.TWCUtils
import java.beans.PropertyChangeEvent
import java.util.*
import java.util.function.Function
import java.util.function.Predicate

object LockerManager {
    private var project: Project = defaultProject
    private val areProjectTWC = HashMap<Project, Boolean>()

    private val defaultProject: Project
        get() = OMFUtils.getProject()
    private val projectService: ILockProjectService?
        get() = LockService.getLockService(project)



    @JvmStatic
    fun withProject(project: Project): LockerManager {
        this.project = project
        return this
    }


    fun canEdit(element: Element): Boolean {
        return isEditable(element) &&
                isLockFree(element) &&
                isMovable(element)
    }

    fun isLockFree(element: Element): Boolean {
        return isNotTWCProject() || (isLocked(element) && isLockedByMe(element))
    }


    fun isNotTWCProject(): Boolean =  !(areProjectTWC.computeIfAbsent(project) { TWCUtils.isItTWCProject(project) })



    fun isLockedByOther(listElementsToLock: Collection<Element>): Boolean {
        return listElementsToLock.any { element: Element -> isLockedByMe(element) && isLocked(element) }

        //        for(Element e: listElementsToLock){
//            if (!projectService.isLockedByMe(e) && projectService.isLocked(e)) {
//                return false;
//            }
//        }
//        return true;
    }

    fun isLockedByMe(element: Element): Boolean {
        if (projectService == null) return false
        return !projectService!!.isLockedByMe(element)
    }

    fun isEditable(element: Element): Boolean {
        val isInAttachedProject = ProjectUtilities.isElementInAttachedProject(element)
        val isEditable = element.isEditable
        return isEditable && !isInAttachedProject
    }

    fun isLockedByOther(element: Element): Boolean {
        if (projectService == null) return false
        val isLocked = isLockedByMe(element) && isLocked(element)

        return isLocked
    }

    fun isLocked(element: Element): Boolean {
        if (projectService == null) return false
        return projectService!!.isLocked(element)
    }

    fun checkIfEditable(element: Element) {
        val isEditable = isEditable(element) && !isLockedByOther(element)
        val lockInfo = ""

        projectService!!.getLockInfo(element)

        if (isLockedByOther(element)) {
            LegacyErrorHandler.handleException(
                LockException(
                    "[LOCK ERROR] Element is locked by  $lockInfo   PLEASE UNDO and resolve this lock issue", element
                )
            )
            return
        }

        if (!isEditable) {
            LegacyErrorHandler.handleException(
                LockException(
                    "[LOCK ERROR] Element is not editable => PLEASE UNDO and check if these elements are accessible (shared and read-only projects, libraries etc)  $lockInfo",
                    element
                )
            )
            return
        }

        if (!projectService!!.isLocked(element)) {
            LegacyErrorHandler.handleException(
                LockException(
                    "[LOCK ERROR] Element is not lock. => PLEASE UNDO and lock these element before actions  $lockInfo",
                    element
                )
            )
            return
        }
    }


    fun checkIfEditable2(element: Element): LockException? {
        val isEditable = isEditable(element) || isLockedByOther(element)
        val lockInfo = ""
        projectService!!.getLockInfo(element)

        if (isLockedByOther(element)) {
            return LockException("     [LOCKED BY] $lockInfo", element)
        }

        if (!isEditable) {
            return LockException("     [NON EDITABLE]  $lockInfo", element)
        }

        if (!projectService!!.isLocked(element)) {
            return LockException("     [NOT LOCK] $lockInfo", element)
        }
        return null
    }


    fun checkCreation(events: List<PropertyChangeEvent>, checkedElements: MutableSet<Element>): List<LockException> {
        return defaultCheck(events, checkedElements)
    }


    fun checkUpdate(
        events: List<PropertyChangeEvent>,
        checkedElements: MutableSet<Element>
    ): Collection<LockException> {
        return defaultCheck(events, checkedElements)
    }

    fun checkDelete(
        events: List<PropertyChangeEvent>,
        checkedElements: MutableSet<Element>
    ): Collection<LockException> {
        return defaultCheck(events, checkedElements)
    }

    private fun defaultCheck(
        events: List<PropertyChangeEvent>,
        checkedElements: MutableSet<Element>
    ): List<LockException> {
        val elementsToCheckMap = filterElementToCheck(events, checkedElements)

        checkedElements.addAll(elementsToCheckMap.keys)

        val lockExceptions = ArrayList<LockException>()

        elementsToCheckMap.forEach { (element: Element, propertyName: String) ->
            checkElement(
                element,
                propertyName
            ).ifPresent { e: LockException -> lockExceptions.add(e) }
        }

        return lockExceptions
    }

    /**
     * Check if an element is Editable and return a OMFLockException if the element is not editable (e.g. project Usages) or if it is not movable (e.g. locks)
     * @param elementToCheck
     * @param propertyName
     * @return Optional OMFLockException
     */
    private fun checkElement(elementToCheck: Element, propertyName: String): Optional<LockException> {
        val isEditable = isEditable(elementToCheck)
        val isMovable = isMovable(elementToCheck)

        var lockInfo: LockInfo? = null
        if (projectService != null) lockInfo = projectService!!.getLockInfo(elementToCheck)

        val sLockInfo = if (Objects.isNull(lockInfo)) "" else lockInfo.toString()

        if (!isEditable) return Optional.of(
            LockException(
                "     [NON EDITABLE]  $sLockInfo - cause: $propertyName", elementToCheck
            )
        )


        if (!isMovable) return Optional.of(
            LockException(
                "     [NON MOVABLE]  $sLockInfo - cause: $propertyName", elementToCheck
            )
        )


        return Optional.empty()
    }

     fun isMovable(elementToCheck: Element) =
        elementToCheck.owner == null || ModelHelper.canMoveChildInto(
            elementToCheck.owner, elementToCheck
        )

    /**
     * Will filter all events removing all already checked elements, and irrelevant elements. It will also retrieve the real modified element in the case of TaggedValue .
     * @param events
     * @param checkedElements
     * @return map<Modified Element, Modified PropertyName>
    </Modified> */
    private fun filterElementToCheck(
        events: List<PropertyChangeEvent>,
        checkedElements: MutableSet<Element>
    ): Map<Element, String> {
        val hasTheGoodClass: Predicate<in PropertyChangeEvent> =
            (Predicate { evt: PropertyChangeEvent ->
                (Element::class.java.isInstance(evt.source)
                        && !ConnectorEnd::class.java.isInstance(evt.source)
                        && !Stereotype::class.java.isInstance(evt.source)
                        && !Stereotype::class.java.isInstance((evt.source as Element).owner))
            })
        val isNotTaggedValue: Predicate<in PropertyChangeEvent> =
            Predicate { evt: PropertyChangeEvent -> evt.propertyName != "_elementTaggedValue" }
        val isNotEnd: Predicate<in PropertyChangeEvent> =
            Predicate { evt: PropertyChangeEvent -> evt.propertyName != "end" }
        val isNotParticipatesInInteraction: Predicate<in PropertyChangeEvent> =
            Predicate { evt: PropertyChangeEvent -> !(evt.propertyName.startsWith("participates")) }
        val isTaggedValue: Predicate<in PropertyChangeEvent> =
            Predicate { evt: PropertyChangeEvent -> (evt.source is TaggedValue) }
        val isElementTaggedValue: Predicate<in PropertyChangeEvent> =
            Predicate { evt: PropertyChangeEvent -> (evt.source is ElementTaggedValue) }
        val isNotAlreadyChecked: Predicate<in PropertyChangeEvent> =
            (Predicate { evt: PropertyChangeEvent -> !checkedElements.contains(evt.source) })


        val notNull: Predicate<in Map.Entry<Element?, String>> = (Predicate { entry: Map.Entry<Element?, String> -> entry.key != null })

        val isNotAComputedProperty =
            Predicate { evt: Any -> !((evt as PropertyChangeEvent).propertyName.startsWith("_")) }

        val manageElementTaggedValue: Function<in PropertyChangeEvent, Element?> =
            Function<PropertyChangeEvent, Element?> { evt: PropertyChangeEvent ->
                if (isElementTaggedValue.test(evt)) {
                    checkedElements.addAll(
                        (evt.source as ElementTaggedValue).value
                            .filter { obj: Element? -> Element::class.java.isInstance(obj) })
                    return@Function null
                }
                if (isTaggedValue.test(evt)) {
                    checkedElements.add(evt.source as TaggedValue)
                    return@Function null
                }
                evt.source as Element
            }

        val evtToEntry: Function<in PropertyChangeEvent, Map.Entry<Element?, String>> =
            Function<PropertyChangeEvent, Map.Entry<Element?, String>> { evt: PropertyChangeEvent ->
                val element = manageElementTaggedValue.apply(evt)
                AbstractMap.SimpleEntry(element, evt.propertyName)
            }


        val elementToPropertiesNamesMap = HashMap<Element, String>()
        events
            .asSequence()
            .filter{isNotTaggedValue.test(it)}
            .filter{isNotEnd.test(it)}
            .filter{isNotParticipatesInInteraction.test(it)}
            .filter{isNotAComputedProperty.test(it)}
            .filter{hasTheGoodClass.test(it)}
            .filter{isNotAlreadyChecked.test(it)}
            .map{evtToEntry.apply(it)}
            .filter{notNull.test(it)}
            .toList()
            .forEach { entry: Map.Entry<Element?, String> -> elementToPropertiesNamesMap[entry.key!!] = entry.value }

        return elementToPropertiesNamesMap
    }

    fun validateLocks(element: Element): ModelValidationResult? {
        val isEditable = isEditable(element) && !isLockedByOther(element)

        if (!isLocked(element)) return ModelValidationResult(element, "[LOCK ERROR] Element is not locked ")

        if (isLockedByOther(element)) return ModelValidationResult(
            element,
            "[LOCK ERROR] Element is locked by  " + projectService!!.getLockInfo(element)
        )

        if (!isEditable) return ModelValidationResult(
            element,
            "[LOCK ERROR] Element is not editable  " + projectService!!.getLockInfo(element)
        )
        return null
    }




}
