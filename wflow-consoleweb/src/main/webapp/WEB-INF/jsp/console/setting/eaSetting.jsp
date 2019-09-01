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
        <div id="generalSetup">
            <form method="post" action="${pageContext.request.contextPath}/web/console/setting/eaSettings/submit">
            <div class="main-body-content-subheader">
                <span><fmt:message key="console.setting.general.header.eaSettings"/></span>
            </div>
            <div class="main-body-row">
                <span class="row-content">
                    <div class="form-row">
                        <label for="dataFileBasePath"><fmt:message key="console.setting.general.label.emailAccount"/></label>
                        <span class="form-input">
                            <input id="emailAccount" type="text" name="emailAccount" value="<c:out value="${settingMap['emailAccount']}"/>"/>
                        </span>
                    </div>
                </span>
            </div>
            <div class="main-body-row">
                <span class="row-content">
                    <div class="form-row">
                        <label for="dataFileBasePath"><fmt:message key="console.setting.general.label.emailPassword"/></label>
                        <span class="form-input">
                            <input id="emailPassword" type="password" name="emailPassword" value="<c:out value="${settingMap['emailPassword']}"/>"/>
                        </span>
                    </div>
                </span>
            </div>
            <div class="main-body-row">
                <span class="row-content">
                    <div class="form-row">
                        <label for="dataFileBasePath"><fmt:message key="console.setting.general.label.emailProtocol"/></label>
                        <span class="form-input">
                            <input id="emailProtocol" type="text" name="emailProtocol" value="<c:out value="${settingMap['emailProtocol']}"/>"/>
                        </span>
                    </div>
                </span>
            </div>
            <div class="main-body-row">
                <span class="row-content">
                    <div class="form-row">
                        <label for="dataFileBasePath"><fmt:message key="console.setting.general.label.emailHost"/></label>
                        <span class="form-input">
                            <input id="emailHost" type="text" name="emailHost" value="<c:out value="${settingMap['emailHost']}"/>"/>
                        </span>
                    </div>
                </span>
            </div>
            <div class="main-body-row">
                <span class="row-content">
                    <div class="form-row">
                        <label for="dataFileBasePath"><fmt:message key="console.setting.general.label.emailPort"/></label>
                        <span class="form-input">
                            <input id="emailPort" type="text" name="emailPort" value="<c:out value="${settingMap['emailPort']}"/>"/>
                        </span>
                    </div>
                </span>
            </div>
            <div class="main-body-row">
                <span class="row-content">
                    <div class="form-row">
                        <label for="dataFileBasePath"><fmt:message key="console.setting.general.label.emailFolder"/></label>
                        <span class="form-input">
                            <input id="emailFolder" type="text" name="emailFolder" value="<c:out value="${settingMap['emailFolder']}"/>"/>
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
    Template.init("", "#nav-setting-eaSettings");
</script>

<commons:footer />
