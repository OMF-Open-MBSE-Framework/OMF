/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.stereotypes.utils;

import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.impl.PropertyNames;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

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
    public EventChecker isElementCreated(){
        isInstanceCreated();
        predicates.add(evt -> evt.getSource() instanceof Element && ((Element) evt.getSource()).getOwner() != null);
        return this;
    }
    public EventChecker hasStereotype(Stereotype stereotype){
        predicates.add(evt -> evt.getSource() instanceof Element && StereotypesHelper.hasStereotype((Element) evt.getSource(), stereotype));
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
    public EventChecker isPart() {
        isSourceNotNull();
        predicates.add(evt -> Profile.getSysmlAdditionalStereotypes().partProperty().is((Element) evt.getSource()));
        return this;
    }
    public EventChecker isPort() {
        isSourceNotNull();
        predicates.add(evt -> evt.getSource() instanceof Port);
        return this;
    }

    public EventChecker onRenaming() {
        isSourceNotNull();
        predicates.add(evt -> evt.getPropertyName().equals(PropertyNames.NAME));
        return this;
    }
}
