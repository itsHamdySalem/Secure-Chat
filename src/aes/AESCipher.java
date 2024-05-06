package aes;

import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import java.math.BigInteger;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.NoSuchAlgorithmException;

public class AESCipher {
    private SecretKey secretKey;

    private SecretKeySpec generateAESKeyFromBigInt(BigInteger bigInt) {
        byte[] sharedSecretBytes = bigInt.toByteArray();
        byte[] keyBytes = Arrays.copyOf(sharedSecretBytes, 32);
        return new SecretKeySpec(keyBytes, "AES");
    }

    public AESCipher(BigInteger sharedSecretInt) {
        this.secretKey = generateAESKeyFromBigInt(sharedSecretInt);
    }

    public String encrypt(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedMessage) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }
}
