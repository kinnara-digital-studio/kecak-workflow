package org.joget.directory.dao;

import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.UserToken;
import org.joget.workflow.model.service.WorkflowUserManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class UserTokenDaoImpl extends AbstractSpringDao<UserToken> implements UserTokenDao {

    public final static String ENTITY_NAME = "UserToken";

    private WorkflowUserManager workflowUserManager;


    public WorkflowUserManager getWorkflowUserManager() {
        return workflowUserManager;
    }

    public void setWorkflowUserManager(WorkflowUserManager workflowUserManager) {
        this.workflowUserManager = workflowUserManager;
    }

    @Override
    @Nonnull
    public Boolean addUserToken(UserToken UserToken) {
        try {
            String currentUsername = workflowUserManager.getCurrentUsername();
            Date currentDate = new Date();

            UserToken.setCreatedBy(currentUsername);
            UserToken.setModifiedBy(currentUsername);
            UserToken.setDateCreated(currentDate);
            UserToken.setDateModified(currentDate);
            save(ENTITY_NAME, UserToken);
            return true;
        } catch (Exception e) {
            LogUtil.error(UserTokenDaoImpl.class.getName(), e, "Add UserToken Error!");
            return false;
        }
    }

    @Override
    @Nonnull
    public Boolean updateUserToken(UserToken UserToken) {
        try {
            String currentUsername = workflowUserManager.getCurrentUsername();

            UserToken.setModifiedBy(currentUsername);
            UserToken.setDateModified(new Date());
            merge(ENTITY_NAME, UserToken);
            return true;
        } catch (Exception e) {
            LogUtil.error(UserTokenDaoImpl.class.getName(), e, "Update UserToken Error!");
            return false;
        }
    }

    @Override
    @Nonnull
    public Boolean deleteUserToken(String id) {
        Boolean result = false;
        try {
            UserToken UserToken = getUserTokenByUserId(id);
            if (UserToken != null) {
                delete(ENTITY_NAME, UserToken);
                result = true;
            }
        } catch (Exception e) {
            LogUtil.error(UserTokenDaoImpl.class.getName(), e, "Update UserToken Error!");
        }
        return result;
    }

    @Override
    @Nullable
    public UserToken getUserTokenByUserId(String userId) {
        UserToken result = null;
        try {
            result = (UserToken) find(ENTITY_NAME, userId);
        } catch (Exception e) {
            LogUtil.error(UserTokenDaoImpl.class.getName(), e, "Get UserToken By Id Error!");
        }
        return result;
    }

    @Override
    @Nullable
    public UserToken getUserTokenByUserId(String userId, String platformId) {
        UserToken result = null;
        try {
            UserToken userToken = new UserToken();
            userToken.setUserId(userId);
            userToken.setPlatformId(platformId);
            List userTokens = findByExample(ENTITY_NAME, userToken);
            if(userTokens != null && userTokens.size() > 0) {
                result = (UserToken) userTokens.get(0);
            }
        } catch (Exception e) {
            LogUtil.error(UserTokenDaoImpl.class.getName(), e, "Get UserToken By Id Error!");
        }
        return result;
    }

    @Override
    @Nullable
    public UserToken getUserTokenByExternalId(String externalId, String platformId) {
        Collection<UserToken> userTokens = find(ENTITY_NAME, "WHERE platformId = ? AND externalId = ?", new String[] {platformId, externalId}, "dateCreated", true, null, 1);
        return Optional.ofNullable(userTokens).map(Collection::stream).orElseGet(Stream::empty).findFirst().orElse(null);
    }

    @Override
    @Nonnull
    public Boolean deleteUserToken(UserToken userToken) {
        boolean result = false;
        try {
            List userTokens = findByExample(ENTITY_NAME, userToken);
            if(userTokens.size() > 0) {
                UserToken uToken = (UserToken) userTokens.get(0);
                delete(ENTITY_NAME,uToken);
                result = true;
            }
        } catch (Exception e) {
            LogUtil.error(UserTokenDaoImpl.class.getName(), e, "Delete UserToken Error!");
        }
        return result;
    }
}
