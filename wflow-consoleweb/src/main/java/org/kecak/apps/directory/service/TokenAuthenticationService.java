package org.joget.apps.app.controller;

import io.jsonwebtoken.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.directory.model.service.ExtDirectoryManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.EnvironmentVariable;
import org.joget.workflow.util.WorkflowUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class TokenAuthenticationService {

    static final long EXPIRATIONTIME = 864_000_000; // 10 days
    //	static final long EXPIRATIONTIME = 6000;
    static final String SECRET = "ThisIsASecret";
    static final String TOKEN_PREFIX = "Bearer";
    static final String HEADER_STRING = "Authorization";
    static final String ISSUER = "Kecak";

    public static String addAuthentication(HttpServletResponse res, String username) {
        String JWT = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .setIssuer(ISSUER)
                .compact();
        return JWT;
    }

    public static User getAuthentication(HttpServletRequest request) throws Exception {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            String user = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody().getSubject();
            if (user.equals("")) {
                return null;
            } else {
                ApplicationContext ac = AppUtil.getApplicationContext();
                ExtDirectoryManager dm = (ExtDirectoryManager) ac.getBean("directoryManager");
                WorkflowUserManager wfUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
                wfUserManager.setCurrentThreadUser(user);
                return dm.getUserByUsername(user);
            }
        } else {
            // get from web session
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                String username = authentication.getName();
                if (!"roleAnonymous".equals(username)) {
                    ApplicationContext ac = AppUtil.getApplicationContext();
                    ExtDirectoryManager dm = (ExtDirectoryManager) ac.getBean("directoryManager");
                    return dm.getUserByUsername(username);
                }
            }
        }
        return null;
    }

    public static Claims getClaims(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);

        if (token != null && !token.isEmpty() && !token.contains("NOT FOUND")) {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""));

           return claims.getBody();
        }
        return null;
    }
}
