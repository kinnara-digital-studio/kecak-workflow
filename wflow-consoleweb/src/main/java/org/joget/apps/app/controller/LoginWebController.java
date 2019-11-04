package org.joget.apps.app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.xpath.operations.Bool;
import org.hibernate.jdbc.Work;
import org.joget.apps.app.dao.UserviewDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.UserviewDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.userview.model.Userview;
import org.joget.apps.userview.service.UserviewService;
import org.joget.apps.userview.service.UserviewThemeProcesser;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.SecurityUtil;
import org.joget.commons.util.SetupManager;
import org.joget.commons.util.StringUtil;
import org.joget.directory.dao.ClientAppDao;
import org.joget.directory.model.ClientApp;
import org.joget.directory.model.User;
import org.joget.plugin.base.Plugin;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.kecak.oauth.model.Oauth2ClientPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LoginWebController {

    @Autowired
    private UserviewService userviewService;
    @Autowired
    AppService appService;
    @Autowired
    PluginManager pluginManager;
    @Autowired
    UserviewDefinitionDao userviewDefinitionDao;
    @Autowired
    WorkflowUserManager workflowUserManager;
    @Autowired
    ClientAppDao clientAppDao;

    @RequestMapping("/login")
    public String login(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        String savedUrl = "";
        if (savedRequest != null) {
            savedUrl = savedRequest.getRedirectUrl();
        } else if (request.getHeader("referer") != null) { //for userview logout
            savedUrl = request.getHeader("referer");
        }

        //add oauth button on login view
        Collection<Plugin> pluginList = pluginManager.list(Oauth2ClientPlugin.class);
        String oauth2PluginButton = "";
        for (Plugin plugin : pluginList){
            Oauth2ClientPlugin oauthPlugin = (Oauth2ClientPlugin) pluginManager.getPlugin(plugin.getClass().getName());
            Map<String,String> generalSetting = oauthPlugin.getGeneralSetting();
            Boolean showed = (generalSetting == null)?false:true;
            for (String setting: generalSetting.keySet()){
                if(SetupManager.getSettingValue(setting) == null || SetupManager.getSettingValue(setting).isEmpty()) showed = false;
            }
            if(showed) oauth2PluginButton += oauthPlugin.renderHtmlLoginButton();
        }

        if (savedUrl.contains("/web/userview") || savedUrl.contains("/web/embed/userview")) {
            String embedPrefix = "";
            if (savedUrl.contains("/web/userview")) {
                savedUrl = savedUrl.substring(savedUrl.indexOf("/web/userview"));
                savedUrl = savedUrl.replace("/web/userview/", "");
            } else {
                savedUrl = savedUrl.substring(savedUrl.indexOf("/web/embed/userview"));
                savedUrl = savedUrl.replace("/web/embed/userview/", "");
                embedPrefix = "embed/";
            }
            
            if (request.getParameter("embed") != null && Boolean.parseBoolean((String) request.getParameter("embed"))) {
                embedPrefix = "embed/";
            }
            
            String[] urlKey = savedUrl.split("/");
            String appId = urlKey[0];
            String userviewId = urlKey[1];

            if (savedRequest == null) { //for userview logout
                return "redirect:/web/" + embedPrefix + "userview/" + appId + "/" + userviewId;
            }
        } else if ((savedUrl.contains("/web/mobile") || savedUrl.contains("/web/embed/mobile")) && !workflowUserManager.isCurrentUserAnonymous()) {
            String embedPrefix = "";
            if (savedUrl.endsWith("/web/mobile") || savedUrl.endsWith("/web/embed/mobile")) {
                savedUrl = "";
            } else if (savedUrl.contains("/web/mobile")) {
                savedUrl = savedUrl.substring(savedUrl.indexOf("/web/mobile"));
                savedUrl = savedUrl.replace("/web/mobile/", "");
            } else {
                savedUrl = savedUrl.substring(savedUrl.indexOf("/web/embed/mobile"));
                savedUrl = savedUrl.replace("/web/embed/mobile/", "");
                embedPrefix = "embed/";
            }
            
            if (savedUrl.isEmpty() || "apps".equals(savedUrl)) {
                return "redirect:/web/mobile/apps?_=" + System.currentTimeMillis();
            }
            
            if (request.getParameter("embed") != null && Boolean.parseBoolean((String) request.getParameter("embed"))) {
                embedPrefix = "embed/";
            }
            
            String[] urlKey = savedUrl.split("/");
            String appId = urlKey[0];
            String userviewId = null;
            if (urlKey.length > 1) {
                userviewId = urlKey[1];
            }

            if (savedRequest == null) { //for userview logout
                String redirectUrl = (userviewId != null) ? "redirect:/web/" + embedPrefix + "mobile/" + appId + "/" + userviewId + "//landing?_=" + System.currentTimeMillis() : "redirect:/web/mobile/apps?_=" + System.currentTimeMillis();
                return redirectUrl;
            }
        } else if (savedUrl.contains("/web/ulogin") || savedUrl.contains("/web/embed/ulogin")) {
            Boolean embed = false;
            if (savedUrl.contains("/web/ulogin")) {
                savedUrl = savedUrl.substring(savedUrl.indexOf("/web/ulogin"));
                savedUrl = savedUrl.replace("/web/ulogin/", "");
            } else {
                savedUrl = savedUrl.substring(savedUrl.indexOf("/web/embed/ulogin"));
                savedUrl = savedUrl.replace("/web/embed/ulogin/", "");
                embed = true;
            }
            
            String[] urlKey = savedUrl.split("/");
            String appId = urlKey[0];
            String userviewId = urlKey[1];
            String key = null;
            String menuId = null;
            if (urlKey.length > 2) {
                key = urlKey[2];
                
                if (urlKey.length > 3) {
                    menuId = urlKey[3];
                }
            }

            Long appVersion = appService.getPublishedVersion(appId);
            if (appVersion == null || appVersion == 0) {
                return "error404";
            }

            // retrieve app and userview
            AppDefinition appDef = appService.getAppDefinition(appId, appVersion.toString());
            if (appDef == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
            map.addAttribute("appId", appId);
            map.addAttribute("appDefinition", appDef);
            map.addAttribute("appVersion", appDef.getVersion());
            map.addAttribute("key", key);
            map.addAttribute("menuId", menuId);
            map.addAttribute("embed", embed);
            map.addAttribute("oauth2PluginButton",oauth2PluginButton);
            UserviewDefinition userview = userviewDefinitionDao.loadById(userviewId, appDef);
            if (userview != null) {
                String json = userview.getJson();
                Userview userviewObject = userviewService.createUserview(json, null, false, request.getContextPath(), request.getParameterMap(), key, embed);
                UserviewThemeProcesser processer = new UserviewThemeProcesser(userviewObject, request);
                map.addAttribute("userview", userviewObject);
                map.addAttribute("processer", processer);
                String view = processer.getLoginView();
                if (view != null) {
                    if (view.startsWith("redirect:")) {
                        map.clear();
                    }
                    return view;
                }
            }

            return "ubuilder/login";
        } else if (savedUrl.contains("/web/mlogin") || savedUrl.contains("/web/embed/mlogin") || savedUrl.contains("/web/mobile") || savedUrl.contains("/web/embed/mobile")) {
            Boolean embed = false;
            if (savedUrl.equals("/web/mlogin") || savedUrl.equals("/web/embed/mlogin") || savedUrl.endsWith("/web/mobile") || savedUrl.endsWith("/web/embed/mobile")) {
                savedUrl = "";
            } else if (savedUrl.contains("/web/mlogin")) {
                savedUrl = savedUrl.substring(savedUrl.indexOf("/web/mlogin"));
                savedUrl = savedUrl.replace("/web/mlogin/", "");
            } else if (savedUrl.contains("/web/embed/mlogin")) {
                savedUrl = savedUrl.substring(savedUrl.indexOf("/web/embed/mlogin"));
                savedUrl = savedUrl.replace("/web/embed/mlogin/", "");
                embed = true;
            } else if (savedUrl.contains("/web/mobile")) {
                savedUrl = savedUrl.substring(savedUrl.indexOf("/web/mobile"));
                savedUrl = savedUrl.replace("/web/mobile/", "");
            } else {
                savedUrl = savedUrl.substring(savedUrl.indexOf("/web/embed/mobile"));
                savedUrl = savedUrl.replace("/web/embed/mobile/", "");
                embed = true;
            }
            
            if (savedUrl.isEmpty()) {
                return "mobile/mLogin";
            }
            
            String[] urlKey = savedUrl.split("/");
            String appId = urlKey[0];
            String userviewId = urlKey[1];
            String key = null;
            String menuId = null;
            if (urlKey.length > 2) {
                key = urlKey[2];
                
                if (urlKey.length > 3) {
                    menuId = urlKey[3];
                }
            }

            Long appVersion = appService.getPublishedVersion(appId);
            if (appVersion == null || appVersion == 0) {
                return "error404";
            }

            // retrieve app and userview
            AppDefinition appDef = appService.getAppDefinition(appId, appVersion.toString());
            if (appDef == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
            map.addAttribute("appId", appId);
            map.addAttribute("appDefinition", appDef);
            map.addAttribute("appVersion", appDef.getVersion());
            map.addAttribute("key", key);
            map.addAttribute("menuId", menuId);
            map.addAttribute("embed", embed);
            UserviewDefinition userview = userviewDefinitionDao.loadById(userviewId, appDef);
            if (userview != null) {
                String json = userview.getJson();
                map.addAttribute("userview", userviewService.createUserview(json, null, false, request.getContextPath(), request.getParameterMap(), key, embed));
            }

            return "mobile/mLogin";
        } else if (savedUrl.contains(request.getContextPath() + "/mobile")) {
            return "mobile/mLogin";
        }
        map.addAttribute("oauth2PluginButton",oauth2PluginButton);
        return "login";
    }

    @RequestMapping("/unauthorized")
    public String unauthorized(ModelMap map) {
        return "unauthorized";
    }
    
    @RequestMapping("/mlogin")
    public String mlogin(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return "mobile/mLogin";
    }

    @RequestMapping("/oauth2/login")
    public String oauth2Login(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return "oauth2/login";
    }

//    @RequestMapping("/oauth2/authorize")
//    public String oauth2Authorize(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String currentUsername = WorkflowUtil.getCurrentUsername();
//        ClientApp searchParam = new ClientApp();
////        HttpSession session = request.getSession();
////        searchParam.setClientId(session.getAttribute("clientId").toString());
////        searchParam.setClientSecret(session.getAttribute("clientSecret").toString());
//        searchParam.setClientId(request.getParameter("clientId"));
//        searchParam.setClientId(request.getParameter("clientSecret"));
//        ClientApp clientApp = clientAppDao.getClientApp(searchParam);
//        if(!WorkflowUtil.isCurrentUserAnonymous() && clientApp != null) {
//            String redirectUrl = clientApp.getRedirectUrl();
//            String token = TokenAuthenticationService.addAuthentication(response,currentUsername);
//            return "redirect:" + redirectUrl + "&token=" + token;
//        } else {
//            return "redirect:/web/oauth2/login?login_error=1";
//        }
//    }

        @RequestMapping("/browserExtension")
    public String browserExtension(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String registrationId = request.getParameter("registrationId");
        if(registrationId == null || registrationId.isEmpty()) {
            LogUtil.warn(getClass().getName(), "Parameter [registrationId] not found");
            return "login";
        }

        LogUtil.info(getClass().getName(), "current username ["+WorkflowUtil.getCurrentUsername()+"]");
        if(WorkflowUtil.getCurrentUsername() == null || WorkflowUtil.getCurrentUsername().isEmpty() || WorkflowUtil.getCurrentUsername().equals(WorkflowUserManager.ROLE_ANONYMOUS))
            return "login";


        final String masterSetupUsername = SetupManager.getSettingValue(SetupManager.MASTER_LOGIN_USERNAME);
        final String masterSetupPassword = SetupManager.getSettingValue(SetupManager.MASTER_LOGIN_PASSWORD);
        final String decryptedMasterSetupPassword = SecurityUtil.decrypt(masterSetupPassword);

        final User master = new User();
        master.setUsername(masterSetupUsername.trim());
        master.setPassword(StringUtil.md5Base16(decryptedMasterSetupPassword));

        final String loginHash = master.getLoginHash().toUpperCase();

        LogUtil.info(getClass().getName(), "RequestURI ["+request.getRequestURI()+"]");
        LogUtil.info(getClass().getName(), "ServletPath ["+request.getServletPath()+"]");
        LogUtil.info(getClass().getName(), "ContextPath ["+request.getContextPath()+"]");

        // connect to 8443?
        HttpClient client = HttpClientBuilder.create().build();
        String uri = request.getScheme() + "://"
                + request.getServerName() + ":"
                + request.getServerPort()
                + request.getServletPath()
                + "/json/plugin/com.kinnara.kecakplugins.mobileapi.LoginApi/service?loginAs="
                + WorkflowUtil.getCurrentUsername();
        LogUtil.info(getClass().getName(), "uri ["+uri+"]");
        HttpPost post = new HttpPost(uri);
        post.addHeader("Content-Type", "application/json");
        post.addHeader(SetupManager.MASTER_LOGIN_USERNAME, master.getUsername());
        post.addHeader(SetupManager.MASTER_LOGIN_PASSWORD, decryptedMasterSetupPassword);
//        post.addHeader("Authorization", "Basic YWRtaW46UEBzc3dvcmQxMjM=");
        post.addHeader("Referer", request.getScheme() + "://"
                + request.getServerName() + ":"
                + request.getServerPort()
                + request.getServletPath());

        JSONObject body = new JSONObject();
        body.put("device_id", request.getParameter("deviceId"));
        body.put("fcm_token", request.getParameter("registrationId"));
        post.setEntity(new StringEntity(body.toString()));

        try {
            HttpResponse loginResponse = client.execute(post);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(loginResponse.getEntity().getContent()))) {
                JSONObject jsonResponseBody = new JSONObject(br.lines().collect(Collectors.joining()));
                LogUtil.info(getClass().getName(), "Login response ["+loginResponse.getStatusLine().getStatusCode()+"] ["+jsonResponseBody.toString()+"]");

                String currentUsername = jsonResponseBody.optString("username");
                if(currentUsername != null && !currentUsername.isEmpty())
                    workflowUserManager.setCurrentThreadUser(jsonResponseBody.getString("username"));
            }
        } catch (IOException | UnsupportedOperationException e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
//            workflowUserManager.setCurrentThreadUser(WorkflowUtil.getCurrentUsername());
        }

        LogUtil.info(getClass().getName(), "isCurrentUserAnonymous ["+WorkflowUtil.isCurrentUserAnonymous()+"]" );
        LogUtil.info(getClass().getName(), "getCurrentThreadUser ["+workflowUserManager.getCurrentThreadUser()+"]" );
        LogUtil.info(getClass().getName(), "getCurrentUsername ["+workflowUserManager.getCurrentUsername()+"]" );

        return "browserExtension";
    }
}
