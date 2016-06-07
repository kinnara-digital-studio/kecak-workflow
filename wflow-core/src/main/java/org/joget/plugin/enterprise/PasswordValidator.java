package org.joget.plugin.enterprise;

import java.util.Collection;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormValidator;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.ResourceBundleUtil;
import org.joget.directory.model.service.DirectoryUtil;
import org.joget.directory.model.service.UserSecurity;

public class PasswordValidator extends FormValidator {

	public String getLabel() {
		return "Password Validator";
	}

	public String getClassName() {
		return this.getClass().getName();
	}

	public String getPropertyOptions() {
		String extra = "";
        UserSecurity us = DirectoryUtil.getUserSecurity();
        if (us != null) {
            extra = ",{\n        name : 'usernameField',\n        label : '@@form.passwordvalidator.usernameField@@',\n        type : 'textfield',\n        required : 'true'\n    },\n    {\n        name : 'currentPasswordField',\n        label : '@@form.passwordvalidator.currentPasswordField@@',\n        type : 'textfield'\n    },\n    {\n        name:'showPasswordPolicy',\n        label:'@@form.passwordvalidator.showPasswordPolicy@@',\n        type:'checkbox',\n        value:'true',\n        options:[\n            {\n                value:'true',\n                label:''\n            }\n        ]\n     }";
        }
        return AppUtil.readPluginResource((String)this.getClass().getName(), (String)"/properties/form/passwordValidator.json", (Object[])new Object[]{extra}, (boolean)true, (String)null);
	}

	public String getName() {
		return "Password Validator";
	}

	public String getVersion() {
		 return "5.0.0";
	}

	public String getDescription() {
		return "Validate password format.";
	}

	@Override
	public boolean validate(Element element, FormData data, String[] values) {
		String[] confirmPasswordValues;
        Form form;
        String confirmPasswordField;
        Element confirmPassword;
        Boolean result = true;
        String id = FormUtil.getElementParameterName((Element)element);
        String label = element.getPropertyString("label");
        if (label.isEmpty()) {
            label = ResourceBundleUtil.getMessage((String)"form.passwordvalidator.password");
        }
        if ("true".equals(this.getPropertyString("mandatory")) && (values == null || values.length == 0 || values != null && values.length > 0 && values[0].isEmpty())) {
            result = false;
            data.addFormError(id, ResourceBundleUtil.getMessage((String)"form.passwordvalidator.error.mandatory", (Object[])new String[]{label}));
        }
        if (values != null && values.length > 0 && (confirmPasswordField = this.getPropertyString("confirmPasswordField")) != null && !confirmPasswordField.isEmpty() && (confirmPassword = FormUtil.findElement((String)confirmPasswordField, (Element)(form = FormUtil.findRootForm((Element)element)), (FormData)data)) != null && (confirmPasswordValues = FormUtil.getElementPropertyValues((Element)confirmPassword, (FormData)data)) != null && confirmPasswordValues.length > 0) {
            String confirmPasswordValue = confirmPasswordValues[0];
            String passwordValue = values[0];
            UserSecurity us = DirectoryUtil.getUserSecurity();
            if (us != null) {
                Element currentPasswordEl;
                String[] usernameValues;
                @SuppressWarnings("unused")
				String currentPasswordField;
                Collection<String> errors;
                String[] currentPasswordValues;
                String username = "";
                String currentPassword = "";
                String usernameField = this.getPropertyString("usernameField");
                Element usernameEl = FormUtil.findElement((String)usernameField, (Element)form, (FormData)data);
                if (usernameEl != null && (usernameValues = FormUtil.getElementPropertyValues((Element)usernameEl, (FormData)data)) != null && usernameValues.length > 0) {
                    username = usernameValues[0];
                }
                if ((currentPasswordEl = FormUtil.findElement((String)(currentPasswordField = this.getPropertyString("currentPasswordField")), (Element)form, (FormData)data)) != null && (currentPasswordValues = FormUtil.getElementPropertyValues((Element)currentPasswordEl, (FormData)data)) != null && currentPasswordValues.length > 0) {
                    currentPassword = currentPasswordValues[0];
                }
                if ((errors = us.validatePassword(username, currentPassword, passwordValue, confirmPasswordValue)) != null && !errors.isEmpty()) {
                    result = false;
                    String errorMsg = "";
                    for (String error : errors) {
                        errorMsg = errorMsg + error + "<br/>";
                    }
                    data.addFormError(id, errorMsg);
                }
            } else if (!confirmPasswordValue.equals(passwordValue)) {
                result = false;
                String cpLabel = confirmPassword.getPropertyString("label");
                if (cpLabel.isEmpty()) {
                    cpLabel = ResourceBundleUtil.getMessage((String)"form.passwordvalidator.confirmPassword");
                }
                data.addFormError(id, ResourceBundleUtil.getMessage((String)"form.passwordvalidator.error.passwordNotMatch", (Object[])new String[]{label, cpLabel}));
            }
        }
        return result;
	}

}
