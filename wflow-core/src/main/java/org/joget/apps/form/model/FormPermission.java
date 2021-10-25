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
    default FormData getFormData() { return null; }

    /**
     * Set current form data
     * @param formData
     */
    default void setFormData(FormData formData) { }

    /**
     * Get current Element
     * @return
     */
    default Element getElement() { return null; }

    /**
     * Set current element
     * @param element
     */
    default void setElement(Element element) { }
}
