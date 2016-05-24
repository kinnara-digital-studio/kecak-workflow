package org.joget.plugin.directory.model;

import java.io.Serializable;
import java.util.Date;

public class UserPasswordHistory implements Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 9122891688018002798L;
	
	private String id;
    private String username;
    private String salt;
    private Date updatedDate;
    private String password;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSalt() {
        return this.salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Date getUpdatedDate() {
        return this.updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
