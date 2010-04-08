package DataBaseContent;

import DataBaseContent.Generic.DataElement;
import Shared.Configuration;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author partizanka
 */
public class Problem extends DataElement {

    private String description = "";
    private String restrictions = "";
    private String samples = "";
    private String name = "";
    private String testsdir = "";
    private ArrayList<Integer> solutions = null;
    private ArrayList<Integer> igenerators = null;
    private HashMap<Integer, Integer> ioparams = null;
    /**
     *
     */
    public int n = 0;
    /**
     *
     */
    /**
     *
     */
    public String[] in = null, out = null;

    /**
     *
     * @param id
     * @param description
     * @param restrictions
     * @param samples
     * @param name
     * @param testsdir
     * @param solutions
     * @param igenerators
     * @param ioparams
     */
    public Problem(int id, String description, String restrictions, String samples,
            String name, String testsdir,
            ArrayList<Integer> solutions,
            ArrayList<Integer> igenerators,
            HashMap<Integer, Integer> ioparams) {
        this.id = id;
        this.description = description;
        this.restrictions = restrictions;
        this.samples = samples;
        this.name = name;
        this.testsdir = testsdir;
        this.solutions = solutions;
        this.igenerators = igenerators;
        this.ioparams = ioparams;
        readTestsDir(Configuration.getTestsDir() + "/" + this.testsdir);
    }

    /**
     * 
     * @return
     */
    public String getAbsPathToTests() {
        return Configuration.getTestsDir() + "/" + this.testsdir;
    }

    class inFilenameFilter implements FilenameFilter {

        public boolean accept(File f, String name) {
            return (name.startsWith("in") && !name.endsWith("~"));
        }
    }

    class outFilenameFilter implements FilenameFilter {

        public boolean accept(File f, String name) {
            return (name.startsWith("out") && !name.endsWith("~"));
        }
    }

    private void readTestsDir(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            in = dir.list(new inFilenameFilter());
            out = dir.list(new outFilenameFilter());
            if (in != null && out != null) {
                Arrays.sort(in);
                Arrays.sort(out);
                n = Math.min(in.length, out.length);
            }
        }
    }
}
