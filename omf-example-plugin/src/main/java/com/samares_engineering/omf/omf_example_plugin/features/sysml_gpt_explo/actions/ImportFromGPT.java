/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.sysml_gpt_explo.actions;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_example_plugin.features.sysml_gpt_explo.SysmlGptExploFeature;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MenuAction
@DeactivateListener
@MDAction(actionName = "Import from GPT", category = "OMF.Gpt explo")
public class ImportFromGPT extends AUIAction {
    private Map<String, Class> createdBlocks = new HashMap<>();
    private Map<Property, String> partPropertiesToConnect = new HashMap<>();

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        createdBlocks = new HashMap<>();
        partPropertiesToConnect = new HashMap<>();

        // Load the JSON data from file
        String jsonFilePath = null;
        jsonFilePath = (String) feature.getPlugin().getEnvironmentOptionsGroup()
                .orElseThrow(() -> new OMFCriticalException("No environment options groups have been declared for this " +
                        "plugin")
                ).getProperty(SysmlGptExploFeature.GPT_GENERATED_JSON_TO_IMPORT).getValue();

        JSONTokener tokener = null;
        try {
            tokener = new JSONTokener(new FileReader(jsonFilePath));
        } catch (FileNotFoundException e) {
            throw new OMFCriticalException("The specified file " + jsonFilePath + " could not be found", e);
        }
        JSONObject json = new JSONObject(tokener);

        // Get the diagram name and create a new BDD
        String diagramName = json.getString("diagramName");
        Project project = OMFUtils.getProject();

        Package parentPackage = project.getElementsFactory().createPackageInstance();
        parentPackage.setName(diagramName);
        parentPackage.setOwner(project.getPrimaryModel());

        //Diagram diagram = project.getElementsFactory().createDiagramInstance();
        //diagram.setName(diagramName);
        //diagram.setOwner(project.getPrimaryModel())

        // Create the blocks and properties
        JSONArray blocksData = json.getJSONArray("blocks");

        createBlocksAndSubBlocks(parentPackage, blocksData);

        // Connect the part properties
        for (Property property : partPropertiesToConnect.keySet()) {
            String typingBlockName = partPropertiesToConnect.get(property);
            Class typingBlock;
            if (!createdBlocks.containsKey(typingBlockName)) {
                // Create the block if it doesn't exist (sometimes GPT forgets to declare the block)
                typingBlock = SysMLFactory.getInstance().createBlock(parentPackage);
                typingBlock.setName(typingBlockName);
            } else {
                typingBlock = createdBlocks.get(typingBlockName);
            }
            property.setType(typingBlock);
        }
    }

    private void createBlocksAndSubBlocks(Element parent, JSONArray blocksData) {
        for (int i = 0; i < blocksData.length(); i++) {
            JSONObject blockData = blocksData.getJSONObject(i);

            // Create the block
            String blockName = blockData.getString("name");
            Class block = SysMLFactory.getInstance().createBlock(parent);
            block.setName(blockName);
            createdBlocks.put(blockName, block);

            // Add the properties to the block
            JSONArray properties = blockData.getJSONArray("properties");
            for (int j = 0; j < properties.length(); j++) {
                JSONObject property = properties.getJSONObject(j);
                String name = property.getString("name");
                String type = property.getString("type");
                String propertyType = property.getString("propertyType");

                if (propertyType.equals("part")) {
                    Property partProperty = SysMLFactory.getInstance().createPartProperty(block);
                    partProperty.setName(name);
                    partPropertiesToConnect.put(partProperty, type);
                } else if (propertyType.equals("value")) {
                    Property valueProperty = SysMLFactory.getInstance().createValueProperty(block);
                    valueProperty.setName(name);
                }
            }

            // Create the sub blocks if they exist
            if (blockData.has("blocks")) {
                createBlocksAndSubBlocks(block, blockData.getJSONArray("blocks"));
            }
        }
    }

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return true;
    }
}
