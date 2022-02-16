package org.kecak.webapi.json.controller;

import com.kinnarastudio.commons.Declutter;
import com.kinnarastudio.commons.Try;
import org.apache.commons.io.IOUtils;
import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.dao.DatalistDefinitionDao;
import org.joget.apps.app.dao.PackageDefinitionDao;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.app.service.AuditTrailManager;
import org.joget.apps.datalist.service.DataListService;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.service.FormService;
import org.joget.commons.util.HashSalt;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.PasswordGeneratorUtil;
import org.joget.commons.util.SetupManager;
import org.joget.directory.dao.UserDao;
import org.joget.directory.dao.UserSaltDao;
import org.joget.directory.model.User;
import org.joget.directory.model.UserSalt;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.directory.model.service.DirectoryUtil;
import org.joget.directory.model.service.UserSecurity;
import org.joget.workflow.model.dao.WorkflowHelper;
import org.joget.workflow.model.dao.WorkflowProcessLinkDao;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.kecak.apps.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class DirectoryProfileJsonController implements Declutter {
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
    @Autowired
    WorkflowUserManager workflowUserManager;
    @Autowired
    UserDao userDao;
    @Autowired
    @Qualifier("main")
    DirectoryManager directoryManager;

    /**
     * Get Current Profile
     * <p>
     * Deprecated, please use /json/directory/profile instead
     *
     * @param request  HTTP Request
     * @param response HTTP Response
     * @throws ApiException
     */
    @Deprecated
    @RequestMapping(value = "/json/data/profile/current", method = RequestMethod.GET)
    public void getCurrentProfile(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        getProfile(request, response);
    }

    /**
     * Get profile picture
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/json/directory/profile/picture/(*:username)", method = RequestMethod.GET)
    public void getProfilePicture(final HttpServletRequest request, final HttpServletResponse response,
                                  @RequestParam("username") final String username) throws IOException {
        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");
        response.setContentType("image/jpeg");
        try {
            User user = userDao.getUser(username);
            if (user == null) {
                throw new ApiException(HttpServletResponse.SC_NOT_FOUND, "User not found");
            }

            Blob blob = user.getProfilePicture();
            try (InputStream in = blob.getBinaryStream()) {
                IOUtils.copy(in, response.getOutputStream());
            } catch (IOException | SQLException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }

        } catch (ApiException e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * Get Profile
     *
     * @param request  HTTP Request
     * @param response HTTP Response
     * @throws ApiException
     */
    @RequestMapping(value = "/json/directory/profile", method = RequestMethod.GET)
    public void getProfile(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            final JSONObject jsonResponse = Optional.of(workflowUserManager.getCurrentUsername())
                    .map(directoryManager::getUserByUsername)
                    .map(Try.onFunction(this::getUserAsJson))
                    .orElseThrow(() -> new ApiException(HttpServletResponse.SC_NOT_FOUND, "User not found"));

            response.getWriter().write(jsonResponse.toString());
        } catch (ApiException e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * Change user profile
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/json/directory/profile", method = {RequestMethod.PUT})
    public void putProfile(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        putProfile(request, response, WorkflowUtil.getCurrentUsername());
    }

    /**
     * Change user profile
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/json/directory/profile/(*:username)", method = {RequestMethod.PUT})
    public void putProfile(final HttpServletRequest request, final HttpServletResponse response,
                           @RequestParam("username") final String username) throws IOException {
        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            final String currentUser = WorkflowUtil.getCurrentUsername();
            if (!currentUser.equals(username) && !WorkflowUtil.isCurrentUserInRole(WorkflowUtil.ROLE_ADMIN)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "Current user is not an Administrator");
            }

            final User user = directoryManager.getUserByUsername(username);
            if (user == null) {
                throw new ApiException(HttpServletResponse.SC_NOT_FOUND, "User [" + username + "] not found");
            }

            final JSONObject jsonBody = getRequestPayload(request);

            try {
                user.setFirstName(jsonBody.getString("firstName"));
            } catch (JSONException ignored) {
            }

            try {
                user.setLastName(jsonBody.getString("lastName"));
            } catch (JSONException ignored) {
            }

            try {
                user.setEmail(jsonBody.getString("email"));
            } catch (JSONException ignored) {
            }

            try {
                user.setActive(jsonBody.getBoolean("active") ? 1 : 0);
            } catch (JSONException ignored) {
            }

            try {
                user.setTimeZone(jsonBody.getString("timeZone"));
            } catch (JSONException ignored) {
            }

            try {
                user.setLocale(jsonBody.getString("locale"));
            } catch (JSONException ignored) {
            }

            try {
                user.setTelephoneNumber(jsonBody.getString("telephoneNumber"));
            } catch (JSONException ignored) {
            }

            directoryManager.updateUser(user);

            final JSONObject jsonUser = getUserAsJson(user);

            response.getWriter().write(jsonUser.toString());

            // register to audit trail
            addAuditTrail("putProfile", new Object[]{request, response, username});

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.error(getClass().getName(), e, e.getMessage());
        } catch (JSONException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            LogUtil.error(getClass().getName(), e, e.getMessage());
        }
    }

    /**
     *
     * Reset Password for user ID
     * <p>
     * Requires json object field "password" as request body
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/json/directory/profile/resetPassword", method = RequestMethod.POST)
    public void postResetPassword(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        postResetPassword(request, response, "");
    }

    /**
     *
     * Reset Password for user ID
     * <p>
     * Requires json object field "password" as request body
     *
     * @param request
     * @param response
     * @param foo
     * @throws IOException
     */
    @RequestMapping(value = "/json/directory/profile/resetPassword/(~:foo)", method = {RequestMethod.POST, RequestMethod.PUT})
    public void postResetPassword(final HttpServletRequest request, final HttpServletResponse response,
                                  @RequestParam(value = "foo", defaultValue = "") String foo) throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        if(!foo.isEmpty()) {
            LogUtil.warn(getClass().getName(), "Parameter [" + foo + "] will be ignored");
        }

        try {
            final String currentUsername = WorkflowUtil.getCurrentUsername();

            final User user = Optional.of(currentUsername)
                    .map(directoryManager::getUserByUsername)
                    .orElseThrow(() -> new ApiException(HttpServletResponse.SC_NOT_FOUND, "User [" + currentUsername + "] is not available"));

            final JSONObject jsonBody = getRequestPayload(request);
            final String password = jsonBody.getString("password");

            user.setPassword(password);
            user.setConfirmPassword(password);

            if (updatePassword(user)) {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("message", "Password has been reset");
                response.getWriter().write(jsonResponse.toString());

                addAuditTrail("postResetPassword", new Object[]{
                        request,
                        response,
                        foo
                });
            } else {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Password is not supplied");
            }
        } catch (ApiException e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (JSONException e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Generate request body as JSONObject
     *
     * @param request
     * @return
     */
    @Nonnull
    protected JSONObject getRequestPayload(HttpServletRequest request) throws ApiException {
        try {
            String payload = request.getReader().lines().collect(Collectors.joining());
            return new JSONObject(ifEmptyThen(payload, "{}"));
        } catch (IOException | JSONException e) {
            throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, e);
        }
    }

    protected boolean updatePassword(User user) throws NoSuchAlgorithmException, InvalidKeySpecException {
        UserSecurity us = DirectoryUtil.getUserSecurity();
        ApplicationContext applicationContext = AppUtil.getApplicationContext();
        UserDao userDao = (UserDao) applicationContext.getBean("userDao");
        UserSaltDao userSaltDao = (UserSaltDao) applicationContext.getBean("userSaltDao");
        UserSalt userSalt = userSaltDao.getUserSaltByUserId(user.getUsername());

        if (user.getPassword() != null && user.getConfirmPassword() != null && user.getPassword().length() > 0 && user.getPassword().equals(user.getConfirmPassword())) {
            if (us != null) {
                user.setPassword(us.encryptPassword(user.getUsername(), user.getPassword()));
            } else {
                //currentUser.setPassword(StringUtil.md5Base16(user.getPassword()));
                HashSalt hashSalt = PasswordGeneratorUtil.createNewHashWithSalt(user.getPassword());
                userSalt.setRandomSalt(hashSalt.getSalt());

                user.setPassword(hashSalt.getHash());
            }
            user.setConfirmPassword(user.getPassword());

            userDao.updateUser(user);
            userSaltDao.updateUserSalt(userSalt);

            if (us != null) {
                us.updateUserProfilePostProcessing(user);
            }

            return true;
        }

        return false;
    }

    protected void addAuditTrail(String methodName, Object[] parameters) {
        final Class[] types = Optional.of(this)
                .map(Object::getClass)
                .map(Class::getMethods)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .filter(m -> methodName.equals(m.getName()) && m.getParameterCount() == parameters.length)
                .findFirst()
                .map(Method::getParameterTypes)
                .orElse(null);

        final HttpServletRequest request = WorkflowUtil.getHttpServletRequest();
        final String httpUrl = Optional.ofNullable(request).map(HttpServletRequest::getRequestURI).orElse("");
        final String httpMethod = Optional.ofNullable(request).map(HttpServletRequest::getMethod).orElse("");

        workflowHelper.addAuditTrail(
                DirectoryProfileJsonController.class.getName(),
                methodName,
                "Rest API " + httpUrl + " method " + httpMethod,
                types,
                parameters,
                null
        );
    }

    /**
     * Convert {@link User} to {@link JSONObject}
     *
     * @param user
     * @return
     * @throws JSONException
     */
    protected JSONObject getUserAsJson(User user) throws JSONException {
        final JSONObject jsonUser = new JSONObject();

        jsonUser.put("id", user.getId());
        jsonUser.put("dateCreated", user.getDateCreated());
        jsonUser.put("createdBy", user.getCreatedBy());
        jsonUser.put("dateModified", user.getDateModified());
        jsonUser.put("modifiedBy", user.getModifiedBy());
        jsonUser.put("username", user.getUsername());
        jsonUser.put("firstName", user.getFirstName());
        jsonUser.put("lastName", user.getLastName());
        jsonUser.put("email", user.getEmail());
        jsonUser.put("timeZone", user.getTimeZone());
        jsonUser.put("timeZoneLabel", user.getTimeZoneLabel());
        jsonUser.put("locale", user.getLocale());
        jsonUser.put("telephoneNumber", user.getTelephoneNumber());
        jsonUser.put("active", user.getActive() == 1);

        final String contextPath = Optional.ofNullable(WorkflowUtil.getHttpServletRequest())
                .map(HttpServletRequest::getContextPath)
                .orElse("");
        jsonUser.put("profilePicture", contextPath + "/directory/profile/picture/" + user.getUsername());

        return jsonUser;
    }
}
