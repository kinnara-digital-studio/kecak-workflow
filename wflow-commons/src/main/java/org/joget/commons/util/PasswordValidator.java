package org.joget.commons.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {

	private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%]).{8,20})";

	/**
	 * Validate password with regular expression
	 * 
	 * @param password
	 *            password for validation
	 * @return true valid password, false invalid password
	 */
	public static boolean validate(String password) {
		Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}
	
	public static boolean isPasswordEmpty(String password) {
		boolean result = false;
		if (password == null) {
			result = true;
		} else {
			if (password.trim().isEmpty()) {
				result = true;
			}
		}
		
		return result;
	}
}
