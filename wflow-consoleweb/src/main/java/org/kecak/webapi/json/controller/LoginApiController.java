package org.kecak.webapi.json.controller;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.service.DirectoryManager;
import org.json.JSONObject;
import org.kecak.webapi.security.JwtTokenUtil;
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
    JwtTokenUtil jwtTokenUtil;

    @RequestMapping(value = "/oauth/login", method = RequestMethod.POST)
    public void postBasicLogin(final HttpServletRequest request,
                               final HttpServletResponse response) throws IOException {
        final JSONObject jsonResponse = new JSONObject();
        String header = request.getHeader(loginHeader);
        if ( header!= null && header.startsWith("Basic ") ) {
            String tokens[] = extractAndDecodeHeader(header, request);

            String username = tokens[0];
            LogUtil.debug(this.getClass().getName(), "Basic authentication found for user " + username);
            String password = tokens[1];
            boolean validLogin = false;
            try {
                validLogin = directoryManager.authenticate(username, password);
                if (!validLogin)
                    throw new BadCredentialsException("Invalid Username or Password");
                else {
                    String jwtToken = jwtTokenUtil.generateToken(username);
                    jsonResponse.put("status", HttpServletResponse.SC_OK);
                    jsonResponse.put("message", MESSAGE_SUCCESS);
                    jsonResponse.put("token", jwtToken);
                    response.getWriter().write(jsonResponse.toString());
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error 205: Invalid Username or Password");
            }
        }
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
                String newToken = jwtTokenUtil.refreshToken(token, refToken);
                response.setHeader(NEW_TOKEN, newToken);
                jsonResponse.put("status", HttpServletResponse.SC_OK);
                jsonResponse.put("message", MESSAGE_SUCCESS);
                response.getWriter().write(jsonResponse.toString());
            } catch(Exception e) {
                LogUtil.error(this.getClass().getName(), e, null);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error when refreshing token: see log for details");
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
