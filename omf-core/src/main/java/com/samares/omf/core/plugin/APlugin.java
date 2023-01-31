package com.samares.omf.core.plugin;

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.nomagic.magicdraw.core.options.ProjectOptions;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.samares.omf.core.actions.v2.configurators.OMFBrowserConfigurator;
import com.samares.omf.core.feature.FeatureRegister;
import com.samares.omf.core.feature.MDFeature;
import com.samares.omf.core.feature.options.AOptionListener;
import com.samares.omf.core.feature.options.OptionKind;
import com.samares.omf.core.listeners.IListenerManager;
import com.samares.omf.core.listeners.listeners.OMFProjectListener;
import com.samares.omf.core.ui.OMFDiagramConfigurator;
import com.samares.omf.core.ui.OMFMainMenuConfigurator;
import com.samares.omf.core.ui.environmentoptions.OMFEnvironmentOptionsGroup;
import com.samares.omf.core.ui.projectoptions.FeatureProjectOptionsConfigurator;
import com.samares.omf.core.utils.ColorPrinter;
import com.samares.omf.core.utils.OMFConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TEMPLATE of plugin allowing easily feature registering and development.
 * It includes auto registering of:
 * - Configurators (Browser, Diagram, Menu)
 * - Options (Environment, and Project (NOT IMPLEMENTED YET)
 * - Features registering (Listeners, RuleEngines, Options, MDActions)
 *
 * For quick plugin registering use OMFxxx as default classes (OMFBrowserConfigurator, OMFEnvironmentOptions, ...)
 */
public abstract class APlugin extends Plugin {
    @SuppressWarnings({"FieldCanBeLocal"})
    private List<AOptionListener> environmentOptionsListener;

    private FeatureProjectOptionsConfigurator projectOptionConfigurator;
    private OMFEnvironmentOptionsGroup environmentOptionConfigurator;

    private List<MDFeature> l_features;
    private FeatureRegister featureRegister;
    private boolean isInitialized = false;

    private static APlugin instance;

    public static APlugin getInstance(){
        return (APlugin) Application.getInstance().getPluginManager().B().stream()
                .filter(APlugin.class::isInstance)
                .findFirst()
                .orElseThrow();
//        if(instance == null)
//            instance = new APlugin();
//        return instance;
    }

    private IListenerManager listenerManager;
    private OMFProjectListener projectListener;

    public APlugin(){
        l_features = new ArrayList<>();
    }

    //------------------------ ELEMENTS TO REGISTER AT INIT -------------------------------------------//

    /**
     * Define all the features registered by default at Plugin initialization.
     * NOTE: Features can be registered later, by code or the project is opened (use instead getOnProjectOpeningFeatureToRegister())
     * @return List of feature to register at plugin initialization
     */
    public abstract List<MDFeature> getDefaultFeatureRegistered();

    /**
     * Define all the features to register at project opening. Features will be unregistered when the project is closed, or on project switching
     * NOTE: Features can be registered later, by code or the project is opened (use instead getOnProjectOpeningFeatureToRegister())
     * @return List of feature to register at plugin initialization
     */
    public abstract List<MDFeature> getOnProjectOpeningFeatureToRegister();

    /**
     * Define the BrowserConfigurator to register at plugin initialization.
     * This Configurator will be used for FeatureRegistering
     * @return BrowserConfigurator to register
     */
    public abstract OMFBrowserConfigurator getFeatureRegisteringBrowserConfigurator();
    /**
     * Define the DiagramConfigurator to register at plugin initialization.
     * This Configurator will be used for FeatureRegistering
     * @return DiagramConfigurator to register
     */
    public abstract OMFDiagramConfigurator getFeatureRegisteringDiagramConfigurator();
    /**
     * Define the MainMenuConfigurator to register at plugin initialization.
     * This Configurator will be used for FeatureRegistering
     * @return MainMenuConfigurator to register
     */
    public abstract OMFMainMenuConfigurator getFeatureRegisteringMainMenuConfigurator();

    /**
     * Define the EnvironmentOptionsGroup to register at plugin Initialization
     * This Configurator will be used for FeatureRegistering
     * @return EnvironmentOptionsGroup to register
     */
    public abstract OMFEnvironmentOptionsGroup getFeatureRegisteringEnvironmentOptionGroup();
    /**
     * Define the ProjectOptionsGroup to register at plugin Initialization
     * This Configurator will be used for FeatureRegistering
     * @return ProjectOptionsGroup to register
     */
    public abstract FeatureProjectOptionsConfigurator getFeatureRegisteringProjectOptionGroup();
    /**
     * Define the ProjectListener to register at plugin Initialization
     * This Listener will be used for FeatureRegistering at projectOpening and registration of ProjectOptions
     * @return ProjectOptionsGroup to register
     */
    public abstract OMFProjectListener getProjectListener();

    /**
     * Define the ListenerManager to register at plugin Initialization
     * This ListenerManager will be used for FeatureRegistering with liveActions and all registration of listeners
     * @return ProjectOptionsGroup to register
     */
    public abstract IListenerManager getListenerManager();




    //------------------------ INITIALIZATION PROCESS-------------------------------------------//
    /**
     * Initialize the plugin, and will configure ActionConfigurators, Options, Constants, and will register features.
     */
    @Override
    public void init() {

//        ProjectOptions.addConfigurator();
//        ProjectOptions.addConfigurator(TestProjectOptionsConfigurator.getInstance())
        configureListenerManager();
        configureActions();
        configureProjectListener();
        configureEnvironmentOptions();
        configureProjectOptions();
        configureConstants();
        configureFeatures();
        registerFeatures();

        isInitialized = true;
    }

    private void configureListenerManager() {
        this.listenerManager = getListenerManager();
    }

    private void configureFeatures() {
        List<MDFeature> l_defaultFeature = getDefaultFeatureRegistered();
        if(l_defaultFeature == null || l_defaultFeature.isEmpty()){

        }
        getL_features().addAll(l_defaultFeature);
    }


    /**
     * CONFIGURE DEVELOPMENT/TESTING OPTIONS:
     * - GUI_REQUIRED: to inform custom wizard to not be displayed
     */
    protected void configureConstants() {
        OMFConstants.GUI_REQUIRED = !Application.runtimeInternal().isTester() || Application.runtimeInternal().isDeveloper();
    }

    protected void configureProjectListener() {
        projectListener = getProjectListener();
        if(projectListener != null)
            Application.getInstance().getProjectsManager().addProjectListener(projectListener);
        else
            ColorPrinter.warn("[OMF] NO PROJECT LISTENER REGISTERED");

    }

    protected void configureActions() {
        ActionsConfiguratorsManager actionManager = ActionsConfiguratorsManager.getInstance();

        OMFBrowserConfigurator browserConfigurator = getFeatureRegisteringBrowserConfigurator();
        if (browserConfigurator == null)
            ColorPrinter.warn("[OMF] NO BROWSER CONFIGURATOR REGISTERED");
        else
            actionManager.addContainmentBrowserContextConfigurator(browserConfigurator);

        OMFDiagramConfigurator diagramConfigurator = getFeatureRegisteringDiagramConfigurator();
        if (diagramConfigurator == null)
            ColorPrinter.warn("[OMF] NO DIAGRAM CONFIGURATOR REGISTERED");
        else
        actionManager.addDiagramContextConfigurator(DiagramTypeConstants.UML_ANY_DIAGRAM,diagramConfigurator);

        OMFMainMenuConfigurator menuConfigurator = getFeatureRegisteringMainMenuConfigurator();
        if (menuConfigurator == null)
            ColorPrinter.warn("[OMF] NO MAIN MENU CONFIGURATOR REGISTERED");
        else
            actionManager.addMainMenuConfigurator(menuConfigurator);

        featureRegister = new FeatureRegister(browserConfigurator, diagramConfigurator, menuConfigurator, listenerManager);
    }


    protected void registerFeatures() {
        l_features.forEach(featureRegister::registerFeature);

        if (projectListener == null)
            ColorPrinter.warn("[OMF] NO PROJECT LISTENER REGISTERED");
        else {
            projectListener.addFeatureToRegisterAtProjectOpening(getOnProjectOpeningFeatureToRegister());
            projectListener.addProjectOptionToRegister(getAllProjectOptionsFeatures(l_features));
        }
    }

    private List<MDFeature> getAllProjectOptionsFeatures(List<MDFeature> l_features) {
        return l_features.stream()
                .filter(feature -> feature.getOptions().stream().anyMatch(option-> option.getKind() == OptionKind.Project))
                .collect(Collectors.toList());
    }

    protected void configureEnvironmentOptions() {
        Application application = Application.getInstance();
        EnvironmentOptions options = application.getEnvironmentOptions();


        environmentOptionConfigurator = getFeatureRegisteringEnvironmentOptionGroup();
        if(environmentOptionConfigurator == null){
            ColorPrinter.warn("[OMF] NO ENVIRONMENT OPTIONS REGISTERED");
            return;
        }


        options.addGroup(environmentOptionConfigurator);

        environmentOptionsListener = getEnvironmentOptionsListener();
        if(!environmentOptionsListener.isEmpty())
            environmentOptionsListener.forEach(options::addEnvironmentChangeListener);

    }

    private void configureProjectOptions() {
        projectOptionConfigurator = getFeatureRegisteringProjectOptionGroup();

        if(projectOptionConfigurator == null){
            ColorPrinter.warn("[OMF] NO PROJECT OPTIONS REGISTERED");
            return;
        }

        ProjectOptions.addConfigurator(projectOptionConfigurator);
    }

    //------------------------------------ GETTER SETTER ----------------------------------------------------//

    @Override
    public boolean close() {
        return true;
    }
    @Override
    public boolean isSupported() {
        return true;
    }

    public abstract List<AOptionListener> getEnvironmentOptionsListener();
    public List<MDFeature> getL_features() {
        return l_features;
    }

    public FeatureRegister getFeatureRegister() {
        return featureRegister;
    }

    public boolean isInitialized() {
        return this.isInitialized;
    }

}
