/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_public_features.stereotypes.utils;


import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdbasicbehaviors.Behavior;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.beans.PropertyChangeEvent;
import java.lang.Class;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StereotypesLiveActionsUtils {

    public static boolean isTypeInstantiationPatternSatisfied(Element src, Class srcClass, String strType, Class typeClass) {
        if (!(src instanceof TypedElement) || !srcClass.isInstance(src)) {
            return false;
        }
        TypedElement typedElement = (TypedElement) src;
        if (typedElement.getType() != null && typeClass.isInstance(typedElement.getType())) {
            return hasStereotype(typedElement.getType(), strType);
        }
        return false;
    }

    public static boolean isInstancePropertyWithStr(Element src, String strInstance) {
        if (!(src instanceof Property))
            return false;

        return hasStereotype(src, strInstance);
    }

    public static boolean isInstanceActionWithStr(Element src, String strInstance) {
        if (!(src instanceof Action))
            return false;

        return hasStereotype(src, strInstance);
    }

    public static boolean isTypeElementTypeNull(Element src) {
        if (!(src instanceof TypedElement))
            return false;
        return ((TypedElement) src).getType() == null;
    }

    public static boolean isCBATypeNull(Element src) {
        if (!(src instanceof CallBehaviorAction))
            return false;
        return ((CallBehaviorAction) src).getBehavior() == null;
    }

    public static boolean isParentBehaviorInstantiationPatternSatisfied(Element src, String strParent) {
        if (!(src instanceof CallBehaviorAction))
            return false;
        CallBehaviorAction action = (CallBehaviorAction) src;
        if (action.getBehavior() != null)
            return hasStereotype(action.getBehavior(), strParent);
        return false;
    }

    public static boolean ownerHasStereotype(Element src, List<String> strOwner) {
        Element owner = src.getOwner();
        if (owner == null) return false;
        if(strOwner.isEmpty()) return true;
        return owner.getAppliedStereotype().stream().map(Stereotype::getName)
                .anyMatch(strOwner::contains);
    }

    public static void instantiationBehavior(PropertyChangeEvent evt, String strInstance) {
        Stereotype stereotype = getStereotypeFromAnyProfile(strInstance);
        if (stereotype == null) {
            throw new OMFCriticalException("Can't find stereotype " + strInstance + " in project profiles");
        }
        Element elementToStereotype = (Element) evt.getSource();
        removeRedundantStereotypes(stereotype, elementToStereotype);

        StereotypesHelper.addStereotype(elementToStereotype, stereotype);
        checkStereotypeApplication(elementToStereotype, strInstance);
    }

    /**
     * Check if the stereotype or a generalized version of it is applied to the element, and remove them.
     * @param stereotype Stereotype to remove
     * @param elementToStereotype Element to remove the stereotype from
     */
    public static void removeRedundantStereotypes(Stereotype stereotype, Element elementToStereotype) {
        ModelHelper.getGeneralClassifiersRecursively(stereotype)//Removing all redundant stereotypes of the same type (using generalization)
                .stream()
                .filter(Stereotype.class::isInstance)
                .map(Stereotype.class::cast)
                .filter(str -> elementToStereotype.getAppliedStereotype().contains(str))
                .forEach(str -> StereotypesHelper.removeStereotype(elementToStereotype, str));
    }

    public static void createTypeBehavior(PropertyChangeEvent evt, String strType) {
        TypedElement typedElement = (TypedElement) evt.getSource();
        final boolean shallCreatePropertyType = typedElement.getType() == null && typedElement instanceof Property;

        Type type = null;
        if (shallCreatePropertyType)
            type = SysMLFactory.getInstance().createClass();

        Stereotype stereotype = getStereotypeFromAnyProfile(strType);
        assert stereotype != null;
        assert type != null;
        StereotypesHelper.addStereotype(type, stereotype);

        type.setOwner(Objects.requireNonNull(typedElement.getOwner()).getOwner());
        type.setName("TO_RENAME");

        Optional<DirectedRelationship> foundDirectedRelationship = typedElement.get_directedRelationshipOfTarget().stream().filter(Association.class::isInstance).findFirst();
        Association association;
        if (foundDirectedRelationship.isEmpty()) {
            Element exOwner = typedElement.getOwner();
            association = SysMLFactory.getInstance().getMagicDrawFactory().createAssociationInstance();   //CREATE COMPOSITION
            association.setOwner(type.getOwner());
            association.getMemberEnd().get(0).setType((Type) typedElement.getOwner());  //SRC COMPOSITION (OWNER OF PART PROPERTY
            association.getOwnedEnd().set(1, (Property) typedElement);
            association.getNavigableOwnedEnd().add(association.getMemberEnd().get(1));    //TARGET COMPOSITION

            typedElement.setType(type);
            typedElement.setOwner(exOwner);//Property has moved, surely not the best way to do this...
        } else {
            association = (Association) foundDirectedRelationship.get();
        }

        association.getMemberEnd().get(1).setAggregation(AggregationKindEnum.COMPOSITE);
        ((Property) typedElement).setAssociation(association);
        association.getNavigableOwnedEnd();

        checkStereotypeApplication(type, strType);
    }

    public static void createActivityTypeBehavior(PropertyChangeEvent evt, String strType) {
        CallBehaviorAction cba = (CallBehaviorAction) evt.getSource();
        Behavior activity = null;

        activity = SysMLFactory.getInstance().createActivity();

        Stereotype stereotype = getStereotypeFromAnyProfile(strType);
        assert stereotype != null;
        StereotypesHelper.addStereotype(activity, stereotype);
        activity.setOwner(Objects.requireNonNull(cba.getOwner()).getOwner());

        activity.setName("TO_RENAME");

        cba.setBehavior(activity);
        checkStereotypeApplication(activity, strType);
    }

    private static void checkStereotypeApplication(Element element, String strType) {
        //STEREOTYPE NOT APPLIED
        if(!hasStereotype(element, strType))
            try {
                throw new LegacyOMFException("[Stereotype Application] the stereotype has not be properly applied, " +
                        "please check that it exists", GenericException.ECriticality.ALERT);
            } catch (Exception e) {
                LegacyErrorHandler.handleException(e, false);
            }
    }

    public static void organizeType(PropertyChangeEvent evt, Element owner) {
        TypedElement element = (TypedElement) evt.getSource();
        element.getType().setOwner(element.getOwner());
    }

    public static void organizeOwner(PropertyChangeEvent evt, Element owner) {
        ((Element) evt.getSource()).setOwner(owner);
    }

    /**
     * Check if the element has a stereotype applied
     * @param element Element
     * @param str   String
     * @return boolean
     */
    public static boolean hasStereotype(Element element, String str) {
        return element.getAppliedStereotype().stream().map(Stereotype::getName)
                .anyMatch(str::equals);
    }

    /**
     * Get a Stereotype with its name by browsing through the project profiles
     * @param str String
     * @return Stereotype
     */
    public static Stereotype getStereotypeFromAnyProfile(String str){
        Collection<Profile> profileList = StereotypesHelper.getAllProfiles(OMFUtils.getProject());
        Function<Profile, List<Stereotype>> getAllStereotypes = profile -> {
            List<Stereotype> stereotypes = profile.getNestedPackage().stream()
                    .map(Package::getOwnedStereotype)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            stereotypes.addAll(profile.getOwnedStereotype());
            return stereotypes;
        };

        Optional<Stereotype> optStereotype = profileList.stream()
                .map(getAllStereotypes)
                .flatMap(Collection::stream)
                .filter(Stereotype -> Stereotype.getName().equalsIgnoreCase(str))
                .findFirst();

        Stereotype foundStereotype = optStereotype.isPresent() ?
                optStereotype.get()
                : StereotypesHelper.getStereotype(OMFUtils.getProject(), str); //Not optimal but cover stereotype declared inside directly the project

        if(foundStereotype != null){
            return foundStereotype;
        }else{
            LegacyErrorHandler.handleException(new LegacyOMFException("[InstanceCreator] " +
                    "\n It seems that no profile owns the stereotype of the name \"" + str, LegacyOMFException.ECriticality.CRITICAL), false);
        }
        return null; // Will not goes that far, an exception will thrown
    }

    /**
     * Returns a profile with the name of a stereotype it owns.
     * @param str String
     * @return Profile
     */
    public static Profile getProfileByStereotypeName(String str) {
        Collection<Profile> profileList = StereotypesHelper.getAllProfiles(OMFUtils.getProject());
        Optional<Stereotype> optStereotype = profileList.stream().map(Profile::getOwnedStereotype).flatMap(Collection::stream)
                .filter(Stereotype -> Stereotype.getName().equalsIgnoreCase(str)).findFirst();
        if(optStereotype.isPresent()){
            return optStereotype.get().getProfile();
        }else{
            LegacyErrorHandler.handleException(new LegacyOMFException("[InstanceCreator] " +
                    "\n It seems that no profile owns the stereotype of the name \"" + str, LegacyOMFException.ECriticality.CRITICAL), false);
        }
        return null;
    }


}
