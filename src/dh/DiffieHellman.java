package dh;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DiffieHellman {
    private BigInteger p;
    private BigInteger g;
    private BigInteger publicKey;
    private BigInteger privateKey;

    private void generateKeyPair() {
        privateKey = new BigInteger(p.bitLength() - 1, new SecureRandom());
        publicKey = g.modPow(privateKey, p);
    }

    public DiffieHellman(BigInteger p, BigInteger g) {
        this.p = p;
        this.g = g;
        this.generateKeyPair();
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    public BigInteger computeSharedSecret(BigInteger otherPublicKey) {
        return otherPublicKey.modPow(privateKey, p);
    }
}
