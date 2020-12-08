<%@page import="org.joget.apps.userview.model.UserviewMenu"%>
<%@page import="org.joget.apps.app.service.AppUtil"%>
<%@page import="org.springframework.util.StopWatch"%>
<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<%@ page import="org.joget.workflow.util.WorkflowUtil"%>
<%@ page import="org.joget.apps.app.service.MobileUtil"%>
<%@ page import="org.joget.apps.app.service.AppUtil"%>
<%@ page import="org.joget.apps.userview.model.Userview"%>
<%@ page import="org.joget.commons.util.SetupManager"%>
<%@ page contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<%
if (!MobileUtil.isMobileDisabled() && MobileUtil.isMobileUserAgent(request)) {
    pageContext.setAttribute("mobileUserAgent", Boolean.TRUE);
}
%>
<c:set var="mobileViewDisabled" value="${userview.setting.properties.mobileViewDisabled}"/>
<c:if test="${mobileUserAgent && !mobileViewDisabled && (empty cookie['desktopSite'].value || cookie['desktopSite'].value != 'true')}">
    <c:redirect url="/web/mobile/${appId}/${userview.properties.id}/${key}"/>
</c:if>

<%
    String rightToLeft = WorkflowUtil.getSystemSetupValue("rightToLeft");
    pageContext.setAttribute("rightToLeft", rightToLeft);

    StopWatch sw = new StopWatch(request.getRequestURI());
    sw.start("userview");
%>
<% response.setHeader("P3P", "CP=\"This is not a P3P policy\""); %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<c:set var="qs"><ui:decodeurl value="${queryString}"/></c:set>
<c:if test="${empty menuId && !empty userview.properties.homeMenuId}">
    <c:set var="homeRedirectUrl" scope="request" value="/web/"/>
    <c:if test="${embed}">
        <c:set var="homeRedirectUrl" scope="request" value="${homeRedirectUrl}embed/"/>
    </c:if>
    <c:if test="${empty key}">
        <c:set var="key" scope="request" value="<%= Userview.USERVIEW_KEY_EMPTY_VALUE %>"/>
    </c:if>
    <c:set var="homeRedirectUrl" scope="request" value="${homeRedirectUrl}userview/${appId}/${userview.properties.id}/${key}/${userview.properties.homeMenuId}"/>
    <c:redirect url="${homeRedirectUrl}?${qs}"/>
</c:if>

<c:set var="isAnonymous" value="<%= WorkflowUtil.isCurrentUserAnonymous() %>"/>
<c:if test="${((!empty userview.setting.permission && !userview.setting.permission.authorize) || empty userview.current) && isAnonymous}">
    <c:set var="redirectUrl" scope="request" value="/web/"/>
    <c:choose>
        <c:when test="${embed}">
            <c:set var="redirectUrl" scope="request" value="${redirectUrl}embed/ulogin/${appId}/${userview.properties.id}/"/>
        </c:when>
        <c:otherwise>
            <c:set var="redirectUrl" scope="request" value="${redirectUrl}ulogin/${appId}/${userview.properties.id}/"/>
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
    <c:redirect url="${redirectUrl}?${qs}"/>
</c:if>

<c:if test="${empty key}">
    <c:set var="key" scope="request" value="<%= Userview.USERVIEW_KEY_EMPTY_VALUE %>"/>
</c:if>

<c:set var="bodyId" scope="request" value=""/>
<c:choose>
    <c:when test="${!empty userview.setting.permission && !userview.setting.permission.authorize}">
        <c:set var="bodyId" scope="request" value="unauthorize"/>
    </c:when>
    <c:when test="${!empty userview.current}">
        <c:choose>
            <c:when test="${!empty userview.current.properties.customId}">
                <c:set var="bodyId" scope="request" value="${userview.current.properties.customId}"/>
            </c:when>
            <c:otherwise>
                <c:set var="bodyId" scope="request" value="${userview.current.properties.id}"/>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <c:set var="bodyId" scope="request" value="pageNotFound"/>
    </c:otherwise>
</c:choose>

<c:catch var="bodyError">
<c:set var="bodyContent">
    <c:choose>
        <c:when test="${!empty userview.current}">
            <c:set var="isQuickEditEnabled" value="<%= AppUtil.isQuickEditEnabled() %>"/>
            <c:if test="${isQuickEditEnabled}">
            <div class="quickEdit" style="display: none">
                <a href="${pageContext.request.contextPath}/web/console/app/${appId}/${appVersion}/userview/builder/${userview.properties.id}?menuId=${userview.current.properties.id}" target="_blank"><i class="icon-edit"></i> <fmt:message key="adminBar.label.page"/>: <c:out value="${userview.current.properties.label}"/></a>
            </div>
            </c:if>
            <c:set var="properties" scope="request" value="${userview.current.properties}"/>
            <c:set var="requestParameters" scope="request" value="${userview.current.requestParameters}"/>
            <c:set var="readyJspPage" value="${userview.current.readyJspPage}"/>
            <c:choose>
                <c:when test="${!empty readyJspPage}">
                    <jsp:include page="../${readyJspPage}" flush="true"/>
                </c:when>
                <c:otherwise>
                    ${userview.current.readyRenderPage}
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <h3><fmt:message key="ubuilder.pageNotFound"/></h3>

            <fmt:message key="ubuilder.pageNotFound.message"/>
            <br><br>
            <fmt:message key="ubuilder.pageNotFound.explanation"/>
            <p>&nbsp;</p>
            <p>&nbsp;</p>
            <p>
                <a href="${pageContext.request.contextPath}/web/userview/${appId}/${userview.properties.id}/<c:out value="${key}"/>/${userview.properties.homeMenuId}"><fmt:message key="ubuilder.pageNotFound.backToMain"/></a>
            </p>
        </c:otherwise>
    </c:choose>
</c:set>

<c:set var="alertTitleProperty" value="<%= UserviewMenu.ALERT_TITLE_PROPERTY %>"/>
<c:set var="alertTitleValue" value="${userview.current.properties[alertTitleProperty]}"/>
<c:set var="alertMessageProperty" value="<%= UserviewMenu.ALERT_MESSAGE_PROPERTY %>"/>
<c:set var="alertMessageValue" value="${userview.current.properties[alertMessageProperty]}"/>
<c:set var="redirectUrlProperty" value="<%= UserviewMenu.REDIRECT_URL_PROPERTY %>"/>
<c:set var="redirectUrlValue" value="${userview.current.properties[redirectUrlProperty]}"/>
<c:set var="redirectParentProperty" value="<%= UserviewMenu.REDIRECT_PARENT_PROPERTY %>"/>
<c:set var="redirectParentValue" value="${userview.current.properties[redirectParentProperty]}"/>
<c:choose>
<c:when test="${!empty alertMessageValue}">
    <div id="alertMessage" class="modal fade" role="dialog"  data-backdrop="static" data-keyboard="false">
       <div class="modal-dialog">
           <div class="modal-content">
               <div class="modal-header">
                   <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                   <c:choose>
                       <c:when test="${!empty alertTitleValue}">
                           <h4 class="modal-title">${alertTitleValue}</h4>
                       </c:when>
                       <c:otherwise>
                           <h4 class="modal-title"><c:choose><c:when test="${currentLocale == 'en_US'}">SUCCESS</c:when><c:otherwise>SUKSES</c:otherwise></c:choose></h4>
                       </c:otherwise>
                   </c:choose>
               </div>
               <div class="modal-body">
                     <p>${alertMessageValue}</p>
               </div>
               <div class="modal-footer">
                   <input type="button" id="btnClose" <c:if test="${!empty redirectUrlValue}">onclick="javascript:<c:if test="${redirectParentValue}">parent.</c:if>location.href = '${redirectUrlValue}'"</c:if> value="OK" class="btn btn-primary" data-dismiss="modal">
               </div>
           </div>
       </div>
   </div>
</c:when>
<c:when test="${!empty redirectUrlValue}">
    <c:choose>
        <c:when test="${redirectParentValue}">
            <script>
                parent.location.href = "${redirectUrlValue}";
            </script>
        </c:when>
        <c:otherwise>
            <c:if test="${!fn:containsIgnoreCase(redirectUrlValue, 'http') && !fn:startsWith(redirectUrlValue, '/')}">
                <c:set var="redirectBaseUrlValue" scope="request" value="/web/"/>
                <c:if test="${embed}">
                    <c:set var="redirectBaseUrlValue" scope="request" value="${redirectBaseUrlValue}embed/"/>
                </c:if>
                <c:set var="redirectBaseUrlValue" scope="request" value="${redirectBaseUrlValue}userview/${appId}/${userview.properties.id}/${key}/"/>
                <c:set var="redirectUrlValue" value="${redirectBaseUrlValue}${redirectUrlValue}"/>
            </c:if>
            <c:if test="${fn:startsWith(redirectUrlValue, pageContext.request.contextPath)}">
                <c:set var="redirectUrlValue" value="${fn:substring(redirectUrlValue, fn:length(pageContext.request.contextPath), fn:length(redirectUrlValue))}"/>
            </c:if>
            <c:redirect url="${redirectUrlValue}"/>
        </c:otherwise>
    </c:choose>
</c:when>
</c:choose>
</c:catch>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
        <title>
              <c:set var="html">
                  ${userview.properties.name} &nbsp;&gt;&nbsp;
                  <c:if test="${!empty userview.current}">
                      ${userview.current.properties.label}
                  </c:if>
              </c:set>
              <ui:stripTag html="${html}"/>
        </title>
		${userview.setting.theme.css}
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/form.css?build=<fmt:message key="build.number"/>" />
        <jsp:include page="/WEB-INF/jsp/includes/scripts.jsp" />
        ${oauth2LogoutScript}

        <style type="text/css">
            .quickEdit, #form-canvas .quickEdit {
                display: none;
            }
        </style>

        <script type="text/javascript">
            function userviewPrint(){
                $('head').append('<link id="userview_print_css" rel="stylesheet" href="${pageContext.request.contextPath}/css/userview_print.css" type="text/css" media="print"/>');
                $('body').addClass("userview_print");
                setTimeout("do_print()", 1000);
            }

            function do_print(){
                window.print();
                $('body').removeClass("userview_print");
                $('#userview_print_css').remove();
            }
            UI.base = "${pageContext.request.contextPath}";
            UI.userview_app_id = '${appId}';
            UI.userview_id = '${userview.properties.id}';
        </script>
          <!-- Tell the browser to be responsive to screen width -->
          <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
          <link rel="stylesheet" href="${pageContext.request.contextPath}/bower_components/bootstrap/dist/css/bootstrap.min.css">
          <!-- Ionicons -->
          <link rel="stylesheet" href="${pageContext.request.contextPath}/bower_components/Ionicons/css/ionicons.min.css">
          <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/bower_components/font-awesome/css/font-awesome.min.css">

          <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
          <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
          <!--[if lt IE 9]>
          <script src="${pageContext.request.contextPath}/js/html5shiv.min.js"></script>
          <script src="${pageContext.request.contextPath}/js/respond.min.js"></script>
          <![endif]-->

          <!-- Google Font -->
          <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600,700,300italic,400italic,600italic">
          <link href="${pageContext.request.contextPath}/css/userview.css?build=<fmt:message key="build.number"/>" rel="stylesheet" type="text/css" />
          <link rel="shortcut icon" href="${pageContext.request.contextPath}/images/favicon_uv.ico"/>
    </head>

  <body class="${userview.setting.theme.properties.skin} <c:if test="${embed}">embeded</c:if><c:if test="${rightToLeft == 'true' || fn:startsWith(currentLocale, 'ar') == true}"> rtl</c:if>">
    <div id="navbar" class="navbar navbar-default <c:if test="${userview.setting.theme.properties.fixedNavbar == 'true'}">navbar-fixed-top</c:if>">
      <div class="navbar-container ace-save-state" id="navbar-container">
        <button type="button" class="navbar-toggle menu-toggler pull-left" id="menu-toggler" data-target="#sidebar">
          <span class="sr-only">Toggle sidebar</span>

          <span class="icon-bar"></span>

          <span class="icon-bar"></span>

          <span class="icon-bar"></span>
        </button>

        <div class="navbar-header pull-left">
          <a href="${pageContext.request.contextPath}/web/userview/${appId}/${userview.properties.id}/<c:out value="${key}"/>/" class="navbar-brand">
            <small>
              ${userview.properties.name}
            </small>
          </a>
        </div>

        <div class="navbar-buttons navbar-header pull-right" role="navigation">
          <ul class="nav ace-nav">
            <li class="light-blue dropdown-modal">
              <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                <img src="${defaultAvatar}" class="nav-user-photo" alt="User Image" onerror="this.src='<c:url value="/images/default-avatar.png" />'">
                <span class="user-info">
                  <small>Welcome,</small>
                  ${user.firstName} ${user.lastName}
                </span>

                <i class="ace-icon fa fa-caret-down"></i>
              </a>

              <ul class="user-menu dropdown-menu-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">

                <li class="divider"></li>

                <li>
                  <c:choose>
                      <c:when test="${isAnonymous}">
                          <a href="${pageContext.request.contextPath}/web/ulogin/${appId}/${userview.properties.id}/<c:out value="${key}"/>" class="btn btn-default btn-flat">
                            <i class="ace-icon fa fa-sign-in"></i> <fmt:message key="ubuilder.login"/></span>
                          </a>
                      </c:when>
                      <c:otherwise>
                          <a href="${pageContext.request.contextPath}/j_spring_security_logout" class="btn btn-default btn-flat" onClick="return logout();">
                            <i class="ace-icon fa fa-power-off"></i> ${userview.properties.logoutText}
                          </a>
                      </c:otherwise>
                  </c:choose>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </div><!-- /.navbar-container -->
    </div>

    <div class="main-container <c:if test="${userview.setting.theme.properties.insideContainer == 'true'}">container</c:if>" id="main-container">
      <script type="text/javascript">
        try{ace.settings.loadState('main-container')}catch(e){}
      </script>

      <div id="sidebar" class="sidebar responsive <c:if test="${userview.setting.theme.properties.fixedSidebar == 'true'}">sidebar-fixed</c:if>">
        <script type="text/javascript">
          try{ace.settings.loadState('sidebar')}catch(e){}
        </script>
        <c:if test="${isQuickEditEnabled}">
          <div class="quickEdit" style="display: none">
              <a href="${pageContext.request.contextPath}/web/console/app/${appId}/${appVersion}/userview/builder/${userview.properties.id}" target="_blank"><i class="icon-edit"></i> <fmt:message key="adminBar.label.menu"/>: <c:out value="${userview.properties.name}"/></a>
          </div>
        </c:if>
          <ul class="nav nav-list">
            <c:forEach items="${userview.categories }" var="category" varStatus="cStatus">
              <c:set var="c_class" value="categoryMenu"/>
              <c:if test="${!empty userview.currentCategory && category.properties.id eq userview.currentCategory.properties.id}">
                  <c:set var="c_class" value="${c_class} active open"/>
              </c:if>
              
              <c:if test="${category.properties.hide != 'yes'}">
              <li class="${c_class}">
				<c:set var="firstMenuItem" value="${category.menus[0]}"/>
              	<c:choose>
					<c:when test="${!empty firstMenuItem && firstMenuItem.homePageSupported}">
						<c:set var="menuItemId" value="${firstMenuItem.properties.menuId}"/>
						<a href="${firstMenuItem.url}" class="dropdown-toggle">
							<span><ui:stripTag html="${category.properties.label}" relaxed="true"/></span>
							<b class="arrow icon-angle-down"></b>
						</a>
					</c:when>
					<c:otherwise>
						<span><ui:stripTag html="${category.properties.label}" relaxed="true"/></span>
					</c:otherwise>
				</c:choose>
                <ul class="submenu">
                	<c:forEach items="${category.menus}" var="menu" varStatus="mStatus">
                    	<c:set var="m_class" value=""/>
                        <c:if test="${!empty userview.current && menu.properties.id eq userview.current.properties.id}">
                        	<c:set var="m_class" value="${m_class} active"/>
                        </c:if>
               			<li class="${m_class}" id="${menu.properties.id}">
                            ${menu.menu}
                            <b class="arrow"></b>
                        </li>
                      </c:forEach>
                  </ul>
            </li> <!-- /.Category Menu -->
            </c:if>
            </c:forEach>
          </ul><!-- /.nav-list -->

        <div class="sidebar-toggle sidebar-collapse" id="sidebar-collapse">
          <i id="sidebar-toggle-icon" class="ace-icon fa fa-angle-double-left ace-save-state" data-icon1="ace-icon fa fa-angle-double-left" data-icon2="ace-icon fa fa-angle-double-right"></i>
        </div>
      </div>

      <div class="main-content">
        <div class="main-content-inner">
          <div class="breadcrumbs <c:if test="${userview.setting.theme.properties.fixedBreadcrumbs == 'true'}">breadcrumbs-fixed</c:if>" id="breadcrumbs">
            <ul class="breadcrumb">
              <li>
                <a href="#">${userview.currentCategory.properties.label}</a>
              </li>
              <li class="active">${userview.current.properties.label}</li>
            </ul><!-- /.breadcrumb -->
          </div>

          <div class="page-content">
            <!-- PAGE CONTENT BEGINS -->
            ${bodyContent}
            <c:if test="${!empty bodyError}"><c:out value="${bodyError}" escapeXml="true"/></c:if>
            <!-- PAGE CONTENT ENDS -->
          </div><!-- /.page-content -->
        </div>
      </div><!-- /.main-content -->

      <div class="footer">
        <div class="footer-inner">
          <div class="footer-content">
              <c:choose>
                  <c:when test="${!empty userview.setting.theme.footer}">
                    <span>${userview.setting.theme.footer}</span>
                  </c:when>
                  <c:otherwise>
                    <span><ui:stripTag html="${userview.properties.footerMessage}" relaxed="true"/></span>
                  </c:otherwise>
              </c:choose>
          </div>
        </div>
      </div>

      <a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
        <i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
      </a>
    </div><!-- /.main-container -->

    <!-- basic scripts -->
    

    <!-- REQUIRED JS SCRIPTS -->
 	${userview.setting.theme.javascript}
    <!-- Bootstrap 3.3.7 -->
    
    <script src="${pageContext.request.contextPath}/js/vueJs/vue.min.js"></script>
    <script>
      $.fn.bootstrapBtn = $.fn.button.noConflict();
      $(function(){
        $('input[type=file]').ace_file_input({
          no_file:'No File ...',
          btn_choose:'Choose',
          btn_change:'Change',
          droppable:false,
          onchange:null,
          thumbnail:false
        });
      });
    </script>
   

    <script type="text/javascript">
            HelpGuide.base = "${pageContext.request.contextPath}"
            HelpGuide.attachTo = "#header";
            HelpGuide.key = "help.web.userview.${appId}.${userview.properties.id}.${bodyId}";
            HelpGuide.show();
        </script>

        <%= AppUtil.getSystemAlert() %>

        <%
            sw.stop();
            long duration = sw.getTotalTimeMillis();
            pageContext.setAttribute("duration", duration);
        %>
        <%--div class="small">[${duration}ms]</div--%>

        <jsp:include page="/WEB-INF/jsp/console/apps/adminBar.jsp" flush="true">
            <jsp:param name="appId" value="${appId}"/>
            <jsp:param name="appVersion" value="${appVersion}"/>
            <jsp:param name="userviewId" value="${userview.properties.id}"/>
        </jsp:include>
  </body>
</html>