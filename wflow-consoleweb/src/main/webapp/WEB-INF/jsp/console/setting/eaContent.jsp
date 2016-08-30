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
					<fmt:message key="console.setting.eaContent.create.label" />
				</button></li>
		</ul>
	</div>
	<div id="main-body">
		<ui:jsontable
			url="${pageContext.request.contextPath}/web/json/console/setting/eaContent/list?${pageContext.request.queryString}"
			var="JsonDataTable" 
			divToUpdate="eaContentList" 
			jsonData="data"
			rowsPerPage="10" 
			width="100%" 
			sort="processId" desc="false"
			href="${pageContext.request.contextPath}/web/console/setting/eaContent/edit"
			hrefParam="id" 
			hrefQuery="false" 
			hrefDialog="true" 
			hrefDialogTitle=""
			checkbox="true"
			checkboxButton2="general.method.label.delete"
			checkboxCallback2="eaContentDelete" 
			searchItems="processId|Process ID"
			fields="['id','processId','activityId','content','createdate','modifiedate']"
			column1="{key: 'processId', label: 'console.setting.eaContent.common.label.processId', sortable: true}"
			column2="{key: 'activityId', label: 'console.setting.eaContent.common.label.activityId', sortable: false}" 
			column3="{key: 'createdate', label: 'console.setting.eaContent.common.label.createdate', sortable: false}"
			column4="{key: 'modifiedate', label: 'console.setting.eaContent.common.label.modifiedate', sortable: false}"/>
	</div>
</div>

<script>
    $(document).ready(function(){
        $('#JsonDataTable_searchTerm').hide();

    });

    <ui:popupdialog var="popupDialog" src="${pageContext.request.contextPath}/web/console/setting/eaContent/create"/>

    function onCreate(){
        popupDialog.init();
    }

    function closeDialog() {
        popupDialog.close();
    }

    
	function eaContentDelete(selectedList) {
		if (confirm('<fmt:message key="console.setting.eaContent.delete.label.confirmation"/>')) {

			var callback = {
				success : function() {
					filter(JsonDataTable, '', '');
				}
			}
			var request = ConnectionManager.post('${pageContext.request.contextPath}/web/console/setting/eaContent/delete', callback, 'ids=' + selectedList);
		}
	}
</script>

<script>
	Template.init("", "#nav-setting-eaContent");
</script>

<commons:footer />
