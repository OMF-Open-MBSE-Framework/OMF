package com.samares_engineering.omf.omf_core_framework.model_comparators.comparison;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TaggedValue;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.helpers.ComparatorUtils;

import java.util.*;

public class LegacyLogger {
    private final Set<Entry> added = new HashSet<>();
    private final Set<Entry> removed = new HashSet<>();
    private final Map<Element, Diff> changed = new HashMap<>();

    public void logReferencedElements(String refName, Element elemLeft, Element elemRight,
                                      List<Element> remainingValuesLeft, List<Element> remainingValuesRight) {
        if (remainingValuesLeft.size() == 1 && remainingValuesRight.size() == 1) {
            addChange(elemLeft, elemRight, "Changed reference <b>"
                    + refName + "</b> from " + getFullName(remainingValuesLeft.get(0)) + " to "
                    + getFullName(remainingValuesRight.get(0)));
        } else {
            remainingValuesLeft.forEach(elem ->
                    addChange(elemLeft, elemRight, "Removed reference <b>"
                            + refName + "</b> to " + getFullName(elem)));
            remainingValuesRight.forEach(elem ->
                    addChange(elemLeft, elemRight, "Added reference <b>"
                            + refName + "</b> to " + getFullName(elem)));
        }
    }

    public void logTaggedValueValuesDiffs(Element elemLeft, Element elemRight, TaggedValue taggedValue1,
                                          List<Object> remainingValuesOld, List<Object> remainingValuesNew) {
        int unchangedValuesCount = taggedValue1.getValue().size() - remainingValuesOld.size();
        String unchangedValuesDescription = unchangedValuesCount > 0 ? unchangedValuesCount + " other values where unchanged." : "";
        addChange(elemRight, elemLeft, "Changed taggedValue <b>"
                + taggedValue1.getTagDefinition().getName() + "</b>"
                + " removed values [" + ComparatorUtils.taggedValueValuesToString(remainingValuesOld) + "] and added values ["
                + ComparatorUtils.taggedValueValuesToString(remainingValuesNew) + "]. " + unchangedValuesDescription);
    }

    public void logTheTaggedValuesDiffs(Element elemLeft, Element elemRight, List<TaggedValue> unmatchedTaggedValuesLeft,
                                        List<TaggedValue> unmatchedTaggedValuesRight) {
        // Log the unmatched tagged values which correspond to added or removed tagged values.
        unmatchedTaggedValuesLeft.forEach(taggedValue ->
                addChange(elemRight, elemLeft,
                        "Removed taggedValue <b> " + taggedValue.getTagDefinition().getName()
                                + "</b> with value [" + ComparatorUtils.taggedValueValuesToString(taggedValue.getValue()) + "]"));
        unmatchedTaggedValuesRight.forEach(taggedValue ->
                addChange(elemRight, elemLeft,
                        "Added taggedValue <b> " + taggedValue.getTagDefinition().getName()
                                + "</b> with value [" + ComparatorUtils.taggedValueValuesToString(taggedValue.getValue()) + "]"));
    }
    public void logAttributeDiffs(Element elemLeft, Element elemRight, String attributeName, Object valueLeft, Object valueRight) {
        addChange(elemRight, elemLeft, elemLeft, elemRight,
                "Changed attribute <b>" + attributeName + "</b> from " + ComparatorUtils.toString(valueLeft)
                        + " to " + ComparatorUtils.toString(valueRight));
    }

    public String getDiffInfo() {
        StringBuilder sBuilder = new StringBuilder();
        Entry entry;
        if (!getAdded().isEmpty()) {
            sBuilder.append("New elements:\n");

            for (Entry value : getAdded()) {
                entry = value;
                sBuilder.append("--- ");
                ComparatorUtils.toFullName(entry.getElement(), sBuilder);
                sBuilder.append("\n------ ");
                sBuilder.append(" property=");
                sBuilder.append(entry.getProperty());
                sBuilder.append("\n------ ");
                sBuilder.append("\n");
            }
        }

        if (!getRemoved().isEmpty()) {
            sBuilder.append("Removed elements:\n");

            for (Entry value : getRemoved()) {
                entry = value;
                sBuilder.append("--- ");
                ComparatorUtils.toFullName(entry.getElement(), sBuilder);
                sBuilder.append("property=");
                sBuilder.append(entry.getProperty());
                sBuilder.append("\n");
            }
        }

        if (!getChanged().isEmpty()) {
            sBuilder.append("Changed elements:\n");

            for (Map.Entry<Element, Diff> elementDiffEntry : getChanged().entrySet()) {
                sBuilder.append("--- ");
                ComparatorUtils.toFullName(elementDiffEntry.getKey(), sBuilder);
                sBuilder.append(":\n");
                Diff diff = elementDiffEntry.getValue();

                for (String change : diff.getChanges()) {
                    sBuilder.append("------ ").append(change).append("\n");
                }
            }
        }

        return sBuilder.toString();
    }

    private String getFullName(Element elem) {
        StringBuilder var1 = new StringBuilder();
        ComparatorUtils.toFullName(elem, var1);
        return var1.toString();
    }

    public void addChange(Element elem1, Element elem2, String diffResult) {
        addChange(elem1.getOwner(), elem2.getOwner(), elem1, elem2, diffResult);
    }

    protected void addChange(Element owner1, Element owner2, Element elem1, Element elem2, String changeDesc) {
        Diff diff = changed.computeIfAbsent(elem1, elem1x -> new Diff(elem2));
        diff.addChange(changeDesc);
    }

    public static final class Entry {

        private final Element element;
        private final String property;
        private final Element owner1;
        private final Element owner2;

        private Entry(Element modifiedElement, String changeDesc, Element owner1, Element owner2) {
            this.element = modifiedElement;
            this.property = changeDesc;
            this.owner1 = owner1;
            this.owner2 = owner2;
        }

        public Element getElement() {
            return element;
        }

        public String getProperty() {
            return property;
        }

    }

    public static class Diff {

        private final Element changed;
        private final List<String> changes = new ArrayList<>();

        public Diff(Element var1) {
            changed = var1;
        }

        public void addChange(String var1) {
            changes.add(var1);
        }

        public List<String> getChanges() {
            return changes;
        }

        public Element getChangedElement() {
            return changed;
        }

    }

    public Set<Entry> getAdded() {
        return added;
    }

    public Set<Entry> getRemoved() {
        return removed;
    }

    public Map<Element, Diff> getChanged() {
        return changed;
    }
}
