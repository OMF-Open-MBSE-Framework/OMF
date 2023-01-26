package com.samares.omf.core.feature;

import com.samares.omf.core.actions.v2.configurators.OMFBrowserConfigurator;
import com.samares.omf.core.feature.actions.MDActionRegisterer;
import com.samares.omf.core.feature.options.OptionRegisterer;
import com.samares.omf.core.feature.ruleengine.RuleEngineRegisterer;
import com.samares.omf.core.listeners.IListenerManager;
import com.samares.omf.core.ui.OMFDiagramConfigurator;
import com.samares.omf.core.ui.OMFMainMenuConfigurator;

import java.util.ArrayList;
import java.util.List;

public class FeatureRegister implements IFeatureRegisterer{
    OMFBrowserConfigurator browserConfigurator;
    OMFDiagramConfigurator diagramConfigurator;
    OMFMainMenuConfigurator menuConfigurator;

    RuleEngineRegisterer ruleEngineRegisterer;
    MDActionRegisterer mdActionRegisterer;

    OptionRegisterer optionRegisterer;
    List<MDFeature> l_registeredFeatures = new ArrayList<>();

    //TODO: Create a class regrouping all Configurators
    public FeatureRegister(OMFBrowserConfigurator browser, OMFDiagramConfigurator diagramConfigurator, OMFMainMenuConfigurator menuConfigurator, IListenerManager listenerManager){
        this.browserConfigurator = browser;
        this.diagramConfigurator = diagramConfigurator;
        this.menuConfigurator    = menuConfigurator;
        this.mdActionRegisterer  = new MDActionRegisterer(browser, diagramConfigurator, menuConfigurator);
        this.ruleEngineRegisterer = new RuleEngineRegisterer(listenerManager);
        this.optionRegisterer = new OptionRegisterer();
    }

    /**
     * Register a feature using delegation to register MDActions and RuleEngines. Return true if the feature is already registered;
     * @param mdFeature
     * @return
     */
    public void registerFeature(MDFeature mdFeature){
        boolean isAlreadyRegistered = l_registeredFeatures.stream().anyMatch(mdFeature.getClass()::isInstance);
        if(isAlreadyRegistered)
            return;

        l_registeredFeatures.add(mdFeature);
        mdFeature.activate();
        optionRegisterer.registerFeature(mdFeature);
        mdActionRegisterer.registerFeature(mdFeature);
        ruleEngineRegisterer.registerFeature(mdFeature);
    }

    public void unregisterFeature(MDFeature mdFeature){
        boolean isNotRegistered = !l_registeredFeatures.contains(mdFeature);
        l_registeredFeatures.remove(mdFeature);

        mdFeature.deactivate();
        mdActionRegisterer.unregisterFeature(mdFeature);
        ruleEngineRegisterer.unregisterFeature(mdFeature);
        optionRegisterer.unregisterFeature(mdFeature);
//        return isNotRegistered;
    }

    public List<MDFeature> getRegisteredFeatures() {
        return l_registeredFeatures;
    }


    //-------------------------------- GETTER / SETTER --------------------------------------------
    public OMFBrowserConfigurator getBrowserConfigurator() {
        return browserConfigurator;
    }
    public void setBrowserConfigurator(OMFBrowserConfigurator browserConfigurator) {
        this.browserConfigurator = browserConfigurator;
    }
    public OMFDiagramConfigurator getDiagramConfigurator() {
        return diagramConfigurator;
    }
    public void setDiagramConfigurator(OMFDiagramConfigurator diagramConfigurator) {
        this.diagramConfigurator = diagramConfigurator;
    }
    public OMFMainMenuConfigurator getMenuConfigurator() {
        return menuConfigurator;
    }
    public void setMenuConfigurator(OMFMainMenuConfigurator menuConfigurator) {
        this.menuConfigurator = menuConfigurator;
    }
    public RuleEngineRegisterer getRuleEngineRegisterer() {
        return ruleEngineRegisterer;
    }
    public void setRuleEngineRegisterer(RuleEngineRegisterer ruleEngineRegisterer) {
        this.ruleEngineRegisterer = ruleEngineRegisterer;
    }
    public MDActionRegisterer getMdActionRegisterer() {
        return mdActionRegisterer;
    }
    public void setMdActionRegisterer(MDActionRegisterer mdActionRegisterer) {
        this.mdActionRegisterer = mdActionRegisterer;
    }
    public OptionRegisterer getOptionRegisterer() {
        return optionRegisterer;
    }
    public void setOptionRegisterer(OptionRegisterer optionRegisterer) {
        this.optionRegisterer = optionRegisterer;
    }
    public List<MDFeature> getL_registeredFeatures() {
        return l_registeredFeatures;
    }
    public void setL_registeredFeatures(List<MDFeature> l_registeredFeatures) {
        this.l_registeredFeatures = l_registeredFeatures;
    }
}
