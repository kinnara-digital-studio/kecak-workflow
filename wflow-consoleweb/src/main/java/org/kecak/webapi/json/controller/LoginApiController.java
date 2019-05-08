package org.kecak.webapi.json.controller;


import org.joget.apps.app.service.ApiTokenService;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.service.DirectoryManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.kecak.webapi.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class LoginApiController {
    private final String loginHeader = "Authorization";
    private final String refreshHeader = "REF_TOKEN";
    public final static String NEW_TOKEN = "NEW_TOKEN";
    private final static String MESSAGE_SUCCESS = "Success";

    @Autowired
    @Qualifier("main")
    DirectoryManager directoryManager;

    @Autowired
    ApiTokenService apiTokenService;

    @RequestMapping(value = "/oauth/login", method = RequestMethod.POST)
    public void postBasicLogin(final HttpServletRequest request,
                               final HttpServletResponse response) throws IOException {
        final JSONObject jsonResponse = new JSONObject();
        String header = request.getHeader(loginHeader);

        try {
            if(header == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid request header");
            }

            if(!header.startsWith("Basic ")) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Only receive Basic Authentication");
            }
            String[] tokens = extractAndDecodeHeader(header, request);

            String username = tokens[0];
            LogUtil.debug(this.getClass().getName(), "Basic authentication found for user " + username);
            String password = tokens[1];

            boolean invalidLogin = !directoryManager.authenticate(username, password);
            if (invalidLogin)
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Username or Password");

            final JSONObject requestPayload = new JSONObject(request.getReader().lines().collect(Collectors.joining()));
            Map<String, Object> claim = parseClaimFromRequestPayload(requestPayload);
            String jwtToken = apiTokenService.generateToken(username, claim);

            jsonResponse.put("status", HttpServletResponse.SC_OK);
            jsonResponse.put("message", MESSAGE_SUCCESS);
            jsonResponse.put("token", jwtToken);

            response.setContentType("application/json");
            response.getWriter().write(jsonResponse.toString());
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), "Error [" + e.getErrorCode() + "] message [" + e.getMessage() + "]");
            response.sendError(e.getErrorCode(), "Error " + e.getErrorCode() + " : " + e.getMessage());
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private Map<String, Object> parseClaimFromRequestPayload(JSONObject requestPayload) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                (Iterator<String>)requestPayload.keys(), 0), false)
                .collect(HashMap::new, (m, k) -> {
                    try {
                        m.put(k, requestPayload.get(k));
                    } catch (JSONException ignored) { }
                }, Map::putAll);
    }

    @RequestMapping(value = "oauth/refresh", method = RequestMethod.POST)
    public void refreshToken(final HttpServletRequest request,
                             final HttpServletResponse response) throws IOException {
        final JSONObject jsonResponse = new JSONObject();

        String header = request.getHeader(loginHeader);
        String refToken = request.getHeader(refreshHeader);

        if(header != null && header.startsWith("Bearer ")
                && refToken != null && !refToken.isEmpty()) {
            String token = header.substring(7);
            try {
                String newToken = apiTokenService.refreshToken(token, refToken);
                response.setHeader(NEW_TOKEN, newToken);
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

        byte[] base64Token = header.substring(6).getBytes("UTF-8");
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        }
        catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    "Failed to decode basic authentication token");
        }

        String token = new String(decoded, "UTF-8");

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[] { token.substring(0, delim), token.substring(delim + 1) };
    }
}
