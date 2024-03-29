package org.kecak.oauth2;

import org.joget.apps.app.service.AuthTokenService;
import org.kecak.directory.dao.ClientAppDao;
import org.kecak.directory.model.ClientApp;
import org.joget.workflow.util.WorkflowUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

public class Oauth2Handler implements AuthenticationSuccessHandler {
    private ClientAppDao clientAppDao;
    private AuthTokenService authTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        ClientApp searchParam = new ClientApp();
        searchParam.setId(request.getParameter("clientId"));
        searchParam.setClientSecret(request.getParameter("clientSecret"));
        ClientApp clientApp = clientAppDao.getClientApp(searchParam);
        if(!WorkflowUtil.isCurrentUserAnonymous() && clientApp != null) {
            URL redirectUrl = new URL(clientApp.getRedirectUrl());
//            String token = TokenAuthenticationService.addAuthentication(clientApp.getId());
            String token = authTokenService.generateToken(clientApp.getId(), null);
            response.sendRedirect(redirectUrl+"?token=" + token);
        } else {
            response.sendRedirect(request.getContextPath() + "/web/oauth2/login?login_error=1");
        }
    }

    public void setClientAppDao(ClientAppDao clientAppDao) {
        this.clientAppDao = clientAppDao;
    }

    public void setAuthTokenService(AuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }
}
