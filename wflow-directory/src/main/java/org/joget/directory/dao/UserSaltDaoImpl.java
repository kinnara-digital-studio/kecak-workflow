package org.joget.directory.dao;

import java.util.Date;
import java.util.List;

import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.UserSalt;
import org.joget.workflow.model.service.WorkflowUserManager;

public class UserSaltDaoImpl extends AbstractSpringDao implements UserSaltDao {

	private WorkflowUserManager workflowUserManager;
	
	
	public WorkflowUserManager getWorkflowUserManager() {
		return workflowUserManager;
	}

	public void setWorkflowUserManager(WorkflowUserManager workflowUserManager) {
		this.workflowUserManager = workflowUserManager;
	}

	public Boolean addUserSalt(UserSalt userSalt) {
		try {
			String currentUsername = workflowUserManager.getCurrentUsername();
        	Date currentDate = new Date();
        	
        	userSalt.setCreatedBy(currentUsername);
        	userSalt.setModifiedBy(currentUsername);
        	userSalt.setDateCreated(currentDate);
        	userSalt.setDateModified(currentDate);
			save("UserSalt", userSalt);
			return true;
		} catch (Exception e) {
			LogUtil.error(UserSaltDaoImpl.class.getName(), e, "Add UserSalt Error!");
			return false;
		}
	}

	public Boolean updateUserSalt(UserSalt userSalt) {
		try {
			String currentUsername = workflowUserManager.getCurrentUsername();

        	userSalt.setModifiedBy(currentUsername);
        	userSalt.setDateModified(new Date());
			merge("UserSalt", userSalt);
			return true;
		} catch (Exception e) {
			LogUtil.error(UserSaltDaoImpl.class.getName(), e, "Update UserSalt Error!");
			return false;
		}
	}

	public Boolean deleteUserSalt(String id) {
		Boolean result = false;
		try {
			UserSalt userSalt = getUserSalt(id);
			if (userSalt != null) {
				delete("UserSalt", userSalt);
				result = true;
			}
		} catch (Exception e) {
			LogUtil.error(UserSaltDaoImpl.class.getName(), e, "Update UserSalt Error!");
		}
		return result;
	}

	public UserSalt getUserSalt(String id) {
		UserSalt result = null;
		try {
			result = (UserSalt) find("UserSalt", id);
		} catch (Exception e) {
			LogUtil.error(UserSaltDaoImpl.class.getName(), e, "Get UserSalt By Id Error!");
		}
		return result;
	}

	public UserSalt getUserSaltByUserId(String userId) {
		try {
			UserSalt userSalt = new UserSalt();
			userSalt.setUserId(userId);
			@SuppressWarnings("rawtypes")
			List list = findByExample("UserSalt", userSalt);

			if (list.size() > 0) {
				return (UserSalt) list.get(0);
			}
		} catch (Exception e) {
			LogUtil.error(UserSaltDaoImpl.class.getName(), e, "Get UserSalt By UserId Error!");
		}

		return null;
	}

}
