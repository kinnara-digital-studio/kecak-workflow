<%@page import="org.joget.commons.util.HostManager"%>
<%@include file="/WEB-INF/jsp/includes/taglibs.jsp" %>

<c:set var="isVirtualHostEnabled" value="<%= HostManager.isVirtualHostEnabled() %>"/>
<li id="nav-setting-general"><a class="nav-link" href="${pageContext.request.contextPath}/web/console/setting/general"><span class="nav-steps">&nbsp;</span><fmt:message key="console.header.submenu.label.setting.general"/></a></li>
<c:if test="${!isVirtualHostEnabled}"><li id="nav-setting-datasource"><a class="nav-link" href="${pageContext.request.contextPath}/web/console/setting/datasource"><span class="nav-steps">&nbsp;</span><fmt:message key="console.header.submenu.label.setting.datasource"/></a></li></c:if>
<li id="nav-setting-directory"><a class="nav-link" href="${pageContext.request.contextPath}/web/console/setting/directory"><span class="nav-steps">&nbsp;</span><fmt:message key="console.header.submenu.label.setting.directory"/></a></li>
<li id="nav-setting-plugin"><a class="nav-link" href="${pageContext.request.contextPath}/web/console/setting/plugin"><span class="nav-steps">&nbsp;</span><fmt:message key="console.header.submenu.label.setting.plugin"/></a></li>
<li id="nav-setting-message"><a class="nav-link" href="${pageContext.request.contextPath}/web/console/setting/message"><span class="nav-steps">&nbsp;</span><fmt:message key="console.header.submenu.label.setting.message"/></a></li>
<li id="nav-setting-property"><a class="nav-link" href="${pageContext.request.contextPath}/web/console/setting/property"><span class="nav-steps">&nbsp;</span><fmt:message key="console.header.submenu.label.setting.property"/></a></li>
<li id="nav-setting-schedulerContent"><a class="nav-link" href="${pageContext.request.contextPath}/web/console/setting/scheduler"><span class="nav-steps">&nbsp;</span><fmt:message key="console.header.submenu.label.setting.scheduler"/></a></li>
<li id="nav-setting-eaSettings"><a class="nav-link" href="${pageContext.request.contextPath}/web/console/setting/eaSettings"><span class="nav-steps">&nbsp;</span><fmt:message key="console.header.submenu.label.setting.emailAppr"/></a></li>
<li id="nav-setting-clientApp"><a class="nav-link" href="${pageContext.request.contextPath}/web/console/setting/clientApps"><span class="nav-steps">&nbsp;</span><fmt:message key="console.header.submenu.label.setting.clientApp"/></a></li>
<%-- Aristo : preparation for next release --%>
<%-- <li id="nav-setting-repo"><a class="nav-link" href="${pageContext.request.contextPath}/web/console/setting/repo"><span class="nav-steps">&nbsp;</span><fmt:message key="console.header.submenu.label.setting.repo"/></a></li> --%>
