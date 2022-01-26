package org.joget.apps.form.model;

import org.joget.apps.form.service.FormUtil;

import javax.annotation.Nonnull;

/**
 * This interface indicate that a Form Field Element is a multi options field 
 * such as Select Box, Check Box & Radio Button. It can use Form Options Binder 
 * to populate its options.
 * 
 */
public interface FormOptionsElement {
    /**
     * Returns the option key=value pairs for this select box.
     * @param formData
     * @return
     */
    @Nonnull
    default FormRowSet getOptionMap(FormData formData) {
        if(this instanceof Element) {
            FormRowSet optionMap = FormUtil.getElementPropertyOptionsMap((Element) this, formData);
            optionMap.setMultiRow(true);
            return optionMap;
        } else {
            return new FormRowSet();
        }
    }
}
