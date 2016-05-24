package org.joget.plugin.directory.dao;

import java.util.Collection;
import org.joget.plugin.directory.model.UserPasswordHistory;

public interface UserPasswordHistoryDao {
	
    public Boolean addUserPasswordHistory(UserPasswordHistory var1);

    public UserPasswordHistory getUserPasswordHistory(String var1, String var2);

    public UserPasswordHistory getUserPasswordHistoryById(String var1);

    public Collection<UserPasswordHistory> findUserPasswordHistory(String var1, Integer var2);

    public Boolean deleteUserPasswordHistory(String var1);

    public Boolean deleteUserPasswordHistoryById(String var1);
}
