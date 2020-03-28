import java.util.Random;

import javax.lang.model.util.ElementScanner6;

import java.lang.Math;
import java.math.BigInteger;

public class PrimitiveRoots {

    public static int GCD(int n1, int n2)
    {
        if (n2 != 0)
            return GCD(n2, n1 % n2);
        else
            return n1;
    }

    public void PrimRoots(int b) {
        int[] theSet;
        theSet = new int[b];
        // Then, loop from 1 to n-1
        // Then, see if each number and all of it power generates all numbers
        // We'll cheat by looking for a "1" and seeing if that one appears before the end
        int ctr = 0;
        int theSetLength = 0;
        // First, test for coprime (gcd(a,b) == 1)
        for ( int g = 1; g < b; g++ ) {
            if ( GCD(g,b) == 1 ) {
                System.out.println(g+" is coprime to "+b);
                theSet[theSetLength] = g;
                theSetLength++;
            } else {
                System.out.println(g+" is not coprime to "+b);
            }
        }
        System.out.println("The length of the set that are generator must generate is: "+theSetLength);
        // We want to test each number in the set to see if it's a generator
        // alpha starts at 2
        for ( int alphaIndex = 0; alphaIndex <= theSetLength-1; alphaIndex++ ) {
            ctr = 1;
            for ( int s = 2; s <= theSetLength; s++ ) {
                ctr++;
                //System.out.println("Checking "+Math.pow(alpha, s));
                //System.out.println("Calculating "+theSet[alphaIndex]+"^"+s+" modulo "+b+": "+(Math.pow(theSet[alphaIndex], s) % b)+"(ctr="+ctr+")");
                if ( (Math.pow(theSet[alphaIndex], s) % b) == 1 && ctr == theSetLength ) {
                    System.out.println (theSet[alphaIndex]+" is a generator!");
                } else if ( (Math.pow(theSet[alphaIndex], s) % b) == 1 ) {
                    // we're getting ready to circle back around but we're not a generator
                    break;
                }
            }
        }
    }

	public static void main(final String[] args) {
		System.out.println ("********** Project 3 output begins **********");
        final PrimitiveRoots proots = new PrimitiveRoots();
        proots.PrimRoots(25);
	}

}