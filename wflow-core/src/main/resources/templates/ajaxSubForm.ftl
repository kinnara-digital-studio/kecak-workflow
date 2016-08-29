<div class="${cellClass!}" ${elementMetaData!}>
    <#if includeMetaData>
        <span class="form-floating-label">AJAX Sub Form</span>
    </#if>
    <#if !(request.getAttribute("org.joget.plugin.enterprise.AjaxSubForm")??) >
        <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.AjaxSubForm/js/jquery.ajaxsubform.js"></script>
    </#if>    
    <div>
        <div name="${elementParamName!}" id="${elementParamName!}_ajaxsubform_${element.properties.elementUniqueKey!}" class="form-element" loadedValue="${loadedValue!?html}">
            ${childrenHTML!}
        </div>
    </div>
    <script type="text/javascript">
        $(document).ready(function(){
            $("#${elementParamName!}_ajaxsubform_${element.properties.elementUniqueKey!}").ajaxSubForm({
                contextPath: "${request.contextPath}",
                id: "${element.properties.id!}",
                label: "${element.properties.label!?html}",
                formDefId: "${element.properties.formDefId!}",
                readOnly: "${element.properties.readonly!}",
                readOnlyLabel: "${element.properties.readonlyLabel!?html}",
                noframe: "${element.properties.noframe!}",
                ajax: "${element.properties.ajax!}",
                parentSubFormId : "${element.properties.parentSubFormParamName!}",
                prefix : "${element.properties.prefix!}",
                hideEmpty : "${element.properties.hideEmpty!}",
                appId : "${appId!}",
                appVersion : "${appVersion!}",
                processId : "${processId!}",
                nonce : "${nonceForm!}",
                collapsible: "${element.properties.collapsible!}",
                collapsibleLabelExpanded: "${element.properties.collapsibleLabelExpanded!?html}",
                collapsibleLabelCollapsed: "${element.properties.collapsibleLabelCollapsed!?html}",
                collapsibleExpanded: "${element.properties.collapsibleExpanded!}",
                collapsibleNoLoad: "${element.properties.noLoad!}"
            });
        });
    </script>
</div>