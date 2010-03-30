package Program;

import DataBaseContent.Problem;
import FileOperations.Exceptions.CanNotCreateTemporaryDirectoryException;
import FileOperations.Exceptions.CanNotCreateTemporaryFileException;
import FileOperations.Exceptions.CanNotWriteFileException;
import FileOperations.FileOperator;
import Shared.Configuration;
import java.io.File;

/**
 *
 * @author partizanka
 */
public class Program {
    public final int lang;
    public final String text;
    public final Problem problem;
    private String srcPath="", dirPath="", binPath="";
    public Program(int lang, String text, Problem problem) {
        this.lang = lang;
        this.text = text!=null?text:"";
        this.problem = problem;
    }
    public void prepare() throws CanNotCreateTemporaryDirectoryException, CanNotCreateTemporaryFileException, CanNotWriteFileException {
        dirPath = FileOperator.createDirectory("tmpdir", "solution", Configuration.getTmpDir());
        srcPath = FileOperator.createFile(Configuration.getSourcePrefix(lang), Configuration.getSourceSuffix(lang), dirPath);
        binPath = srcPath.replace(Configuration.getSourceSuffix(lang), Configuration.getBinarySuffix(lang));
        FileOperator.writeFile(srcPath, text);
    }
    public void close() {
        if (dirPath.compareTo("") != 0 && (new File(dirPath)).exists()) {
            FileOperator.deleteDir(dirPath);
        }
    }
    public boolean canExecute() {
        File executable = new File(getBinPath());
        return executable.exists() /*&& executable.canExecute()*/;
    }
    public String[] getCmd() {
        String[] cmd = Configuration.getExecuteCommand(lang);
        if (cmd!=null)
            for (int i=0;i<cmd.length;++i) {
                cmd[i] = cmd[i].replace("<bin>", getBinFileName());// getBinPath());
            }
        cmd[cmd.length-1] = cmd[cmd.length-1].replace(".class", "");
        for(int i=0;i<cmd.length;++i) System.err.println(cmd[i]);
        return cmd;
    }
    public String getBinPath() {
        return binPath;
    }
    public String getBinFileName() {
        return (new File(getBinPath())).getName();
    }
    public String getSrcPath() {
        return srcPath;
    }
    public String getSrcFileName() {
        return (new File(getSrcPath())).getName();
    }
    public String getDirPath() {
        return dirPath;
    }
}
