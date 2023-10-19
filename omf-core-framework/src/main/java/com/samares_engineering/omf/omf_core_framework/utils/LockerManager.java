/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.utils;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.ProjectUtilities;
import com.nomagic.magicdraw.teamwork2.locks.ILockProjectService;
import com.nomagic.magicdraw.teamwork2.locks.LockInfo;
import com.nomagic.magicdraw.teamwork2.locks.LockService;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ElementTaggedValue;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TaggedValue;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.transaction.ModelValidationResult;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.OMFLockException;

import javax.annotation.CheckForNull;
import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LockerManager {

    private Project project;
    private ILockProjectService projectService = null;
    private static LockerManager instance;

    private LockerManager(Project project){
        this.projectService = LockService.getLockService(project);
        this.project = project;
    }

    public static LockerManager getInstance(){
        return getInstance(OMFUtils.getProject());
    }

    public static LockerManager getInstance(Project project){
        if(instance == null || instance.project != project)
            instance = new LockerManager(project);

        return instance;
    }

    public boolean isLockedByOther(Collection<Element> listElementsToLock){

        return listElementsToLock.stream().anyMatch(element -> isLockedByMe(element) && isLocked(element));

//        for(Element e: listElementsToLock){
//            if (!projectService.isLockedByMe(e) && projectService.isLocked(e)) {
//                return false;
//            }
//        }
//        return true;
    }

    private boolean isLockedByMe(Element element) {
        if(projectService == null) return false;
        return !projectService.isLockedByMe(element);
    }

    public boolean isEditable(Element element){

        boolean isInAttachedProject = ProjectUtilities.isElementInAttachedProject(element);
        boolean isEditable = element.isEditable();
        return isEditable && !isInAttachedProject;
    }
    public boolean isLockedByOther(Element element) {
        if(projectService == null) return false;
        boolean isLocked = isLockedByMe(element) && isLocked(element);

        return isLocked;
    }

    public boolean isLocked(Element element){
        if(projectService == null) return false;
        return projectService.isLocked(element);
    }
    
    public void checkIfEditable(Element element) {
        boolean isEditable = isEditable(element) && !isLockedByOther(element);
        String lockInfo = "";

        projectService.getLockInfo(element);

        if(isLockedByOther(element)) {
//            throw new OMFLockException("[LOCK ERROR] Element is locked by  " + projectService.getLockInfo(element), element);
            OMFErrorHandler.handleException(new OMFLockException("[LOCK ERROR] Element is locked by  " + lockInfo + "   PLEASE UNDO and resolve this lock issue", element));
            return;
        }

        if(!isEditable) {
//            throw new OMFLockException("[LOCK ERROR] Element is not editable  " + projectService.getLockInfo(element), element);}
            OMFErrorHandler.handleException(new OMFLockException("[LOCK ERROR] Element is not editable => PLEASE UNDO and check if these elements are accessible (shared and read-only projects, libraries etc)  " + lockInfo, element));
            return;
        }
//        if(Objects.isNull(projectService.getLockInfo(element)))
//            return;
        if(!projectService.isLocked(element)) {
            OMFErrorHandler.handleException(new OMFLockException("[LOCK ERROR] Element is not lock. => PLEASE UNDO and lock these element before actions  " + lockInfo, element));
//            projectService.lockElements(Collections.singleton(element), new MyProgressStatus());

            return;
        }


    }


    public OMFLockException checkIfEditable2(Element element) {
        boolean isEditable = isEditable(element) || isLockedByOther(element);
        String lockInfo = "";
        projectService.getLockInfo(element);

        if(isLockedByOther(element)) {
//            throw new OMFLockException("[LOCK ERROR] Element is locked by  " + projectService.getLockInfo(element), element);
            return new OMFLockException("     [LOCKED BY] " + lockInfo, element);
        }

        if(!isEditable) {
//            throw new OMFLockException("[LOCK ERROR] Element is not editable  " + projectService.getLockInfo(element), element);}
            return new OMFLockException("     [NON EDITABLE]  " + lockInfo, element);
        }
//        if(Objects.isNull(projectService.getLockInfo(element)))
//            return;
        if(!projectService.isLocked(element)) {
            return new OMFLockException("     [NOT LOCK] " + lockInfo, element);
//            projectService.lockElements(Collections.singleton(element), new MyProgressStatus());
        }
        return null;
    }


    public List<OMFLockException> checkCreation(@CheckForNull List<PropertyChangeEvent> events, Set<Element> checkedElements) {
        return defaultCheck(events, checkedElements);
    }


    public Collection<OMFLockException> checkUpdate(@CheckForNull List<PropertyChangeEvent> events, Set<Element> checkedElements) {
        return defaultCheck(events, checkedElements);
    }
    public Collection<OMFLockException> checkDelete(@CheckForNull List<PropertyChangeEvent> events, Set<Element> checkedElements) {
        return defaultCheck(events, checkedElements);
    }

    private List<OMFLockException> defaultCheck(List<PropertyChangeEvent> events, Set<Element> checkedElements) {
        Map<Element, String> elementsToCheckMap = filterElementToCheck(events, checkedElements);

        checkedElements.addAll(elementsToCheckMap.keySet());

        ArrayList<OMFLockException> lockExceptions = new ArrayList<>();

        elementsToCheckMap.forEach((element, propertyName) ->
                checkElement(element, propertyName).ifPresent(lockExceptions::add));

        return lockExceptions;
    }

    /**
     * Check if an element is Editable and return a OMFLockException if the element is not editable (e.g. project Usages) or if it is not movable (e.g. locks)
     * @param elementToCheck
     * @param propertyName
     * @return Optional OMFLockException
     */
    private Optional<OMFLockException> checkElement(Element elementToCheck, String propertyName) {
        boolean isEditable = isEditable(elementToCheck);
        boolean isMovable = elementToCheck.getOwner() == null ||  ModelHelper.canMoveChildInto(elementToCheck.getOwner(), elementToCheck);

        LockInfo lockInfo = null;
        if(projectService != null)
            lockInfo = projectService.getLockInfo(elementToCheck);

        String sLockInfo = Objects.isNull(lockInfo)? "":  lockInfo.toString();

        if(!isEditable) return Optional.of(new OMFLockException("     [NON EDITABLE]  " + sLockInfo + " - cause: " + propertyName, elementToCheck));


        if(!isMovable) return Optional.of(new OMFLockException("     [NON MOVABLE]  " + sLockInfo+ " - cause: " + propertyName, elementToCheck));


        return Optional.empty();
    }

    /**
     * Will filter all events removing all already checked elements, and irrelevant elements. It will also retrieve the real modified element in the case of TaggedValue .
     * @param events
     * @param checkedElements
     * @return map<Modified Element, Modified PropertyName>
     */
    private Map<Element, String> filterElementToCheck(@CheckForNull List<PropertyChangeEvent> events, Set<Element> checkedElements) {
        Predicate <? super PropertyChangeEvent> hasTheGoodClass =
                   (evt -> Element.class.isInstance(evt.getSource())
                            && !ConnectorEnd.class.isInstance(evt.getSource())
                            && !Stereotype.class.isInstance(evt.getSource())
                            && !Stereotype.class.isInstance(((Element) evt.getSource()).getOwner()));
        Predicate <? super PropertyChangeEvent> isNotTaggedValue = evt -> !(evt.getPropertyName().equals("_elementTaggedValue"));
        Predicate <? super PropertyChangeEvent> isNotEnd = evt -> !(evt.getPropertyName().equals("end"));
        Predicate <? super PropertyChangeEvent> isNotParticipatesInInteraction = evt -> !(evt.getPropertyName().startsWith("participates"));
        Predicate <? super PropertyChangeEvent> isTaggedValue =        evt -> (evt.getSource() instanceof TaggedValue);
        Predicate <? super PropertyChangeEvent> isElementTaggedValue = evt -> (evt.getSource() instanceof ElementTaggedValue);
        Predicate <? super PropertyChangeEvent> isNotAlreadyChecked = (evt -> !checkedElements.contains(evt.getSource()));


        Predicate <? super Map.Entry> notNull = (entry -> entry.getKey() != null);

        Predicate <? super Object> isNotAComputedProperty = evt -> !(((PropertyChangeEvent) evt).getPropertyName().startsWith("_"));

        Function<? super  PropertyChangeEvent, Element> manageElementTaggedValue = evt -> {
            if(isElementTaggedValue.test(evt)) {
                checkedElements.addAll(((ElementTaggedValue) evt.getSource()).getValue().stream().filter(Element.class::isInstance).collect(Collectors.toList()));
                return null;
            }
            if(isTaggedValue.test(evt)) {
                checkedElements.add((TaggedValue) evt.getSource());
                return null;
            }
            return (Element) evt.getSource();
        };

        Function<? super  PropertyChangeEvent, Map.Entry<Element, String>> evtToEntry = evt -> {
            Element element = manageElementTaggedValue.apply(evt);
            return new AbstractMap.SimpleEntry<>(element, evt.getPropertyName());
        };



        HashMap<Element, String> elementToPropertieNamesMap = new HashMap<>();
        events.stream()
                .filter(isNotTaggedValue)
                .filter(isNotEnd)
                .filter(isNotParticipatesInInteraction)
                .filter(isNotAComputedProperty)
                .filter(hasTheGoodClass)
                .filter(isNotAlreadyChecked)
                .map(evtToEntry)
                .filter(notNull)
                .forEach(entry -> elementToPropertieNamesMap.put(entry.getKey(),entry.getValue()));

        return elementToPropertieNamesMap;
    }

    public ModelValidationResult validateLocks(Element element)  {
        boolean isEditable = isEditable(element) && !isLockedByOther(element);

        if(!isLocked(element)) return new ModelValidationResult(element, "[LOCK ERROR] Element is not locked ");

        if(isLockedByOther(element)) return new ModelValidationResult(element, "[LOCK ERROR] Element is locked by  " + projectService.getLockInfo(element));

        if(!isEditable) return new ModelValidationResult(element, "[LOCK ERROR] Element is not editable  " + projectService.getLockInfo(element));
        return null;

    }

}
