/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.projectcomparator;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.tests.common.comparators.ProjectsComparator;

public class CustomProjectComparator extends ProjectsComparator {

    @Override
    public boolean compare(Project project, Project project1) {
        return super.compare(project, project1);
    }
}
