package com.webapp.helpers;

import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Provide functionality for hashing & salting the password.
 * @author Sairam Krishnan
 */
public class ProtectPassword {
	
	/**
	 * @param saltSet -> list that will contain the set of characters
	 *                   used to generate the salt
	 */
	private static void getSaltCharset(List<Character> saltSet) {
		char base;

		for(base=48; base<=57; base++)
			saltSet.add(base);
		for(base=65; base<=91; base++)
			saltSet.add(base);
		for(base=97; base<=113; base++)
			saltSet.add(base);
	}
	
	/**
	 * @return a 64-character long salt, which will be 
	 *         hashed & stored w/ password
	 */
	public static String generateSalt() {
		List<Character> saltSet = new ArrayList<Character>();
		int count = 0;
		String salt = "";
		
		//Add set of characters that will be used to generate
	    //salt into saltSet.
		getSaltCharset(saltSet);
		
		//Generate a 64-character long salt. Use a random #
		//generator to randomly pick a character in saltSet,
		//and append this character to the salt.
		while(count < 64) {
			int index = (int)(Math.random() * saltSet.size());
			salt += saltSet.get(index);
			count++;
		}
		
		return salt;
	}
	
	/**
	 * Generate a one-way hash of given password & salt.
	 * @param p  Password
	 * @param s  Salt
	 */
	public static String hash(String p, String s) {
		String hash = null;
		String combined = p + s;
		
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			hash = new String(md.digest(combined.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return hash;
	}
}
