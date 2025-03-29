/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine

enum class LiveActionType(val actionName: String) {
    //Standard
    ANALYSE("ANALYSE"),
    CREATE("CREATE"),
    DELETE("DELETE"),
    UPDATE("UPDATE"),
    HISTORY("HISTORY"),
    AFTER_AUTOMATION("AFTER_AUTOMATION"),

    //Undo/Redo
    ANALYSE_UNDO_REDO("ANALYSE_UNDO_REDO"),
    CREATE_UNDO_REDO("CREATE_UNDO_REDO"),
    DELETE_UNDO_REDO("DELETE_UNDO_REDO"),
    UPDATE_UNDO_REDO("UPDATE_UNDO_REDO"),
    HISTORY_UNDO_REDO("HISTORY_UNDO_REDO"),
    AFTER_AUTOMATION_UNDO_REDO("AFTER_AUTOMATION_UNDO_REDO")
}