/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
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

    private List<IOption> options;
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
        options = new ArrayList<>();
    }
    public FeatureProjectOptionsConfigurator(String categoryName){
        options = new ArrayList<>();
        this.projectOptionMainCategory = categoryName;
    }
    
    @Override
    public void configure(ProjectOptions projectOptions) {
        options.forEach(opt -> createIfAbsent(opt, projectOptions));
    }

    private void createIfAbsent(IOption opt, ProjectOptions projectOptions) {
        Property optProperty = opt.getProperty();
        projectOptions.addProperty(projectOptionMainCategory, optProperty);

    }

    @Override
    public void afterLoad(ProjectOptions projectOptions) {

    }

    public void addOption(IOption projectOption){
        options.add(projectOption);
    }

    public void addAllOption(List<IOption> projectOptions){
        options.addAll(projectOptions);
    }


    public void removeOption(IOption projectOption){
        options.remove(projectOption);
    }

    public void removeAllOption(List<IOption> projectOptions){
        options.removeAll(projectOptions);
    }

}
