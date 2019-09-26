package org.joget.directory.dao;

import org.joget.directory.model.UserToken;

public interface UserTokenDao {

    Boolean addUserToken(UserToken userToken);

    Boolean updateUserToken(UserToken userToken);

    Boolean deleteUserToken(String id);

    UserToken getUserTokenByUserId(String userId);

    UserToken getUserTokenByUserId(String userId, String platformId);

    UserToken getUserTokenByExternalId(String externalId, String platformId);

    Boolean deleteUserToken(UserToken userToken);

}
