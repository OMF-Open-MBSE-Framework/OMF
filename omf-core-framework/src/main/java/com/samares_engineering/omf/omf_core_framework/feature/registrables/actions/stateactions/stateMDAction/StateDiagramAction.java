package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.stateactions.stateMDAction;

import com.nomagic.magicdraw.actions.DiagramAction;
import com.nomagic.magicdraw.actions.MDStateAction;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Specialized MDStateAction for DiagramAction
 */
public class StateDiagramAction extends MDStateAction implements DiagramAction {

    DiagramPresentationElement diagramPresentationElement;

    public StateDiagramAction(@Nullable String s, @Nullable String s1, @Nullable KeyStroke keyStroke, @Nullable String s2) {
        super(s, s1, keyStroke, s2);
    }

    @Override
    public void setDiagram(@Nullable DiagramPresentationElement diagramPresentationElement) {
        this.diagramPresentationElement = diagramPresentationElement;
    }

    public DiagramPresentationElement getDiagram() {
        return diagramPresentationElement == null ? null : this.diagramPresentationElement;
    }
}
