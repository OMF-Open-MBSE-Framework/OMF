package com.samares_engineering.omf.omf_core_framework.utils.connections;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;

import java.util.*;

public class PartOrdering {

    /**
     * Get the list of parts ordered by depth: first the part directly in the simulation architecture, then the nested parts under it, etc.
     * @param startingPoint the starting point
     * @param allParts all the parts to order
     * @return the ordered list of parts
     */
    public List<Property> getOrderedPartByDepth(Element startingPoint, List<Property> allParts) {
        List<Property> orderedParts = new ArrayList<>();
        Set<Property> visited = new HashSet<>();
        Queue<Property> queue = new LinkedList<>();

        // Add initial parts directly related to the starting point
        for (Property part : allParts) {
            if (isDirectlyRelated(startingPoint, part)) {
                queue.offer(part);
                visited.add(part);
            }
        }

        // BFS to order parts by depth
        while (!queue.isEmpty()) {
            Property currentPart = queue.poll();
            orderedParts.add(currentPart);

            for (Property part : allParts) {
                if (!visited.contains(part) && isChildOf(currentPart, part)) {
                    queue.offer(part);
                    visited.add(part);
                }
            }
        }

        return orderedParts;
    }

    /**
     * Check if a part is directly related to the starting point.
     * This is a stub method and should be implemented according to your specific logic.
     */
    private boolean isDirectlyRelated(Element startingPoint, Property part) {
        return startingPoint.getOwnedElement().contains(part);
    }

    /**
     * Check if one part is a child of another part.
     * This is a stub method and should be implemented according to your specific logic.
     */
    private boolean isChildOf(Property parent, Property child) {
        if(parent.getType() == null) return false;
        return isDirectlyRelated((Class) parent.getType(), child);
    }

}
