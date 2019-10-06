<div class="form-cell form-group <#if error??>has-error</#if>" ${elementMetaData!}>
    <label class="control-label">${element.properties.label} <span class="form-cell-validator">${decoration}</span></label>
    <div class="form-cell-value" id="${elementParamName!}${element.properties.elementUniqueKey!}">
    <#list options as option>
        <#if (element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true') >
            <#if values?? && values?seq_contains(option.value!)>
                <label class="readonly_label">
                    <span>${option.label!?html}</span>
                    <input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${option.value!?html}" />
                </label>
            </#if>
        <#else>
            <div class="checkbox">
                <label>
                    <input class="ace" grouping="${option.grouping!?html}" id="${elementParamName!}" name="${elementParamName!}" type="checkbox" value="${option.value!?html}" <#if error??>class="form-error-cell"</#if> <#if element.properties.readonly! == 'true'>  onclick="return false" </#if> <#if values?? && values?seq_contains(option.value!)>checked</#if> />
                    <span class="lbl"> ${option.label!?html}</span>
                </label>
            </div>
        </#if>
    </#list>
    </div>
    <#if error??> <span class="form-error-message help-block">${error}</span></#if>
    <div style="clear:both;"></div>

    <#if (element.properties.controlField?? && element.properties.controlField! != "" && !(element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true')) >
        <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.apps.form.lib.SelectBox/js/jquery.dynamicoptions.js"></script>
        <script type="text/javascript">
            $(document).ready(function(){
                $("#${elementParamName!}${element.properties.elementUniqueKey!}").dynamicOptions({
                    controlField : "${element.properties.controlFieldParamName!}",
                    paramName : "${elementParamName!}",
                    type : "checkbox",
                    readonly : "${element.properties.readonly!}",
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