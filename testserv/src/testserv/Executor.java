package testserv;

import DataBaseContent.Generic.Content;
import Shared.Configuration;
import java.sql.*;

/**
 *
 * @author partizanka
 */
public class Executor {

    //private final String url = "jdbc:mysql://localhost/moodle",
      //      user = "moodleuser",
        //    password = "moo";
    private boolean isRunning = false;
    private Connection connection = null;
    private final long timeout = 2000; // ms

    /**
     * 
     */
    public void run() {
        if (Configuration.loadFromFile("testserv.cfg.xml") != 0) {
            System.err.println("Configuration file not found or parse error");
            return;
        }
        isRunning = true;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    Configuration.getURL(),
                    Configuration.getUser(),
                    Configuration.getPassword());
            connection.setTransactionIsolation(connection.TRANSACTION_READ_COMMITTED);
            System.out.println("URL: " + Configuration.getURL());
            System.out.println("Connection: " + connection);
            Content.loadAll(connection);
            SubmissionProcessor sp = new SubmissionProcessor(connection);
            while (isRunning) {
                long time1 = System.currentTimeMillis();
                sp.processSubmission();
                long time2 = System.currentTimeMillis(), delta = time2 - time1;
                Thread.sleep(timeout - delta > 0 ? timeout - delta : 0);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Mysql lib not found: " + e);
        } catch (SQLException e) {
            System.err.println("SQL error occurs: " + e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error while closing connection: " + e);
            }
        }
    }

    /**
     *
     */
    public void stop() {
        isRunning = false;
    }

    /**
     *
     * @param argv
     */
    public static void main(String[] argv) {
        Executor exec = new Executor();
        exec.run();
    }
}
