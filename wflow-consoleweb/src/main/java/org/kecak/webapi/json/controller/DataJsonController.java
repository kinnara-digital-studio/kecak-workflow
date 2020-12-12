package org.kecak.webapi.json.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.dao.DatalistDefinitionDao;
import org.joget.apps.app.dao.PackageDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.DatalistDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.model.PackageDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.app.service.AuditTrailManager;
import org.joget.apps.datalist.model.*;
import org.joget.apps.datalist.service.DataListService;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.lib.HiddenField;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormService;
import org.joget.apps.form.service.FormUtil;
import org.joget.apps.workflow.lib.AssignmentCompleteButton;
import org.joget.commons.util.FileManager;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.SetupManager;
import org.joget.commons.util.UuidGenerator;
import org.joget.workflow.model.*;
import org.joget.workflow.model.dao.WorkflowHelper;
import org.joget.workflow.model.dao.WorkflowProcessLinkDao;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kecak.apps.form.model.GridElement;
import org.kecak.utils.Declutter;
import org.kecak.webapi.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author aristo
 * <p>
 * Automatic API generation using Kecak UI builder
 */
@Controller
public class DataJsonController implements Declutter {
    private final static String FIELD_MESSAGE = "message";
    private final static String FIELD_DATA = "data";
    private final static String FIELD_VALIDATION_ERROR = "validation_error";
    private final static String FIELD_DIGEST = "digest";
    private final static String FIELD_TOTAL = "total";

    private final static String MESSAGE_VALIDATION_ERROR = "Validation Error";
    private final static String MESSAGE_SUCCESS = "Success";

    @Autowired
    private WorkflowManager workflowManager;
    @Autowired
    private AppService appService;
    @Autowired
    private AppDefinitionDao appDefinitionDao;
    @Autowired
    private DataListService dataListService;
    @Autowired
    private DatalistDefinitionDao datalistDefinitionDao;
    @Autowired
    private FormService formService;
    @Autowired
    private WorkflowProcessLinkDao workflowProcessLinkDao;
    @Autowired
    private FormDataDao formDataDao;
    @Autowired
    private AuditTrailManager auditTrailManager;
    @Autowired
    private WorkflowHelper workflowHelper;
    @Autowired
    private SetupManager setupManager;
    @Autowired
    private PackageDefinitionDao packageDefinitionDao;

    /**
     * Submit form into table, can be used to save master data
     *
     * @param request    HTTP Request, request body contains form field values
     * @param response   HTTP response
     * @param appId      Application ID
     * @param appVersion put 0 for current published app
     * @param formDefId  Form ID
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/form/(*:formDefId)", method = RequestMethod.POST)
    public void postFormSubmit(final HttpServletRequest request, final HttpServletResponse response,
                               @RequestParam("appId") final String appId,
                               @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                               @RequestParam("formDefId") final String formDefId)
            throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get current App Definition
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            // read request body and convert request body to json
            final JSONObject jsonBody = getRequestPayload(request);

            FormData formData = new FormData();
            formData.setPrimaryKeyValue(jsonBody.optString("id"));

            Form form = getForm(appDefinition, formDefId, formData);
            formData = formService.retrieveFormDataFromRequestMap(formData, convertJsonObjectToFormRow(form, formData, jsonBody));

            fillStoreBinderInFormData(jsonBody, form, formData, false);

            // check form permission
            if (!isAuthorize(form, formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            // submit form
            final FormData result = appService.submitForm(form, formData, false);

            // construct response
            final JSONObject jsonResponse = new JSONObject();
            Map<String, String> formErrors = getFormErrors(result);
            if (!formErrors.isEmpty()) {
                final JSONObject jsonError = createErrorObject(formErrors);
                jsonResponse.put(FIELD_VALIDATION_ERROR, jsonError);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_VALIDATION_ERROR);
            } else {
                // set current data as response
                response.setStatus(HttpServletResponse.SC_OK);

                FormUtil.executeLoadBinders(form, result);
                JSONObject jsonData = getData(form, result);

                jsonResponse.put(FIELD_DATA, jsonData);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
                jsonResponse.put(FIELD_DIGEST, getDigest(jsonData));
            }

            response.getWriter().write(jsonResponse.toString());
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Check form permission
     *
     * @param request
     * @param response
     * @param appId
     * @param appVersion
     * @param formDefId
     * @throws IOException
     * @throws JSONException
     */
    @RequestMapping(value = "/json/data/permission/app/(*:appId)/(~:appVersion)/form/(*:formDefId)", method = RequestMethod.POST)
    public void postCheckFormPermission(final HttpServletRequest request, final HttpServletResponse response,
                                        @RequestParam("appId") final String appId,
                                        @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                                        @RequestParam("formDefId") final String formDefId) throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // read request body and convert request body to json
            JSONObject jsonBody = getRequestPayload(request);

            FormData formData = new FormData();

            // get current App Definition
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            Form form = getForm(appDefinition, formDefId, formData);
            formData = formService.retrieveFormDataFromRequestMap(formData, convertJsonObjectToFormRow(form, formData, jsonBody));

            // check form permission
            if (!isAuthorize(form, formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            // construct response
            JSONObject jsonResponse = new JSONObject();

            // set current data as response
            response.setStatus(HttpServletResponse.SC_OK);
            jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Check form element permission
     *
     * @param request
     * @param response
     * @param appId
     * @param appVersion
     * @param formDefId
     * @param elementId
     * @throws IOException
     * @throws JSONException
     */
    @RequestMapping(value = "/json/data/permission/app/(*:appId)/(~:appVersion)/form/(*:formDefId)/(*:elementId)", method = RequestMethod.POST)
    public void postCheckFormElementPermission(final HttpServletRequest request, final HttpServletResponse response,
                                               @RequestParam("appId") final String appId,
                                               @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                                               @RequestParam("formDefId") final String formDefId,
                                               @RequestParam("elementId") final String elementId) throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get current App Definition
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            // read request body and convert request body to json
            JSONObject jsonBody = getRequestPayload(request);

            final FormData formData = new FormData();

            Form form = getForm(appDefinition, formDefId, formData);
            formService.retrieveFormDataFromRequestMap(formData, convertJsonObjectToFormRow(form, formData, jsonBody));

            // check element permission
            elementStream(form, formData)
                    .filter(e -> elementId.equals(e.getPropertyString(FormUtil.PROPERTY_ID)))
                    .findAny()
                    .orElseThrow(() -> new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this element"));

            // construct response
            JSONObject jsonResponse = new JSONObject();

            // set current data as response
            response.setStatus(HttpServletResponse.SC_OK);
            jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Deprecated, please use {@link DataJsonController#postFormValidation(HttpServletRequest, HttpServletResponse, String, Long, String, String)}
     *
     * @param request
     * @param response
     * @param appId
     * @param appVersion
     * @param formDefId
     * @param elementId
     * @throws IOException
     * @throws JSONException
     */
    @Deprecated
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/form/(*:formDefId)/(*:elementId)/validate", method = RequestMethod.POST)
    public void postFormValidationDeprecated(final HttpServletRequest request, final HttpServletResponse response,
                                             @RequestParam("appId") final String appId,
                                             @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                                             @RequestParam("formDefId") final String formDefId,
                                             @RequestParam("elementId") final String elementId) throws IOException, JSONException {

        postFormValidation(request, response, appId, appVersion, formDefId, elementId);
    }

    @RequestMapping(value = "/json/data/validate/app/(*:appId)/(~:appVersion)/form/(*:formDefId)/(*:elementId)", method = RequestMethod.POST)
    public void postFormValidation(final HttpServletRequest request, final HttpServletResponse response,
                                   @RequestParam("appId") final String appId,
                                   @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                                   @RequestParam("formDefId") final String formDefId,
                                   @RequestParam("elementId") final String elementId) throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get current App Definition
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            FormData formData = new FormData();

            Form form = getForm(appDefinition, formDefId, formData);

            // read request body and convert request body to json
            JSONObject jsonBody = getRequestPayload(request);

            formData = formService.retrieveFormDataFromRequestMap(formData, convertJsonObjectToFormRow(form, formData, jsonBody));

            // check form permission
            if (!isAuthorize(form, formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            // validate form
            FormData result = validateFormData(form, formData);

            // construct response
            JSONObject jsonResponse = new JSONObject();
            Map<String, String> formErrors = getFormErrors(result);
            if (!formErrors.isEmpty()) {
                JSONObject jsonError = createErrorObject(formErrors);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_VALIDATION_ERROR);
                jsonResponse.put(FIELD_VALIDATION_ERROR, jsonError);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
            } else {
                // set current data as response
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
                response.getWriter().write(jsonResponse.toString());
            }
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Update data in Form
     *
     * @param request    HTTP Request
     * @param response   HTTP Response
     * @param appId      Application ID
     * @param appVersion Application version
     * @param formDefId  Form Definition ID
     * @param primaryKey Primary Key
     * @throws IOException
     * @throws JSONException
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/form/(*:formDefId)/(*:primaryKey)", method = RequestMethod.PUT)
    public void putFormData(final HttpServletRequest request, final HttpServletResponse response,
                            @RequestParam("appId") final String appId,
                            @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                            @RequestParam("formDefId") final String formDefId,
                            @RequestParam("primaryKey") final String primaryKey)
            throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {

            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            // read request body and convert request body to json
            JSONObject jsonBody = getRequestPayload(request);

            FormData formData = new FormData();
            formData.setPrimaryKeyValue(primaryKey);

            Form form = getForm(appDefinition, formDefId, formData);
            formData = formService.retrieveFormDataFromRequestMap(formData, convertJsonObjectToFormRow(form, formData, jsonBody));

            fillStoreBinderInFormData(jsonBody, form, formData, false);

            // check form permission
            if (!isAuthorize(form, formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            // submit form
            final FormData result = appService.submitForm(form, formData, false);

            // construct response
            final JSONObject jsonResponse = new JSONObject();
            Map<String, String> formErrors = getFormErrors(result);
            if (!formErrors.isEmpty()) {
                final JSONObject jsonError = createErrorObject(formErrors);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_VALIDATION_ERROR);
                jsonResponse.put(FIELD_VALIDATION_ERROR, jsonError);
            } else {
                // set current data as response
                response.setStatus(HttpServletResponse.SC_OK);


                JSONObject jsonData = getData(form, result);
                jsonResponse.put(FIELD_DATA, jsonData);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
                jsonResponse.put(FIELD_DIGEST, getDigest(jsonData));
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Get Form Data
     *
     * @param request    HTTP Request
     * @param response   HTTP Response
     * @param appId      Application ID
     * @param appVersion Application version
     * @param formDefId  Form Definition ID
     * @param primaryKey Primary Key
     * @param digest     Digest
     * @throws IOException
     * @throws JSONException
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/form/(*:formDefId)/(*:primaryKey)", method = RequestMethod.GET)
    public void getFormData(final HttpServletRequest request, final HttpServletResponse response,
                            @RequestParam("appId") final String appId,
                            @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                            @RequestParam("formDefId") final String formDefId,
                            @RequestParam("primaryKey") final String primaryKey,
                            @RequestParam(value = "asLabel", defaultValue = "false") final Boolean asLabel,
                            @RequestParam(value = "asAttachmentUrl", defaultValue = "false") final Boolean asAttachmentUrl,
                            @RequestParam(value = "asOptions", defaultValue="false") final Boolean asOptions,
                            @RequestParam(value = "digest", required = false) final String digest)
            throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            final FormData formData = new FormData();
            formData.setPrimaryKeyValue(primaryKey);

            if (asAttachmentUrl) {
                formData.addRequestParameterValues(FileDownloadSecurity.PARAMETER_AS_LINK, new String[]{"true"});
            }

            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            Form form = getForm(appDefinition, formDefId, formData);

            if (asLabel) {
                FormUtil.setReadOnlyProperty(form, true, true);
            }

            // check form permission
            if (!isAuthorize(form, formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            // construct response
            JSONObject jsonData = getData(form, formData, asOptions);

            String currentDigest = getDigest(jsonData);

            JSONObject jsonResponse = new JSONObject();

            if (!Objects.equals(currentDigest, digest)) {
                jsonResponse.put(FIELD_DATA, jsonData);
            }

            jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
            jsonResponse.put(FIELD_DIGEST, currentDigest);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Delete Form Data
     *
     * @param request    HTTP Request
     * @param response   HTTP Response
     * @param appId      Application ID
     * @param appVersion Application version
     * @param formDefId  Form Definition ID
     * @param primaryKey Primary Key
     * @throws IOException
     * @throws JSONException
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/form/(*:formDefId)/(*:primaryKey)", method = RequestMethod.DELETE)
    public void deleteFormData(final HttpServletRequest request, final HttpServletResponse response,
                               @RequestParam("appId") final String appId,
                               @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                               @RequestParam("formDefId") final String formDefId,
                               @RequestParam("primaryKey") final String primaryKey,
                               @RequestParam(value = "digest", required = false) final String digest)
            throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            final FormData formData = new FormData();
            formData.setPrimaryKeyValue(primaryKey);

            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            @Nonnull
            Form form = getForm(appDefinition, formDefId, formData);

            // check form permission
            if (!isAuthorize(form, formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            // construct response
            JSONObject jsonData = getData(form, formData);

            String currentDigest = getDigest(jsonData);

            JSONObject jsonResponse = new JSONObject();

            if (!Objects.equals(currentDigest, digest)) {
                jsonResponse.put(FIELD_DATA, jsonData);
            }

            jsonResponse.put(FIELD_DIGEST, currentDigest);

            // delete data
            deleteData(form, formData, true);

            jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Get Element Data
     * Execute element's load binder
     *
     * @param request
     * @param response
     * @param appId
     * @param appVersion
     * @param formDefId
     * @param elementId
     * @param primaryKey
     * @param digest
     * @throws IOException
     * @throws JSONException
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/form/(*:formDefId)/(*:elementId)/(*:primaryKey)", method = RequestMethod.GET)
    public void getElementData(final HttpServletRequest request, final HttpServletResponse response,
                               @RequestParam("appId") final String appId,
                               @RequestParam(value = "appVersion", defaultValue = "0") final Long appVersion,
                               @RequestParam("formDefId") final String formDefId,
                               @RequestParam("elementId") final String elementId,
                               @RequestParam("primaryKey") final String primaryKey,
                               @RequestParam(value = "includeSubForm", defaultValue = "false") final Boolean includeSubForm,
                               @RequestParam(value = "digest", required = false) final String digest,
                               @RequestParam(value = "asAttachmentUrl", defaultValue = "false") final Boolean asAttachmentUrl,
                               @RequestParam(value = "asLabel", defaultValue = "false") final Boolean asLabel,
                               @RequestParam(value = "asOptions", defaultValue = "false") final Boolean asOptions)
            throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            final FormData formData = new FormData();
            formData.setPrimaryKeyValue(primaryKey);

            if (asAttachmentUrl) {
                formData.addRequestParameterValues(FileDownloadSecurity.PARAMETER_AS_LINK, new String[]{"true"});
            }

            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            Form form = getForm(appDefinition, formDefId, formData);

            if (asLabel) {
                FormUtil.setReadOnlyProperty(form, true, true);
            }

            // check form permission
            if (!isAuthorize(form, formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            // construct response
            JSONObject jsonData = getData(form, formData, asOptions);
            Object fieldData = jsonStream(jsonData)
                    .filter(elementId::equals)
                    .findFirst()
                    .map(throwableFunction(jsonData::get))
                    .orElse(null);

            String currentDigest = getDigest(fieldData);

            JSONObject jsonResponse = new JSONObject();

            if (!Objects.equals(currentDigest, digest)) {
                jsonResponse.put(FIELD_DATA, fieldData);
            }

            jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
            jsonResponse.put(FIELD_DIGEST, currentDigest);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/form/(*:formDefId)/(*:elementId)/options", method = RequestMethod.GET)
    public void getElementOptionsData(final HttpServletRequest request, final HttpServletResponse response,
                                      @RequestParam("appId") final String appId,
                                      @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                                      @RequestParam("formDefId") final String formDefId,
                                      @RequestParam("elementId") final String elementId,
                                      @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                      @RequestParam(value = "start", required = false, defaultValue = "0") final Integer start,
                                      @RequestParam(value = "rows", required = false, defaultValue = "0") final Integer rows,
                                      @RequestParam(value = "includeSubForm", required = false, defaultValue = "false") final Boolean includeSubForm,
                                      @RequestParam(value = "digest", required = false) final String digest)
            throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            final FormData formData = new FormData();

            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            Form form = getForm(appDefinition, formDefId, formData);

            // check form permission
            if (!isAuthorize(form, formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            Element element = FormUtil.findElement(elementId, form, formData, includeSubForm);
            if (element == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid element [" + elementId + "]");
            }

            long pageSize = rows != null && rows > 0 ? rows : page != null && page > 0 ? DataList.DEFAULT_PAGE_SIZE : DataList.MAXIMUM_PAGE_SIZE;
            long rowStart = start != null ? start : page != null && page > 0 ? ((page - 1) * pageSize) : 0;

            @Nonnull
            FormRowSet formRows = FormUtil.getElementPropertyOptionsMap(element, formData).stream()
                    .skip(rowStart)
                    .limit(pageSize)
                    .collect(Collectors.toCollection(FormRowSet::new));

            // construct response

            @Nonnull
            JSONArray jsonArrayData = convertFormRowSetToJsonArray(element, formData, formRows);

            @Nullable
            String currentDigest = getDigest(jsonArrayData);

            JSONObject jsonResponse = new JSONObject();

            if (!Objects.equals(currentDigest, digest)) {
                jsonResponse.put(FIELD_DATA, jsonArrayData);
            }

            jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
            jsonResponse.put(FIELD_DIGEST, currentDigest);

            response.setStatus(HttpServletResponse.SC_OK);

            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Get List Count
     *
     * @param request    HTTP Request
     * @param response   HTTP Response
     * @param appId      Application ID
     * @param appVersion Application version
     * @param dataListId DataList ID
     * @throws IOException
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/datalist/(*:dataListId)/count", method = RequestMethod.GET)
    public void getListCount(final HttpServletRequest request, final HttpServletResponse response,
                             @RequestParam("appId") final String appId,
                             @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                             @RequestParam("dataListId") final String dataListId)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            // get dataList
            DataList dataList = getDataList(appDefinition, dataListId);

            getCollectFilters(request.getParameterMap(), dataList);

            int total = dataList.getSize();

            try {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put(FIELD_TOTAL, total);
                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        } catch (ApiException e) {
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * Retrieve dataList data
     *
     * @param request    HTTP Request
     * @param response   HTTP Response
     * @param appId      Application ID
     * @param appVersion Application version
     * @param dataListId DataList ID
     * @param page       paging every 10 rows, page = 0 will show all data without paging
     * @param start      from row index (index starts from 0)
     * @param sort       order list by specified field name
     * @param desc       optional true/false
     * @param digest     hash calculation of data json
     * @throws IOException
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/datalist/(*:dataListId)", method = RequestMethod.GET)
    public void getList(final HttpServletRequest request, final HttpServletResponse response,
                        @RequestParam("appId") final String appId,
                        @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                        @RequestParam("dataListId") final String dataListId,
                        @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                        @RequestParam(value = "start", required = false) final Integer start,
                        @RequestParam(value = "rows", required = false, defaultValue = "0") final Integer rows,
                        @RequestParam(value = "sort", required = false) final String sort,
                        @RequestParam(value = "desc", required = false, defaultValue = "false") final Boolean desc,
                        @RequestParam(value = "digest", required = false) final String digest)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            // get dataList
            DataList dataList = getDataList(appDefinition, dataListId);

            // configure sorting
            if (sort != null) {
                dataList.setDefaultSortColumn(sort);

                // order ASC / DESC
                dataList.setDefaultOrder(desc ? DataList.ORDER_DESCENDING_VALUE : DataList.ORDER_ASCENDING_VALUE);
            }

            // paging
            int pageSize = rows != null && rows > 0 ? rows : page != null && page > 0 ? dataList.getPageSize() : DataList.MAXIMUM_PAGE_SIZE;
            int rowStart = start != null ? start : page != null && page > 0 ? ((page - 1) * pageSize) : 0;

            getCollectFilters(request.getParameterMap(), dataList);

            try {
                JSONArray jsonData = Optional.of(dataList)
                        .map(d -> d.getRows(pageSize, rowStart))
                        .map(collection -> (DataListCollection<Map<String, Object>>) collection)
                        .orElse(new DataListCollection<>())
                        .stream()

                        // reformat content value
                        .map(row -> formatRow(dataList, row))

                        // collect as JSON
                        .collect(jsonCollector());

                String currentDigest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();

                jsonResponse.put(FIELD_TOTAL, dataList.getSize());

                if (!Objects.equals(digest, currentDigest))
                    jsonResponse.put(FIELD_DATA, jsonData);

                jsonResponse.put(FIELD_DIGEST, currentDigest);

                response.setStatus(HttpServletResponse.SC_OK);

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        } catch (ApiException e) {
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * Retrieve dataList data
     *
     * @param request    HTTP Request
     * @param response   HTTP Response
     * @param appId      Application ID
     * @param appVersion Application version
     * @param dataListId DataList ID
     * @param page       paging every 10 rows, page = 0 will show all data without paging
     * @param start      from row index (index starts from 0)
     * @param sort       order list by specified field name
     * @param desc       optional true/false
     * @param digest     hash calculation of data json
     * @throws IOException
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/datalist/(*:dataListId)/form/(*:formDefId)", method = RequestMethod.GET)
    public void getListForm(final HttpServletRequest request, final HttpServletResponse response,
                            @RequestParam("appId") final String appId,
                            @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                            @RequestParam("dataListId") final String dataListId,
                            @RequestParam("formDefId") final String formDefId,
                            @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                            @RequestParam(value = "start", required = false) final Integer start,
                            @RequestParam(value = "rows", required = false, defaultValue = "0") final Integer rows,
                            @RequestParam(value = "sort", required = false) final String sort,
                            @RequestParam(value = "desc", required = false, defaultValue = "false") final Boolean desc,
                            @RequestParam(value = "asLabel", defaultValue = "false") final Boolean asLabel,
                            @RequestParam(value = "asOptions", defaultValue = "false") final Boolean asOptions,
                            @RequestParam(value = "asAttachmentUrl", defaultValue = "false") final Boolean asAttachmentUrl,
                            @RequestParam(value = "digest", required = false) final String digest)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            // get dataList
            DataList dataList = getDataList(appDefinition, dataListId);

            // configure sorting
            if (sort != null) {
                dataList.setDefaultSortColumn(sort);

                // order ASC / DESC
                dataList.setDefaultOrder(desc ? DataList.ORDER_DESCENDING_VALUE : DataList.ORDER_ASCENDING_VALUE);
            }

            // paging
            int pageSize = rows != null && rows > 0 ? rows : page != null && page > 0 ? dataList.getPageSize() : DataList.MAXIMUM_PAGE_SIZE;
            int rowStart = start != null ? start : page != null && page > 0 ? ((page - 1) * pageSize) : 0;

            getCollectFilters(request.getParameterMap(), dataList);

            Form form = getForm(appDefinition, formDefId, new FormData());

            if (asLabel) {
                FormUtil.setReadOnlyProperty(form, true, true);
            }

            try {
                JSONArray jsonData = Optional.of(dataList)
                        .map(d -> d.getRows(pageSize, rowStart))
                        .map(collection -> (DataListCollection<Map<String, Object>>) collection)
                        .orElse(new DataListCollection<>())
                        .stream()
                        .map(row -> row.get(dataList.getBinder().getPrimaryKeyColumnName()))
                        .map(String::valueOf)

                        //load form
                        .map(throwableFunction(s -> {
                            final FormData formData = new FormData();
                            formData.setPrimaryKeyValue(s);

                            if (asAttachmentUrl) {
                                formData.addRequestParameterValues(FileDownloadSecurity.PARAMETER_AS_LINK, new String[]{"true"});
                            }

                            return Optional.of(form)
                                    .filter(f -> isAuthorize(f, formData))
                                    .map(throwableFunction(f -> getData(f, formData, asOptions), (Exception e) -> null))
                                    .orElse(null);

                        }))

                        .filter(Objects::nonNull)

                        // collect as JSON
                        .collect(jsonCollector());

                String currentDigest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();

                jsonResponse.put(FIELD_TOTAL, dataList.getSize());

                if (!Objects.equals(digest, currentDigest))
                    jsonResponse.put(FIELD_DATA, jsonData);

                jsonResponse.put(FIELD_DIGEST, currentDigest);

                response.setStatus(HttpServletResponse.SC_OK);

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        } catch (ApiException e) {
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * Start new process
     *
     * @param request    HTTP Request, request body contains form field values
     * @param response   HTTP Response
     * @param appId      Application ID
     * @param appVersion put 0 for current published app
     * @param processId  Process ID
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/process/(*:processId)", method = RequestMethod.POST)
    public void postProcessStart(final HttpServletRequest request, final HttpServletResponse response,
                                 @RequestParam("appId") String appId,
                                 @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                                 @RequestParam("processId") String processId)
            throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            // get processDefId
            String processDefId = Optional.ofNullable(appService.getWorkflowProcessForApp(appDefinition.getAppId(), appDefinition.getVersion().toString(), processId))
                    .map(WorkflowProcess::getId)
                    .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid process [" + processId + "] in application [" + appDefinition.getAppId() + "] version [" + appDefinition.getVersion() + "]"));

            // check for permission
            if (!workflowManager.isUserInWhiteList(processDefId)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] is not allowed to start process [" + processDefId + "]");
            }

            // get process form
            PackageActivityForm packageActivityForm = Optional.ofNullable(appService.viewStartProcessForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), processDefId, null, ""))
                    .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Start Process [" + processDefId + "] has not been mapped to form"));

            Form form = Optional.of(packageActivityForm)
                    .map(PackageActivityForm::getForm)
                    .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Error retrieving form for [" + packageActivityForm.getActivityDefId() + "]"));

            // read request body and convert request body to json
            JSONObject jsonBody = getRequestPayload(request);
            final FormData formData = formService.retrieveFormDataFromRequestMap(null, convertJsonObjectToFormRow(form, null, jsonBody));
            fillStoreBinderInFormData(jsonBody, form, formData, true);

            // check form permission
            if (!isAuthorize(form, formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            JSONObject jsonResponse = new JSONObject();

            Map<String, String> workflowVariables = generateWorkflowVariable(form, formData);

            // trigger run process
            WorkflowProcessResult processResult = appService.submitFormToStartProcess(packageActivityForm, formData, workflowVariables, null);

            Map<String, String> formErrors = getFormErrors(formData);
            if (!formErrors.isEmpty()) {
                JSONObject jsonError = new JSONObject(formErrors);
                jsonResponse.put(FIELD_VALIDATION_ERROR, jsonError);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_VALIDATION_ERROR);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);

                FormUtil.executeLoadBinders(form, formData);
                JSONObject jsonData = getData(form, formData);

                jsonResponse.put(FIELD_DATA, jsonData);

                // construct response
//                FormRow row = loadFormData(form, formData.getPrimaryKeyValue());

//                final JSONObject jsonData = new JSONObject(row);

                Optional.ofNullable(processResult)
                        .map(WorkflowProcessResult::getProcess)
                        .map(WorkflowProcess::getInstanceId)
                        .map(workflowManager::getAssignmentByProcess)
                        .ifPresent(nextAssignment -> {
                            try {
                                JSONObject jsonProcess = new JSONObject();
                                jsonProcess.put("processId", nextAssignment.getProcessId());
                                jsonProcess.put("activityId", nextAssignment.getActivityId());
                                jsonProcess.put("dateCreated", nextAssignment.getDateCreated());
                                jsonProcess.put("dueDate", nextAssignment.getDueDate());
                                jsonProcess.put("priority", nextAssignment.getPriority());
                                jsonProcess.put("assigneeList", Optional.of(nextAssignment)
                                        .map(WorkflowAssignment::getAssigneeList)
                                        .map(Collection::stream)
                                        .orElseGet(Stream::empty)
                                        .collect(Collectors.joining(";")));
                                jsonProcess.put("assigneeId", nextAssignment.getAssigneeId());
                                jsonProcess.put("assigneeName", nextAssignment.getAssigneeName());

                                Collection<WorkflowActivity> processActivities = processResult.getActivities();
                                if (isNotEmpty(processActivities)) {
                                    jsonProcess.put("activityIds", new JSONArray(processActivities.stream().map(WorkflowActivity::getId).collect(Collectors.toList())));
                                }

                                jsonResponse.put("process", jsonProcess);
                            } catch (JSONException e) {
                                LogUtil.error(getClass().getName(), e, e.getMessage());
                            }
                        });

                String digest = getDigest(jsonData);
                jsonResponse.put(FIELD_DATA, jsonData);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
                jsonResponse.put(FIELD_DIGEST, digest);
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Post Assignment Complete
     * <p>
     * Complete assignment form
     *
     * @param request      HTTP Request, request body contains form field values
     * @param response     HTTP Response
     * @param assignmentId Assignment ID
     */
    @RequestMapping(value = "/json/data/assignment/(*:assignmentId)", method = {RequestMethod.POST, RequestMethod.PUT})
    public void postAssignmentComplete(final HttpServletRequest request, final HttpServletResponse response,
                                       @RequestParam("assignmentId") String assignmentId)
            throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get assignment
            WorkflowAssignment assignment = getAssignment(assignmentId);

            // get application definition
            AppDefinition appDefinition = getApplicationDefinition(assignment);

            // read request body and convert request body to json
            final JSONObject jsonBody = getRequestPayload(request);

            FormData formData = new FormData();
            formData.setActivityId(assignment.getActivityId());
            formData.setProcessId(assignment.getProcessId());

            // get assignment form
            Form form = getAssignmentForm(appDefinition, assignment, formData);
            formData = formService.retrieveFormDataFromRequestMap(formData, convertJsonObjectToFormRow(form, formData, jsonBody));

            fillStoreBinderInFormData(jsonBody, form, formData, true);

            // check form permission
            if (!isAuthorize(form, formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            Map<String, String> workflowVariables = generateWorkflowVariable(form, formData);

            FormData resultFormData = appService.completeAssignmentForm(form, assignment, formData, workflowVariables);

            // return processResult
            JSONObject jsonResponse = new JSONObject();
            Map<String, String> formErrors = getFormErrors(formData);
            ;
            if (!formErrors.isEmpty()) {
                JSONObject jsonError = new JSONObject(formErrors);
                jsonResponse.put(FIELD_VALIDATION_ERROR, jsonError);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_VALIDATION_ERROR);

            } else {
                Optional.ofNullable(resultFormData)
                        .map(FormData::getProcessId)
                        .map(workflowManager::getAssignmentByProcess)
                        .ifPresent(throwableConsumer(nextAssignment -> {
                            JSONObject jsonProcess = new JSONObject();
                            jsonProcess.put("processId", nextAssignment.getProcessId());
                            jsonProcess.put("activityId", nextAssignment.getActivityId());
                            jsonProcess.put("dateCreated", nextAssignment.getDateCreated());
                            jsonProcess.put("dueDate", nextAssignment.getDueDate());
                            jsonProcess.put("priority", nextAssignment.getPriority());

                            jsonResponse.put("process", jsonProcess);
                        }));

                response.setStatus(HttpServletResponse.SC_OK);

                // construct response
                FormUtil.executeLoadBinders(form, resultFormData);
                JSONObject jsonData = getData(form, resultFormData);

                String digest = getDigest(jsonData);

                jsonResponse.put(FIELD_DATA, jsonData);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
                jsonResponse.put(FIELD_DIGEST, digest);
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Post Assignment Complete by assignment process id
     * <p>
     * Complete assignment form
     *
     * @param request   HTTP Request, request body contains form field values
     * @param response  HTTP Response
     * @param processId Assingment Process ID
     */
    @RequestMapping(value = "/json/data/assignment/process/(*:processId)", method = {RequestMethod.POST, RequestMethod.PUT})
    public void postAssignmentCompleteByProcess(final HttpServletRequest request, final HttpServletResponse response,
                                                @RequestParam("processId") String processId,
                                                @RequestParam(value = "activityDefId", defaultValue = "") String activityDefId)
            throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get assignment
            WorkflowAssignment assignment = getAssignmentByProcess(processId, activityDefId);

            // get application definition
            AppDefinition appDefinition = getApplicationDefinition(assignment);

            // read request body and convert request body to json
            final JSONObject jsonBody = getRequestPayload(request);

            FormData formData = new FormData();
            formData.setActivityId(assignment.getActivityId());
            formData.setProcessId(assignment.getProcessId());

            // get assignment form
            Form form = getAssignmentForm(appDefinition, assignment, formData);

            formData = formService.retrieveFormDataFromRequestMap(formData, convertJsonObjectToFormRow(form, formData, jsonBody));

            fillStoreBinderInFormData(jsonBody, form, formData, true);

            // check form permission
            if (!isAuthorize(form, formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            Map<String, String> workflowVariables = generateWorkflowVariable(form, formData);

            FormData resultFormData = appService.completeAssignmentForm(form, assignment, formData, workflowVariables);

            // return processResult
            JSONObject jsonResponse = new JSONObject();
            Map<String, String> formErrors = getFormErrors(resultFormData);
            if (!formErrors.isEmpty()) {
                JSONObject jsonError = new JSONObject(formErrors);
                jsonResponse.put(FIELD_VALIDATION_ERROR, jsonError);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_VALIDATION_ERROR);

            } else {
                Optional.ofNullable(resultFormData)
                        .map(FormData::getProcessId)
                        .map(workflowManager::getAssignmentByProcess)
                        .ifPresent(throwableConsumer(nextAssignment -> {
                            JSONObject jsonProcess = new JSONObject();
                            jsonProcess.put("processId", nextAssignment.getProcessId());
                            jsonProcess.put("activityId", nextAssignment.getActivityId());
                            jsonProcess.put("dateCreated", nextAssignment.getDateCreated());
                            jsonProcess.put("dueDate", nextAssignment.getDueDate());
                            jsonProcess.put("priority", nextAssignment.getPriority());

                            jsonResponse.put("process", jsonProcess);
                        }));

                response.setStatus(HttpServletResponse.SC_OK);

                // construct response
                FormUtil.executeLoadBinders(form, resultFormData);
                JSONObject jsonData = getData(form, resultFormData);

                String digest = getDigest(jsonData);

                jsonResponse.put(FIELD_DATA, jsonData);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
                jsonResponse.put(FIELD_DIGEST, digest);
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Get Assignment
     * <p>
     * Get assignment data
     *
     * @param request      HTTP Request
     * @param response     HTTP Response
     * @param assignmentId Assingment ID
     * @param digest       Digest
     * @throws IOException
     */
    @RequestMapping(value = "/json/data/assignment/(*:assignmentId)", method = RequestMethod.GET)
    public void getAssignment(final HttpServletRequest request, final HttpServletResponse response,
                              @RequestParam("assignmentId") final String assignmentId,
                              @RequestParam(value = "asLabel", defaultValue = "false") final Boolean asLabel,
                              @RequestParam(value = "asOptions", defaultValue = "false") final Boolean asOptions,
                              @RequestParam(value = "asAttachmentUrl", defaultValue = "false") final Boolean asAttachmentUrl,
                              @RequestParam(value = "digest", required = false) String digest)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            WorkflowAssignment assignment = getAssignment(assignmentId);

            JSONObject jsonData = internalGetAssignmentJsonData(assignment, asLabel, asOptions, asAttachmentUrl);

            try {
                String currentDigest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put(FIELD_DIGEST, currentDigest);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);

                if (!Objects.equals(digest, currentDigest))
                    jsonResponse.put(FIELD_DATA, jsonData);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Get Assignment by Process ID
     *
     * @param request
     * @param response
     * @param processId Process ID
     * @param digest
     * @throws IOException
     */
    @RequestMapping(value = "/json/data/assignment/process/(*:processId)", method = RequestMethod.GET)
    public void getAssignmentByProcess(final HttpServletRequest request, final HttpServletResponse response,
                                       @RequestParam("processId") final String processId,
                                       @RequestParam(value = "activityDefId", defaultValue = "") final String activityDefId,
                                       @RequestParam(value = "asLabel", defaultValue = "false") final Boolean asLabel,
                                       @RequestParam(value = "asOptions", defaultValue = "false") final Boolean asOptions,
                                       @RequestParam(value = "asAttachmentUrl", defaultValue = "false") final Boolean asAttachmentUrl,
                                       @RequestParam(value = "digest", required = false) String digest)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            WorkflowAssignment assignment = getAssignmentByProcess(processId, activityDefId);

            JSONObject jsonData = internalGetAssignmentJsonData(assignment, asLabel, asOptions, asAttachmentUrl);

            try {
                String currentDigest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put(FIELD_DIGEST, currentDigest);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);

                if (!Objects.equals(digest, currentDigest))
                    jsonResponse.put(FIELD_DATA, jsonData);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Get assignment using form
     *
     * @param request
     * @param response
     * @param formDefId
     * @param assignmentId
     * @param asLabel
     * @param asAttachmentUrl
     * @param digest
     * @throws IOException
     */
    @RequestMapping(value = "/json/data/assignment/form/(*:formDefId)/(*:assignmentId)", method = RequestMethod.GET)
    public void getAssignmentUsingForm(final HttpServletRequest request, final HttpServletResponse response,
                                       @RequestParam("formDefId") final String formDefId,
                                       @RequestParam("assignmentId") final String assignmentId,
                                       @RequestParam(value = "asLabel", defaultValue = "false") final Boolean asLabel,
                                       @RequestParam(value = "asOptions", defaultValue = "false") final Boolean asOptions,
                                       @RequestParam(value = "asAttachmentUrl", defaultValue = "false") final Boolean asAttachmentUrl,
                                       @RequestParam(value = "digest", required = false) String digest)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            WorkflowAssignment assignment = getAssignment(assignmentId);

            FormData formData = getAssignmentFormData(assignment, asAttachmentUrl);

            AppDefinition appDefinition = getApplicationDefinition(assignment);

            Form form = getForm(appDefinition, formDefId, formData);

            if (asLabel) {
                FormUtil.setReadOnlyProperty(form, true, true);
            }

            // check form permission
            if (!isAuthorize(form, formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            try {
                // construct response
                JSONObject jsonData = getData(form, formData, asOptions);

                String currentDigest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();

                if (!Objects.equals(currentDigest, digest)) {
                    jsonResponse.put(FIELD_DATA, jsonData);
                }

                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
                jsonResponse.put(FIELD_DIGEST, currentDigest);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, e);
            }
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Get assignment by process using form
     *
     * @param request
     * @param response
     * @param formDefId
     * @param processId
     * @param asLabel
     * @param asAttachmentUrl
     * @param digest
     * @throws IOException
     */
    @RequestMapping(value = "/json/data/assignment/form/(*:formDefId)/process/(*:processId)", method = RequestMethod.GET)
    public void getAssignmentByProcessUsingForm(final HttpServletRequest request, final HttpServletResponse response,
                                                @RequestParam("formDefId") final String formDefId,
                                                @RequestParam("processId") final String processId,
                                                @RequestParam(value = "activityDefId", defaultValue = "") final String activityDefId,
                                                @RequestParam(value = "asLabel", defaultValue = "false") final Boolean asLabel,
                                                @RequestParam(value = "asOptions", defaultValue = "false") final Boolean asOptions,
                                                @RequestParam(value = "asAttachmentUrl", defaultValue = "false") final Boolean asAttachmentUrl,
                                                @RequestParam(value = "digest", required = false) String digest)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            WorkflowAssignment assignment = getAssignmentByProcess(processId, activityDefId);

            FormData formData = getAssignmentFormData(assignment, asAttachmentUrl);

            AppDefinition appDefinition = getApplicationDefinition(assignment);

            Form form = getForm(appDefinition, formDefId, formData);

            if (asLabel) {
                FormUtil.setReadOnlyProperty(form, true, true);
            }

            // check form permission
            if (!isAuthorize(form, formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            try {
                // construct response
                JSONObject jsonData = getData(form, formData, asOptions);

                String currentDigest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();

                if (!Objects.equals(currentDigest, digest)) {
                    jsonResponse.put(FIELD_DATA, jsonData);
                }

                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
                jsonResponse.put(FIELD_DIGEST, currentDigest);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, e);
            }
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Get Assignment Count
     *
     * @param request    HTTP Request
     * @param response   HTTP Response
     * @param appId      Application ID
     * @param appVersion Application version
     * @param processId  Process Definition ID
     * @throws IOException
     */
    @RequestMapping(value = "/json/data/assignments/count", method = RequestMethod.GET)
    public void getAssignmentsCount(final HttpServletRequest request, final HttpServletResponse response,
                                    @RequestParam(value = "appId", required = false) final String appId,
                                    @RequestParam(value = "version", required = false, defaultValue = "0") final Long appVersion,
                                    @RequestParam(value = "processId", required = false) final String processId) throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));
            String processDefId = validateAndDetermineProcessDefId(appDefinition, processId);
            int total = workflowManager.getAssignmentSize(appId, processDefId, null);

            try {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put(FIELD_TOTAL, total);
                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        } catch (ApiException e) {
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * Get Assignment List
     *
     * @param request    HTTP Request
     * @param response   HTTP Response
     * @param appId      Application ID
     * @param appVersion Application version
     * @param processId  Process Def ID
     * @param page       Page starts from 1
     * @param start      From index (index starts from 0)
     * @param rows       Page size (rows = 0 means load all data)
     * @param sort       Sort by field
     * @param desc       Descending (true/false)
     * @param digest     Digest
     * @throws IOException
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/assignments", method = RequestMethod.GET)
    public void getAssignments(final HttpServletRequest request, final HttpServletResponse response,
                               @RequestParam(value = "appId") final String appId,
                               @RequestParam(value = "appVersion", defaultValue = "0") final Long appVersion,
                               @RequestParam(value = "processId", required = false) final String processId,
                               @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                               @RequestParam(value = "start", required = false) final Integer start,
                               @RequestParam(value = "rows", required = false, defaultValue = "0") final Integer rows,
                               @RequestParam(value = "sort", required = false) final String sort,
                               @RequestParam(value = "desc", required = false, defaultValue = "false") final Boolean desc,
                               @RequestParam(value = "digest", required = false) final String digest)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));
            String processDefId = validateAndDetermineProcessDefId(appDefinition, processId);

            int pageSize = rows != null && rows > 0 ? rows : page != null && page > 0 ? DataList.DEFAULT_PAGE_SIZE : DataList.MAXIMUM_PAGE_SIZE;
            int rowStart = start != null ? start : page != null && page > 0 ? ((page - 1) * pageSize) : 0;

            // get total data
            int total = workflowManager.getAssignmentSize(appId, processDefId, null);


            FormRowSet resultRowSet = Optional.of(appDefinition)
                    .map(AppDefinition::getPackageDefinition)
                    .map(PackageDefinition::getId)
                    .map(s -> workflowManager.getAssignmentPendingAndAcceptedList(s, processDefId, null, sort, desc, rowStart, pageSize))
                    .map(Collection::stream)
                    .orElseGet(Stream::empty)
                    .map(WorkflowAssignment::getActivityId)
                    .map(workflowManager::getAssignment)
                    .filter(Objects::nonNull)
                    .map(throwableFunction(assignment -> {
                        // get application definition
                        AppDefinition assignmentAppDefinition = getApplicationDefinition(assignment);

                        FormData formData = new FormData();

                        // get form
                        Form form = getAssignmentForm(assignmentAppDefinition, assignment, formData);

                        // check form permission
                        if (!isAuthorize(form, formData)) {
                            return null;
                        }

                        FormRow row = getFormRow(form, formData.getPrimaryKeyValue());
                        row.setProperty("activityId", assignment.getActivityId());
                        row.setProperty("processId", assignment.getProcessId());
                        row.setProperty("assigneeId", assignment.getAssigneeId());

                        return row;
                    }))
                    .filter(Objects::nonNull)
                    .collect(FormRowSet::new, FormRowSet::add, FormRowSet::addAll);

            JSONArray jsonData = new JSONArray();
            for (FormRow row : resultRowSet) {
                try {
                    JSONObject jsonRow = new JSONObject();
                    for (Object key : row.keySet()) {
                        jsonRow.put(key.toString(), row.get(key));
                    }
                    jsonData.put(jsonRow);
                } catch (JSONException e) {
                    jsonData.put(new JSONObject(row));
                }
            }

            try {
                JSONObject jsonResponse = new JSONObject();
                String currentDigest = getDigest(jsonData);

                jsonResponse.put(FIELD_TOTAL, total);

                if (!Objects.equals(currentDigest, digest))
                    jsonResponse.put(FIELD_DATA, jsonData);

                jsonResponse.put(FIELD_DIGEST, currentDigest);

                response.setStatus(HttpServletResponse.SC_OK);

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        } catch (ApiException e) {
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * Delete assignment data and abort process
     *
     * @param request
     * @param response
     * @param assignmentId
     */
    @RequestMapping(value = "/json/data/assignment/(*:assignmentId)", method = RequestMethod.DELETE)
    public void deleteAssignmentData(final HttpServletRequest request, final HttpServletResponse response,
                                     @RequestParam("assignmentId") final String assignmentId,
                                     @RequestParam(value = "digest", required = false) final String digest) throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            WorkflowAssignment assignment = getAssignment(assignmentId);

            JSONObject jsonData = internalDeleteAssignmentData(assignment);

            try {
                String currentDigest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();

                if (!Objects.equals(currentDigest, digest))
                    jsonResponse.put(FIELD_DATA, jsonData);

                jsonResponse.put(FIELD_DIGEST, currentDigest);

                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);

                response.setStatus(HttpServletResponse.SC_OK);

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Delete assignment data and abort process
     *
     * @param request
     * @param response
     * @param processId
     */
    @RequestMapping(value = "/json/data/assignment/process/(*:processId)", method = RequestMethod.DELETE)
    public void deleteAssignmentDataByProcess(final HttpServletRequest request, final HttpServletResponse response,
                                              @RequestParam("processId") final String processId,
                                              @RequestParam(value = "activityDefId", defaultValue = "") final String activityDefId,
                                              @RequestParam(value = "digest", required = false) final String digest) throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            WorkflowAssignment assignment = getAssignmentByProcess(processId, activityDefId);

            JSONObject jsonData = internalDeleteAssignmentData(assignment);

            try {
                String currentDigest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();

                if (!Objects.equals(currentDigest, digest))
                    jsonResponse.put(FIELD_DATA, jsonData);

                jsonResponse.put(FIELD_DIGEST, currentDigest);

                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);

                response.setStatus(HttpServletResponse.SC_OK);

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
        }
    }

    /**
     * Delete assignment data
     *
     * @param assignment
     * @return
     * @throws ApiException
     */
    private JSONObject internalDeleteAssignmentData(WorkflowAssignment assignment) throws ApiException {
        // set current app definition
        AppDefinition appDefinition = getApplicationDefinition(assignment);

        // retrieve data
        FormData formData = getAssignmentFormData(assignment);

        // generate form
        Form form = getAssignmentForm(appDefinition, assignment, formData);

        // check form permission
        if (!isAuthorize(form, formData)) {
            throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
        }

        JSONObject jsonData = getData(form, formData);

        abortProcess(assignment);

        deleteData(form, formData, true);

        return jsonData;
    }

    /**
     * Get assignment data as JSONObject
     *
     * @param assignment
     * @param asLabel
     * @param asAttachmentUrl
     * @return
     * @throws ApiException
     */
    private JSONObject internalGetAssignmentJsonData(@Nonnull WorkflowAssignment assignment, boolean asLabel, boolean asOptions, boolean asAttachmentUrl) throws ApiException {
        AppDefinition appDefinition = getApplicationDefinition(assignment);

        // retrieve data
        FormData formData = getAssignmentFormData(assignment);
        if (asAttachmentUrl) {
            formData.addRequestParameterValues(FileDownloadSecurity.PARAMETER_AS_LINK, new String[]{"true"});
        }

        // get form
        Form form = getAssignmentForm(appDefinition, assignment, formData);

        if (asLabel) {
            FormUtil.setReadOnlyProperty(form, true, true);
        }

        // check form permission
        if (!isAuthorize(form, formData)) {
            throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
        }

        JSONObject jsonData = getData(form, formData, asOptions);
        return jsonData;
    }

    /**
     * Get {@link FormData} from {@link WorkflowAssignment}
     *
     * @param assignment
     * @return
     */
    @Nonnull
    private FormData getAssignmentFormData(@Nonnull WorkflowAssignment assignment) {
        return getAssignmentFormData(assignment, false);
    }

    /**
     * Get {@link FormData} from {@link WorkflowAssignment}
     *
     * @param assignment
     * @param asAttachmentUrl
     * @return
     */
    @Nonnull
    private FormData getAssignmentFormData(@Nonnull WorkflowAssignment assignment, boolean asAttachmentUrl) {
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("activityId", assignment.getActivityId());
        FormData formData = formService.retrieveFormDataFromRequestMap(new FormData(), parameterMap);

        String primaryKey = Optional.of(assignment)
                .map(WorkflowAssignment::getProcessId)
                .map(workflowManager::getWorkflowProcessLink)
                .map(WorkflowProcessLink::getOriginProcessId)
                .orElseGet(assignment::getProcessId);

        formData.setPrimaryKeyValue(primaryKey);

        if (asAttachmentUrl) {
            formData.addRequestParameterValues(FileDownloadSecurity.PARAMETER_AS_LINK, new String[]{"true"});
        }

        return formData;
    }


    /**
     * Abort assignment
     *
     * @param assignment
     */
    private void abortProcess(@Nonnull WorkflowAssignment assignment) throws ApiException {
        if (!workflowManager.processAbort(assignment.getProcessId()))
            throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Failed to abort assignment [" + assignment + "]");
    }

    /**
     * Convert request body to form data
     *
     * @param jsonBody
     * @param form
     * @param formData
     * @param isAssignment
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws ApiException
     */
    @Nonnull
    private FormData fillStoreBinderInFormData(final JSONObject jsonBody, final Form form, final FormData formData, final boolean isAssignment) throws IOException, JSONException, ApiException {
        if (form == null) {
            return formData;
        }

        // handle store binder
        String primaryKey = determinePrimaryKey(jsonBody, formData, isAssignment);
        formData.setPrimaryKeyValue(primaryKey);

        WorkflowAssignment assignment = isAssignment ? getAssignment(formData) : null;

        elementStream(form, formData)
                .filter(e -> !(e instanceof Form || e instanceof Section || e instanceof Column))
                .filter(e -> e.getStoreBinder() != null)

                // handle store binder
                .forEach(throwableConsumer(element -> processStoreBinder(element, formData)));

        // handle files; no need to reupload the file if you still need to keep the old one,
        // simply don't send the field

        // persist old values
        String uploadPath = getUploadFilePath();

        FormUtil.executeLoadBinders(form, formData);
        elementStream(form, formData)
                .filter(e -> !(e instanceof FormContainer))
                .forEach(throwableConsumer(e -> {
                    String parameterName = FormUtil.getElementParameterName(e);
                    String parameterValue = formData.getRequestParameter(parameterName);

                    if (parameterValue == null) {
                        // get old data value
                        Optional.of(e)
                                .map(formData::getLoadBinderData)
                                .ifPresent(rowSet -> {
                                    // ordinary element
                                    if (e.getStoreBinder() == null) {
                                        String elementId = e.getPropertyString("id");
                                        Optional.of(rowSet)
                                                .map(Collection::stream)
                                                .orElseGet(Stream::empty)
                                                .findFirst()
                                                .ifPresent(row -> {
                                                    String elementValue;

                                                    // hidden field, special case
                                                    if (e instanceof HiddenField) {
                                                        elementValue = getValueForHiddenField(e, row, assignment);
                                                    }

                                                    // other Element types
                                                    else {
                                                        elementValue = row.getProperty(elementId);
                                                    }

                                                    formData.addRequestParameterValues(parameterName, new String[]{elementValue});
                                                });
                                    }

                                    // any other element with store binder
                                    else {
                                        formData.setStoreBinderData(e.getStoreBinder(), rowSet);
                                    }
                                });
                    }

                    // parameter value is not null and it is a FileDownloadSecurity
                    else if (e instanceof FileDownloadSecurity) {
                        String[] filePaths = Optional.of(parameterValue)
                                .map(this::handleEncodedFile)
                                .map(Arrays::stream)
                                .orElseGet(Stream::empty)
                                .map(throwableFunction(this::decodeFile))
                                .filter(Objects::nonNull)
                                .map(f -> FileManager.storeFile(f, uploadPath))
                                .toArray(String[]::new);

                        formData.addRequestParameterValues(parameterName, filePaths.length > 0 ? filePaths : new String[]{""});
                    }
                }));

        return formData;
    }

    /**
     * @param element
     * @param row
     * @param assignment
     * @return
     */
    private String getValueForHiddenField(Element element, FormRow row, WorkflowAssignment assignment) {
        String elementId = getStringProperty(element, FormUtil.PROPERTY_ID);
        String defaultValue = AppUtil.processHashVariable(getStringProperty(element, "value"), assignment, null, null);
        String databaseValue = row.getProperty(elementId);
        String priority = getStringProperty(element, "useDefaultWhenEmpty");

        if (isNotEmpty(priority) && (("true".equals(priority) && isEmpty(databaseValue)) || "valueOnly".equals(priority))) {
            return defaultValue;
        }

        return this.ifEmptyThen(databaseValue, defaultValue);
    }

    /**
     * @param element
     * @param propertyName
     * @return
     */
    @Nonnull
    private String getStringProperty(Element element, String propertyName) {
        return ifNullThen(element.getPropertyString(propertyName), "");
    }

    /**
     * Get Upload File Path
     *
     * @return
     */
    private String getUploadFilePath() {
        String basePath = SetupManager.getBaseDirectory();
        String dataFileBasePath = setupManager.getSettingValue("dataFileBasePath");
        if (isNotEmpty(dataFileBasePath)) {
            basePath = dataFileBasePath;
        }
        return basePath;
    }

    /**
     * DAMN IM TOO CONFUSED TO THINK ABOUT A BETTER NAME
     *
     * @param value
     * @return
     */
    private String[] handleEncodedFile(String value) {
        try {
            JSONArray jsonArray = new JSONArray(value);
            return this.<String>jsonStream(jsonArray)
                    .toArray(String[]::new);

        } catch (JSONException e) {
            return new String[]{value};
        }
    }

    /**
     * Get default value
     *
     * @param element
     * @param formData
     * @return
     */
    private String getDefaultValue(@Nonnull Element element, @Nonnull FormData formData) {
        String defaultValue = element.getPropertyString(FormUtil.PROPERTY_VALUE);
        if (isNotEmpty(defaultValue)) {
            try {
                WorkflowAssignment workflowAssignment = getAssignment(formData.getActivityId());
                return AppUtil.processHashVariable(defaultValue, workflowAssignment, null, null);
            } catch (ApiException e) {
                LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Prepare formData with store binder
     *
     * @param element  element with store binder
     * @param formData
     * @throws ApiException
     */
    private void processStoreBinder(@Nonnull Element element, @Nonnull final FormData formData) throws ApiException {
        FormStoreBinder storeBinder = element.getStoreBinder();
        assert storeBinder != null;

        AppDefinition appDefinition = AppUtil.getCurrentAppDefinition();
        Form form = FormUtil.findRootForm(element);
        String parameterName = FormUtil.getElementParameterName(element);
        String requestParameter = formData.getRequestParameter(parameterName);

        FormRowSet rowSet;

        // Multi row
        if (storeBinder instanceof FormStoreMultiRowElementBinder) {
            JSONArray values = Optional.ofNullable(requestParameter)
                    .map(throwableFunction(JSONArray::new))
                    .orElseGet(JSONArray::new);

            Form form1 = Optional.of(storeBinder)
                    .map(b -> (FormBinder)b)
                    .map(b -> b.getPropertyString("formDefId"))
                    .map(throwableFunction(s -> getForm(appDefinition, s, formData)))
                    .orElse(null);

            rowSet = convertJsonArrayToRowSet(form1, formData, values);
        }

        // Single row
        else if (storeBinder instanceof FormStoreElementBinder) {
            JSONObject value = Optional.ofNullable(formData.getRequestParameter(parameterName))
                    .map(throwableFunction(JSONObject::new))
                    .orElseGet(JSONObject::new);
            rowSet = convertJsonObjectToRowSet(form, formData, value);
        }

        // Undefined store binder
        else {
            throw new ApiException(HttpServletResponse.SC_NOT_IMPLEMENTED,
                    "Unknown store binder type [" + storeBinder.getClass().getName()
                            + "] in form [" + form.getPropertyString(FormUtil.PROPERTY_ID)
                            + "] element [" + element.getPropertyString(FormUtil.PROPERTY_ID) + "] ");
        }

        // store binder data to be stored
        formData.setStoreBinderData(storeBinder, rowSet);
    }

    /**
     * Determine primary key based on given parameter
     *
     * @param jsonBody
     * @param formData
     * @param isAssignment
     * @return
     */
    @Nullable
    private String determinePrimaryKey(@Nonnull JSONObject jsonBody, @Nonnull FormData formData, boolean isAssignment) {
        // handle start process or assingment complete process
        if (isAssignment) {
            formData.addRequestParameterValues(AssignmentCompleteButton.DEFAULT_ID, new String[]{AssignmentCompleteButton.DEFAULT_ID});

            return Optional.of(formData)
                    .map(FormData::getProcessId)
                    .map(appService::getOriginProcessId)
                    .orElse(null);
        }

        // if not assingment and primary is not assigned
        else if (!Optional.of(formData).map(FormData::getPrimaryKeyValue).filter(this::isNotEmpty).isPresent()) {
            return Optional.of(jsonBody)
                    .map(j -> j.optString(FormUtil.PROPERTY_ID))
                    .filter(this::isNotEmpty)
                    .orElse(UuidGenerator.getInstance().getUuid());
        }

        // get default primary key
        else {
            return formData.getPrimaryKeyValue();
        }
    }

    @Nonnull
    private FormRow convertJsonObjectToFormRow(@Nullable final Form form, @Nullable FormData formData, @Nullable final JSONObject json) {
        return jsonStream(json)
                .collect(FormRow::new, throwableBiConsumer((row, key) -> {
                    String name = getElementParameterName(key, form, formData);
                    String value = extractStringValue(form, formData, json, key);

                    row.put(name, value);
                }), FormRow::putAll);
    }

    @Nonnull
    private FormRowSet convertJsonObjectToRowSet(@Nonnull Form form, @Nonnull FormData formData, @Nullable JSONObject json) {
        FormRowSet result = new FormRowSet();

        if (json != null) {
            FormRow row = convertJsonObjectToFormRow(form, formData, json);
            result.add(row);
        }

        return result;
    }

    @Nonnull
    private FormRowSet convertJsonArrayToRowSet(@Nullable Form form, @Nonnull FormData formData, @Nonnull JSONArray jsonArray) {
        FormRowSet result = this.<JSONObject>jsonStream(jsonArray)
                .map(j -> convertJsonObjectToFormRow(form, formData, j))
                .collect(Collectors.toCollection(FormRowSet::new));

        result.setMultiRow(true);

        return result;
    }

    @Nonnull
    private String getElementParameterName(@Nonnull String fieldId, @Nonnull Form form, FormData formData) {
        return Optional.of(fieldId)
                .map(s -> FormUtil.findElement(s, form, formData, true))
                .map(FormUtil::getElementParameterName)
                .orElse(fieldId);
    }

    @Nonnull
    private String extractStringValue(@Nullable Form form, @Nullable FormData formData, JSONObject json, String fieldName) throws JSONException {
        if (form == null) {
            return json.getString(fieldName);
        }

        Element element = FormUtil.findElement(fieldName, form, formData, true);
        if(element == null) {
            return "";
        } else if (element instanceof FileDownloadSecurity) {
            // TODO : handle file here
            return json.getString(fieldName);
        } else {
            return json.getString(fieldName);
        }
    }

    /**
     * Generate request body as JSONObject
     *
     * @param request
     * @return
     */
    @Nonnull
    private JSONObject getRequestPayload(HttpServletRequest request) {
        try {
            String payload = request.getReader().lines().collect(Collectors.joining());
            return new JSONObject(this.ifEmptyThen(payload, "{}"));
        } catch (IOException | JSONException e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
            return new JSONObject();
        }
    }

    /**
     * @param value    follow this format : "[file name];[base64 encoded file]
     * @param element
     * @param formData
     */
    @Nullable
    private MultipartFile addFileRequestParameter(@Nullable String value, Element element, FormData formData) {
        if (value == null) {
            return null;
        }

        String[] fileParts = value.split(";");
        String filename = fileParts[0];

        if (fileParts.length > 1) {
            String encodedFile = fileParts[1];

            String paramName = FormUtil.getElementParameterName(element);
            List<String> values = Optional.ofNullable(formData.getRequestParameterValues(paramName))
                    .map(Arrays::stream)
                    .orElseGet(Stream::empty)
                    .map(v -> v.split(";"))
                    .flatMap(Arrays::stream)
                    .collect(Collectors.toList());
            values.add(filename);

            formData.addRequestParameterValues(paramName, new String[]{String.join(";", values)});

            // determine file path
            MultipartFile file = decodeFile(filename, encodedFile);
//            FileUtil.storeFile(file, element, element.getPrimaryKeyValue(formData));
            return file;
        }

        return null;
    }

    /**
     * Decode base64 to file
     *
     * @param filename
     * @param bse64EncodedFile
     * @return
     */
    @Nullable
    private MultipartFile decodeFile(@Nonnull String filename, @Nonnull String bse64EncodedFile) throws IllegalArgumentException {
        if (bse64EncodedFile.isEmpty())
            return null;

        byte[] data = Base64.getDecoder().decode(bse64EncodedFile);
        return new MockMultipartFile(filename, filename, null, data);
    }

    /**
     * Delete data
     *
     * @param form
     * @param formData
     * @throws ApiException
     */
    private void deleteData(@Nonnull Form form, @Nonnull FormData formData, boolean deepClean) throws ApiException {
        formDataDao.delete(form, new String[]{formData.getPrimaryKeyValue()});

        // delete sub data
        if (deepClean) {
            Optional.of(formData)
                    .map(FormData::getLoadBinderMap)
                    .map(Map::entrySet)
                    .map(Collection::stream)
                    .orElseGet(Stream::empty)
                    .filter(e -> e.getKey() instanceof FormDataDeletableBinder)
                    .forEach(e -> {
                        FormDataDeletableBinder formLoadBinder = (FormDataDeletableBinder) e.getKey();
                        String formId = formLoadBinder.getFormId();
                        String tableName = formLoadBinder.getTableName();
                        formDataDao.delete(formId, tableName, e.getValue());
                    });
        }
    }

    /**
     * Load form data using form service as {@link FormRow}
     *
     * @param form
     * @param primaryKey
     * @return
     */
    private FormRow getFormRow(@Nonnull Form form, String primaryKey) {
        return Optional.ofNullable(appService.loadFormData(form, primaryKey))
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .findFirst()
                .map(FormRow::entrySet)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .filter(this::isNotEmpty)
                .collect(FormRow::new, (result, entry) -> result.put(entry.getKey(), assignValue(entry.getValue())), FormRow::putAll);
    }


    /**
     * Decode base64 to file
     *
     * @param value
     * @return
     */
    @Nullable
    private MultipartFile decodeFile(@Nonnull String value) throws IllegalArgumentException {
        String[] split = value.split(";");
        if (split.length > 0) {
            String fileName = split[0];
            if (split.length > 1) {
                String encodedFile = split[1];
                return decodeFile(fileName, encodedFile);
            }
            return null;
        } else {
            return null;
        }
    }

    /**
     * @param request
     * @param response
     * @param appId
     * @param appVersion
     * @param dataListId
     * @param processId
     * @param activityDefIds
     * @param page
     * @param start
     * @param rows
     * @param sort
     * @param desc
     * @param digest
     * @throws IOException
     * @deprecated use {@link DataJsonController#getDataListAssignments}
     */
    @Deprecated
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/assignments/datalist/(*:dataListId)", method = RequestMethod.GET)
    public void deprecatedGetDataListAssignments(final HttpServletRequest request, final HttpServletResponse response,
                                                 @RequestParam("appId") final String appId,
                                                 @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                                                 @RequestParam("dataListId") final String dataListId,
                                                 @RequestParam(value = "processId", required = false) final String[] processId,
                                                 @RequestParam(value = "activityId", required = false) final String[] activityDefIds,
                                                 @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                 @RequestParam(value = "start", required = false) final Integer start,
                                                 @RequestParam(value = "rows", required = false, defaultValue = "0") final Integer rows,
                                                 @RequestParam(value = "sort", required = false) final String sort,
                                                 @RequestParam(value = "desc", required = false, defaultValue = "false") final Boolean desc,
                                                 @RequestParam(value = "digest", required = false) final String digest)
            throws IOException {

        getDataListAssignments(request, response, appId, appVersion, dataListId, processId, activityDefIds, page, start, rows, sort, desc, digest);
    }

    /**
     * Get DataList Assignments
     * <p>
     * Same functionality as DataList Inbox plugin
     *
     * @param request
     * @param response
     * @param appId
     * @param appVersion
     * @param dataListId
     * @param processId
     * @param activityDefIds
     * @param page
     * @param start
     * @param rows
     * @param sort
     * @param desc
     * @param digest
     * @throws IOException
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/datalist/(*:dataListId)/assignments", method = RequestMethod.GET)
    public void getDataListAssignments(final HttpServletRequest request, final HttpServletResponse response,
                                       @RequestParam("appId") final String appId,
                                       @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                                       @RequestParam("dataListId") final String dataListId,
                                       @RequestParam(value = "processId", required = false) final String[] processId,
                                       @RequestParam(value = "activityId", required = false) final String[] activityDefIds,
                                       @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                       @RequestParam(value = "start", required = false) final Integer start,
                                       @RequestParam(value = "rows", required = false, defaultValue = "0") final Integer rows,
                                       @RequestParam(value = "sort", required = false) final String sort,
                                       @RequestParam(value = "desc", required = false, defaultValue = "false") final Boolean desc,
                                       @RequestParam(value = "digest", required = false) final String digest)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            // get dataList
            DataList dataList = getDataList(appDefinition, dataListId);

            // configure sorting
            if (sort != null) {
                dataList.setDefaultSortColumn(sort);

                // order ASC / DESC
                dataList.setDefaultOrder(desc ? DataList.ORDER_DESCENDING_VALUE : DataList.ORDER_ASCENDING_VALUE);
            }

            // paging
            int pageSize = rows != null && rows > 0 ? rows : page != null && page > 0 ? dataList.getPageSize() : DataList.MAXIMUM_PAGE_SIZE;
            int rowStart = start != null ? start : page != null && page > 0 ? ((page - 1) * pageSize) : 0;

            getCollectFilters(request.getParameterMap(), dataList);

            try {
                @Nonnull List<String> pids = convertMultiValueParameterToList(processId);
                @Nonnull List<String> aids = convertMultiValueParameterToList(activityDefIds);

                @Nonnull Collection<WorkflowAssignment> assignmentList = getAssignmentList(pids, aids, null, null, null, null);

                // get original process ID from assignments
                final Map<String, Collection<String>> mapPrimaryKeyToProcessId = workflowProcessLinkDao.getOriginalIds(assignmentList.stream().map(WorkflowAssignment::getProcessId).collect(Collectors.toList()));
                @Nonnull List<String> originalPids = mapPrimaryKeyToProcessId
                        .keySet()
                        .stream()
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                addFilterById(dataList, originalPids);

                JSONArray jsonData = Optional.ofNullable((DataListCollection<Map<String, Object>>) dataList.getRows(pageSize, rowStart))
                        .orElse(new DataListCollection<>())
                        .stream()

                        // reformat content value
                        .map(row -> formatRow(dataList, row))

                        // collect as JSON
                        .collect(JSONArray::new, throwableBiConsumer((jsonArray, row) -> {
                            String primaryKeyColumn = getPrimaryKeyColumn(dataList);
                            String primaryKey = String.valueOf(row.get(primaryKeyColumn));

                            // put process detail
                            WorkflowAssignment workflowAssignment = getAssignmentFromProcessIdMap(mapPrimaryKeyToProcessId, String.valueOf(row.get("_" + FormUtil.PROPERTY_ID)));
                            if (workflowAssignment != null) {
                                row.put("activityId", workflowAssignment.getActivityId());
                                row.put("activityDefId", workflowAssignment.getActivityDefId());
                                row.put("processId", workflowAssignment.getProcessId());
                                row.put("processDefId", workflowAssignment.getProcessDefId());
                                row.put("assigneeId", workflowAssignment.getAssigneeId());
                                row.put("appId", appDefinition.getAppId());
                                row.put("appVersion", appDefinition.getVersion());

                                FormData formData = new FormData();
                                formData.setPrimaryKeyValue(primaryKey);
                                Form form = getAssignmentForm(appDefinition, workflowAssignment, formData);

                                if (isAuthorize(form, formData)) {
                                    row.put("formId", form.getPropertyString(FormUtil.PROPERTY_ID));
                                }
                            }

                            jsonArray.put(row);
                        }), (a1, a2) -> {
                            for (int i = 0, size = a2.length(); i < size; i++) {
                                try {
                                    a1.put(a2.get(i));
                                } catch (JSONException ignored) {
                                }
                            }
                        });

                String currentDigest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();

                jsonResponse.put(FIELD_TOTAL, dataList.getSize());

                if (!Objects.equals(digest, currentDigest))
                    jsonResponse.put(FIELD_DATA, jsonData);

                jsonResponse.put(FIELD_DIGEST, currentDigest);

                response.setStatus(HttpServletResponse.SC_OK);

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        } catch (ApiException e) {
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }


    /**
     * @param request
     * @param response
     * @param appId
     * @param appVersion
     * @param dataListId
     * @param processId
     * @param activityId
     * @throws IOException
     * @deprecated use {@link #getDataListAssignmentsCount(HttpServletRequest, HttpServletResponse, String, Long, String, String[], String[])}
     */
    @Deprecated
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/assignments/datalist/(*:dataListId)/count", method = RequestMethod.GET)
    public void depecatedGetDataListAssignmentsCount(final HttpServletRequest request, final HttpServletResponse response,
                                                     @RequestParam("appId") final String appId,
                                                     @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                                                     @RequestParam("dataListId") final String dataListId,
                                                     @RequestParam(value = "processId", required = false) final String[] processId,
                                                     @RequestParam(value = "activityId", required = false) final String[] activityId)
            throws IOException {
        getDataListAssignmentsCount(request, response, appId, appVersion, dataListId, processId, activityId);
    }

    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/datalist/(*:dataListId)/assignments/count", method = RequestMethod.GET)
    public void getDataListAssignmentsCount(final HttpServletRequest request, final HttpServletResponse response,
                                            @RequestParam("appId") final String appId,
                                            @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                                            @RequestParam("dataListId") final String dataListId,
                                            @RequestParam(value = "processId", required = false) final String[] processId,
                                            @RequestParam(value = "activityId", required = false) final String[] activityId)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            // get dataList
            DataList dataList = getDataList(appDefinition, dataListId);

            getCollectFilters(request.getParameterMap(), dataList);

            try {
                @Nonnull List<String> pids = convertMultiValueParameterToList(processId);
                @Nonnull List<String> aids = convertMultiValueParameterToList(activityId);

                @Nonnull Collection<WorkflowAssignment> assignmentList = getAssignmentList(pids, aids, null, null, null, null);

                // get original process ID from assignments
                @Nonnull List<String> originalPids = workflowProcessLinkDao
                        .getOriginalIds(assignmentList
                                .stream()
                                .map(WorkflowAssignment::getProcessId)
                                .collect(Collectors.toList()))
                        .keySet()
                        .stream()
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                addFilterById(dataList, originalPids);

                JSONObject jsonResponse = new JSONObject();

                jsonResponse.put(FIELD_TOTAL, dataList.getSize());

                response.setStatus(HttpServletResponse.SC_OK);

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        } catch (ApiException e) {
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/datalist/(*:dataListId)/(~:actionType)/(~:actionIndex)", method = RequestMethod.POST)
    public void postDataListAction(final HttpServletRequest request, final HttpServletResponse response,
                                   @RequestParam("appId") final String appId,
                                   @RequestParam(value = "appVersion", required = false, defaultValue = "0") Long appVersion,
                                   @RequestParam("dataListId") final String dataListId,
                                   @RequestParam("actionType") final String actionType,
                                   @RequestParam(value = "actionIndex", defaultValue = "0") final Integer actionIndex,
                                   @RequestParam("id") final String[] ids) throws IOException {

        try {
            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNullThen(appVersion, 0L));

            // get dataList
            DataList dataList = getDataList(appDefinition, dataListId);
            DataListAction action = getDataListAction(dataList, actionType, ifNullThen(actionIndex, 0));
            DataListActionResult actionResult = action.executeAction(dataList, ids);

            try {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("message", actionResult.getMessage());
                jsonResponse.put("url", actionResult.getUrl());
                jsonResponse.put("type", actionResult.getType());
                response.setStatus(HttpServletResponse.SC_OK);

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        } catch (ApiException e) {
            LogUtil.error(getClass().getName(), e, "HTTP error [" + e.getErrorCode() + "] : " + e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }


    @Nonnull
    private DataListAction getDataListAction(@Nonnull DataList dataList, @Nonnull String actionType, int actionIndex) throws ApiException {
        // validate action type
        if (!actionType.matches("rowAction|action")) {
            throw new ApiException(HttpServletResponse.SC_NOT_FOUND, "Action type [" + actionType + "] not found");
        }

        return Optional.of(actionIndex)
                .flatMap(i -> Optional.of(dataList)
                        .map(d -> {
                            switch (actionType) {
                                case "rowAction":
                                    return d.getRowActions();
                                case "action":
                                    return d.getActions();
                                default:
                                    return null;
                            }
                        })
                        .filter(a -> i < a.length)
                        .map(a -> a[i])
                )
                .filter(DataListAction::isPermitted)
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Action type [" + actionType + "] index [" + actionIndex + "] not found"));
    }

    /**
     * Get Assignment List
     * Get assignment of dataList
     *
     * @param processIds
     * @param activityDefIds
     * @param sort
     * @param desc
     * @param start
     * @param size
     * @return
     */
    @Nonnull
    private Collection<WorkflowAssignment> getAssignmentList(@Nonnull final List<String> processIds, @Nonnull final List<String> activityDefIds, String sort, Boolean desc, Integer start, Integer size) {
        @Nonnull final AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        @Nonnull final ApplicationContext ac = AppUtil.getApplicationContext();
        @Nonnull final WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
        @Nonnull final AppService appService = (AppService) ac.getBean("appService");
        @Nonnull final PackageDefinition packageDef = appDef.getPackageDefinition();

        return processIds.stream()
                .map(s -> {
                    if (isEmpty(s)) {
                        return "";
                    }

                    WorkflowProcess p = appService.getWorkflowProcessForApp(appDef.getId(), appDef.getVersion().toString(), s);
                    if (p == null) {
                        LogUtil.warn(getClass().getName(), "Process [" + s + "] is not defined for this app");
                        return null;
                    }

                    return p.getId();
                })

                .filter(Objects::nonNull)

                // get assignments
                .flatMap(pid -> activityDefIds.stream()
                        .map(s -> workflowManager.getAssignmentListLite(packageDef.getId(), pid, null, nullIfEmpty(s), sort, desc, start, size))
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream))

                .collect(Collectors.toList());
    }

    /**
     * Get form from assignment
     *
     * @param appDefinition I
     * @param assignment    I
     * @param formData      I/O
     * @return
     * @throws ApiException
     */
    @Nonnull
    private Form getAssignmentForm(@Nonnull AppDefinition appDefinition, @Nonnull WorkflowAssignment assignment, @Nonnull final FormData formData) throws ApiException {
        return Optional.ofNullable(appService.viewAssignmentForm(appDefinition, assignment, formData, ""))
                .map(PackageActivityForm::getForm)
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Assignment [" + assignment.getActivityId() + "] has not been mapped to form"));
    }

    /**
     * Calculate digest (version if I may call) but will omit "elementUniqueKey"
     *
     * @param json JSON array object
     * @return digest value
     */
    private String getDigest(JSONArray json) {
        return json == null || json.toString() == null ? null : DigestUtils.sha256Hex(json.toString());
    }

    private String getDigest(Object value) {
        return value == null ? null : DigestUtils.sha256Hex(String.valueOf(value));
    }

    /**
     * Calculate digest (version if I may call) but will omit "elementUniqueKey"
     *
     * @param json JSON object
     * @return digest value
     */
    private String getDigest(JSONObject json) {
        return json == null || json.toString() == null ? null : DigestUtils.sha256Hex(json.toString());
    }

    /**
     * @param requestParameters I, Request parameter
     * @param dataList          I/O, DataList
     */
    private void getCollectFilters(@Nonnull final Map<String, String[]> requestParameters, @Nonnull final DataList dataList) {
        Optional.of(dataList)
                .map(DataList::getFilters)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)

                .filter(f -> Optional.of(f)
                        .map(DataListFilter::getName)
                        .map(requestParameters::get)
                        .map(a -> a.length)
                        .map(i -> i > 0)
                        .orElse(false))

                .forEach(filter -> {
                    String filterName = filter.getName();
                    String[] values = requestParameters.get(filterName);

                    DataListFilterType filterType = filter.getType();
                    filterType.setProperty("defaultValue", String.join(";", values));
                });

        dataList.getFilterQueryObjects();
        dataList.setFilters(null);
    }

    /**
     * Format
     *
     * @param dataList DataList
     * @param row      Row
     * @param field    Field
     * @return
     */
    @Nonnull
    private String formatValue(@Nonnull final DataList dataList, @Nonnull final Map<String, Object> row, String field) {
        String value = Optional.of(field)
                .map(row::get)
                .map(String::valueOf)
                .orElse("");

        return Optional.of(dataList)
                .map(DataList::getColumns)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .filter(c -> field.equals(c.getName()))
                .findFirst()
                .map(column -> Optional.of(column)
                        .map(DataListColumn::getFormats)
                        .map(Collection::stream)
                        .orElseGet(Stream::empty)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .map(f -> f.format(dataList, column, row, value))
                        .map(s -> s.replaceAll("<[^>]*>", ""))
                        .orElse(value))
                .orElse(value);
    }

    @Nonnull
    private Map<String, Object> formatRow(@Nonnull DataList dataList, @Nonnull Map<String, Object> row) {
        Map<String, Object> formattedRow = Optional.of(dataList)
                .map(DataList::getColumns)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .filter(not(DataListColumn::isHidden))
                .map(DataListColumn::getName)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(s -> s, s -> formatValue(dataList, row, s)));

        String primaryKeyColumn = getPrimaryKeyColumn(dataList);
        formattedRow.putIfAbsent("_" + FormUtil.PROPERTY_ID, row.get(primaryKeyColumn));

        return formattedRow;
    }

    /**
     * Validate and Determine Process ID
     *
     * @param appDefinition Application definition
     * @param processId     Process ID
     * @return
     * @throws ApiException
     */
    @Nonnull
    private String validateAndDetermineProcessDefId(@Nonnull AppDefinition appDefinition, @Nonnull String processId) throws ApiException {
        return Optional.of(processId)
                .map(s -> appService.getWorkflowProcessForApp(appDefinition.getAppId(), appDefinition.getVersion().toString(), s))
                .map(WorkflowProcess::getId)
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Process [" + processId + "] for Application ID [" + appDefinition.getAppId() + "] is not available"));
    }

    /**
     * Generate Workflow Variable
     *
     * @param form     Form
     * @param formData Form Data
     * @return
     */
    @Nonnull
    private Map<String, String> generateWorkflowVariable(@Nonnull final Form form, @Nonnull final FormData formData) {
        return formData.getRequestParams().entrySet().stream().collect(HashMap::new, (m, e) -> {
            Element element = FormUtil.findElement(e.getKey(), form, formData, true);
            if (Objects.isNull(element))
                return;

            String workflowVariable = element.getPropertyString("workflowVariable");

            if (isEmpty(workflowVariable))
                return;

            m.put(element.getPropertyString("workflowVariable"), String.join(";", e.getValue()));
        }, Map::putAll);
    }

    /**
     * Create filter by ID
     *
     * @param dataList
     * @param originalPids
     * @return
     */
    private DataList addFilterById(DataList dataList, List<String> originalPids) {
        DataListFilterQueryObject filterQueryObject = new DataListFilterQueryObject();
        filterQueryObject.setOperator("AND");

        final List<String> values = new ArrayList<>();
        String prefix = dataList.getBinder().getColumnName("id") + " in (";
        String suffix = ")";
        String sql = originalPids
                .stream()
                .map(String::trim)
                .filter(this::isNotEmpty)
                .map(s -> {
                    values.add(s);
                    return "?";
                })
                .collect(Collectors.joining(", "));
        filterQueryObject.setQuery(sql.isEmpty() ? " id is null" : (prefix + sql + suffix));
        filterQueryObject.setValues(values.toArray(new String[0]));
        dataList.addFilterQueryObject(filterQueryObject);
        return dataList;
    }

    /**
     * Get assignment object
     *
     * @param activityId any activity ID, event the completed / aborted
     * @return
     * @throws ApiException
     */
    @Nonnull
    private WorkflowAssignment getAssignment(@Nonnull String activityId) throws ApiException {
        return Optional.of(activityId)
                .map(throwableFunction(workflowManager::getAssignment))
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Assignment [" + activityId + "] not available"));
    }

    /**
     * Get assignment object by process ID
     *
     * @param processId any linked process ID or primary key
     * @return
     * @throws ApiException
     */
    @Nonnull
    private WorkflowAssignment getAssignmentByProcess(@Nonnull String processId) throws ApiException {
        return Optional.of(processId)
                .map(workflowProcessLinkDao::getLinks)
                .map(Collection::stream)
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Process [" + processId + "] is not defined"))
                .findFirst()
                .map(WorkflowProcessLink::getProcessId)
                .map(throwableFunction(workflowManager::getAssignmentByProcess))
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Assignment for process [" + processId + "] not available"));
    }

    /**
     * Get assignment object by process ID and activity Def ID
     *
     * @param processId
     * @param activityDefId
     * @return
     * @throws ApiException
     */
    @Nonnull
    private WorkflowAssignment getAssignmentByProcess(@Nonnull String processId, @Nonnull String activityDefId) throws ApiException {
        return Optional.of(processId)
                .map(workflowProcessLinkDao::getLinks)
                .map(Collection::stream)
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Process [" + processId + "] is not defined"))
                .map(WorkflowProcessLink::getProcessId)
                .filter(Objects::nonNull)
                .map(s -> workflowManager.getAssignmentPendingAndAcceptedList(null, null, s, null, null, null, null))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)

                // filter by Activity Def ID, if activityDefId is empty, get the first activity
                .filter(it -> isEmpty(activityDefId) || Optional.of(it)
                        .map(WorkflowAssignment::getActivityId)
                        .map(workflowManager::getActivityById)
                        .map(WorkflowActivity::getActivityDefId)
                        .map(activityDefId::equals)
                        .orElse(false))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Assignment for process [" + processId + "] activity definition [" + activityDefId + "] not available"));
    }


    /**
     * Attempt to get app definition using activity ID or process ID
     *
     * @param assignment
     * @return
     */
    @Nonnull
    private AppDefinition getApplicationDefinition(@Nonnull WorkflowAssignment assignment) throws ApiException {
        final String activityId = assignment.getActivityId();
        final String processId = assignment.getProcessId();

        AppDefinition appDefinition = Optional.of(activityId)
                .map(appService::getAppDefinitionForWorkflowActivity)
                .orElseGet(() -> Optional.of(processId)
                        .map(appService::getAppDefinitionForWorkflowProcess)
                        .orElse(null));

        return Optional.ofNullable(appDefinition)

                // set current app definition
                .map(peekMap(AppUtil::setCurrentAppDefinition))

                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Application definition for assignment [" + activityId + "] process [" + processId + "] not found"));
    }

    /**
     * Load form data
     * @param form
     * @param formData
     * @return
     * @throws ApiException
     */
    private JSONObject getData(@Nonnull final Form form, @Nonnull final FormData formData) throws ApiException {
        return getData(form, formData, false);
    }

    /**
     * Load form data
     *
     * @param form
     * @param formData
     */
    @Nonnull
    private JSONObject getData(@Nonnull final Form form, @Nonnull final FormData formData, final boolean asOptions) throws ApiException {
        // check result size
        Optional.of(form)
                .map(formData::getLoadBinderData)
                .map(FormRowSet::size)
                .filter(i -> i > 0)
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Data [" + formData.getPrimaryKeyValue() + "] in form [" + form.getPropertyString(FormUtil.PROPERTY_ID) + "] not found"));

        JSONObject parentJson = new JSONObject();
        Optional.of(formData)
                .map(fd -> elementStream(form, fd))
                .orElseGet(Stream::empty)
//                .filter(e -> e.getLoadBinder() != null)
//                .filter(e -> formData.getLoadBinderData(e) != null)
                .filter(e -> !(e instanceof FormContainer))
                .forEach(throwableConsumer(e -> {
                    final String elementId = e.getPropertyString("id");
                    final FormRowSet rowSet = formData.getLoadBinderData(e);

                    if(rowSet == null) {
                        return ;
                    }

                    // Element with load binder, set as child object
                    if(e.getLoadBinder() != null) {
                        if (rowSet.isMultiRow()) {
                            JSONArray data = collectGridElement((GridElement) e, rowSet);
                            parentJson.put(elementId, data);
                        } else {
                            FormRow row = rowSet.stream().findFirst().orElseGet(FormRow::new);
                            JSONObject data = collectGridElement((GridElement) e, row);
                            parentJson.put(elementId, data);
                        }
                    }

                    else {
                        FormRow row = rowSet.stream().findFirst().orElseGet(FormRow::new);

                        if(asOptions && e instanceof FormOptionsElement) {
                            final FormRowSet options = FormUtil.getElementPropertyOptionsMap(e, formData);

                            JSONArray values = Optional.of(elementId)
                                    .map(row::getProperty)
                                    .map(s -> s.split(";"))
                                    .map(Arrays::stream)
                                    .orElseGet(Stream::empty)
                                    .map(s -> options.stream()
                                            .filter(r -> s.equals(r.getProperty(FormUtil.PROPERTY_VALUE)))
                                            .findFirst()
                                            .orElseGet(FormRow::new))
                                    .map(JSONObject::new)
                                    .collect(jsonCollector());

                            jsonPutOnce(elementId, values, parentJson);
                        } else {
                            jsonPutOnce(elementId, e.getElementValue(formData), parentJson);
                        }
                    }
                }));

        try {
            parentJson.putOpt("activityId", formData.getActivityId());
            parentJson.putOpt("processId", formData.getProcessId());
        } catch (JSONException e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
        }

        return parentJson;
    }

    /**
     * Generate datalist
     *
     * @param appDefinition
     * @param dataListId
     * @return
     * @throws ApiException
     */
    @Nonnull
    private DataList getDataList(@Nonnull AppDefinition appDefinition, @Nonnull String dataListId) throws ApiException {
        // get dataList definition
        DatalistDefinition datalistDefinition = datalistDefinitionDao.loadById(dataListId, appDefinition);
        if (datalistDefinition == null) {
            throw new ApiException(HttpServletResponse.SC_NOT_FOUND, "DataList Definition for dataList [" + dataListId + "] not found");
        }

        DataList dataList = Optional.of(datalistDefinition)
                .map(DatalistDefinition::getJson)
                .map(it -> AppUtil.processHashVariable(it, null, null, null))
                .map(it -> dataListService.fromJson(it))
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Error generating dataList [" + dataListId + "]"));

        // check permission
        if (!isAuthorize(dataList)) {
            throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] is not authorized to access datalist [" + dataListId + "]");
        }

        return dataList;
    }


    /**
     * Validate Form Data
     *
     * @param form
     * @param formData
     */
    private FormData validateFormData(Form form, FormData formData) {
        Optional.of(formData)
                .map(FormData::getRequestParams)
                .map(Map::entrySet)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(e -> FormUtil.findElement(e.getKey(), form, formData))
                .forEach(e -> FormUtil.executeValidators(e, formData));

        return formData;

    }

    /**
     * Get application definition and set default application definition
     *
     * @param appId
     * @param version 0 for published version
     * @return
     * @throws ApiException
     */
    @Nonnull
    private AppDefinition getApplicationDefinition(@Nonnull String appId, long version) throws ApiException {
        return Optional.ofNullable(appDefinitionDao.getPublishedVersion(appId))
                .map(it -> version == 0 ? it : version)
                .map(it -> appDefinitionDao.loadVersion(appId, it))

                // set current app definition
                .map(peekMap(AppUtil::setCurrentAppDefinition))

                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_NOT_FOUND, "Application [" + appId + "] version [" + version + "] not found"));
    }

    /**
     * Null-safe way to retrieve {@link AppService#viewDataForm(String, String, String, String, String, String, FormData, String, String)}
     *
     * @param appDefinition
     * @param formDefId
     * @param formData
     * @return
     * @throws ApiException
     */
    @Nonnull
    private Form getForm(@Nonnull AppDefinition appDefinition, @Nonnull String formDefId, @Nonnull final FormData formData) throws ApiException {
        return Optional.ofNullable(appService.viewDataForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), formDefId, null, null, null, formData, null, null))
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Form [" + formDefId + "] in app [" + appDefinition.getAppId() + "] version [" + appDefinition.getVersion() + "] not available"));
    }

    @Nonnull
    private Form setReadonly(@Nonnull Form form, FormData formData) {
        elementStream(form, formData)
                .filter(e -> !(e instanceof FormContainer))
                .forEach(element -> {
                    String parameterName = FormUtil.getElementParameterName(element);
                    String parameterValue = formData.getRequestParameter(parameterName);
                    FormUtil.setReadOnlyProperty(element);
//                    if(Objects.isNull(parameterValue)) {
//                        FormUtil.setReadOnlyProperty(element);
//                    }
                });
        return form;
    }

    /**
     * Construct JSON Object from Form Errors
     *
     * @param formErrors
     * @return
     */
    private JSONObject createErrorObject(Map<String, String> formErrors) {
        final JSONObject result = new JSONObject();

        // show error message
        formErrors.forEach(throwableBiConsumer(result::put));

        return result;
    }

    /**
     * Convert {@link FormRowSet} to {@link JSONArray}
     *
     * @param rowSet
     * @return
     */
    @Deprecated
    @Nonnull
    private JSONArray convertFormRowSetToJsonArray(@Nonnull final Element element, @Nonnull final FormData formData, @Nullable final FormRowSet rowSet) {
        return Optional.ofNullable(rowSet)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(r -> convertFromRowToJsonObject(element, formData, r))
                .collect(jsonCollector());
    }

    /**
     * Collect grid element
     *
     * @param gridElement
     * @param rowSet
     * @return
     */
    private JSONArray collectGridElement(@Nonnull final GridElement gridElement, @Nonnull final FormRowSet rowSet) {
        return rowSet.stream()
                .map(r -> collectGridElement(gridElement, r))
                .collect(jsonCollector());
    }

    /**
     * Collect grid element
     *
     * @param gridElement
     * @param row
     * @return
     */
    private JSONObject collectGridElement(@Nonnull final GridElement gridElement, @Nonnull final FormRow row) {
        final AppDefinition appDefinition = AppUtil.getCurrentAppDefinition();
        final Map<String, String>[] columnProperties = gridElement.getColumnProperties();

        if (columnProperties == null && gridElement instanceof Element) {
            return collectElement((Element) gridElement, row);
        } else {
            final JSONObject jsonObject = Optional.ofNullable(columnProperties)
                    .map(Arrays::stream)
                    .orElseGet(Stream::empty)
                    .collect(jsonCollector(gridElement::getField, m -> {
                        String primaryKey = Optional.of(row).map(FormRow::getId).orElse("");
                        String columnName = Optional.of(m)
                                .map(gridElement::getField)
                                .orElse("");

                        return Optional.of(columnName)
                                .filter(this::isNotEmpty)
                                .map(row::getProperty)
                                .map(s -> gridElement.formatColumn(columnName, m, primaryKey, s, appDefinition.getAppId(), appDefinition.getVersion(), ""))
                                .orElse(null);
                    }));

            collectRowMetaData(row, jsonObject);

            return jsonObject;
        }
    }

    /**
     * Collect container element
     *
     * @param containerElement
     * @param formData
     * @return
     */
    private JSONObject collectContainerElement(@Nonnull final FormContainer containerElement, @Nonnull final FormData formData, @Nonnull final FormRow row) {
        assert containerElement instanceof Element;

        final JSONObject jsonObject = elementStream((Element) containerElement, formData)
                .filter(e -> !(e instanceof FormContainer))
                .collect(jsonCollector(e -> e.getPropertyString(FormUtil.PROPERTY_ID),
                        e -> e.getElementValue(formData)));

        collectRowMetaData(row, jsonObject);

        return jsonObject;
    }

    /**
     * Collect element
     *
     * @param element
     * @param row
     * @return
     */
    private JSONObject collectElement(@Nonnull final Element element, @Nonnull final FormRow row) {
        Objects.requireNonNull(element);

        final JSONObject jsonObject = row.entrySet().stream()
                .collect(jsonCollector(e -> e.getKey().toString(), Map.Entry::getValue));

        collectRowMetaData(row, jsonObject);

        return jsonObject;
    }

    /**
     * Collect element
     *
     * @param element
     * @param rowSet
     * @return
     */
    private JSONArray collectElement(@Nonnull final Element element, @Nonnull final FormRowSet rowSet) {
        return rowSet.stream()
                .map(r -> collectElement(element, r))
                .collect(jsonCollector());
    }

    /**
     * Collect metadata
     *
     * @param row
     * @param jsonObject I/O
     */
    private void collectRowMetaData(@Nonnull final FormRow row, @Nonnull final JSONObject jsonObject) {
        jsonPutOnce("_" + FormUtil.PROPERTY_ID, row.getId(), jsonObject);
        jsonPutOnce("_" + FormUtil.PROPERTY_DATE_CREATED, row.getDateCreated(), jsonObject);
        jsonPutOnce("_" + FormUtil.PROPERTY_CREATED_BY, row.getCreatedBy(), jsonObject);
        jsonPutOnce("_" + FormUtil.PROPERTY_DATE_MODIFIED, row.getDateModified(), jsonObject);
        jsonPutOnce("_" + FormUtil.PROPERTY_MODIFIED_BY, row.getModifiedBy(), jsonObject);
    }

    /**
     *
     * @param key   I
     * @param value I
     * @param json  I/O
     */
    private void jsonPutOnce(@Nonnull String key, Object value, @Nonnull final JSONObject json) {
        try {
            if (!json.has(key))
                json.put(key, value);
        } catch (JSONException e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
        }
    }

    /**
     * Convert {@link FormRow} to {@link JSONObject}
     *
     * @param row
     * @return
     */
    @Deprecated
    @Nonnull
    private JSONObject convertFromRowToJsonObject(@Nonnull final Element element, @Nonnull final FormData formData, @Nonnull final FormRow row) {
        if (element instanceof GridElement) {
            return collectGridElement((GridElement) element, row);
        } else if (element instanceof FormContainer) {
            return collectContainerElement((FormContainer) element, formData, row);
        } else {
            return collectElement(element, row);
        }
    }

    /**
     * Convert Multi Value Parameter to List
     *
     * @param parameter request parameter values
     * @return sorted list
     */
    @Nonnull
    private List<String> convertMultiValueParameterToList(@Nullable String[] parameter) {
        return Optional.ofNullable(parameter)
                .map(Arrays::stream)
                .orElse(Stream.of(""))
                .map(s -> s.split(";,"))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get Primary Key
     *
     * @param dataList
     * @return
     */
    @Nonnull
    private String getPrimaryKeyColumn(@Nonnull final DataList dataList) {
        return Optional.of(dataList)
                .map(DataList::getBinder)
                .map(DataListBinder::getPrimaryKeyColumnName)
                .orElse("id");
    }

    /**
     * Get assignment from form data
     *
     * @param formData
     * @return
     */
    @Nullable
    private WorkflowAssignment getAssignment(@Nonnull FormData formData) {
        return Optional.of(formData)
                // try load addignment from activity ID
                .map(FormData::getActivityId)
                .map(throwableFunction(this::getAssignment, (ApiException e) -> null))

                // if fails, try to load assignment from process ID
                .orElseGet(throwableSupplier(() -> Optional.of(formData)
                        .map(FormData::getProcessId)
                        .map(throwableFunction(this::getAssignmentByProcess, (ApiException e) -> null))
                        .orElse(null)));
    }

    /**
     * Get assignment from process ID
     *
     * @param mapPrimaryKeyToProcessId
     * @param primaryKey
     * @return
     */
    @Nullable
    private WorkflowAssignment getAssignmentFromProcessIdMap(final Map<String, Collection<String>> mapPrimaryKeyToProcessId, String primaryKey) {
        return Optional.ofNullable(primaryKey)
                .map(mapPrimaryKeyToProcessId::get)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(workflowManager::getAssignmentByProcess)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /**
     * Null-safe way to retrieve {@link FormData#getFormErrors()}
     *
     * @param formData
     * @return
     */
    @Nonnull
    private Map<String, String> getFormErrors(FormData formData) {
        return Optional.ofNullable(formData)
                .map(FormData::getFormErrors)
                .orElseGet(HashMap::new);
    }

    /**
     * Assign value to JSON
     *
     * @param value
     * @return
     */
    @Nonnull
    private Object assignValue(@Nonnull Object value) {
        String stringValue = value.toString();
        try {
            return new JSONArray(stringValue);
        } catch (JSONException e1) {
            try {
                return new JSONObject(stringValue);
            } catch (JSONException e2) {
                return stringValue;
            }
        }
    }

    /**
     * Check datalist authorization
     * Restrict if no permission is set and user is anonymous
     *
     * @param dataList
     * @return
     */
    private boolean isAuthorize(@Nonnull DataList dataList) {
        return (dataList.getPermission() != null || !WorkflowUtil.isCurrentUserAnonymous())
                && dataListService.isAuthorize(dataList);
    }

    /**
     * Check form authorization
     * Restrict if no permission is set and user is anonymous
     *
     * @param form
     * @param formData
     * @return
     */
    private boolean isAuthorize(@Nonnull Form form, FormData formData) {
        return (form.getProperty("permission") != null || !WorkflowUtil.isCurrentUserAnonymous())
                && form.isAuthorize(formData);
    }
}
