<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>

<commons:popupHeader />

    <div id="main-body-header">
        <fmt:message key="console.setting.property.create.label.title"/>
    </div>

    <div id="main-body-content">
        <form:form id="createProperty" action="${pageContext.request.contextPath}/web/console/setting/property/submit/edit" method="POST" commandName="property" cssClass="form">
            <form:errors path="*" cssClass="form-errors"/>
            <c:if test="${!empty errors}">
                <span class="form-errors" style="display:block">
                    <c:forEach items="${errors}" var="error">
                        <fmt:message key="${error}"/>
                    </c:forEach>
                </span>
            </c:if>
            <fieldset>
                <legend><fmt:message key="console.setting.property.common.label.details"/></legend>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.setting.property.common.label.id"/></label>
                    <span class="form-input"><c:out value="${property.id}"/><input id="id" type="hidden" value="${property.id}" name="id"/></span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.setting.property.common.label.propertyLabel"/></label>
                    <span class="form-input"><form:input path="propertyLabel" cssErrorClass="form-input-error" size="30" disabled="true" readonly="true" /> *</span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.setting.property.common.label.propertyValue"/></label>
                    <span class="form-input"><form:input path="propertyValue" cssErrorClass="form-input-error" size="30"/> *</span>
                </div>
            </fieldset>
            <div class="form-buttons">
                <input class="form-button" type="button" value="<fmt:message key="general.method.label.save"/>"  onclick="validateField()"/>
                <input class="form-button" type="button" value="<fmt:message key="general.method.label.cancel"/>" onclick="closeDialog()"/>
            </div>
        </form:form>
    </div>

    <script type="text/javascript">
        function validateField(){
        	var propertyLabel = /^[0-9a-zA-Z._-]+$/.test($("#propertyLabel").attr("value"));
        	var propertyValue = /^[0-9a-zA-Z.@#$%^&*()!?><,_-]+$/.test($("#propertyValue").attr("value"));
        	
            if(!propertyLabel){
                var alertString = '<fmt:message key="console.setting.property.error.label.propertyLabelInvalid"/>';
                $("#propertyLabel").focus();
                alert(alertString);
            } else if (!propertyValue) {
            	var alertString = '<fmt:message key="console.setting.scheduler.error.label.propertyValueInvalid"/>';
                $("#propertyValue").focus();
                alert(alertString);
            } else{
                $("#createProperty").submit();
            }
        }

        function closeDialog() {
            if (parent && parent.PopupDialog.closeDialog) {
                parent.PopupDialog.closeDialog();
            }
            return false;
        }
    </script>
<commons:popupFooter />
