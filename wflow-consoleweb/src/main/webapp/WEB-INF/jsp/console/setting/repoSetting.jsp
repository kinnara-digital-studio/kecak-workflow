<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<%@ page import="org.joget.apps.app.service.AppUtil"%>
<%@ page import="java.io.File,org.joget.commons.util.SetupManager"%>
<%@ page import="org.joget.commons.util.HostManager"%>

<c:set var="isVirtualHostEnabled" value="<%= HostManager.isVirtualHostEnabled() %>"/>

<commons:header />
<style>
    .row-content{
        display: block;
        float: none;
    }

    .form-input{
        width: 50%
    }

    .form-input input, .form-input textarea{
        width: 100%
    }

    .row-title{
        font-weight: bold;
    }
</style>
<div id="nav">
    <div id="nav-title">
        <p><i class="icon-cogs"></i> <fmt:message key='console.header.top.label.settings'/></p>
    </div>
    <div id="nav-body">
        <ul id="nav-list">
            <jsp:include page="subMenu.jsp" flush="true" />
        </ul>
    </div>
</div>

<div id="main">
    <div id="main-title"></div>
    <div id="main-action">
    </div>
    <div id="main-body">
        <div id="repoSetup">
            <form method="post" action="${pageContext.request.contextPath}/web/console/setting/repo/submit">
            <div class="main-body-content-subheader">
                <span><fmt:message key="console.setting.git.header.repoSetup"/></span>
            </div>
            <div class="main-body-row">
                <span class="row-content">
                    <div class="form-row">
                        <label for="dataFileBasePath">
                            <fmt:message key="console.setting.git.label.repoURL"/>
                            <br>
                            <fmt:message key="console.setting.git.label.repoURL.desc"/>
                        </label>
                        <span class="form-input">
                            <input id="repoURL" type="text" name="repoURL" value="<c:out value="${settingMap['repoURL']}"/>"/>
                        </span>
                    </div>
                </span>
            </div>
            <div class="main-body-row">
                <span class="row-content">
                    <div class="form-row">
                        <label for="dataFileBasePath">
                            <fmt:message key="console.setting.git.label.username"/>
                        </label>
                        <span class="form-input">
                            <input id="repoUsername" type="text" name="repoUsername" value="<c:out value="${settingMap['repoUsername']}"/>"/>
                        </span>
                    </div>
                </span>
            </div>
            <div class="main-body-row">
                <span class="row-content">
                    <div class="form-row">
                        <label for="dataFileBasePath">
                            <fmt:message key="console.setting.git.label.password"/>
                        </label>
                        <span class="form-input">
                            <input id="repoPassword" type="password" name="repoPassword" value="<c:out value="${settingMap['repoPassword']}"/>"/>
                        </span>
                    </div>
                </span>
            </div>
            <div class="form-buttons">
                <input class="form-button" type="submit" value="<fmt:message key="general.method.label.submit"/>" />
            </div>
            </form>
        </div>
    </div>
</div>

<script>
    Template.init("", "#nav-setting-repo");
</script>

<commons:footer />
