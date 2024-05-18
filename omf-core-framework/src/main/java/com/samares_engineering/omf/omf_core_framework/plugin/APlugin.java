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
import com.samares_engineering.omf.omf_core_framework.errormanagement2.ErrorHandler2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel2;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.plugin.OMFPluginRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFBrowserConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFDiagramConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFMainMenuConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.magicdraw.MagicDrawHookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.FeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.ProjectOnlyFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.OptionFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.RuleEngineFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.UIActionFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.hooks.FeatureLifeCycleHookFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.hooks.MagicDrawLifeCycleHookFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.hooks.ProjectLifeCycleHookFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly.ProjectOnlyOptionFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly.ProjectOnlyRuleEngineFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly.ProjectOnlyUIActionFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.ProjectListener;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_core_framework.ui.projectoptions.FeatureProjectOptionsConfigurator;
import com.samares_engineering.omf.omf_core_framework.utils.ColorPrinter;
import com.samares_engineering.omf.omf_core_framework.utils.OMFConstants;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * TEMPLATE of plugin allowing easily feature registering and development.
 * It includes auto registering of:
 * - Configurators (Browser, Diagram, Menu)
 * - Options (Environment, and Project (NOT IMPLEMENTED YET)
 * - Features registering (Listeners, RuleEngines, Options, MDActions)
 * <p>
 * For quick plugin registering use OMFxxx as default classes (OMFBrowserConfigurator, OMFEnvironmentOptions, ...)
 */
public abstract class APlugin extends Plugin {
    private boolean isInitialized = false;

    // Initialized by user implementing the plugin (basically API of the framework for the plugin)
    private final Map<String, MDFeature> features = new LinkedHashMap<>();
    private List<AOptionListener> environmentOptionsListener;
    private FeatureProjectOptionsConfigurator projectOptionConfigurator;
    private OMFPropertyOptionsGroup environmentOptionsGroup;
    private IListenerManager listenerManager;
    private ProjectListener projectListener;
    private OMFBrowserConfigurator browserConfigurator;
    private OMFDiagramConfigurator diagramConfigurator;
    private OMFMainMenuConfigurator menuConfigurator;
    private FeatureRegisterer featureRegisterer;

    // Feature item registerers
    private UIActionFeatureItemRegisterer uiActionFeatureItemRegisterer;
    private RuleEngineFeatureItemRegisterer ruleEngineFeatureItemRegisterer;
    private OptionFeatureItemRegisterer optionFeatureItemRegisterer;
    private ProjectOnlyUIActionFeatureItemRegisterer projectOnlyUiActionRegisterer;
    private ProjectOnlyRuleEngineFeatureItemRegisterer projectOnlyRuleEngineFeatureItemRegisterer;
    private ProjectOnlyOptionFeatureItemRegisterer projectOnlyOptionFeatureItemRegisterer;
    private ProjectLifeCycleHookFeatureItemRegisterer projectLifeCycleHookFeatureItemRegisterer;
    private MagicDrawLifeCycleHookFeatureItemRegisterer magicDrawLifeCycleHookFeatureItemRegisterer;
    private FeatureLifeCycleHookFeatureItemRegisterer featureLifeCycleHookFeatureItemRegisterer;

    // MagicDraw Hook Executor
    private MagicDrawHookExecutor magicDrawHookExecutor;

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

    /**
     * Define the MagicDrawHookExecutor to register at plugin Initialization
     * This HookExecutor will be used for FeatureRegistering with MagicDraw hooks
     *
     * @return MagicDrawHookExecutor to register
     */
    protected MagicDrawHookExecutor initMagicDrawHookExecutor() {
        return new MagicDrawHookExecutor();
    }

    /**
     * Define the FeatureRegisterer to register at plugin Initialization
     * This FeatureRegisterer will be used for FeatureRegistering with all features
     *
     * @return FeatureRegisterer to register
     */
    protected FeatureRegisterer initFeatureRegisterer() {return new FeatureRegisterer(this);}


    //------------------------ INITIALIZATION PROCESS-------------------------------------------//

    /**
     * Please do not override this method, use initPlugin() instead.
     * It will call initPlugin() and handle exceptions
     */
    @Override
    public final void init() {
        try {
            OMFLogger2.init(this);
            ErrorHandler2.init(this);
        } catch (Exception e) {
            throw new OMFPluginRegisteringException("Error occurred during error management initialization", e);
        }

        try {
            initPlugin();
        } catch (Exception e) {
            ErrorHandler2.getInstance().handleException(new OMFPluginRegisteringException("Error occurred during Plugin Initialization", e));
        }
    }

    /**
     * Initialize the plugin, and will configure:
     * - MagicDrawHookExecutor (for MagicDraw lifecycle hooks) <br>
     * - ListenerManager (for all the core listeners) <br>
     * - Actions Configurators (Browser, Diagram, Menu) <br>
     * - ProjectListener (for project lifecycle automations) <br>
     * - EnvironmentOptions (allowing Environment option registering) <br>
     * - ProjectOptions (allowing Project option registering) <br>
     * - Constants (DEV/TESTER, GUI_REQUIRED, etc.) <br>
     * Override this method to add custom configuration.<br>
     * Call super.initPlugin() to keep default OMF configuration.<br>
     */
    public void initPlugin() {
        //        ProjectOptions.addConfigurator();
        //        ProjectOptions.addConfigurator(TestProjectOptionsConfigurator.getInstance())
        configureMagicDrawHookExecutor();
        configureListenerManager();
        configureActions();
        configureProjectListener();
        configureEnvironmentOptions();
        configureProjectOptions();
        configureConstants();
        configureFeatureRegisterer();
        configureFeatures();

        registerAllFeatures();

        isInitialized = true;
    }

    private void configureMagicDrawHookExecutor() {
        try {
            this.magicDrawHookExecutor = initMagicDrawHookExecutor();
            this.magicDrawHookExecutor.init(this);
            addOnStartupHookToFeatures();
        } catch (Exception e) {
            throw new OMFPluginRegisteringException("Error occurred during MagicDrawHookExecutorConfiguration", e);
        }
    }

    private void addOnStartupHookToFeatures() {
        try {
            Application.getInstance().insertActivityAfterStartup(() -> {
                try {
                    magicDrawHookExecutor.triggerOnMagicDrawStartHooks();
                } catch (CoreException2 coreException) {
                    ErrorHandler2.getInstance().handleException(coreException);
                } catch (RuntimeException e) {
                    ErrorHandler2.getInstance().handleException(new CoreException2("Error occurred during onMagicDrawStart hook execution", e));
                }
            });
        }catch (Exception e){
            throw new OMFPluginRegisteringException("Error occurred during onStartupHookConfiguration: \n" + e.getMessage(), e);
        }

    }

    private void configureFeatureRegisterer() {
        try {
            this.featureRegisterer = initFeatureRegisterer();
            this.uiActionFeatureItemRegisterer = new UIActionFeatureItemRegisterer(this);
            this.ruleEngineFeatureItemRegisterer = new RuleEngineFeatureItemRegisterer();
            this.optionFeatureItemRegisterer = new OptionFeatureItemRegisterer();
            this.projectOnlyUiActionRegisterer = new ProjectOnlyUIActionFeatureItemRegisterer(this);
            this.projectOnlyRuleEngineFeatureItemRegisterer = new ProjectOnlyRuleEngineFeatureItemRegisterer();
            this.projectOnlyOptionFeatureItemRegisterer = new ProjectOnlyOptionFeatureItemRegisterer();
            this.projectLifeCycleHookFeatureItemRegisterer = new ProjectLifeCycleHookFeatureItemRegisterer();
            this.magicDrawLifeCycleHookFeatureItemRegisterer = new MagicDrawLifeCycleHookFeatureItemRegisterer();
            this.featureLifeCycleHookFeatureItemRegisterer = new FeatureLifeCycleHookFeatureItemRegisterer();

            List<FeatureItemRegisterer> defaultFeatureRegisterer = List.of(
                    uiActionFeatureItemRegisterer,
                    ruleEngineFeatureItemRegisterer,
                    optionFeatureItemRegisterer,
                    projectLifeCycleHookFeatureItemRegisterer,
                    magicDrawLifeCycleHookFeatureItemRegisterer,
                    featureLifeCycleHookFeatureItemRegisterer
                    );

            List<ProjectOnlyFeatureItemRegisterer> defaultProjectOnlyFeatureRegisterer = List.of(projectOnlyUiActionRegisterer,
                    projectOnlyRuleEngineFeatureItemRegisterer,
                    projectOnlyOptionFeatureItemRegisterer);
            featureRegisterer.addAllIFeatureItemRegisterer(defaultFeatureRegisterer);
            featureRegisterer.addAllProjectOnlyFeatureItemRegisterer(defaultProjectOnlyFeatureRegisterer);
        } catch (Exception e) {
            throw new OMFPluginRegisteringException("Error occurred during FeatureRegistererConfiguration", e);
        }
    }

    private void configureListenerManager() {
        try {
            this.listenerManager = initListenerManager();
        } catch (Exception e) {
            throw new OMFPluginRegisteringException("Error occurred during ListenerManagerConfiguration", e);
        }
    }

    private void configureFeatures() {
        try {
            List<MDFeature> featureInstances = this.initFeatures();
            if (featureInstances == null) {
                OMFLogger2.logToUIConsole("No feature to registered in the plugin", OMFLogLevel2.WARNING);
                return;
            }

            featureInstances.forEach(f -> {
                if (features.containsKey(f.getName())) {
                    throw new OMFPluginRegisteringException("Can't init feature " + f.getName()
                            + " as a feature with the same name has already" +
                            "been instantiated in the plugin");
                } else {
                    features.put(f.getName(), f);
                }
            });
        } catch (Exception e) {
            throw new OMFPluginRegisteringException("Error occurred during FeatureConfiguration", e);
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
            throw new OMFPluginRegisteringException("Error occurred during constant configuration " +
                    "(JVM args, DEV/TESTER, GUI REQUIRED, etc)", e);
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
            throw new OMFPluginRegisteringException("Error occurred during ProjectListener configuration", e);
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
            throw new OMFPluginRegisteringException("Error occurred during BrowserAction Registering", e);
        }


        try {
            diagramConfigurator = initFeatureRegisteringDiagramConfigurator();
            if (diagramConfigurator == null)
                ColorPrinter.warn("[OMF] NO DIAGRAM CONFIGURATOR REGISTERED");
            else {
                actionManager.addDiagramContextConfigurator(DiagramTypeConstants.UML_ANY_DIAGRAM, diagramConfigurator);
            }
        } catch (Exception e) {
            throw new OMFPluginRegisteringException("Error occurred during DiagramAction Registering", e);
        }

        try {
            menuConfigurator = initFeatureRegisteringMainMenuConfigurator();
            if (menuConfigurator == null)
                ColorPrinter.warn("[OMF] NO MAIN MENU CONFIGURATOR REGISTERED");
            else
                actionManager.addMainMenuConfigurator(menuConfigurator);
        } catch (Exception e) {
            throw new OMFPluginRegisteringException("Error occurred during MainMenuAction Registering", e);
        }
    }


    protected void registerAllFeatures() {
        try {
            featureRegisterer.registerFeatures(getFeatures());
        } catch (Exception e) {
            throw new OMFPluginRegisteringException("Error occured while registering features");
        }
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

            environmentOptionsListener = new ArrayList<>();
            environmentOptionsListener.addAll(initEnvironmentOptionsListener());
            if (CollectionUtils.isNotEmpty(environmentOptionsListener))
                environmentOptionsListener.forEach(options::addEnvironmentChangeListener);
        } catch (Exception e) {
            throw new OMFPluginRegisteringException("Error occurred during EnvironmentOptions Configuration", e);
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
            throw new OMFPluginRegisteringException("Error occurred during ProjectOptions Configuration", e);
        }
    }


    //------------------------------------ GETTER SETTER ----------------------------------------------------//
    public RuleEngineFeatureItemRegisterer getRuleEngineRegisterer() {
        return ruleEngineFeatureItemRegisterer;
    }

    public UIActionFeatureItemRegisterer getUiActionFeatureItemRegisterer() {
        return uiActionFeatureItemRegisterer;
    }

    public OptionFeatureItemRegisterer getOptionRegisterer() {
        return optionFeatureItemRegisterer;
    }

    @Override
    public boolean close() {
        return true;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    public List<AOptionListener> initEnvironmentOptionsListener() {
        return Collections.emptyList();
    }

    public List<MDFeature> getFeatures() {
        return new ArrayList<>(features.values());
    }

    public Optional<MDFeature> getFeatureByName(String name) {
        if (features.containsKey(name)) {
            return Optional.of(features.get(name));
        }
        return Optional.empty();
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

    public Optional<OMFPropertyOptionsGroup> getEnvironmentOptionsGroup() {
        return Optional.ofNullable(environmentOptionsGroup);
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

    public ProjectOnlyUIActionFeatureItemRegisterer getProjectOnlyUiActionRegisterer() {
        return projectOnlyUiActionRegisterer;
    }

    public ProjectOnlyRuleEngineFeatureItemRegisterer getProjectOnlyRuleEngineFeatureItemRegisterer() {
        return projectOnlyRuleEngineFeatureItemRegisterer;
    }

    public ProjectOnlyOptionFeatureItemRegisterer getProjectOnlyOptionFeatureItemRegisterer() {
        return projectOnlyOptionFeatureItemRegisterer;
    }

    public RuleEngineFeatureItemRegisterer getRuleEngineFeatureItemRegisterer() {
        return ruleEngineFeatureItemRegisterer;
    }

    public OptionFeatureItemRegisterer getOptionFeatureItemRegisterer() {
        return optionFeatureItemRegisterer;
    }

    public MagicDrawLifeCycleHookFeatureItemRegisterer getMagicDrawLifeCycleHookFeatureItemRegisterer() {
        return magicDrawLifeCycleHookFeatureItemRegisterer;
    }

    public ProjectLifeCycleHookFeatureItemRegisterer getProjectLifeCycleHookFeatureItemRegisterer() {
        return projectLifeCycleHookFeatureItemRegisterer;
    }

    public FeatureLifeCycleHookFeatureItemRegisterer getFeatureLifeCycleHookFeatureItemRegisterer() {
        return featureLifeCycleHookFeatureItemRegisterer;
    }

    public MagicDrawHookExecutor getMagicDrawHookExecutor() {
        return magicDrawHookExecutor;
    }
}
