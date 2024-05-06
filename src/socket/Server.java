package socket;

import java.io.File;
import java.util.Set;
import java.net.Socket;
import java.util.HashSet;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.IllegalBlockSizeException;

public class Server {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 2;
    private static final String DH_PARAMETERS_FILE = "resources\\parameters\\dh_parameters.txt";
    private static final String ELGAMAL_PARAMETERS_FILE = "resources\\parameters\\elgamal_parameters.txt";
    private static final BigInteger P = new BigInteger("23");
    private static final BigInteger G = new BigInteger("5");

    public static Set<PrintWriter> clientWriters = new HashSet<>();

    private static void clearFile(String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.print("");
        }
    }

    private static void saveDHParametersToFile(BigInteger p, BigInteger g) throws FileNotFoundException {
        File file = new File(DH_PARAMETERS_FILE);
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println(p);
            writer.println(g);
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for clients...");

            clearFile(DH_PARAMETERS_FILE);
            clearFile(ELGAMAL_PARAMETERS_FILE);

            BigInteger p = P;
            BigInteger g = G;
            saveDHParametersToFile(p, g);

            int clientsCount = 0;
            while (clientsCount < MAX_CLIENTS) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(writer);

                new Thread(new ClientHandler(clientSocket, writer)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message, PrintWriter sender) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        for (PrintWriter writer : clientWriters) {
            if (writer != sender) {
                writer.println(message);
            }
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter writer;

    public ClientHandler(Socket socket, PrintWriter writer) {
        this.clientSocket = socket;
        this.writer = writer;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Received message from " + clientSocket + ": " + line);
                Server.broadcastMessage(line, writer);
            }
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                Server.clientWriters.remove(writer);
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
