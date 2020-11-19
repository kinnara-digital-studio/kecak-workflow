<#if includeMetaData>
    <div class="form-cell form-group control-group" ${elementMetaData!}>
        <#if element.properties.hidden! == 'true'>
            <span class="form-floating-label form-control">@@form.idgeneratorfield.idGeneratorField@@</span>
        <#else>
            <label class="control-label">${element.properties.label}</label>
            <div class="controls">
            	<span class="form-control">@@form.idgeneratorfield.auto@@</span>
            </div>
        </#if>
    </div>
<#else>
    <#if element.properties.hidden! == 'true'>
        <#if value?? ><input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" /></#if>
    <#else>
        <div class="form-cell form-group control-group" ${elementMetaData!}>
            <label class="control-label">${element.properties.label}</label>
            <div class="controls">
            <span class="form-control">${value!?html}<#if !value?? || value == ''>@@form.idgeneratorfield.auto@@</#if></span>
            <#if value?? ><input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" /></#if>
        	</div>
        </div>
    </#if>
</#if>
