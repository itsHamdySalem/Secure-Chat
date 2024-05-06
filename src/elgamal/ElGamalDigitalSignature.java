package elgamal;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ElGamalDigitalSignature {
    private BigInteger p;
    private BigInteger g;
    private BigInteger x;
    private BigInteger y;

    private void generateKeyPair() {
        x = new BigInteger(p.bitLength() - 1, new SecureRandom());
        y = g.modPow(x, p);
    }

    public ElGamalDigitalSignature(BigInteger p, BigInteger g) {
        this.p = p;
        this.g = g;
        generateKeyPair();
    }

    public BigInteger getPublicKey() {
        return this.y;
    }

    public BigInteger[] signMessage(BigInteger message) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        sha1.update(message.toByteArray());
        byte[] digest = sha1.digest();

        BigInteger k;

        do {
            k = new BigInteger(1, digest).mod(p.subtract(BigInteger.ONE)).add(BigInteger.ONE);
            sha1.update(digest);
            digest = sha1.digest();
        } while (!k.gcd(p.subtract(BigInteger.ONE)).equals(BigInteger.ONE));

        BigInteger r = g.modPow(k, p);

        BigInteger s = k.modInverse(p.subtract(BigInteger.ONE)).multiply(message.subtract(x.multiply(r)))
                .mod(p.subtract(BigInteger.ONE));

        return new BigInteger[] { r, s };
    }

    public boolean verifySignature(BigInteger message, BigInteger otherPublicKey, BigInteger r, BigInteger s) {
        if (r.compareTo(BigInteger.ONE) < 0 || r.compareTo(p.subtract(BigInteger.ONE)) >= 0
                || s.compareTo(BigInteger.ONE) < 0 || s.compareTo(p.subtract(BigInteger.ONE)) >= 0) {
            return false;
        }

        BigInteger v1 = g.modPow(message, p);
        BigInteger v2 = otherPublicKey.modPow(r, p).multiply(r.modPow(s, p)).mod(p);

        return v1.equals(v2);
    }
}
