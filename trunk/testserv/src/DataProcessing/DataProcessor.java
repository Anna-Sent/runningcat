package DataProcessing;

import DataProcessing.Exceptions.DataWriteException;
import DataProcessing.Exceptions.DataReadException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author partizanka
 */
public class DataProcessor {

    /**
     *
     * @param list1
     * @param list2
     * @return
     */
    public boolean isEqual(ArrayList<String> list1, ArrayList<String> list2) {
        int count1 = list1.size(), count2 = list2.size();
        if (count1 > count2) {
            return false;
        } else if (count1 < count2) {
            return false;
        } else {
            for (int i = 0; i < count1; ++i) {
                if (list1.get(i).compareTo(list2.get(i)) != 0) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 
     * @param reader
     * @return
     * @throws DataReadException
     */
    public ArrayList<String> read(BufferedReader reader)
            throws DataReadException {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            throw new DataReadException("Data read error: " + e);
        }
    }

    /**
     *
     * @param writer
     * @param lines
     * @throws DataWriteException
     */
    public void write(BufferedWriter writer, ArrayList<String> lines)
            throws DataWriteException {
        try {
            for (int i = 0; i < lines.size(); ++i) {
                writer.write(lines.get(i));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new DataWriteException("Data write error: " + e);
        }
    }

    /**
     *
     * @param reader
     * @return
     * @throws DataReadException
     */
    public Long readLong(Reader reader) throws DataReadException {
        Scanner scanner = new Scanner(reader);
        if (scanner.hasNextLong()) {
            return new Long(scanner.nextLong());
        } else {
            throw new DataReadException("Reading long value error");
        }
    }

    /**
     *
     * @param reader
     * @return
     * @throws DataReadException
     */
    public Double readDouble(BufferedReader reader) throws DataReadException {
        Scanner scanner = new Scanner(reader);
        if (scanner.hasNextDouble()) {
            return new Double(scanner.nextDouble());
        } else {
            throw new DataReadException("Reading double value error");
        }
    }

    /**
     *
     * @param reader
     * @return
     * @throws DataReadException
     */
    public String readString(BufferedReader reader) throws DataReadException {
        Scanner scanner = new Scanner(reader);
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        } else {
            throw new DataReadException("Reading string value error");
        }
    }

    /**
     *
     * @param reader
     * @return
     * @throws DataReadException
     */
    public Character readCharacter(BufferedReader reader) throws DataReadException {
        try {
            return new Character((char) reader.read());
        } catch (IOException e) {
            throw new DataReadException("Reading character value error");
        }
    }

    /**
     *
     * @param reader
     * @return
     * @throws DataReadException
     */
    public ArrayList<Long> readArrayOfLong(BufferedReader reader) throws DataReadException {
        Scanner scanner = new Scanner(reader);
        if (scanner.hasNextInt()) {
            int size = scanner.nextInt();
            ArrayList<Long> array = new ArrayList<Long>();
            for (int i = 0; i < size; ++i) {
                if (scanner.hasNextLong()) {
                    array.add(new Long(scanner.nextLong()));
                } else {
                    throw new DataReadException("Reading array of long value error");
                }
            }
            return array;
        } else {
            throw new DataReadException("Reading array of long value error");
        }
    }

    /**
     *
     * @param reader
     * @return
     * @throws DataReadException
     */
    public ArrayList<Double> readArrayOfDouble(BufferedReader reader) throws DataReadException {
        Scanner scanner = new Scanner(reader);
        if (scanner.hasNextInt()) {
            int size = scanner.nextInt();
            ArrayList<Double> array = new ArrayList<Double>();
            for (int i = 0; i < size; ++i) {
                if (scanner.hasNextDouble()) {
                    array.add(new Double(scanner.nextDouble()));
                } else {
                    throw new DataReadException("Reading array of double value error");
                }
            }
            return array;
        } else {
            throw new DataReadException("Reading array of double value error");
        }
    }

    /**
     *
     * @param reader
     * @return
     * @throws DataReadException
     */
    public ArrayList<String> readArrayOfString(BufferedReader reader) throws DataReadException {
        Scanner scanner = new Scanner(reader);
        if (scanner.hasNextInt()) {
            int size = scanner.nextInt();
            ArrayList<String> array = new ArrayList<String>();
            for (int i = 0; i < size; ++i) {
                if (scanner.hasNextLine()) {
                    array.add(scanner.nextLine());
                } else {
                    throw new DataReadException("Reading array of string value error");
                }
            }
            return array;
        } else {
            throw new DataReadException("Reading array of string value error");
        }
    }

    /**
     *
     * @param reader
     * @return
     * @throws DataReadException
     */
    public ArrayList<Character> readArrayOfCharacter(BufferedReader reader) throws DataReadException {
        return null;
        /*Scanner scanner = new Scanner(reader);
        if (scanner.hasNextInt()) {
        int size = scanner.nextInt();
        ArrayList<Long> array = new ArrayList<Long>();
        for (int i=0;i<size;++i)
        if (scanner.hasNextLong())
        array.add(new Long(scanner.nextLong()));
        else
        throw new DataErrorException("Reading array of long value error");
        return array;
        } else
        throw new DataErrorException("Reading array of long value error");*/
    }

    /**
     *
     * @param reader
     * @return
     * @throws DataReadException
     */
    public ArrayList<ArrayList<Integer>> readArrayOfArrayOfInteger(BufferedReader reader) throws DataReadException {
        return null;
    }

    /**
     *
     * @param reader
     * @return
     * @throws DataReadException
     */
    public ArrayList<ArrayList<Double>> readArrayOfArrayOfDouble(BufferedReader reader) throws DataReadException {
        return null;
    }

    /**
     *
     * @param reader
     * @return
     * @throws DataReadException
     */
    public ArrayList<ArrayList<String>> readArrayOfArrayOfString(BufferedReader reader) throws DataReadException {
        return null;
    }

    /**
     *
     * @param reader
     * @return
     * @throws DataReadException
     */
    public ArrayList<ArrayList<Character>> readArrayOfArrayOfCharacter(BufferedReader reader) throws DataReadException {
        return null;
    }
}
