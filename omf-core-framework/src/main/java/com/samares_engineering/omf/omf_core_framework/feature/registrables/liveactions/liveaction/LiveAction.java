/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.KeepListenerActivated;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;

public interface LiveAction<I, O> {
    boolean matches(I e);

    /**
     * Process the event.
     * @param e the event to process
     * @return the event after processing
     */
    O process(I e);

    boolean isActivated();

    void setActivated(boolean activated);

    String getId();

    boolean isBlocking();

    void setLiveActionEngine(LiveActionEngine<I> liveActionEngine);

    LiveActionEngine<I> getLiveActionEngine();

    default boolean keepListenerActivated() {return getClass().isAnnotationPresent(KeepListenerActivated.class);}
}
