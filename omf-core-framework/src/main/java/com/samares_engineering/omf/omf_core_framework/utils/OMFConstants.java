/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.utils;

import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

public class OMFConstants {
    public final static String SYSML_PACKAGE_NAME = "SysML";
    public final static String SYSML_LIBRARY_PACKAGE_NAME = "Libraries";

    public static boolean GUI_REQUIRED = true;
    public static boolean DEBUG_MODE_ACTIVATED = false;

    public static Stereotype stereotypeExample;
    static public void reinitConstants() {
//        stereotypeExample = Profile.getInstance().getSysml().getBlock();
    }
}

