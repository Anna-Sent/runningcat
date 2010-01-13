/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
    public static String createTempFile(
            String prefix, String suffix, String directoryPath)
            throws CanNotCreateTemporaryFileException {
        String path = "";
        File temp = null;
        try {
            temp = File.createTempFile(prefix, suffix, new File(directoryPath));
            path = temp.getAbsolutePath();
        } catch (IOException ex) {
            throw new CanNotCreateTemporaryFileException(
                    "Cannot create temporary file in directory \""+directoryPath
                    +"\"");
        }
        return path;
    }
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
                    "Cannot create temporary directory in directory \""+
                    directoryPath+"\"");
        }
        return path;
    }
    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists())
            file.delete();
    }
    public static void deleteDir(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i=0; i<files.length; ++i)
                if (files[i].isDirectory())
                    deleteDir(files[i].getAbsolutePath());
                else
                    files[i].delete();
        }
        dir.delete();
    }
    public static void writeFile(String path, String text)
            throws CanNotWriteFileException {
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(path));
            output.write(text);
        } catch (IOException ex) {
            throw new CanNotWriteFileException("Cannot write file \""+path+
                    "\"");
        } finally {
            try {
                if (output!=null) output.close();
            } catch (IOException ex) {
                throw new CanNotWriteFileException("Cannot write file \""+path+
                    "\"");
            }
        }
    }
    public static void close(Reader reader) {
        try {
            if (reader != null) reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void close(Writer writer) {
        try {
            if (writer != null) writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
