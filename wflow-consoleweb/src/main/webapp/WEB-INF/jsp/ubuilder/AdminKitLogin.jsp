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
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta name="description" content="Kecak Responsive Admin &amp; Dashboard Template based on Bootstrap 5">
		<meta name="author" content="Kecak-AdminKit">
		<meta name="keywords" content="adminkit, bootstrap, bootstrap 5, admin, dashboard, kecak, kecak-workflow">
		
		<link rel="shortcut icon" href="img/icons/icon-48x48.png" />
		
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
            ${userview.setting.theme.javascript}
            UI.base = "${pageContext.request.contextPath}";    
            UI.userview_app_id = '${appId}';
            UI.userview_id = '${userview.properties.id}';
        </script>
        
        <script type="text/javascript">
            ${userview.setting.theme.javascript}
        </script>

        <link href="${pageContext.request.contextPath}/css/admin-kit.css" rel="stylesheet" type="text/css" />

        <style type="text/css">
            ${userview.setting.theme.css}
        </style>
	</head>
	
	<body id="login" data-theme="default" data-layout="fluid" data-sidebar="left">
		<main class="d-flex w-100 h-100">
			<div class="container d-flex flex-column">
				<div class="row vh-100">
					<div class="col-sm-10 col-md-8 col-lg-6 mx-auto d-table h-100">
						<div class="d-table-cell align-middle">
	
							<div class="text-center mt-4">
								<h1 class="h2">${userview.properties.welcomeMessage}</h1>
								<p class="lead">
									Sign in to your account to continue
								</p>
							</div>
	
							<div class="card">
								<div class="card-body">
									<div class="m-sm-4">
										<c:if test="${!empty param.login_error}">
											<div class="alert alert-danger alert-dismissible" role="alert">
												<button type="button" class="close" data-dismiss="alert" aria-label="Close">
								                	<span aria-hidden="true">&times;</span>
								              	</button>
												<div class="alert-message">
													${SPRING_SECURITY_LAST_EXCEPTION.message}
												</div>
											</div>
										</c:if>
										<form id="loginForm" name="loginForm" action="<c:url value='/j_spring_security_check'/>" method="POST">
											<div class="mb-3">
												<label class="form-label"><fmt:message key="ubuilder.login.username" /></label>
												<input class="form-control form-control-lg" type="text" id='j_username' name='j_username' value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>' placeholder="Enter your username" />
											</div>
											<div class="mb-3">
												<label class="form-label"><fmt:message key="ubuilder.login.password" /></label>
												<input class="form-control form-control-lg" type="password" id='j_password' name='j_password' placeholder="Enter your password" />
												<small>
													<a href="#">Forgot password?</a>
												</small>
											</div>
											<div>
												<label class="form-check">
													<input class="form-check-input" type="checkbox" value="remember-me" name="remember-me" checked>
													<span class="form-check-label">
														Remember me next time
													</span>
												</label>
											</div>
											<div class="text-center mt-3">
												<input name="submit" class="btn btn-lg btn-primary" type="submit" value="<fmt:message key="ubuilder.login" />" />
												<!-- <button type="submit" class="btn btn-lg btn-primary">Sign in</button> -->
											</div>
										</form>
									</div>
								</div>
							</div>
	
						</div>
					</div>
				</div>
			</div>
		</main>
	</body>
</html>