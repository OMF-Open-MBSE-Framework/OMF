/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.formatter;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.SysoutColorPrinter;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.junit.*;
import org.apache.tools.ant.util.DOMElementWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Based on source from junit ant task. We modified the class to generate the XML in the format expected by the
 * gradle plugin we use to generate a human-readable test report.
 */
public class XMLJUnitResultFormatter implements JUnitResultFormatter, XMLConstants {
    private static final String UNKNOWN = "unknown";
    private Document doc;
    private Element rootElement;
    private Hashtable testElements = new Hashtable();
    private Hashtable failedTests = new Hashtable();
    private Hashtable testStarts = new Hashtable();
    private OutputStream out;
    private Element currentTest;

    private static DocumentBuilder getDocumentBuilder() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            return factory.newDocumentBuilder();
        } catch (Exception var1) {
            throw new ExceptionInInitializerError(var1);
        }
    }

    public XMLJUnitResultFormatter() {
    }

    public void setOutput(OutputStream out) {
        this.out = out;
    }

    public void setSystemOutput(String out) {
        this.formatOutput("system-out", out);
    }

    public void setSystemError(String out) {
        this.formatOutput("system-err", out);
    }

    public void startTestSuite(JUnitTest suite) {
        this.doc = getDocumentBuilder().newDocument();
        this.rootElement = this.doc.createElement("testsuite");
        String n = suite.getName();
        this.rootElement.setAttribute("name", n == null ? "unknown" : n);
        Element propsElement = this.doc.createElement("properties");
        this.rootElement.appendChild(propsElement);
        Properties props = suite.getProperties();
        if (props != null) {
            Enumeration e = props.propertyNames();

            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                Element propElement = this.doc.createElement("property");
                propElement.setAttribute("name", name);
                propElement.setAttribute("value", props.getProperty(name));
                propsElement.appendChild(propElement);
            }
        }

    }

    public void endTestSuite(JUnitTest suite) throws BuildException {
        this.rootElement.setAttribute("tests", "" + suite.runCount());
        this.rootElement.setAttribute("failures", "" + suite.failureCount());
        this.rootElement.setAttribute("errors", "" + suite.errorCount());
        this.rootElement.setAttribute("time", "" + (double) suite.getRunTime() / 1000.0);
        if (this.out != null) {
            Writer wri = null;

            try {
                wri = new BufferedWriter(new OutputStreamWriter(this.out, "UTF8"));
                wri.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
                (new DOMElementWriter()).write(this.rootElement, wri, 0, "  ");
                wri.flush();
            } catch (IOException var11) {
                throw new BuildException("Unable to write log file", var11);
            } finally {
                if (this.out != System.out && this.out != System.err && wri != null) {
                    try {
                        wri.close();
                    } catch (IOException var10) {
                        SysoutColorPrinter.err("Unable to close log file: " + var10.getMessage()); //TODO: replace with OMFErrorHandler
                    }
                }

            }
        }

    }

    public void startTest(Test t) {
        this.testStarts.put(t, new Long(System.currentTimeMillis()));
    }

    public void endTest(Test test) {
        if (!this.testStarts.containsKey(test)) {
            this.startTest(test);
        }

        currentTest = null;
        if (!this.failedTests.containsKey(test)) {
            currentTest = this.doc.createElement("testcase");
            String n = JUnitVersionHelper.getTestCaseName(test);
            currentTest.setAttribute("name", n == null ? "unknown" : n);
            currentTest.setAttribute("classname", test.getClass().getName());
            this.rootElement.appendChild(currentTest);
            this.testElements.put(test, currentTest);
        } else {
            currentTest = (Element) this.testElements.get(test);
        }

        Long l = (Long) this.testStarts.get(test);
        currentTest.setAttribute("time", "" + (double) (System.currentTimeMillis() - l) / 1000.0);
    }

    public void addFailure(Test test, Throwable t) {
        this.formatError("failure", test, t);
    }

    public void addFailure(Test test, AssertionFailedError t) {
        this.addFailure(test, (Throwable) t);
    }

    public void addError(Test test, Throwable t) {
        this.formatError("error", test, t);
    }

    private void formatError(String type, Test test, Throwable t) {
        if (test != null) {
            this.endTest(test);
            this.failedTests.put(test, test);
        }

        Element nested = this.doc.createElement(type);
        Element currentTest = null;
        if (test != null) {
            currentTest = (Element) this.testElements.get(test);
        } else {
            currentTest = this.rootElement;
        }

        currentTest.appendChild(nested);
        String message = t.getMessage();
        if (message != null && message.length() > 0) {
            nested.setAttribute("message", t.getMessage());
        }

        nested.setAttribute("type", t.getClass().getName());
        String strace = JUnitTestRunner.getFilteredTrace(t);
        Text trace = this.doc.createTextNode(strace);
        nested.appendChild(trace);
    }

    private void formatOutput(String type, String output) {
        formatOutputForElement(this.rootElement, type, output);
        formatOutputForElement(this.currentTest, type, output);
    }

    private void formatOutputForElement(Element elem, String type, String output) {
        Element nested = this.doc.createElement(type);
        elem.appendChild(nested);
        nested.appendChild(this.doc.createCDATASection(output));
    }
}
