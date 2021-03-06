<div class="form-cell form-group control-group <#if error??>has-error</#if>" ${elementMetaData!}>
    <label class="control-label">
    	${element.properties.label} 
    	<span class="form-cell-validator">${decoration}</span>
	</label>
	<div class="controls">
    	<div class="form-cell-value" id="${elementParamName!}${element.properties.elementUniqueKey!}">
	    <#list options as option>
	        <#if (element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true') >
	            <#if value?? && value == option.value!>
	                <label class="readonly_label">
	                    <span>${option.label!?html}</span>
	                </label>
	            </#if>
	        <#else>
	            <div class="radio">
	                <label>
	                    <input class="ace" grouping="${option.grouping!?html}" <#if element.properties.readonly! != 'true'>id="${elementParamName!}"</#if> name="${elementParamName!}" type="radio" value="${option.value!?html}" <#if error??>class="form-error-cell"</#if> <#if element.properties.readonly! == 'true'> disabled</#if> <#if value?? && value == option.value!>checked</#if> />
	                    <span class="lbl"> ${option.label!?html}</span>
	                </label>
	            </div>
	        </#if>
	    </#list>
	        <#if element.properties.readonly! == 'true'><input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!}" /></#if>
	    </div>
	    <#if error??> <span class="form-error-message help-block">${error}</span></#if>
	    <div style="clear:both;"></div>
    </div>

    <#if (element.properties.controlField?? && element.properties.controlField! != "" && !(element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true')) >
        <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.apps.form.lib.SelectBox/js/jquery.dynamicoptions.js"></script>
        <script type="text/javascript">
            $(document).ready(function(){
                $("#${elementParamName!}${element.properties.elementUniqueKey!}").dynamicOptions({
                    controlField : "${element.properties.controlFieldParamName!}",
                    paramName : "${elementParamName!}",
                    type : "radio",
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