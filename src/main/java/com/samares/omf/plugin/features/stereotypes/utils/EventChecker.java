/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.features.stereotypes.utils;

import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.utils.profile.Profile;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

public class EventChecker {

    private final ArrayList<Predicate<? super PropertyChangeEvent>> predicates;

    public EventChecker(){
        this.predicates = new ArrayList<>();
    }

    public boolean test(PropertyChangeEvent evt){
        return predicates.stream()
                .allMatch(predicate -> predicate.test(evt));
    }

    public EventChecker isInstanceCreated(){
        predicates.add(evt -> evt.getPropertyName().equals(UML2MetamodelConstants.INSTANCE_CREATED));
        return this;
    }
    public EventChecker isInstanceDeleted(){
        predicates.add(
                evt ->  evt.getPropertyName().equals(UML2MetamodelConstants.BEFORE_DELETE)
                    ||  evt.getPropertyName().equals(UML2MetamodelConstants.INSTANCE_DELETED));
        return this;
    }

    public EventChecker isSourceNotNull() {
        predicates.add(evt -> Objects.nonNull(evt.getSource()));
        return this;
    }
    public EventChecker isBlock() {
        isSourceNotNull();
        predicates.add(evt -> Profile.getInstance().getSysml().block().is((Element) evt.getSource()));
        return this;
    }
}
