<div class="form-cell" ${elementMetaData!}>
	<link rel="stylesheet" href="${request.contextPath}/plugin/org.joget.apps.form.lib.SelectBox/css/chosen.min.css">
	<script src="${request.contextPath}/plugin/org.joget.apps.form.lib.SelectBox/js/chosen.jquery.min.js" type="text/javascript"></script>
	
    <label class="label">${element.properties.label} <span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
    <#if (element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true') >
        <div class="form-cell-value">
            <#list options as option>
                <#if values?? && values?seq_contains(option.value!)>
                    <label class="readonly_label">
                        <span>${option.label!?html}</span>
                    </label>
                </#if>
            </#list>
        </div>
        <div style="clear:both;"></div>
    <#else>
        <select class="chosen-select" <#if element.properties.readonly! != 'true'>id="${elementParamName!}${element.properties.elementUniqueKey!}"</#if> name="${elementParamName!}" <#if element.properties.multiple! == 'true'>multiple</#if> <#if error??>class="form-error-cell"</#if> <#if element.properties.readonly! == 'true'> disabled </#if>>
            <#list options as option>
                <option value="${option.value!?html}" grouping="${option.grouping!?html}" <#if values?? && values?seq_contains(option.value!)>selected</#if> <#if element.properties.readonly! == 'true'>disabled</#if>>${option.label!?html}</option>
            </#list>
        </select>
    </#if>
    <#if element.properties.readonly! == 'true'>    
        <#list values as value>
            <input type="hidden" id="${elementParamName!}" name="${elementParamName!}" value="${value?html}" />
        </#list>
    </#if>

    <#if (element.properties.controlField?? && element.properties.controlField! != "" && !(element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true')) >
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
		        var config = {
				  '#${elementParamName!}${element.properties.elementUniqueKey!}.chosen-select'           : {},
				  '#${elementParamName!}${element.properties.elementUniqueKey!}.chosen-select-deselect'  : {allow_single_deselect:true},
				  '#${elementParamName!}${element.properties.elementUniqueKey!}.chosen-select-no-single' : {disable_search_threshold:10},
				  '#${elementParamName!}${element.properties.elementUniqueKey!}.chosen-select-no-results': {no_results_text:'Oops, nothing found!'},
				  '#${elementParamName!}${element.properties.elementUniqueKey!}.chosen-select-width'     : {width:"95%"}
				}
				
				for (var selector in config) {
				  $(selector).chosen({width : "${element.properties.size!}%"});
				}
			});
	    </script>
	</#if>
</div>
