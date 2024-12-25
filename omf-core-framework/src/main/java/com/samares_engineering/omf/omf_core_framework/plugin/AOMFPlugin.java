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
import com.nomagic.uml2.diagram.DiagramTypes;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFNotificationManager;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.SysoutColorPrinter;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.PluginRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFBrowserConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFDiagramConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFMainMenuConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.magicdraw.MagicDrawHookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.FeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.ProjectOnlyFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.LiveActionEngineFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.OptionFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.UIActionFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.hooks.FeatureLifeCycleHookFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.hooks.MagicDrawLifeCycleHookFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.hooks.ProjectLifeCycleHookFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly.ProjectOnlyLiveActionEngineFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly.ProjectOnlyOptionFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly.ProjectOnlyUIActionFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.ProjectListener;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_core_framework.ui.projectoptions.FeatureProjectOptionsConfigurator;
import com.samares_engineering.omf.omf_core_framework.utils.OMFConstants;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * TEMPLATE of plugin allowing easily feature registering and development.
 * It includes auto registering of:
 * - Configurators (Browser, Diagram, Menu)
 * - Options (Environment, and Project (NOT IMPLEMENTED YET)
 * - Features registering (Listeners, LiveActionEngines, Options, MDActions)
 * <p>
 * For quick plugin registering use OMFxxx as default classes (OMFBrowserConfigurator, OMFEnvironmentOptions, ...)
 */
public abstract class AOMFPlugin extends Plugin implements OMFPlugin, OMFPluginInitializer {
    private boolean isInitialized = false;

    // Initialized by user implementing the plugin (basically API of the framework for the plugin)
    private final Map<String, OMFFeature> features = new LinkedHashMap<>();
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
    private LiveActionEngineFeatureItemRegisterer liveActionEngineFeatureItemRegisterer;
    private OptionFeatureItemRegisterer optionFeatureItemRegisterer;
    private ProjectOnlyUIActionFeatureItemRegisterer projectOnlyUiActionRegisterer;
    private ProjectOnlyLiveActionEngineFeatureItemRegisterer projectOnlyLiveActionEngineFeatureItemRegisterer;
    private ProjectOnlyOptionFeatureItemRegisterer projectOnlyOptionFeatureItemRegisterer;
    private ProjectLifeCycleHookFeatureItemRegisterer projectLifeCycleHookFeatureItemRegisterer;
    private MagicDrawLifeCycleHookFeatureItemRegisterer magicDrawLifeCycleHookFeatureItemRegisterer;
    private FeatureLifeCycleHookFeatureItemRegisterer featureLifeCycleHookFeatureItemRegisterer;

    // MagicDraw Hook Executor
    private MagicDrawHookExecutor magicDrawHookExecutor;
    
    //------------------------ ELEMENTS TO REGISTER AT INIT -------------------------------------------//
    /**
     * Define the MagicDrawHookExecutor to register at plugin Initialization
     * This HookExecutor will be used for FeatureRegistering with MagicDraw hooks
     *
     * @return MagicDrawHookExecutor to register
     */
    @Override
    public MagicDrawHookExecutor initMagicDrawHookExecutor() {
        return new MagicDrawHookExecutor();
    }

    /**
     * Define the FeatureRegisterer to register at plugin Initialization
     * This FeatureRegisterer will be used for FeatureRegistering with all features
     *
     * @return FeatureRegisterer to register
     */
    @Override
    public FeatureRegisterer initFeatureRegisterer() {
        return new FeatureRegisterer(this);
    }


    //------------------------ INITIALIZATION PROCESS-------------------------------------------//

    /**
     * Do not override this method, use onPluginInit() instead to add behavior on init.
     * It will call initPlugin() and handle exceptions
     */
    @Override
    public void init() {
        try {
            initLogger();
            OMFErrorHandler.init(this);
        } catch (Exception e) {
            throw new PluginRegisteringException("Error occurred during error management initialization", e);
        }

        try {
            configurePlugin();
            onPluginInit(); // Call the overridable on plugin init hook
        } catch (Exception e) {
            OMFErrorHandler.getInstance().handleException(new PluginRegisteringException("Error occurred during Plugin Initialization", e));
        }
    }

    protected void initLogger() {
        OMFNotificationManager.init(10);
        OMFLogger.init(this);
        OMFLogger2.init(this);
    }

    // Helper methods

    @Override
    public void registerAllFeatures() {
        try {
            featureRegisterer.registerFeatures(getFeatures());
        } catch (Exception e) {
            throw new PluginRegisteringException("Error occured while registering features");
        }
    }

    @Override
    public void unregisterAllFeatures() {
        featureRegisterer.unregisterFeatures(getFeatures());
    }
    
    // Plugin configuration

    /**
     * Initialize the plugin, and will configure:
     * - MagicDrawHookExecutor (for MagicDraw lifecycle hooks) <br>
     * - ListenerManager (for all the core listeners) <br>
     * - Actions Configurators (Browser, Diagram, Menu) <br>
     * - ProjectListener (for project lifecycle automations) <br>
     * - EnvironmentOptions (allowing Environment option registering) <br>
     * - ProjectOptions (allowing Project option registering) <br>
     * - Constants (DEV/TESTER, GUI_REQUIRED, etc.) <br>
     */
    protected void configurePlugin() {
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
            throw new PluginRegisteringException("Error occurred during MagicDrawHookExecutorConfiguration", e);
        }
    }

    private void addOnStartupHookToFeatures() {
        try {
            Application.getInstance().insertActivityAfterStartup(() -> {
                try {
                    magicDrawHookExecutor.triggerOnMagicDrawStartHooks();
                } catch (CoreException2 coreException) {
                    OMFErrorHandler.getInstance().handleException(coreException);
                } catch (RuntimeException e) {
                    OMFErrorHandler.getInstance().handleException(new CoreException2("Error occurred during onMagicDrawStart hook execution", e));
                }
            });
        } catch (Exception e) {
            throw new PluginRegisteringException("Error occurred during onStartupHookConfiguration: \n" + e.getMessage(), e);
        }

    }

    private void configureFeatureRegisterer() {
        try {
            this.featureRegisterer = initFeatureRegisterer();
            this.uiActionFeatureItemRegisterer = new UIActionFeatureItemRegisterer(this);
            this.liveActionEngineFeatureItemRegisterer = new LiveActionEngineFeatureItemRegisterer();
            this.optionFeatureItemRegisterer = new OptionFeatureItemRegisterer();
            this.projectOnlyUiActionRegisterer = new ProjectOnlyUIActionFeatureItemRegisterer(this);
            this.projectOnlyLiveActionEngineFeatureItemRegisterer = new ProjectOnlyLiveActionEngineFeatureItemRegisterer();
            this.projectOnlyOptionFeatureItemRegisterer = new ProjectOnlyOptionFeatureItemRegisterer();
            this.projectLifeCycleHookFeatureItemRegisterer = new ProjectLifeCycleHookFeatureItemRegisterer();
            this.magicDrawLifeCycleHookFeatureItemRegisterer = new MagicDrawLifeCycleHookFeatureItemRegisterer();
            this.featureLifeCycleHookFeatureItemRegisterer = new FeatureLifeCycleHookFeatureItemRegisterer();

            List<FeatureItemRegisterer> defaultFeatureRegisterer = List.of(
                    getUiActionFeatureItemRegisterer(),
                    getLiveActionEngineRegisterer(),
                    getOptionRegisterer(),
                    getProjectLifeCycleHookFeatureItemRegisterer(),
                    getMagicDrawLifeCycleHookFeatureItemRegisterer(),
                    getFeatureLifeCycleHookFeatureItemRegisterer()
            );

            List<ProjectOnlyFeatureItemRegisterer> defaultProjectOnlyFeatureRegisterer = List.of(projectOnlyUiActionRegisterer,
                    projectOnlyLiveActionEngineFeatureItemRegisterer,
                    projectOnlyOptionFeatureItemRegisterer);
            featureRegisterer.addAllIFeatureItemRegisterer(defaultFeatureRegisterer);
            featureRegisterer.addAllProjectOnlyFeatureItemRegisterer(defaultProjectOnlyFeatureRegisterer);
        } catch (Exception e) {
            throw new PluginRegisteringException("Error occurred during FeatureRegistererConfiguration", e);
        }
    }

    private void configureListenerManager() {
        try {
            this.listenerManager = initListenerManager();
        } catch (Exception e) {
            throw new PluginRegisteringException("Error occurred during ListenerManagerConfiguration", e);
        }
    }

    private void configureFeatures() {
        try {
            List<OMFFeature> featureInstances = this.initFeatures();
            if (featureInstances == null) {
                OMFLogger.logToUIConsole("No feature to registered in the plugin", OMFLogLevel.WARNING);
                return;
            }

            featureInstances.forEach(f -> {
                if (features.containsKey(f.getName())) {
                    throw new PluginRegisteringException("Can't init feature " + f.getName()
                            + " as a feature with the same name has already" +
                            "been instantiated in the plugin");
                } else {
                    features.put(f.getName(), f);
                }
            });
        } catch (Exception e) {
            throw new PluginRegisteringException("Error occurred during FeatureConfiguration", e);
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
            throw new PluginRegisteringException("Error occurred during constant configuration " +
                    "(JVM args, DEV/TESTER, GUI REQUIRED, etc)", e);
        }
    }

    protected void configureProjectListener() {
        try {
            projectListener = initProjectListener();
            if (projectListener != null)
                Application.getInstance().getProjectsManager().addProjectListener(projectListener);
            else
                SysoutColorPrinter.warn("[OMF] NO PROJECT LISTENER REGISTERED");
        } catch (Exception e) {
            throw new PluginRegisteringException("Error occurred during ProjectListener configuration", e);
        }
    }

    protected void configureActions() {
        ActionsConfiguratorsManager actionManager = ActionsConfiguratorsManager.getInstance();

        try {
            browserConfigurator = initFeatureRegisteringBrowserConfigurator();
            if (browserConfigurator == null)
                SysoutColorPrinter.warn("[OMF] NO BROWSER CONFIGURATOR REGISTERED");
            else {
                actionManager.addContainmentBrowserContextConfigurator(browserConfigurator);
                actionManager.addContainmentBrowserShortcutsConfigurator(browserConfigurator);
            }
        } catch (Exception e) {
            throw new PluginRegisteringException("Error occurred during BrowserAction Registering", e);
        }


        try {
            diagramConfigurator = initFeatureRegisteringDiagramConfigurator();
            if (diagramConfigurator == null)
                SysoutColorPrinter.warn("[OMF] NO DIAGRAM CONFIGURATOR REGISTERED");
            else {
                DiagramTypeConstants.STANDARD_TYPES.forEach(diagramType -> actionManager.addDiagramContextConfigurator(diagramType, diagramConfigurator));
                DiagramTypeConstants.STANDARD_TYPES.forEach(diagramType -> actionManager.addDiagramShortcutsConfigurator(diagramType, diagramConfigurator));
                actionManager.addDiagramContextConfigurator(DiagramTypes.GENERIC_TABLE, diagramConfigurator);
                actionManager.addDiagramContextConfigurator(DiagramTypes.DEPENDENCY_MATRIX, diagramConfigurator);
                actionManager.addDiagramContextConfigurator(DiagramTypes.GLOSSARY_TABLE, diagramConfigurator);

            }
        } catch (Exception e) {
            throw new PluginRegisteringException("Error occurred during DiagramAction Registering", e);
        }

        try {
            menuConfigurator = initFeatureRegisteringMainMenuConfigurator();
            if (menuConfigurator == null)
                SysoutColorPrinter.warn("[OMF] NO MAIN MENU CONFIGURATOR REGISTERED");
            else
                actionManager.addMainMenuConfigurator(menuConfigurator);
        } catch (Exception e) {
            throw new PluginRegisteringException("Error occurred during MainMenuAction Registering", e);
        }
    }

    protected void configureEnvironmentOptions() {

        Application application = Application.getInstance();
        EnvironmentOptions options = application.getEnvironmentOptions();

        try {
            environmentOptionsGroup = initFeatureRegisteringEnvironmentOptionGroup();
            if (environmentOptionsGroup == null) {
                SysoutColorPrinter.warn("[OMFPluginRegistering] NO ENVIRONMENT OPTIONS REGISTERED");
                return;
            }


            options.addGroup(environmentOptionsGroup);

            environmentOptionsListener = new ArrayList<>();
            environmentOptionsListener.addAll(initEnvironmentOptionsListener());
            if (CollectionUtils.isNotEmpty(environmentOptionsListener))
                environmentOptionsListener.forEach(options::addEnvironmentChangeListener);
        } catch (Exception e) {
            throw new PluginRegisteringException("Error occurred during EnvironmentOptions Configuration", e);
        }

    }

    private void configureProjectOptions() {
        try {
            projectOptionConfigurator = initFeatureRegisteringProjectOptionGroup();

            if (projectOptionConfigurator == null) {
                SysoutColorPrinter.warn("[OMF] NO PROJECT OPTIONS REGISTERED");
                return;
            }

            ProjectOptions.addConfigurator(projectOptionConfigurator);
        } catch (Exception e) {
            throw new PluginRegisteringException("Error occurred during ProjectOptions Configuration", e);
        }
    }
    
    
    //------------------------------------ GETTER SETTER ----------------------------------------------------//

    @Override
    public LiveActionEngineFeatureItemRegisterer getLiveActionEngineRegisterer() {
        return liveActionEngineFeatureItemRegisterer;
    }

    @Override
    public UIActionFeatureItemRegisterer getUiActionFeatureItemRegisterer() {
        return uiActionFeatureItemRegisterer;
    }

    @Override
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

    @Override
    public List<AOptionListener> initEnvironmentOptionsListener() {
        return Collections.emptyList();
    }

    @Override
    public List<OMFFeature> getFeatures() {
        return new ArrayList<>(features.values());
    }

    @Override
    public Optional<OMFFeature> getFeatureByName(String name) {
        if (features.containsKey(name)) {
            return Optional.of(features.get(name));
        }
        return Optional.empty();
    }

    @Override
    public FeatureRegisterer getFeatureRegisterer() {
        return featureRegisterer;
    }

    @Override
    public boolean isInitialized() {
        return this.isInitialized;
    }

    @Override
    public List<AOptionListener> getEnvironmentOptionsListener() {
        return environmentOptionsListener;
    }

    @Override
    public FeatureProjectOptionsConfigurator getProjectOptionConfigurator() {
        return projectOptionConfigurator;
    }

    @Override
    public Optional<OMFPropertyOptionsGroup> getEnvironmentOptionsGroup() {
        return Optional.ofNullable(environmentOptionsGroup);
    }

    @Override
    public IListenerManager getListenerManager() {
        return listenerManager;
    }

    @Override
    public ProjectListener getProjectListener() {
        return projectListener;
    }

    @Override
    public OMFBrowserConfigurator getBrowserConfigurator() {
        return browserConfigurator;
    }

    @Override
    public OMFDiagramConfigurator getDiagramConfigurator() {
        return diagramConfigurator;
    }

    @Override
    public OMFMainMenuConfigurator getMenuConfigurator() {
        return menuConfigurator;
    }

    @Override
    public String getName() {
        return this.getDescriptor().getName();
    }

    @Override
    public ProjectOnlyUIActionFeatureItemRegisterer getProjectOnlyUiActionRegisterer() {
        return projectOnlyUiActionRegisterer;
    }

    @Override
    public ProjectOnlyLiveActionEngineFeatureItemRegisterer getProjectOnlyLiveActionEngineFeatureItemRegisterer() {
        return projectOnlyLiveActionEngineFeatureItemRegisterer;
    }

    @Override
    public ProjectOnlyOptionFeatureItemRegisterer getProjectOnlyOptionFeatureItemRegisterer() {
        return projectOnlyOptionFeatureItemRegisterer;
    }

    @Override
    public LiveActionEngineFeatureItemRegisterer getLiveActionEngineFeatureItemRegisterer() {
        return liveActionEngineFeatureItemRegisterer;
    }

    @Override
    public OptionFeatureItemRegisterer getOptionFeatureItemRegisterer() {
        return optionFeatureItemRegisterer;
    }

    @Override
    public MagicDrawLifeCycleHookFeatureItemRegisterer getMagicDrawLifeCycleHookFeatureItemRegisterer() {
        return magicDrawLifeCycleHookFeatureItemRegisterer;
    }

    @Override
    public ProjectLifeCycleHookFeatureItemRegisterer getProjectLifeCycleHookFeatureItemRegisterer() {
        return projectLifeCycleHookFeatureItemRegisterer;
    }

    @Override
    public FeatureLifeCycleHookFeatureItemRegisterer getFeatureLifeCycleHookFeatureItemRegisterer() {
        return featureLifeCycleHookFeatureItemRegisterer;
    }

    @Override
    public MagicDrawHookExecutor getMagicDrawHookExecutor() {
        return magicDrawHookExecutor;
    }
}
