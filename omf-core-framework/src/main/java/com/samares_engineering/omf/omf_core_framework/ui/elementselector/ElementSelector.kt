package com.samares_engineering.omf.omf_core_framework.ui.elementselector

import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider
import com.nomagic.magicdraw.ui.dialogs.SelectElementInfo
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlgFactory
import com.nomagic.magicdraw.ui.dialogs.selection.TypeFilter
import com.nomagic.magicdraw.ui.dialogs.selection.TypeFilterImpl
import com.nomagic.magicdraw.uml.BaseElement
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.ui.elementselector.exception.DialogCanceledByUser
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors

object ElementSelector {
    @Throws(DialogCanceledByUser::class)
    fun promptUserToSelectElement(vararg selectableFilters: Predicate<Element>): Optional<Element> {
        return promptUserToSelectElement("Select element", *selectableFilters)
    }

    @Throws(DialogCanceledByUser::class)
    fun promptUserToSelectElement(
        userPrompt: String?,
        vararg selectableFilters: Predicate<Element>
    ): Optional<Element> {
        val selectionDlg = ElementSelectionDlgFactory
            .create(MDDialogParentProvider.getProvider().dialogOwner, userPrompt, null)
        val selectableFilter: TypeFilter = object : TypeFilterImpl() {
            override fun accept(element: BaseElement, checkType: Boolean): Boolean {
                if (element !is Element) return false
                return Arrays.stream(selectableFilters).anyMatch { selectableFilter: Predicate<Element> ->
                    selectableFilter.test(
                        element
                    )
                }
            }
        }

        val selectElementInfo = SelectElementInfo(true, false, null, true)
        ElementSelectionDlgFactory.initSingle(
            selectionDlg, selectElementInfo, selectableFilter, selectableFilter,
            null, null
        )

        selectionDlg.isVisible = true

        if (!selectionDlg.isOkClicked) {
            throw DialogCanceledByUser()
        }
        return selectionDlg.selectedElements.stream().map { obj: BaseElement? -> Element::class.java.cast(obj) }
            .findFirst()
    }

    @Throws(DialogCanceledByUser::class)
    fun promptUserToSelectElements(userPrompt: String?, vararg selectableFilters: Predicate<Element>): List<Element> {
        val selectionDlg = ElementSelectionDlgFactory
            .create(MDDialogParentProvider.getProvider().dialogOwner, userPrompt, null)
        val selectableFilter: TypeFilter = object : TypeFilterImpl() {
            override fun accept(element: BaseElement, checkType: Boolean): Boolean {
                if (element is Element) {
                    return Arrays.stream(selectableFilters).anyMatch { selectableFilter: Predicate<Element> ->
                        selectableFilter.test(
                            element
                        )
                    }
                } else return false
            }
        }

        val selectElementInfo = SelectElementInfo(true, false, null, true)

        ElementSelectionDlgFactory.initMultiple(
            selectionDlg, selectElementInfo, selectableFilter, selectableFilter, true,
            ArrayList<Any?>(), ArrayList<Any?>()
        )

        selectionDlg.isVisible = true

        if (!selectionDlg.isOkClicked) {
            throw DialogCanceledByUser()
        }
        return selectionDlg.selectedElements.stream().map { obj: BaseElement? -> Element::class.java.cast(obj) }
            .collect(Collectors.toList())
    }
}
