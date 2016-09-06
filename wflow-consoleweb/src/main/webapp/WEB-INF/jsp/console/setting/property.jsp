<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp"%>
<%@ page import="org.joget.workflow.util.WorkflowUtil,org.joget.commons.util.HostManager"%>

<c:set var="isVirtualHostEnabled" value="<%=HostManager.isVirtualHostEnabled()%>" />

<commons:header />

<div id="nav">
	<div id="nav-title">
		<p>
			<i class="icon-cogs"></i>
			<fmt:message key='console.header.top.label.settings' />
		</p>
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
			<li><button onclick="onCreate()">
					<fmt:message key="console.setting.property.create.label" />
				</button></li>
		</ul>
	</div>
	<div id="main-body">
		<ui:jsontable
			url="${pageContext.request.contextPath}/web/json/console/setting/property/list?${pageContext.request.queryString}"
			var="JsonDataTable" 
			divToUpdate="propertyContentList" 
			jsonData="data"
			rowsPerPage="10" 
			width="100%" 
			sort="propertyLabel" 
			desc="false"
			href="${pageContext.request.contextPath}/web/console/setting/property/edit"
			hrefParam="id" 
			hrefQuery="false" 
			hrefDialog="true" 
			hrefDialogTitle=""
			checkbox="true"
			checkboxButton2="general.method.label.delete"
			checkboxCallback2="propertyDetailsDelete"
			searchItems="propertyLabel|property Label"
			fields="['id','propertyLabel','propertyValue']"
			column1="{key: 'propertyLabel', label: 'console.setting.property.common.label.propertyLabel', sortable: true}"
			column2="{key: 'propertyValue', label: 'console.setting.property.common.label.propertyValue', sortable: false}" 
			/>
	</div>
</div>

<script>
    $(document).ready(function(){
        $('#JsonDataTable_searchTerm').hide();

    });

    <ui:popupdialog var="popupDialog" src="${pageContext.request.contextPath}/web/console/setting/property/create"/>

    function onCreate(){
        popupDialog.init();
    }

    function closeDialog() {
        popupDialog.close();
    }

    
	function propertyDetailsDelete(selectedList) {
		if (confirm('<fmt:message key="console.setting.property.delete.label.confirmation"/>')) {

			var callback = {
				success : function() {
					filter(JsonDataTable, '', '');
				}
			}
			var request = ConnectionManager.post('${pageContext.request.contextPath}/web/console/setting/property/delete', callback, 'ids=' + selectedList);
		}
	}
	
</script>

<script>
	Template.init("", "#nav-setting-property");
</script>

<commons:footer />
