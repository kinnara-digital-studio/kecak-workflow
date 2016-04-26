package org.joget.commons.util;

import java.io.Serializable;

public class HashSalt implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -227345095994042394L;
	private String hash;
    private String salt;
    
    public HashSalt(String salt, String hash) {
        this.salt = salt;
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public String getSalt() {
        return salt;
    }
}
