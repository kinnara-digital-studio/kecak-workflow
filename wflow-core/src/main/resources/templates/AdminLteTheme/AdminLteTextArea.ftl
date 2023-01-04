<div class="form-cell form-group <#if error??>has-error</#if>" ${elementMetaData!}>
    <label>${element.properties.label} <span class="form-cell-validator">${decoration}</span></label>
    <#if (element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true') >
        <div class="form-cell-value">
            <div>${value!?html?replace("\n", "<br>")}</div>
            <input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" />
        </div>
        <div style="clear:both;"></div>
    <#else>
        <textarea id="${elementParamName!}" class="form-control" name="${elementParamName!}" cols="${element.properties.cols!}"  rows="${element.properties.rows!}" placeholder="${element.properties.placeholder!}" style="width: 95%;" <#if error??>class="form-error-cell"</#if> <#if element.properties.readonly! == 'true'>readonly</#if>>${value!?html}</textarea>
    </#if>
    <#if error??> <span class="form-error-message help-block">${error}</span></#if>
</div>
