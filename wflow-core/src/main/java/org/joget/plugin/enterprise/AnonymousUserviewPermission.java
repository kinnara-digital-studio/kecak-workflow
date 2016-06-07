package org.joget.plugin.enterprise;

import org.joget.apps.form.model.FormPermission;
import org.joget.apps.userview.model.UserviewPermission;

public class AnonymousUserviewPermission extends UserviewPermission implements FormPermission {

	public String getLabel() {
		return "Is Anonymous";
	}

	public String getClassName() {
		return this.getClass().getName();
	}

	public String getPropertyOptions() {
		return "";
	}

	public String getName() {
		return "Anonymous Userview Permission";
	}

	public String getVersion() {
		return "5.0.0";
	}

	public String getDescription() {
		return "";
	}

	@Override
	public boolean isAuthorize() {
		 return this.getCurrentUser() == null;
	}

}
