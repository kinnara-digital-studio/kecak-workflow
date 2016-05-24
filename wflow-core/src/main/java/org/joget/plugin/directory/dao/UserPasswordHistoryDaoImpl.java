package org.joget.plugin.directory.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.directory.model.UserPasswordHistory;

public class UserPasswordHistoryDaoImpl extends AbstractSpringDao
implements UserPasswordHistoryDao {
	
    public Boolean addUserPasswordHistory(UserPasswordHistory password) {
        try {
            this.save("UserPasswordHistory", (Object)password);
            return true;
        }
        catch (Exception e) {
            LogUtil.error((String)UserPasswordHistory.class.getName(), (Throwable)e, (String)"Add User Password History!");
            return false;
        }
    }

    public UserPasswordHistory getUserPasswordHistory(String username, String password) {
        try {
            ArrayList<String> param = new ArrayList<String>();
            String condition = "where e.username = ? and e.password = ?";
            param.add(username);
            param.add(password);
            @SuppressWarnings("rawtypes")
			Collection passwords = this.find("UserPasswordHistory", condition, param.toArray(), "username", Boolean.valueOf(false), Integer.valueOf(0), Integer.valueOf(1));
            if (passwords != null && !passwords.isEmpty()) {
                return (UserPasswordHistory)passwords.iterator().next();
            }
        }
        catch (Exception e) {
            LogUtil.error((String)UserPasswordHistory.class.getName(), (Throwable)e, (String)"Get User Password History Error!");
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public Collection<UserPasswordHistory> findUserPasswordHistory(String username, Integer nPast) {
        try {
            ArrayList<String> param = new ArrayList<String>();
            String condition = "where e.username = ?";
            param.add(username);
			return this.find("UserPasswordHistory", condition, param.toArray(), "updatedDate", Boolean.valueOf(true), Integer.valueOf(0), nPast);
        }
        catch (Exception e) {
            LogUtil.error((String)UserPasswordHistory.class.getName(), (Throwable)e, (String)"Find User Password History Error!");
            return null;
        }
    }

    public Boolean deleteUserPasswordHistory(String username) {
        try {
            ArrayList<String> param = new ArrayList<String>();
            String condition = "where e.username = ?";
            param.add(username);
            @SuppressWarnings("unchecked")
			Collection<UserPasswordHistory> passwords = this.find("UserPasswordHistory", condition, param.toArray(), "updatedDate", Boolean.valueOf(true), null, null);
            if (passwords != null && !passwords.isEmpty()) {
                for (UserPasswordHistory password : passwords) {
                    this.delete("UserPasswordHistory", (Object)password);
                }
            }
        }
        catch (Exception e) {
            LogUtil.error((String)UserPasswordHistory.class.getName(), (Throwable)e, (String)"Delete User Password History Error!");
            return false;
        }
        return true;
    }

    public UserPasswordHistory getUserPasswordHistoryById(String id) {
        return (UserPasswordHistory)this.find("UserPasswordHistory", id);
    }

    public Boolean deleteUserPasswordHistoryById(String id) {
        UserPasswordHistory p = this.getUserPasswordHistoryById(id);
        if (p != null) {
            this.delete("UserPasswordHistory", (Object)p);
            return true;
        }
        return false;
    }
}
