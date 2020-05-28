<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<%@ page import="java.net.URLEncoder" %>

<commons:popupHeader />

    <div id="main-body-header">
        <fmt:message key="console.setting.clientApp.create.label.title"/>
    </div>

    <div id="main-body-content">
        <form:form id="createClientApp" action="${pageContext.request.contextPath}/web/console/setting/clientApp/submit/create" method="POST" commandName="clientApp" cssClass="form">
            <form:errors path="*" cssClass="form-errors"/>
            <c:if test="${!empty errors}">
                <span class="form-errors" style="display:block">
                    <c:forEach items="${errors}" var="error">
                        <c:out value="${error}"/><br/>
                    </c:forEach>
                </span>
            </c:if>
            <fieldset>
                <legend><fmt:message key="console.setting.clientApp.common.label.details"/></legend>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.setting.clientApp.common.label.appName"/></label>
                    <span class="form-input"><form:input path="appName" cssErrorClass="form-input-error" /> *</span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.setting.clientApp.common.label.redirectUrl"/></label>
                    <span class="form-input"><form:input path="redirectUrl" cssErrorClass="form-input-error" /> *</span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.setting.clientApp.common.label.status"/></label>
                    <span class="form-input">
                        <form:select path="active" cssErrorClass="form-input-error">
                            <form:options items="${status}"/>
                        </form:select>
                    </span>
                </div>
            </fieldset>
            <div class="form-buttons">
                <input class="form-button" type="submit" value="<fmt:message key="general.method.label.save"/>"/>
                <input class="form-button" type="button" value="<fmt:message key="general.method.label.cancel"/>" onclick="closeDialog()"/>
            </div>
        </form:form>
    </div>

    <script type="text/javascript">
        function closeDialog() {
            if (parent && parent.PopupDialog.closeDialog) {
                parent.PopupDialog.closeDialog();
            }
            return false;
        }
    </script>
<commons:popupFooter />
