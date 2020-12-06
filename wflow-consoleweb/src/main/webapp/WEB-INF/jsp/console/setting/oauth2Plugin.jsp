<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<%@ page import="org.joget.workflow.util.WorkflowUtil,org.joget.commons.util.HostManager"%>

<c:set var="isVirtualHostEnabled" value="<%= HostManager.isVirtualHostEnabled() %>"/>

<commons:header />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/blockui/jquery.blockUI.js"></script>

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
    <div id="main-body">
        <ui:jsontable url="${pageContext.request.contextPath}/web/json/plugin/list?className=org.kecak.oauth.model.Oauth2ClientPlugin"
                       var="JsonDataTable"
                       divToUpdate="pluginList"
                       jsonData="data"
                       rowsPerPage="10"
                       width="100%"
                       sort="name"
                       desc="false"
                       href="${pageContext.request.contextPath}/web/console/setting/oauth2plugin/config"
                       hrefParam="id"
                       hrefQuery="true"
                       hrefDialog="true"
                       hrefDialogWidth="600px"
                       hrefDialogHeight="400px"
                       hrefDialogTitle="Process Dialog"
                       checkbox="false"
                       fields="['id','name','description','version']"
                       column1="{key: 'name', label: 'console.plugin.label.name', sortable: false}"
                       column2="{key: 'description', label: 'console.plugin.label.description', sortable: false}"
                       column3="{key: 'version', label: 'console.plugin.label.version', sortable: false}"
                       column4="{key: 'id', label: 'console.plugin.label.version', sortable: false}"
                       />
    </div>
</div>

<script>
    $(document).ready(function(){
        $('#JsonDataTable_searchTerm').hide();

        <c:if test="${isVirtualHostEnabled}">
            $('#JsonDataTable_pluginList-buttons button').hide();
            $('#JsonDataTable_pluginList-buttons button:eq(0)').show();
        </c:if>
    });

    function closeDialog() {
        popupDialog.close();
    }
</script>

<script>
    Template.init("", "#nav-setting-oauth2plugin");
</script>

<commons:footer />
