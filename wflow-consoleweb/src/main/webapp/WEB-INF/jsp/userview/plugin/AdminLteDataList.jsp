<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:if test="${requestParameters.isPreview eq 'true'}">
    <script>
        $(document).ready(function() {
            $(".dataList .actions button").attr("disabled", "disabled");
            $(".dataList form input, .dataList form select").attr("disabled", "disabled");
            $(".dataList a").attr("href", "#");
        });
    </script>
</c:if>
   
<link rel="stylesheet" href="<c:url value="/css/datalistBuilderView.css"/>?build=<fmt:message key="build.number"/>" />
<div class="datalist-body-content box">
	<div class="box-header">
	    <c:if test="${!empty properties.customHeader}">
	        <h3 class="box-title">${properties.customHeader}</h3>
	    </c:if>
	    <c:if test="${!empty properties.error}">
	        <h3>Error generating the data list</h3>
	        <div id="error">${properties.error}</div>
	    </c:if>
	
	    <c:set scope="request" var="dataList" value="${properties.dataList}"/>
	
	    <c:if test="${!empty dataList}">
	        <jsp:include page="/WEB-INF/jsp/dbuilder/adminLteDatalistView.jsp" flush="true" />
	    </c:if>    
	        
	    <c:if test="${!empty properties.customFooter}">
	        ${properties.customFooter}
	    </c:if>    
    </div>
</div>
