<div class="form-cell" ${elementMetaData!}>
    <#if includeMetaData!>
        <span class="form-floating-label">@@form.calculationfield.calculationfield@@</span>
    <#else>
        <#if !(request.getAttribute("org.joget.plugin.enterprise.CalculationField")??) >
            <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.CalculationField/js/jquery.calculation.js"></script>
            <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.CalculationField/js/jquery.calculationfield.js"></script>
        </#if>        
        <script type="text/javascript">
            var options_calField_${element.properties.elementUniqueKey} = ${configJson!};
            $(document).ready(function(){
                $('.calFiel_${element.properties.elementUniqueKey!}').calculationField(options_calField_${element.properties.elementUniqueKey});
            });
        </script>
    </#if>
    <#if element.properties.hidden! == 'true'>
        <#if includeMetaData!>
            <label class="label">${element.properties.id}</label>
            <input id="${elementParamName!}" name="${elementParamName!}" type="text" value="${value!?html}" readonly/>
        <#else>
            <input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" class="calFiel_${element.properties.elementUniqueKey!}"/>
        </#if>
    <#else>
        <label class="label">${element.properties.label} <span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
        <#if element.properties.readonlyLabel! == 'true' >
            <span class="calculationField_value">${value!?html}</span>
            <input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" class="calFiel_${element.properties.elementUniqueKey!}"/>
        <#else>
            <input id="${elementParamName!}" name="${elementParamName!}" type="text" value="${value!?html}" readonly class="calFiel_${element.properties.elementUniqueKey!}"/>
        </#if>
    </#if>
</div>