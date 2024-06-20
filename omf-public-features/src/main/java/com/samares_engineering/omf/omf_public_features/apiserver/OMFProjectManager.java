package com.samares_engineering.omf.omf_public_features.apiserver;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.TwcAccessor;

public class OMFProjectManager {

    private TwcAccessor twcAccessor;
    private final String serverAddress;
    private final String serverUser;
    private final String userPassword;

    public OMFProjectManager() {
        this(System.getProperty("serverIp"),
                System.getProperty("userName"),
                System.getProperty("userPwd"));
    }

    public OMFProjectManager(String serverAddress, String serverUser, String userPassword) {
        this.serverAddress = serverAddress;
        this.serverUser = serverUser;
        this.userPassword = userPassword;
    }

    public Project openLocalProject(String projectPath) {
        ProjectsManager projectManager = Application.getInstance().getProjectsManager();
        return projectManager.getProject(projectPath);
    }

    public Project openTWCProject(String projectPath) throws LegacyOMFException {
        getTWCAccessor().openProject(projectPath);
        return OMFUtils.getProject();
    }

    private TwcAccessor getTWCAccessor() {
        if(twcAccessor == null)
            twcAccessor = new TwcAccessor(this.serverAddress, this.serverUser, this.userPassword);
        return twcAccessor;
    }
    private TwcAccessor getTWCAccessor(String serverAddress, String serverUser, String userPassword) {
        if(twcAccessor == null)
            twcAccessor = new TwcAccessor(serverAddress, serverUser, userPassword);
        return twcAccessor;
    }
}
