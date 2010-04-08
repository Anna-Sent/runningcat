package FileOperations;

import FileOperations.Exceptions.CanNotWriteFileException;
import FileOperations.Exceptions.CanNotCreateTemporaryFileException;
import FileOperations.Exceptions.CanNotCreateTemporaryDirectoryException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 *
 * @author partizanka
 */
public class FileOperator {

    private static String createTempFile(
            String prefix, String suffix, String directoryPath)
            throws CanNotCreateTemporaryFileException {
        String path = "";
        File temp = null;
        try {
            temp = File.createTempFile(prefix, suffix, new File(directoryPath));
            path = temp.getAbsolutePath();
        } catch (IOException ex) {
            throw new CanNotCreateTemporaryFileException(
                    "Cannot create temporary file in directory \"" + directoryPath + "\"");
        }
        return path;
    }

    /**
     *
     * @param prefix
     * @param suffix
     * @param directoryPath
     * @return
     * @throws CanNotCreateTemporaryFileException
     */
    public static String createFile(
            String prefix, String suffix, String directoryPath)
            throws CanNotCreateTemporaryFileException {
        String path = "";
        File temp = null;
        try {
            temp = new File(directoryPath + "/" + prefix + suffix);
            temp.createNewFile();
            path = temp.getAbsolutePath();
        } catch (IOException ex) {
            throw new CanNotCreateTemporaryFileException(
                    "Cannot create temporary file in directory \"" + directoryPath + "\"");
        }
        return path;
    }

    /**
     * 
     * @param prefix
     * @param suffix
     * @param directoryPath
     * @return
     * @throws CanNotCreateTemporaryDirectoryException
     */
    public static String createDirectory(
            String prefix, String suffix, String directoryPath)
            throws CanNotCreateTemporaryDirectoryException {
        String path = "";
        File temp = null;
        try {
            temp = File.createTempFile(prefix, suffix, new File(directoryPath));
            String name = temp.getAbsolutePath();
            if (temp.delete()) {
                File dir = new File(name);
                dir.mkdir();
                path = temp.getAbsolutePath();
            }
        } catch (IOException ex) {
            throw new CanNotCreateTemporaryDirectoryException(
                    "Cannot create temporary directory in directory \"" +
                    directoryPath + "\"");
        }
        return path;
    }

    /**
     *
     * @param path
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     *
     * @param path
     */
    public static void deleteDir(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
                    deleteDir(files[i].getAbsolutePath());
                } else {
                    files[i].delete();
                }
            }
        }
        dir.delete();
    }

    /**
     *
     * @param path
     * @param text
     * @throws CanNotWriteFileException
     */
    public static void writeFile(String path, String text)
            throws CanNotWriteFileException {
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(path));
            output.write(text);
        } catch (IOException ex) {
            throw new CanNotWriteFileException("Cannot write file \"" + path +
                    "\"");
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException ex) {
                throw new CanNotWriteFileException("Cannot write file \"" + path +
                        "\"");
            }
        }
    }

    /**
     *
     * @param reader
     */
    public static void close(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            System.err.println("Reader closing error");//e.printStackTrace();
        }
    }

    /**
     *
     * @param writer
     */
    public static void close(Writer writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("Writer closing error");//e.printStackTrace();
        }
    }
}
