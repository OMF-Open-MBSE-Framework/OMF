/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine;


import com.nomagic.magicdraw.utils.PriorityProvider;
import com.samares_engineering.omf.omf_core_framework.feature.OMFAutomationManager;
import com.samares_engineering.omf.omf_core_framework.feature.RegistrableFeatureItem;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.LiveAction;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public interface LiveActionEngine<EVT> extends PriorityProvider, RegistrableFeatureItem {



    /* *************************** OLD METHODS DEPRECATED ******************************* */
    Optional<LiveAction<EVT, EVT>> getMatchingLiveAction(EVT evt);

    List<LiveAction<EVT, EVT>> getAllMatchingLiveActions(EVT evt);

    boolean processAllMatchingLiveActions(EVT evt);

    boolean skipLiveActions(EVT evt);

    void addLiveAction(LiveAction<EVT, EVT> liveAction);

    void addAllLiveActions(List<LiveAction<EVT, EVT>> liveActions);

    List<LiveAction<EVT, EVT>> getLiveActions();

    void removeLiveAction(LiveAction<EVT, EVT> liveAction);

    void removeLiveActions(List<LiveAction<EVT, EVT>> liveActions);

    void removeAllLiveActions();

    void setPriority(int priority);

    default <T> boolean checkLiveActionEngineType(Class<T> clazz) {
        Type[] genericInterfaces = getClass().getGenericInterfaces();
        if (genericInterfaces.length == 0) return false;
        ParameterizedType genericInterface = (ParameterizedType) genericInterfaces[0];
        return clazz.isAssignableFrom((Class<?>) genericInterface.getActualTypeArguments()[0]);
    }

    default boolean hasAutomationTriggered(){
        return OMFAutomationManager.getInstance().noAutomationTriggered();
    }

    default boolean noAutomationTriggered(){
        return OMFAutomationManager.getInstance().noAutomationTriggered();
    }

    String getType();
    void setType(String category);
}
