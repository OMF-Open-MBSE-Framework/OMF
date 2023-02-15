/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.plugin;

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.nomagic.magicdraw.core.options.ProjectOptions;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.samares.omf.core.feature.registrables.actions.actions.configurators.OMFBrowserConfigurator;
import com.samares.omf.core.feature.FeatureRegisterer;
import com.samares.omf.core.feature.MDFeature;
import com.samares.omf.core.feature.registrables.options.option.AOptionListener;
import com.samares.omf.core.feature.registrables.options.option.OptionKind;
import com.samares.omf.core.listeners.IListenerManager;
import com.samares.omf.core.listeners.listeners.ProjectListener;
import com.samares.omf.core.feature.registrables.actions.actions.configurators.OMFDiagramConfigurator;
import com.samares.omf.core.feature.registrables.actions.actions.configurators.OMFMainMenuConfigurator;
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

    private List<MDFeature> features;
    private FeatureRegisterer featureRegisterer;
    private boolean isInitialized = false;
    private IListenerManager listenerManager;
    private ProjectListener projectListener;

    public APlugin(){
        features = new ArrayList<>();
    }

    //------------------------ ELEMENTS TO REGISTER AT INIT -------------------------------------------//

    /**
     * Define all the features registered by default at Plugin initialization.
     * NOTE: Features can be registered later, by code or the project is opened (use instead getOnProjectOpeningFeatureToRegister())
     * @return List of feature to register at plugin initialization
     */
    public abstract List<MDFeature> getFeaturesRegisteredOnPluginInit();

    /**
     * Define all the features to register at project opening. Features will be unregistered when the project is closed, or on project switching
     * NOTE: Features can be registered later, by code or the project is opened (use instead getOnProjectOpeningFeatureToRegister())
     * @return List of feature to register at plugin initialization
     */
    public abstract List<MDFeature> getFeaturesRegisteredOnProjectOpening();

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
    public abstract ProjectListener getProjectListener();

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
        List<MDFeature> defaultFeatures = getFeaturesRegisteredOnPluginInit();
        if(defaultFeatures == null || defaultFeatures.isEmpty()){

        }
        getFeatures().addAll(defaultFeatures);
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
        else {
            actionManager.addDiagramContextConfigurator(DiagramTypeConstants.UML_ANY_DIAGRAM,diagramConfigurator);
        }

        OMFMainMenuConfigurator menuConfigurator = getFeatureRegisteringMainMenuConfigurator();
        if (menuConfigurator == null)
            ColorPrinter.warn("[OMF] NO MAIN MENU CONFIGURATOR REGISTERED");
        else
            actionManager.addMainMenuConfigurator(menuConfigurator);

        featureRegisterer = new FeatureRegisterer(this);
    }


    protected void registerFeatures() {
        features.forEach(featureRegisterer::registerFeature);

        if (projectListener == null)
            ColorPrinter.warn("[OMF] NO PROJECT LISTENER REGISTERED");
        else {
            projectListener.addFeatureToRegisterAtProjectOpening(getFeaturesRegisteredOnProjectOpening());
            projectListener.addProjectOptionToRegister(getAllProjectOptionsFeatures(features));
        }
    }

    private List<MDFeature> getAllProjectOptionsFeatures(List<MDFeature> featuress) {
        return featuress.stream()
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
    public List<MDFeature> getFeatures() {
        return features;
    }

    public FeatureRegisterer getFeatureRegister() {
        return featureRegisterer;
    }

    public boolean isInitialized() {
        return this.isInitialized;
    }

}
