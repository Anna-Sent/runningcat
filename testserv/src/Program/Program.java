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
    private String srcPath, dirPath, binPath;

    public Program(int lang, String text, Problem problem) {
        this.lang = lang;
        this.text = text != null ? text : "";
        this.problem = problem;
    }

    public void prepare() throws CanNotCreateTemporaryDirectoryException, CanNotCreateTemporaryFileException, CanNotWriteFileException {
        dirPath = FileOperator.createDirectory("tmpdir", "solution", Configuration.getTmpDir());
        srcPath = FileOperator.createFile(Configuration.getSourcePrefix(lang), Configuration.getSourceSuffix(lang), dirPath);
        if (Configuration.getBinarySuffix(lang) != null) {
            binPath = srcPath.replace(Configuration.getSourceSuffix(lang), Configuration.getBinarySuffix(lang));
        }
        FileOperator.writeFile(srcPath, text);
    }

    public void close() {
        if (dirPath.compareTo("") != 0 && (new File(dirPath)).exists()) {
            FileOperator.deleteDir(dirPath);
        }
    }

    public boolean canExecute() {
        if (getBinPath() != null) {
            File executable = new File(getBinPath());
            return executable.exists() /*&& executable.canExecute()*/;
        } else {
            return true;
        }
    }

    private String[] getCmd(String[] cmd) {
        if (cmd != null) {
            for (int i = 0; i < cmd.length; ++i) {
                if (getSrcFileName() != null) {
                    cmd[i] = cmd[i].replace("<src_file>", getSrcFileName());
                }
                if (getSrcPath() != null) {
                    cmd[i] = cmd[i].replace("<src_path>", getSrcPath());
                }
                if (getBinFileName() != null) {
                    cmd[i] = cmd[i].replace("<bin_file>", getBinFileName());
                }
                if (getBinPath() != null) {
                    cmd[i] = cmd[i].replace("<bin_path>", getBinPath());
                }
                if (Configuration.getSourcePrefix(lang) != null) {
                    cmd[i] = cmd[i].replace("<src_prefix>", Configuration.getSourcePrefix(lang));
                }
            }
        }
        return cmd;
    }

    public String[] getExecuteCmd() {
        return getCmd(Configuration.getExecuteCommand(lang));
    }

    public String[] getCompileCmd() {
        return getCmd(Configuration.getCompilerCommand(lang));
    }

    public String getBinPath() {
        return binPath;
    }

    public String getBinFileName() {
        return binPath != null ? (new File(binPath)).getName() : null;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public String getSrcFileName() {
        return srcPath != null ? (new File(srcPath)).getName() : null;
    }

    public String getDirPath() {
        return dirPath;
    }
}
