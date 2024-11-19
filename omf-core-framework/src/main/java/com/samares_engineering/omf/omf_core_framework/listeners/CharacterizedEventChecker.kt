/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_core_framework.listeners

import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype
import com.nomagic.uml2.impl.PropertyNames
import com.samares_engineering.omf.omf_core_framework.feature.OMFAutomationManager
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.events.CharacterizedEvent
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile
import java.util.function.Predicate

class CharacterizedEventChecker {
    private val predicates = ArrayList<Predicate<CharacterizedEvent>>()

    fun test(evt: CharacterizedEvent): Boolean {
        return predicates.stream()
            .allMatch { predicate: Predicate<CharacterizedEvent> -> predicate.test(evt) }
    }

    val noAutomationTriggered: CharacterizedEventChecker
        get() {
            isTrue { _: CharacterizedEvent ->
                OMFAutomationManager.getInstance().noAutomationTriggered()
            }
            return this
        }

    val hasNoAutomationTriggered: CharacterizedEventChecker
        get() {
            isTrue { _: CharacterizedEvent ->
                !OMFAutomationManager.getInstance().noAutomationTriggered()
            }
            return this
        }

    val isInstanceCreated: CharacterizedEventChecker
        get() {
            predicates.add(Predicate { history: CharacterizedEvent ->
                history.relatedEvents.contains(UML2MetamodelConstants.INSTANCE_CREATED)
            })
            return this
        }
    val isElementCreated: CharacterizedEventChecker
        get() {
            isInstanceCreated
            predicates.add(Predicate { history -> history.element.owner != null })
            return this
        }

    fun hasStereotype(stereotype: Stereotype?): CharacterizedEventChecker {
        predicates.add(Predicate { history: CharacterizedEvent ->
            StereotypesHelper.hasStereotype(history.element, stereotype)
        })
        return this
    }

    fun hasStereotypeOrDerived(stereotype: Stereotype?): CharacterizedEventChecker {
        predicates.add(Predicate { history: CharacterizedEvent ->
            StereotypesHelper.hasStereotypeOrDerived(history.element, stereotype)
        })
        return this
    }

    val isInstanceDeleted: CharacterizedEventChecker
        get() {
            predicates.add(Predicate { history: CharacterizedEvent ->
                history.relatedEvents.contains(UML2MetamodelConstants.INSTANCE_DELETED)
            })
            return this
        }


    val isPart: CharacterizedEventChecker
        get() {
            predicates.add(Predicate { history: CharacterizedEvent ->
                Profile._getSysmlAdditionalStereotypes().partProperty().`is`(history.element)
            })
            return this
        }
    val isPort: CharacterizedEventChecker
        get() {
            predicates.add(Predicate { history: CharacterizedEvent ->
                history.element is Port
            })
            return this
        }

    val isClass: CharacterizedEventChecker
        get() {
            predicates.add(Predicate { history: CharacterizedEvent ->
                history.element is com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class
            })
            return this
        }

    val isBlock: CharacterizedEventChecker
        get() {
            predicates.add(Predicate { history: CharacterizedEvent ->
                Profile._getSysml().block().`is`(history.element)
            })
            return this
        }

    val isInterfaceBlock: CharacterizedEventChecker
        get() {
            predicates.add(Predicate { history: CharacterizedEvent ->
                Profile._getSysml().interfaceBlock().`is`(history.element)
            })
            return this
        }

    val isConnector: CharacterizedEventChecker
        get() {
            predicates.add(Predicate { history: CharacterizedEvent ->
                history.element is Connector
            })
            return this
        }

    fun onRenaming(): CharacterizedEventChecker {
        predicates.add(Predicate { history: CharacterizedEvent ->
            history.relatedEvents.contains(PropertyNames.NAME) && history.relatedEvents[PropertyNames.NAME]?.oldValue != null
        })
        return this
    }

    val isElementRenamed: CharacterizedEventChecker
        get() {
            predicates.add(Predicate { history: CharacterizedEvent ->
                history.relatedEvents.contains(PropertyNames.NAME) && history.relatedEvents[PropertyNames.NAME]?.oldValue != null
            })
            return this
        }

    val isPortTyped: CharacterizedEventChecker
        get() {
        predicates.add(Predicate { history: CharacterizedEvent -> (history.element as Port).type != null })
        return this
    }
    val isPortUntyped: CharacterizedEventChecker
        get() {
        predicates.add(Predicate { history: CharacterizedEvent -> (history.element as Port).type == null })
        return this
    }

    fun isInstanceOf(clazz: Class<*>): CharacterizedEventChecker {
        predicates.add(Predicate { history: CharacterizedEvent -> clazz.isInstance(history.element) })
        return this
    }

    fun isTrue(predicate: Predicate<CharacterizedEvent>): CharacterizedEventChecker {
        predicates.add(predicate)
        return this
    }

    fun vanillaElementCreated(): CharacterizedEventChecker {
        isElementCreated
        return this
    }

}
