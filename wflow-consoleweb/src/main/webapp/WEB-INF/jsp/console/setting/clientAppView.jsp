<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>

<commons:header />

<div id="nav">
    <div id="nav-title">
        <p><i class="icon-group"></i> <fmt:message key='console.header.menu.label.clientApp'/></p>
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
        <ul id="main-action-buttons">
            <li class="btnClientAppEdit"><button onclick="onEdit()"><fmt:message key="console.setting.clientApp.edit.label"/></button></li>
            <li class="btnClientAppDelete"><button onclick="onDelete()"><fmt:message key="console.setting.clientApp.delete.label"/></button></li>
        </ul>
    </div>
    <div id="main-body">
        <fieldset class="view">
            <legend><fmt:message key="console.setting.clientApp.common.label.details"/></legend>
            <div class="form-row">
                <label for="field1"><fmt:message key="console.setting.clientApp.common.label.clientId"/></label>
                <span class="form-input"><c:out value="${clientApp.id}"/></span>
            </div>
            <div class="form-row">
                <label for="field1"><fmt:message key="console.setting.clientApp.common.label.appName"/></label>
                <span class="form-input"><c:out value="${clientApp.appName}"/></span>
            </div>
            <div class="form-row">
                <label for="field1"><fmt:message key="console.setting.clientApp.common.label.clientSecret"/></label>
                <span class="form-input"><c:out value="${clientApp.clientSecret}"/></span>
            </div>
            <div class="form-row">
                <label for="field1"><fmt:message key="console.setting.clientApp.common.label.redirectUrl"/></label>
                <span class="form-input"><c:out value="${clientApp.redirectUrl}"/></span>
            </div>
            <div class="form-row">
                <label for="field1"><fmt:message key="console.setting.clientApp.common.label.status"/></label>
                <span class="form-input">
                    <c:choose>
                        <c:when test="${clientApp.active == 1}">
                            <fmt:message key="console.setting.clientApp.common.label.status.active"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="console.setting.clientApp.common.label.status.inactive"/>
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>
        </fieldset>
    </div>
</div>

<script>
    <ui:popupdialog var="popupDialog" src="${pageContext.request.contextPath}/web/console/setting/clientApp/edit/${clientApp.id}."/>

    function onEdit(){
        popupDialog.init();
    }

    function closeDialog() {
        popupDialog.close();
    }

    function onDelete(){
         if (confirm('<fmt:message key="console.setting.clientApp.delete.label.confirmation"/>')) {
            var callback = {
                success : function() {
                    document.location = '${pageContext.request.contextPath}/web/console/setting/clientApps';
                }
            }
            var request = ConnectionManager.post('${pageContext.request.contextPath}/web/console/setting/clientApp/delete', callback, 'ids=${clientApp.id}');
        }
    }
</script>

<script>
    Template.init("#menu-users", "#nav-users-users");
</script>

<commons:footer />


