/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test;

import com.nomagic.magicdraw.commandline.CommandLineAction;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.esi.EsiUtils;
import com.nomagic.magicdraw.teamwork2.ITeamworkService;
import com.nomagic.magicdraw.teamwork2.ServerLoginInfo;
import com.nomagic.task.EmptyProgressStatus;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.io.File;

public class ProjectRetriever implements CommandLineAction {

    public String initProjectName;
    public String finalProjectName;
    public String pathToSaveProject;

    public String serverAddress;
    public String serverUser;
    public String serverPassword;


    public ProjectRetriever(String serverAddress, String serverUser, String serverPassword, String initProjectName, String finalProjectName, String pathToSaveProject) {
        this.initProjectName = initProjectName;
        this.finalProjectName = finalProjectName;
        this.pathToSaveProject = pathToSaveProject;
        this.serverAddress = serverAddress;
        this.serverUser = serverUser;
        this.serverPassword = serverPassword;
    }

    public byte execute(String[] args) {

        ITeamworkService iTeamworkService = EsiUtils.getTeamworkService();
        iTeamworkService.login(new ServerLoginInfo(serverAddress, serverUser, serverPassword, false), true);

        ProjectDescriptor projectDescriptor;
        try {
            projectDescriptor = iTeamworkService.getProjectDescriptorByQualifiedName(initProjectName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (projectDescriptor != null) {
            Application.getInstance().getProjectsManager().loadProject(projectDescriptor, EmptyProgressStatus.getDefault());

            File file = new File(pathToSaveProject, initProjectName + ".mdzip");
            EsiUtils.convertToLocal(OMFUtils.getProject(), file);
        }
            return 0;
    }

}
