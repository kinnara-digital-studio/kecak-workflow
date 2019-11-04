<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<%@ page import="org.joget.workflow.util.WorkflowUtil"%>
<%@ page import="org.joget.apps.app.service.AppUtil"%>
<%@ page import="org.joget.apps.userview.model.Userview"%>
<%@ page import="org.joget.directory.model.service.DirectoryUtil"%>
<%@page contentType="text/html" pageEncoding="windows-1252"%>
<%@ page import="org.joget.commons.util.SetupManager"%>

<%
    String rightToLeft = WorkflowUtil.getSystemSetupValue("rightToLeft");
    pageContext.setAttribute("rightToLeft", rightToLeft);
%>
<% response.setHeader("P3P", "CP=\"This is not a P3P policy\""); %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<c:set var="isAnonymous" value="<%= WorkflowUtil.isCurrentUserAnonymous() %>"/>
<c:if test="${!isAnonymous}">
    <c:set var="redirectUrl" scope="request" value="/web/"/>
    <c:choose>
        <c:when test="${embed}">
            <c:set var="redirectUrl" scope="request" value="${redirectUrl}embed/userview/${appId}/${userview.properties.id}/"/>
        </c:when>
        <c:otherwise>
            <c:set var="redirectUrl" scope="request" value="${redirectUrl}userview/${appId}/${userview.properties.id}/"/>
        </c:otherwise>
    </c:choose>
    <c:choose>
        <c:when test="${!empty key}">
            <c:set var="redirectUrl" scope="request" value="${redirectUrl}${key}"/>
        </c:when>
        <c:otherwise>
            <c:set var="key" scope="request" value="<%= Userview.USERVIEW_KEY_EMPTY_VALUE %>"/>
            <c:if test="${!empty menuId}">
                <c:set var="redirectUrl" scope="request" value="${redirectUrl}${key}"/>
            </c:if>
        </c:otherwise>
    </c:choose>
    <c:if test="${!empty menuId}">
        <c:set var="redirectUrl" scope="request" value="${redirectUrl}/${menuId}"/>
    </c:if>
    <c:set var="qs"><ui:decodeurl value="${queryString}"/></c:set>
    <c:redirect url="${redirectUrl}?${qs}"/>
</c:if>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
            <meta name="google-signin-client_id" content="${SetupManager.getSettingValue('googleClientId')}">
            <script src="https://apis.google.com/js/platform.js?onload=onLoad" async defer></script>
            <script type="text/javascript">
                function onSignIn(googleUser) {
                    var id_token = googleUser.getAuthResponse().id_token;
                    $('#googleForm #auth_type').val('GOOGLE_AUTH');
                    $('#googleForm #id_token').val(id_token);
                    $('#googleForm').submit();
                }
                function onLoad() {
                    <c:if test="${!empty param.login_error}">
                      gapi.load('auth2', function() {
                        gapi.auth2.init().then(function(){
                            gapi.auth2.getAuthInstance().signOut();
                        });
                      });
                    </c:if>
                }
            </script>
        <title>
            <c:set var="html">
                ${userview.properties.name} &nbsp;&gt;&nbsp;
                <c:if test="${!empty userview.current}">
                    ${userview.current.properties.label}
                </c:if>
            </c:set>
            <ui:stripTag html="${html}"/>
        </title>

        <jsp:include page="/WEB-INF/jsp/includes/scripts.jsp" />
        <script type="text/javascript">
            UI.base = "${pageContext.request.contextPath}";
            UI.userview_app_id = '${appId}';
            UI.userview_id = '${userview.properties.id}';
        </script>
        ${userview.setting.theme.javascript}

        <!-- Tell the browser to be responsive to screen width -->
          <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
          <link rel="stylesheet" href="${pageContext.request.contextPath}/bower_components/bootstrap/dist/css/bootstrap.min.css">
          <!-- Ionicons -->
          <link rel="stylesheet" href="${pageContext.request.contextPath}/bower_components/Ionicons/css/ionicons.min.css">
          <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/bower_components//font-awesome/css/font-awesome.min.css">
          <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
          ${userview.setting.theme.css}
    </head>

    <body id="login" class="hold-transition login-page <c:if test="${embed}">embeded</c:if><c:if test="${rightToLeft == 'true' || fn:startsWith(currentLocale, 'ar') == true}"> rtl</c:if>">
        <div class="login-box">
          <div class="login-logo">
            <a href="${pageContext.request.contextPath}/web/userview/${appId}/${userview.properties.id}"><b>
            <c:choose>
                <c:when test="${!empty userview.setting.theme.header}">
                    ${userview.setting.theme.header}
                </c:when>
                <c:otherwise>
                    ${userview.properties.name}
                </c:otherwise>
            </c:choose>
            </b></a>
          </div>
          <!-- /.login-logo -->
          <div class="login-box-body">
            <p class="login-box-msg">
                <c:if test="${!empty userview.setting.theme.properties.loginMessage}">
                    ${userview.setting.theme.properties.loginMessage}
                </c:if>
                <c:if test="${empty userview.setting.theme.properties.loginMessage}">
                    ${userview.properties.welcomeMessage}
                </c:if>
            </p>
            <c:if test="${!empty SPRING_SECURITY_LAST_EXCEPTION.message}">
                ${SPRING_SECURITY_LAST_EXCEPTION.message}
            </c:if>
            <form id="loginForm" name="loginForm" action="<c:url value='/j_spring_security_check'/>" method="post">
              <div class="form-group has-feedback">
                <input type="text" class="form-control" placeholder="<fmt:message key="ubuilder.login.username" />" id='j_username' name='j_username' value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>'>
                <span class="glyphicon glyphicon-user form-control-feedback"></span>
              </div>
              <div class="form-group has-feedback">
                <input type="password" class="form-control" placeholder="<fmt:message key="ubuilder.login.password" />" id='j_password' name='j_password'>
                <span class="glyphicon glyphicon-lock form-control-feedback"></span>
              </div>
              <div class="row">
                <div class="col-xs-12">
                  <button type="submit" class="btn btn-primary btn-block btn-flat"><fmt:message key="ubuilder.login" /></button>
                </div>
                <!-- /.col -->
              </div>
            </form>
                <br>
                <div class="oauth2_login_container">
                    ${oauth2PluginButton}
                </div>
          </div>
          <!-- /.login-box-body -->
          <div class="center" style="margin-top:10px;">
              <c:choose>
                  <c:when test="${!empty userview.setting.theme.footer}">
                      <div id="footerMessage">${userview.setting.theme.footer}</div>
                  </c:when>
                  <c:otherwise>
                      <div id="footer-message">
                          <span id="footerMessage">${userview.properties.footerMessage}</span>
                      </div>
                  </c:otherwise>
              </c:choose>
          </div>
          <c:if test="${!empty userview.setting.properties.loginPageBottom}">
              ${userview.setting.properties.loginPageBottom}
          </c:if>
        </div>
    </body>
</html>
