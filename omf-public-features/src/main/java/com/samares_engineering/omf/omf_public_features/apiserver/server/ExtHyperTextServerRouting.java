package com.samares_engineering.omf.omf_public_features.apiserver.server;

import com.google.common.base.Strings;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.nomagic.magicdraw.hyperlinks.Hyperlink;
import com.nomagic.magicdraw.hyperlinks.HyperlinkUtils;
import com.nomagic.magicdraw.uml.BaseElement;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.DevelopmentException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.NoElementFoundException;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.apiserver.OMFProjectManager;
import com.samares_engineering.omf.omf_public_features.apiserver.RequestHandler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class ExtHyperTextServerRouting {
    public static RequestHandler refModel(){
        return new RequestHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {

                String id = request.getParameter("ID");
                String projectPath = request.getParameter("projectPath");

                if(Strings.isNullOrEmpty(id)){
                    notFound("[Error] ID is Null", target, baseRequest, request, response);
                    return;
                }

                if(!Strings.isNullOrEmpty(projectPath)) handleProjectOpening(projectPath);

                try {
                    handleOpenElementInBrowser(id);
                } catch (DevelopmentException e) {
                    throw new RuntimeException(e);
                }
                String answer = "<h1>Element opened successfully!</h1>"
                        + "\n" + "<p>The element with ID " + id + " has been opened in the browser.</p>";
                successAnswer(answer, target, baseRequest, request, response);
            }
        };


    }

    private static void handleOpenElementInBrowser(String id) throws DevelopmentException {
      try {
          OMFUtils.selectElementInContainmentTree(id);
      } catch (NoElementFoundException e) {
          LegacyErrorHandler.handleException(e, false);
      }
    }
    private static void handleOpenElementSpecification(String id) throws DevelopmentException {
        try {
            OMFUtils.selectElementInContainmentTree(id);
        } catch (NoElementFoundException e) {
            LegacyErrorHandler.handleException(e, false);
        }
    }

    //ROUTING
    public static RequestHandler openProject() {
        return new RequestHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
                String projectPath = request.getParameter("projectPath");


                if (Strings.isNullOrEmpty(projectPath)) {
                    notFound("[Error] project path is Null", target, baseRequest, request, response);
                    return;
                }

                handleProjectOpening(projectPath);

                successAnswer("Project " + OMFUtils.getProject().getName() + " Opened", target, baseRequest, request, response);
            }
        };
    }

    private static Project handleProjectOpening(String projectPath) {
        ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
        File file = new File(projectPath);

        ProjectDescriptor projectDescriptor = ProjectDescriptorsFactory.createProjectDescriptor(file.toURI());
        projectsManager.loadProject(projectDescriptor, false);

        return OMFUtils.getProject();

    }

    public static RequestHandler openTWCProject() {
        return new RequestHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
                String projectPath = request.getParameter("projectPath");
                if (projectPath.isBlank() || projectPath.isEmpty()) {
                    notFound("[Error] project path is Null", target, baseRequest, request, response);
                    return;
                }

                try {
                    handleTWCProjectOpening(projectPath);
                } catch (LegacyOMFException e) {
                    throw new RuntimeException(e);
                }
                successAnswer("Project " + OMFUtils.getProject().getName() + " Opened", target, baseRequest, request, response);
            }
        };
    }

    private static Project handleTWCProjectOpening(String projectPath) throws LegacyOMFException {
        return new OMFProjectManager().openTWCProject(projectPath);
    }

    public static RequestHandler openSpecification() {
        return new RequestHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
                String id = request.getParameter("ID");
                String projectPath = request.getParameter("projectPath");

                if(Strings.isNullOrEmpty(id)){
                    notFound("[Error] ID is Null", target, baseRequest, request, response);
                    return;
                }

                if(!Strings.isNullOrEmpty(projectPath)) handleProjectOpening(projectPath);

                try {
                    handleOpenElementSpecification(id);
                } catch (DevelopmentException e) {
                    throw new RuntimeException(e);
                }
                String answer = "<h1>Element opened successfully!</h1>"
                        + "\n" + "<p>The element with ID " + id + " has been opened in the browser.</p>";
                successAnswer(answer, target, baseRequest, request, response);
            }
        };
    }

    public void handleElement(String id){
        BaseElement elem = OMFUtils.getProject().getElementByID(id);
        Hyperlink hypertext = HyperlinkUtils.createHyperlink("TEST", elem);
        Application.getInstance().getMainFrame().getBrowser().getActiveTree().openNode(elem);
//        hypertext.
    }
}
