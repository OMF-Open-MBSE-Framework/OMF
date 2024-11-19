/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.templates.batches;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.modules.ModuleUsage;
import com.nomagic.magicdraw.esi.EsiUtils;
import com.nomagic.magicdraw.teamwork2.locks.ILockProjectService;
import com.nomagic.magicdraw.teamwork2.locks.LockService;
import com.nomagic.task.EmptyProgressStatus;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.TwcAccessor;
import com.samares_engineering.omf.omf_test_framework.projectmanager.TestCloseProjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public abstract class ATestBatchTWC extends ATestBatch {
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
            initProject = Objects.requireNonNull(OMFUtils.getProject(), "Current project is not set");
            Objects.requireNonNull(LockService.getLockService(getInitProject()), "Can't get lock service")
                    .lockElements(Collections.singleton(getInitProject().getPrimaryModel()), true, EmptyProgressStatus.getDefault());

        } catch (LegacyOMFException e) {
            LegacyErrorHandler.handleException(e, true);
        }
    }

    @Override
    public void endBatch(boolean shallSaveModel) {
        if(shallSaveModel) {
            commitProject();
            Objects.requireNonNull(LockService.getLockService(getInitProject()), "Can't get lock service")
                    .unlockElements(Collections.singleton(getInitProject().getPrimaryModel()), true, EmptyProgressStatus.getDefault());
        }

        new TestCloseProjects().testAction();
    }


    private Project getProjectFromTWC(TwcAccessor twcAccessor, String projectName) throws LegacyOMFException {
        twcAccessor.openProject(projectName);
        return OMFUtils.getProject();
    }

    private void commitProject(){
        ILockProjectService lockService = EsiUtils.getLockService(getInitProject());
        assert lockService != null;
        Collection<Element> lockedElements = lockService.getLockedByMe();
        Collection<ModuleUsage> lockedModules = lockService.getModulesLockedByMe();

        EsiUtils.commitProject(initProject, "commit for test reviewing", lockedElements, lockedModules, true, new ArrayList<>());
    }
}
