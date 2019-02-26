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
<div class="datalist-body-content">
    <div class="content-header"><h1>
    <c:if test="${!empty properties.customHeader}">
        ${properties.customHeader}
    </c:if>
    </h1></div>
    <section class="content">
        <div class="row">
            <div class="col-md-12">
                <div class="box box-primary">
                    <div class="box-body">
                        <c:if test="${!empty properties.error}">
                            <h3>Error generating the data list</h3>
                            <div id="error">${properties.error}</div>
                        </c:if>

                        <c:set scope="request" var="dataList" value="${properties.dataList}"/>

                        <c:if test="${!empty dataList}">
                            <jsp:include page="/WEB-INF/jsp/dbuilder/dataListView2.jsp" flush="true" />
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <c:if test="${!empty properties.customFooter}">
        <footer class="main-footer">
            ${properties.customFooter}
        </footer>
    </c:if>
</div>
