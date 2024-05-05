package dh;

import java.math.BigInteger;
import java.security.*;

public class DiffieHellman {
    private BigInteger p;
    private BigInteger g;
    private BigInteger publicKey;
    private BigInteger privateKey;

    private void generatePublicKey() {
        privateKey = new BigInteger(p.bitLength() - 1, new SecureRandom());
    }

    private void generatePrivateKey() {
        publicKey = g.modPow(privateKey, p);
    }

    public DiffieHellman(BigInteger p, BigInteger g) {
        this.p = p;
        this.g = g;
        this.generatePublicKey();
        this.generatePrivateKey();
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    public BigInteger computeSharedSecret(BigInteger otherPublicKey) {
        return otherPublicKey.modPow(privateKey, p);
    }

    public static void main(String[] args) {
        BigInteger p = new BigInteger("33");
        BigInteger g = new BigInteger("8");

        DiffieHellman alice = new DiffieHellman(p, g);
        DiffieHellman bob = new DiffieHellman(p, g);

        BigInteger alicePublicKey = alice.getPublicKey();
        BigInteger bobPublicKey = bob.getPublicKey();

        BigInteger aliceSharedSecret = alice.computeSharedSecret(bobPublicKey);
        BigInteger bobSharedSecret = bob.computeSharedSecret(alicePublicKey);

        System.out.println("Alice's shared secret: " + aliceSharedSecret);
        System.out.println("Bob's shared secret: " + bobSharedSecret);
    }
}
