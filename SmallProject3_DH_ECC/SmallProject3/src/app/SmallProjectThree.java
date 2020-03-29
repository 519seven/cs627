package app;

/*
  Copyright Peter Akey 2020
  With assistance from the following:
  https://docs.oracle.com/javase/6/docs/technotes/tools/index.html#basic
  https://stackoverflow.com/questions/4095024/java-comparing-biginteger-values
  https://stackoverflow.com/questions/8557716/how-to-return-multiple-values
  http://commons.apache.org/proper/commons-cli/javadocs/api-release/index.html
  https://stackoverflow.com/questions/367706/how-do-i-parse-command-line-arguments-in-java
  https://blog.usejournal.com/visual-studio-code-for-java-the-ultimate-guide-2019-8de7d2b59902
  https://rosettacode.org/wiki/Modular_inverse#Java

*/

import java.util.Random;
import org.apache.commons.cli.*;
import javax.lang.model.util.ElementScanner6;
import java.lang.Math;
import java.math.BigInteger;

public class SmallProjectThree {

    public static Boolean DEBUG = false;
    public static int CERTAINTY = 70;
    BigInteger ZERO = new BigInteger("0", 2);
    BigInteger ONE = new BigInteger("1", 2);
    static BigInteger TWO = new BigInteger("10", 2);
    static BigInteger THREE = new BigInteger("11", 2);
    static BigInteger FIVE = new BigInteger("101", 2);
    BigInteger SIX = new BigInteger("110", 2);
    static BigInteger TEN = new BigInteger("1010", 2); // 10
    static BigInteger TWENTYTWO = new BigInteger("16", 16);
    static BigInteger THIRTYONE = new BigInteger("1F", 16);

    public static void printer(String s) {
        System.out.println(s);
    }


    public boolean checkGenerator(String b, BigInteger g) {
        BigInteger p = new BigInteger(b, 16);
        BigInteger q = p.subtract(ONE).divide(TWO);
        if (probPrime(q.toString(16))) {
            if (g.mod(p).compareTo(ONE) == 0) {
                return false;
            } else if (g.modPow(g, p).compareTo(ONE) == 0) {
                return false;
            } else if (g.modPow(q, p).compareTo(ONE) == 0) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }


    public boolean checkPrime(String b) {
        // convert string to hex
        BigInteger n = new BigInteger(b, 16);
        if ( n.compareTo(TWO) == 0 && n.mod(TWO).compareTo(ZERO) == 0 ) {
            printer(n.toString(16) + " is not prime");
            return false;
        }
        BigInteger top = n.divide(TWO);
        for(BigInteger i = new BigInteger("3", 10); i.compareTo(top.subtract(ONE)) < 0; i.add(SIX)){
            if (n.mod(i).compareTo(ZERO) == 0){
                printer(n + " is not prime");
                return false;
            }
        }
        printer(n + " is prime");
        return true; 
    }


    public void diffieHellman(String header, BigInteger g, BigInteger Xa, BigInteger Xb, BigInteger p) {
        // Alice and Bob compute their public keys
        BigInteger pubAlice = g.modPow(Xa, p);
        BigInteger pubBob = g.modPow(Xb, p);
        // Next, Alice and Bob would exchange keys
        // Now, each can compute the common session key
        BigInteger kAlice = pubBob.modPow(Xa, p);
        BigInteger kBob = pubAlice.modPow(Xb, p);
        if (DEBUG) {
            printer("["+header+"    ANSWER] Alice's common session key: 0x"+kAlice.toString(16));
            printer("["+header+"     DEBUG] Bob's common session key  : 0x"+kBob.toString(16));
        }
        if (kAlice.compareTo(kBob) == 0) {
            printer("["+header+"      INFO] Success! Alice's and Bob's common session keys are the same!");
        } else {
            printer("["+header+"      INFO] Fail! Alice's and Bob's common session keys are NOT the same!");
        }
    }


    public BigInteger[] eccCreatePublicKey(String header, BigInteger a, BigInteger b, BigInteger p, BigInteger nA, BigInteger Gx, BigInteger Gy) {
        // Using the for loop referenced in Dr. Wang's slides, let's calculate a x (Gx, Gy)
        int t = nA.bitLength();
        BigInteger[] Z = new BigInteger[2];
        Z[0] = ZERO; Z[1] = ZERO;
        BigInteger[] Q = new BigInteger[2];
        Q[0] = ZERO;
        Q[1] = ZERO;
        if (DEBUG) { printer("["+header+"       DEBUG] length in bits of Alice's private key: "+t+" (in binary: "+Integer.toBinaryString(nA.intValue())+")"); }
        for (int i = t-1; i >= 0; i--) {
            // Each time thgrough we have to check...
            // If ZERO (first time through):
            //   - assign value of base point G to Q
            // If not ZERO:
            //   - add Q to Q
            // Then...in a separate transaction >>>
            // If bit is a 1:
            //   - If Q ZERO (still?):
            //       -> Q + G (assign G to Q) (will algorithm support this?)
            //                                (not too concerned           )
            //                                (it's a simple check         )
            //   - Else:
            //       -> add Q + G
            if (Q[0].compareTo(ZERO) != 0 && Q[1].compareTo(ZERO) != 0) {
                // The first time through, Q is ZERO POINT
                Q = ECPointAddition(header, a, b, p, Q[0], Q[1], Q[0], Q[1]);
            } else if (Z[0].compareTo(ZERO) == 0 && Z[1].compareTo(ZERO) == 0) {
                // Z will only be (0,0) the first iteration; after that, we always add Q+Q
                // Change Z (ZERO,ZERO) to (ONE,ONE) and never hit this condition again
                Z[0] = ONE;
                Z[1] = ONE;
            }
            // if the bit at index t is a 1 then do point addition Q + G
            if (DEBUG) { printer("["+header+"       DEBUG] Is the bit a 1?: "+nA.testBit(i)); }
            if (nA.testBit(i)) {
                // if this bit is one, then
                if (Q[0].compareTo(ZERO) == 0 && Q[1].compareTo(ZERO) == 0) {
                    // If Q is ZERO POINT then Q becomes the thing being added to it
                    printer("["+header+"        INFO] Adding G (0x"+Gx.toString(16)+", 0x"+Gy.toString(16)+") to Q");
                    Q[0] = Gx;
                    Q[1] = Gy;
                } else {
                    if (DEBUG) { printer("["+header+"       DEBUG] Adding G (0x"+Gx.toString(16)+", 0x"+Gy.toString(16)+") to Q"); }
                    Q = ECPointAddition(header, a, b, p, Q[0], Q[1], Gx, Gy);
                }
            }
        }
        printer("["+header+"(1)   ANSWER] Alice's ECC public key is (0x"+Q[0].toString(16)+", 0x"+Q[1].toString(16)+")");
        if (DEBUG) {
            printer("["+header+"       DEBUG] With the following parameters:");
            printer("["+header+"       DEBUG] Base point G: (0x"+Gx.toString(16)+", 0x"+Gy.toString(16)+")");
            printer("["+header+"       DEBUG] a = 0x"+a.toString(16));
            printer("["+header+"       DEBUG] b = 0x"+b.toString(16));
            printer("["+header+"       DEBUG] p = 0x"+p.toString(16));
        }
        return Q;   // At this point, this is actually Pa
    }


    public BigInteger[] ECPointAddition(String header, BigInteger a, BigInteger b, BigInteger p, BigInteger Xp, BigInteger Yp, BigInteger Xq, BigInteger Yq) {
        BigInteger[] newPoint = new BigInteger[2];
        BigInteger topHalf;
        BigInteger bottomHalf;

        if (DEBUG) { printer("["+header+"       DEBUG] Testing to see if "+Xp+" and "+Xq+" AND "+Yp+" and "+Yq+" are the same..."); }
        if ( Xp.compareTo(Xq) == 0 && Yp.compareTo(Yq) == 0 ) {
            // If P = Q
            if (DEBUG) { printer("["+header+"       DEBUG] P and Q are the same"); }
            topHalf = (THREE.multiply(Xp.pow(2))).add(a).mod(p);
            bottomHalf = (TWO.multiply(Yp)).mod(p);
        } else {
            if (DEBUG) { printer("["+header+"       DEBUG] P and Q are NOT the same"); }
            topHalf = (Yq.subtract(Yp)).abs().mod(p);
            if (DEBUG) { printer("["+header+"       DEBUG] Xq = "+Xq+" and Xp = "+Xp); }
            bottomHalf = (Xq.subtract(Xp)).abs().mod(p);
        }
        BigInteger modularInverseOfBottomHalf = bottomHalf.modInverse(p);
        if (DEBUG) { printer("["+header+"       DEBUG] Modular inverse of 0x"+bottomHalf.toString(16)+" is: 0x"+modularInverseOfBottomHalf.toString(16)); }
        BigInteger lambda = (topHalf.multiply(modularInverseOfBottomHalf)).mod(p);
        if (DEBUG) { printer("["+header+"       DEBUG] lambda is 0x"+lambda.toString(16)); }
        BigInteger Xr = (((lambda.pow(2)).subtract(Xp)).subtract(Xq)).mod(p);
        BigInteger Yr = (lambda.multiply(Xp.subtract(Xr)).subtract(Yp)).mod(p);
        newPoint[0] = Xr;
        newPoint[1] = Yr;
        return newPoint;
        /**/
    }


    public void elgamal(String header, BigInteger a, BigInteger Xa, BigInteger Xb, BigInteger q, BigInteger k) {
        // Question 4 Part (a) - What is Alice's Elgamal public key?
        BigInteger Ya = a.modPow(Xa, q);
        printer("["+header+"(a) ANSWER] Alice's Elgamal public key is: 0x" + Ya.toString(16));
        // Now, Alice's public key is made available to Bob: {q, a, Ya}
        BigInteger keyEphemeral = Ya.modPow(k, q);
        BigInteger message = new BigInteger("A", 16);
        BigInteger C1 = a.modPow(k, q);
        BigInteger C2 = keyEphemeral.multiply(message).mod(q);
        printer("["+header+"(b) ANSWER] Bob's Elgamal ciphertext is: ("+C1+", "+C2+"). He will send this to Alice.");
        // The ciphertext is sent to Alice and Alice decrypts it
        BigInteger K = C1.modPow(Xa, q);
        BigInteger invK = K.modInverse(q);
        BigInteger plaintext = C1.multiply(invK).mod(q);
        if (message.compareTo(plaintext) == 0) {
            printer("["+header+"(c) ANSWER] Success!  Decrypted plaintext matches message! (["+message+"] == ["+plaintext+"])");
        } else {
            printer("["+header+"(c) ANSWER] Fail! :(  Decrypted plaintext does NOT match message! (["+message+"] == ["+plaintext+"])");
        }
    }


    public boolean probPrime(String b) {
        BigInteger n = new BigInteger(b, 16);
        // When CERTAINTY is one, it will check number for prime or composite 
        boolean result = false;
        result = n.isProbablePrime(CERTAINTY); 
  
        boolean doAll = false;
        if (doAll) {
            CERTAINTY = 0;
            // When CERTAINTY is zero, it is always true 
            result = n.isProbablePrime(0);
            // When CERTAINTY is negative, it is always true 
            CERTAINTY = -1;
            result = n.isProbablePrime(-1); 
        }
        return result;
    }

	public static void main(final String[] args) throws Exception {
		printer("\n\n[        ATTENTION] Small Project 3 output begins...");
        final SmallProjectThree sp3 = new SmallProjectThree();
        Options options = new Options();

        Option input = new Option("d", "debug", true, "Enable verbose output");
        input.setRequired(false);
        options.addOption(input);

        Option output = new Option("c", "certainty", true, "Certainty level for checking for primes");
        output.setRequired(false);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            try {
                DEBUG = Boolean.parseBoolean(cmd.getOptionValue("debug"));
                printer("[             INFO] Setting DEBUG to "+Boolean.toString(DEBUG));
            } catch (IndexOutOfBoundsException i) {
                System.out.println(i.getMessage());
                formatter.printHelp("utility-name", options);
                System.exit(1);
            }
            
            try {
                CERTAINTY = Integer.valueOf(cmd.getOptionValue("certainty"));
            } catch (NumberFormatException n) {
                CERTAINTY = 70;
            }
        } catch (ParseException e) {
            //
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        String modulusString = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF6955817183995497CEA956AE515D2261898FA051015728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6BF12FFA06D98A0864D87602733EC86A64521F2B18177B200CBBE117577A615D6C770988C0BAD946E208E24FA074E5AB3143DB5BFCE0FD108E4B82D120A92108011A723C12A787E6D788719A10BDBA5B2699C327186AF4E23C1A946834B6150BDA2583E9CA2AD44CE8DBBBC2DB04DE8EF92E8EFC141FBECAA6287C59474E6BC05D99B2964FA090C3A2233BA186515BE7ED1F612970CEE2D7AFB81BDD762170481CD0069127D5B05AA993B4EA988D8FDDC186FFB7DC90A6C08F4DF435C934063199FFFFFFFFFFFFFFFF";
        /*
        if (sp3.checkPrime(p)) {
            printer("p is a prime!");
        } else {
            printer("p is not prime!");
        }
        */
        printer("\n");
        String header = "3.2.2-1";
        if (sp3.probPrime(modulusString)) {
            printer("["+header+"    ANSWER] p is a prime (with "+CERTAINTY+" certainty)!");
        } else {
            printer("["+header+"    ANSWER] p is not a prime!");
        }
        header = "3.2.2-2";
        if (sp3.checkGenerator(modulusString, TWO)) {
            printer("["+header+"    ANSWER] g ("+TWO.toString()+") is a generator!");
        } else {
            printer("["+header+"    ANSWER] g ("+TWO.toString()+") is NOT a generator!");
        }
        header = "3.2.2-3";
        if (sp3.checkGenerator(modulusString, TWENTYTWO)) {
            printer("["+header+"    ANSWER] g ("+TWENTYTWO.toString()+") is a generator!");
        } else {
            printer("["+header+"    ANSWER] g ("+TWENTYTWO.toString()+") is NOT a generator!");
        }
        // 3.2.2 Diffie-Hellman
        // We know these global parameters:
        BigInteger g1 = TWO;
        BigInteger Xa = new BigInteger("954637821", 16);
        BigInteger Xb = new BigInteger("107965234", 10);
        BigInteger modulus = new BigInteger(modulusString, 16);
        header = "3.2.2-4";
        printer("\n\n");
        sp3.diffieHellman(header, g1, Xa, Xb, modulus);
        // 3.2.3 Elgamal encryption
        header = "3.2.3-1";
        printer("\n\n");
        // Basic checks - prime and generator?
        modulusString = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF6955817183995497CEA956AE515D2261898FA051015728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6BF12FFA06D98A0864D87602733EC86A64521F2B18177B200CBBE117577A615D6C770988C0BAD946E208E24FA074E5AB3143DB5BFCE0FD108E4B82D120A93AD2CAFFFFFFFFFFFFFFFF";
        if (sp3.probPrime(modulusString)) {
            printer("["+header+"    ANSWER] p is a prime (with "+CERTAINTY+" certainty)!");
        } else {
            printer("["+header+"    ANSWER] p is a prime!");
        }
        header = "3.2.3-2";
        if (sp3.checkGenerator(modulusString, TWO)) {
            printer("["+header+"    ANSWER] g ("+TWO.toString()+") is a generator!");
        } else {
            printer("["+header+"    ANSWER] g ("+TWO.toString()+") is NOT a generator!");
        }
        header = "3.2.3-3";
        if (sp3.checkGenerator(modulusString, THIRTYONE)) {
            printer("["+header+"    ANSWER] g ("+THIRTYONE.toString()+") is a generator!");
        } else {
            printer("["+header+"    ANSWER] g ("+THIRTYONE.toString()+") is NOT a generator!");
        }
        header = "3.2.3-4";
        BigInteger g2 = THIRTYONE;
        Xa = new BigInteger("f9e8d7c6b5a43210", 16);
        BigInteger k = new BigInteger("1234567890", 16);
        modulus = new BigInteger(modulusString, 16);
        sp3.elgamal(header, g2, Xa, Xb, k, modulus);

        // 3.2.4 Elliptic-curve Cryptosystem
        header = "3.2.4";
        printer("\n\n");
        // Simple test
        if (DEBUG) {
            printer("["+header+"      SIMPLE] Performing a simple test of Elliptic-curve cryptography...");
            BigInteger simpleA = new BigInteger("9", 10);
            BigInteger simpleB = new BigInteger("17", 10);
            BigInteger simpleP = new BigInteger("23", 10);
            // Alice's private key (nA)
            BigInteger simplenA = TEN;        
            BigInteger simpleGx = new BigInteger("16", 10);
            BigInteger simpleGy = new BigInteger("5", 10);
            BigInteger[] simplePa = sp3.eccCreatePublicKey(header, simpleA, simpleB, simpleP, simplenA, simpleGx, simpleGy);
        } else {
            // y^2 = x^3 + ax + b mod p where a = -3, and the following:
            BigInteger a = new BigInteger("-3", 10);
            BigInteger b = new BigInteger("b3312fa7e23ee7e4988e056be3f82d19181d9c6efe8141120314088f5013875ac656398d8a2ed19d2a85c8edd3ec2aef", 16);
            BigInteger p = new BigInteger("39402006196394479212279040100143613805079739270465446667948293404245721771496870329047266088258938001861606973112319", 10);
            BigInteger nA = new BigInteger("f9e8d7c6b5a43210", 16);
            BigInteger Gx = new BigInteger("aa87ca22be8b05378eb1c71ef320ad746e1d3b628ba79b9859f741e082542a385502f25dbf55296c3a545e3872760ab7", 16);
            BigInteger Gy = new BigInteger("3617de4a96262c6f5d9e98bf9292dc29f8f41dbd289a147ce9da3113b5f0b8c00a60b1ce1d7e819d7a431d7c90ea0e5f", 16);
            BigInteger[] Pa = sp3.eccCreatePublicKey(header, a, b, p, nA, Gx, Gy);
        }
	}
}