/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.test.templates;

import com.google.common.base.Strings;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.ActionsProvider;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.options.AbstractPropertyOptionsGroup;
import com.nomagic.magicdraw.core.options.ProjectOptions;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.properties.Property;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.magicdraw.tests.MagicDrawTestCase;
import com.nomagic.magicdraw.tests.common.comparators.ProjectsComparator;
import com.nomagic.magicdraw.ui.browser.ContainmentTree;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.uml.ElementIcon;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.shapes.AbstractHeaderShapeView;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.test.BatchLauncher;
import com.samares.omf.test.utils.TestHelper;
import com.samares.omf.test.utils.TestLogger;
import org.junit.*;
import org.junit.jupiter.api.Assertions;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractTestCase extends MagicDrawTestCase{
    protected String testCaseID;
    protected TestLogger loggerTest;
    protected Project initProject;
    protected Project oracleProject;

    protected String initZipProject;
    protected String oracleZipProject;

    protected ATestBatch testBatch;
    protected boolean oracleNeeded = true;

    protected String testPackageName;


    public AbstractTestCase() {
        this.loggerTest     = new TestLogger(getLogger());
        this.testBatch      = BatchLauncher.currentBatch;
        this.initProject    = testBatch.getInitProject();
        this.oracleProject  = testBatch.getOracleProject();
        this.initZipProject = testBatch.getInitZipProject();
        this.oracleZipProject = testBatch.getOracleZipProject();
    }

    public AbstractTestCase(TestLogger logger) {
        this.loggerTest = logger;
    }

    /**
     * Rule to calculate/show time for each testcase
     */
    @Rule
    public TestRule watcher = new TestWatcher() {
        protected Instant start;

        protected void starting(Description description) {
            if(Strings.isNullOrEmpty(getName()))
                setName(description.getMethodName());
            loggerTest.log("");
            loggerTest.log("------------------------------------------------");
            loggerTest.status("- [TEST] - Starting test: " + getName());
            start = Instant.now();
        }

        @Override
        protected void finished(Description description) {
            Instant end = Instant.now();
            Duration timeElapsed = Duration.between(start, end);
            if(!isFailed())
                loggerTest.success("- [RESULT] " + getName() + " : " + "PASSED");
            else
                loggerTest.err("xxx [RESULT] " + getName() + " : " + "FAILED");
            loggerTest.log("- [TIME] :" + timeElapsed.toMinutesPart() + "min" + timeElapsed.toSecondsPart() + "s");
            loggerTest.log("------------------------------------------------");
            loggerTest.log("");
            loggerTest.showLogs();
        }
    };

    /**
     * To setUp before the tests execution
     *
     * @throws Exception
     */
    @Before
    public void setUpTest() throws Exception {
        initVariables();
        setSkipMemoryTest(true);
        super.setUpTest();
        checkPrecondition();
        initEnvOptions();

    }

    protected void checkPrecondition(){
        verifyProjectLoading();
    }


    public abstract void initVariables();


    public abstract void initEnvOptions();

    /**
     * To do after the tests execution
     *
     * @throws Exception
     */
    @After
    public void tearDownTest() throws Exception {
        super.tearDownTest();
        reInitEnvOptions();
    }

    /**
     * Test : compare two projects to see automation works
     */
    @Test
    public void test() {
        //Action to test
        executeInsideSession(this::testAction);

        //Verify
        verifyResults();
        closeSession();
    }

    protected void verifyProjectLoading() {
        assertNotNull("The project's test 'projectInitial' is not loaded.", initProject);
        if (oracleNeeded)
            assertNotNull("The project's test 'projectOracle' is not loaded.", oracleProject);
        loggerTest.success("- [LOADING] PROJECT LOADED");
    }

    public abstract void testAction();




    public abstract void reInitEnvOptions();


    /**
     * Save a copy of the init model with the test modification applied. Name will be 'initModelName' _save.mdzip
     */
    protected void saveModel() {
        loggerTest.log("- [SAVING RESULT] Saving test case file: - ");
        loggerTest.log("* " + initZipProject + "_save.mdzip");
        File resultTestFile = new File(System.getProperty("tests.resources"), initZipProject + "_save.mdzip");
        saveProject(initProject, resultTestFile);
        loggerTest.log("file://"+resultTestFile.getAbsolutePath());
    }

    /**
     * Verifying final result. E.g. with model comparator, or a wizard state.
     */
    public abstract void verifyResults();



    //------------------------ Helper Testing Functions -----------------------------------------//

    /**
     * Search by Name in the model for elements, trigger an assert error if not found
     * @param elementName name of the searched element
     * @param clazz metaclass of the element
     * @return all element with the given name
     */
    protected Collection<Element> findTestedElementByName(String elementName, java.lang.Class<Class> clazz) {
        com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package testPackage = findTestPackage();
        Collection<Element> foundElement = Finder.byNameAllRecursively().find(testPackage, new java.lang.Class[]{clazz}, elementName);
        assertNotNull("No element was found with the name: " + elementName
                + "\n + inside TestPackage: " + testPackage, foundElement);
        return foundElement;
    }

    /**
     * Search by id in the model for an element, it will then verify that this element is present under the tested package hierarchy.
     * @param id id of the requested element
     * @return found element. Trigger an assert error if not found
     */
    protected Element findTestedElementByID(String id) {
        NamedElement foundElement = (NamedElement) initProject.getElementByID(id);
        assertNotNull(" no element found with ID " + id, foundElement);

        Collection<Element> foundElementsWithSameName = findTestedElementByName(foundElement.getName(), foundElement.getClassType());
        assertTrue("No element named : " + foundElement.getName() + " found with ID: " + id + "\n in the package: " + testPackageName,
                foundElementsWithSameName.stream()
                        .anyMatch(e-> e.getLocalID().equals(id) || e.getID().equals(id)));
        return foundElement;
    }

    /**
     * Search by id in the whole model for an element, including subModels and project Usages
     * @param id id of the requested element
     * @return found element. Trigger an assert error if not found
     */
    protected Element findElementByID(String id) {
        NamedElement foundElement = (NamedElement) initProject.getElementByID(id);
        assertNotNull(" no element found with ID " + id, foundElement);
        return foundElement;
    }

    /**
     * Search by name in the model for the testedPackage.
     * @return testedPackage. Trigger an assert error if not found
     *
     */
    protected com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package findTestPackage() {
        com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package testPackage = Finder.byNameRecursively().find( initProject, new java.lang.Class[]{com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package.class}, testPackageName);
        assertNotNull("Not Found Test package: " + testPackageName, testPackage);
        return testPackage;
    }

    /**
     * Will search by ID and then open the given diagram.
     * @param idDiagram
     * @return opened diagram. Trigger an assert error if not found
     */
    protected Diagram openDiagram(String idDiagram) {
        Diagram diagram = (Diagram) findTestedElementByID(idDiagram);
        Objects.requireNonNull(initProject.getDiagram(diagram), "Can't get diagram as test init project is null").open();
        return diagram;
    }

    /**
     * Create a session inside MagicDraw to execute the given runnable. It closes the previous one if opened (for test independence, and could prevent next cases to be properly executed)
     * @param runnable
     */
    protected void executeInsideSession(Runnable runnable) {
        closeSession();
        //Action to test
        SessionManager.getInstance().executeInsideSession(initProject,"Executing test case - " + getClass().getSimpleName(),  runnable);

        closeSession();
    }

    /**
     * Close the current session if opened
     */
    protected void closeSession() {
        if(SessionManager.getInstance().isSessionCreated(initProject))
            SessionManager.getInstance().closeSession(initProject);
    }


    /**
     * Search in all the Project options the given property in the given category. If absent it will make the test fail. Case and White space non-sensitive.
     * @param category name of the category, list available in ProjectOptions staticField (PROJECT_GENERAL_PROPERTIES, ...)
     * @param optionName name of the option
     * @return Property option
     */
    protected Property getProjectOptionByCategoryName(String category, String optionName){
        Property option = getProjectOptionsByCategory(category, optionName, OMFUtils.currentProject);
        if(option == null)
            Assert.fail("The project options: " + optionName + " wasn't found in the category: " + category);
        return option;
    }

    /**
     * Search in all the Project options the given property in the given category. If absent it will make the test fail. Case and White space non-sensitive.
     * @param optionName name of the option
     * @return Property option
     */
    protected Property getProjectOptionByOptionName(String optionName) {
        ProjectOptions options = OMFUtils.currentProject.getOptions();
        Optional<Property> optOption;
        optOption = Arrays.stream(ProjectOptions.class.getFields())
                .map(field -> {
                    try {
                        return field.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(cat -> !Strings.isNullOrEmpty(cat))
                .map(cat -> getProjectOptionsByCategory(cat, optionName, OMFUtils.currentProject))
                .filter(Objects::nonNull)
                .findFirst();


        if (optOption.isEmpty())
            Assert.fail("The project options: " + optionName + " wasn't found");
        return optOption.get();
    }

    /**
     * Search in Project options the given property in the given category. If absent it returns Null.
     * @param category name of the category (General, OMF, OMF ORGANIZER)
     * @param optionName name of the option
     * @param project project containing the options
     * @return Property option
     */
    private Property getProjectOptionsByCategory(String category, String optionName, Project project){
        try {
           return project.getOptions().getProperty(category, optionName);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Search in all the Environment options the given property in the given group. If absent it will make the test fail. Case and White space non-sensitive.
     * @param group name of the group (General, OMF, OMF ORGANIZER)
     * @param optionName name of the option
     * @return Property option
     */
    protected Property getEnvironmentOptionByGroupName(String group, String optionName){
        Optional<AbstractPropertyOptionsGroup> optGroup = Application.getInstance().getEnvironmentOptions().getGroups()
                .stream()
                .filter(AbstractPropertyOptionsGroup.class::isInstance)
                .map(AbstractPropertyOptionsGroup.class::cast)
                .filter(cat -> cat.getName().equals(group))
                .findAny();

        if(optGroup.isEmpty())
            Assert.fail("The environment options group: " + group + " wasn't found");

        Optional<Property> optOption = optGroup.get().getOptions().getProperties().stream()
                .filter(property -> TestHelper.compareStringsNoCaseNoSpace(property.getName(), optionName))
                .findAny();

        if(optOption.isEmpty())
            Assert.fail("The environment options: " + optionName + " wasn't found in the group: " + group);
        return optOption.get();
    }

    /**
     * Search in all the Environment options the given property in the given ID. If absent it will make the test fail. Case and White space non-sensitive.
     * @param id name of the category (General, OMF, OMF ORGANIZER)
     * @param optionName name of the option
     * @return Property option
     */
    protected Property getEnvironmentOptionByID(String id, String optionName){
        Optional<PropertyManager> optCategory = Application.getInstance().getEnvironmentOptions().getGroups()
                .stream()
                .filter(AbstractPropertyOptionsGroup.class::isInstance)
                .map(AbstractPropertyOptionsGroup.class::cast)
                .map(AbstractPropertyOptionsGroup::getOptions)
                .filter(cat -> cat.getName().equals(id))
                .findAny();

        if(optCategory.isEmpty())
            Assert.fail("The environment options ID: " + id + " wasn't found");

        Optional<Property> optOption = optCategory.get().getProperties().stream()
                .filter(property -> TestHelper.compareStringsNoCaseNoSpace(property.getName(), optionName))
                .findAny();

        if(optOption.isEmpty())
            Assert.fail("The environment options: " + optionName + " wasn't found in the category ID: " + id);
        return optOption.get();
    }

    /**
     * Compare the option actual value with the given value.
     * @param option
     * @param value
     * @return
     */
    protected boolean compareOptionValue(Property option, Object value){
        return option.getValue().equals(value);
    }

    /**
     * Search for the environment option and compare it with the given value.
     * Test will fail if not found. Case and White space non-sensitive.
     * See: getEnvironmentOption for more detail
     * @param group
     * @param optionName
     * @param value
     */
    protected boolean compareOptionValueByCategoryName(String group, String optionName, Object value){
        return getEnvironmentOptionByGroupName(group, optionName).getValue().equals(value);
    }

    /**
     * Search for the environment option and compare it with the given value.
     * Test will fail if not found. Case and White space non-sensitive.
     * See: getEnvironmentOption for more detail
     * @param group
     * @param optionName
     * @param value
     */
    protected boolean compareOptionValueByGroupID(String group, String optionName, Object value){
        return getEnvironmentOptionByGroupName(group, optionName).getValue().equals(value);
    }

    /**
     * Set a new value to the given property
     * @param option
     * @param value
     */
    protected void setOptionValue(Property option, Object value){
        option.setValue(value);
    }

    /**
     * Search for the envvironment option and will set the given value.
     * Test will fail if not found. Case and White space non-sensitive.
     * See: getEnvironmentOption for more detail
     * @param groupName
     * @param optionName
     * @param value
     */
    protected void setEnvironmentOptionValueByGroupName(String groupName, String optionName, Object value){
        setOptionValue(getEnvironmentOptionByGroupName(groupName, optionName), value);
    }

    /**
     * Search for the environment option searching by Environment ID, then set the given value.
     * Test will fail if not found. Case and White space non-sensitive.
     * See: getEnvironmentOptionByID for more detail
     * @param group
     * @param optionName
     * @param value
     */
    protected void setEnvironmentOptionValueByID(String group, String optionName, Object value){
        setOptionValue(getEnvironmentOptionByGroupName(group, optionName), value);
    }


    //------------------- MD ACTION TESTING -------------------------------------------//

    /**
     * It will simulate a click on the given element, and then look up for the browser action with the given name and given category then trigger it.
     * @param selectedElement selected element
     * @param mdActionsCategoryName action category name
     * @param actionToTestName action name
     */
    public void triggerBrowserAction(Element selectedElement, String mdActionsCategoryName, String actionToTestName) {
        ActionsManager actionManager = ActionsProvider.getContainmentBrowserContextActions(OMFUtils.currentProject.getBrowser().getContainmentTree());
        ContainmentTree tree = OMFUtils.currentProject.getBrowser().getContainmentTree();
        tree.setSelectedNodes(new Node[]{new Node(selectedElement, ElementIcon.getIcon(selectedElement))});

        List<BrowserContextAMConfigurator> browserMenuConfigurators = getBrowserMenus();
        browserMenuConfigurators.forEach(configurator -> configurator.configure(actionManager, tree));
        ActionsCategory mdActionsCategory = getCategory(actionManager, mdActionsCategoryName);

        executeAction(actionToTestName, selectedElement, mdActionsCategory);

    }
    /**
     * It will simulate a click on the given element, and then look up for the browser action with the given name and given category then trigger it.
     * Note: It will use the current opened diagram, opened before using this method
     * @param selectedElement selected element
     * @param mdActionsCategoryName action category name
     * @param actionToTestName action name
     */
    public void triggerDiagramAction(Element selectedElement, String mdActionsCategoryName, String actionToTestName) {
        DiagramPresentationElement currentDiagramPresentation =  OMFUtils.currentProject.getActiveDiagram();
        if(currentDiagramPresentation == null)
            Assert.fail("No Diagram opened, please open a diagram before using this method");
        Diagram currentDiagram = currentDiagramPresentation.getDiagram();

        if(currentDiagram == null)
            Assert.fail("No Diagram opened, please open a diagram before using this method");
        PresentationElement[] selectedPresentationElements = new PresentationElement[]{currentDiagramPresentation.findPresentationElement(selectedElement, AbstractHeaderShapeView.class)};

        if(selectedPresentationElements == null || selectedPresentationElements.length == 0)
            Assert.fail("No PresentationElement found in the diagram for element: " + selectedElement.getHumanName());

        currentDiagramPresentation.setSelected(Arrays.asList(selectedPresentationElements));

        ActionsManager actionManager = ActionsProvider.getInstance().getDiagramContextActions(
                currentDiagram.get_representation().getType(), currentDiagramPresentation,
                selectedPresentationElements,
                selectedPresentationElements[0]);

        List<DiagramContextAMConfigurator> diagramConfigurator = this.getDiagramMenus(currentDiagram);
        diagramConfigurator.forEach((configurator) -> {
            configurator.configure(actionManager, currentDiagramPresentation, selectedPresentationElements, selectedPresentationElements[0]);
        });
        ActionsCategory mdActionsCategory = this.getCategory(actionManager, mdActionsCategoryName);
        this.executeAction(actionToTestName, selectedElement, mdActionsCategory);
    }




    /**
     * from the actionManager will search for all the registered Category. If absent the test will fail.
     * @param actionManager
     * @param mdActionsCategoryName
     * @return
     */
    public ActionsCategory getCategory(ActionsManager actionManager, String mdActionsCategoryName) {
        Optional<ActionsCategory> optCategory = actionManager.getCategories().stream().filter(cat -> cat.getName().equals(mdActionsCategoryName)).findFirst();

        if (optCategory.isEmpty())
            Assertions.fail("[MDActionTesting] " + testCaseID + " menu category : " + mdActionsCategoryName + " was not found");

        return optCategory.get();
    }

    /**
     * Get all  BrowserMenuConfigurators registered. BrowserMenuConfigurator hosts the Categories and the Actions
     * @return List of all the browserMenuConfigurator registered
     */
    public List<BrowserContextAMConfigurator> getBrowserMenus(){
        ActionsConfiguratorsManager actionsConfiguratorsManager = ActionsConfiguratorsManager.getInstance();

        Optional<Method> optMethod = Arrays.stream(actionsConfiguratorsManager.getClass().getDeclaredMethods()).filter(m -> m.getName().equals("collectConfigurators")).findAny();
        if(optMethod.isEmpty())
            Assertions.fail("[Technical error] Cannot retrieved the Browser configurator");


        List accessibleBrowserMenus = null;
        try {
            Method method = optMethod.get();
            method.setAccessible(true);
            Object res = method.invoke(actionsConfiguratorsManager,"ContainmentBrowserMenu");
            if(res != null && res instanceof List)
                accessibleBrowserMenus = (List) res;
        } catch (IllegalAccessException | InvocationTargetException e) {
            Assertions.fail("[Technical error] Cannot retrieved the Browser configurator");
            throw new RuntimeException(e);
        }

        return (List<BrowserContextAMConfigurator>) accessibleBrowserMenus.stream()
                .filter(BrowserContextAMConfigurator.class::isInstance)
                .map(BrowserContextAMConfigurator.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Get all  DiagramContextConfigurators registered. DiagramContextConfigurators hosts the Categories and the Actions
     * @param diagram: current opened diagram
     * @return List of all the DiagramContextConfigurators registered
     */
    private List<DiagramContextAMConfigurator> getDiagramMenus(Diagram diagram) {
        ActionsConfiguratorsManager actionsConfiguratorsManager = ActionsConfiguratorsManager.getInstance();

        Optional<Method> optMethod = Arrays.stream(actionsConfiguratorsManager.getClass().getDeclaredMethods()).filter(m -> m.getName().equals("collectConfigurators")).findAny();
        if(optMethod.isEmpty())
            Assertions.fail("[Technical error] Cannot retrieved the Diagram configurator");


        List accessibleDiagramMenus = null;
        try {
            Method method = optMethod.get();
            method.setAccessible(true);
            Object res = method.invoke(actionsConfiguratorsManager,diagram.get_representation().getType() + "Context");
            if(res != null && res instanceof List)
                accessibleDiagramMenus = (List) res;
        } catch (IllegalAccessException | InvocationTargetException e) {
            Assertions.fail("[Technical error] Cannot retrieved the Diagram configurator");
            throw new RuntimeException(e);
        }

        return (List<DiagramContextAMConfigurator>) accessibleDiagramMenus.stream()
                .filter(DiagramContextAMConfigurator.class::isInstance)
                .map(DiagramContextAMConfigurator.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Execute an NMAction on a related Element
     * @param actionToTestName String
     * @param element Element
     * @param mdActionsCategory MDActionsCategory
     */
    public void executeAction(String actionToTestName, Element element, ActionsCategory mdActionsCategory){
        long now = System.currentTimeMillis();
        ActionEvent actionEvent = new ActionEvent(ActionEvent.ACTION_PERFORMED,
                1001,
                actionToTestName,
                now,
                16);
        actionEvent.setSource(element);

        mdActionsCategory.getActions().stream().filter(nmAction -> nmAction.getName().equalsIgnoreCase(actionToTestName))
                .findFirst()
                .ifPresentOrElse(action -> action.actionPerformed(actionEvent), () -> fail("Action: " + actionToTestName + " was not found in category: " + mdActionsCategory));

    }

    //------------------------ GETTERS SETTERS -----------------------------------------//

    public String getTestCaseID() {
        return testCaseID;
    }

    public void setTestCaseID(String testCaseID) {
        this.testCaseID = testCaseID;
    }

    public TestLogger getLoggerTest() {
        return loggerTest;
    }

    public void setLoggerTest(TestLogger loggerTest) {
        this.loggerTest = loggerTest;
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

    public String getInitZipProject() {
        return initZipProject;
    }

    public void setInitZipProject(String initZipProject) {
        this.initZipProject = initZipProject;
    }

    public String getOracleZipProject() {
        return oracleZipProject;
    }

    public void setOracleZipProject(String oracleZipProject) {
        this.oracleZipProject = oracleZipProject;
    }

    public ATestBatch getTestBatch() {
        return testBatch;
    }

    public void setTestBatch(ATestBatch testBatch) {
        this.testBatch = testBatch;
    }

    public boolean isOracleNeeded() {
        return oracleNeeded;
    }

    public void setOracleNeeded(boolean oracleNeeded) {
        this.oracleNeeded = oracleNeeded;
    }

    public String getTestPackageName() {
        return testPackageName;
    }

    public void setTestPackageName(String testPackageName) {
        this.testPackageName = testPackageName;
    }

    public ProjectsComparator createNewProjectComparator(String logFile) {
        return createProjectComparator(logFile);
    }
}

