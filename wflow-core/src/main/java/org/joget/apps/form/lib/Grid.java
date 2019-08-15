package org.joget.apps.form.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.ListOrderedMap;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Grid extends Element implements FormBuilderPaletteElement, FormContainer, AceFormElement, AdminLteFormElement {
    protected Map<FormData, FormRowSet> cachedRowSet = new HashMap<FormData, FormRowSet>();

    public String getName() {
        return "Grid";
    }

    public String getVersion() {
        return "5.0.0";
    }

    public String getDescription() {
        return "Grid Element";
    }

    /**
     * Return a Map of value=label, each pair representing a grid column header.
     * @param formData
     * @return
     */
    protected Map<String, String> getHeaderMap(FormData formData) {
        @SuppressWarnings("unchecked")
		Map<String, String> headerMap = new ListOrderedMap();
        Object optionProperty = getProperty(FormUtil.PROPERTY_OPTIONS);
        if (optionProperty != null && optionProperty instanceof Collection) {
            for (@SuppressWarnings("rawtypes") Map opt : (FormRowSet) optionProperty) {
                Object value = opt.get("value");
                Object label = opt.get("label");
                if (value != null && label != null) {
                    headerMap.put(value.toString(), label.toString());
                }
            }
        }
        return headerMap;
    }

    /**
     * Return the grid data
     * @param formData
     * @return A FormRowSet containing the grid cell data.
     */
    protected FormRowSet getRows(FormData formData) {
        if (!cachedRowSet.containsKey(formData)) {
            String id = getPropertyString(FormUtil.PROPERTY_ID);
            String param = FormUtil.getElementParameterName(this);
            FormRowSet rowSet = new FormRowSet();
            rowSet.setMultiRow(true);

            // get headers
            Map<String, String> headerMap = getHeaderMap(formData);

            // read from 'value' property
            String json = getPropertyString(FormUtil.PROPERTY_VALUE);
            try {
                rowSet = parseFormRowSetFromJson(json);
            } catch (Exception ex) {
                LogUtil.error(Grid.class.getName(), ex, "Error parsing grid JSON");
            }

            // read from request if available.
            boolean continueLoop = true;
            int i = 0;
            while (continueLoop) {
                FormRow row = new FormRow();
                for (String header : headerMap.keySet()) {
                    String paramName = param + "_" + header + "_" + i;
                    String paramValue = formData.getRequestParameter(paramName);
                    if (paramValue != null) {
                        row.setProperty(header, paramValue);
                    }
                }
                i++;
                if (!row.isEmpty()) {
                    if (i == 0) {
                        // reset rowset
                        rowSet = new FormRowSet();
                    }
                    rowSet.add(row);
                } else {
                    // no more rows, stop looping
                    continueLoop = false;
                }
            }

            if (!FormUtil.isFormSubmitted(this, formData)) {
                // load from binder if available
                FormRowSet binderRowSet = formData.getLoadBinderData(this);
                if (binderRowSet != null) {
                    if (!binderRowSet.isMultiRow()) {
                        // parse from String
                        if (!binderRowSet.isEmpty()) {
                            FormRow row = binderRowSet.get(0);
                            String jsonValue = row.getProperty(id);
                            try {
                                rowSet = parseFormRowSetFromJson(jsonValue);
                            } catch (Exception ex) {
                                LogUtil.error(Grid.class.getName(), ex, "Error parsing grid JSON");
                            }
                        }

                    } else {
                        rowSet = binderRowSet;
                    }
                }

            }
            cachedRowSet.put(formData, rowSet);
        }

        return cachedRowSet.get(formData);
    }

    /**
     * Parses a JSON String into a FormRowSet
     * @param json
     * @return
     * @throws JSONException
     */
    protected FormRowSet parseFormRowSetFromJson(String json) throws JSONException {
        FormRowSet rowSet = new FormRowSet();
        rowSet.setMultiRow(false);

        if (json != null && json.trim().length() > 0) {
            // loop thru each row in json array
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonRow = (JSONObject) jsonArray.get(i);

                // create row and populate fields
                FormRow row = new FormRow();
                JSONArray fields = jsonRow.names();
                if (fields != null && fields.length() > 0) {
                    for (int k = 0; k < fields.length(); k++) {
                        String fieldName = fields.getString(k);
                        String value = jsonRow.getString(fieldName);
                        row.setProperty(fieldName, value);
                    }
                }
                rowSet.add(row);
            }
        }
        return rowSet;
    }

    @Override
    public FormRowSet formatData(FormData formData) {
        // get form rowset
        FormRowSet rowSet = getRows(formData);
        rowSet.setMultiRow(true);

        // TODO: set foreign key?

        return rowSet;
    }

    @SuppressWarnings("unchecked")
	@Override
    public String renderTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        String template = "grid.ftl";
        return renderTemplate(template,formData,dataModel);
    }

    private String renderTemplate(String template, FormData formData, @SuppressWarnings("rawtypes") Map dataModel){
        // set value
        String[] valueArray = FormUtil.getElementPropertyValues(this, formData);
        List<String> values = Arrays.asList(valueArray);
        dataModel.put("values", values);

        // set validator decoration
        String decoration = FormUtil.getElementValidatorDecoration(this, formData);
        dataModel.put("decoration", decoration);

        // set headers
        Map<String, String> headers = getHeaderMap(formData);
        dataModel.put("headers", headers);

        // set rows
        FormRowSet rows = getRows(formData);
        dataModel.put("rows", rows);

        dataModel.put("customDecorator", getDecorator());

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getFormBuilderTemplate() {
        return "<label class='label'>Grid</label><table cellspacing='0'><tr><th>Header</th><th>Header</th></tr><tr><td>Cell</td><td>Cell</td></tr></table>";
    }

    public String getLabel() {
        return "Grid";
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/form/grid.json", null, true, "message/form/Grid");
    }

    public String getFormBuilderCategory() {
        return FormBuilderPalette.CATEGORY_CUSTOM;
    }

    public int getFormBuilderPosition() {
        return 1100;
    }

    public String getFormBuilderIcon() {
        return "/plugin/org.joget.apps.form.lib.Grid/images/grid_icon.gif";
    }
     
    @Override
    public Boolean selfValidate(FormData formData) {
        Boolean valid = true;
        
        FormRowSet rowSet = getRows(formData);
        String id = FormUtil.getElementParameterName(this);
        String errorMsg = getPropertyString("errorMessage");
        
        String min = getPropertyString("validateMinRow");
        if (min != null && !min.isEmpty()) {
            try {
                int minNumber = Integer.parseInt(min);
                if (rowSet.size() < minNumber) {
                    valid = false;
                }
            } catch (Exception e) {}
        }
        
        String max = getPropertyString("validateMaxRow");
        if (max != null && !max.isEmpty()) {
            try {
                int maxNumber = Integer.parseInt(max);
                if (rowSet.size() > maxNumber) {
                    valid = false;
                }
            } catch (Exception e) {}
        }
        
        if (!valid) {
            formData.addFormError(id, errorMsg);
        }
        
        return valid;
    }
    
    protected String getDecorator() {
        String decorator = "";
        
        try {
            String min = getPropertyString("validateMinRow");
            
            if ((min != null && !min.isEmpty())) {
                int minNumber = Integer.parseInt(min);
                if (minNumber > 0) {
                    decorator = "*";
                }
            }
        } catch (Exception e) {}
        
        return decorator;
    }
    
    @Override
    public Collection<String> getDynamicFieldNames() {
        Collection<String> fieldNames = new ArrayList<String>();
        
        if (getStoreBinder() == null) {
            fieldNames.add(getPropertyString(FormUtil.PROPERTY_ID));
        }
        
        return fieldNames;
    }

    @Override
    public String renderAceTemplate(FormData formData, Map dataModel) {
        String template = "AceTheme/AceGrid.ftl";
        return renderTemplate(template,formData,dataModel);
    }

    @Override
    public String renderAdminLteTemplate(FormData formData, Map dataModel) {
        String template = "AdminLteTheme/AdminLteGrid.ftl";
        return renderTemplate(template,formData,dataModel);
    }
}

