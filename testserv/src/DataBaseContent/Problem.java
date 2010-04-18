package DataBaseContent;

import DataBaseContent.Generic.DataElement;
import Shared.Configuration;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Класс "Задача по программированию".
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
     * Количество тестов.
     */
    public int n = 0;
    /**
     * Массив строк - названия файлов со входными данными.
     */
    public String[] in = null;
    /**
     * Массив строк - названия файлов с выходными данными.
     */
    public String[] out = null;

    /**
     * Конструктор класса.
     *
     * @param id код задачи
     * @param description постановка задачи
     * @param restrictions ограничения
     * @param samples примеры
     * @param name имя задачи
     * @param testsdir название директории с тестами
     * @param solutions массив кодов эталонных решений
     * @param igenerators массив кодов генераторов входа
     * @param ioparams коды входных/выходных параметров
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
     * Возвращает путь к директории с тестами.
     *
     * @return
     */
    public String getPathToTests() {
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
