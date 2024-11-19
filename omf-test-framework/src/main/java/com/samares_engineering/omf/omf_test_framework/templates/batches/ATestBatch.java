/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.templates.batches;

import com.nomagic.magicdraw.core.Project;

public abstract class ATestBatch {
    public  String initZipProject = "";
    public  String oracleZipProject = "";

    public  Project initProject;
    public  Project oracleProject;

    public ATestBatch(){
        initVariable();
    }

    public abstract void initVariable();

    public abstract void startBatch();
    public abstract void endBatch(boolean shallSaveModel);

    public String getInitZipProject() {
        return initZipProject;
    }

    public void setZipProjectInitial(String initZipProject) {
        this.initZipProject = initZipProject;
    }

    public String getOracleZipProject() {
        return oracleZipProject;
    }

    public void setZipProjectExpected(String oracleZipProject) {
        this.oracleZipProject = oracleZipProject;
    }

    public Project getInitProject() {
        return initProject;
    }

    public void setInitProject(Project initProject) {
        this.initProject = initProject;
    }

    public Project getOracleProject() {
        return oracleProject;
    }

    public void setOracleProject(Project oracleProject) {
        this.oracleProject = oracleProject;
    }
}
