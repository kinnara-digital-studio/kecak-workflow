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
                            <%@ page import="org.joget.apps.app.service.MobileUtil"%>
                            <%@ page import="org.joget.apps.app.service.AppUtil"%>
                            <%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
                            <%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

                            <c:set var="mobileView" value="<%= MobileUtil.isMobileView() %>"/>
                            <c:if test="${mobileView}">
                                <c:set scope="request" var="dataList" value="${dataList}"/>
                                <jsp:forward page="/WEB-INF/jsp/mobile/mDataListView.jsp"/>
                            </c:if>

                            <c:set scope="request" var="dataListId" value="${dataList.id}"/>
                            <style>
                                .filters { text-align:right; font-size:smaller }
                                .filter-cell{display:inline-block;padding-left:5px;}
                            </style>

                            <div class="dataList">

                                <c:set var="isQuickEditEnabled" value="<%= AppUtil.isQuickEditEnabled() %>"/>
                                <c:if test="${isQuickEditEnabled}">
                                <div class="quickEdit" style="display: none">
                                    <a href="${pageContext.request.contextPath}/web/console/app/${appId}/${appVersion}/datalist/builder/${dataList.id}" target="_blank"><i class="icon-edit"></i>  <fmt:message key="adminBar.label.list"/>: <c:out value="${dataList.name}"/></a>
                                </div>
                                </c:if>

                                <c:catch var="dataListException">

                                    <c:set var="actionResult" value="${dataList.actionResult}" />
                                    <c:if test="${!empty actionResult}">
                                        <c:if test="${!empty actionResult.message}">
                                            <script>
                                                alert("${actionResult.message}");
                                            </script>
                                        </c:if>
                                        <c:choose>
                                            <c:when test="${actionResult.type == 'REDIRECT' && actionResult.url == 'REFERER'}">
                                                <script>
                                                    location.href = "${header['Referer']}";
                                                </script>
                                            </c:when>
                                            <c:when test="${actionResult.type == 'REDIRECT'  && !empty actionResult.url}">
                                                <script>
                                                    location.href = "${actionResult.url}";
                                                </script>
                                            </c:when>
                                            <c:otherwise>

                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                    <c:set var="dataListRows" scope="request" value="${dataList.rows}"/>
                                    <c:set var="dataListSize" scope="request" value="${dataList.size}"/>
                                    <c:set var="dataListPageSize" scope="request" value="${dataList.pageSize}"/>
                                    <c:set var="decorator" scope="request" value="${dataList.primaryKeyDecorator}"/>

                                    <!-- set default button position if value is null -->
                                    <c:set var="buttonPosition" value="${dataList.actionPosition}" />

                                    <c:set var="buttonFloat" value="left" />
                                    <c:if test="${buttonPosition eq 'topRight' || buttonPosition eq 'bottomRight' || buttonPosition eq 'bothRight'}">
                                        <c:set var="buttonFloat" value="right" />
                                    </c:if>

                                    <!-- set checkbox position if value is null -->
                                    <c:set var="checkboxPosition" value="${dataList.checkboxPosition}" />

                                    <c:set var="selectionType" value="multiple" />
                                    <c:if test="${dataList.selectionType eq 'single'}">
                                        <c:set var="selectionType" value="single" />
                                    </c:if>

                                    <!-- Display Filters -->
                            		<c:choose>
                            			<c:when test="${dataList.id eq 'tac_my_request_list'}">
                            				<c:if test="${!empty dataList.filterTemplates[0]}">
                            					<form name="filters_${dataListId}" id="filters_${dataListId}" action="?" method="POST">
                            						<table border="0" width="100%">
                            							<tr>
                            								<td>
                            									<table border="0" id="filtertemplate_allList2">
                            										<tr><td width="150">Filter by date : </td>
                            											<c:forEach items="${dataList.filterTemplates}" var="template" varStatus="loop">
                            												<c:if test="${loop.index > 1}">
                            													<c:set var="idx" value="${loop.index%3}" />
                            													<td width="165">
                            														${template}
                            													</td>
                            													<c:if test="${loop.index > 0
                            															and (loop.index == 4 or loop.index == 7 or loop.index == 10 or loop.index == 12)}">
                            														</tr>
                            														<tr>
                            															<c:choose>
                            																<c:when test="${loop.index == 4}">
                            																	<td>Filter by key of request :</td>
                            																</c:when>
                            																<c:when test="${loop.index == 7}">
                            																	<td>Filter by memo attribute :</td>
                            																</c:when>
                            																<c:when test="${loop.index == 10}">
                            																	<td>&nbsp;</td>
                            																</c:when>
                            																<c:when test="${loop.index == 12}">
                            																	<td>Filter by status :</td>
                            																</c:when>
                            															</c:choose>
                            													</c:if>
                            												</c:if>
                            											</c:forEach>
                            										</tr>
                            									</table>
                            								</td>

                            								<td align="right" width="200">
                            									<c:forEach items="${dataList.filterTemplates}" var="template2" varStatus="loop">
                            										<c:if test="${loop.index == 1}">
                            											<div style="width:130px">
                            											Page Size : ${template2}
                            											</div>
                            										</c:if>
                            									</c:forEach>
                            								</td>
                            							</tr>
                            						</table>

                            						 <span class="filter-cell">
                            							 <input type="submit" value="<fmt:message key="general.method.label.show"/>"/>
                            						 </span>
                            					</form>
                            				</c:if>
                            			</c:when>
                            			<c:when test="${dataList.id eq 'tacAllList2'}">
                            				<c:if test="${!empty dataList.filterTemplates[0]}">
                            					<form name="filters_${dataListId}" id="filters_${dataListId}" action="?" method="POST">
                            						<table border="0" width="100%">
                            							<tr>
                            								<td>
                            									<table border="0" id="filtertemplate_allList2">
                            										<tr><td width="150">Filter by date : </td>
                            											<c:forEach items="${dataList.filterTemplates}" var="template" varStatus="loop">
                            												<c:if test="${loop.index > 1}">
                            													<c:set var="idx" value="${loop.index%3}" />
                            													<td width="165">
                            														${template}
                            													</td>
                            													<c:if test="${loop.index > 0
                            															and (loop.index == 4 or loop.index == 7 or loop.index == 10 or loop.index == 12)}">
                            														</tr>
                            														<tr>
                            															<c:choose>
                            																<c:when test="${loop.index == 4}">
                            																	<td>Filter by key of request :</td>
                            																</c:when>
                            																<c:when test="${loop.index == 7}">
                            																	<td>Filter by memo attribute :</td>
                            																</c:when>
                            																<c:when test="${loop.index == 10}">
                            																	<td>&nbsp;</td>
                            																</c:when>
                            																<c:when test="${loop.index == 12}">
                            																	<td>Filter by status :</td>
                            																</c:when>
                            															</c:choose>
                            													</c:if>
                            												</c:if>
                            											</c:forEach>
                            										</tr>
                            									</table>
                            								</td>

                            								<td align="right" width="200">
                            									<c:forEach items="${dataList.filterTemplates}" var="template2" varStatus="loop">
                            										<c:if test="${loop.index == 1}">
                            											<div style="width:130px">
                            											Page Size : ${template2}
                            											</div>
                            										</c:if>
                            									</c:forEach>
                            								</td>
                            							</tr>
                            						</table>

                            						 <span class="filter-cell">
                            							 <input type="submit" value="<fmt:message key="general.method.label.show"/>"/>
                            						 </span>
                            					</form>
                            				</c:if>
                            			</c:when>
                            			<c:otherwise>

                            				<c:if test="${!empty dataList.filterTemplates[0]}">
                            					<form name="filters_${dataListId}" id="filters_${dataListId}" action="?" method="POST">
                            						<div class="filters">
                            							<c:forEach items="${dataList.filterTemplates}" var="template">
                            								<span class="filter-cell">
                            									${template}
                            								</span>
                            							</c:forEach>
                            							 <span class="filter-cell">
                            								 <input type="submit" value="<fmt:message key="general.method.label.show"/>"/>
                            							 </span>
                            						</div>
                            					</form>
                            				</c:if>
                            			</c:otherwise>
                            		</c:choose>

                                    <!-- Display Main Table -->
                                    <form name="form_${dataListId}" action="?${queryString}" method="POST">
                                        <!-- Display Buttons -->
                                        <c:if test="${buttonPosition eq 'topLeft' || buttonPosition eq 'topRight' || buttonPosition eq 'bothLeft' || buttonPosition eq 'bothRight'}">
                                            <div class="actions bottom ${buttonFloat}">
                                                <c:forEach items="${dataList.actions}" var="action">
                                                    <c:if test="${!(empty dataListRows[0] || checkboxPosition eq 'no') || action.visibleOnNoRecord}">
                                                        <c:set var="buttonConfirmation" value="" />
                                                        <c:if test="${!empty action.confirmation}">
                                                            <c:set var="buttonConfirmation" value=" onclick=\"return showConfirm(this, '${fn:escapeXml(action.confirmation)}')\""/>
                                                        </c:if>
                                                        <button name="${dataList.actionParamName}" value="${action.properties.id}" ${buttonConfirmation}><c:out value="${action.linkLabel}" escapeXml="true"/></button>
                                                    </c:if>
                                                </c:forEach>
                                            </div>
                                        </c:if>

                                        <c:if test="${param.embed}">
                                            <input type="hidden" name="embed" id="embed" value="true"/>
                                        </c:if>
                                        <display:table id="${dataListId}" name="dataListRows" pagesize="${dataListPageSize}" class="xrounded_shadowed" export="true" decorator="decorator" excludedParams="${dataList.binder.primaryKeyColumnName}" requestURI="?" sort="external" partialList="true" size="dataListSize">
                                            <c:if test="${checkboxPosition eq 'left' || checkboxPosition eq 'both'}">
                                                <c:choose>
                                                    <c:when test="${selectionType eq 'single'}">
                                                        <display:column headerClass="select_radio" class="select_radio" property="radio" media="html" title="" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <display:column headerClass="select_checkbox" class="select_checkbox" property="checkbox" media="html" title="<input type='checkbox' onclick='toggleAll(this)' style='float:left;'/>" />
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:if>
                                            <c:forEach items="${dataList.columns}" var="column">
                                                <c:set var="columnLabel"><c:out value="${column.label}"/></c:set>
                                                <display:column
                                                    property="column(${column.name})"
                                                    title="${columnLabel}"
                                                    sortable="${column.sortable}"
                                                    headerClass="column_${column.name}"
                                                    class="column_${column.name}"
                                                    />
                                            </c:forEach>
                                            <c:if test="${!empty dataList.rowActions[0]}">
                                                <c:set var="actionTitle" value="" />
                                                <c:forEach items="${dataList.rowActions}" var="rowAction" begin="1">
                                                    <c:set var="actionTitle" value="${actionTitle}</th><th class=\"row_action\">" />
                                                </c:forEach>
                                                <display:column headerClass="row_action" class="row_action" property="actions" media="html" title="${actionTitle}"/>
                                            </c:if>
                                            <c:if test="${checkboxPosition eq 'right' || checkboxPosition eq 'both'}">
                                                <c:choose>
                                                    <c:when test="${selectionType eq 'single'}">
                                                        <display:column headerClass="select_radio" class="select_radio" property="radio" media="html" title="" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <display:column headerClass="select_checkbox" class="select_checkbox" property="checkbox" media="html" title="<input type='checkbox' onclick='toggleAll(this)' style='float:left;'/>" />
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:if>
                                        </display:table>

                                        <!-- Display Buttons -->
                                        <c:if test="${buttonPosition eq 'bottomLeft' || buttonPosition eq 'bottomRight' || buttonPosition eq 'bothLeft' || buttonPosition eq 'bothRight'}">
                                            <div class="actions bottom ${buttonFloat}">
                                                <c:forEach items="${dataList.actions}" var="action">
                                                    <c:if test="${!(empty dataListRows[0] || checkboxPosition eq 'no') || action.visibleOnNoRecord}">
                                                        <c:set var="buttonConfirmation" value="" />
                                                        <c:if test="${!empty action.confirmation}">
                                                            <c:set var="buttonConfirmation" value=" onclick=\"return showConfirm(this, '${fn:escapeXml(action.confirmation)}')\""/>
                                                        </c:if>
                                                        <button name="${dataList.actionParamName}" value="${action.properties.id}" ${buttonConfirmation}><c:out value="${action.linkLabel}" escapeXml="true"/></button>
                                                    </c:if>
                                                </c:forEach>
                                            </div>
                                        </c:if>
                                    </form>
                                </c:catch>

                                ${dataListException}
                                <c:if test="${!empty dataListException}">
                            <pre>
                            <%
                            Throwable t =(Throwable)pageContext.findAttribute("dataListException");
                            t.printStackTrace(new java.io.PrintWriter(out));
                            %>
                            </pre>
                                </c:if>

                            </div>

                            <script>
                                var popupActionDialog = null;

                                DataListUtil = {
                                    submitForm: function(form) {
                                        var params = $(form).serialize();
                                        var queryStr = window.location.search;
                                        params = params.replace(/\+/g, " ");
                                        var newUrl = UrlUtil.mergeRequestQueryString(queryStr, params);
                                        window.location.href = "?" + newUrl;
                                        return false;
                                    }
                                }
                                $(document).ready(function() {
                                    $("#filters_${dataListId}").submit(function(e) {
                                        e.preventDefault();
                                        DataListUtil.submitForm(this);
                                    });
                                });
                                function toggleAll(element) {
                                    var table = $(element).parent().parent().parent().parent();
                                    if ($(element).is(":checked")) {
                                        $(table).find("input[type=checkbox]").attr("checked", "checked");
                                    } else {
                                        $(table).find("input[type=checkbox]").removeAttr("checked");
                                    }
                                }
                                function dlPopupAction(element, message) {
                                    var url = $(element).attr("href");
                                    var showPopup = true;
                                    if (message != "") {
                                        showPopup = confirm(message);
                                    }
                                    if (showPopup) {
                                        if (popupActionDialog == null) {
                                            popupActionDialog = new PopupDialog(url);
                                        } else {
                                            popupActionDialog.src = url;
                                        }
                                        popupActionDialog.init();
                                    }
                                    return false;
                                }
                                function showConfirm(element, message) {
                                    var table = $(element).parent().parent().find('table');
                                    if ($(table).find("input[type=checkbox][name|=d]:checked").length > 0) {
                                        return confirm(message);
                                    } else {
                                        alert("<fmt:message key="dbuilder.alert.noRecordSelected"/>");
                                        return false;
                                    }
                                }
                            </script>
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
