/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils.profile;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.magicdraw.sysml.util.MDCustomizationForSysMLProfile;
import com.nomagic.uml2.MagicDrawProfile;
import com.nomagic.uml2.StandardProfile;
import com.samares.omf.core.utils.OMFConstants;
import com.samares.omf.core.utils.OMFUtils;

public class Profile {
    private MagicDrawProfile magicDrawProfile = null;
    private StandardProfile standardProfile = null;
    private SysMLProfile sysmlProfile = null;
    private MDCustomizationForSysMLProfile mdCustomSysMLProfile = null;

    private Project project;

    private static Profile instance = null;

    public Profile(Project project){
        this.project = project;
    }

    public static Profile getInstance(){
        return getInstance(OMFUtils.currentProject);
    }

    public static Profile getInstance(Project project){
        if(instance == null) {
            instance = new Profile(project);
            instance.init(project);
        }
        if(project != instance.project) {
            instance.init(project);
        }
        return instance;
    }

    public void init(Project project){
        this.project = project;
        this.standardProfile = StandardProfile.getInstanceByProject(project);
        this.sysmlProfile = SysMLProfile.getInstanceByProject(project);
        this.mdCustomSysMLProfile = MDCustomizationForSysMLProfile.getInstanceByProject(project);
        this.magicDrawProfile = MagicDrawProfile.getInstanceByProject(project);

        OMFConstants.reinitConstants();
    }

    public MagicDrawProfile getMagicDraw() {
        return magicDrawProfile;
    }

    public StandardProfile getStandard() {
        return standardProfile;
    }

    public SysMLProfile getSysml() {
        return sysmlProfile;
    }

    public MDCustomizationForSysMLProfile getMDCustomSysml() {
        return mdCustomSysMLProfile;
    }

    public Project getProject() {
        return project;
    }

    public static MDCustomizationForSysMLProfile getSysmlAdditionalStereotypes() {
        return getInstance().getMDCustomSysml();
    }

}
