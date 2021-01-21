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
public interface DataJsonControllerHandler {
    String PARAMETER_DATA_JSON_CONTROLLER = "_DATA_JSON_CONTROLLER";
    String PARAMETER_AS_OPTIONS = "_AS_OPTIONS";

    /**
     *
     * @param requestParameterData
     * @param element
     * @param formData
     * @return data that will be passed to request parameter
     */
    default String[] handleMultipartDataRequest(Map<String, String[]> requestParameterData, Element element, FormData formData) {
        String elementId = element.getPropertyString(FormUtil.PROPERTY_ID);
        return requestParameterData.get(elementId);
    }

    /**
     *
     * @param requestBodyPayload
     * @param element
     * @param formData
     * @return data that will be passed to request parameter
     */
    default String[] handleJsonDataRequest(String requestBodyPayload, Element element, FormData formData) throws JSONException {
        String elementId = element.getPropertyString(FormUtil.PROPERTY_ID);

        JSONObject jsonBody = new JSONObject(requestBodyPayload);
        return new String[] { jsonBody.getString(elementId) };
    }

    /**
     * Handle values that will be thrown as response in DataJsonController
     *
     * @param element
     * @param formData
     * @value that will be shown as response
     */
    default Object handleElementValueResponse(Element element, FormData formData) {
        String[] values = element.getElementValues(formData);
        if(values != null) {
            return String.join(";", values);
        } else {
            return null;
        }
    }
}
