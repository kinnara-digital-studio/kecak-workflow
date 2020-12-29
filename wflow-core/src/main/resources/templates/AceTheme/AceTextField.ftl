<div class="form-cell form-group control-group <#if error??>has-error</#if>" ${elementMetaData!}>
    <label class="control-label">
    	${element.properties.label} 
    	<span class="form-cell-validator">${decoration}</span>
	</label>
	
	<div class="controls">
	    <#if (element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true') >
	        <div class="form-cell-value">
	        	<span>${value!?html}</span>
	        </div>
	        <input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" class="form-control" />
	    <#else>
	        <input id="${elementParamName!}" name="${elementParamName!}" type="text" class="form-control" size="${element.properties.size!}" placeholder="${element.properties.placeholder!}" value="${value!?html}" maxlength="${element.properties.maxlength!}" <#if error??>class="form-error-cell"</#if> <#if element.properties.readonly! == 'true'>readonly</#if> />
	    </#if>
	    <#if error??> <span class="form-error-message help-block">${error}</span></#if>
    </div>
</div>