package org.joget.directory.dao;

import org.joget.directory.model.UserToken;

public interface UserTokenDao {

    Boolean addUserToken(UserToken userToken);

    Boolean updateUserToken(UserToken userToken);

    Boolean deleteUserToken(String id);

    UserToken getUserToken(String id);

    Boolean deleteUserToken(UserToken userToken);
}
