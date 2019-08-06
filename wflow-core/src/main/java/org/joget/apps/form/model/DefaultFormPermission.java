package org.joget.apps.form.model;

import org.joget.apps.userview.model.UserviewPermission;

/**
 * Kecak Exclusive
 *
 */
public abstract class DefaultFormPermission extends UserviewPermission implements FormPermission {
    private FormData formData;
    private Element element;

    @Override
    public FormData getFormData() {
        return formData;
    }

    @Override
    public void setFormData(FormData formData) {
        this.formData = formData;
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public void setElement(Element element) {
        this.element = element;
    }
}
