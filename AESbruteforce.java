import java.io.*;
import java.util.*;
import org.apache.commons.lang3.*;
import java.math.BigInteger;
import java.time.Instant;
import java.text.SimpleDateFormat;

/**
 * @author Xunhua Wang (wangxx@jmu.edu)
 * @date 09/27/2014; revised on 02/22/2015; further revised on 04/03/2015, 09/23/2015, 09/24/2016
 * All rights reserved
 * Change Log: 17-Feb-2020	Peter Akey
 * 			   Altered code to brute-force when knowing the IV and 71% of the key
 */

public class AESbruteforce {
	public void testAESImplementationInCBC (String[] commandLineArgs) {
		if (commandLineArgs.length == 0) {
			System.out.println("Two poistional arguments - beginning decimal and ending decimal - are required.");
			System.out.println("Usage: <script> <begin> <end> where begin < end");
			System.exit(0);
		}
		int modVal = 100000;
		long begin = (long)Long.valueOf(commandLineArgs[0]);
		long end = (long)Long.valueOf(commandLineArgs[1]);
	   	try {
			boolean writeFile = true;
			// Creating a file object that represents the disk file
			String logFileName = String.valueOf(begin);
			if (begin == 0) {
				//Date date = new Date(Instant.now().toEpochMilli());
				SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmssZ", Locale.US);
				logFileName = format.format(new Date(Instant.now().toEpochMilli()));
			}
			PrintStream o = new PrintStream(new File(logFileName+"_output.log"));
			// Store current System.out before assigning a new value
			PrintStream console = System.out;
			// Assign o to output stream
			System.setOut(o);
			// ^^^ that ought to send output to a file
			byte[] inKey = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
							(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
							(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03};
			long known = 3 << 25;
			for (long i = begin; i <= end; i++) {  // this upper bound ought to be 38
				long first_half = known + (i << 27);
				if (i % modVal == 0) {
					System.out.println("known: " + known + " | i: " + i);
					System.out.println(String.format("first_half: %s", Long.toBinaryString(first_half)).replaceAll(" ", "0"));
				}
				// Populate the first half of the byte array (the high 64 bits)
				for (int j = 7; j >= 0; j--) {
					inKey[j] = (byte)(first_half & 0xFF);
					first_half >>= 8;
				}
				if (i % modVal == 0) { System.out.println("Byte array: "+convertToString(inKey)); }
				// This is our hex string.  It includes the IV
				String cipherStringWithIV = "C75C203A72099A6D24B306761469D00B"
										   +"49F8DCD23D629605488681864C6580F3"
										   +"388BC75834EF02DD3F71B2D97FB24D64"
										   +"0619EA2E0506477989EC170FB2881917"
										   +"1C6CA15BF37549D71F85C53EAF5CA0E0";
				// Create a byte array to hold the hex values
				byte[] cipherHexWithIV = new byte[cipherStringWithIV.length() / 2];
				// Split the cipher text into equal parts and hold in an array
				int ctr1;
				for (ctr1 = 0; ctr1 < cipherHexWithIV.length; ctr1++) {
					int index = ctr1 * 2;
					int j = Integer.parseInt(cipherStringWithIV.substring(index, index + 2), 16);
					cipherHexWithIV[ctr1] = (byte) j;
				 }
				if (i % modVal == 0) { System.out.println ("Ciphertext (including IV) is " + convertToString (cipherHexWithIV)); }
				Object decryptRoundKeys = Rijndael_Algorithm.makeKey (Rijndael_Algorithm.DECRYPT_MODE, inKey); // 
				int numOfCiphertextBlocks = cipherHexWithIV.length / 16 - 1; // Each block has 16 bytes and we need to exclude the IV
				byte[] cleartextBlocks = new byte[numOfCiphertextBlocks * 16];
				byte[] receivedIV = new byte[16];
				for (ctr1 = 0; ctr1 < 16; ctr1++) { receivedIV[ctr1] = cipherHexWithIV[ctr1]; }
				if (i % modVal == 0) { System.out.println(convertToString(receivedIV)); }
				byte[] currentDecryptionBlock = new byte[16];

				for (ctr1=0; ctr1 < numOfCiphertextBlocks; ctr1++) {
					// Note that the first block is the IV
					// Isolate the block that will be decrypted into its own byte array
					for (int j=0; j < 16; j++) currentDecryptionBlock[j] = cipherHexWithIV[(ctr1+1)*16 + j];
					byte[] thisDecryptedBlock = Rijndael_Algorithm.blockDecrypt2 (currentDecryptionBlock, 0, decryptRoundKeys);
					for (int j=0; j < 16; j++) {
						 cleartextBlocks[ctr1*16+j] = (byte)(thisDecryptedBlock[j] ^ cipherHexWithIV[ctr1*16 + j]);
					}
				}
				String recoveredString = new String (cleartextBlocks);
				try{ 
					for(int bCtr = 0, n = recoveredString.length() ; bCtr < n ; bCtr++) { 
						if ((int)recoveredString.charAt(bCtr) > 127 || (int)recoveredString.charAt(bCtr) < 32) {
							writeFile = false;
							break;
						}
					}
					if (writeFile == true) {
						System.out.println("MATCH");
						FileWriter fstream = new FileWriter ("./key_"+i+"output.txt");
						BufferedWriter results = new BufferedWriter(fstream);
						results.write(convertToString(inKey));
						results.newLine();
						results.write(recoveredString);
						results.newLine();
						results.close();
					}
					writeFile = true;
				} catch(Exception ex) {
					if (i % modVal == 0) {
						System.out.println("A write error has occurred");
						System.out.println("My key: "+convertToString(inKey));
						System.out.println(recoveredString);
					}
					ex.printStackTrace();
				}
				if ((i % modVal) == 0) {
					System.out.println(i);
				}
			} // end for loop for full range
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String convertToString (byte[] data) {
		char[] _hexArray = {'0', '1', '2', '3', '4', '5','6', '7', '8',
			    '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		StringBuffer sb = new StringBuffer();
		for (int i=0; i <data.length; i++) {
			sb.append("" + _hexArray[(data[i] >> 4) & 0x0f] + _hexArray[data[i] & 0x0f]);
		}
		return sb.toString();
	}

	// Split string every nth character where n = partitionSize
	private static List<String> getParts(String string, int partitionSize) {
        	List<String> parts = new ArrayList<String>();
        	int len = string.length();
        	for (int i=0; i<len; i+=partitionSize) { parts.add(string.substring(i, Math.min(len, i + partitionSize))); }
        	return parts;
	}
	
	public static void main (String[] args) {
		try {
			AESbruteforce aes = new AESbruteforce();
			aes.testAESImplementationInCBC (args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
