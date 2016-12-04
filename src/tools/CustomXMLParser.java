package tools;

import gui.Gui;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tester.Test;
import tester.TestSpec;
import tester.Tester;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petec on 19.11.2016.
 */
public class CustomXMLParser {
    private Gui gui;
    TestSpec testSpec;

    public CustomXMLParser(Gui gui) {
        this.gui = gui;
    }

    public void parseXML(File inputFile){
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            Element root = doc.getDocumentElement();
            root.normalize();
            NodeList rootList = root.getElementsByTagName("*");
            for (int i = 0; i < rootList.getLength(); i++){
                Element element = (Element) rootList.item(i);
                switch (rootList.item(i).getNodeName()){
                    case "libs":
                        processLibs(element);
                        break;
                    case "solution":
                        processSolution(element);
                        break;
                    case "output":
                        processOutput(element);
                        break;
                    case "du":
                        processDu(element);
                        break;
                    case "testfiles":
                        processTestFiles(element);
                        break;
                    default:
                        System.out.println("Ignoring element: " + rootList.item(i).getNodeName());
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processVariables(Element element, String className) {
        String testElement = element.getNodeName();
        List<TestSpec> specList = new ArrayList<>();

        testSpec = createTestSpec(element, "count");
        if(testSpec != null){
            specList.add(testSpec);
        }

        testSpec = createTestSpec(element, "type");
        if(testSpec != null){
            specList.add(testSpec);
        }

        Test test = new Test(className, testElement, specList);
        Tester.getInstance().getTests().add(test);
    }

    private TestSpec createTestSpec(Element element, String testSpecification){
        String testValue = element.getAttribute(testSpecification);
        if(!testValue.isEmpty()){
            return new TestSpec(testSpecification, testValue);
        }
        return null;
    }

    private void processTestFiles(Element element) {
        String path = element.getAttribute("path");
        gui.setTestFiles(path);
    }

    private void processClass(Element element, String className) {
        String testElement = element.getNodeName();
        List<TestSpec> specList = new ArrayList<>();

        testSpec = createTestSpec(element, "nameCondition");
        if(testSpec != null){
            specList.add(testSpec);
        }

        Test test = new Test(className, testElement, specList);
        Tester.getInstance().getTests().add(test);
    }

    private void processDu(Element element) {
        String duFile = element.getAttribute("file");
        if(!duFile.isEmpty()){
            NodeList childList = element.getElementsByTagName("class");
            for (int i = 0; i < childList.getLength(); i++){
                Element childEl = (Element) childList.item(i);
                processClass(childEl, duFile);
            }
            childList = element.getElementsByTagName("variables");
            for (int i = 0; i < childList.getLength(); i++){
                Element childEl = (Element) childList.item(i);
                processVariables(childEl, duFile);
            }
        }else{
            System.out.println("Error in XML file.");
        }
    }

    private void processOutput(Element element) {
        gui.getCompOutLabel().setText(element.getAttribute("path"));
    }

    private void processSolution(Element element) {
        gui.getSolutionDirLabel().setText(element.getAttribute("path"));
    }

    private void processLibs(Element element) {
        gui.getLibsDirLabel().setText(element.getAttribute("path"));
    }
}
