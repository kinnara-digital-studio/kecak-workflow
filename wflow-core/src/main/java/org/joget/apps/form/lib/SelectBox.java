package org.joget.apps.form.lib;

import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.dao.FormDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.FormDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormService;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class SelectBox extends Element implements FormBuilderPaletteElement, FormAjaxOptionsElement, PluginWebSupport {
    private final WeakHashMap<String, Form> formCache = new WeakHashMap<>();

    private Element controlElement;

    private final static long PAGE_SIZE = 10;

    public String getName() {
        return "Select Box";
    }

    public String getVersion() {
        return "5.0.0";
    }

    public String getDescription() {
        return "Select Box Element";
    }

    /**
     * Returns the option key=value pairs for this select box.
     * @param formData
     * @return
     */
    @SuppressWarnings("rawtypes")
	public Collection<Map> getOptionMap(FormData formData) {
        Collection<Map> optionMap = FormUtil.getElementPropertyOptionsMap(this, formData);
        return optionMap;
    }
    
    @Override
    public FormData formatDataForValidation(FormData formData) {
        String[] paramValues = FormUtil.getRequestParameterValues(this, formData);
        if ((paramValues == null || paramValues.length == 0) && FormUtil.isFormSubmitted(this, formData)) {
            String paramName = FormUtil.getElementParameterName(this);
            formData.addRequestParameterValues(paramName, new String[]{""});
        }
        return formData;
    }

    @Override
    public FormRowSet formatData(FormData formData) {
        FormRowSet rowSet = null;

        // get value
        String id = getPropertyString(FormUtil.PROPERTY_ID);
        if (id != null) {
            String[] values = FormUtil.getElementPropertyValues(this, formData);
            if (values != null && values.length > 0) {
                // check for empty submission via parameter
                String[] paramValues = FormUtil.getRequestParameterValues(this, formData);
                if ((paramValues == null || paramValues.length == 0) && FormUtil.isFormSubmitted(this, formData)) {
                    values = new String[]{""};
                }

                // formulate values
                String delimitedValue = FormUtil.generateElementPropertyValues(values);

                // set value into Properties and FormRowSet object
                FormRow result = new FormRow();
                result.setProperty(id, delimitedValue);
                rowSet = new FormRowSet();
                rowSet.add(result);
            }
            
        	// remove duplicate based on label (because list is sorted by label by default)
            if("true".equals(getProperty("removeDuplicates")) && rowSet != null) {
            	FormRowSet newResults = new FormRowSet();
            	String currentValue = null;
            	for(FormRow row : rowSet) {
            		String label = row.getProperty(FormUtil.PROPERTY_LABEL);
            		if(currentValue == null || !currentValue.equals(label)) {
            			currentValue = label;
            			newResults.add(row);
            		}
            	}
            	
            	rowSet = newResults;
            }
        }

        return rowSet;
    }

    @SuppressWarnings("unchecked")
	@Override
    public String renderTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        String template = "selectBox.ftl";
        
        dynamicOptions(formData);

        // set value
        String[] valueArray = FormUtil.getElementPropertyValues(this, formData);
        List<String> values = Arrays.asList(valueArray);
        dataModel.put("values", values);

        // set options
        @SuppressWarnings("rawtypes")
		final List<Map<String, String>> optionMap = new ArrayList<>();
        for(Map m : getOptionMap(formData)) {
            optionMap.add((Map<String,String>)m);
        }
        dataModel.put("options", optionMap);

        Comparator<Map<String, String>> comparator = new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> m1, Map<String, String> m2) {
                return m1.get("value").compareTo(m2.get("value"));
            }
        };

        final List<Map<String, String>> valuesMap = new ArrayList<>();
        for(String s : values) {
            if(!s.isEmpty()) {
                Map<String, String> map = new HashMap<>();
                map.put("value", s);

                final Map<String, String> lookingFor = new HashMap<>();
                lookingFor.put("value", s);

                int index = Collections.binarySearch(optionMap, lookingFor, comparator);
                map.put("label", index >= 0 ? optionMap.get(index).get("label") : s);

                valuesMap.add(map);
            }
        }
        dataModel.put("optionsValues", valuesMap);


        dataModel.put("className", getClassName());

        dataModel.put("width", getPropertyString("size") == null || getPropertyString("size").isEmpty() ? "resolve" : (getPropertyString("size").replaceAll("[^0-9]+]", "") + "%"));

        ApplicationContext appContext = AppUtil.getApplicationContext();
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) appContext.getBean("workflowUserManager");
        DirectoryManager directoryManager = (DirectoryManager) appContext.getBean("directoryManager");
        User user = directoryManager.getUserByUsername(WorkflowUtil.getCurrentUsername());
        dataModel.put("language", user == null || user.getLocale() == null? "en" : user.getLocale().replaceAll("_", "-"));

        final AppDefinition appDefinition = AppUtil.getCurrentAppDefinition();
        if(appDefinition != null) {
            dataModel.put("appId", appDefinition.getAppId());
            dataModel.put("appVersion", appDefinition.getVersion());
        }

        final Form form = FormUtil.findRootForm(this);
        if(form != null)
            dataModel.put("formDefId", form.getPropertyString(FormUtil.PROPERTY_ID));

        dataModel.put("pageSize", PAGE_SIZE);

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getFormBuilderTemplate() {
        return "<label class='label'>Select Box</label><select><option>Option</option></select>";
    }

    public String getLabel() {
        return "Select Box";
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/form/selectBox.json", null, true, "message/form/SelectBox");
    }

    public String getFormBuilderCategory() {
        return FormBuilderPalette.CATEGORY_GENERAL;
    }

    public int getFormBuilderPosition() {
        return 300;
    }

    public String getFormBuilderIcon() {
        return null;
    }
    
    protected void dynamicOptions(FormData formData) {
        if (getControlElement(formData) != null) {
            setProperty("controlFieldParamName", FormUtil.getElementParameterName(getControlElement(formData)));
            
            FormUtil.setAjaxOptionsElementProperties(this, formData);
        }
    }

    public Element getControlElement(FormData formData) {
        if (controlElement == null) {
            if (getPropertyString("controlField") != null && !getPropertyString("controlField").isEmpty()) {
                Form form = FormUtil.findRootForm(this);
                controlElement = FormUtil.findElement(getPropertyString("controlField"), form, formData);
            }
        }
        return controlElement;
    }

    private Form generateForm(AppDefinition appDef, String formDefId) {
        ApplicationContext appContext = AppUtil.getApplicationContext();
        FormService formService = (FormService) appContext.getBean("formService");
        FormDefinitionDao formDefinitionDao = (FormDefinitionDao)appContext.getBean("formDefinitionDao");

        // check in cache
        if(formCache.containsKey(formDefId))
            return formCache.get(formDefId);

        // proceed without cache
        if (appDef != null && formDefId != null && !formDefId.isEmpty()) {
            FormDefinition formDef = formDefinitionDao.loadById(formDefId, appDef);
            if (formDef != null) {
                String json = formDef.getJson();
                Form form = (Form)formService.createElementFromJson(json);

                formCache.put(formDefId, form);

                return form;
            }
        }
        return null;
    }

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if("GET".equals(request.getMethod())) {
            final AppDefinitionDao appDefinitionDao = (AppDefinitionDao) AppUtil.getApplicationContext().getBean("appDefinitionDao");

            final String appId = request.getParameter("appId");
            final String appVersion = request.getParameter("appVersion");
            final String formDefId = request.getParameter("formDefId");
            final String[] fieldIds = request.getParameterValues("fieldId");
            final String search = request.getParameter("search");
            final Pattern searchPattern = Pattern.compile(search == null ? "" : search, Pattern.CASE_INSENSITIVE);
            final long page = Long.parseLong(request.getParameter("page"));
            final String grouping = request.getParameter("grouping");

            final AppDefinition appDefinition = appDefinitionDao.loadVersion(appId, Long.parseLong(appVersion));

            final FormData formData = new FormData();
            final Form form = generateForm(appDefinition, formDefId);

            final JSONArray jsonResults = new JSONArray();
            for (String fieldId : fieldIds) {
                Element element = FormUtil.findElement(fieldId, form, formData);

                if (element == null)
                    continue;

                FormRowSet optionsRowSet;
                if (element.getOptionsBinder() == null) {
                    optionsRowSet = (FormRowSet) element.getProperty(FormUtil.PROPERTY_OPTIONS);
                } else {
                    FormUtil.executeOptionBinders(element, formData);
                    optionsRowSet = formData.getOptionsBinderData(element, null);
                }

                int skip = (int) ((page - 1) * PAGE_SIZE);
                int pageSize = (int) PAGE_SIZE;
                for (int i = 0, size = optionsRowSet.size(); i < size && pageSize > 0; i++) {
                    FormRow formRow = optionsRowSet.get(i);
                    if (searchPattern.matcher(formRow.getProperty(FormUtil.PROPERTY_LABEL)).find() && (
                            grouping == null
                                    || grouping.isEmpty()
                                    || grouping.equalsIgnoreCase(formRow.getProperty(FormUtil.PROPERTY_GROUPING)))) {

                        if (skip > 0) {
                            skip--;
                        } else {
                            try {
                                JSONObject jsonResult = new JSONObject();
                                jsonResult.put("id", formRow.getProperty(FormUtil.PROPERTY_VALUE));
                                jsonResult.put("text", formRow.getProperty(FormUtil.PROPERTY_LABEL));
                                jsonResults.put(jsonResult);
                                pageSize--;
                            } catch (JSONException ignored) {
                            }
                        }
                    }
                }
            }

            // I wonder why these codes don't work; they got some NULL POINTER EXCEPTION
            //        JSONArray jsonResults = new JSONArray((optionsRowSet).stream()
            //                .filter(Objects::nonNull)
            //                .filter(formRow -> searchPattern.matcher(formRow.getProperty(FormUtil.PROPERTY_LABEL)).find())
            //                .filter(formRow -> grouping == null
            //                        || formRow.getProperty(FormUtil.PROPERTY_GROUPING) == null
            //                        || grouping.isEmpty()
            //                        || formRow.getProperty(FormUtil.PROPERTY_GROUPING).isEmpty()
            //                        || grouping.equalsIgnoreCase(formRow.getProperty(FormUtil.PROPERTY_GROUPING)))
            //                .skip((page - 1) * PAGE_SIZE)
            //                .limit(PAGE_SIZE)
            //                .map(formRow -> {
            //                    final Map<String, String> map = new HashMap<>();
            //                    map.put("id", formRow.getProperty(FormUtil.PROPERTY_VALUE));
            //                    map.put("text", formRow.getProperty(FormUtil.PROPERTY_LABEL));
            //                    return map;
            //                })
            //                .collect(Collectors.toList()));

            try {
                JSONObject jsonPagination = new JSONObject();
                jsonPagination.put("more", jsonResults.length() >= PAGE_SIZE);

                JSONObject jsonData = new JSONObject();
                jsonData.put("results", jsonResults);
                jsonData.put("pagination", jsonPagination);

                response.setContentType("application/json");
                response.getWriter().write(jsonData.toString());
            } catch (JSONException e) {
                LogUtil.error(getClassName(), e, e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }
}

