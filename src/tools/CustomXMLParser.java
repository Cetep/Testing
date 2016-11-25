package tools;

import gui.Gui;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tester.Test;
import tester.Tester;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Petec on 19.11.2016.
 */
public class CustomXMLParser {
    private Gui gui;

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
                switch (rootList.item(i).getNodeName()){
                    case "libs":
                        processLibs((Element) rootList.item(i));
                        break;
                    case "solution":
                        processSolution((Element) rootList.item(i));
                        break;
                    case "output":
                        processOutput((Element) rootList.item(i));
                        break;
                    case "du":
                        processDu((Element) rootList.item(i));
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

    private void processValidateClass(Element element, String className) {
        String nameCond = element.getAttribute("nameCondition");
        if(!nameCond.isEmpty()){
            String nodeName = element.getNodeName();
            Tester.getInstance().getTests().add(new Test(className, nodeName, "nameCondition", nameCond));
        }
    }

    private void processDu(Element element) {
        String duFile = "";
        duFile = element.getAttribute("file");
        if(!duFile.isEmpty()){
            NodeList childList = element.getElementsByTagName("class");
            for (int i = 0; i < childList.getLength(); i++){
                Element childEl = (Element) childList.item(i);
                processValidateClass(childEl, duFile);
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
