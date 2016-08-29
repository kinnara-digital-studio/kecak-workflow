<div class="form-cell" ${elementMetaData!}>

    <#if (element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true') >
        <label class="label">${element.properties.label} <span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
        <div class="form-cell-value">
            <#list options as option>
                <#if values?? && values?seq_contains(option.value!)>
                    <label class="readonly_label">
                        <span>${option.label!?html}</span>
                        <input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${option.value!?html}" />
                    </label>
                </#if>
            </#list>
        </div>
        <div style="clear:both;"></div>
    <#else>
        <#if (element.properties.controlField?? && element.properties.controlField! != "" && !(element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true')) >
            <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.apps.form.lib.SelectBox/js/jquery.dynamicoptions.js"></script>
        </#if>

        <#if !(request.getAttribute("org.joget.plugin.enterprise.MultiSelectBox_EDITABLE")??) >
            <script type="text/javascript" src="${request.contextPath}/js/chosen/chosen.jquery.js"></script>
            <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.MultiSelectBox/js/jquery.multiselect.js"></script>
            <link rel="stylesheet" type="text/css" href="${request.contextPath}/js/chosen/chosen.css" />
            <link rel="stylesheet" type="text/css" href="${request.contextPath}/plugin/org.joget.plugin.enterprise.MultiSelectBox/css/jquery.multiselect.css"></script>
        </#if>

        <label class="label">${element.properties.label} <span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
        <select id="${elementParamName!}" name="${elementParamName!}" style="display:none" size="3" <#if element.properties.multiple! == 'true'>multiple</#if> class="multiselect_${element.properties.elementUniqueKey!} <#if error??>form-error-cell</#if>">
            <#list options as option>
                <#if option.value! != ''>
                    <option value="${option.value!?html}" grouping="${option.grouping!?html}" <#if values?? && values?seq_contains(option.value!)>selected</#if>>${option.label!?html}</option>
                <#else>
                    <option value=""></option>
                </#if>
            </#list>
        </select>

        <script type="text/javascript">
            $(document).ready(function(){
                $(".multiselect_${element.properties.elementUniqueKey!}").multiselect({
                    width: "${element.properties.width!}",
                    readOnly: "${element.properties.readonly!}",
                    paramName : "${elementParamName!}",
                    controlField: "${element.properties.controlFieldParamName!}",
                    nonce : "${element.properties.nonce!}",
                    binderData : "${element.properties.binderData!}",
                    appId : "${element.properties.appId!}",
                    appVersion : "${element.properties.appVersion!}",
                    contextPath : "${request.contextPath}"
                });
            });
        </script>
    </#if>
</div>
