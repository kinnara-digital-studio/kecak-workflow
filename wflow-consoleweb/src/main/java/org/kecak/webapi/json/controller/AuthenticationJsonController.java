package org.kecak.webapi.json.controller;


import org.joget.apps.app.service.AuthTokenService;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kecak.utils.Unclutter;
import org.kecak.webapi.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Controller
public class AuthenticationJsonController implements Unclutter {
    private final String loginHeader = "Authorization";
    private final String refreshHeader = "REF_TOKEN";
    public final static String NEW_TOKEN = "NEW_TOKEN";
    private final static String MESSAGE_SUCCESS = "Success";

    @Autowired
    @Qualifier("main")
    DirectoryManager directoryManager;

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    WorkflowUserManager workflowUserManager;

    @RequestMapping(value = "/json/authentication/login", method = RequestMethod.POST)
    public void postBasicAuthentication(final HttpServletRequest request,
                                        final HttpServletResponse response) throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            String username = WorkflowUtil.getCurrentUsername();

            boolean isAdmin = WorkflowUtil.isCurrentUserInRole(WorkflowUtil.ROLE_ADMIN);
            String loginAs = getOptionalParameter(request, "loginAs", "");
            User userAs = Optional.of(loginAs)
                    .map(directoryManager::getUserById)
                    .orElse(null);

            if (isAdmin && isNotEmpty(loginAs)) {
                if (userAs == null || isEmpty(userAs.getUsername())) {
                    throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Error logging in as [" + loginAs + "]");
                }

                LogUtil.info(getClass().getName(), "[" + username + "] is logging in as [" + loginAs + "]");

                workflowUserManager.setCurrentThreadUser(loginAs);
                username = loginAs;
            }

            JSONObject requestPayload;
            try {
                requestPayload = new JSONObject(request.getReader().lines().collect(Collectors.joining()));
            } catch (JSONException e) {
                requestPayload = new JSONObject();
            }

            Map<String, Object> claim = parseClaimFromRequestPayload(requestPayload);
            String jwtToken = authTokenService.generateToken(username, claim);

            try {
                final JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("status", HttpServletResponse.SC_OK);
                jsonResponse.put("message", MESSAGE_SUCCESS);
                jsonResponse.put("token", jwtToken);

                response.setContentType("application/json");
                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                LogUtil.error(getClass().getName(), e, e.getMessage());
            }
        } catch (ApiException e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    @RequestMapping(value = "/json/authentication/profile", method = RequestMethod.GET)
    public void getCheckToken(final HttpServletRequest request,
                              final HttpServletResponse response) throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            final String username = WorkflowUtil.getCurrentUsername();
            User user = Optional.ofNullable(directoryManager.getUserByUsername(username))
                    .orElseThrow(() -> new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User not [" + username + "] available"));

            JSONObject jsonData = new JSONObject();

            JSONObject jsonUser = getJsonUser(user);
            try {
                jsonData.put("user", jsonUser);
            } catch (JSONException e) {
                LogUtil.error(getClass().getName(), e, e.getMessage());
            }

            JSONArray jsonRoles = getJsonRoles(user);
            try {
                jsonData.put("roles", jsonRoles);
            } catch (JSONException e) {
                LogUtil.error(getClass().getName(), e, e.getMessage());
            }

            JSONArray jsonGroups = getJsonGroups(user);
            try {
                jsonData.put("groups", jsonGroups);
            } catch (JSONException e) {
                LogUtil.error(getClass().getName(), e, e.getMessage());
            }
//
//            JSONArray jsonEmployment = getJsonEmployment(user);
//            try {
//                jsonData.put("employment", jsonEmployment);
//            } catch (JSONException e) {
//                LogUtil.error(getClass().getName(), e, e.getMessage());
//            }

            response.getWriter().write(jsonData.toString());
        } catch (ApiException e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }

    }

    private Map<String, Object> parseClaimFromRequestPayload(JSONObject requestPayload) {
        return jsonStream(requestPayload)
                .collect(Collectors.toMap(k -> k, throwableFunction(requestPayload::get)));
    }

    @RequestMapping(value = "json/authentication/refresh", method = RequestMethod.POST)
    public void refreshToken(final HttpServletRequest request,
                             final HttpServletResponse response) throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        String header = request.getHeader(loginHeader);
        String refToken = request.getHeader(refreshHeader);

        if(header != null && header.startsWith("Bearer ")
                && refToken != null && !refToken.isEmpty()) {
            String token = header.substring(7);
            try {
                String newToken = authTokenService.refreshToken(token, refToken);
                response.setHeader(NEW_TOKEN, newToken);

                final JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("status", HttpServletResponse.SC_OK);
                jsonResponse.put("message", MESSAGE_SUCCESS);
                response.getWriter().write(jsonResponse.toString());
            } catch(Exception e) {
                LogUtil.error(getClass().getName(), e, e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error when refreshing token, see log for details");
            }
        }
    }

    private String[] extractAndDecodeHeader(String header, HttpServletRequest request)
            throws IOException {

        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        }
        catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    "Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[] { token.substring(0, delim), token.substring(delim + 1) };
    }


    /**
     * Get user data in JSON format
     * @param user
     * @return
     * @throws JSONException
     */
    private JSONObject getJsonUser(User user) throws ApiException {
        try {
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("id", user.getId());
            jsonUser.put("username", user.getUsername());
            jsonUser.put("email", user.getEmail());
            jsonUser.put("firstName", user.getFirstName());
            jsonUser.put("lastName", user.getLastName());
            jsonUser.put("active", user.getActive());
            return jsonUser;
        } catch (JSONException e) {
            throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, e);
        }
    }

    /**
     * Get user's roles as JSONArray
     *
     * @param user
     * @return
     */
    private JSONArray getJsonRoles(User user) {
        return Optional.ofNullable(user)
                .map(User::getUsername)
                .map(directoryManager::getUserRoles)
                .map(Collection::stream)
                .orElseGet(Stream::empty)

                // create JSONObject for Role
                .map(r -> {
                    JSONObject jsonRole = new JSONObject();
                    try {
                        jsonRole.put("id", r.getId());
                        jsonRole.put("description", r.getDescription());
                        return jsonRole;
                    } catch (JSONException e) {
                        LogUtil.error(getClass().getName(), e, e.getMessage());
                    }

                    return null;
                })
                .filter(Objects::nonNull)

                // combine JSON Role as JSON Array
                .collect(JSONArray::new, JSONArray::put, (arr1, arr2) -> {
                    for (int i = 0, size = arr2.length(); i < size; i++) {
                        try {
                            arr1.put(arr2.get(i));
                        } catch (JSONException ignored) {
                        }
                    }
                });
    }


    /**
     *
     * @param user
     * @return
     */
    private JSONArray getJsonGroups(User user) {
        return Optional.ofNullable(user)
                .map(User::getUsername)
                .map(directoryManager::getGroupByUsername)
                .map(Collection::stream)
                .orElseGet(Stream::empty)

                // create JSONObject for Department
                .map(g -> {
                    JSONObject jsonGroup = new JSONObject();
                    try {
                        jsonGroup.put("id", g.getId());
                        jsonGroup.put("description", g.getDescription());
                        jsonGroup.put("organization", g.getOrganizationId());
                        return jsonGroup;
                    } catch (JSONException e) {
                        LogUtil.error(getClass().getName(), e, e.getMessage());
                    }

                    return null;
                })

                .filter(Objects::nonNull)

                // combine JSON Role as JSON Array
                .collect(JSONArray::new, JSONArray::put, (arr1, arr2) -> {
                    for (int i = 0, size = arr2.length(); i < size; i++) {
                        try {
                            arr1.put(arr2.get(i));
                        } catch (JSONException ignored) { }
                    }
                });
    }

    /**
     * Get optional parameter
     *
     * @param request
     * @param parameterName
     * @param defaultValue
     * @return
     */
    private String getOptionalParameter(@Nonnull HttpServletRequest request, @Nonnull String parameterName, String defaultValue) {
        return Optional.of(parameterName)
                .map(request::getParameter)
                .filter(not(String::isEmpty))
                .orElse(defaultValue);
    }
//
//
//    private JSONArray getJsonEmployment(@Nullable final User user) {
//        return Optional.ofNullable(user)
//                .map(User::getEmployments)
//                .map(es -> (Collection<Employment>)es)
//                .map(Collection::stream)
//                .orElseGet(Stream::empty)
//                .map(e -> {
//                    assert user != null;
//
//                    JSONObject jsonOrganization = new JSONObject();
//                    try {
//                        jsonOrganization.put("id", e.getId());
//                        jsonOrganization.put("organization", getJsonOrganization(e.getOrganization(), user.getId()));
//                        return jsonOrganization;
//                    } catch (JSONException ex) {
//                        return null;
//                    }
//                })
//
//                .filter(Objects::nonNull)
//
//                // combine JSON Role as JSON Array
//                .collect(JSONArray::new, JSONArray::put, (arr1, arr2) -> {
//                    for (int i = 0, size = arr2.length(); i < size; i++) {
//                        try {
//                            arr1.put(arr2.get(i));
//                        } catch (JSONException ignored) { }
//                    }
//                });
//    }
//
//    @Nonnull
//    private JSONObject getJsonOrganization(Organization organization, String username) throws JSONException {
//        JSONObject jsonOrganization = new JSONObject();
//        jsonOrganization.put("id", organization.getId());
//        jsonOrganization.put("description", organization.getDescription());
//        jsonOrganization.put("department", getJsonDepartment(organization.getDepartments(), username));
//        return jsonOrganization;
//    }
//
//    @Nullable
//    private JSONObject getJsonDepartment(Set<Department> departments, String username) {
//        return Optional.ofNullable(departments)
//                .map(Collection::stream)
//                .orElseGet(Stream::empty)
//                .filter(d -> Optional.ofNullable(d.getEmployments()).map(Collection::stream).orElseGet(Stream::empty).anyMatch(e -> ((Employment) e).getUserId().equals(username)))
//                .map(d -> {
//                    try {
//                        JSONObject jsonDepartment = new JSONObject();
//                        jsonDepartment.put("id", d.getId());
//                        jsonDepartment.put("description", d.getDescription());
//                        jsonDepartment.put("hod", Optional.ofNullable(d.getHod()).map(Employment::getId).orElse(null));
//                        return jsonDepartment;
//                    } catch (JSONException e) {
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .findFirst()
//                .orElse(null);
//    }
}

