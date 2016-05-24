package org.joget.plugin.directory.model;

import java.io.Serializable;
import java.util.Date;

public class UserExtra implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8932311422964089000L;
	
	private String username;
    private String algorithm;
    private Integer loginAttempt;
    private Integer failedloginAttempt;
    private Date lastLogedInDate;
    private Date lastPasswordChangeDate;
    private Date lockOutDate;
    private Boolean requiredPasswordChange;
    private Boolean noPasswordExpiration;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public Integer getLoginAttempt() {
        return this.loginAttempt;
    }

    public void setLoginAttempt(Integer loginAttempt) {
        this.loginAttempt = loginAttempt;
    }

    public Integer getFailedloginAttempt() {
        return this.failedloginAttempt;
    }

    public void setFailedloginAttempt(Integer failedloginAttempt) {
        this.failedloginAttempt = failedloginAttempt;
    }

    public Date getLastLogedInDate() {
        return this.lastLogedInDate;
    }

    public void setLastLogedInDate(Date lastLogedInDate) {
        this.lastLogedInDate = lastLogedInDate;
    }

    public Date getLastPasswordChangeDate() {
        return this.lastPasswordChangeDate;
    }

    public void setLastPasswordChangeDate(Date lastPasswordChangeDate) {
        this.lastPasswordChangeDate = lastPasswordChangeDate;
    }

    public Date getLockOutDate() {
        return this.lockOutDate;
    }

    public void setLockOutDate(Date lockOutDate) {
        this.lockOutDate = lockOutDate;
    }

    public Boolean getRequiredPasswordChange() {
        return this.requiredPasswordChange;
    }

    public void setRequiredPasswordChange(Boolean requiredPasswordChange) {
        this.requiredPasswordChange = requiredPasswordChange;
    }

    public Boolean getNoPasswordExpiration() {
        return this.noPasswordExpiration;
    }

    public void setNoPasswordExpiration(Boolean noPasswordExpiration) {
        this.noPasswordExpiration = noPasswordExpiration;
    }
}
