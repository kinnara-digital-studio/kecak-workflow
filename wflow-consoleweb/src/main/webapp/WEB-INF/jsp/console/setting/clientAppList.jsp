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
            <li><button onclick="onCreate()"><fmt:message key="console.setting.clientApp.create.label"/></button></li>
        </ul>
    </div>
    <div id="main-body">
        <div id="main-body-content-filter">
        </div>
        <ui:jsontable url="${pageContext.request.contextPath}/web/json/setting/admin/clientApp/list?${pageContext.request.queryString}"
                       var="JsonDataTable"
                       divToUpdate="clientAppList"
                       jsonData="data"
                       rowsPerPage="10"
                       width="100%"
                       sort="app_name"
                       desc="false"
                       href="${pageContext.request.contextPath}/web/console/setting/clientApp/view"
                       hrefParam="id"
                       hrefSuffix="."
                       hrefQuery="false"
                       hrefDialog="false"
                       hrefDialogWidth="600px"
                       hrefDialogHeight="400px"
                       hrefDialogTitle="Process Dialog"
                       checkbox="true"
                       checkboxButton2="general.method.label.delete"
                       checkboxCallback2="deleteClientApp"
                       searchItems="name|Name"
                       fields="['id','appName','clientSecret','redirectUrl']"
                       column1="{key: 'id', label: 'console.setting.clientApp.common.label.clientId', sortable: true}"
                       column2="{key: 'appName', label: 'console.setting.clientApp.common.label.appName', sortable: true}"
                       column3="{key: 'clientSecret', label: 'console.setting.clientApp.common.label.clientSecret', sortable: true}"
                       column4="{key: 'redirectUrl', label: 'console.setting.clientApp.common.label.redirectUrl', sortable: true}"
                       />

    </div>
</div>

<script>
    <ui:popupdialog var="popupDialog" src="${pageContext.request.contextPath}/web/console/setting/clientApp/create"/>

    function onCreate(){
        popupDialog.init();
    }

    function closeDialog() {
        popupDialog.close();
    }

    function deleteClientApp(selectedList){
         if (confirm('<fmt:message key="console.setting.clientApp.delete.label.confirmation"/>')) {
            var callback = {
                success : function() {
                    document.location = '${pageContext.request.contextPath}/web/console/setting/clientApps';
                }
            }
            var request = ConnectionManager.post('${pageContext.request.contextPath}/web/console/setting/clientApp/delete', callback, 'ids='+selectedList);
        }
    }

    var org_filter = window.filter;
    var filter = function(jsonTable, url, value){
        url = "&orgId=" + $('#JsonDataTable_filterbyOrg').val();
        url += "&name=" + $('#JsonDataTable_searchCondition').val();
        org_filter(jsonTable, url, '');
    };
</script>

<script>
    Template.init("#menu-users", "#nav-users-users");
</script>

<commons:footer />
