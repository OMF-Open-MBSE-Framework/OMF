package com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams;

import com.nomagic.magicdraw.actions.ActionsExecuter;
import com.nomagic.magicdraw.context.PropertyPathChangeManager;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.DisplayPathElements;
import com.nomagic.magicdraw.uml.symbols.OrthogonalLinkDiagramLayouterCustomSelection;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.shapes.DiagramFrameView;
import com.nomagic.magicdraw.uml.symbols.shapes.PartView;
import com.nomagic.magicdraw.uml.symbols.shapes.PortView;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFLogException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import javax.annotation.CheckForNull;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier.SILENT;

/**
 * This class is used to display the parts and ports of a block in a diagram in an efficient way.
 * Please note that multi-instance safe has not been tested.<br>
 * (multi-instance safe means that the same block or an Interface is displayed multiple time in the same context)
 */
public class LayoutManager {
    final Set<PresentationElement> allDisplayedElements = new HashSet<>();

    final DiagramPresentationElement diagramPresentationElement;
    final Diagram diagram;

    final PresentationElementsManager manager;

    public LayoutManager(DiagramPresentationElement diagramPE) {
        this.diagramPresentationElement = diagramPE;
        this.diagram = diagramPresentationElement.getDiagram();
        this.manager = PresentationElementsManager.getInstance();
    }

    /**
     * Display the part (if not already displayed) in the diagram.<br>
     * NOTE: the owner of the part must be displayed first.
     * @param part The part to display
     * @return The PresentationElement of the part
     */
    public PresentationElement refreshPart(Property part) {
        //Find the parent part PresentationElement
        PresentationElement parentPresentationElement = diagramPresentationElement;
        if (part.getOwner() != diagram.getOwner()) {//If the part is not directly owned by the diagram owner, we need to find the parent part
            Class partOwner = (Class) part.getOwner();
            Optional<PresentationElement> optParentOwnerPE = partOwner.get_typedElementOfType().stream()
                    .map(parentPart -> diagramPresentationElement.findPresentationElement(parentPart, PartView.class))
                    .filter(Objects::nonNull)
                    .findFirst();
            if (optParentOwnerPE.isEmpty())
                throw new OMFLogException("Parent part not found for part " + part.getHumanName() + ". Please display the part manually."); //TODO: Create a custom Layout exception
            parentPresentationElement = optParentOwnerPE.get();
        }

        //Display the part if it is not already displayed
        PresentationElement partPresentationElement = null;
        partPresentationElement = createShapeForPart(part, partPresentationElement, parentPresentationElement);
        if (partPresentationElement != null) {
            allDisplayedElements.add(partPresentationElement);
        }
        return partPresentationElement;
    }


    /**
     * Display all the nested ports recursively.<br>
     * NOTE: the owner of the port (the first one) must be displayed first.
     * @param firstPort The first port to display
     * @return The list of all the displayed ports
     */
    public Collection<? extends PresentationElement> displayAllNestedPortRecursively(Port firstPort) {
        List<PresentationElement> allDisplayedPorts = new ArrayList<>();
        List<PresentationElement> allPortToExpands = diagramPresentationElement.findPresentationElementsForPathConnecting(firstPort, PortView.class).collect(Collectors.toList());
        Class typeInterface = (Class) firstPort.getType();

        if (typeInterface == null) return allDisplayedPorts;

        for (PresentationElement hostingPortPE : allPortToExpands) {
            List<PresentationElement> PEUnderHostingPort = hostingPortPE.getManipulatedPresentationElements();
            Predicate<Port> doesNotExist = nestedPort -> diagramPresentationElement.findPresentationElementsForPathConnecting(nestedPort, PortView.class)
                    .noneMatch(PEUnderHostingPort::contains);

            Collection<Port> ownedPort = typeInterface.getOwnedPort();
            if (ownedPort != null) {
                ownedPort.stream()
                        .filter(doesNotExist).forEach(nestedPort -> {
                            PresentationElement nestedPortPE = createPortShapeElement(nestedPort, hostingPortPE);
                            allDisplayedPorts.add(nestedPortPE);
                            allDisplayedPorts.addAll(displayAllNestedPortRecursively(nestedPort));
                        });
            }
        }
        allDisplayedElements.addAll(allDisplayedPorts);
        return allDisplayedPorts;
    }

    /**
     * Display all the ports of a block in the diagram.<br>
     * NOTE: the part must be displayed first.
     * @param block The block to display
     * @param part The part of the block to display (if null, the block is displayed directly on the diagram border)
     * @return The list of all the displayed ports
     */
    public List<PresentationElement> refreshAllPorts(Class block, Property part) {
        List<PresentationElement> createdPortPE = new ArrayList<>();
        if (part != null) {
            try {
                PresentationElement portPresentationElement = diagramPresentationElement.findPresentationElement(part, PartView.class);
                if (portPresentationElement != null) {
                    createdPortPE.addAll(createShapeElementForPort(block, diagramPresentationElement, portPresentationElement));
                } else {
                    createdPortPE.add(portPresentationElement);
                }
            } catch (Exception e) {
                OMFLogger.err("Error during port refreshing.", e);
            }
        } else {
            try {
                PresentationElement ibdFramePresentationElement = diagramPresentationElement.findPresentationElement(diagramPresentationElement.getDiagram(), DiagramFrameView.class);
                createdPortPE.addAll(createShapeElementForPort(block, diagramPresentationElement, ibdFramePresentationElement));
            } catch (Exception e) {
                Application.getInstance().getGUILog().log(e.getMessage());
            }
        }
        allDisplayedElements.addAll(createdPortPE);
        return createdPortPE;
    }

    /**
     * Display all the paths FROM all the previous elements created.<br>
     * Meaning that all path going from or to the given elements will be displayed.<br>
     * NOTE: the elements must be displayed first.
     * NOTE2: This method has been retro-engineered from the DisplayPathElements.displayPathElements method.
     */
    public void displayAllPaths(){
        displayAllPaths(allDisplayedElements);
    }
    /**
     * Display all the paths FROM the given elements in the diagram.<br>
     * Meaning that all path going from or to the given elements will be displayed.<br>
     * NOTE: the elements must be displayed first.
     * NOTE2: This method has been retro-engineered from the DisplayPathElements.displayPathElements method.
     * @param presentationElements The elements to display the path between
     */
    public void displayAllPaths(Collection<PresentationElement> presentationElements){
        if (presentationElements != null && !presentationElements.isEmpty()) {
            PropertyPathChangeManager.getInstance(OMFUtils.getProject()).executeWithoutUpdatingPropertyPath(() -> {
                createConnectorPaths("CREATE_PATHS", presentationElements);
            });
        }
    }
    /**
     * Display all the paths BETWEEN the given elements in the diagram.<br>
     * Meaning that only path between elements of the collection will be displayed.<br>
     * NOTE: the elements must be displayed first.
     * NOTE2: This method has been retro-engineered from the DisplayPathElements.displayPathElements method.
     * @param presentationElements The elements to display the path between
     */
    public void displayAllPathsBetweenElementsOnly(Collection<PresentationElement> presentationElements){
        if (presentationElements != null && !presentationElements.isEmpty()) {
            PropertyPathChangeManager.getInstance(OMFUtils.getProject()).executeWithoutUpdatingPropertyPath(() -> {
                createConnectorPaths("BETWEEN_PATH_CREATION", presentationElements); //Instruction name does not matter
            });
        }
    }

    /**
     * Actual method to display the paths from the given elements.<br>
     * NOTE: This method has been retro-engineered from the DisplayPathElements.displayPathElements method.
     * @param displayInstruction "CREATE_PATHS" to display all path or "BETWEEN_PATH_CREATION" to display only path between elements of the collection
     * @param peWithPathToDisplay The elements to display the path between
     */
    private void createConnectorPaths(String displayInstruction, Collection<PresentationElement> peWithPathToDisplay) {
        DisplayPathElements.DisplayPathOptions displayPathOption = new DisplayPathElements.DisplayPathOptions();
        if ("CREATE_PATHS".equals(displayInstruction)) {
            displayPathOption.setDisplayPathsToSelf(true);
        } else {
            peWithPathToDisplay = peWithPathToDisplay.size() > 1 ? peWithPathToDisplay : null;
            displayPathOption.setOnlyBetweenThese(peWithPathToDisplay);
            displayPathOption.setDisplayPathsToSelf(false);
        }

        DisplayPathElements.displayPathElements(peWithPathToDisplay, displayPathOption);
    }

    /**
     * Create the shape for the given part.<br>
     * @param part The part to display
     * @param partPresentationElement The PresentationElement of the part (if null, it will be created)
     * @param parentPresentationElement The PresentationElement of the parent part.
     * @return The PresentationElement of the part
     */
    private PresentationElement createShapeForPart(Property part, PresentationElement partPresentationElement, @CheckForNull PresentationElement parentPresentationElement) {
        try {
            partPresentationElement = diagramPresentationElement.findPresentationElement(part, PartView.class);

            if (partPresentationElement == null) {
                partPresentationElement = manager.createPartShape(part, parentPresentationElement, null, false, new Point(100, 100));
            }
        } catch (Exception e) {
            throw new OMFCriticalException("Error during part refreshing.", e, SILENT);
        }
        return partPresentationElement;
    }

    /**
     * Create the shape for the ports of the given block.<br>
     * @param block The block to display
     * @param diagramPresentationElement The PresentationElement of the diagram
     * @param partPresentationElement The PresentationElement of the part (if null, it will be created)
     * @return The list of all the created PresentationElement
     */
    private List<PresentationElement> createShapeElementForPort(Class block, DiagramPresentationElement diagramPresentationElement,
                                                                PresentationElement partPresentationElement) {
        return block.getOwnedPort().stream()
                .filter(port -> diagramPresentationElement.findPresentationElement(port, PortView.class) == null)
                .map(port -> createPortShapeElement(port, partPresentationElement))
                .collect(Collectors.toList());

    }

    /**
     * Create the shape for the given port.<br>
     * @param nestedPort The port to display
     * @param hostingPortPE The PresentationElement of the hosting port
     * @return The PresentationElement of the port
     */
    private PresentationElement createPortShapeElement(Port nestedPort, PresentationElement hostingPortPE) {
        try {
            allDisplayedElements.add(hostingPortPE);
            return manager.createShapeElement(nestedPort, hostingPortPE);
        } catch (ReadOnlyElementException e) {
            throw new OMFCriticalException("Error during creation of nested port");
        }
    }

    /**
     * Get all the displayed elements
     * @return The set of all the displayed elements
     */
    public Set<PresentationElement> getAllDisplayedElements() {
        return allDisplayedElements;
    }


    public void applyQuickLayout(List<PresentationElement> presentationElementsToLayout) {
        OrthogonalLinkDiagramLayouterCustomSelection orthoLayouter = new OrthogonalLinkDiagramLayouterCustomSelection(presentationElementsToLayout);
        ActionsExecuter.runLayoutTask(orthoLayouter, diagramPresentationElement);
    }

    public void applyQuickLayout() {
        Application.getInstance().getActionsManager().getActionsExecuter().layout(true);
    }
}
