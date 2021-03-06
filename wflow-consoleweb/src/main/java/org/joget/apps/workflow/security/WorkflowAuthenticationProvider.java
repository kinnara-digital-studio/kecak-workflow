package org.joget.apps.workflow.security;

import org.apache.commons.codec.binary.Hex;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.CsvUtil;
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
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.model.PropertyEditable;
import org.joget.plugin.property.service.PropertyUtil;
import org.joget.workflow.model.dao.WorkflowHelper;
import org.kecak.oauth.model.AbstractOauth2Client;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

public class WorkflowAuthenticationProvider implements AuthenticationProvider, MessageSourceAware {
    private DirectoryManager directoryManager;
    private PluginManager pluginManager;
    private UserTokenDao userTokenDao;

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Override
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
            AbstractOauth2Client oauth2ClientPlugin = (AbstractOauth2Client) pluginManager.getPlugin(username);
            String properties = SetupManager.getSettingValue(username);
            if(oauth2ClientPlugin != null && !properties.isEmpty()){
                Map propertyMap;
                if (!(oauth2ClientPlugin instanceof PropertyEditable)) {
                    propertyMap = CsvUtil.getPluginPropertyMap(properties);
                } else {
                    propertyMap = PropertyUtil.getPropertiesValueFromJson(properties);
                }
                oauth2ClientPlugin.setProperties(propertyMap);
                User u = oauth2ClientPlugin.getUser(password);
                if (u != null) {
                    username = u.getUsername();
                    validLogin = true;
                    UserToken userToken = new UserToken();
                    userToken.setUserId(u.getId());
                    userToken.setPlatformId(oauth2ClientPlugin.getClass().getName());
                    userTokenDao.deleteUserToken(userToken);
                    userToken.setId(UUID.randomUUID().toString());
                    userToken.setToken(password);
                    userTokenDao.addUserToken(userToken);
                } else {
                    throw new BadCredentialsException("User not found");
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
        List<GrantedAuthority> gaList = new ArrayList<>();
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

    @Override
    public boolean supports(@SuppressWarnings("rawtypes") Class authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public static String encodeHMacSHA256(byte[] key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key, "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes()));
    }

    public void setDirectoryManager(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public void setUserTokenDao(UserTokenDao userTokenDao) {
        this.userTokenDao = userTokenDao;
    }
}
