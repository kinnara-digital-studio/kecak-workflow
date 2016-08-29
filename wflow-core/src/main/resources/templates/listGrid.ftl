<div class="form-cell" ${elementMetaData!}>
<#if !(request.getAttribute("org.joget.plugin.enterprise.ListGrid")??) >
    <link href="${request.contextPath}/js/boxy/stylesheets/boxy.css" rel="stylesheet" type="text/css" />
    <link href="${request.contextPath}/plugin/org.joget.plugin.enterprise.FormGrid/css/gridPaging.css" rel="stylesheet" type="text/css" />
    <script type='text/javascript' src='${request.contextPath}/js/boxy/javascripts/jquery.boxy.js'></script>
    <script type="text/javascript" src="${request.contextPath}/js/jquery/jquery.jeditable.js"></script>
    <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.FormGrid/js/jquery.enterpriseformgrid.js"></script>
    <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.FormGrid/js/date.js"></script>
    <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.ListGrid/js/jquery.listgrid.js"></script>
    <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.FormGrid/js/jquery.gridPaging.js"></script>

    <style type="text/css">
        .grid table {
            width: 100%;
        }
        .grid th, .grid td {
            border: solid 1px silver;
            margin: 0px;
        }
        .grid-cell-options {
            width: 10px;
        }
        .grid-row-template {
            display: none;
        }
        .grid-cell input:focus {
            background: #efefef;
            border: 1px solid #a1a1a1;
        }
        .grid-action-edit,
        .grid-action-delete,
        .grid-action-moveup,
        .grid-action-movedown,
        .grid-action-add{
            display:inline-block;
            height:16px;
            width:16px;
        }
        .grid-action-delete{
            background: url(${request.contextPath}/images/v3/property_editor/delete.png) no-repeat;
        }
        .grid-action-moveup{
            display:none;
            background: url(${request.contextPath}/images/v3/property_editor/up.png) no-repeat;
        }
        .grid-action-movedown{
            display:none;
            background: url(${request.contextPath}/images/v3/property_editor/down.png) no-repeat;
        }
        .grid-action-add{
            margin-top:3px;
            background: url(${request.contextPath}/images/v3/property_editor/add.png) no-repeat;
        }
        .grid-action-edit{
            background: url(${request.contextPath}/plugin/org.joget.plugin.enterprise.FormGrid/images/edit.png) no-repeat;
        }
        .grid-action-moveup.disabled{
            background: url(${request.contextPath}/images/v3/property_editor/up_d.png) no-repeat;
        }
        .grid-action-movedown.disabled{
            background: url(${request.contextPath}/images/v3/property_editor/down_d.png) no-repeat;
        }
        .grid-action-edit span,
        .grid-action-delete span,
        .grid-action-moveup span,
        .grid-action-movedown span,
        .grid-action-add span{
            display:none;
        }
        .grid.enableSorting a.grid-action-moveup,
        .grid.enableSorting a.grid-action-movedown{
            display:inline-block;
        }
        .grid.readonly.enableSorting a.grid-action-moveup,
        .grid.readonly.enableSorting a.grid-action-movedown,
        .grid.readonly a.grid-action-add,
        .grid.readonly a.grid-action-delete,
        .grid.disabledAdd a.grid-action-add,
        .grid.disabledDelete a.grid-action-delete{
            display:none;
        }
        .grid.readonly a.grid-action-edit{
            display:inline-block;
            background: url(${request.contextPath}/plugin/org.joget.plugin.enterprise.FormGrid/images/view.png) no-repeat;
        }
        .grid-cell.hover {
            background: lightyellow;
            display: block;
        }
        .grid-cell[column_editable='yes'] {
            display: block;
        }
        .grid-cell[column_editable='yes'].editable_empty {
            color:gray;
        }
        .grid .grid-action-header, .grid .grid-action-cell {
            border: 0 none;
        }
    </style>
</#if>
<script type="text/javascript">
    $(document).ready(function() {
        var messages = {
            "form.listgrid.addRows": "@@form.listgrid.addRows@@",
            "form.listgrid.addEntry": "@@form.listgrid.addEntry@@",
            "form.listgrid.editable.tooltip": "@@form.listgrid.editable.tooltip@@",
            "form.listgrid.editable.ok": "@@form.listgrid.editable.ok@@"
        }
        $("#listgrid_${elementParamName!}_${element.properties.elementUniqueKey!}").listgrid({messages: messages, options : ${optionsJson!}});
        $("#listgrid_${elementParamName!}_${element.properties.elementUniqueKey!}").listgrid("initPopupDialog", {contextPath:'${request.contextPath}', width:'${element.properties.width!?html}', height:'${element.properties.height!?html}', title:'@@form.listgrid.addEntry@@', appId:'${appId}', appVersion:'${appVersion}', listId:'${listId}', label: '<#if element.properties.buttonLabel??>${element.properties.buttonLabel?html}<#else>@@form.listgrid.insert@@</#if>' <#if formId??>, formId:'${formId}'</#if> <#if uniqueKey??>, uniqueKey:'${uniqueKey}'</#if> <#if requestParams??>, requestParams:${requestParams}</#if>});
        
        $("#listgrid_${elementParamName!}_${element.properties.elementUniqueKey!}").gridPaging({<#if element.properties.enableSorting! == 'true'>dataSorting : true</#if>});
    });

    function listgrid_${elementParamName!}_${element.properties.elementUniqueKey!}_add(args){
        $("#listgrid_${elementParamName!}_${element.properties.elementUniqueKey!}").listgrid("addRow", args);
    }

    function listgrid_${elementParamName!}_${element.properties.elementUniqueKey!}_edit(args){
        $("#listgrid_${elementParamName!}_${element.properties.elementUniqueKey!}").listgrid("editRow", args);
    }
</script>

    <label class="label">${element.properties.label!?html} <span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
    <div class="form-clear"></div>
    <div id="listgrid_${elementParamName!}_${element.properties.elementUniqueKey!}" name="${elementParamName!}" class="grid form-element <#if element.properties.readonly! == 'true'>readonly</#if> <#if element.properties.enableSorting! == 'true'>enableSorting</#if> <#if element.properties.disabledAdd! == 'true'>disabledAdd</#if> <#if element.properties.disabledDelete! == 'true'>disabledDelete</#if>">
        <input type="hidden" disabled="disabled" id="formUrl" value="${request.contextPath}/web/app/${appId}/${appVersion}/form/embed?_submitButtonLabel=${buttonLabel!?html}">
        <input type="hidden" disabled="disabled" id="json" value="${json!}">
        <input type="hidden" disabled="disabled" id="appId" value="${appId!}">
        <input type="hidden" disabled="disabled" id="appVersion" value="${appVersion!}">
        <input type="hidden" disabled="disabled" id="contextPath" value="${request.contextPath}">
        <input type="hidden" disabled="disabled" id="height" value="${element.properties.height!?html}">
        <input type="hidden" disabled="disabled" id="width" value="${element.properties.width!?html}">
        <input type="hidden" disabled="disabled" id="validateMaxRow" value="${element.properties.validateMaxRow!}">
        <input type="hidden" disabled="disabled" id="deleteMessage" value="${element.properties.deleteMessage!?html}">
        <input type="hidden" disabled="disabled" id="nonce" value="${nonceForm!?html}">
        <input type="hidden" disabled="disabled" id="nonceList" value="${nonceList!?html}">
        <table cellspacing="0" class="tablesaw tablesaw-stack" data-tablesaw-mode="stack">
            <thead>
            <tr>
            <#if element.properties.showRowNumber?? && element.properties.showRowNumber! != "">
                <th></th>
            </#if>
            <#list headers?keys as header>
                <#assign width = "">
                <#if headers[header]['width']?? && headers[header]['width'] != "">
                    <#assign width = "width:" + headers[header]['width'] >
                </#if>
                <th id="${elementParamName!}_${header?html}" style="${width}">${headers[header]['label']!?html}</th>
            </#list>
                <th class="grid-action-header"></th>
            </tr>
            <tr id="grid-row-template" class="grid-row-template" style="display:none;">
                <#if element.properties.showRowNumber?? && element.properties.showRowNumber! != "">
                    <td><span class="grid-cell rowNumber"></span></td>
                </#if>
            <#list headers?keys as header>
                <td><span id="${elementParamName!}_${header?html}"  name="${elementParamName!}_${header?html}" column_key="${header?html}" column_type="${headers[header]['formatType']!?html}" column_format="${headers[header]['format']!?html}" column_editable="${headers[header]['editable']!?html}" class="grid-cell"></span></td>
            </#list>
                <td style="display:none;"><textarea id="${elementParamName!}_jsonrow"></textarea></td>
            </tr>
            </thead>
            <tbody>
            <#list rows as row>
                <tr class="grid-row" id="{elementParamName!}_row_${row_index}">
                    <#if element.properties.showRowNumber?? && element.properties.showRowNumber! != "">
                        <td><span class="grid-cell rowNumber">${row_index + 1}</span></td>
                    </#if>
                <#list headers?keys as header>
                    <td><span id="${elementParamName!}_${header?html}" name="${elementParamName!}_${header?html}" column_key="${header?html}" column_type="${headers[header]['formatType']!?html}" column_format="${headers[header]['format']!?html}" column_editable="${headers[header]['editable']!?html}" class="grid-cell">
                            <#attempt>
                                ${element.formatColumn(header, headers[header], row["id"], row[header], appId, appVersion, request.contextPath)}
                            <#recover>
                                ${row[header]!?html}
                            </#attempt>
                        </span>
                    </td>
                </#list>
                    <td style="display:none;"><textarea id="${elementParamName!}_jsonrow" name="${elementParamName!}_jsonrow_${row_index}">${row['jsonrow']!?html}</textarea></td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</div>
