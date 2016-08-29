<#if element.properties.readonly! != 'true' >
    <div class="form-cell" ${elementMetaData!}>
        <label class="label">${element.properties.label!} <span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
        <div class="form-cell-value" id="${elementParamName!}${element.properties.elementUniqueKey!}">
            <img id="${elementParamName!}_img" src="${request.contextPath}/web/json/plugin/org.joget.plugin.enterprise.CaptchaField/service?_t=${timestamp}"/><br/>
            <input id="${elementParamName!}" name="${elementParamName!}" type="text" value="" <#if error??>class="form-error-cell"</#if> />
        </div>
    </div>
</#if>