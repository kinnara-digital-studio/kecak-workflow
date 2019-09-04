<div class="form-cell form-group <#if error??>has-error</#if>" ${elementMetaData!}>
    <label class="col-sm-3 control-label">${element.properties.label} <span class="form-cell-validator">${decoration}</span></label>
    <div class="col-sm-9">
        <#if (element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true') >
            <span>*************</span>
            <input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" />
        <#else>
            <input id="${elementParamName!}" name="${elementParamName!}" type="password" size="${element.properties.size!}" value="${value!?html}" maxlength="${element.properties.maxlength!}" class="form-control <#if error??>form-error-cell</#if>" <#if element.properties.readonly! == 'true'>readonly</#if> />
        </#if>
    </div>
    <#if error??> <span class="form-error-message help-block">${error}</span></#if>
</div>