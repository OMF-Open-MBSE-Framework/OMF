/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine;

public enum LiveActionType {
    ANALYSE("ANALYSE"),
    CREATE("CREATE"),
    DELETE("DELETE"),
    UPDATE("UPDATE"),
    AFTER_AUTOMATION("AFTER_AUTOMATION");

    private final String name;

    LiveActionType(String name){
        this.name = name;
    }

}
