package com.samares_engineering.omf.omf_test_framework.utils.omffeaturehelpers;

import com.nomagic.magicdraw.ui.browser.ContainmentTree;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.uml.ElementIcon;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_test_framework.templates.AbstractTestCase;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.Assert;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class APITestComponent {
    private final AbstractTestCase testCase;
    static final String DEFAULT_SERVER_PORT = "9850";
    private final String DEFAULT_SERVER_URL  = "http://localhost:";
    String serverPort;
    String serverUrl;
    private ContentResponse response;
    HttpClient  httpClient;


    public APITestComponent(AbstractTestCase testCase) {
        this.testCase = testCase;
        httpClient = new HttpClient();
        try {
            httpClient.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        serverPort = DEFAULT_SERVER_PORT;
        serverUrl = DEFAULT_SERVER_URL + serverPort + "/";
    }
    public void openProjectUsingServerAPI(String projectPath) {
        String url = replaceBackSlash(serverUrl + "openProject/?projectPath=" + new File(projectPath).getPath());
        sendGetRequest(httpClient, url);

    }

    private String doubleBackslash(String path) {
       return path.replaceAll("(?<!\\\\)\\\\(?!\\\\)", "\\\\\\\\");
    }
    private String replaceBackSlash(String path) {
       return path.replaceAll("\\\\", "/");
    }

    void openProjectUsingServerAPI() {
        openProjectUsingServerAPI(testCase.getInitZipProject());
    }



    ContentResponse sendGetRequest(HttpClient httpClient, String url) {
//        url = url.replace("\\", "%5C");
        try {
            response = httpClient.GET(url);
            didResponseSucceed();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
    public void didResponseSucceed() {
        Assert.assertEquals(200, response.getStatus());
    }

    public ContentResponse getResponse() {
        return response;
    }

    public String getServerPort() {
        return serverPort;
    }
    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void selectElementInContainmentTree(String elementID) {
        Element testedElement = testCase.findTestedElementByID(elementID);

        ContainmentTree containmentTree = testCase.getContainmentTree();
        containmentTree.setSelectedNodes(new Node[]{new Node(testedElement, ElementIcon.getIcon(testedElement))});

        String url = replaceBackSlash(serverUrl + "refmodel/?ID=" + elementID);
        sendGetRequest(httpClient, url);
    }

    public void verifyResultElementSelection(String id) {
        String expectedContent = "Element : " + id + " selected";

        ContainmentTree containmentTree = testCase.getContainmentTree();


        Node selectedNode = containmentTree.getSelectedNode();
        if (selectedNode == null)
            Assert.fail("No node has been selected, expected selectedElement id = " + id + " ");
        Element selectedElement = (Element) selectedNode.getUserObject();
        Assert.assertEquals("Selected element does not match: ", id, selectedElement.getID());

    }


    public void openProjectUsingServerAPI(File projectFile) {
        openProjectUsingServerAPI(projectFile.getAbsolutePath());
    }
}