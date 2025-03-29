/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.ui.projectoptions;

import com.nomagic.magicdraw.core.options.ProjectOptions;
import com.nomagic.magicdraw.core.options.ProjectOptionsConfigurator;
import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;

import java.util.ArrayList;
import java.util.List;

public class FeatureProjectOptionsConfigurator implements ProjectOptionsConfigurator {

    private List<Option> options;
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

    private void createIfAbsent(Option opt, ProjectOptions projectOptions) {
        Property optProperty = opt.getProperty();
        projectOptions.addProperty(projectOptionMainCategory, optProperty);

    }

    @Override
    public void afterLoad(ProjectOptions projectOptions) {

    }

    public void addOption(Option projectOption){
        options.add(projectOption);
    }

    public void addAllOption(List<Option> projectOptions){
        options.addAll(projectOptions);
    }


    public void removeOption(Option projectOption){
        options.remove(projectOption);
    }

    public void removeAllOption(List<Option> projectOptions){
        options.removeAll(projectOptions);
    }

}
