package org.joget.commons.util;

import java.io.Serializable;

public class PasswordSalt implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4383413137905377042L;
	private String salt;
    private String password;
    
    public PasswordSalt(String salt, String password) {
        this.salt = salt;
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public String getPassword() {
        return password;
    }
}
