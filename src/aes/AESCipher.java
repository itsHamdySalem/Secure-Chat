package aes;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AESCipher {
    private static final String AES_ALGORITHM = "AES";

    private SecretKey secretKey;

    private SecretKeySpec generateAESKeyFromBigInt(BigInteger bigInt) {
        byte[] sharedSecretBytes = bigInt.toByteArray();
        byte[] keyBytes = Arrays.copyOf(sharedSecretBytes, 16);
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }

    public AESCipher(BigInteger sharedSecretInt) {
        this.secretKey = generateAESKeyFromBigInt(sharedSecretInt);
    }

    public String encrypt(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return new String(encryptedBytes);
    }

    public String decrypt(String encryptedMessage) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedMessage.getBytes());
            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
