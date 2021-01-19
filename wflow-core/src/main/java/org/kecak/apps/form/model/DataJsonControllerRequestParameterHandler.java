package org.kecak.apps.form.model;

import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Handler for DataJsonController, this interface will be called
 * when DataJsonController assigns values to {@link org.joget.apps.form.model.FormData#addRequestParameterValues(String, String[])}
 *
 * Implement in {@link Element}. How the element will handle json data
 */
public interface DataJsonControllerRequestParameterHandler {

    /**
     *
     * @param data
     * @param element
     * @param formData
     * @return data that will be passed to request parameter
     */
    default String[] handleMultipartRequest(Map<String, String[]> data, Element element, FormData formData) {
        String elementId = element.getPropertyString(FormUtil.PROPERTY_ID);
        return data.get(elementId);
    }

    /**
     *
     * @param bodyPayload
     * @param element
     * @param formData
     * @return data that will be passed to request parameter
     */
    default String[] handleJsonRequest(String bodyPayload, Element element, FormData formData) throws JSONException {
        String elementId = element.getPropertyString(FormUtil.PROPERTY_ID);

        JSONObject jsonBody = new JSONObject(bodyPayload);
        return new String[] { jsonBody.getString(elementId) };
    }
}
