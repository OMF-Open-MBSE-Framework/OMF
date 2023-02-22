/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.test.projectcomparator;

import com.nomagic.magicdraw.tests.MagicDrawTestCase;
import com.nomagic.magicdraw.tests.common.comparators.ProjectsComparator;

public class CustomMagicDrawTestCase extends MagicDrawTestCase {

    public ProjectsComparator createProjectComparator(String logfile){
        return super.createProjectComparator(logfile);
    }

}
