/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.plugin;

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.nomagic.magicdraw.core.options.ProjectOptions;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFFeatureNotFoundException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFPluginRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.IFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.configurators.OMFBrowserConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.configurators.OMFDiagramConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.configurators.OMFMainMenuConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.MDActionRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.OptionRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.RuleEngineRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly.IProjectOnlyFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly.ProjectOnlyMDActionRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly.IProjectOnlyOptionRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly.ProjectOnlyRuleEngineRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.ProjectListener;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_core_framework.ui.projectoptions.FeatureProjectOptionsConfigurator;
import com.samares_engineering.omf.omf_core_framework.utils.ColorPrinter;
import com.samares_engineering.omf.omf_core_framework.utils.OMFConstants;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private OMFPropertyOptionsGroup environmentOptionsGroup;

    private final Map<String, MDFeature> features = new HashMap<>();
    private FeatureRegisterer featureRegisterer;
    private MDActionRegisterer uiActionRegisterer;
    private RuleEngineRegisterer ruleEngineRegisterer;
    private OptionRegisterer optionRegisterer;
    private ProjectOnlyMDActionRegisterer projectOnlyUiActionRegisterer;
    private ProjectOnlyRuleEngineRegisterer projectOnlyRuleEngineRegisterer;
    private IProjectOnlyOptionRegisterer projectOnlyOptionRegisterer;
    private boolean isInitialized = false;
    private IListenerManager listenerManager;
    private ProjectListener projectListener;
    private OMFBrowserConfigurator browserConfigurator;
    private OMFDiagramConfigurator diagramConfigurator;
    private OMFMainMenuConfigurator menuConfigurator;

    public APlugin() {
    }

    //------------------------ ELEMENTS TO REGISTER AT INIT -------------------------------------------//

    /**
     * Define all the features registered by default at Plugin initialization.
     * NOTE: Features can be registered later, by code or the project is opened (use instead getOnProjectOpeningFeatureToRegister())
     *
     * @return List of feature to register at plugin initialization
     */
    protected abstract List<MDFeature> initFeatures();

    /**
     * Define the BrowserConfigurator to register at plugin initialization.
     * This Configurator will be used for FeatureRegistering
     *
     * @return BrowserConfigurator to register
     */
    protected abstract OMFBrowserConfigurator initFeatureRegisteringBrowserConfigurator();

    /**
     * Define the DiagramConfigurator to register at plugin initialization.
     * This Configurator will be used for FeatureRegistering
     *
     * @return DiagramConfigurator to register
     */
    protected abstract OMFDiagramConfigurator initFeatureRegisteringDiagramConfigurator();

    /**
     * Define the MainMenuConfigurator to register at plugin initialization.
     * This Configurator will be used for FeatureRegistering
     *
     * @return MainMenuConfigurator to register
     */
    public abstract OMFMainMenuConfigurator initFeatureRegisteringMainMenuConfigurator();

    /**
     * Define the EnvironmentOptionsGroup to register at plugin Initialization
     * This Configurator will be used for FeatureRegistering
     *
     * @return EnvironmentOptionsGroup to register
     */
    protected abstract OMFPropertyOptionsGroup initFeatureRegisteringEnvironmentOptionGroup();

    /**
     * Define the ProjectOptionsGroup to register at plugin Initialization
     * This Configurator will be used for FeatureRegistering
     *
     * @return ProjectOptionsGroup to register
     */
    protected abstract FeatureProjectOptionsConfigurator initFeatureRegisteringProjectOptionGroup();

    /**
     * Define the ProjectListener to register at plugin Initialization
     * This Listener will be used for FeatureRegistering at projectOpening and registration of ProjectOptions
     *
     * @return ProjectOptionsGroup to register
     */
    protected abstract ProjectListener initProjectListener();

    /**
     * Define the ListenerManager to register at plugin Initialization
     * This ListenerManager will be used for FeatureRegistering with liveActions and all registration of listeners
     *
     * @return ProjectOptionsGroup to register
     */
    protected abstract IListenerManager initListenerManager();


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
        configureFeatureRegisterer();
        configureFeatures();
        registerFeatures();

        isInitialized = true;
    }

    private void configureFeatureRegisterer() {
        try {
            this.featureRegisterer = new FeatureRegisterer(this);
            this.uiActionRegisterer = new MDActionRegisterer();
            this.ruleEngineRegisterer = new RuleEngineRegisterer();
            this.optionRegisterer = new OptionRegisterer();
            this.projectOnlyUiActionRegisterer = new ProjectOnlyMDActionRegisterer();
            this.projectOnlyRuleEngineRegisterer = new ProjectOnlyRuleEngineRegisterer();
            this.projectOnlyOptionRegisterer = new IProjectOnlyOptionRegisterer();

            List<IFeatureItemRegisterer> defaultFeatureRegisterer = List.of(uiActionRegisterer,
                    ruleEngineRegisterer,
                    optionRegisterer);

            List<IProjectOnlyFeatureItemRegisterer> defaultProjectOnlyFeatureRegisterer = List.of(projectOnlyUiActionRegisterer,
                    projectOnlyRuleEngineRegisterer,
                    projectOnlyOptionRegisterer);
            featureRegisterer.addAllIFeatureItemRegisterer(defaultFeatureRegisterer);
            featureRegisterer.addAllProjectOnlyFeatureItemRegisterer(defaultProjectOnlyFeatureRegisterer);
        }catch (Exception e){
            OMFErrorHandler.handleException(new OMFPluginRegisteringException("Error occurred during FeatureRegistererConfiguration", e, this, GenericException.ECriticality.CRITICAL));
        }
    }

    private void configureListenerManager() {
        try {
            this.listenerManager = initListenerManager();
        } catch (Exception e) {
            OMFErrorHandler.handleException(new OMFPluginRegisteringException("Error occurred during ListenerManagerConfiguration",
                    e, this, GenericException.ECriticality.CRITICAL));
        }
    }

    private void configureFeatures() {
        try {
            List<MDFeature> featureInstances = this.initFeatures();
            if (featureInstances == null) {
                ColorPrinter.warn("No feature to registered in the plugin"); //TODO: Introduce real logging management
                return;
            }

            featureInstances.forEach(f -> {
                if (features.containsKey(f.getName())) {
                    OMFErrorHandler.handleException(new OMFPluginRegisteringException("Can't init feature " + f.getName()
                            + " as a feature with the same name has already" +
                            "been instantiated in the plugin", this, GenericException.ECriticality.CRITICAL));
                } else {
                    features.put(f.getName(), f);
                }
            });
        } catch (Exception e) {
            OMFErrorHandler.handleException(new OMFPluginRegisteringException("Error occurred during FeatureConfiguration",
                    e, this, GenericException.ECriticality.CRITICAL));
        }
    }

    /**
     * CONFIGURE DEVELOPMENT/TESTING OPTIONS:
     * - GUI_REQUIRED: to inform custom wizard to not be displayed
     */
    protected void configureConstants() {
        try {
            OMFConstants.GUI_REQUIRED = !Application.runtimeInternal().isTester() || Application.runtimeInternal().isDeveloper();
        } catch (Exception e) {
            OMFErrorHandler.handleException(new OMFPluginRegisteringException("Error occurred during constant configuration " +
                    "(JVM args, DEV/TESTER, GUI REQUIRED, etc)", e, this, GenericException.ECriticality.CRITICAL));
        }
    }

    protected void configureProjectListener() {
        try {
            projectListener = initProjectListener();
            if (projectListener != null)
                Application.getInstance().getProjectsManager().addProjectListener(projectListener);
            else
                ColorPrinter.warn("[OMF] NO PROJECT LISTENER REGISTERED");
        } catch (Exception e) {
            OMFErrorHandler.handleException(new OMFPluginRegisteringException("Error occurred during ProjectListener configuration",
                    e, this, GenericException.ECriticality.CRITICAL));
        }
    }

    protected void configureActions() {
        ActionsConfiguratorsManager actionManager = ActionsConfiguratorsManager.getInstance();

        try {
            browserConfigurator = initFeatureRegisteringBrowserConfigurator();
            if (browserConfigurator == null)
                ColorPrinter.warn("[OMF] NO BROWSER CONFIGURATOR REGISTERED");
            else {
                actionManager.addContainmentBrowserContextConfigurator(browserConfigurator);
            }
        } catch (Exception e) {
            OMFErrorHandler.handleException(new OMFPluginRegisteringException("Error occurred during BrowserAction Registering",
                    e, this, GenericException.ECriticality.CRITICAL));
        }


        try {
            diagramConfigurator = initFeatureRegisteringDiagramConfigurator();
            if (diagramConfigurator == null)
                ColorPrinter.warn("[OMF] NO DIAGRAM CONFIGURATOR REGISTERED");
            else {
                actionManager.addDiagramContextConfigurator(DiagramTypeConstants.UML_ANY_DIAGRAM, diagramConfigurator);
            }
        } catch (Exception e) {
            OMFErrorHandler.handleException(new OMFPluginRegisteringException("Error occurred during DiagramAction Registering",
                    e, this, GenericException.ECriticality.CRITICAL));
        }

        try {
            menuConfigurator = initFeatureRegisteringMainMenuConfigurator();
            if (menuConfigurator == null)
                ColorPrinter.warn("[OMF] NO MAIN MENU CONFIGURATOR REGISTERED");
            else
                actionManager.addMainMenuConfigurator(menuConfigurator);
        } catch (Exception e) {
            OMFErrorHandler.handleException(new OMFPluginRegisteringException("Error occurred during MainMenuAction Registering",
                    e, this, GenericException.ECriticality.CRITICAL));
        }
    }


    protected void registerAllFeatures() {
        featureRegisterer.registerFeatures(getFeatures());
    }

    protected void unregisterAllFeatures() {
        featureRegisterer.unregisterFeatures(getFeatures());
    }

    protected void configureEnvironmentOptions() {

        Application application = Application.getInstance();
        EnvironmentOptions options = application.getEnvironmentOptions();

        try {
            environmentOptionsGroup = initFeatureRegisteringEnvironmentOptionGroup();
            if (environmentOptionsGroup == null) {
                ColorPrinter.warn("[OMFPluginRegistering] NO ENVIRONMENT OPTIONS REGISTERED");
                return;
            }


            options.addGroup(environmentOptionsGroup);

            environmentOptionsListener = initEnvironmentOptionsListener();
            if (CollectionUtils.isNotEmpty(environmentOptionsListener))
                environmentOptionsListener.forEach(options::addEnvironmentChangeListener);
        } catch (Exception e) {
            OMFErrorHandler.handleException(new OMFPluginRegisteringException("Error occurred during EnvironmentOptions Configuration",
                    e, this, GenericException.ECriticality.CRITICAL));
        }

    }

    private void configureProjectOptions() {
        try {
            projectOptionConfigurator = initFeatureRegisteringProjectOptionGroup();

            if (projectOptionConfigurator == null) {
                ColorPrinter.warn("[OMF] NO PROJECT OPTIONS REGISTERED");
                return;
            }

            ProjectOptions.addConfigurator(projectOptionConfigurator);
        } catch (Exception e) {
            OMFErrorHandler.handleException(new OMFPluginRegisteringException("Error occurred during ProjectOptions Configuration",
                    e, this, GenericException.ECriticality.CRITICAL));
        }
    }


    //------------------------------------ GETTER SETTER ----------------------------------------------------//
    public RuleEngineRegisterer getRuleEngineRegisterer() {
        return ruleEngineRegisterer;
    }
    public void setRuleEngineRegisterer(RuleEngineRegisterer ruleEngineRegisterer) {
        this.ruleEngineRegisterer = ruleEngineRegisterer;
    }
    public MDActionRegisterer getUiActionRegisterer() {
        return uiActionRegisterer;
    }
    public void setUiActionRegisterer(MDActionRegisterer uiActionRegisterer) {
        this.uiActionRegisterer = uiActionRegisterer;
    }
    public OptionRegisterer getOptionRegisterer() {
        return optionRegisterer;
    }
    public void setOptionRegisterer(OptionRegisterer optionRegisterer) {
        this.optionRegisterer = optionRegisterer;
    }
    @Override
    public boolean close() {
        return true;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    public abstract List<AOptionListener> initEnvironmentOptionsListener();

    public List<MDFeature> getFeatures() {
        return new ArrayList<>(features.values());
    }

    public MDFeature getFeatureByName(String name) throws OMFException {
        if (features.containsKey(name)) {
            return features.get(name);
        }
        throw new OMFFeatureNotFoundException("Can't find feature instance with name " + name + " in plugin " +
                featureRegisterer.getPlugin(), GenericException.ECriticality.CRITICAL);
    }

    public FeatureRegisterer getFeatureRegister() {
        return featureRegisterer;
    }

    public boolean isInitialized() {
        return this.isInitialized;
    }

    public List<AOptionListener> getEnvironmentOptionsListener() {
        return environmentOptionsListener;
    }

    public FeatureProjectOptionsConfigurator getProjectOptionConfigurator() {
        return projectOptionConfigurator;
    }

    public OMFPropertyOptionsGroup getEnvironmentOptionsGroup() {
        return environmentOptionsGroup;
    }

    public FeatureRegisterer getFeatureRegisterer() {
        return featureRegisterer;
    }

    public IListenerManager getListenerManager() {
        return listenerManager;
    }

    public ProjectListener getProjectListener() {
        return projectListener;
    }

    public OMFBrowserConfigurator getBrowserConfigurator() {
        return browserConfigurator;
    }

    public OMFDiagramConfigurator getDiagramConfigurator() {
        return diagramConfigurator;
    }

    public OMFMainMenuConfigurator getMenuConfigurator() {
        return menuConfigurator;
    }

    public String getName() {
        return this.getDescriptor().getName();
    }
}
