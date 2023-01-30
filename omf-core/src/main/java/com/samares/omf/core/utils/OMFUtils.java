/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/
package com.samares.omf.core.utils;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectableElement;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares.omf.core.utils.errorManagement.OMFErrorHandler;
import com.samares.omf.core.utils.errorManagement.exceptions.GenericException;
import com.samares.omf.core.utils.errorManagement.exceptions.OMFException;
import com.samares.omf.core.utils.profile.Profile;
import org.apache.commons.lang.StringUtils;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class OMFUtils {

    public static File lastPath = null;

    public static Project currentProject = null;

    public static Diagram getOpenedDiagram() {
        DiagramPresentationElement diagramPresentationElement = Objects.requireNonNull(currentProject, "Can't " +
                        "get open diagram as project is null")
                .getActiveDiagram();

        return Objects.requireNonNull(diagramPresentationElement, "Can't get open diagram as" +
                        " diagramPresentationElement is null")
                .getDiagram();
    }

    public static DiagramPresentationElement getDiagram(Diagram diagram) {
        return Objects.requireNonNull(currentProject, "Can't get diagram as project is null")
                .getDiagram(diagram);
    }

    public static void unzipFile(String zipFilePath, String outputDirectoryPath) {
        File dir = new File(outputDirectoryPath);
        // create output directory if it doesn't exist
        if (!dir.exists()) dir.mkdirs();
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try (FileInputStream fis = new FileInputStream(zipFilePath);
             ZipInputStream zis = new ZipInputStream(fis)
        ) {
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(outputDirectoryPath + File.separator + fileName);
                //create directories for sub directories in zip
                int len;
                new File(newFile.getParent()).mkdirs();
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
        } catch (IOException e) {
            OMFErrorHandler.handleException(e, false);
        }
    }

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static File searchFile(File file, String search) {
        if (file.isDirectory()) {
            File[] arr = file.listFiles();
            for (File f : arr) {
                File found = searchFile(f, search);
                if (found != null)
                    return found;
            }
        } else {
            if (file.getName().equals(search)) {
                return file;
            }
        }
        return null;
    }

    /**
     * Split a String with regex given as parameter
     *
     * @param lineToSplit String
     * @param regex       String
     * @return List<String>
     */
    public static List<String> getValuesWithinLine(String lineToSplit, String regex) {
        assert lineToSplit != null;
        assert regex != null;
        List<String> result = new ArrayList<>();
        String[] strArray = null;

        if (lineToSplit.matches(regex)) {
            strArray = lineToSplit.split(regex);
            Collections.addAll(result, strArray);
        } else {
            result.add(lineToSplit);
        }
        return result;
    }

    public static void setCurrentDirectory(JFileChooser fileChooser) {
        if (lastPath != null)
            fileChooser.setCurrentDirectory(lastPath);
    }

    public static void setDefaultPath(@CheckForNull File selectedFile) {
        lastPath = selectedFile.getParentFile();
    }

    public static List<Property> getPropertyListFromElementList(List<Element> elementList) {
        return elementList.stream()
                .filter(Objects::nonNull)
                .filter(Property.class::isInstance)
                .map(Property.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Gets common ancestor.
     *
     * @param part1          the part 1
     * @param part2          the part 2
     * @param untilObject    the until object
     * @param availableParts the available parts
     * @return the common ancestor
     */
    public static Class getCommonAncestor(Property part1, Property part2, Class untilObject, List<Property> availableParts) throws OMFException {
        Class commonAncestor = null;
        List<Property> nestedPart1List = old_calculateNestedPath(new ArrayList<>(), part1, untilObject, availableParts);
        List<Property> nestedPart2List = old_calculateNestedPath(new ArrayList<>(), part2, untilObject, availableParts);

        for (Property p : nestedPart1List) {
            if (nestedPart2List.contains(p)) {
                commonAncestor = (Class) p.getType();
                return commonAncestor;
            }
        }

        return untilObject;
    }

    public static Element getCommonAncestorFromPropertyPath(List<Property> propertyPathSRC, List<Property> propertyPathDST) throws OMFException {

        if (propertyPathSRC.size() == 0 && propertyPathDST.size() == 0)  // Default;
            return OMFUtils.getOpenedDiagram().getOwner();


        if (propertyPathSRC.size() == 0) //Mother port to Son
            return propertyPathDST.get(0).getOwner();


        if (propertyPathDST.size() == 0) //Son to Mother Port
            return propertyPathSRC.get(0).getOwner();


        boolean isConnectingTwoSiblingsElements = propertyPathSRC.get(0).getOwner() == propertyPathDST.get(0).getOwner();
        if (isConnectingTwoSiblingsElements)
            return propertyPathSRC.get(0).getOwner();

        boolean isMotherToSon = propertyPathSRC.get(0).getType() == propertyPathDST.get(0).getOwner();
        if (isMotherToSon)
            return propertyPathSRC.get(0).getType();

        //isSonToMother
        return propertyPathDST.get(0).getType();
    }

    /**
     * Calculate nested path list.
     *
     * @param nestedPath     the nested path
     * @param currentPart    the current part
     * @param untilObject    the until object
     * @param availableParts the available parts
     * @return the list
     */
    public static List<Property> old_calculateNestedPath(List<Property> nestedPath, Property currentPart,
                                                         Class untilObject,
                                                         List<Property> availableParts) throws OMFException {
        if (untilObject.equals(currentPart.getOwner())) {
            nestedPath.add(currentPart);
            return nestedPath;
        } else if (untilObject.equals(currentPart.getType())) {
            return nestedPath;
        } else {
            nestedPath.add(currentPart);
            Element partOwner = currentPart.getOwner();
            Property nestedPart = availableParts.stream()
                    .filter(property -> partOwner.equals(property.getType()))
                    .findFirst()
                    .orElseThrow();

            if (!availableParts.contains(currentPart))
                throw new OMFException("[FullConnectionPath]-calculateNestedPath cannot find part: " + currentPart.getHumanName(),
                        GenericException.ECriticality.CRITICAL);

            return calculateNestedPath(nestedPath, nestedPart, untilObject, availableParts);
        }
    }

    public static List<Property> calculateNestedPath(List<Property> nestedPath, Property currentPart, Class untilObject, List<Property> availableParts) throws OMFException {

        if (untilObject.equals(currentPart.getOwner())) {
            nestedPath.add(currentPart);
            return nestedPath;
        } else if (untilObject.equals(currentPart.getType())) {
            return nestedPath;
        } else {
            nestedPath.add(currentPart);
            Element partOwner = currentPart.getOwner();

            Optional<Property> nestedPart = availableParts.stream().filter(property -> partOwner.equals((property).getType())).findFirst();
            if (nestedPart.isEmpty())
                throw new OMFException("[FullConnectionPath]-calculateNestedPath cannot find part: " + currentPart.getHumanName(), GenericException.ECriticality.CRITICAL);

            return calculateNestedPath(nestedPath, nestedPart.get(), untilObject, availableParts);

        }
    }


    /**
     * Gets all parts in context.
     *
     * @param currentElement       the current element
     * @param listAllPartInContext the list all part in context
     * @return the all parts in context
     */
    public static List<Property> getAllPartsInContext(Class currentElement, List<Property> listAllPartInContext) {
        if (null == listAllPartInContext)
            listAllPartInContext = new ArrayList();

        List<Property> l_properties = currentElement.getOwnedAttribute().stream()
                .filter(Profile.getSysmlAdditionalStereotypes().partProperty()::is)
                .filter(property -> Objects.nonNull(property.getType()))
                .filter(property -> Profile.getSysml().block().is(property.getType()))
                .collect(Collectors.toList());

        for (Property p : l_properties) {
            listAllPartInContext.add(p);
            getAllPartsInContext((Class) p.getType(), listAllPartInContext);
        }

        return listAllPartInContext;
    }


    public static ConnectableElement getHighestConnectableElementFromConnectorList(List<Connector> l_inConnector, Element commonAncestor, List<Property> listPropertyPath) {
        Optional<ConnectorEnd> optCE = l_inConnector.stream()
                .map(Connector::getEnd)
                .flatMap(Collection::stream)
                .filter(ce -> ce.getPartWithPort() == null)
                .filter(ce -> commonAncestor.getOwnedElement().contains(getPartInContext(Objects.requireNonNull(ce.getRole()).getOwner(), listPropertyPath)))
                .findFirst();

        if (optCE.isPresent()) {
            return optCE.get().getRole();
        } else {
            System.err.println("[PART FINDER] SRC Ancestor not found");
            return null;
        }
    }

    public static boolean isTypeOut(Type type) {
        return type.getOwnedElement().stream().filter(Property.class::isInstance).anyMatch(flow -> Objects.equals(Profile.getSysml().flowProperty().getDirection(flow), SysMLProfile.FlowDirectionKindEnum.OUT));
    }

    public static boolean isIn(Type type) {
        return type.getOwnedElement().stream().filter(Property.class::isInstance).anyMatch(flow -> Objects.equals(Profile.getSysml().flowProperty().getDirection(flow), SysMLProfile.FlowDirectionKindEnum.IN));
    }

    public static Property getPartFromPropertyPath(Element partType, List<Property> listPropertyPath) {
        Optional<Property> optPart = listPropertyPath.stream().filter(property -> partType.equals((property).getType())).findFirst();
        return optPart.orElse(null);
    }

    /**
     * Gets get Part In FA Context.
     *
     * @param partType the part type
     * @return the part
     */
    public static Property getPartInContext(Element partType, List<Property> availableParts) {
        Optional<Property> optPart = availableParts.stream().filter(property -> partType.equals((property).getType())).findFirst();
        return optPart.orElse(null);
    }

    /**
     * Gets get Part In FA Context.
     *
     * @param partType the part type
     * @return the part
     */
    public static Property getPartInContextWithID(Element partType, String id, List<Property> availableParts) {
        return availableParts.stream().filter(property -> partType.equals((property).getType()) && Profile.getSysml().block().is(property.getOwner())).iterator().next();
    }

    public static boolean areFlowPropertyDirectionCompatible(Property a, Property b, boolean isMotherToSon) {
        SysMLProfile.FlowDirectionKindEnum dirA = Profile.getSysml().flowProperty().getDirection(a);
        SysMLProfile.FlowDirectionKindEnum dirB = Profile.getSysml().flowProperty().getDirection(b);

        if (isMotherToSon) {
            return dirA == dirB;
        }

        if (dirA == dirB && dirA == SysMLProfile.FlowDirectionKindEnum.INOUT)
            return true;

        if (dirA == SysMLProfile.FlowDirectionKindEnum.IN && dirB == SysMLProfile.FlowDirectionKindEnum.OUT)
            return true;

        return dirA == SysMLProfile.FlowDirectionKindEnum.OUT && dirB == SysMLProfile.FlowDirectionKindEnum.IN;
    }

    /**
     * Utility method to parse csv with plugin versions.
     *
     * @return String the version of the plugin
     */
    public static String versionCsvReader() {
        ClassLoader classLoader = OMFUtils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("plugin_versions.csv");
        HashMap<String, String> versionPluginList = new HashMap<>();
        try (
                InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] fields = line.split(";");
                String name = fields[0];
                String version = fields[1];
                versionPluginList.put(name, version);
            }
        } catch (IOException e) {
            OMFErrorHandler.handleException(e, false);
        }
        if (versionPluginList != null && versionPluginList.containsKey("plugin_version")) {
            return versionPluginList.get("plugin_version");
        }
        return null;
    }

    public static List<Property> getPropertyPathListFromConnectorEnd(ConnectorEnd ce) {
        ConnectableElement end = ce.getRole();
        ArrayList<Element> elementPath = new ArrayList(Profile.getSysml().elementPropertyPath().getPropertyPath(ce));
        List<Property> propertyPath = OMFUtils.getPropertyListFromElementList(elementPath);
//

        if (!(end instanceof Port) && end instanceof Property)  //if end == part add it to the list
            propertyPath.add((Property) end);
        else if (null != ce.getPartWithPort() && !(ce.getPartWithPort() instanceof Port))   //could be redundant if end is a part
            propertyPath.add(ce.getPartWithPort());

        return new ArrayList<>(propertyPath);
    }

    public static class Version implements Comparable<Version> {

        private String version;

        public Version(String version) {
            if (version == null)
                throw new IllegalArgumentException("Version can not be null");
            if (!version.matches("[0-9]+(\\.[0-9]+)*"))
                throw new IllegalArgumentException("Invalid version format");
            this.version = version;
        }

        public final String get() {
            return this.version;
        }

        @Override
        public int compareTo(Version that) {
            if (that == null)
                return 1;
            String[] thisParts = this.get().split("\\.");
            String[] thatParts = that.get().split("\\.");
            int length = Math.max(thisParts.length, thatParts.length);
            for (int i = 0; i < length; i++) {
                int thisPart = i < thisParts.length ?
                        Integer.parseInt(thisParts[i]) : 0;
                int thatPart = i < thatParts.length ?
                        Integer.parseInt(thatParts[i]) : 0;
                if (thisPart < thatPart)
                    return -1;
                if (thisPart > thatPart)
                    return 1;
            }
            return 0;
        }

    }

    public static boolean compareStringsNoCaseNoSpace(String s1, String s2) {
        return StringUtils.deleteWhitespace(s1).equalsIgnoreCase(StringUtils.deleteWhitespace(s2));
    }

    public static List<Class> getStereotypeMetaClass(Stereotype str) {
        return StereotypesHelper.getBaseClasses(str);
    }

    public static boolean isStereotypeMetaClassMatchClass(Stereotype str, java.lang.Class clazz) {
        return getStereotypeMetaClass(str).stream()
                .anyMatch(metaClass -> metaClass == StereotypesHelper.getMetaClassByClass(OMFUtils.currentProject, clazz));
    }

    public static boolean isInstanceOfMetaClass(Element type, java.lang.Class metaClass) throws OMFException {
        try {
            if (type == null)
                return false;

            if (type instanceof Stereotype)
                return isStereotypeMetaClassMatchClass((Stereotype) type, metaClass);

            return type instanceof NamedElement && ((NamedElement) type).getName().equals(metaClass.getSimpleName());
        } catch (Exception e) {
            throw new OMFException("Impossible to determine MetaClass of selected type",
                    e, GenericException.ECriticality.CRITICAL);
        }
    }

}