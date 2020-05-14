package org.kecak.apps.form.model;

import org.joget.apps.form.model.FormData;

/**
 * @author aristo
 *
 * Indicator for element that has multi values
 */
public interface MultiValueElement {
    /**
     * Method to retrieve element value from form data ready to be shown to UI.
     * You can override this to use your own value formatting.
     *
     * @param formData
     * @return
     */
    String[] getElementValues(FormData formData);
}
