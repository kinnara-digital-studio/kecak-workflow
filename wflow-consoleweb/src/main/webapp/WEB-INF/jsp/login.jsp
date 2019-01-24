<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="org.joget.workflow.util.WorkflowUtil"%>
<%@ page import="org.joget.directory.model.service.DirectoryUtil"%>
<%@ page import="org.joget.apps.app.service.MobileUtil"%>

<c:set var="isAnonymous" value="<%= WorkflowUtil.isCurrentUserAnonymous() %>"/>
<c:if test="${!isAnonymous}">
    <c:redirect url="/web/console/home"/>
</c:if>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta charset="utf-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	
	    <title>TAP PT.TIMAH - Login</title>
	
	    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/inspinia/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/inspinia/font-awesome.css" >
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/inspinia/animate.css" >
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/inspinia/style.css" >
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/inspinia/pace.css">
		<link rel="icon"  type="image/ico" href="${pageContext.request.contextPath}/favicon.ico">
		<link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico">
		<link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
	</head>
	<!--<body class="gray-bg">-->
	<body style="color:#fff;">
    	<div class="middle-box text-center loginscreen animated fadeInDown" style="padding-top: 100px;">
		<!--<div>
		<img style="width:100%;"  src="${pageContext.request.contextPath}/plugin/com.kinnara.pttimah.theme.CorporateBlueTheme/img/logo_good.png" title="Logo"></img>
		</div>
        	<div>
            	<h2>TinsPay</h2>-->
		<div> <img style="width:100%;"  src="${pageContext.request.contextPath}/plugin/com.kinnara.pttimah.theme.CorporateBlueTheme/img/logo_good_II.png" title="Logo"> </div>
            	<c:if test="${!empty param.login_error}">
					<div class="alert alert-danger">
						<c:out value="${sessionScope['SPRING_SECURITY_LAST_EXCEPTION'].message}" />
					</div>
				</c:if><br/>
            	<form id="loginForm" name="loginForm" action="<c:url value='/j_spring_security_check'/>" method="POST">
	                <div class="form-group">
	                    <input type="text" class="form-control input-lg" style="color:#000;" id="j_username" name="j_username" placeholder="Username" required />
	                </div>
	                <div class="form-group">
	                    <input class="form-control input-lg" style="color:#000;"  placeholder="Password" id="j_password" name="j_password" type="password" required />
	                </div>
	                <button type="submit" class="btn btn-primary block full-width m-b" style="background-color:#428bca;">Login</button>
	                <a href="${pageContext.request.contextPath}/" class="btn btn-default block full-width m-b">Home</a><br/>
	                
            	</form>
            	<p class="m-t"><small>@Copyright TAP PT. TIMAH 2018</small> </p>
        	</div>
    	</div>
    	<script src="${pageContext.request.contextPath}/js/jquery-1.11.1.min.js"></script>
    	<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
	</body>
</html>

      



