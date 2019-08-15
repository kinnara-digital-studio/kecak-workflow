<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<c:if test="${requestParameters.isPreview eq 'true'}">
    <script>
        $(document).ready(function() {
            $(".form-button").attr("disabled", "disabled");
        });
    </script>
</c:if>
<div class="viewForm-body-content">
    <div class="viewForm-body-header content-header">
        <h1>
            <c:choose>
                <c:when test="${empty properties.customHeader && !empty properties.headerTitle}">
                        <c:out value="${properties.headerTitle}"/>
                </c:when>
                <c:otherwise>
                    ${properties.customHeader}
                </c:otherwise>
            </c:choose>
        </h1>
    </div>
    <section class="content">
        <div class="row">
            <div class="col-md-12">
                <div class="box box-primary">
                    <c:choose>
                        <c:when test="${properties.view eq 'unauthorized'}">
                            <p>
                                <fmt:message key="general.body.unauthorized" />
                            </p>
                        </c:when>
                        <c:when test="${properties.view eq 'formView'}">
                            <c:set var="appDef" scope="request" value="${properties.appDef}"/>
                            <c:set var="assignment" scope="request" value="${properties.assignment}"/>
                            <c:set var="activityForm" scope="request" value="${properties.activityForm}"/>
                            <c:set var="formHtml" scope="request" value="${properties.formHtml}"/>
                            <c:set var="formJson" scope="request" value="${properties.formJson}"/>
                            <c:set var="errorCount" scope="request" value="${properties.errorCount}"/>
                            <c:set var="submitted" scope="request" value="${properties.submitted}"/>
                            <c:set var="stay" scope="request" value="${properties.stay}"/>
                            <c:set var="closeDialog" scope="request" value="${properties.closeDialog}"/>
                            <jsp:include page="../../client/app/formView2.jsp" flush="true" />
                        </c:when>
                        <c:when test="${properties.view eq 'formUnavailable'}">
                            <p>
                                <fmt:message key="client.app.run.process.label.form.unavailable" />
                            </p>
                        </c:when>
                        <c:when test="${properties.view eq 'assignmentUpdated'}">
                            <p>
                                <fmt:message key="client.app.run.process.label.assignment.updated" />
                            </p>
                        </c:when>
                    </c:choose>
                    <c:if test="${!empty properties.showRecordTraveling && properties.showRecordTraveling}">
                        <div id="recordTraveling" style="padding:10px 0">
                            <c:if test="${!empty properties.firstRecordUrl}">
                                <a id="firstRecord" href="${properties.firstRecordUrl}"><span>${properties.firstRecordLabel}</span></a>&nbsp;&nbsp;
                            </c:if>
                            <c:if test="${!empty properties.previousRecordUrl}">
                                <a id="prevRecord" href="${properties.previousRecordUrl}"><span>${properties.previousRecordLabel}</span></a>&nbsp;&nbsp;
                            </c:if>
                            <span>${properties.recordPosition} / ${properties.totalRecord}</span>&nbsp;&nbsp;
                            <c:if test="${!empty properties.nextRecordUrl}">
                                <a id="nextRecord" href="${properties.nextRecordUrl}"><span>${properties.nextRecordLabel}</span></a>&nbsp;&nbsp;
                            </c:if>
                            <c:if test="${!empty properties.lastRecordUrl}">
                                <a id="lastRecord" href="${properties.lastRecordUrl}"><span>${properties.lastRecordLabel}</span></a>&nbsp;&nbsp;
                            </c:if>
                        </div>
                    </c:if>
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

<div style="clear:both;"></div>

<a class="print-button btn btn-primary" href="#" onclick="userviewPrint();return false;"><fmt:message key="general.method.label.print" /></a>
<script type="text/javascript">
    $(function(){
        $('.print-button').appendTo('#section-actions .form-column');
    });
</script>