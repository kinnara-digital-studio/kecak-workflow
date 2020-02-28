package org.kecak.oauth2;

import org.kecak.apps.directory.service.TokenAuthenticationService;
import org.joget.directory.dao.ClientAppDao;
import org.joget.directory.model.ClientApp;
import org.joget.workflow.util.WorkflowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

public class Oauth2Handler implements AuthenticationSuccessHandler {
    @Autowired
    ClientAppDao clientAppDao;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        ClientApp searchParam = new ClientApp();
        searchParam.setId(request.getParameter("clientId"));
        searchParam.setClientSecret(request.getParameter("clientSecret"));
        ClientApp clientApp = clientAppDao.getClientApp(searchParam);
        if(!WorkflowUtil.isCurrentUserAnonymous() && clientApp != null) {
            URL redirectUrl = new URL(clientApp.getRedirectUrl());
            String token = TokenAuthenticationService.addAuthentication(clientApp.getId());
            response.sendRedirect(redirectUrl+"?token=" + token);
        } else {
            response.sendRedirect(request.getContextPath() + "/web/oauth2/login?login_error=1");
        }
    }
}
