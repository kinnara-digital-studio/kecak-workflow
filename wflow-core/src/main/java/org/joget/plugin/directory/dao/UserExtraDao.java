package org.joget.plugin.directory.dao;

import org.joget.plugin.directory.model.UserExtra;

public interface UserExtraDao {
    public Boolean addUserExtra(UserExtra var1);

    public Boolean updateUserExtra(UserExtra var1);

    public Boolean deleteUserExtra(String var1);

    public UserExtra getUserExtra(String var1);
}
