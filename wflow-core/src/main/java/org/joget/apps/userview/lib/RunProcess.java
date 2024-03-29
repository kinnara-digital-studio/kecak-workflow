package org.joget.apps.userview.lib;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.model.PackageDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.app.service.MobileUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormService;
import org.joget.apps.form.service.FormUtil;
import org.joget.apps.userview.model.UserviewBuilderPalette;
import org.joget.apps.userview.model.UserviewMenu;
import org.joget.apps.workflow.lib.AssignmentCompleteButton;
import org.joget.apps.workflow.lib.AssignmentWithdrawButton;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.ResourceBundleUtil;
import org.joget.commons.util.SecurityUtil;
import org.joget.commons.util.StringUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.WorkflowActivity;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.WorkflowProcess;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONArray;
import org.kecak.apps.userview.model.BootstrapUserviewTheme;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RunProcess extends UserviewMenu implements PluginWebSupport {

    public String getClassName() {
        return getClass().getName();
    }

    public String getLabel() {
        return "Run Process";
    }

    @Override
    public String getIcon() {
        return "/plugin/org.joget.apps.userview.lib.RunProcess/images/grid_icon.gif";
    }

    @Override
    public String getRenderPage() {
        return null;
    }

    public String getName() {
        return "Run Process Menu";
    }

    public String getVersion() {
        return "5.0.0";
    }

    public String getDescription() {
        return "";
    }

    public String getPropertyOptions() {
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        String appId = appDef.getId();
        String appVersion = appDef.getVersion().toString();
        Object[] arguments = new Object[]{appId, appVersion};
        String json = AppUtil.readPluginResource(getClass().getName(), "/properties/userview/runProcess.json", arguments, true, "message/userview/runProcess");
        return json;
    }

    @Override
    public String getDecoratedMenu() {
        // sanitize label
        String label = getPropertyString("label");
        if (label != null) {
            label = StringUtil.stripHtmlRelaxed(label);
        }
        
        if ("Yes".equals(getPropertyString("showInPopupDialog"))) {
            String menu = "<a onclick=\"menu_" + getPropertyString("id") + "_showDialog();return false;\" class=\"menu-link\"><span>" + label + "</span></a>";
            menu += "<script>\n";

            if ("Yes".equals(getPropertyString("showInPopupDialog"))) {
                String url = getUrl() + "?embed=true";

                menu += "var menu_" + getPropertyString("id") + "Dialog = new PopupDialog(\"" + url + "\",\"\");\n";
            }
            menu += "function menu_" + getPropertyString("id") + "_showDialog(){\n";
            if ("true".equals(getRequestParameter("isPreview"))) {
                menu += "alert(\"" + ResourceBundleUtil.getMessage("userview.runprocess.showInPopupPreviewWarning") + "\");\n";
            } else {
                menu += "menu_" + getPropertyString("id") + "Dialog.init();\n";
            }
            menu += "}\n</script>";
            return menu;
        } else if ("Yes".equals(getPropertyString("runProcessDirectly"))) {
            ApplicationContext ac = AppUtil.getApplicationContext();
            AppService appService = (AppService) ac.getBean("appService");
            PackageActivityForm startFormDef = appService.retrieveMappedForm(getRequestParameterString("appId"), getRequestParameterString("appVersion"), getPropertyString("processDefId"), WorkflowUtil.ACTIVITY_DEF_ID_RUN_PROCESS);
            if (startFormDef == null || startFormDef.getFormId() == null) {
                String url = getUrl();
                
                if (MobileUtil.isMobileView()) {
                    url = url.replace("/web/userview", "/web/mobile");
                }
                
                String menu = "<a onclick=\"menu_" + getPropertyString("id") + "_postForm();return false;\" class=\"menu-link\"><span>" + label + "</span></a>";
                menu += "<form id=\"menu_" + getPropertyString("id") + "_form\" method=\"POST\" action=\"" + url + "?_action=run" + "\" style=\"display:none\"></form>\n";
                menu += "<script>"
                        + "function menu_" + getPropertyString("id") + "_postForm() {";
                if ("true".equals(getRequestParameter("isPreview"))) {
                    menu += "alert(\"" + ResourceBundleUtil.getMessage("userview.runprocess.runProcessPreviewWarning") + "\");\n";
                } else {
                    menu += "$('#menu_" + getPropertyString("id") + "_form').submit()\n";
                }
                menu += "}";
                menu += "</script>\n";
                return menu;
            }
        }
        
        return null;
    }

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean isAdmin = WorkflowUtil.isCurrentUserInRole(WorkflowUserManager.ROLE_ADMIN);
        if (!isAdmin) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }        

        String action = request.getParameter("action");

        if ("getOptions".equals(action)) {
            String appId = request.getParameter("appId");
            String appVersion = request.getParameter("appVersion");
            try {
                JSONArray jsonArray = new JSONArray();

                ApplicationContext ac = AppUtil.getApplicationContext();
                AppService appService = (AppService) ac.getBean("appService");
                WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
                AppDefinition appDef = appService.getAppDefinition(appId, appVersion);
                PackageDefinition packageDefinition = appDef.getPackageDefinition();
                Long packageVersion = (packageDefinition != null) ? packageDefinition.getVersion() : new Long(1);
                Collection<WorkflowProcess> processList = workflowManager.getProcessList(appId, packageVersion.toString());

                Map<String, String> empty = new HashMap<String, String>();
                empty.put("value", "");
                empty.put("label", "");
                jsonArray.put(empty);

                for (WorkflowProcess p : processList) {
                    Map<String, String> option = new HashMap<String, String>();
                    option.put("value", p.getIdWithoutVersion());
                    option.put("label", p.getName() + " (" + p.getIdWithoutVersion() + ")");
                    jsonArray.put(option);
                }

                jsonArray.write(response.getWriter());
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "Get Run Process's options Error!");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    @Override
    public String getJspPage() {
        return getJspPage("userview/plugin/runProcess.jsp");
    }

    protected String getJspPage(String jspFile) {
        if ("start".equals(getRequestParameterString("_action")) || "run".equals(getRequestParameterString("_action"))) {
            // only allow POST
            HttpServletRequest request = WorkflowUtil.getHttpServletRequest();
            if (request != null && !"POST".equalsIgnoreCase(request.getMethod())) {
                return "userview/plugin/unauthorized.jsp";
            }

            startProcess(getRequestParameterString("_action"));
        } else if ("assignmentView".equals(getRequestParameterString("_action"))) {
            assignmentView();
        } else if ("assignmentSubmit".equals(getRequestParameterString("_action"))) {
            // only allow POST
            HttpServletRequest request = WorkflowUtil.getHttpServletRequest();
            if (request != null && !"POST".equalsIgnoreCase(request.getMethod())) {
                return "userview/plugin/unauthorized.jsp";
            }

            assignmentSubmit();
        } else {
            ApplicationContext ac = AppUtil.getApplicationContext();
            AppService appService = (AppService) ac.getBean("appService");
            PackageActivityForm startFormDef = appService.retrieveMappedForm(getRequestParameterString("appId"), getRequestParameterString("appVersion"), getPropertyString("processDefId"), WorkflowUtil.ACTIVITY_DEF_ID_RUN_PROCESS);

            if ("Yes".equals(getPropertyString("runProcessDirectly")) && !((startFormDef != null && (startFormDef.getFormId() != null || PackageActivityForm.ACTIVITY_FORM_TYPE_EXTERNAL.equals(startFormDef.getType()))))) {
                if ("true".equals(getRequestParameter("isPreview"))) {
                    setProperty("view", "featureDisabled");
                } else {
                    viewProcess(null);
                    String csrfToken = "";
                    HttpServletRequest request = WorkflowUtil.getHttpServletRequest();
                    if (request != null) {
                        csrfToken = SecurityUtil.getCsrfTokenName() + "=" + SecurityUtil.getCsrfTokenValue(request);
                    }
                    setProperty("csrfToken", csrfToken);
                    setProperty("view", "processFormPost");
                }
            } else {
                viewProcess(null);
            }
        }

        return jspFile;
    }

    protected void viewProcess(PackageActivityForm startFormDef) {
        ApplicationContext ac = AppUtil.getApplicationContext();
        AppService appService = (AppService) ac.getBean("appService");
        FormService formService = (FormService) ac.getBean("formService");
        
        if (getPropertyString("processDefId").isEmpty()) {
            setProperty("view", "noProcess");
            return;
        }

        WorkflowProcess process = appService.getWorkflowProcessForApp(getRequestParameterString("appId"), getRequestParameterString("appVersion"), getPropertyString("processDefId"));
        if (process == null) {
            setProperty("view", "noProcess");
            return;
        }
        
        setProperty("process", process);
        
        if (isUnauthorized(process.getId())) {
            // check for start mapped form
            String formUrl = getUrl() + "?_action=start";
            FormData formData = new FormData();
            formData = formService.retrieveFormDataFromRequestMap(formData, getRequestParameters());

            String primaryKey = getRequestParameterString("recordId");
            if (primaryKey != null && primaryKey.trim().length() > 0) {
                formData.setPrimaryKeyValue(primaryKey);
            }

            if (getPropertyString("keyName") != null && getPropertyString("keyName").trim().length() > 0 && getKey() != null) {
                formData.addRequestParameterValues(FormService.PREFIX_FOREIGN_KEY + getPropertyString("keyName"), new String[]{getKey()});
            }
            
            if (startFormDef == null) {
                startFormDef = appService.viewStartProcessForm(getRequestParameterString("appId"), getRequestParameterString("appVersion"), getPropertyString("processDefId"), formData, formUrl);
            }
            
            if (startFormDef != null && (startFormDef.getForm() != null || PackageActivityForm.ACTIVITY_FORM_TYPE_EXTERNAL.equals(startFormDef.getType()))) {
                Form startForm = startFormDef.getForm();

                // generate form HTML
                String formHtml = formService.retrieveFormHtml(startForm, formData);
                AppDefinition appDef = appService.getAppDefinition(getRequestParameterString("appId"), getRequestParameterString("appVersion"));

                // show form
                setProperty("headerTitle", process.getName());
                setProperty("view", "formView");
                setProperty("formHtml", formHtml);
                setProperty("activityForm", startFormDef);
                setProperty("appDef", appDef);
            } else {
                // empty start page
                setProperty("headerTitle", process.getPackageName() + " (version " + process.getVersion() + ")");

                //append fk & fke parameter to url
                @SuppressWarnings("rawtypes")
				Map requestParam = getRequestParameters();
                for (Object k : requestParam.keySet()) {
                    String key = (String) k;
                    if (key.startsWith(FormService.PREFIX_FOREIGN_KEY) || key.startsWith(FormService.PREFIX_FOREIGN_KEY_EDITABLE) || key.startsWith(AppUtil.PREFIX_WORKFLOW_VARIABLE)) {
                        try {
                            formUrl += "&" + key + "=" + URLEncoder.encode(requestParam.get(k).toString(), "UTF-8");
                        } catch (Exception e) {
                            LogUtil.info(RunProcess.class.getName(), "Paramter:" + key + "cannot be append to URL");
                        }
                    }
                }

                setProperty("startUrl", formUrl);
                setProperty("view", "processDetail");
            }
        }
    }

    protected void startProcess(String action) {
        ApplicationContext ac = AppUtil.getApplicationContext();
        AppService appService = (AppService) ac.getBean("appService");
        FormService formService = (FormService) ac.getBean("formService");

        WorkflowProcess process = appService.getWorkflowProcessForApp(getRequestParameterString("appId"), getRequestParameterString("appVersion"), getPropertyString("processDefId"));
        setProperty("process", process);

        if (isUnauthorized(process.getId())) {
            // extract form values from request
            FormData formData = new FormData();

            String recordId = getRequestParameterString("recordId");
            if (recordId != null && recordId.trim().length() == 0) {
                recordId = null;
            }

            formData = formService.retrieveFormDataFromRequestMap(formData, getRequestParameters());

            if (getPropertyString("keyName") != null && getPropertyString("keyName").trim().length() > 0 && getKey() != null) {
                formData.addRequestParameterValues(FormService.PREFIX_FOREIGN_KEY + getPropertyString("keyName"), new String[]{getKey()});
            }

            String formUrl = getUrl() + "?_action=start";
            PackageActivityForm startFormDef = appService.viewStartProcessForm(getRequestParameterString("appId"), getRequestParameterString("appVersion"), getPropertyString("processDefId"), formData, formUrl);
            if (startFormDef != null && startFormDef.getForm() != null && "run".equals(action)) {
                viewProcess(startFormDef);
                return;
            }
            
            // get workflow variables
            Map<String, String> variableMap = AppUtil.retrieveVariableDataFromMap(getRequestParameters());
            WorkflowProcessResult result = appService.submitFormToStartProcess(getRequestParameterString("appId"), getRequestParameterString("appVersion"), getPropertyString("processDefId"), formData, variableMap, recordId, formUrl);
            Form startForm = null;
            if (startFormDef != null && (startFormDef.getForm() != null || PackageActivityForm.ACTIVITY_FORM_TYPE_EXTERNAL.equals(startFormDef.getType()))) {
                startForm = startFormDef.getForm();
                if (result == null) {
                    // generate form HTML
                    String formHtml = formService.retrieveFormErrorHtml(startForm, formData);
                    AppDefinition appDef = appService.getAppDefinition(getRequestParameterString("appId"), getRequestParameterString("appVersion"));

                    // show form
                    setProperty("headerTitle", process.getName());
                    setProperty("view", "formView");
                    setProperty("formHtml", formHtml);
                    setProperty("stay", formData.getStay());
                    setProperty("errorCount", formData.getFormErrors().size());
                    setProperty("submitted", Boolean.TRUE);
                    setProperty("activityForm", startFormDef);
                    setProperty("appDef", appDef);
                }
            } else {
                // start process 
                WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
                result = workflowManager.processStart(process.getId(), null, variableMap, null, recordId, false);
            }

            // set result
            if (result != null) {
                setAlertMessage(getPropertyString("messageTitleShowAfterComplete"),getPropertyString("messageShowAfterComplete"));
                // Show next activity if available
                Collection<WorkflowActivity> activities = result.getActivities();
                if (activities != null && !activities.isEmpty()) {
                    WorkflowActivity nextActivity = activities.iterator().next();
                    if (nextActivity != null) {
                        setProperty("view", "redirect");
                        setProperty("messageShowAfterComplete", "");
                        String redirectUrl = getUrl() + "?_action=assignmentView&activityId=" + nextActivity.getId();
                        setAlertMessage("");
                        setRedirectUrl(redirectUrl);
                    }
                    return;
                } else {
                    processStarted(startForm, formData);
                }
            }
        }
    }

    protected void assignmentView() {
        String activityId = getRequestParameterString("activityId");
        ApplicationContext ac = AppUtil.getApplicationContext();
        AppService appService = (AppService) ac.getBean("appService");
        FormService formService = (FormService) ac.getBean("formService");
        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");

        if (isAssignmentExist(activityId)) {
            try {
                WorkflowAssignment assignment = workflowManager.getAssignment(activityId);
                // set process instance ID as primary key
                FormData formData = new FormData();

                formData = formService.retrieveFormDataFromRequestMap(formData, getRequestParameters());

                if (getPropertyString("keyName") != null && getPropertyString("keyName").trim().length() > 0 && getKey() != null) {
                    formData.addRequestParameterValues(FormService.PREFIX_FOREIGN_KEY + getPropertyString("keyName"), new String[]{getKey()});
                }

                // get form
                String formUrl = getUrl() + "?_action=assignmentSubmit&activityId=" + activityId;
                PackageActivityForm activityForm = appService.viewAssignmentForm(getRequestParameterString("appId"), getRequestParameterString("appVersion"), activityId, formData, formUrl);
                Form form = activityForm.getForm();
                AppDefinition appDef = appService.getAppDefinition(getRequestParameterString("appId"), getRequestParameterString("appVersion"));

                // generate form HTML
                String formHtml = formService.retrieveFormHtml(form, formData);

                // show form
                setProperty("headerTitle", assignment.getProcessName() + " - " + assignment.getActivityName());
                setProperty("view", "formView");
                setProperty("formHtml", formHtml);
                setProperty("activityForm", activityForm);
                setProperty("appDef", appDef);
                setProperty("assignment", assignment);
            } catch (Exception e) {
                LogUtil.error(RunProcess.class.getName(), e, "");
            }
        }
    }

    @SuppressWarnings("deprecation")
	protected void assignmentSubmit() {
        ApplicationContext ac = AppUtil.getApplicationContext();
        AppService appService = (AppService) ac.getBean("appService");
        FormService formService = (FormService) ac.getBean("formService");
        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
        String activityId = getRequestParameterString("activityId");
        if (isAssignmentExist(activityId)) {
            try {
                WorkflowAssignment assignment = workflowManager.getAssignment(activityId);
                // set process instance ID as primary key
                FormData formData = new FormData();
                formData = formService.retrieveFormDataFromRequestMap(formData, getRequestParameters());
                String processId = assignment.getProcessId();

                // get form
                String formUrl = getUrl() + "?_action=assignmentSubmit&activityId=" + activityId;
                PackageActivityForm activityForm = appService.viewAssignmentForm(getRequestParameterString("appId"), getRequestParameterString("appVersion"), activityId, formData, formUrl);
                Form form = activityForm.getForm();

                // submit form
                FormData formResult = formService.executeFormActions(form, formData);

                // check for validation errors
                if (formResult.getFormResult(AssignmentWithdrawButton.DEFAULT_ID) != null) {
                    // withdraw assignment
                    workflowManager.assignmentWithdraw(activityId);
                } else if (formResult.getFormResult(AssignmentCompleteButton.DEFAULT_ID) != null) {
                    // complete assignment
                    Map<String, String> variableMap = AppUtil.retrieveVariableDataFromMap(getRequestParameters());
                    formResult = appService.completeAssignmentForm(form, assignment, formData, variableMap);

                    Map<String, String> errors = formResult.getFormErrors();
                    if (!formResult.getStay() && (errors == null || errors.isEmpty()) && activityForm.isAutoContinue()) {
                        setAlertMessage(getPropertyString("messageTitleShowAfterComplete"),getPropertyString("messageShowAfterComplete"));
                        // redirect to next activity if available
                        WorkflowAssignment nextActivity = workflowManager.getAssignmentByProcess(processId);
                        if (nextActivity != null) {
                            setProperty("view", "redirect");
                            setProperty("messageShowAfterComplete", "");
                            String redirectUrl = getUrl() + "?_action=assignmentView&activityId=" + nextActivity.getActivityId();
                            setAlertMessage("");
                            setRedirectUrl(redirectUrl);
                            return;
                        }
                    }
                } else {
                    String redirectUrl = getPropertyString("redirectUrlAfterSaveAsDraft");
                    if(redirectUrl != null && !redirectUrl.isEmpty()){
                        setAlertMessage(getPropertyString("messageTitleShowAfterSaved"),getPropertyString("messageShowAfterSaved"));
                        setRedirectUrl(redirectUrl);
                        return;
                    }
                }

                String html = null;

                // check for validation errors
                Map<String, String> errors = formResult.getFormErrors();
                int errorCount = 0;
                if (!formResult.getStay() && (errors == null || errors.isEmpty())) {
                    // render normal template
                    html = formService.generateElementHtml(form, formResult);
                } else {
                    // render error template
                    html = formService.generateElementErrorHtml(form, formResult);
                    errorCount = errors.size();
                }

                // show form
                setProperty("headerTitle", assignment.getProcessName() + " - " + assignment.getActivityName());
                if (!formResult.getStay() &&errorCount == 0) {
                    if (getPropertyString("redirectUrlAfterComplete") != null && !getPropertyString("redirectUrlAfterComplete").isEmpty()) {
                        setProperty("view", "redirect");
                        boolean redirectToParent = "Yes".equals(getPropertyString("showInPopupDialog"));
                        setRedirectUrl(getRedirectUrl(form, formResult), redirectToParent);
                    } else {
                        setProperty("view", "assignmentUpdated");
                    }
                } else {
                    setProperty("view", "formView");
                }
                setProperty("stay", formResult.getStay());
                setProperty("errorCount", errorCount);
                setProperty("submitted", Boolean.TRUE);
                setProperty("formHtml", html);
                return;

            } catch (Exception e) {
                LogUtil.error(RunProcess.class.getName(), e, "");
            }
        }
    }

    private boolean isUnauthorized(String processDefId) {
        // check for permission
        ApplicationContext ac = AppUtil.getApplicationContext();
        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
        if (workflowManager.isUserInWhiteList(processDefId)) {
            return true;
        } else {
            setProperty("headerTitle", ResourceBundleUtil.getMessage("general.header.unauthorized"));
            setProperty("view", "unauthorized");
            return false;
        }
    }

    private boolean isAssignmentExist(String activityId) {
        // check for assignment
        ApplicationContext ac = AppUtil.getApplicationContext();
        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
        WorkflowAssignment assignment = workflowManager.getAssignment(activityId);
        if (assignment != null) {
            return true;
        } else {
            setProperty("headerTitle", ResourceBundleUtil.getMessage("general.label.assignmentUnavailable"));
            setProperty("view", "assignmentUnavailable");
            return false;
        }
    }

    protected void processStarted(Form form, FormData formData) {
        setAlertMessage(getPropertyString("messageTitleShowAfterComplete"),getPropertyString("messageShowAfterComplete"));
        if (getPropertyString("redirectUrlAfterComplete") != null && !getPropertyString("redirectUrlAfterComplete").isEmpty()) {
            setProperty("view", "redirect");
            boolean redirectToParent = "Yes".equals(getPropertyString("showInPopupDialog"));            
            setRedirectUrl(getRedirectUrl(form, formData), redirectToParent);
        } else {
            setProperty("headerTitle", "Process Started");
            setProperty("view", "processStarted");
        }
    }
    
    protected String getRedirectUrl(Form form, FormData formData) {
        // determine redirect URL
        String redirectUrl = getPropertyString("redirectUrlAfterComplete");

        if (form != null && formData != null && redirectUrl != null && redirectUrl.trim().length() > 0 && getPropertyString("fieldPassover") != null && getPropertyString("fieldPassover").trim().length() > 0) {
            String passoverFieldName = getPropertyString("fieldPassover");
            Element passoverElement = FormUtil.findElement(passoverFieldName, form, formData, true);
            
            String passoverValue = "";
            
            if (passoverElement != null) {
                passoverValue = FormUtil.getElementPropertyValue(passoverElement, formData);
            } else if (FormUtil.PROPERTY_ID.equals(passoverFieldName)) {
                passoverValue = formData.getPrimaryKeyValue();
            }
            
            try {
                if ("append".equals(getPropertyString("fieldPassoverMethod"))) {
                    if (!redirectUrl.endsWith("/")) {
                        redirectUrl += "/";
                    }
                    redirectUrl += URLEncoder.encode(passoverValue, "UTF-8");
                } else {
                    if (redirectUrl.contains("?")) {
                        redirectUrl += "&";
                    } else {
                        redirectUrl += "?";
                    }
                    redirectUrl += URLEncoder.encode(getPropertyString("paramName"), "UTF-8") + "=" + URLEncoder.encode(passoverValue, "UTF-8");
                }
            } catch (Exception e) {}
        }
        return redirectUrl;
    }

    @Override
    public boolean isHomePageSupported() {
        if (!"Yes".equals(getPropertyString("showInPopupDialog"))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getCategory() {
        return UserviewBuilderPalette.CATEGORY_GENERAL;
    }

    @Override
    public String getAceJspPage(BootstrapUserviewTheme bootstrapTheme) {
        return getJspPage(bootstrapTheme.getRunProcessJsp());
    }

    @Override
    public String getAdminLteJspPage(BootstrapUserviewTheme bootstrapTheme) {
        return getJspPage(bootstrapTheme.getRunProcessJsp());
    }
}
