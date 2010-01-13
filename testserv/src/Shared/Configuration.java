/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Shared;

import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

class Compiler {
    String lang;
    int id;
    String cmd;
    int FD;
    String ext;

    public Compiler(Node n) {
        if (!n.getNodeName().equals("compiler"))
            return ;
        NamedNodeMap attrs = n.getAttributes();
        for (int j=0; j<attrs.getLength(); ++j) {
            Node attr = attrs.item(j);
            if (attr.getNodeName().equals("lang"))
                lang = attr.getNodeValue();
            else if (attr.getNodeName().equals("id"))
                id = Integer.parseInt(attr.getNodeValue());
            else if (attr.getNodeName().equals("cmd"))
                cmd = attr.getNodeValue();
            else if (attr.getNodeName().equals("outputFD"))
                FD = Integer.parseInt(attr.getNodeValue());
            else if (attr.getNodeName().equals("ext")) {
                ext = attr.getNodeValue();
                //if (ext == null) ext = "";
            }
        }
    }
    @Override
    public String toString() {
        return id + ": " + lang + " ; " + FD + " ; " + cmd;
    }
}

class Platform {
    HashMap<Integer, Compiler> compilers;
    String tests;
    String tmp;
    String extension;
    String program_run;
    String os;

    public Platform(Node n) {
        compilers = new HashMap<Integer, Compiler>();
        if (!n.getNodeName().equals("platform"))
            return ;
        {
            NamedNodeMap attrs = n.getAttributes();
            for (int j=0; j<attrs.getLength(); ++j) {
                Node attr = attrs.item(j);
                if (attr.getNodeName().equals("os"))
                os = attr.getNodeValue();
            }
        }
        NodeList children = n.getChildNodes();
        for (int i=0; i<children.getLength(); ++i) {
            if (children.item(i).getNodeName().equals("paths")) {
                Node path = children.item(i);
                NamedNodeMap attrs = path.getAttributes();
                for (int j=0; j<attrs.getLength(); ++j) {
                    Node attr = attrs.item(j);
                    if (attr.getNodeName().equals("tmp"))
                        tmp = attr.getNodeValue();
                    else if (attr.getNodeName().equals("tests"))
                        tests = attr.getNodeValue();
                    else if (attr.getNodeName().equals("extension")) {
                        extension = attr.getNodeValue();
                        //if (extension == null) extension = "";
                    } else if (attr.getNodeName().equals("program_run"))
                        program_run = attr.getNodeValue();
                }
            }
            if (children.item(i).getNodeName().equals("compiler")) {
                Compiler cmpl = new Compiler(children.item(i));
                compilers.put(new Integer(cmpl.id), cmpl);
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("tmp: " + tmp + "; tests: " + tests
            + "; extension: " + extension + "; program_run: " + program_run + "\n");
        for (int i=0; i<compilers.size(); ++i)
            sb.append("\t" + compilers.get(new Integer(i)).toString() + "\n");
        return sb.toString();
    }
}

public class Configuration {
    private static Document config = null;
    private static HashMap<String, Platform> platforms;
    private static String osname;
    static {osname=System.getProperty("os.name");}

    public static int loadFromFile(String filename) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
	try {
            db = dbf.newDocumentBuilder(); // ParserConfigurationException
            config = db.parse(filename); // SAXException, IOException
            platforms = new HashMap<String, Platform>();
            parse(config.getDocumentElement());
            return 0;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private static void parse(Node node) {
        int type = node.getNodeType();
        switch (type) {
            case Node.DOCUMENT_NODE: {
                parse(((Document)node).getDocumentElement());
                break;
            }
            case Node.ELEMENT_NODE: {
                if (node.getNodeName().equals("platform")) {
                Platform pl = new Platform(node);
                platforms.put(pl.os, pl);
                } else if (node.hasChildNodes()) {
                    NodeList children = node.getChildNodes();
                    for (int i=0; i<children.getLength(); ++i)
                        parse(children.item(i));
                }
                break;
            }
        }
    }

    private static void printDomTree(Node node) {
        int type = node.getNodeType();
        switch (type) {
            case Node.DOCUMENT_NODE: {
                System.out.println("<?xml version=\"1.0\" ?>");
                printDomTree(((Document)node).getDocumentElement());
                break;
            }
            case Node.ELEMENT_NODE: {
                System.out.print("<");
                System.out.print(node.getNodeName());
                NamedNodeMap attrs = node.getAttributes();
                for (int i = 0; i < attrs.getLength(); i++)
                    printDomTree(attrs.item(i));
                System.out.print(">");
                if (node.hasChildNodes()) {
                NodeList children = node.getChildNodes();
                for (int i = 0; i < children.getLength(); i++)
                    printDomTree(children.item(i));
                }
                System.out.print("</");
                System.out.print(node.getNodeName());
                System.out.print('>');
                break;
            }
            case Node.ATTRIBUTE_NODE: {
                System.out.print(" " + node.getNodeName() + "=\"" + ((Attr) node).getValue() + "\"");
                break;
            }
            case Node.TEXT_NODE: {
                System.out.print(node.getNodeValue());
                break;
            }
        }
    }

    public static String getTestsDir() {
        return platforms.get(osname).tests;
    }

    public static String getTmpDir() {
        return platforms.get(osname).tmp;
    }

    public static String[] getProgramCommand() {
        return platforms.get(osname).program_run.split(",");
    }

    public static String[] getCompilerCommand(int lang) {
        return platforms.get(osname).compilers.get(new Integer(lang)).cmd.split(",");
    }

    public static String getExtension() {
        return platforms.get(osname).extension;
    }

    public static String getSuffix(int lang) {
        return platforms.get(osname).compilers.get(new Integer(lang)).ext;
    }

    public static Integer[] getLangs() {
        return platforms.get(osname).compilers.keySet().toArray(new Integer[0]);
    }

    public static int getOutputFD(int lang) {
        return platforms.get(osname).compilers.get(new Integer(lang)).FD;
    }

    public static void main(String[] args) {
        System.err.println(osname);
        int res = loadFromFile("testserv.cfg.xml");
        if (res==0) printDomTree(config);
        else System.err.println("Error");
    }
}
