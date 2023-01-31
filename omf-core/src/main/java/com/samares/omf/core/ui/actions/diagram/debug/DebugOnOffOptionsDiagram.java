/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.ui.actions.diagram.debug;


import com.google.common.base.Function;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserStateAction;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.listeners.OMFListenerManager;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.errors.OMFErrorHandler;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DebugOnOffOptionsDiagram extends DefaultBrowserStateAction {
    private final Object optionInstance;
    public String name = "";
    public Function<Element, Element> optionSetterFunction = null;
    public Function<Element, Element> optionGetterFunction = null;
    public Method optionSetterMethod = null;
    public Method optionGetterMethod;
//
//    public DebugOnOffOptions(String name, Function<Class, Boolean> optionSetterFunction, Function<Class, null> optionGetterFunction) {
//        super("", "[Debug] option state: " + name, null, null);
//        this.name = name;
//        this.optionSetterFunction = optionSetterFunction;
//        this.optionGetterFunction = optionGetterFunction;
//    }

    public DebugOnOffOptionsDiagram(String name, Object optionInstance, Method optionSetterMethod, Method optionGetterMethod) {
        super("", "[Debug] option state: " + name, null, null);
        this.name = name;
        this.optionInstance = optionInstance;
        this.optionSetterMethod = optionSetterMethod;
        this.optionGetterMethod = optionGetterMethod;
    }


    public void actionPerformed(ActionEvent e) {
        Runnable runnable = () -> {
            try {
                if (optionSetterMethod != null && optionGetterMethod != null)
                    activateDeactivateOption();
//                if(methodToTrigger != null)
//                    methodToTrigger.invoke(null, selectedElement);
            } catch (Exception exception) {
                OMFErrorHandler.handleException(exception, true);
            }
        };


        OMFListenerManager.getInstance().removeAllListeners();
        try {
            SessionManager.getInstance().executeInsideSession(OMFUtils.currentProject, "[Debug] option state: " + name + " in progress", runnable);
        } catch (Exception exception) {
            OMFErrorHandler.handleException(exception, false);
        }
        OMFListenerManager.getInstance().activateAllListeners();
    }

    public void activateDeactivateOption() throws InvocationTargetException, IllegalAccessException {
        boolean isOptionActivated = (boolean) optionGetterMethod.invoke(optionInstance);
        optionSetterMethod.invoke(optionInstance, !isOptionActivated);


    }

    /**
     * @see com.nomagic.actions.NMAction#updateState()
     */
    @Override
    public void updateState() {
        boolean isOptionActivated = false;
        try {
            isOptionActivated = (boolean) optionGetterMethod.invoke(optionInstance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            OMFErrorHandler.handleException(e, false);
        }
        setState(isOptionActivated);
    }

}
