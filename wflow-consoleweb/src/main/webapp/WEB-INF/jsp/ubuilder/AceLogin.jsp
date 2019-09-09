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
        <c:if test="${userview.setting.properties.googleSignInButton == 'true'}">
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
        </c:if>
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

        <link rel="stylesheet" href="${pageContext.request.contextPath}/bower_components/bootstrap/dist/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/bower_components/font-awesome/css/font-awesome.min.css">
        ${userview.setting.theme.css}
    </head>

    <body id="login" class="login-layout <c:if test="${embed}">embeded</c:if><c:if test="${rightToLeft == 'true' || fn:startsWith(currentLocale, 'ar') == true}"> rtl</c:if>">
        <div class="main-container">
            <div class="main-content">
                <div class="row">
                    <div class="col-sm-10 col-sm-offset-1">
                        <div class="login-container">
                            <div class="center">
                                <c:choose>
                                    <c:when test="${!empty userview.setting.theme.header}">
                                        <h1><span class="white" id="name">${userview.setting.theme.header}</span></h1>
                                    </c:when>
                                    <c:otherwise>
                                        <h1>
                                            <a href="${pageContext.request.contextPath}/web/userview/${appId}/${userview.properties.id}" id="header-link"><span id="name" class="white">${userview.properties.name}</span></a>
                                        </h1>
                                        <h4 class="blue">
                                            <span id="description" class="blue">${userview.properties.description}</span>
                                        </h4>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="space-6"></div>

                            <div class="position-relative">
                                <div id="login-box" class="login-box visible widget-box no-border">
                                    <div class="widget-body">
                                        <div class="widget-main">
                                            <h4 class="header blue lighter bigger">
                                                <c:if test="${!empty userview.setting.theme.properties.loginMessage}">
                                                    ${userview.setting.theme.properties.loginMessage}
                                                </c:if>
                                                <c:if test="${empty userview.setting.theme.properties.loginMessage}">
                                                    ${userview.properties.welcomeMessage}
                                                </c:if>
                                            </h4>
                                            <c:if test="${!empty SPRING_SECURITY_LAST_EXCEPTION.message}">
                                                <h5 class="red lighter bigger">
                                                    ${SPRING_SECURITY_LAST_EXCEPTION.message}
                                                </h5>
                                            </c:if>
                                            <div class="space-6"></div>
                                            <c:if test="${!empty userview.setting.properties.loginPageTop}">
                                                ${userview.setting.properties.loginPageTop}
                                            </c:if>
                                            <form id="loginForm" name="loginForm" action="<c:url value='/j_spring_security_check'/>" method="POST">
                                                <fieldset>
                                                    <label class="block clearfix">
                                                        <span class="block input-icon input-icon-right">
                                                            <input type="text" class="form-control" placeholder="<fmt:message key="ubuilder.login.username" />" id='j_username' name='j_username' value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>'/>
                                                            <i class="ace-icon fa fa-user"></i>
                                                        </span>
                                                    </label>
                                                    <label class="block clearfix">
                                                        <span class="block input-icon input-icon-right">
                                                            <input type="password" class="form-control" placeholder="<fmt:message key="ubuilder.login.password" />" id='j_password' name='j_password'/>
                                                            <i class="ace-icon fa fa-lock"></i>
                                                        </span>
                                                    </label>

                                                    <div class="space"></div>

                                                    <div class="clearfix">
                                                        <button type="submit" class="width-35 pull-right btn btn-sm btn-primary">
                                                            <i class="ace-icon fa fa-key"></i>
                                                            <span class="bigger-110"><fmt:message key="ubuilder.login" /></span>
                                                        </button>
                                                        <label class="inline">
                                                            <%= DirectoryUtil.getLoginFormFooter() %>
                                                        </label>
                                                    </div>

                                                    <div class="space-4"></div>
                                                </fieldset>
                                            </form>

                                            <c:if test="${userview.setting.properties.googleSignInButton == 'true'}">
                                                <form id="googleForm" action="<c:url value='/j_spring_security_check'/>" method="POST">
                                                    <input type="hidden" id="auth_type" name="j_username">
                                                    <input type="hidden" id="id_token" name="j_password">
                                                </form>
                                                <div class="g-signin2" data-onsuccess="onSignIn"></div>
                                            </c:if>
                                        </div><!-- /.widget-main -->
                                    </div><!-- /.widget-body -->
                                </div><!-- /.login-box -->
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
                            </div><!-- /.position-relative -->
                        </div>
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </div><!-- /.main-content -->
        </div><!-- /.main-container -->
        <!-- inline scripts related to this page -->
        <script type="text/javascript">
            jQuery(function($) {
                $("#j_username").focus();
                var loginSkin = "${userview.setting.theme.properties.loginSkin}";
                if(loginSkin == "dark"){
                    $('body').attr('class', 'login-layout');
                    $('#name,#footerMessage').attr('class', 'white');
                    $('#description').attr('class', 'blue');
                } else if(loginSkin == "blur"){
                    $('body').attr('class', 'login-layout blur-login');
                    $('#name,#footerMessage').attr('class', 'white');
                    $('#description').attr('class', 'light-blue');

                } else {
                    $('body').attr('class', 'login-layout light-login');
                    $('#name,#footerMessage').attr('class', 'grey');
                    $('#description').attr('class', 'blue');
                }
            });
        </script>
        <%= AppUtil.getSystemAlert() %>
    </body>
</html>
