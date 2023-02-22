/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.features.example1;

import com.google.common.base.Strings;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.tests.MagicDrawTestCase;

public class OpenProject extends MagicDrawTestCase {

    String projectName = "";
    Project project;

    public OpenProject(String project){
        this.projectName = project;

    }

    public void testAction() {
        if(!Strings.isNullOrEmpty(projectName))
//            project = loadProject(new File(System.getProperty("tests.resources"), projectName).getAbsolutePath());
            project = MagicDrawTestCase.loadProject(projectName);
    }

}

