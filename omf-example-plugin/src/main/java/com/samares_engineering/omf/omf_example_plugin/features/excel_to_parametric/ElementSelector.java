package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric;

import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.ui.dialogs.SelectElementInfo;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlg;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlgFactory;
import com.nomagic.magicdraw.ui.dialogs.selection.TypeFilter;
import com.nomagic.magicdraw.ui.dialogs.selection.TypeFilterImpl;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.exception.DialogCanceledByUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ElementSelector {
    // Suppress default constructor for noninstantiability
    private ElementSelector() {
    }

    public static Optional<Element> promptUserToSelectElement(Predicate<Element>... selectableFilters)
            throws DialogCanceledByUser {
        return promptUserToSelectElement("Select element", selectableFilters);
    }

    public static Optional<Element> promptUserToSelectElement(String userPrompt, Predicate<Element>... selectableFilters)
            throws DialogCanceledByUser {
        ElementSelectionDlg selectionDlg = ElementSelectionDlgFactory
                .create(MDDialogParentProvider.getProvider().getDialogOwner(), userPrompt, null);
        TypeFilter selectableFilter = new TypeFilterImpl() {
            @Override
            public boolean accept(BaseElement element, boolean checkType) {
                if (!(element instanceof Element)) return false;
                Element elem = (Element) element;
                return Arrays.stream(selectableFilters).anyMatch(selectableFilter -> selectableFilter.test(elem));
            }
        };

        SelectElementInfo selectElementInfo = new SelectElementInfo(true, false, null, true);
        ElementSelectionDlgFactory.initSingle(selectionDlg, selectElementInfo, selectableFilter, selectableFilter,
                null, null);

        selectionDlg.setVisible(true);

        if (!selectionDlg.isOkClicked()) {
            throw new DialogCanceledByUser();
        }
        return selectionDlg.getSelectedElements().stream().map(Element.class::cast).findFirst();
    }

    public static List<Element> promptUserToSelectElements(String userPrompt, Predicate<Element>... selectableFilters)
            throws DialogCanceledByUser {
        ElementSelectionDlg selectionDlg = ElementSelectionDlgFactory
                .create(MDDialogParentProvider.getProvider().getDialogOwner(), userPrompt, null);
        TypeFilter selectableFilter = new TypeFilterImpl() {
            @Override
            public boolean accept(BaseElement element, boolean checkType) {
                if (element instanceof Element) {
                    Element elem = (Element) element;
                    return Arrays.stream(selectableFilters).anyMatch(selectableFilter -> selectableFilter.test(elem));
                } else
                    return false;
            }
        };

        SelectElementInfo selectElementInfo = new SelectElementInfo(true, false, null, true);

        ElementSelectionDlgFactory.initMultiple(selectionDlg, selectElementInfo, selectableFilter, selectableFilter, true,
                new ArrayList<>(), new ArrayList<>());

        selectionDlg.setVisible(true);

        if (!selectionDlg.isOkClicked()) {
            throw new DialogCanceledByUser();
        }
        return selectionDlg.getSelectedElements().stream().map(Element.class::cast).collect(Collectors.toList());
    }
}
