package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
<<<<<<< HEAD
    private String storageFolderPath;

    public static void main(String args[]) throws IOException {
=======
    private final String storageFolderPath;
    private String databasePath;

    public static void main(String[] args) throws IOException {
>>>>>>> develop
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
<<<<<<< HEAD
        } catch(IOException ioe) {
=======
        } catch (IOException ioe) {
>>>>>>> develop
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */
    public String handleCommand(String command) {
<<<<<<< HEAD
        // TODO implement your server logic here

        return "";
=======
        if (command.isEmpty()) {
            return "";
        }
        Parser parser = new Parser(command);
        DBCmd dbCmd = parser.parseCommand();
        if (parser.getParsedOk() && dbCmd != null) {
            return dbCmd.query(this);
        } else {
            return parser.getErrorMessage();
        }
>>>>>>> develop
    }

    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
<<<<<<< HEAD
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

=======
        try (
            Socket s = serverSocket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))
        ) {
>>>>>>> develop
            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }

<<<<<<< HEAD
    public DBTable readDataFromFile(String filename) {
        File fileToRead = new File(filename);
        try {
            FileReader reader = new FileReader(fileToRead);
            BufferedReader buffReader = new BufferedReader(reader);
            try {
                String firstLine = buffReader.readLine();
                if (firstLine.length() > 0) {
                    String[] firstLineSplit = firstLine.split("\t");
                    String name = fileToRead.getName();
                    DBTable table = new DBTable(name, firstLineSplit);
                    int expectedLength = firstLineSplit.length;
                    buffReader
                            .lines()
                            .filter(s -> s.length() > 0)
                            .forEach(s -> addOrCatchInvalidFormatting(table, s, expectedLength));
                    return table;
                }
                buffReader.close();
            } catch (IOException ioException) {
                System.out.println("Cannot read " + filename);
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println(filename + " not found");
        }
        return null;
    }

    private void addOrCatchInvalidFormatting (DBTable table, String line, int expectedLength) {
        try {
            String[] lineSplit = line.split("\t");
            checkInvalidFormatting(lineSplit, expectedLength);
            table.addRow(lineSplit);
        } catch (IOException ioException) {
            System.out.println("Tab file has invalid formatting");
        }
    }

    private void checkInvalidFormatting(String[] line, int expectedLength) throws IOException {
        if (line.length != expectedLength) {
            throw new IOException();
        }
    }

    public void tableToFile(DBTable table, File fileToWrite) {
        try {
            FileWriter writer = new FileWriter(fileToWrite);
            writer.write(table.getColNames() + "\n");
            writer.write(table.getValues());
            writer.flush();
            writer.close();
        } catch (IOException ioException) {
            System.out.println("Cannot write to " + fileToWrite);
        }
    }
=======
    public String getStorageFolderPath() {
        return this.storageFolderPath;
    }

    public String getDatabasePath() {
        return this.databasePath;
    }

    public void setDatabasePath(String path) {
        this.databasePath = path;
    }


>>>>>>> develop
}
