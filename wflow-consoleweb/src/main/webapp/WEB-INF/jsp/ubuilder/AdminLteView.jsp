<%@page import="org.joget.apps.userview.model.UserviewMenu"%>
<%@page import="org.joget.apps.app.service.AppUtil"%>
<%@page import="org.springframework.util.StopWatch"%>
<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<%@ page import="org.joget.workflow.util.WorkflowUtil"%>
<%@ page import="org.joget.apps.app.service.MobileUtil"%>
<%@ page import="org.joget.apps.app.service.AppUtil"%>
<%@ page import="org.joget.apps.userview.model.Userview"%>
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

        <jsp:include page="/WEB-INF/jsp/includes/scripts.jsp" />

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
          <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/bower_components//font-awesome/css/font-awesome.min.css">

          <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
          <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
          <!--[if lt IE 9]>
          <script src="${pageContext.request.contextPath}/js/html5shiv.min.js"></script>
          <script src="${pageContext.request.contextPath}/js/respond.min.js"></script>
          <![endif]-->

          <!-- Google Font -->
          <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
          <link href="${pageContext.request.contextPath}/css/userview.css?build=<fmt:message key="build.number"/>" rel="stylesheet" type="text/css" />
          <link rel="shortcut icon" href="${pageContext.request.contextPath}/images/favicon_uv.ico"/>
    </head>
<body id="${bodyId}" class="hold-transition ${userview.setting.theme.properties.skin}  ${userview.setting.theme.properties.layout} <c:if test="${embed}">embeded</c:if><c:if test="${rightToLeft == 'true' || fn:startsWith(currentLocale, 'ar') == true}"> rtl</c:if>">
<div class="wrapper">
  <!-- Main Header -->
  <header class="main-header">
    <!-- Logo -->
    <a href="${pageContext.request.contextPath}/web/userview/${appId}/${userview.properties.id}/<c:out value="${key}"/>/" class="logo">
      <!-- mini logo for sidebar mini 50x50 pixels -->
      <span class="logo-mini">${fn:substring(userview.properties.name,0,3)}</span>
      <!-- logo for regular state and mobile devices -->
      <span class="logo-lg">${userview.properties.name}</span>
    </a>

    <!-- Header Navbar -->
    <nav class="navbar navbar-static-top" role="navigation">
      <!-- Sidebar toggle button-->
      <a href="#" class="sidebar-toggle" data-toggle="push-menu" role="button">
        <span class="sr-only">Toggle navigation</span>
      </a>
      <!-- Navbar Right Menu -->
      <div class="navbar-custom-menu">
        <ul class="nav navbar-nav">
          <!-- User Account Menu -->
          <li class="dropdown user user-menu">
            <!-- Menu Toggle Button -->
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
              <!-- The user image in the navbar-->
              <c:choose>
                  <c:when test="${!empty profilePicture}">
                        <c:set var="defaultAvatar" value="data:image/jpeg;base64,${profilePicture}" />
                  </c:when>
                  <c:otherwise>
                        <c:set var="defaultAvatar"><c:url value="/images/default-avatar.png" /></c:set>
                  </c:otherwise>
              </c:choose>
              <img src="${defaultAvatar}" class="user-image" alt="User Image" onerror="this.src='<c:url value="/images/default-avatar.png" />'">
              <!-- hidden-xs hides the username on small devices so only the image appears. -->
              <span class="hidden-xs">${user.firstName}</span>
            </a>
            <ul class="dropdown-menu">
              <!-- The user image in the menu -->
              <li class="user-header">
                <img src="${defaultAvatar}" class="img-circle" alt="User Image" onerror="this.src='<c:url value="/images/default-avatar.png" />'">
                <p>
                  ${user.firstName} ${user.lastName}
                </p>
              </li>
              <li class="user-footer">
                <div class="pull-right">

                  <c:choose>
                      <c:when test="${isAnonymous}">
                          <a href="${pageContext.request.contextPath}/web/ulogin/${appId}/${userview.properties.id}/<c:out value="${key}"/>" class="btn btn-default btn-flat">
                            <fmt:message key="ubuilder.login"/>
                          </a>
                      </c:when>
                      <c:otherwise>
                          <a href="${pageContext.request.contextPath}/j_spring_security_logout" class="btn btn-default btn-flat">
                            ${userview.properties.logoutText}
                          </a>
                      </c:otherwise>
                  </c:choose>
                </div>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </nav>
  </header>
  <!-- Left side column. contains the logo and sidebar -->
  <aside class="main-sidebar">

    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">

      <!-- Sidebar user panel (optional) -->
      <div class="user-panel">
        <div class="pull-left image">
          <img src="${defaultAvatar}" class="img-circle" alt="User Image" onerror="this.src='<c:url value="/images/default-avatar.png" />'">
        </div>
        <div class="pull-left info">
          <p>${user.firstName} ${user.lastName}</p>
        </div>
      </div>

      <!-- search form (Optional) -->
      <!-- <form action="#" method="get" class="sidebar-form">
        <div class="input-group">
          <input type="text" name="q" class="form-control" placeholder="Search...">
          <span class="input-group-btn">
              <button type="submit" name="search" id="search-btn" class="btn btn-flat"><i class="fa fa-search"></i>
              </button>
            </span>
        </div>
      </form> -->
      <!-- /.search form -->

      <!-- Sidebar Menu -->
        <c:if test="${isQuickEditEnabled}">
        <div class="quickEdit" style="display: none">
            <a href="${pageContext.request.contextPath}/web/console/app/${appId}/${appVersion}/userview/builder/${userview.properties.id}" target="_blank"><i class="icon-edit"></i> <fmt:message key="adminBar.label.menu"/>: <c:out value="${userview.properties.name}"/></a>
        </div>
        </c:if>
        <div id="${category.properties.id}" class="category ${c_class}">
            <div class="category-label">
                <c:set var="firstMenuItem" value="${category.menus[0]}"/>
                <c:choose>
                    <c:when test="${!empty firstMenuItem && firstMenuItem.homePageSupported}">
                        <c:set var="menuItemId" value="${firstMenuItem.properties.menuId}"/>
                        <a href="${firstMenuItem.url}"><span><ui:stripTag html="${category.properties.label}" relaxed="true"/></span></a>
                    </c:when>
                    <c:otherwise>
                        <span><ui:stripTag html="${category.properties.label}" relaxed="true"/></span>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="clear"></div>
            <div class="menu-container">
                <c:forEach items="${category.menus}" var="menu" varStatus="mStatus">
                    <c:set var="m_class" value=""/>

                    <c:if test="${mStatus.first}">
                        <c:set var="m_class" value="${m_class} first"/>
                    </c:if>
                    <c:if test="${mStatus.last}">
                        <c:set var="m_class" value="${m_class} last"/>
                    </c:if>
                    <c:if test="${!empty userview.current && menu.properties.id eq userview.current.properties.id}">
                        <c:set var="m_class" value="${m_class} current"/>
                    </c:if>

                    <div id="${menu.properties.id}" class="menu ${m_class}">
                        ${menu.menu}
                    </div>
                </c:forEach>
            </div>
        </div>
      <c:if test="${category.properties.hide ne 'yes'}">
          <ul class="sidebar-menu" data-widget="tree">
          <c:forEach items="${userview.categories }" var="category" varStatus="cStatus">
            <c:set var="c_class" value=""/>
            <c:if test="${!empty userview.currentCategory && category.properties.id eq userview.currentCategory.properties.id}">
                <c:set var="c_class" value="${c_class} active"/>
            </c:if>

            <c:set var="firstMenuItem" value="${category.menus[0]}"/>
            <c:if test="${!empty firstMenuItem && firstMenuItem.homePageSupported}">
                <c:set var="menuItemId" value="${firstMenuItem.properties.menuId}"/>
            </c:if>
            <c:if test="${userview.setting.theme.properties.useDropdown != 'true'}">
                <li class="header"><ui:stripTag html="${category.properties.label}" relaxed="true"/></li>
                <c:forEach items="${category.menus}" var="menu" varStatus="mStatus">
                    <c:set var="m_class" value=""/>
                    <c:if test="${!empty userview.current && menu.properties.id eq userview.current.properties.id}">
                        <c:set var="m_class" value="${m_class} active"/>
                    </c:if>

                    <li class="${m_class}" id="${menu.properties.id}">
                        ${menu.menu}
                    </li>
                </c:forEach>
            </c:if>
            <c:if test="${userview.setting.theme.properties.useDropdown == 'true'}">
                <li class="treeview">
                  <a href="#"><span><ui:stripTag html="${category.properties.label}" relaxed="true"/></span>
                    <span class="pull-right-container">
                        <i class="fa fa-angle-left pull-right"></i>
                      </span>
                  </a>
                  <ul class="treeview-menu">
                      <c:forEach items="${category.menus}" var="menu" varStatus="mStatus">
                          <c:set var="m_class" value=""/>
                          <c:if test="${!empty userview.current && menu.properties.id eq userview.current.properties.id}">
                              <c:set var="m_class" value="${m_class} active"/>
                          </c:if>
                          <li class="${m_class}" id="${menu.properties.id}">
                              ${menu.menu}
                          </li>
                      </c:forEach>
                  </ul>
                </li>
            </c:if>
          </c:forEach>
          </ul>
      </c:if>
      <!-- /.sidebar-menu -->
    </section>
    <!-- /.sidebar -->
  </aside>

  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper">
    <c:if test="${!empty userview.setting.theme.beforeContent}">
        ${userview.setting.theme.beforeContent}
    </c:if>
    ${bodyContent}
    <c:if test="${!empty bodyError}"><c:out value="${bodyError}" escapeXml="true"/></c:if>
    <c:if test="${!empty userview.setting.theme.pageBottom}">
        ${userview.setting.theme.pageBottom}
    </c:if>
  </div>
  <!-- /.content-wrapper -->

  <c:if test="${!empty userview.setting.theme.footer}">
      <!-- Main Footer -->
      <footer class="main-footer">
            ${userview.setting.theme.footer}
      </footer>
  </c:if>

  <!-- Control Sidebar -->
  <aside class="control-sidebar control-sidebar-dark">
    <!-- Create the tabs -->
    <ul class="nav nav-tabs nav-justified control-sidebar-tabs">
      <li class="active"><a href="#control-sidebar-home-tab" data-toggle="tab"><i class="fa fa-home"></i></a></li>
      <li><a href="#control-sidebar-settings-tab" data-toggle="tab"><i class="fa fa-gears"></i></a></li>
    </ul>
    <!-- Tab panes -->
    <div class="tab-content">
      <!-- Home tab content -->
      <div class="tab-pane active" id="control-sidebar-home-tab">
        <h3 class="control-sidebar-heading">Recent Activity</h3>
        <ul class="control-sidebar-menu">
          <li>
            <a href="javascript:;">
              <i class="menu-icon fa fa-birthday-cake bg-red"></i>

              <div class="menu-info">
                <h4 class="control-sidebar-subheading">Langdon's Birthday</h4>

                <p>Will be 23 on April 24th</p>
              </div>
            </a>
          </li>
        </ul>
        <!-- /.control-sidebar-menu -->

        <h3 class="control-sidebar-heading">Tasks Progress</h3>
        <ul class="control-sidebar-menu">
          <li>
            <a href="javascript:;">
              <h4 class="control-sidebar-subheading">
                Custom Template Design
                <span class="pull-right-container">
                    <span class="label label-danger pull-right">70%</span>
                  </span>
              </h4>

              <div class="progress progress-xxs">
                <div class="progress-bar progress-bar-danger" style="width: 70%"></div>
              </div>
            </a>
          </li>
        </ul>
        <!-- /.control-sidebar-menu -->

      </div>
      <!-- /.tab-pane -->
      <!-- Stats tab content -->
      <div class="tab-pane" id="control-sidebar-stats-tab">Stats Tab Content</div>
      <!-- /.tab-pane -->
      <!-- Settings tab content -->
      <div class="tab-pane" id="control-sidebar-settings-tab">
        <form method="post">
          <h3 class="control-sidebar-heading">General Settings</h3>

          <div class="form-group">
            <label class="control-sidebar-subheading">
              Report panel usage
              <input type="checkbox" class="pull-right" checked>
            </label>

            <p>
              Some information about this general settings option
            </p>
          </div>
          <!-- /.form-group -->
        </form>
      </div>
      <!-- /.tab-pane -->
    </div>
  </aside>
  <!-- /.control-sidebar -->
  <!-- Add the sidebar's background. This div must be placed
  immediately after the control sidebar -->
  <div class="control-sidebar-bg"></div>
</div>
<!-- ./wrapper -->
${userview.setting.theme.css}

<!-- REQUIRED JS SCRIPTS -->

<!-- Bootstrap 3.3.7 -->
<script src="${pageContext.request.contextPath}/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<script>
  $.fn.bootstrapBtn = $.fn.button.noConflict();
</script>
${userview.setting.theme.javascript}
<script>
  $(document).ready(function () {
    $('.sidebar-menu').tree()
  })
</script>
<script type="text/javascript">
    HelpGuide.base = "${pageContext.request.contextPath}"
    HelpGuide.attachTo = "#header";
    HelpGuide.key = "help.web.userview.${appId}.${userview.properties.id}.${bodyId}";
    HelpGuide.show();
    <c:if test="${!empty alertMessageValue}">
       $(function(){
           $('.runProcess-body-content').hide();
           $('#alertMessage').modal('show');
           $('#alertMessage').on('hidden.bs.modal', function () {
                $('.runProcess-body-content').show();
           })
       });
   </c:if>
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