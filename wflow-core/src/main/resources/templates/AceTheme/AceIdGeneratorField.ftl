<#if includeMetaData>
    <div class="form-cell form-group" ${elementMetaData!}>
        <#if element.properties.hidden! == 'true'>
            <div class="col-sm-12">
                <span class="form-floating-label form-control">@@form.idgeneratorfield.idGeneratorField@@</span>
            </div>
        <#else>
            <label class="col-sm-3 control-label">${element.properties.label}</label>
            <div class="col-sm-9">
                <span class="form-control">@@form.idgeneratorfield.auto@@</span>
            </div>
        </#if>
    </div>
<#else>
    <#if element.properties.hidden! == 'true'>
        <#if value?? ><input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" /></#if>
    <#else>
        <div class="form-cell form-group" ${elementMetaData!}>
            <label class="col-sm-3 control-label">${element.properties.label}</label>
            <div class="col-sm-9">
                <span class="form-control">${value!?html}<#if !value?? || value == ''>@@form.idgeneratorfield.auto@@</#if></span>
                <#if value?? ><input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" /></#if>
            </div>
        </div>
    </#if>
</#if>
