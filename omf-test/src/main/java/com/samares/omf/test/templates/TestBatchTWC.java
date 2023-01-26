/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.test.templates;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.modules.ModuleUsage;
import com.nomagic.magicdraw.esi.EsiUtils;
import com.nomagic.magicdraw.teamwork2.locks.ILockProjectService;
import com.nomagic.magicdraw.teamwork2.locks.LockService;
import com.nomagic.task.EmptyProgressStatus;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.utils.errorManagement.OMFErrorHandler;
import com.samares.omf.core.utils.errorManagement.exceptions.OMFException;
import com.samares.omf.test.projectmanager.TestCloseProjects;
import com.samares.omf.test.utils.TwcAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class TestBatchTWC extends ATestBatch {

    public String serverAddress;
    public String serverUser;
    public String userPassword;
    public String projectInitName;
    public String projectOracleName;

    public TwcAccessor twcAccessor;

    public String branchName;
    public String branchDescription;


    @Override
    public void initVariable() {

        twcAccessor = new TwcAccessor(serverAddress, serverUser, userPassword);

        // To be sure those variables are not set
        initZipProject = null;
        oracleZipProject = null;

        branchName = "Test_Branch";
        branchDescription = "Branch created for tests only";
    }

    @Override
    public void startBatch() {
        try {
            oracleProject = getProjectFromTWC(twcAccessor, projectOracleName);
            twcAccessor.createBranch(projectInitName, branchName, branchDescription);
            twcAccessor.openBranchProject(projectInitName, branchName);
            initProject = OMFUtils.currentProject;
            Objects.requireNonNull(LockService.getLockService(initProject), "Can't get lock service")
                    .lockElements(Collections.singleton(initProject.getPrimaryModel()), true, EmptyProgressStatus.getDefault());

        } catch (OMFException e) {
            OMFErrorHandler.handleException(e, true);
        }
    }

    @Override
    public void endBatch(boolean shallSaveModel) {
        if(shallSaveModel) {
            commitProject();
            Objects.requireNonNull(LockService.getLockService(initProject), "Can't get lock service")
                    .unlockElements(Collections.singleton(initProject.getPrimaryModel()), true, EmptyProgressStatus.getDefault());
        }

        new TestCloseProjects().testAction();
    }


    public Project getProjectFromTWC(TwcAccessor twcAccessor, String projectName) throws OMFException {
        twcAccessor.openProject(projectName);
        return OMFUtils.currentProject;
    }

    public void commitProject(){
        ILockProjectService lockService = EsiUtils.getLockService(initProject);
        assert lockService != null;
        Collection<Element> lockedElements = lockService.getLockedByMe();
        Collection<ModuleUsage> lockedModules = lockService.getModulesLockedByMe();

        EsiUtils.commitProject(initProject, "commit for test reviewing", lockedElements, lockedModules, true, new ArrayList<>());
    }
}
