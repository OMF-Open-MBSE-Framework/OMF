package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.stateactions.stateMDAction;

import com.nomagic.magicdraw.actions.BrowserAction;
import com.nomagic.magicdraw.actions.MDStateAction;
import com.nomagic.magicdraw.ui.browser.Tree;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Specialized MDStateAction for BrowserAction
 */
public class StateBrowserAction extends MDStateAction implements BrowserAction {

    private Tree tree;

    public StateBrowserAction(@Nullable String s, @Nullable String s1, @Nullable KeyStroke keyStroke, @Nullable String s2) {
        super(s, s1, keyStroke, s2);
    }

    @Override
    public void setTree(@Nullable Tree tree) {
        this.tree = tree;
    }

}

