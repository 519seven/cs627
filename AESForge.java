import java.io.*;
import java.util.*;

/**
 * @author Xunhua Wang (wangxx@jmu.edu)
 * @date 09/27/2014; revised on 02/22/2015; further revised on 04/03/2015, 09/23/2015, 09/24/2016
 * All rights reserved
 * 20-Feb-2020 Altered by Peter Akey for Subtask4 Small Project 1
 */

public class AESForge {
	public void testAESImplementationInCBC () {
        	try {
			// This time we know the key
			String theSolutionKey = "503B73E0C60000000000000000000003";
			byte[] inKey = new byte[16];
			for (int i = 0; i < theSolutionKey.length(); i +=2) inKey[i/2] = hexToByte(theSolutionKey.substring(i, i+2));
			String theNewIV = "67c720b72a53b4bf9733732fad997119"; // 32
			byte[] inIV = new byte[16];
			for (int i = 0; i < theNewIV.length(); i +=2) inIV[i/2] = hexToByte(theNewIV.substring(i, i+2));
			String textString = "Transfer fifty thousand dollars from my bank account to Jane Doe"; // exactly 64
			byte[] inText = textString.getBytes();	// This will return the ASCII encoding of the characters
			int numOfBlocks = inText.length / 16; 	// Each AES block has 16 bytes
			Object roundKeys = Rijndael_Algorithm.makeKey (Rijndael_Algorithm.ENCRYPT_MODE, inKey); // This creates the round keys
			byte[] cipherText = new byte[inIV.length + inText.length];
			byte[] feedback = Arrays.copyOf (inIV, inIV.length);
			for (int i = 0; i < 16; i++) cipherText[i] = inIV[i];
			byte[] currentBlock = new byte[16];
			for (int i = 0 ; i < numOfBlocks; i++) {
				for (int j=0; j < 16; j++) currentBlock[j] = (byte) (inText[i*16 + j] ^ feedback[j]); // CBC feedback
				byte[] thisCipherBlock = Rijndael_Algorithm.blockEncrypt2 (currentBlock, 0, roundKeys);
				feedback = Arrays.copyOf (thisCipherBlock, thisCipherBlock.length);
				for (int j=0; j < 16; j++) cipherText[(i+1)*16 + j] = thisCipherBlock[j];
			}
			System.out.println ("Ciphertext (including IV) is " + convertToString (cipherText));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public byte hexToByte(String hexString) {
		int firstDigit = toDigit(hexString.charAt(0));
		int secondDigit = toDigit(hexString.charAt(1));
		return (byte) ((firstDigit << 4) + secondDigit);
	}
	private int toDigit(char hexChar) {
		int digit = Character.digit(hexChar, 16);
		if(digit == -1) throw new IllegalArgumentException("Invalid Hexadecimal Character: "+ hexChar);
		return digit;
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
			AESForge aes = new AESForge();
			aes.testAESImplementationInCBC ();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
