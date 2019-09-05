package org.joget.apps.workflow.security;

import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.util.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.IOUtils;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.commons.util.HostManager;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.SetupManager;
import org.joget.directory.dao.RoleDao;
import org.joget.directory.dao.UserDao;
import org.joget.directory.dao.UserTokenDao;
import org.joget.directory.model.Role;
import org.joget.directory.model.User;
import org.joget.directory.model.UserToken;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.workflow.model.dao.WorkflowHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class WorkflowAuthenticationProvider implements AuthenticationProvider, MessageSourceAware {

    transient
    @Autowired
    @Qualifier("main")
    private DirectoryManager directoryManager;

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserTokenDao userTokenDao;
    @Autowired
    private RoleDao roleDao;

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public DirectoryManager getDirectoryManager() {
        return directoryManager;
    }

    public void setDirectoryManager(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // reset profile and set hostname
        HostManager.initHost();

        LogUtil.debug(this.getClass().getName(), "INSIDE AUTH PROVIDER");

        // Determine username
        String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
        String password = authentication.getCredentials().toString();

        // check credentials
        boolean validLogin = false;
        try {
            if(username.equals("GOOGLE_AUTH")){
                String CLIENT_ID = SetupManager.getSettingValue("googleClientId");
//                String CLIENT_ID = "89454262416-5lgedc4aq5vt6e62971ep19hce3nlu23.apps.googleusercontent.com";
                if(!CLIENT_ID.isEmpty()) {
                    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(Utils.getDefaultTransport(), new JacksonFactory())
                            .setAudience(Collections.singletonList(CLIENT_ID))
                            .build();
                    GoogleIdToken idToken = verifier.verify(password);
                    if (idToken != null) {
                        GoogleIdToken.Payload payload = idToken.getPayload();
                        String email = payload.getEmail();
                        User u = userDao.getUserByEmail(email);
                        if (u != null) {
                            username = u.getUsername();
                            validLogin = true;
                            UserToken userToken = new UserToken();
                            userToken.setUserId(u.getId());
                            userToken.setPlatformId("google");
                            userTokenDao.deleteUserToken(userToken);
                            userToken.setId(UUID.randomUUID().toString());
                            userToken.setToken(password);
                            userTokenDao.addUserToken(userToken);
                        }
                    } else {
                        throw new BadCredentialsException("Invalid Google Token");
                    }
                }
            } else {
                validLogin = directoryManager.authenticate(username, password);
            }
        } catch (Exception e) {
            throw new BadCredentialsException(e.getMessage());
        }
        if (!validLogin) {
            LogUtil.info(getClass().getName(), "Authentication for user " + username + ": " + false);
            WorkflowHelper workflowHelper = (WorkflowHelper) AppUtil.getApplicationContext().getBean("workflowHelper");
            workflowHelper.addAuditTrail(this.getClass().getName(), "authenticate", "Authentication for user " + username + ": " + false, new Class[]{String.class}, new Object[]{username}, false);
            throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        // add audit trail
        LogUtil.info(getClass().getName(), "Authentication for user " + username + ": " + true);
        WorkflowHelper workflowHelper = (WorkflowHelper) AppUtil.getApplicationContext().getBean("workflowHelper");
        workflowHelper.addAuditTrail(this.getClass().getName(), "authenticate", "Authentication for user " + username + ": " + true, new Class[]{String.class}, new Object[]{username}, true);

        // get authorities
        Collection<Role> roles = directoryManager.getUserRoles(username);
        List<GrantedAuthority> gaList = new ArrayList<GrantedAuthority>();
        if (roles != null && !roles.isEmpty()) {
            for (Role role : roles) {
                GrantedAuthority ga = new SimpleGrantedAuthority(role.getId());
                gaList.add(ga);
            }
        }

        // return result
        User user = directoryManager.getUserByUsername(username);
        UserDetails details = new WorkflowUserDetails(user);
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(details, password, gaList);
        result.setDetails(details);
        return result;
    }

    public boolean supports(@SuppressWarnings("rawtypes") Class authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
