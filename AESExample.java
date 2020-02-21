import java.io.*;
import java.util.*;

/**
 * @author Xunhua Wang (wangxx@jmu.edu)
 * @date 09/27/2014; revised on 02/22/2015; further revised on 04/03/2015, 09/23/2015, 09/24/2016
 * All rights reserved
 */

public class AESExample {

	public void testAESImplementationInCBC () {
        	try {
			byte[] inKey = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
					(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
					(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xFF};
			byte[] cbcIV = {(byte) 0x98, (byte) 0x76, (byte) 0x54, (byte) 0x32, (byte) 0x10, (byte) 0xFE,
					(byte) 0xDC, (byte) 0xBA, (byte) 0x98, (byte) 0x76, (byte) 0x54, (byte) 0x32,
					(byte) 0x10, (byte) 0xFE, (byte) 0xDC, (byte) 0xBA};

			// populate the plaintext
			String textString = "abcdefghijklmnopqrstuvwxyz012345"; // exactly 32 bytes, two blocks of data

			byte[] inText = textString.getBytes();		    // This will return the ASCII encoding of the characters
			int numOfBlocks = inText.length / 16; 		// Each AES block has 16 bytes

			Object roundKeys = Rijndael_Algorithm.makeKey (Rijndael_Algorithm.ENCRYPT_MODE, inKey); // This creates the round keys

			// Now, we are ready and let's start the business
			System.out.println (System.getProperty ("line.separator") + "Encrypting ......");
			System.out.println ("Plaintext is " + textString);
			System.out.println ("IV is " + convertToString (cbcIV));
			System.out.println ("Key is " + convertToString (inKey));

			byte[] cipherText = new byte[cbcIV.length + inText.length];
			byte[] feedback = Arrays.copyOf (cbcIV, cbcIV.length);
			for (int i = 0; i < 16; i++) cipherText[i] = cbcIV[i];
			byte[] currentBlock = new byte[16];

			for (int i = 0 ; i < numOfBlocks; i++) {
				for (int j=0; j < 16; j++) currentBlock[j] = (byte) (inText[i*16 + j] ^ feedback[j]); // CBC feedback

				byte[] thisCipherBlock = Rijndael_Algorithm.blockEncrypt2 (currentBlock, 0, roundKeys);

				feedback = Arrays.copyOf (thisCipherBlock, thisCipherBlock.length);

				for (int j=0; j < 16; j++) cipherText[(i+1)*16 + j] = thisCipherBlock[j];
			}

			System.out.println ("Ciphertext (including IV) is " + convertToString (cipherText));

			//
			// If you receive the ciphertext, assuming that you have the same symmetric key, how will you decrypt?
			// Below, you only have inKey and cipherText
			//
			System.out.println (System.getProperty ("line.separator") + "Decrypting ......");
                	Object decryptRoundKeys = Rijndael_Algorithm.makeKey (Rijndael_Algorithm.DECRYPT_MODE, inKey); // 
			int numOfCiphertextBlocks = cipherText.length / 16 - 1; // Each AES block has 16 bytes and we need to exclude the IV
			byte[] cleartextBlocks = new byte[numOfCiphertextBlocks * 16];

			byte[] receivedIV = new byte[16];
			for (int i = 0; i < 16; i++) receivedIV[i] = cipherText[i];
			byte[] currentDecryptionBlock = new byte[16];

			for (int i=0; i < numOfCiphertextBlocks; i++) {
				for (int j=0; j < 16; j++) currentDecryptionBlock [j] = cipherText[(i+1)*16 + j]; // Note that the first block is the IV

				byte[] thisDecryptedBlock = Rijndael_Algorithm.blockDecrypt2 (currentDecryptionBlock, 0, decryptRoundKeys);
			
				for (int j=0; j < 16; j++) cleartextBlocks[i*16+j] =  (byte) (thisDecryptedBlock[j] ^ cipherText[i*16 + j]);
			}

			String recoveredString = new String (cleartextBlocks);
			if (!recoveredString.equals (textString)) {
            			System.out.println ("Decryption does NOT work!");
				System.out.println ("Recovered: " + recoveredString);
				System.out.println ("Original: " + textString);
			} else {
				System.out.println ("Recovered cleartext is " + recoveredString);
				System.out.println ("Decryption worked beautifully and recovered the original plaintext!");
			}

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

	public static void main (String[] args) {
		try {
			AESExample aes = new AESExample();

			aes.testAESImplementationInCBC ();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
