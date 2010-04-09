package Program;

import DataBaseContent.Problem;
import FileOperations.Exceptions.CanNotCreateTemporaryDirectoryException;
import FileOperations.Exceptions.CanNotCreateTemporaryFileException;
import FileOperations.Exceptions.CanNotWriteFileException;
import FileOperations.FileOperator;
import Shared.Configuration;
import java.io.File;

/**
 * Класс "Программа" хранит в себе текст программы, код языка программирования,
 * ссылку на решаемую задачу, имена файлов исходников и бинарников.
 *
 * @author partizanka
 */
public class Program {

    /**
     * Код языка программы. Только для чтения, инициализируется в конструкторе.
     */
    public final int lang;
    /**
     * Текст программы (исходный код). Только для чтения, инициализируется в конструкторе.
     */
    public final String text;
    /**
     * Ссылка на решаемую задачу. Только для чтения, инициализируется в конструкторе.
     */
    public final Problem problem;
    /**
     * Строка - путь к файлу с исходным кодом.
     */
    private String srcPath;
    /**
     * Строка - путь к директории с исполняемым файлом, файлом с исходным кодом,
     * другими файлами (объектными).
     */
    private String dirPath;
    /**
     * Строка - путь к исполняемому (бинарному) файлу.
     */
    private String binPath;

    /**
     * Конструктор класса программы.
     *
     * @param lang код языка программирования, на котором написана программа
     * @param text текст программы (исходный код)
     * @param problem ссылка на задачу, решаемую программой
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
     * Инициализируются переменные <code>dirPath</code>, <code>srcPath</code>,
     * <code>binPath</code>.
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
        if (Configuration.isCompiled(lang)) {
            binPath = srcPath.replace(Configuration.getSourceSuffix(lang), Configuration.getBinarySuffix(lang));
        }
        FileOperator.writeFile(srcPath, text);
    }

    /**
     * Удаляет директорию со всеми созданными файлами.
     * Метод необходимо вызывать после <code>prepare</code>.
     */
    public void close() {
        if (dirPath.compareTo("") != 0 && (new File(dirPath)).exists()) {
            FileOperator.deleteDir(dirPath);
        }
    }

    /**
     * Возвращает <code>true</code>, если бинарный файл существует.
     * Метод стоит вызывать после компиляции программы для проверки ее
     * успешности.
     *
     * @return <code>true</code>, если бинарный файл существует,
     * <code>false</code> - в противном случае
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
     * Возвращает команду с параметрами для исполнения программы.
     *
     * @return массив строк - команду и параметры для выполнения программы
     */
    public String[] getExecuteCmd() {
        return getCmd(Configuration.getExecuteCommand(lang));
    }

    /**
     * Возвращает команду с параметрами для компиляции программы.
     *
     * @return массив строк - команду и параметры для компиляции программы
     */
    public String[] getCompileCmd() {
        return getCmd(Configuration.getCompilerCommand(lang));
    }

    /**
     * Возвращает путь к исполняемому (бинарному) файлу с программой.
     * Метод стоит вызывать после <code>prepare</code>. Файл будет существовать
     * в случае удачной компиляции программы.
     *
     * @return строка - путь к исполняемому (бинарному) файлу с программой;
     * <code>null</code> - в случае, если программа написана на интерпретируемом
     * языке
     */
    public String getBinPath() {
        return binPath;
    }

    /**
     * Возвращает имя исполняемого (бинарного) файла с программой.
     * Стоит вызывать после <code>prepare</code>. Файл будет существовать
     * в случае удачной компиляции программы. Метод используется для
     * генерации команды для выполнения программы.
     *
     * @return возвращает строку - имя исполняемого (бинарного) файла с
     * программой; <code>null</code> - в случае, если программа написана на
     * интерпретируемом языке
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
