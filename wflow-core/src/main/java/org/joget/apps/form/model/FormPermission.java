package org.joget.apps.form.model;

/**
 * Used to mark a Userview Permission Plugin is reusable in Form Builder
 * 
 */
public interface FormPermission {
    /**
     * Get current form data, null if permission is not assigned to a form
     * @return
     */
    FormData getFormData();

    /**
     * Set current form data
     * @param formData
     */
    void setFormData(FormData formData);

    /**
     * Get current Element
     * @return
     */
    Element getElement();

    /**
     * Set current element
     * @param element
     */
    void setElement(Element element);
}
