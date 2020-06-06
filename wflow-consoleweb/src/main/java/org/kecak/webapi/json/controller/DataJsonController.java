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
import org.joget.apps.form.service.FileUtil;
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
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author aristo
 */
@Controller
public class DataJsonController {
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
            // read request body and convert request body to json
            final JSONObject jsonBody = getRequestPayload(request);

            final FormData formData = formService.retrieveFormDataFromRequestMap(null, convertJsonObjectToFormRow(null, jsonBody));

            // get current App Definition
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));

            Form form = getForm(appDefinition, formDefId, formData);

            fillStoreBinderInFormData(jsonBody, form, formData);

            // check form permission
            if (!form.isAuthorize(formData)) {
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
            LogUtil.warn(getClass().getName(), e.getMessage());
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

            final FormData formData = formService.retrieveFormDataFromRequestMap(null, convertJsonObjectToFormRow(null, jsonBody));

            // get current App Definition
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));

            Form form = getForm(appDefinition, formDefId, formData);

            // check form permission
            if (!form.isAuthorize(formData)) {
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
            LogUtil.warn(getClass().getName(), e.getMessage());
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
            // read request body and convert request body to json
            JSONObject jsonBody = getRequestPayload(request);

            final FormData formData = formService.retrieveFormDataFromRequestMap(null, convertJsonObjectToFormRow(null, jsonBody));

            // get current App Definition
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));

            Form form = getForm(appDefinition, formDefId, formData);

            // check element permission
            elementStream(form, formData)
                    .filter(e -> elementId.equals(e.getPropertyString(FormUtil.PROPERTY_ID)))
                    .filter(e -> e.isAuthorize(formData))
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
            LogUtil.warn(getClass().getName(), e.getMessage());
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
            // read request body and convert request body to json
            JSONObject jsonBody = getRequestPayload(request);

            final FormData formData = formService.retrieveFormDataFromRequestMap(null, convertJsonObjectToFormRow(null, jsonBody));

            // get current App Definition
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));

            Form form = getForm(appDefinition, formDefId, formData);

            // check form permission
            if (!form.isAuthorize(formData)) {
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

//                String errorMessage = Optional.of(form)
//                        .map(f -> FormUtil.findElement(elementId, form, result))
//                        .map(e -> FormUtil.getElementParameterName(e))
//                        .map(formErrors::get)
//                        .orElse(MESSAGE_VALIDATION_ERROR);
//
//                response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
            } else {
                // set current data as response
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
                response.getWriter().write(jsonResponse.toString());
            }
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
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
            // read request body and convert request body to json
            JSONObject jsonBody = getRequestPayload(request);
            final FormData formData = formService.retrieveFormDataFromRequestMap(null, convertJsonObjectToFormRow(null, jsonBody));

            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));

            Form form = getForm(appDefinition, formDefId, formData);

            fillStoreBinderInFormData(jsonBody, form, formData);

            // check form permission
            if (!form.isAuthorize(formData)) {
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
            LogUtil.warn(getClass().getName(), e.getMessage());
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
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));

            Form form = getForm(appDefinition, formDefId, formData);

            if (asLabel) {
                FormUtil.setReadOnlyProperty(form, true, true);
            }

            // check form permission
            if (!form.isAuthorize(formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            // construct response
            JSONObject jsonData = getData(form, formData);

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
            LogUtil.warn(getClass().getName(), e.getMessage());
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
                               @RequestParam("primaryKey") final String primaryKey)
            throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            final FormData formData = new FormData();
            formData.setPrimaryKeyValue(primaryKey);

            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));

            @Nonnull
            Form form = getForm(appDefinition, formDefId, formData);

            // check form permission
            if (!form.isAuthorize(formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            // construct response
            JSONObject jsonData = getData(form, formData);

            String currentDigest = getDigest(jsonData);

            JSONObject jsonResponse = new JSONObject();

            jsonResponse.put(FIELD_DATA, jsonData);
            jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
            jsonResponse.put(FIELD_DIGEST, currentDigest);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

//    /**
//     * Get Element Data
//     * Execute element's load binder
//     *
//     * @param request
//     * @param response
//     * @param appId
//     * @param appVersion
//     * @param formDefId
//     * @param elementId
//     * @param primaryKey
//     * @param digest
//     * @throws IOException
//     * @throws JSONException
//     */
//    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/form/(*:formDefId)/(*:elementId)/(*:primaryKey)", method = RequestMethod.GET)
//    public void getElementData(final HttpServletRequest request, final HttpServletResponse response,
//                               @RequestParam("appId") final String appId,
//                               @RequestParam("appVersion") final String appVersion,
//                               @RequestParam("formDefId") final String formDefId,
//                               @RequestParam("elementId") final String elementId,
//                               @RequestParam("primaryKey") final String primaryKey,
//                               @RequestParam(value = "includeSubForm", required = false, defaultValue = "false") final Boolean includeSubForm,
//                               @RequestParam(value = "digest", required = false) final String digest)
//            throws IOException, JSONException {
//
//        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");
//
//        try {
//            final FormData formData = new FormData();
//            formData.setPrimaryKeyValue(primaryKey);
//
//            // get current App
//            AppDefinition appDefinition = loadAppDefinition(appId, Long.parseLong(appVersion));
//
//            Form form = getForm(appDefinition, formDefId, formData);
//
//            // check form permission
//            if(!form.isAuthorize(formData)) {
//                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
//            }
//
//            Element element = FormUtil.findElement(elementId, form, formData, includeSubForm);
//            if (element == null) {
//                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid element [" + elementId + "]");
//            }
//
//            formService.executeFormLoadBinders(element, formData);
//
//            // construct response
//
//            @Nonnull
//            JSONArray jsonArrayData = convertFormRowSetToJsonArray(element, formData, formData.getLoadBinderData(element));
//
//            @Nullable
//            String currentDigest = getDigest(jsonArrayData);
//
//            JSONObject jsonResponse = new JSONObject();
//
//            if (!Objects.equals(currentDigest, digest)) {
//                jsonResponse.put(FIELD_DATA, jsonArrayData);
//            }
//
//            jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
//            jsonResponse.put(FIELD_DIGEST, currentDigest);
//
//            response.setStatus(HttpServletResponse.SC_OK);
//
//            response.getWriter().write(jsonResponse.toString());
//
//        } catch (ApiException e) {
//            response.sendError(e.getErrorCode(), e.getMessage());
//            LogUtil.warn(getClass().getName(), e.getMessage());
//        }
//    }

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
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));

            Form form = getForm(appDefinition, formDefId, formData);

            // check form permission
            if (!form.isAuthorize(formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            Element element = FormUtil.findElement(elementId, form, formData, includeSubForm);
            if (element == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid element [" + elementId + "]");
            }

            FormUtil.executeOptionBinders(element, formData);

            long pageSize = rows != null && rows > 0 ? rows : page != null && page > 0 ? DataList.DEFAULT_PAGE_SIZE : DataList.MAXIMUM_PAGE_SIZE;
            long rowStart = start != null ? start : page != null && page > 0 ? ((page - 1) * pageSize) : 0;

            @Nonnull
            FormRowSet formRows = Optional.ofNullable(formData.getOptionsBinderData(element, null))
                    .map(Collection::stream)
                    .orElseGet(Stream::empty)
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
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    /**
     * Load form data using form service as {@link FormRow}
     *
     * @param form
     * @param primaryKey
     * @return
     */
    private FormRow getData(@Nonnull Form form, String primaryKey) {
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
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));

            // get dataList definition
            DatalistDefinition datalistDefinition = datalistDefinitionDao.loadById(dataListId, appDefinition);
            if (datalistDefinition == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "DataList Definition for dataList [" + dataListId + "] not found");
            }

            // get dataList
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson(), false);
            if (dataList == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Error generating dataList [" + dataListId + "]");
            }

            // check permission
            if(!dataListService.isAuthorize(dataList)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] is not authorized to access datalist [" + dataListId + "]");
            }

            getCollectFilters(request.getParameterMap(), dataList);

            int total = dataList.getSize();

            try {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put(FIELD_TOTAL, total);
                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
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
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));

            // get dataList definition
            DatalistDefinition datalistDefinition = datalistDefinitionDao.loadById(dataListId, appDefinition);
            if (datalistDefinition == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "DataList Definition for dataList [" + dataListId + "] not found");
            }

            // get dataList
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson(), false);
            if (dataList == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Error generating dataList [" + dataListId + "]");
            }

            // check permission
            if(!dataListService.isAuthorize(dataList)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] is not authorized to access datalist [" + dataListId + "]");
            }

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
                        .collect(JSONArray::new, JSONArray::put, (a1, a2) -> {
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
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
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
                            @RequestParam(value = "asAttachmentUrl", defaultValue = "false") final Boolean asAttachmentUrl,
                            @RequestParam(value = "digest", required = false) final String digest)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get current App
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));

            // get dataList definition
            DatalistDefinition datalistDefinition = datalistDefinitionDao.loadById(dataListId, appDefinition);
            if (datalistDefinition == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "DataList Definition for dataList [" + dataListId + "] not found");
            }

            // get dataList
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson(), false);
            if (dataList == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Error generating dataList [" + dataListId + "]");
            }

            // check permission
            if(!dataListService.isAuthorize(dataList)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] is not authorized to access datalist [" + dataListId + "]");
            }

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

            Form form = getForm(appDefinition, formDefId, null);

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
                        .map(throwable(s -> {
                            final FormData formData = new FormData();
                            formData.setPrimaryKeyValue(s);

                            if (asAttachmentUrl) {
                                formData.addRequestParameterValues(FileDownloadSecurity.PARAMETER_AS_LINK, new String[]{"true"});
                            }

                            return Optional.of(form)
                                    .filter(f -> f.isAuthorize(formData))
                                    .map(throwable(f -> getData(f, formData), (Exception e) -> null))
                                    .orElse(null);

                        }))

                        .filter(Objects::nonNull)

                        // collect as JSON
                        .collect(JSONArray::new, JSONArray::put, (a1, a2) -> {
                            IntStream.iterate(0, i -> i + 1).limit(a2.length()).boxed()
                                    .map(throwable(a2::get))
                                    .filter(Objects::nonNull)
                                    .forEach(throwable((ThrowableConsumer<Object, RuntimeException>) a1::put));
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
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
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
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));

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
            final FormData formData = formService.retrieveFormDataFromRequestMap(null, convertJsonObjectToFormRow(null, jsonBody));
            fillStoreBinderInFormData(jsonBody, form, formData, true);

            // check form permission
            if (!form.isAuthorize(formData)) {
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
            LogUtil.warn(getClass().getName(), e.getMessage());
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
    @RequestMapping(value = "/json/data/assignment/(*:assignmentId)", method = RequestMethod.POST)
    public void postAssignmentComplete(final HttpServletRequest request, final HttpServletResponse response,
                                       @RequestParam("assignmentId") String assignmentId)
            throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            WorkflowAssignment assignment = getAssignment(assignmentId);
            AppDefinition appDefinition = getApplicationDefinition(assignment);

            // set current app definition
            AppUtil.setCurrentAppDefinition(appDefinition);

            // read request body and convert request body to json
            final JSONObject jsonBody = getRequestPayload(request);

            final FormData formData = formService.retrieveFormDataFromRequestMap(null, convertJsonObjectToFormRow(null, jsonBody));

            // get assignment form
            Form form = getAssignmentForm(appDefinition, assignment, formData);

            fillStoreBinderInFormData(jsonBody, form, formData, true);

            // check form permission
            if (!form.isAuthorize(formData)) {
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
                        .ifPresent(throwable(nextAssignment -> {
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
            LogUtil.warn(getClass().getName(), e.getMessage());
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
    @RequestMapping(value = "/json/data/assignment/process/(*:processId)", method = RequestMethod.POST)
    public void postAssignmentCompleteByProcess(final HttpServletRequest request, final HttpServletResponse response,
                                                @RequestParam("processId") String processId)
            throws IOException, JSONException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            WorkflowAssignment assignment = getAssignmentByProcess(processId);
            AppDefinition appDefinition = getApplicationDefinition(assignment);

            // set current app definition
            AppUtil.setCurrentAppDefinition(appDefinition);

            // read request body and convert request body to json
            final JSONObject jsonBody = getRequestPayload(request);

            final FormData formData = formService.retrieveFormDataFromRequestMap(null, convertJsonObjectToFormRow(null, jsonBody));

            // get assignment form
            Form form = getAssignmentForm(appDefinition, assignment, formData);

            fillStoreBinderInFormData(jsonBody, form, formData, true);

            // check form permission
            if (!form.isAuthorize(formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            Map<String, String> workflowVariables = generateWorkflowVariable(form, formData);

            FormData resultFormData = appService.completeAssignmentForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), assignment.getActivityId(), formData, workflowVariables);

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
                        .ifPresent(throwable(nextAssignment -> {
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
            LogUtil.warn(getClass().getName(), e.getMessage());
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
                              @RequestParam(value = "asAttachmentUrl", defaultValue = "false") final Boolean asAttachmentUrl,
                              @RequestParam(value = "digest", required = false) String digest)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            WorkflowAssignment assignment = getAssignment(assignmentId);
            AppDefinition appDefinition = getApplicationDefinition(assignment);

            // set current app definition
            AppUtil.setCurrentAppDefinition(appDefinition);

            // retrieve data
            Map<String, String> parameterMap = new HashMap<>();
            parameterMap.put("activityId", assignment.getActivityId());
            FormData formData = formService.retrieveFormDataFromRequestMap(new FormData(), parameterMap);
            if (asAttachmentUrl) {
                formData.addRequestParameterValues(FileDownloadSecurity.PARAMETER_AS_LINK, new String[]{"true"});
            }

            // generate form
            Form form = getAssignmentForm(appDefinition, assignment, formData);
            if (asLabel) {
                FormUtil.setReadOnlyProperty(form, true, true);
            }

            // check form permission
            if (!form.isAuthorize(formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            response.setStatus(HttpServletResponse.SC_OK);

            try {
                JSONObject jsonData = getData(form, formData);

                jsonData.put("activityId", assignment.getActivityId());
                jsonData.put("processId", assignment.getProcessId());

                String currentDigest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put(FIELD_DIGEST, currentDigest);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);

                if (!Objects.equals(digest, currentDigest))
                    jsonResponse.put(FIELD_DATA, jsonData);

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
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
                                       @RequestParam(value = "asLabel", defaultValue = "false") final Boolean asLabel,
                                       @RequestParam(value = "asAttachmentUrl", defaultValue = "false") final Boolean asAttachmentUrl,
                                       @RequestParam(value = "digest", required = false) String digest)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            WorkflowAssignment assignment = getAssignmentByProcess(processId);
            AppDefinition appDefinition = getApplicationDefinition(assignment);

            // set current app definition
            AppUtil.setCurrentAppDefinition(appDefinition);

            // retrieve data
            Map<String, String> parameterMap = new HashMap<>();
            parameterMap.put("activityId", assignment.getActivityId());
            FormData formData = formService.retrieveFormDataFromRequestMap(new FormData(), parameterMap);
            if (asAttachmentUrl) {
                formData.addRequestParameterValues(FileDownloadSecurity.PARAMETER_AS_LINK, new String[]{"true"});
            }

            Form form = getAssignmentForm(appDefinition, assignment, formData);

            if (asLabel) {
                FormUtil.setReadOnlyProperty(form, true, true);
            }

            // check form permission
            if (!form.isAuthorize(formData)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] doesn't have permission to open this form");
            }

            response.setStatus(HttpServletResponse.SC_OK);

            try {
                JSONObject jsonData = getData(form, formData);

                jsonData.put("activityId", assignment.getActivityId());
                jsonData.put("processId", assignment.getProcessId());

                String currentDigest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put(FIELD_DIGEST, currentDigest);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);

                if (!Objects.equals(digest, currentDigest))
                    jsonResponse.put(FIELD_DATA, jsonData);

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    /**
     * Get Assignment Count
     *
     * @param request   HTTP Request
     * @param response  HTTP Response
     * @param appId     Application ID
     * @param version   Application version
     * @param processId Process Definition ID
     * @throws IOException
     */
    @RequestMapping(value = "/json/data/assignments/count", method = RequestMethod.GET)
    public void getAssignmentsCount(final HttpServletRequest request, final HttpServletResponse response,
                                    @RequestParam(value = "appId", required = false) final String appId,
                                    @RequestParam(value = "version", required = false) final Long version,
                                    @RequestParam(value = "processId", required = false) final String processId) throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            AppDefinition appDefinition = getApplicationDefinition(appId, version);
            String processDefId = validateAndDetermineProcessDefId(appDefinition, processId);
            int total = workflowManager.getAssignmentSize(appId, processDefId, null);

            try {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put(FIELD_TOTAL, total);
                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * Get Assignment List
     *
     * @param request       HTTP Request
     * @param response      HTTP Response
     * @param appId         Application ID
     * @param appVersion    Application version
     * @param processId     Process Def ID
     * @param page          Page starts from 1
     * @param start         From index (index starts from 0)
     * @param rows          Page size (rows = 0 means load all data)
     * @param sort          Sort by field
     * @param desc          Descending (true/false)
     * @param digest        Digest
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
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));
            String processDefId = validateAndDetermineProcessDefId(appDefinition, processId);

            int pageSize = rows != null && rows > 0 ? rows : page != null && page > 0 ? DataList.DEFAULT_PAGE_SIZE : DataList.MAXIMUM_PAGE_SIZE;
            int rowStart = start != null ? start : page != null && page > 0 ? ((page - 1) * pageSize) : 0;

            // get total data
            int total = workflowManager.getAssignmentSize(appId, processDefId, null);

            FormRowSet resultRowSet = workflowManager.getAssignmentPendingAndAcceptedList(appId, processDefId, null, sort, desc, rowStart, pageSize).stream()
                    .map(WorkflowAssignment::getActivityId)
                    .map(workflowManager::getAssignment)
                    .filter(Objects::nonNull)
                    .map(throwable(assignment -> {
                        AppDefinition assignmentAppDefinition = getApplicationDefinition(assignment);
                        FormData formData = new FormData();
                        Form form = getAssignmentForm(assignmentAppDefinition, assignment, formData);

                        // check form permission
                        if (!form.isAuthorize(formData)) {
                            return null;
                        }

                        FormRow row = getData(form, formData.getPrimaryKeyValue());
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
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * Convert request body to form data
     *
     * @param jsonPayload
     * @param form
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws ApiException
     */
    @Nonnull
    private FormData fillStoreBinderInFormData(final JSONObject jsonPayload, final Form form, final FormData formData) throws IOException, JSONException, ApiException {
        return fillStoreBinderInFormData(jsonPayload, form, formData, false);
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

        elementStream(form, formData)
                .filter(e -> !(e instanceof Form || e instanceof Section || e instanceof Column))
                .filter(e -> e.getStoreBinder() != null)
                .forEach(throwable(element -> {
                    // handle store binder
                    processStoreBinder(element, formData);
                }));


        // handle files; no need to reupload the file if you still need to keep the old one,
        // simply don't send the field
        // unfortunately this only works for FileUpload, other fields still need to be re-uploaded
        // TODO : apply this kind of logic for the rests of the fields

        String uploadPath = getUploadFilePath();
        elementStream(form, formData)
                .filter(e -> e instanceof FileDownloadSecurity)
                .forEach(e -> {
                    String parameterName = FormUtil.getElementParameterName(e);
                    String parameterValue = Optional.of(parameterName)
                            .map(formData::getRequestParameter)
                            .orElse(null);

                    if (parameterValue != null) {
                        String[] filePaths = Optional.ofNullable(parameterValue)
                                .map(this::handleEncodedFile)
                                .map(Arrays::stream)
                                .orElseGet(Stream::empty)
                                .map(throwable((String s) -> decodeFile(s)))
                                .filter(Objects::nonNull)
                                .map(f -> FileManager.storeFile(f, uploadPath))
                                .toArray(String[]::new);

                        formData.addRequestParameterValues(parameterName, filePaths.length > 0 ? filePaths : new String[]{""});
                    }
                });
        ;

//        // handle request parameters
//        processRequestParameters(jsonBody, element, formData);

//        formData.setDoValidation(true);
//        formData.addRequestParameterValues(FormUtil.getElementParameterName(form) + "_SUBMITTED", new String[]{""});
//        formData.addRequestParameterValues("_action", new String[]{"submit"});

        // use field "ID" as primary key if possible
//        if (isEmpty(formData.getPrimaryKeyValue())) {
//            formData.setPrimaryKeyValue(jsonBody.optString(FormUtil.PROPERTY_ID));
//        }

        return formData;
    }

    /**
     * Get Upload File Path
     *
     * @return
     */
    private String getUploadFilePath() {
        String basePath = SetupManager.getBaseDirectory();
        String dataFileBasePath = setupManager.getSettingValue("dataFileBasePath");
        if (dataFileBasePath != null && dataFileBasePath.length() > 0) {
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
            return IntStream.iterate(0, i -> i + 1).limit(jsonArray.length()).boxed()
                    .map(throwable(jsonArray::getString))
                    .filter(Objects::nonNull)
                    .toArray(String[]::new);

        } catch (JSONException e) {
            return new String[]{value};
        }
    }

    /**
     * Process request parameters
     *
     * @param jsonBody
     * @param element
     * @param formData
     * @throws JSONException
     */
    private void processRequestParameters(@Nonnull JSONObject jsonBody, @Nonnull Element element, @Nonnull final FormData formData) throws JSONException {
        String key = element.getPropertyString(FormUtil.PROPERTY_ID);
        if (element instanceof FileDownloadSecurity) {
            JSONArray jsonValues = jsonBody.optJSONArray(key);

            // multiple file, values are in JSONArray
            if (jsonValues != null) {
                MultipartFile[] files = IntStream.iterate(0, i -> i + 1).limit(jsonBody.length()).boxed()
                        .map(throwable(jsonValues::getString))
                        .map(value -> addFileRequestParameter(value, element, formData))
                        .filter(Objects::nonNull)
                        .toArray(MultipartFile[]::new);

                if (files.length > 0) {
//                    FileStore.getFileMap().put(key, files);
                    for (MultipartFile file : files) {
                        FileUtil.storeFile(file, element, formData.getPrimaryKeyValue());
                    }
                }
            }

            // single file, value is in string
            else {
                String value = jsonBody.getString(key);
                MultipartFile file = addFileRequestParameter(value, element, formData);
                if (file != null) {
//                    FileStore.getFileMap().put(key, new MultipartFile[] { file });
                    FileUtil.storeFile(file, element, formData.getPrimaryKeyValue());
                }
            }
        } else {
            String value;
            if (element instanceof HiddenField) {
                value = getDefaultValue(element, formData);
            } else {
                value = jsonBody.optString(key, null);
                if (isEmpty(value)) {
                    value = getDefaultValue(element, formData);
                }
            }

            if (value != null) {
                formData.addRequestParameterValues(FormUtil.getElementParameterName(element), new String[]{value});
            }
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
                LogUtil.warn(getClass().getName(), e.getMessage());
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

        Form form = FormUtil.findRootForm(element);
        String parameterName = FormUtil.getElementParameterName(element);

        try {
            FormRowSet rowSet;

            // Multi row
            if (storeBinder instanceof FormStoreMultiRowElementBinder) {
                JSONArray values = new JSONArray(formData.getRequestParameter(parameterName));
                rowSet = convertJsonArrayToRowSet(form, values);
            }

            // Single row
            else if (storeBinder instanceof FormStoreElementBinder) {
                JSONObject value = new JSONObject(formData.getRequestParameter(parameterName));
                rowSet = convertJsonObjectToRowSet(form, value);
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

        } catch (JSONException e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
        }
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

    @Nullable
    private FormRow convertJsonObjectToFormRow(@Nullable Form form, @Nullable JSONObject json) {
        FormRow row = new FormRow();
        Iterator iterator = json.keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            try {
                String value = extractStringValue(form, json, key);
                row.put(key, value);
            } catch (JSONException e) {
                LogUtil.error(getClass().getName(), e, e.getMessage());
            }
        }

        return row;
    }

    @Nonnull
    private FormRowSet convertJsonObjectToRowSet(@Nullable Form form, @Nullable JSONObject json) {
        FormRowSet result = new FormRowSet();

        if (json != null) {
            FormRow row = convertJsonObjectToFormRow(form, json);
            if (row != null) {
                result.add(row);
            }
        }

        return result;
    }

    @Nonnull
    private FormRowSet convertJsonArrayToRowSet(@Nullable Form form, @Nonnull JSONArray jsonArray) {
        FormRowSet result = Optional.of(jsonArray)
                .map(a -> IntStream.range(0, a.length()).boxed())
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .map(jsonArray::optJSONObject)
                .map(j -> convertJsonObjectToFormRow(form, j))
                .collect(Collectors.toCollection(FormRowSet::new));

        result.setMultiRow(true);

        return result;
    }

    @Nonnull
    private String extractStringValue(@Nullable Form form, JSONObject json, String fieldName) throws JSONException {
        if (form == null) {
            return json.getString(fieldName);
        }

        Element element = FormUtil.findElement(fieldName, form, null, true);
        if (element instanceof FileDownloadSecurity) {
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
            return new JSONObject(payload);
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
    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/assignments/datalist/(*:dataListId)", method = RequestMethod.GET)
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
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));

            // get dataList definition
            DatalistDefinition datalistDefinition = datalistDefinitionDao.loadById(dataListId, appDefinition);
            if (datalistDefinition == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "DataList Definition for dataList [" + dataListId + "] not found");
            }

            // get dataList
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson(), false);
            if (dataList == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Error generating dataList [" + dataListId + "]");
            }

            // check permission
            if(!dataListService.isAuthorize(dataList)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] is not authorized to access datalist [" + dataListId + "]");
            }

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
                        .collect(JSONArray::new, throwable((jsonArray, row) -> {
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

                                if (form.isAuthorize(formData)) {
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
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    @RequestMapping(value = "/json/data/app/(*:appId)/(~:appVersion)/assignments/datalist/(*:dataListId)/count", method = RequestMethod.GET)
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
            AppDefinition appDefinition = getApplicationDefinition(appId, ifNull(appVersion, 0L));

            // get dataList definition
            DatalistDefinition datalistDefinition = datalistDefinitionDao.loadById(dataListId, appDefinition);
            if (datalistDefinition == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "DataList Definition for dataList [" + dataListId + "] not found");
            }

            // get dataList
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson(), false);
            if (dataList == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Error generating dataList [" + dataListId + "]");
            }

            // check permission
            if(!dataListService.isAuthorize(dataList)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] is not authorized to access datalist [" + dataListId + "]");
            }

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
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
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
     * @param appDefinition
     * @param assignment
     * @param formData input/output parameter
     * @return
     * @throws ApiException
     */
    @Nonnull
    private Form getAssignmentForm(AppDefinition appDefinition, WorkflowAssignment assignment, final FormData formData) throws ApiException {
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
     * @param requestParameters Request parameter
     * @param dataList          Input/Output parameter
     */
    private void getCollectFilters(@Nonnull final Map<String, String[]> requestParameters, @Nonnull final DataList dataList) {
        Arrays.stream(dataList.getFilters())
                .peek(f -> {
                    if (!(f.getType() instanceof DataListFilterTypeDefault))
                        LogUtil.warn(getClass().getName(), "DataList filter [" + f.getName() + "] is not instance of [" + DataListFilterTypeDefault.class.getName() + "], filter will be ignored");
                })
                .filter(f -> Objects.nonNull(requestParameters.get(f.getName())) && f.getType() instanceof DataListFilterTypeDefault)
                .forEach(f -> f.getType().setProperty("defaultValue", String.join(";", requestParameters.get(f.getName()))));
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
        if (dataList.getColumns() == null) {
            return Optional.ofNullable(row.get(field))
                    .map(String::valueOf)
                    .orElse("");
        }

        for (DataListColumn column : dataList.getColumns()) {
            if (!field.equals(column.getName())) {
                continue;
            }

            String value = Optional.ofNullable(row.get(field))
                    .map(String::valueOf)
                    .orElse("");

            if (column.getFormats() == null) {
                return value;
            }

            for (DataListColumnFormat format : column.getFormats()) {
                if (format != null) {
                    return format.format(dataList, column, row, value).replaceAll("<[^>]*>", "");
                }
            }
        }

        return Optional.of(field).map(row::get).map(String::valueOf).orElse("");
    }

    @Nonnull
    private Map<String, Object> formatRow(DataList dataList, Map<String, Object> row) {
        Map<String, Object> formatterRow = new HashMap<>();
        for (DataListColumn column : dataList.getColumns()) {
            String field = column.getName();
            formatterRow.put(field, formatValue(dataList, row, field));
        }

        String primaryKeyColumn = getPrimaryKeyColumn(dataList);
        formatterRow.put("_" + FormUtil.PROPERTY_ID, row.get(primaryKeyColumn));

        return formatterRow;
    }

    /**
     * Validate and Determine Process ID
     *
     * @param appDefinition Application definition
     * @param processId  Process ID
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
//        return Optional.ofNullable(workflowManager.getActivityById(activityId))
//                .map(WorkflowActivity::getProcessId)
//                .map(throwable(this::getAssignmentByProcess, (ApiException e) -> null))
//                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Assignment [" + activityId + "] not available"));

        return Optional.of(activityId)
                .map(workflowManager::getAssignment)
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
                .orElseGet(Stream::empty)
                .map(WorkflowProcessLink::getProcessId)
                .filter(Objects::nonNull)
                .map(workflowManager::getAssignmentByProcess)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Assignment for process [" + processId + "] not available"));
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

        AppDefinition appDefinition =  Optional.of(activityId)
                .map(this::getAppDefinitionForWorkflowActivity)
                .orElseGet(() -> Optional.of(processId)
                        .map(this::getAppDefinitionForWorkflowProcess)
                        .orElse(null));

        return Optional.ofNullable(appDefinition)
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Application definition for assignment [" + activityId + "] process [" + processId + "] not found"));
    }

    /**
     * Copy from {@link org.joget.apps.app.service.AppServiceImpl#getAppDefinitionForWorkflowActivity(String)}
     * with small changes
     *
     * @param activityId
     * @return
     */
    private AppDefinition getAppDefinitionForWorkflowActivity(String activityId) {
        AppDefinition appDef = null;

        WorkflowActivity activity = workflowManager.getActivityById(activityId);
        if (activity != null) {
            String processDefId = activity.getProcessDefId();
            WorkflowProcess process = workflowManager.getProcess(processDefId);
            if (process != null) {
                String packageId = process.getPackageId();
                Long packageVersion = Long.parseLong(process.getVersion());
                PackageDefinition packageDef = packageDefinitionDao.loadPackageDefinition(packageId, packageVersion);
                if (packageDef != null) {
                    appDef = packageDef.getAppDefinition();
                } else {
                    LogUtil.warn(getClass().getName(), "Undefined package [" + packageId + "] ["+packageVersion+"] for process ["+ processDefId + "]");
                }
            } else {
                LogUtil.warn(getClass().getName(), "Undefined process definition [" + processDefId + "]");
            }
        } else {
            LogUtil.warn(getClass().getName(), "Undefined assignment activity [" + activityId + "]");
        }
        return appDef;
    }

    /**
     * Copy from {@link org.joget.apps.app.service.AppServiceImpl#getAppDefinitionForWorkflowProcess(String)}
     * with small changes
     *
     * Retrieves the app definition for a specific workflow process.
     * @param processId
     * @return
     */
    private AppDefinition getAppDefinitionForWorkflowProcess(String processId) {
        AppDefinition appDef = null;

        WorkflowProcess process = workflowManager.getRunningProcessById(processId);
        if (process != null) {
            String packageId = process.getPackageId();
            Long packageVersion = Long.parseLong(process.getVersion());
            PackageDefinition packageDef = packageDefinitionDao.loadPackageDefinition(packageId, packageVersion);
            if (packageDef != null) {
                appDef = packageDef.getAppDefinition();
            } else {
                LogUtil.warn(getClass().getName(), "Undefined package [" + packageId + "] ["+packageVersion+"] for process ["+ processId + "]");
            }
        } else {
            LogUtil.warn(getClass().getName(), "Undefined process [" + processId + "]");
        }
        return appDef;
    }

    /**
     * Load form data
     *
     * @param form
     * @param formData
     */
    @Nonnull
    private JSONObject getData(@Nonnull final Form form, @Nonnull FormData formData) throws ApiException{
        // check result size
        Optional.of(form).map(formData::getLoadBinderData)
                .map(FormRowSet::size)
                .filter(i -> i > 0)
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_NOT_FOUND, "Data [" + formData.getPrimaryKeyValue() + "] in form [" + form.getPropertyString(FormUtil.PROPERTY_ID) + "] not found"));

        JSONObject parentJson = new JSONObject();
        Optional.of(formData)
                .map(fd -> elementStream(form, fd))
                .orElseGet(Stream::empty)
                .filter(e -> e.getLoadBinder() != null)
                .filter(e -> formData.getLoadBinderData(e) != null)
                .forEach(throwable(e -> {
                    FormRowSet rowSet = formData.getLoadBinderData(e);
                    String elementId = e.getPropertyString("id");

                    // Form, Section, or Subform
                    if (e instanceof FormContainer) {
                        JSONObject data = convertFormRowSetToJsonObject(e, formData, rowSet);
                        data.sortedKeys().forEachRemaining(throwable(key -> {
                                parentJson.put(key.toString(), data.get(key.toString()));
                        }));
                    }

                    // most likely grid element
                    else if (rowSet.isMultiRow()) {
                        JSONArray data = convertFormRowSetToJsonArray(e, formData, rowSet);
                        parentJson.put(elementId, data);
                    }

                    // any other element with load binder
                    else {
                        JSONObject data = convertFormRowSetToJsonObject(e, formData, rowSet);
                        parentJson.put(elementId, data);
                    }
                }));

        return parentJson;
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
    private AppDefinition getApplicationDefinition(String appId, long version) throws ApiException {
        return Optional.of(version == 0 ? appDefinitionDao.getPublishedVersion(appId) : version)
                .map(l -> appDefinitionDao.loadVersion(appId, l))

                // set current app definition
                .map(peek(AppUtil::setCurrentAppDefinition))

                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid application [" + appId + "] version [" + version + "]"));
    }

    /**
     * Helper peek for {@link Optional}
     *
     * @param consumer
     * @param <T>
     * @return
     */
    @Nonnull
    private <T> UnaryOperator<T> peek(@Nonnull final Consumer<T> consumer) {
        return t -> {
            consumer.accept(t);
            return t;
        };
    }

    /**
     * Null-safe way to retrieve {@link AppService#viewDataForm(String, String, String, String, String, String, FormData, String, String)}
     *
     * @param appDefinition
     * @param formDefId
     * @return
     * @throws ApiException
     */
    @Nonnull
    private Form getForm(AppDefinition appDefinition, String formDefId, FormData formData) throws ApiException {
        return Optional.ofNullable(appService.viewDataForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), formDefId, null, null, null, formData, null, null))
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Form [" + formDefId + "] in app [" + appDefinition.getAppId() + "] version [" + appDefinition.getVersion() + "] not available"));
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
        formErrors.forEach(throwable(result::put));

        return result;
    }

    /**
     * Convert {@link FormRowSet} to {@link JSONArray}
     *
     * @param rowSet
     * @return
     */
    @Nonnull
    private JSONArray convertFormRowSetToJsonArray(@Nonnull final Element element, @Nonnull final FormData formData, @Nullable final FormRowSet rowSet) {
        return Optional.ofNullable(rowSet)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(r -> convertFromRowToJsonObject(element, formData, r))
                .collect(JSONArray::new, JSONArray::put, (result, source) -> IntStream.range(0, source.length())
                        .boxed()
                        .map(source::optJSONObject)
                        .filter(Objects::nonNull)
                        .forEach(result::put));
    }

    /**
     * Convert {@link FormRow} to {@link JSONObject}
     *
     * @param row
     * @return
     */
    @Nonnull
    private JSONObject convertFromRowToJsonObject(@Nonnull final Element element, @Nonnull final FormData formData, @Nullable final FormRow row) {
        AppDefinition appDefinition = AppUtil.getCurrentAppDefinition();

        return Optional.ofNullable(row)
                .map(throwable(r -> {
                    final JSONObject json = new JSONObject();

                    r.forEach(throwable((k, v) -> {
                        String elementId = String.valueOf(k);

                        if (element instanceof GridElement) {
                            Arrays.stream(((GridElement) element).getColumnProperties())
                                    .filter(m -> k.equals(m.get("value")))
                                    .findFirst()

                                    // execute grid's format column
                                    .map(m -> ((GridElement) element).formatColumn(elementId, m, String.valueOf(r.getId()), String.valueOf(v), appDefinition.getAppId(), appDefinition.getVersion(), ""))

                                    .ifPresent(throwable(s -> json.put(elementId, s), (JSONException ignored) -> {}));
                        } else {
                            Optional.ofNullable(FormUtil.findElement(elementId, element, null))
                                    .map(e -> e.getElementValue(formData))
                                    .ifPresent(throwable(s -> json.put(elementId, s), (JSONException ignored) -> {}));
                        }
                    }));

                    json.put("_" + FormUtil.PROPERTY_ID, r.getId());
                    json.put("_" + FormUtil.PROPERTY_DATE_CREATED, r.getDateCreated());
                    json.put("_" + FormUtil.PROPERTY_CREATED_BY, r.getCreatedBy());
                    json.put("_" + FormUtil.PROPERTY_DATE_MODIFIED, r.getDateModified());
                    json.put("_" + FormUtil.PROPERTY_MODIFIED_BY, r.getModifiedBy());

                    return json;
                }, (JSONException e) -> null))
                .orElseGet(JSONObject::new);
    }

    /**
     * @param rowSet
     * @return
     */
    @Nonnull
    private JSONObject convertFormRowSetToJsonObject(@Nonnull final Element element, @Nonnull final FormData formData, @Nullable final FormRowSet rowSet) {
        return Optional.of(convertFormRowSetToJsonArray(element, formData, rowSet))
                .map(r -> r.optJSONObject(0))
                .orElseGet(JSONObject::new);
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
     * Nullsafe. If string is null or empty
     *
     * @param value
     * @return
     */
    private boolean isEmpty(@Nullable Object value) {
        return Optional.ofNullable(value)
                .map(String::valueOf)
                .map(String::isEmpty)
                .orElse(true);
    }

    /**
     * Nullsafe. If object is not null and not empty
     *
     * @param value
     * @return
     */
    private boolean isNotEmpty(@Nullable Object value) {
        return !isEmpty(value);
    }

    /**
     * Nullsafe. If collection is null or empty
     *
     * @param collection
     * @param <T>
     * @return
     */
    private <T> boolean isEmpty(@Nullable Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::isEmpty)
                .orElse(true);
    }

    /**
     * Nullsafe. If collection is not null and not empty
     *
     * @param collection
     * @param <T>
     * @return
     */
    private <T> boolean isNotEmpty(@Nullable Collection<T> collection) {
        return !isEmpty(collection);
    }


    /**
     * If value null than return failover
     *
     * @param value
     * @param failover
     * @param <T>
     * @return
     */
    @Nonnull
    private <T> T ifNull(@Nullable T value, @Nonnull T failover) {
        return value == null ? failover : value;
    }
    /**
     * Stream element children
     *
     * @param element
     * @return
     */
    @Nonnull
    private Stream<Element> elementStream(@Nonnull Element element, FormData formData) {
        if (!element.isAuthorize(formData)) {
            return Stream.empty();
        }

        Stream<Element> stream = Stream.of(element);
        for (Element child : element.getChildren()) {
            stream = Stream.concat(stream, elementStream(child, formData));
        }
        return stream;
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
     * Return null if string empty
     *
     * @param s
     * @return
     */
    private String nullIfEmpty(String s) {
        return s.isEmpty() ? null : s;
    }

    /*
     *
     *   DARN, THIS IS NEAT !!!!!!!
     *
     *
     *       * * *   * * *
     *     *       *       *
     *      *             *
     *        *         *
     *          *     *
     *            * *
     *             *
     */

    // Throwable methods

    /**
     * @param throwableConsumer
     * @param <T>
     * @param <E>
     * @return
     */
    private <T, E extends Exception> Consumer<T> throwable(ThrowableConsumer<T, E> throwableConsumer) {
        return throwableConsumer;
    }

    /**
     *
     * @param throwableConsumer
     * @param failoverConsumer
     * @param <T>
     * @param <E>
     * @return
     */
    private <T, E extends Exception> Consumer<T> throwable(ThrowableConsumer<T, E> throwableConsumer, Consumer<E> failoverConsumer) {
        return throwableConsumer.onException(failoverConsumer);
    }

    /**
     * @param throwableBiConsumer
     * @param <T>
     * @param <U>
     * @param <E>
     * @return
     */
    private <T, U, E extends Exception> BiConsumer<T, U> throwable(ThrowableBiConsumer<T, U, E> throwableBiConsumer) {
        return throwableBiConsumer;
    }

    /**
     * @param throwableFunction
     * @param <T>
     * @param <R>
     * @param <E>
     * @return
     */
    private <T, R, E extends Exception> ThrowableFunction<T, R, E> throwable(ThrowableFunction<T, R, E> throwableFunction) {
        return throwableFunction;
    }

    /**
     * @param throwableFunction
     * @param <T>
     * @param <R>
     * @param <E>
     * @return
     */
    private <T, R, E extends Exception> Function<T, R> throwable(ThrowableFunction<T, R, E> throwableFunction, Function<E, R> failoverFunction) {
        return throwableFunction.onException(failoverFunction);
    }

    // Extension for functional interfaces

    /**
     * Throwable version of {@link Function}.
     * Returns null then exception is raised
     *
     * @param <T>
     * @param <R>
     * @param <E>
     */
    @FunctionalInterface
    interface ThrowableFunction<T, R, E extends Exception> extends Function<T, R> {

        @Override
        default R apply(T t) {
            try {
                return applyThrowable(t);
            } catch (Exception e) {
                LogUtil.error(getClass().getName(), e, e.getMessage());
                return null;
            }
        }

        R applyThrowable(T t) throws E;

        /**
         * @param f
         * @return
         */
        default Function<T, R> onException(Function<? super E, ? extends R> f) {
            return (T a) -> {
                try {
                    return (R) applyThrowable(a);
                } catch (Exception e) {
                    return f.apply((E) e);
                }
            };
        }

        /**
         * @param f
         * @return
         */
        default Function<T, R> onException(BiFunction<? super T, ? super E, ? extends R> f) {
            return (T a) -> {
                try {
                    return (R) applyThrowable(a);
                } catch (Exception e) {
                    return f.apply(a, (E) e);
                }
            };
        }
    }

    /**
     * Throwable version of {@link Consumer}
     *
     * @param <T>
     * @param <E>
     */
    @FunctionalInterface
    interface ThrowableConsumer<T, E extends Exception> extends Consumer<T> {
        @Override
        default void accept(T t) {
            try {
                acceptThrowable(t);
            } catch (Exception e) {
                LogUtil.error(getClass().getName(), e, e.getMessage());
            }
        }

        default ThrowableConsumer<T, E> onException(final Consumer<E> c) {
            Objects.requireNonNull(c);
            return (T t) -> {
                try {
                    acceptThrowable(t);
                } catch (Exception e) {
                    c.accept((E) e);
                }
            };
        }

        void acceptThrowable(T t) throws E;
    }

    /**
     * Throwable version of {@link Predicate}
     *
     * @param <T>
     * @param <E>
     */
    @FunctionalInterface
    interface ThrowablePredicate<T, E extends Exception> extends Predicate<T> {
        @Override
        default boolean test(T t) {
            try {
                return testThrowable(t);
            } catch (Exception e) {
                return onException((E) e);
            }
        }

        default boolean onException(E e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
            return false;
        }

        boolean testThrowable(T t) throws E;
    }

    /**
     * Throwable version of {@link BiConsumer}
     *
     * @param <T>
     * @param <U>
     * @param <E>
     */
    @FunctionalInterface
    interface ThrowableBiConsumer<T, U, E extends Exception> extends BiConsumer<T, U> {
        default void accept(T t, U u) {
            try {
                acceptThrowable(t, u);
            } catch (Exception e) {
                onException((E) e);
            }
        }

        default void onException(E e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
        }

        void acceptThrowable(T t, U u) throws E;
    }
}
