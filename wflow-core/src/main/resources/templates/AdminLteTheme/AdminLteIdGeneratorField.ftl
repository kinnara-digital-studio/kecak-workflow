<#if includeMetaData>
    <div class="form-cell" ${elementMetaData!}>
        <#if element.properties.hidden! == 'true'>
            <span class="form-floating-label show">@@form.idgeneratorfield.idGeneratorField@@</span>
        <#else>
            <label>${element.properties.label}</label>
            <span class="show">@@form.idgeneratorfield.auto@@</span>
        </#if>
    </div>
<#else>
    <#if element.properties.hidden! == 'true'>
        <#if value?? ><input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" /></#if>
    <#else>
        <div class="form-cell" ${elementMetaData!}>
            <label>${element.properties.label}</label>
            <span class="show">${value!?html}<#if !value?? || value == ''>@@form.idgeneratorfield.auto@@</#if></span>
            <#if value?? ><input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" /></#if>
        </div>
    </#if>
</#if>
