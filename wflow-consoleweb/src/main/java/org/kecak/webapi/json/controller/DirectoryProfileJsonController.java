package org.kecak.webapi.json.controller;

import com.kinnarastudio.commons.Declutter;
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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
    public void getProfilePicture(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("userId") final String username) throws IOException {
        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");
        // TODO
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
            User user = userDao.getUser(workflowUserManager.getCurrentUsername());
            if (user == null) {
                throw new ApiException(HttpServletResponse.SC_NOT_FOUND, "User not found");
            }

            Blob blob = user.getProfilePicture();
            try (InputStream in = blob.getBinaryStream();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
                BufferedImage image = ImageIO.read(in);
                ImageIO.write(image, "jpg", baos);
                baos.flush();
                byte[] imageInByteArray = baos.toByteArray();
                String b64 = javax.xml.bind.DatatypeConverter.printBase64Binary(imageInByteArray);

                JSONObject jsonResponse = new JSONObject();

                jsonResponse.put("username", user.getUsername());
                jsonResponse.put("profilePicture", "/web/json/directory/profile/picture/" + user.getUsername());

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException | IOException | SQLException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
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
        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");
        // TODO
    }

    /**
     * Reset Password for user ID
     *
     * Requires json object field "password" as request body
     *
     * @param request
     * @param response
     * @param userId
     * @throws IOException
     */
    @RequestMapping(value = "/json/directory/profile/resetPassword/(*:userId)", method = {RequestMethod.POST, RequestMethod.PUT})
    public void postResetPassword(final HttpServletRequest request, final HttpServletResponse response,
                                  @RequestParam("userId") String userId) throws IOException {
        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            String currentUser = WorkflowUtil.getCurrentUsername();
            if(!userId.equals(currentUser) && !WorkflowUtil.isCurrentUserInRole(WorkflowUtil.ROLE_ADMIN)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "Current user is not administrator");
            }

            User user = Optional.of(userId)
                    .map(userDao::getUserById)
                    .orElseThrow(() -> new ApiException(HttpServletResponse.SC_NOT_FOUND, "User [" + userId + "] is not available"));

            JSONObject jsonBody = getRequestPayload(request);
            String password = jsonBody.getString("password");

            user.setPassword(password);
            user.setConfirmPassword(password);

            if(updatePassword(user)) {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("message", "Password has been reset");
                response.getWriter().write(jsonResponse.toString());

                addAuditTrail("postResetPassword",new Object[]{
                        request,
                        response,
                        userId
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
                .filter(m -> methodName.equals(m.getName()))
                .findFirst()
                .map(Method::getParameterTypes)
                .orElse(null);

        final HttpServletRequest request = WorkflowUtil.getHttpServletRequest();
        final String httpUrl = Optional.ofNullable(request).map(HttpServletRequest::getRequestURI).orElse("");
        final String httpMethod = Optional.ofNullable(request).map(HttpServletRequest::getMethod).orElse("");

        workflowHelper.addAuditTrail(
                DataJsonController.class.getName(),
                methodName,
                "Rest API " + httpUrl + " method " + httpMethod,
                types,
                parameters,
                null
        );
    }
}
