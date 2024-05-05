package socket;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import aes.AESCipher;
import dh.DiffieHellman;

public class Server {
    private static final int PORT = 12345;
    public static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Map<PrintWriter, String> clientNames = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for clients...");

            BigInteger p = new BigInteger("23");
            BigInteger g = new BigInteger("5");
            DiffieHellman diffieHellman = new DiffieHellman(p, g);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(writer);

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                BigInteger clientPublicKey = new BigInteger(reader.readLine());
                writer.println(diffieHellman.getPublicKey());

                // TODO: share the secret key using elgamal digital signature, then verify it
                BigInteger sharedSecretInt = diffieHellman.computeSharedSecret(clientPublicKey);

                String clientName = reader.readLine();
                clientNames.put(writer, clientName);

                new Thread(new ClientHandler(clientSocket, writer)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
        }
    }

    public static void broadcastMessageWithSender(String message, PrintWriter sender) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String senderName = clientNames.get(sender);
        for (PrintWriter writer : clientWriters) {
            if (writer != sender) {
                writer.println(senderName + ": " + message);
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
                System.out.println("Received message: " + line);
                Server.broadcastMessageWithSender(line, writer);
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
