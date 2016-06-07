package org.joget.plugin.enterprise;

import org.joget.apps.form.model.FormPermission;
import org.joget.apps.userview.model.UserviewPermission;
import org.joget.workflow.util.WorkflowUtil;

public class AdminUserviewPermission extends UserviewPermission implements FormPermission {

	public String getLabel() {
		return "Is Admin";
	}

	public String getClassName() {
		return this.getClass().getName();
	}

	public String getPropertyOptions() {
		return "";
	}

	public String getName() {
		return "Admin Userview Permission";
	}

	public String getVersion() {
		return "5.0.0";
	}

	public String getDescription() {
		return "";
	}

	@Override
	public boolean isAuthorize() {
		return !WorkflowUtil.isCurrentUserAnonymous() && WorkflowUtil.isCurrentUserInRole((String)"ROLE_ADMIN");
	}

}
