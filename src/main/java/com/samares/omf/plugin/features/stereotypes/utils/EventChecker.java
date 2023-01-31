package com.samares.omf.plugin.features.stereotypes.utils;

import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.utils.profile.Profile;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

public class EventChecker {

    private final ArrayList<Predicate<? super PropertyChangeEvent>> l_predicate;

    public EventChecker(){
        this.l_predicate = new ArrayList<>();
    }

    public boolean test(PropertyChangeEvent evt){
        return l_predicate.stream()
                .allMatch(predicate -> predicate.test(evt));
    }

    public EventChecker isInstanceCreated(){
        l_predicate.add(evt -> evt.getPropertyName().equals(UML2MetamodelConstants.INSTANCE_CREATED));
        return this;
    }
    public EventChecker isInstanceDeleted(){
        l_predicate.add(
                evt ->  evt.getPropertyName().equals(UML2MetamodelConstants.BEFORE_DELETE)
                    ||  evt.getPropertyName().equals(UML2MetamodelConstants.INSTANCE_DELETED));
        return this;
    }

    public EventChecker isSourceNotNull() {
        l_predicate.add(evt -> Objects.nonNull(evt.getSource()));
        return this;
    }
    public EventChecker isBlock() {
        isSourceNotNull();
        l_predicate.add(evt -> Profile.getInstance().get_Sysml().block().is((Element) evt.getSource()));
        return this;
    }
}
