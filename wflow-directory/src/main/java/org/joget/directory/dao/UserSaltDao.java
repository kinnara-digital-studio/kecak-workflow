package org.joget.directory.dao;

import org.joget.directory.model.UserSalt;

public interface UserSaltDao {

	Boolean addUserSalt(UserSalt userSalt);

    Boolean updateUserSalt(UserSalt userSalt);

    Boolean deleteUserSalt(String id);
    
    Boolean deleteUserSaltByUserId(String userId);
    
    UserSalt getUserSalt(String id);

    UserSalt getUserSaltByUserId(String userId);
}
