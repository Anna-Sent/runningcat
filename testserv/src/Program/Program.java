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

    /**
     *
     */
    public final int lang;
    /**
     *
     */
    public final String text;
    /**
     *
     */
    public final Problem problem;
    private String srcPath, dirPath, binPath;

    /**
     *
     * @param lang
     * @param text
     * @param problem
     */
    public Program(int lang, String text, Problem problem) {
        this.lang = lang;
        this.text = text != null ? text : "";
        this.problem = problem;
    }

    /**
     * Создает временную директорию, куда складываются все необходимые файлы:
     * 1) сначала файл с исходным кодом, 2) после компиляции бинарный файл,
     * объектные и другие временные файлы.
     * Инициализируются переменные dirPath, srcPath, binPath.
     *
     * @throws CanNotCreateTemporaryDirectoryException ошибка при создании
     * временной директории (может возникнуть, например, при отсутствии прав на
     * запись)
     * @throws CanNotCreateTemporaryFileException ошибка при создании файла с
     * исходным кодом
     * @throws CanNotWriteFileException ошибка при попытке записи в файл,
     * подготовленный для исходного кода
     */
    public void prepare() throws CanNotCreateTemporaryDirectoryException, CanNotCreateTemporaryFileException, CanNotWriteFileException {
        dirPath = FileOperator.createDirectory("tmpdir", "solution", Configuration.getTmpDir());
        srcPath = FileOperator.createFile(Configuration.getSourcePrefix(lang), Configuration.getSourceSuffix(lang), dirPath);
        if (Configuration.getBinarySuffix(lang) != null) {
            binPath = srcPath.replace(Configuration.getSourceSuffix(lang), Configuration.getBinarySuffix(lang));
        }
        FileOperator.writeFile(srcPath, text);
    }

    /**
     * Метод необходимо вызывать после prepare() для удаления временной директории.
     */
    public void close() {
        if (dirPath.compareTo("") != 0 && (new File(dirPath)).exists()) {
            FileOperator.deleteDir(dirPath);
        }
    }

    /**
     * Метод стоит вызывать после компиляции программы.
     *
     * @return возвращает true, если бинарный файл существует, false - в
     * противном случае
     */
    public boolean canExecute() {
        File executable = new File(getBinPath());
        return executable.exists();
    }

    private String[] getCmd(String[] cmd) {
        if (cmd != null) {
            for (int i = 0; i < cmd.length; ++i) {
                if (getSrcFileName() != null) {
                    cmd[i] = cmd[i].replace("{src_file}", getSrcFileName());
                }
                if (getSrcPath() != null) {
                    cmd[i] = cmd[i].replace("{src_path}", getSrcPath());
                }
                if (getBinFileName() != null) {
                    cmd[i] = cmd[i].replace("{bin_file}", getBinFileName());
                }
                if (getBinPath() != null) {
                    cmd[i] = cmd[i].replace("{bin_path}", getBinPath());
                }
                if (Configuration.getSourcePrefix(lang) != null) {
                    cmd[i] = cmd[i].replace("{src_prefix}", Configuration.getSourcePrefix(lang));
                }
            }
        }
        return cmd;
    }

    /**
     * Метод возвращает команду с параметрами для исполнения программы.
     *
     * @return возвращает массив строк - команду и параметры для выполнения
     * программы
     */
    public String[] getExecuteCmd() {
        return getCmd(Configuration.getExecuteCommand(lang));
    }

    /**
     * Метод возвращает команду с параметрами для компиляции программы.
     *
     * @return возвращает массив строк - команду и параметры для компиляции
     * программы
     */
    public String[] getCompileCmd() {
        return getCmd(Configuration.getCompilerCommand(lang));
    }

    /**
     * Метод возвращает путь к исполняемому (бинарному) файлу с программой.
     * Метод стоит вызывать после prepare(). Файл будет существовать в случае
     * удачной компиляции программы.
     *
     * @return возвращает строку - путь к исполняемому (бинарному) файлу с
     * программой
     */
    public String getBinPath() {
        return binPath;
    }

    /**
     * Метод возвращает имя исполняемого (бинарного) файла с программой.
     * Стоит вызывать после prepare(). Файл будет существовать в случае удачной
     * компиляции программы. Метод используется для генерации команды для
     * выполнения программы.
     *
     * @return возвращает строку - имя исполняемого (бинарного) файла с
     * программой
     */
    public String getBinFileName() {
        return binPath != null ? (new File(binPath)).getName() : null;
    }

    /**
     *
     * @return
     */
    public String getSrcPath() {
        return srcPath;
    }

    /**
     *
     * @return
     */
    public String getSrcFileName() {
        return srcPath != null ? (new File(srcPath)).getName() : null;
    }

    /**
     *
     * @return
     */
    public String getDirPath() {
        return dirPath;
    }
}
