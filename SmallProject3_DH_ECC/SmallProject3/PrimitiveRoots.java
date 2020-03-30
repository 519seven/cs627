import java.util.Random;

import org.apache.commons.cli.*;
import javax.lang.model.util.ElementScanner6;
import java.lang.Math;
import java.math.BigInteger;
import java.util.Arrays;

public class PrimitiveRoots {

    public static Boolean DEBUG = false;

    public static int GCD(int n1, int n2)
    {
        if (n2 != 0)
            return GCD(n2, n1 % n2);
        else
            return n1;
    }


    public static void printer(String s) {
        System.out.println(s);
    }


    public void PrimRoots(String header, int b) {
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
                if (DEBUG) { printer("["+header+"     DEBUG] "+g+" is coprime to "+b); }
                theSet[theSetLength] = g;
                theSetLength++;
            } else {
                if (DEBUG) { printer("["+header+"     DEBUG] "+g+" is not coprime to "+b); }
            }
        }
        printer("["+header+"      INFO] The length of the set that are generator must generate is: "+theSetLength);
        // We want to test each number in the set to see if it's a generator
        // alpha starts at 2
        for ( int alphaIndex = 0; alphaIndex <= theSetLength-1; alphaIndex++ ) {
            ctr = 1;
            for ( int s = 2; s <= theSetLength; s++ ) {
                ctr++;
                //System.out.println("Checking "+Math.pow(alpha, s));
                //System.out.println("Calculating "+theSet[alphaIndex]+"^"+s+" modulo "+b+": "+(Math.pow(theSet[alphaIndex], s) % b)+"(ctr="+ctr+")");
                if ( (Math.pow(theSet[alphaIndex], s) % b) == 1 && ctr == theSetLength ) {
                    printer("["+header+"    ANSWER] "+theSet[alphaIndex]+" is a generator!");
                } else if ( (Math.pow(theSet[alphaIndex], s) % b) == 1 ) {
                    // we're getting ready to circle back around but we're not a generator
                    break;
                }
            }
        }
        if (DEBUG) { 
            printer("["+header+"     DEBUG] The set that the generators have to generate (minus the \"0\") is: ");
            printer("["+header+"     DEBUG] "+Arrays.toString(theSet));
        }
    }

	public static void main(final String[] args) {
		System.out.println ("\n\n[        ATTENTION] Small Project 3 begins!");
        final PrimitiveRoots proots = new PrimitiveRoots();
        Options options = new Options();

        Option input = new Option("d", "debug", true, "Enable verbose output");
        input.setRequired(false);
        options.addOption(input);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            try {
                DEBUG = Boolean.parseBoolean(cmd.getOptionValue("debug"));
                printer("[             INFO] Setting DEBUG to "+Boolean.toString(DEBUG)+"\n");
            } catch (IndexOutOfBoundsException i) {
                System.out.println(i.getMessage());
                formatter.printHelp("utility-name", options);
                System.exit(1);
            }
        } catch (ParseException e) {
            //
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
        String header = "3.2.1-1";
        proots.PrimRoots(header, 25);
	}

}