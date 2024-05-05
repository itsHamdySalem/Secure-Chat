package socket;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import aes.AESCipher;
import dh.DiffieHellman;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static AESCipher aesCipher;

    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to the server.");

            BigInteger p = new BigInteger("23");
            BigInteger g = new BigInteger("5");
            DiffieHellman diffieHellman = new DiffieHellman(p, g);

            writer.println(diffieHellman.getPublicKey());
            BigInteger serverPublicKey = new BigInteger(serverReader.readLine());

            BigInteger sharedSecretInt = diffieHellman.computeSharedSecret(serverPublicKey);
            aesCipher = new AESCipher(sharedSecretInt);

            System.out.print("Enter your name: ");
            String userName = userInputReader.readLine();
            // writer.println(aesCipher.encrypt(userName));
            writer.println(userName);

            Thread serverListener = new Thread(() -> {
                String serverMessage;
                try {
                    while ((serverMessage = serverReader.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverListener.start();

            String userInput;
            while ((userInput = userInputReader.readLine()) != null) {
                // String encryptedMessage = aesCipher.encrypt(userInput);
                // writer.println(encryptedMessage);
                writer.println(userInput);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
