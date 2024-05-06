package socket;

import java.io.File;
import java.net.Socket;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.IllegalBlockSizeException;

import aes.AESCipher;
import dh.DiffieHellman;
import elgamal.ElGamalDigitalSignature;

public class Client {
    private static final int SERVER_PORT = 12345;
    private static final String SERVER_ADDRESS = "localhost";
    private static final String DH_PARAMETERS_FILE = "resources\\parameters\\dh_parameters.txt";
    private static final String ELGAMAL_PARAMETERS_FILE = "resources\\parameters\\elgamal_parameters.txt";

    private static DiffieHellman dh;
    private static ElGamalDigitalSignature elGamal;

    private static BigInteger[] getDHParameters() throws FileNotFoundException {
        File file = new File(DH_PARAMETERS_FILE);
        Scanner scanner = new Scanner(file);
        BigInteger p = new BigInteger(scanner.nextLine());
        BigInteger g = new BigInteger(scanner.nextLine());
        scanner.close();
        return new BigInteger[] { p, g };
    }

    private static void saveElGamalParametersToFile(BigInteger publicKey) throws IOException {
        File file = new File(ELGAMAL_PARAMETERS_FILE);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            writer.println(publicKey);
        }
    }

    private static BigInteger getOtherElGamalPublicKey() throws FileNotFoundException {
        BigInteger myPublicKey = elGamal.getPublicKey();
        File file = new File(ELGAMAL_PARAMETERS_FILE);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            BigInteger publicKey = new BigInteger(line);
            if (!publicKey.equals(myPublicKey)) {
                scanner.close();
                return publicKey;
            }
        }
        scanner.close();
        return BigInteger.valueOf(-1);
    }

    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, InterruptedException {

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to the server.");

            BigInteger[] dhParams = getDHParameters();
            BigInteger p = dhParams[0];
            BigInteger g = dhParams[1];

            dh = new DiffieHellman(p, g);
            elGamal = new ElGamalDigitalSignature(p, g);

            BigInteger otherElGamalPublicKey = getOtherElGamalPublicKey();

            saveElGamalParametersToFile(elGamal.getPublicKey());

            while (otherElGamalPublicKey.equals(BigInteger.valueOf(-1))) {
                otherElGamalPublicKey = getOtherElGamalPublicKey();
            }

            BigInteger Y = dh.getPublicKey();
            BigInteger[] signedKey = elGamal.signMessage(Y);
            BigInteger r = signedKey[0];
            BigInteger s = signedKey[1];

            writer.println(Y + "," + r + "," + s);

            BigInteger otherDHPublicKey = BigInteger.valueOf(-1);
            BigInteger otherR = BigInteger.ZERO;
            BigInteger otherS = BigInteger.ZERO;

            String line;
            while ((line = serverReader.readLine()) == null) {
                // wait...
            }
            String[] msg = line.split(",");
            otherDHPublicKey = new BigInteger(msg[0]);
            otherR = new BigInteger(msg[1]);
            otherS = new BigInteger(msg[2]);

            if (otherElGamalPublicKey.equals(BigInteger.valueOf(-1))) {
                System.out.println("Initialization Error!");
                return;
            }

            if (!elGamal.verifySignature(otherDHPublicKey, otherElGamalPublicKey, otherR, otherS)) {
                System.out.println("Invalid digital signature!");
                return;
            }

            BigInteger sharedSecretInt = dh.computeSharedSecret(otherDHPublicKey);
            AESCipher aesCipher = new AESCipher(sharedSecretInt);

            Thread serverListener = new Thread(() -> {
                String serverMessage;
                try {
                    while ((serverMessage = serverReader.readLine()) != null) {
                        serverMessage = aesCipher.decrypt(serverMessage);
                        System.out.println("Received: " + serverMessage);
                    }
                } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                        | IllegalBlockSizeException | BadPaddingException e) {
                    e.printStackTrace();
                }
            });

            System.out.println("You can start chatting!");
            serverListener.start();

            String userInput;
            while ((userInput = userInputReader.readLine()) != null) {
                userInput = aesCipher.encrypt(userInput);
                writer.println(userInput);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
