package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.stateactions.stateMDAction;

import com.nomagic.magicdraw.actions.MDStateAction;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Specialized MDStateAction for MenuAction
 */
public class StateMenuAction extends MDStateAction {

    public StateMenuAction(@Nullable String s, @Nullable String s1, @Nullable KeyStroke keyStroke, @Nullable String s2) {
        super(s, s1, keyStroke, s2);
    }
}
