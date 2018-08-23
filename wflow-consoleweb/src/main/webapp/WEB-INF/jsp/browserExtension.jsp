<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="org.joget.workflow.util.WorkflowUtil"%>
<%@ page import="org.joget.directory.model.service.DirectoryUtil"%>
<%@ page import="org.joget.apps.app.service.MobileUtil"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta charset="utf-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1.0">

	    <title>Kecak Workflow - Login</title>

	    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/inspinia/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/inspinia/font-awesome.css" >
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/inspinia/animate.css" >
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/inspinia/style.css" >
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/inspinia/pace.css">
		<link rel="icon"  type="image/ico" href="${pageContext.request.contextPath}/favicon.ico">
		<link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico">
		<link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
	</head>
	<body class="gray-bg">
    	<div class="middle-box text-center loginscreen animated fadeInDown" style="padding-top: 10px;">
        	<div>
            	<h2>Kecak Workflow.</h2>
            	<c:if test="${!empty param.login_error}">
					<div class="alert alert-danger">
						<c:out value="${sessionScope['SPRING_SECURITY_LAST_EXCEPTION'].message}" />
					</div>
				</c:if><br/>
            	<form id="loginForm" name="loginForm" action="<c:url value='/j_spring_security_logout'/>" method="POST">
	                <button type="submit" class="btn btn-primary block full-width m-b">Logout</button>
	                <a href="${pageContext.request.contextPath}/" class="btn btn-default block full-width m-b">Home</a><br/>
            	</form>
            	<p class="m-t"><small>@Copyright Kecak Workflow 2018</small> </p>
        	</div>
    	</div>
    	<script src="${pageContext.request.contextPath}/js/jquery-1.11.1.min.js"></script>
    	<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
	</body>
</html>