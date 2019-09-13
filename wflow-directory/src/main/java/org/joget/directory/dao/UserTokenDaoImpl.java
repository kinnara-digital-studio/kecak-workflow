package org.joget.directory.dao;

import java.util.Date;
import java.util.List;

import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.UserToken;
import org.joget.workflow.model.service.WorkflowUserManager;

public class UserTokenDaoImpl extends AbstractSpringDao implements UserTokenDao {

    public final static String ENTITY_NAME = "UserToken";

    private WorkflowUserManager workflowUserManager;


    public WorkflowUserManager getWorkflowUserManager() {
        return workflowUserManager;
    }

    public void setWorkflowUserManager(WorkflowUserManager workflowUserManager) {
        this.workflowUserManager = workflowUserManager;
    }

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

    public Boolean deleteUserToken(String id) {
        Boolean result = false;
        try {
            UserToken UserToken = getUserToken(id);
            if (UserToken != null) {
                delete(ENTITY_NAME, UserToken);
                result = true;
            }
        } catch (Exception e) {
            LogUtil.error(UserTokenDaoImpl.class.getName(), e, "Update UserToken Error!");
        }
        return result;
    }

    public UserToken getUserToken(String id) {
        UserToken result = null;
        try {
            result = (UserToken) find(ENTITY_NAME, id);
        } catch (Exception e) {
            LogUtil.error(UserTokenDaoImpl.class.getName(), e, "Get UserToken By Id Error!");
        }
        return result;
    }

    public UserToken getUserToken(String userId, String platformId) {
        UserToken result = null;
        try {
            UserToken userToken = new UserToken();
            userToken.setUserId(userId);
            userToken.setPlatformId(platformId);
            List userTokens = findByExample(ENTITY_NAME, userToken);
            if(userTokens.size() > 0) {
                result = (UserToken) userTokens.get(0);
            }
        } catch (Exception e) {
            LogUtil.error(UserTokenDaoImpl.class.getName(), e, "Get UserToken By Id Error!");
        }
        return result;
    }


    public Boolean deleteUserToken(UserToken userToken) {
        Boolean result = false;
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
