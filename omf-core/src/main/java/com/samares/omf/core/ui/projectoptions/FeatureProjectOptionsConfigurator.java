/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.ui.projectoptions;

import com.nomagic.magicdraw.core.options.ProjectOptions;
import com.nomagic.magicdraw.core.options.ProjectOptionsConfigurator;
import com.nomagic.magicdraw.properties.Property;
import com.samares.omf.core.feature.options.IOption;

import java.util.ArrayList;
import java.util.List;

public class FeatureProjectOptionsConfigurator implements ProjectOptionsConfigurator {

    private List<IOption> l_options;
    String projectOptionMainCategory;

    private static FeatureProjectOptionsConfigurator instance;

    public static FeatureProjectOptionsConfigurator getInstance(){
        return getInstance(ProjectOptions.PROJECT_GENERAL_PROPERTIES);
    }
    public static FeatureProjectOptionsConfigurator getInstance(String mainCategory){
        if(instance == null)
            instance = new FeatureProjectOptionsConfigurator(mainCategory);
        return instance;
    }

    public FeatureProjectOptionsConfigurator(){
        l_options = new ArrayList<>();
    }
    public FeatureProjectOptionsConfigurator(String categoryName){
        l_options = new ArrayList<>();
        this.projectOptionMainCategory = categoryName;
    }
    
    @Override
    public void configure(ProjectOptions projectOptions) {
        l_options.forEach(opt -> createIfAbsent(opt, projectOptions));
    }

    private void createIfAbsent(IOption opt, ProjectOptions projectOptions) {
        Property optProperty = opt.getProperty();
        projectOptions.addProperty(projectOptionMainCategory, optProperty);

    }

    @Override
    public void afterLoad(ProjectOptions projectOptions) {

    }

    public void addOption(IOption projectOption){
        l_options.add(projectOption);
    }

    public void addAllOption(List<IOption> l_projectOption){
        l_options.addAll(l_projectOption);
    }


    public void removeOption(IOption projectOption){
        l_options.remove(projectOption);
    }

    public void removeAllOption(List<IOption> l_projectOption){
        l_options.removeAll(l_projectOption);
    }

}
