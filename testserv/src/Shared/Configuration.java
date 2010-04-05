package Shared;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class ConfigurationErrorException extends Exception {

    /**
     *
     * @param msg
     */
    public ConfigurationErrorException(String msg) {
        super(msg);
    }

}

class Language {

    String name = null;
    Integer id = null;
    String src_prefix = null;
    String src_suffix = null;

    class CompilationInfo {

        int compile_output_file_descriptor = -1;
        String bin_suffix = null;
        String compile_cmd = null;
    };
    CompilationInfo cinfo = null;

    class ExecutionInfo {

        String execute_cmd = null;
        int run_time_output_file_descriptor = -1;
    }
    ExecutionInfo einfo = null;

    public Language(Node n) {
        {
            NamedNodeMap attrs = n.getAttributes();
            for (int j = 0; j < attrs.getLength(); ++j) {
                Node attr = attrs.item(j);
                if (attr.getNodeName().equals("name")) {
                    name = attr.getNodeValue();
                } else if (attr.getNodeName().equals("id")) {
                    id = new Integer(Integer.parseInt(attr.getNodeValue()));
                } else if (attr.getNodeName().equals("src_prefix")) {
                    src_prefix = attr.getNodeValue();
                } else if (attr.getNodeName().equals("src_suffix")) {
                    src_suffix = attr.getNodeValue();
                }
            }
        }
        NodeList childs = n.getChildNodes();
        for (int i = 0; i < childs.getLength(); ++i) {
            Node child = childs.item(i);
            if (child.getNodeName().equals("compile")) {
                cinfo = new CompilationInfo();
                NamedNodeMap attrs = child.getAttributes();
                for (int j = 0; j < attrs.getLength(); ++j) {
                    Node attr = attrs.item(j);
                    if (attr.getNodeName().equals("compile_output_file_descriptor")) {
                        cinfo.compile_output_file_descriptor = Integer.parseInt(attr.getNodeValue());
                    } else if (attr.getNodeName().equals("bin_suffix")) {
                        cinfo.bin_suffix = attr.getNodeValue();
                    } else if (attr.getNodeName().equals("compile_cmd")) {
                        cinfo.compile_cmd = attr.getNodeValue();
                    }
                }
            } else if (child.getNodeName().equals("execute")) {
                einfo = new ExecutionInfo();
                NamedNodeMap attrs = child.getAttributes();
                for (int j = 0; j < attrs.getLength(); ++j) {
                    Node attr = attrs.item(j);
                    if (attr.getNodeName().equals("run_time_output_file_descriptor")) {
                        einfo.run_time_output_file_descriptor = Integer.parseInt(attr.getNodeValue());
                    } else if (attr.getNodeName().equals("execute_cmd")) {
                        einfo.execute_cmd = attr.getNodeValue();
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return id + ": " + (name == null ? "null" : name) + "; "
                + (src_prefix == null ? "null" : src_prefix) + "*" + (src_suffix == null ? "null" : src_suffix) + "; "
                + "compile: "
                + (cinfo == null ? "null" : (cinfo.bin_suffix == null ? "null" : cinfo.bin_suffix)) + "; "
                + (cinfo == null ? "null" : (cinfo.compile_cmd == null ? "null" : cinfo.compile_cmd)) + "; "
                + "fd " + (cinfo == null ? "null" : cinfo.compile_output_file_descriptor) + "; "
                + "execute: "
                + (einfo == null ? "null" : (einfo.execute_cmd == null ? "null" : einfo.execute_cmd)) + "; "
                + "fd " + (einfo == null ? "null" : einfo.run_time_output_file_descriptor);
    }
}

class Platform {

    HashMap<Integer, Language> languages;
    String tests;
    String tmp;
    String os;

    public Platform(Node n) {
        languages = new HashMap<Integer, Language>();
        {
            NamedNodeMap attrs = n.getAttributes();
            for (int j = 0; j < attrs.getLength(); ++j) {
                Node attr = attrs.item(j);
                if (attr.getNodeName().equals("os")) {
                    os = attr.getNodeValue();
                }
            }
        }
        NodeList children = n.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            if (children.item(i).getNodeName().equals("pathes")) {
                Node path = children.item(i);
                NamedNodeMap attrs = path.getAttributes();
                for (int j = 0; j < attrs.getLength(); ++j) {
                    Node attr = attrs.item(j);
                    if (attr.getNodeName().equals("tmp")) {
                        tmp = attr.getNodeValue();
                    } else if (attr.getNodeName().equals("tests")) {
                        tests = attr.getNodeValue();
                    }
                }
            } else if (children.item(i).getNodeName().equals("language")) {
                Language lang = new Language(children.item(i));
                languages.put(lang.id, lang);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("tmp: " + (tmp == null ? "null" : tmp) + "; tests: " + (tests == null ? "null" : tests) + "\n");
        Iterator<Language> iterator = languages.values().iterator();
        while (iterator.hasNext()) {
            Language lang = iterator.next();
            sb.append("\t" + lang.toString() + "\n");
        }
        return sb.toString();
    }
}

/**
 *
 * @author Анна
 */
public class Configuration {

    private static Document config = null;
    private static HashMap<String, Platform> platforms;
    private static String osname;
    private static String url, user, password;

    static {
        osname = System.getProperty("os.name");
    }

    private static void checkPlatform() throws ConfigurationErrorException {
        if (platforms.get(osname) == null) {
            throw new ConfigurationErrorException("There is no configuration for operating system " + osname);
        } else {
            Platform platform = platforms.get(osname);
            if (platform.tmp == null) {
                throw new ConfigurationErrorException("Temp directory name is not defined");
            }
            if (platform.tests == null) {
                throw new ConfigurationErrorException("Tests directory name is not defined");
            }
            if (platform.languages.size() == 0) {
                throw new ConfigurationErrorException("No one language is defined");
            }
            Iterator<Language> iterator = platforms.get(osname).languages.values().iterator();
            while (iterator.hasNext()) {
                Language language = iterator.next();
                if (language.id == null) {
                    throw new ConfigurationErrorException("Language id is not defined");
                }
                if (language.src_suffix == null) {
                    throw new ConfigurationErrorException("Source suffix is not defined for language " + language.id);
                }
                if (language.src_prefix == null) {
                    throw new ConfigurationErrorException("Source prefix is not defined for language " + language.id);
                }
                if (language.cinfo != null) {
                    if (language.cinfo.compile_output_file_descriptor != 1 && language.cinfo.compile_output_file_descriptor != 2) {
                        throw new ConfigurationErrorException("Compilation output file descriptor value is not correct for language "
                                + language.id + ". Must have value 1 (standard output) or 2 (error output)");
                    }
                    if (language.cinfo.bin_suffix == null) {
                        throw new ConfigurationErrorException("Binary suffix is not defined for language " + language.id);
                    }
                    if (language.cinfo.compile_cmd == null) {
                        throw new ConfigurationErrorException("Compilation command is not defined for language " + language.id);
                    }
                }
                if (language.einfo == null) {
                    throw new ConfigurationErrorException("Execution information is not defined for language " + language.id);
                }
                if (language.einfo.run_time_output_file_descriptor != 1 && language.einfo.run_time_output_file_descriptor != 2) {
                    throw new ConfigurationErrorException("Run time output file descriptor value is not correct for language "
                            + language.id + ". Must have value 1 (standard output) or 2 (error output)");
                }
                if (language.einfo.execute_cmd == null) {
                    throw new ConfigurationErrorException("Execution command is not defined for language " + language.id);
                }
            }
        }
    }

    /**
     *
     * @param filename
     * @return
     */
    public static int loadFromFile(String filename) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder(); // ParserConfigurationException
            config = db.parse(filename); // SAXException, IOException
            platforms = new HashMap<String, Platform>();
            parse(config.getDocumentElement());
            checkPlatform(); // ConfigurationErrorException
            return 0;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ConfigurationErrorException e) {
            System.err.println(e.getMessage());
        }
        return 1;
    }

    private static void parse(Node node) throws ConfigurationErrorException {
        int type = node.getNodeType();
        switch (type) {
            case Node.DOCUMENT_NODE: {
                parse(((Document) node).getDocumentElement());
                break;
            }
            case Node.ELEMENT_NODE: {
                if (node.getNodeName().equals("general")) {
                    NamedNodeMap attributes = node.getAttributes();
                    for (int i = 0; i < attributes.getLength(); ++i) {
                        if (attributes.item(i).getNodeName().equals("url")) {
                            url = attributes.item(i).getNodeValue();
                        } else if (attributes.item(i).getNodeName().equals("user")) {
                            user = attributes.item(i).getNodeValue();
                        } else if (attributes.item(i).getNodeName().equals("password")) {
                            password = attributes.item(i).getNodeValue();
                        }
                    }
                } else if (node.getNodeName().equals("platform")) {
                    Platform pl = new Platform(node);
                    platforms.put(pl.os, pl);
                } else if (node.hasChildNodes()) {
                    NodeList children = node.getChildNodes();
                    for (int i = 0; i < children.getLength(); ++i) {
                        parse(children.item(i));
                    }
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
                printDomTree(((Document) node).getDocumentElement());
                break;
            }
            case Node.ELEMENT_NODE: {
                System.out.print("<");
                System.out.print(node.getNodeName());
                NamedNodeMap attrs = node.getAttributes();
                for (int i = 0; i < attrs.getLength(); i++) {
                    printDomTree(attrs.item(i));
                }
                System.out.print(">");
                if (node.hasChildNodes()) {
                    NodeList children = node.getChildNodes();
                    for (int i = 0; i < children.getLength(); i++) {
                        printDomTree(children.item(i));
                    }
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

    /**
     *
     * @return
     */
    public static String getTestsDir() {
        return platforms.get(osname).tests;
    }

    /**
     *
     * @return
     */
    public static String getTmpDir() {
        return platforms.get(osname).tmp;
    }

    /**
     *
     * @return
     */
    public static String getURL() {
        return url;
    }

    /**
     *
     * @return
     */
    public static String getUser() {
        return user;
    }

    /**
     *
     * @return
     */
    public static String getPassword() {
        return password;
    }

    /**
     *
     * @param langId
     * @return
     */
    public static String getSourceSuffix(int langId) {
        return platforms.get(osname).languages.get(new Integer(langId)).src_suffix;
    }

    /**
     *
     * @param langId
     * @return
     */
    public static String getSourcePrefix(int langId) {
        return platforms.get(osname).languages.get(new Integer(langId)).src_prefix;
    }

    /**
     *
     * @param langId
     * @return
     */
    public static boolean isCompiled(int langId) {
        return platforms.get(osname).languages.get(new Integer(langId)).cinfo != null;
    }

    /**
     * call isCompiled() before
     * @param langId
     * @return
     */
    public static int getCompileOutputFileDescriptor(int langId) {
        return platforms.get(osname).languages.get(new Integer(langId)).cinfo.compile_output_file_descriptor;
    }

    /**
     * call isCompiled() before
     * @param langId
     * @return
     */
    public static String getBinarySuffix(int langId) {
        return platforms.get(osname).languages.get(new Integer(langId)).cinfo.bin_suffix;
    }

    /**
     * call isCompiled() before
     * @param langId
     * @return
     */
    public static String[] getCompilerCommand(int langId) {
        return platforms.get(osname).languages.get(new Integer(langId)).cinfo.compile_cmd.split(",");
    }

    /**
     *
     * @param langId
     * @return
     */
    public static int getRunTimeOutputDescriptor(int langId) {
        return platforms.get(osname).languages.get(new Integer(langId)).einfo.run_time_output_file_descriptor;
    }

    /**
     *
     * @param langId
     * @return
     */
    public static String[] getExecuteCommand(int langId) {
        return platforms.get(osname).languages.get(new Integer(langId)).einfo.execute_cmd.split(",");
    }

    /**
     *
     * @return
     */
    public static Integer[] getLangs() {
        return platforms.get(osname).languages.keySet().toArray(new Integer[0]);
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(osname);
        int res = loadFromFile("testserv.cfg.xml");
        if (res == 0) {
            printDomTree(config);
            System.out.println();
            Iterator<Entry<String, Platform>> iterator = platforms.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Platform> entry = iterator.next();
                System.out.println(entry.getKey());
                System.out.println(entry.getValue().toString());
            }
        } else {
            System.err.println("Error");
        }
    }
}
