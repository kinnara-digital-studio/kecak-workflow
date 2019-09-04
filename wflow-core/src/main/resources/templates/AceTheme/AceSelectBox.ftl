<div class="form-cell form-group <#if error??>has-error</#if>" ${elementMetaData!}>
    <script type="text/javascript" src="${request.contextPath}/plugin/${className}/bower_components/select2/dist/js/select2.min.js"></script>
    <link rel="stylesheet" href="${request.contextPath}/plugin/${className}/bower_components/select2/dist/css/select2.min.css">
    <label class="col-sm-3 control-label">${element.properties.label} <span class="form-cell-validator">${decoration}</span></label>
    <div class="col-sm-9">
        <#if (element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true') >
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
            <select style="width:${element.properties.size}%" <#if element.properties.readonly! != 'true'>id="${elementParamName!}${element.properties.elementUniqueKey!}"</#if> name="${elementParamName!}" <#if element.properties.multiple! == 'true'>multiple</#if> class="form-control <#if error??>form-error-cell</#if>" <#if element.properties.readonly! == 'true'> disabled </#if>>
                <#if element.properties.lazyLoading! != 'true' >
                    <#list options! as option>
                        <option value="${option.value!?html}" grouping="${option.grouping!?html}" <#if values?? && values?seq_contains(option.value!)>selected</#if>>${option.label!?html}</option>
                    </#list>
                <#else>
                    <#list options! as option>
                        <#if values?? && values?seq_contains(option.value!) || option.value == ''>
                            <option value="${option.value!?html}" grouping="${option.grouping!?html}" <#if values?? && values?seq_contains(option.value!)>selected</#if>>${option.label!?html}</option>
                        </#if>
                    </#list>
                </#if>
            </select>
        </#if>
        <#if element.properties.readonly! == 'true'>
            <#list values as value>
                <input type="hidden" id="${elementParamName!}" name="${elementParamName!}" value="${value?html}" />
            </#list>
        </#if>
    </div>
    <#if error??> <span class="form-error-message help-block">${error}</span></#if>
    <#if element.properties.controlField?? && element.properties.controlField! != "" && !(element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true') >
        <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.apps.form.lib.SelectBox/js/jquery.dynamicoptions.js"></script>
        <script type="text/javascript">
            $(document).ready(function(){
                $("#${elementParamName!}${element.properties.elementUniqueKey!}").dynamicOptions({
                    controlField : "${element.properties.controlFieldParamName!}",
                    paramName : "${elementParamName!}",
                    type : "selectbox",
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

    <#if element.properties.modernStyle! == 'true' >
        <script type="text/javascript">
            $(document).ready(function(){
                $('#${elementParamName!}${element.properties.elementUniqueKey!}').select2({
                    placeholder: '${element.properties.placeholder!}',
                    language : {
                       errorLoading: () => '${element.properties.messageErrorLoading!}',
                       loadingMore: () => '${element.properties.messageLoadingMore!}',
                       noResults: () => '${element.properties.messageNoResults!}',
                       searching: () => '${element.properties.messageSearching!}'
                    }

                    <#if element.properties.lazyLoading! == 'true' >
                        ,ajax: {
                            url: '${request.contextPath}/web/json/plugin/${className}/service',
                            delay : 500,
                            dataType: 'json',
                            data : function(params) {
                                return {
                                    search: params.term,
                                    appId : '${appId!}',
                                    appVersion : '${appVersion!}',
                                    formDefId : '${formDefId!}',
                                    fieldId : '${element.properties.id!}',
                                    <#if element.properties.controlField! != '' >
                                        grouping : FormUtil.getValue('${element.properties.controlField!}'),
                                    </#if>
                                    page : params.page || 1
                                };
                            }
                        }
                    </#if>
                });
            });
        </script>
    </#if>
</div>