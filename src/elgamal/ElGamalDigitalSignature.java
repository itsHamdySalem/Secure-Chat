package elgamal;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class ElGamalDigitalSignature {
    private static final BigInteger ONE = BigInteger.ONE;

    private BigInteger p; // Prime modules
    private BigInteger g; // Generator
    private BigInteger x; // Private key
    private BigInteger y; // Public key

    private void generateKeyPair() {
        x = new BigInteger(p.bitLength() - 1, new SecureRandom());
        y = g.modPow(x, p);
    }

    public ElGamalDigitalSignature(BigInteger p, BigInteger g) {
        this.p = p;
        this.g = g;
        generateKeyPair();
    }

    public BigInteger[] signMessage(BigInteger message) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        sha1.update(message.toByteArray());
        byte[] digest = sha1.digest();

        BigInteger k = new BigInteger(1, digest).mod(p.subtract(BigInteger.ONE)).add(BigInteger.ONE);

        BigInteger r = g.modPow(k, p);
        BigInteger s = k.modInverse(p.subtract(BigInteger.ONE)).multiply(message.subtract(x.multiply(r)))
                .mod(p.subtract(BigInteger.ONE));

        return new BigInteger[] { r, s };
    }

    public boolean verifySignature(BigInteger message, BigInteger r, BigInteger s) {
        if (r.compareTo(BigInteger.ONE) < 0 || r.compareTo(p.subtract(BigInteger.ONE)) >= 0
                || s.compareTo(BigInteger.ONE) < 0 || s.compareTo(p.subtract(BigInteger.ONE)) >= 0) {
            return false;
        }

        BigInteger v1 = g.modPow(message, p);
        BigInteger v2 = y.modPow(r, p).multiply(r.modPow(s, p)).mod(p);

        return v1.equals(v2);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        BigInteger p = new BigInteger("23");
        BigInteger g = new BigInteger("5");
        ElGamalDigitalSignature elGamal = new ElGamalDigitalSignature(p, g);

        BigInteger message = new BigInteger("7");

        BigInteger[] signature = elGamal.signMessage(message);
        BigInteger r = signature[0];
        BigInteger s = signature[1];

        boolean isValid = elGamal.verifySignature(message, r, s);
        System.out.println("Signature is valid: " + isValid);
    }
}
