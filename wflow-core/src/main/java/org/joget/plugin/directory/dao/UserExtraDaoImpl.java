package org.joget.plugin.directory.dao;

import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.directory.dao.UserExtraDao;
import org.joget.plugin.directory.model.UserExtra;

public class UserExtraDaoImpl extends AbstractSpringDao implements UserExtraDao {
	
	public Boolean addUserExtra(UserExtra user) {
		try {
			this.save("UserExtra", (Object) user);
			return true;
		} catch (Exception e) {
			LogUtil.error((String) UserExtraDaoImpl.class.getName(), (Throwable) e, (String) "Add User Extra Error!");
			return false;
		}
	}

	public Boolean updateUserExtra(UserExtra user) {
		try {
			this.merge("UserExtra", (Object) user);
			return true;
		} catch (Exception e) {
			LogUtil.error((String) UserExtraDaoImpl.class.getName(), (Throwable) e,
					(String) "Update User Extra Error!");
			return false;
		}
	}

	public Boolean deleteUserExtra(String username) {
		try {
			UserExtra user = this.getUserExtra(username);
			this.delete("UserExtra", (Object) user);
			return true;
		} catch (Exception e) {
			LogUtil.error((String) UserExtraDaoImpl.class.getName(), (Throwable) e,
					(String) "Delete User Extra Error!");
			return false;
		}
	}

	public UserExtra getUserExtra(String username) {
		try {
			return (UserExtra) this.find("UserExtra", username);
		} catch (Exception e) {
			LogUtil.error((String) UserExtraDaoImpl.class.getName(), (Throwable) e, (String) "Get User Extra Error!");
			return null;
		}
	}
}
