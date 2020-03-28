import java.util.Random;

import javax.lang.model.util.ElementScanner6;

import java.lang.Math;
import java.math.BigInteger;

public class DiffieHellman {

    BigInteger ZERO = new BigInteger("0", 2);
    BigInteger ONE = new BigInteger("1", 2);
    static BigInteger TWO = new BigInteger("10", 2);
    BigInteger THREE = new BigInteger("11", 2);
    BigInteger FIVE = new BigInteger("101", 2);
    BigInteger SIX = new BigInteger("110", 2);
    BigInteger TEN = new BigInteger("1010", 2); // 10

    public static int GCD(int n1, int n2)
    {
        if (n2 != 0)
            return GCD(n2, n1 % n2);
        else
            return n1;
    }

    public boolean checkGenerator(String b, BigInteger g) {
        BigInteger p = new BigInteger(b, 16);
        BigInteger q = p.subtract(ONE).divide(TWO);
        if (checkPrime(q.toString(16))) {
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
            System.out.println(n.toString(16) + " is not prime");
            return false;
        }
        BigInteger top = n.divide(TWO);
        for(BigInteger i = new BigInteger("3", 10); i.compareTo(top.subtract(ONE)) < 0; i.add(SIX)){
            if (n.mod(i).compareTo(ZERO) == 0){
                System.out.println(n + " is not prime");
                return false;
            }
        }
        System.out.println(n + " is prime");
        return true; 
    }

	public static void main(final String[] args) {
		System.out.println ("********** Project 3 output begins **********");
        final DiffieHellman dh = new DiffieHellman();
        String p = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF6955817183995497CEA956AE515D2261898FA051015728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6BF12FFA06D98A0864D87602733EC86A64521F2B18177B200CBBE117577A615D6C770988C0BAD946E208E24FA074E5AB3143DB5BFCE0FD108E4B82D120A92108011A723C12A787E6D788719A10BDBA5B2699C327186AF4E23C1A946834B6150BDA2583E9CA2AD44CE8DBBBC2DB04DE8EF92E8EFC141FBECAA6287C59474E6BC05D99B2964FA090C3A2233BA186515BE7ED1F612970CEE2D7AFB81BDD762170481CD0069127D5B05AA993B4EA988D8FDDC186FFB7DC90A6C08F4DF435C934063199FFFFFFFFFFFFFFFF";
        if (dh.checkPrime(p)) {
            System.out.println("p is a prime!");
        } else {
            System.out.println("p is not prime!");
        }
        if (dh.checkGenerator(p, TWO)) {
            System.out.println("g is a generator!");
        }
	}

}