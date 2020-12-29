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

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectBox extends Element implements FormBuilderPaletteElement, FormAjaxOptionsElement, PluginWebSupport, AceFormElement, AdminLteFormElement {
    private final Map<String, Form> formCache = new HashMap<>();

    private Element controlElement;

    private final static long PAGE_SIZE = 10;

    @Override
    public String getName() {
        return "Select Box";
    }

    @Override
    public String getVersion() {
        return "5.0.0";
    }

    @Override
    public String getDescription() {
        return "Select Box Element";
    }

    /**
     * Returns the option key=value pairs for this select box.
     * @param formData
     * @return
     */
    @Nonnull
	public FormRowSet getOptionMap(FormData formData) {
        FormRowSet optionMap = FormUtil.getElementPropertyOptionsMap(this, formData);
        optionMap.setMultiRow(true);
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

    @Override
    public String getElementValue(FormData formData) {
        String originalValue = super.getElementValue(formData);
        if(asLabel(formData)) {
            return getValueLabel(originalValue, formData);
        } else {
            return originalValue;
        }
    }

    @Override
    public String[] getElementValues(FormData formData) {
        String[] originalValues = FormUtil.getElementPropertyValues(this, formData);
        if(asLabel(formData)) {
            return getValueLabels(originalValues, formData);
        } else {
            return originalValues;
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public String renderTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        String template = "selectBox.ftl";
        return renderTemplate(template, formData, dataModel);
    }

    protected String renderTemplate(String template, FormData formData, @SuppressWarnings("rawtypes") Map dataModel){
        dynamicOptions(formData);

        // set value
        String[] valueArray = FormUtil.getElementPropertyValues(this, formData);
        List<String> values = Arrays.asList(valueArray);
        dataModel.put("values", values);

        // set options
        FormRowSet optionMap = getOptionMap(formData);
        dataModel.put("options", optionMap);

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

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getFormBuilderTemplate() {
        return "<label class='label'>Select Box</label><select><option>Option</option></select>";
    }

    @Override
    public String getLabel() {
        return "Select Box";
    }

    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/form/selectBox.json", null, true, "message/form/SelectBox");
    }

    @Override
    public String getFormBuilderCategory() {
        return FormBuilderPalette.CATEGORY_GENERAL;
    }

    @Override
    public int getFormBuilderPosition() {
        return 300;
    }

    @Override
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

    protected Form generateForm(AppDefinition appDef, String formDefId) {
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
            final String formDefId = getOptionalParameter(request, "formDefId", "");
            final String[] fieldIds = getOptionalParameterValues(request, "fieldId", new String[0]);
            final String search = getOptionalParameter(request,"search", "");
            final Pattern searchPattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
            final long page = Long.parseLong(getOptionalParameter(request, "page", "1"));
            final String grouping = getOptionalParameter(request, "grouping", "");
            final String[] values = getOptionalParameterValues(request, "values", new String[0]);

            final AppDefinition appDefinition = AppUtil.getCurrentAppDefinition();

            final FormData formData = new FormData();
            final Form form = generateForm(appDefinition, formDefId);

            final JSONArray jsonResults = new JSONArray();
            for (String fieldId : fieldIds) {
                Element element = FormUtil.findElement(fieldId, form, formData);

                if (element == null)
                    continue;

                FormUtil.executeOptionBinders(element, formData);
                FormRowSet optionsRowSet = FormUtil.getElementPropertyOptionsMap(element, formData);
                if(values.length > 0) {
                    for(FormRow row : optionsRowSet) {
                        boolean found = false;
                        for (String value : values) {
                            if(found = value.equals(row.getProperty(FormUtil.PROPERTY_VALUE))) {
                                break;
                            }
                        }

                        if(found) {
                            try {
                                JSONObject jsonRow = new JSONObject();
                                jsonRow.put("id", row.getProperty(FormUtil.PROPERTY_VALUE));
                                jsonRow.put("text", row.getProperty(FormUtil.PROPERTY_LABEL));
                                jsonResults.put(jsonRow);
                            } catch (JSONException ignored) {}
                        }
                    }
                } else {
                    int skip = (int) ((page - 1) * PAGE_SIZE);
                    int pageSize = (int) PAGE_SIZE;
                    for (int i = 0, size = optionsRowSet.size(); i < size && pageSize > 0; i++) {
                        FormRow formRow = optionsRowSet.get(i);
                        if (searchPattern.matcher(formRow.getProperty(FormUtil.PROPERTY_LABEL)).find() && (
                                grouping.isEmpty()
                                        || grouping.equalsIgnoreCase(formRow.getProperty(FormUtil.PROPERTY_GROUPING)))) {

                            if (skip > 0) {
                                skip--;
                            } else {
                                try {
                                    JSONObject jsonRow = new JSONObject();
                                    jsonRow.put("id", formRow.getProperty(FormUtil.PROPERTY_VALUE));
                                    jsonRow.put("text", formRow.getProperty(FormUtil.PROPERTY_LABEL));
                                    jsonResults.put(jsonRow);
                                    pageSize--;
                                } catch (JSONException ignored) {
                                }
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

    @Override
    public String renderAceTemplate(FormData formData, Map dataModel) {
        String template = "AceTheme/AceSelectBox.ftl";
        return renderTemplate(template, formData, dataModel);
    }

    @Override
    public String renderAdminLteTemplate(FormData formData, Map dataModel) {
        String template = "AdminLteTheme/AdminLteSelectBox.ftl";
        return renderTemplate(template, formData, dataModel);
    }


    protected String getValueLabel(String value, FormData formData) {
        String[] values = value == null ? new String[0] : value.split(";");
        return Optional.of(getValueLabels(values, formData))
                .map(Arrays::stream)
                .orElseGet(Stream::empty)

                // yes, we have to use anonymous object
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return !s.isEmpty();
                    }
                })
                .collect(Collectors.joining(";"));
    }

    protected String[] getValueLabels(String[] values, FormData formData) {
        FormRowSet optionMap = getOptionMap(formData);

        // yes, we have to use anonymous object
        Comparator<FormRow> comparator = Comparator.comparing(new Function<FormRow, String>() {
            @Override
            public String apply(FormRow r) {
                return r.getProperty(FormUtil.PROPERTY_VALUE);
            }
        });

        optionMap.sort(comparator);

        // yes, we have to use anonymous object
        return Optional.ofNullable(values)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .map(new Function<String, String[]>() {
                    @Override
                    public String[] apply(String s) {
                        return s.split(";");
                    }
                })
                .flatMap(Arrays::stream)
                .map(new Function<String, FormRow>() {
                    @Override
                    public FormRow apply(String s) {
                        FormRow row = new FormRow();
                        row.setProperty(FormUtil.PROPERTY_VALUE, s);
                        return row;
                    }
                })
                .map(new Function<FormRow, Integer>() {
                    @Override
                    public Integer apply(FormRow row) {
                        return Collections.binarySearch(optionMap, row, comparator);
                    }
                })
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer i) {
                        return i >= 0;
                    }
                })
                .map(new Function<Integer, FormRow>() {
                    @Override
                    public FormRow apply(Integer index) {
                        return optionMap.get(index);
                    }
                })
                .map(new Function<FormRow, String>() {
                    @Override
                    public String apply(FormRow r) {
                        return r.getProperty(FormUtil.PROPERTY_LABEL);
                    }
                })
                .toArray(new IntFunction<String[]>() {
                    @Override
                    public String[] apply(int value) {
                        return new String[value];
                    }
                });
    }

    /**
     * Should this element be shown as readonly label
     *
     * @param formData
     * @return
     */
    protected boolean asLabel(FormData formData) {
        return "true".equalsIgnoreCase(getPropertyString(FormUtil.PROPERTY_READONLY))
                && "true".equalsIgnoreCase(getPropertyString(FormUtil.PROPERTY_READONLY_LABEL));
    }

    protected String getOptionalParameter(HttpServletRequest request, String parameterName, String defaultValue) {
        String value = request.getParameter(parameterName);
        return value == null ? defaultValue : value;
    }

    protected String[] getOptionalParameterValues(HttpServletRequest request, String parameterName, String[] defaultValues) {
        return Optional.of(parameterName)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        return request.getParameter(s);
                    }
                })
                .map(new Function<String, String[]>() {
                    @Override
                    public String[] apply(String s) {
                        return s.split(";");
                    }
                })
                .map(new Function<String[], Stream<String>>() {
                    @Override
                    public Stream<String> apply(String[] array) {
                        return Arrays.stream(array);
                    }
                })
                .orElseGet(new Supplier<Stream<String>>() {
                    @Override
                    public Stream<String> get() {
                        return Stream.empty();
                    }
                })
                .toArray(new IntFunction<String[]>() {
                    @Override
                    public String[] apply(int value) {
                        return new String[value];
                    }
                });
    }
}

