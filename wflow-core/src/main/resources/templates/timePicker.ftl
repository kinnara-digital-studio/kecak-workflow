<div class="form-cell" ${elementMetaData!}>
<#if element.properties.readonly! != 'true'>
    <#if !(request.getAttribute("org.joget.plugin.enterprise.TimePicker_EDITABLE")??) >
        <link rel="stylesheet" href="${request.contextPath}/plugin/org.joget.plugin.enterprise.TimePicker/css/jquery.timeselector.css" />
        <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.TimePicker/js/jquery.timeselector.js"></script>
    </#if>
<script type="text/javascript">
    $(document).ready(function() {
        $("#${elementParamName!}_${element.properties.elementUniqueKey!}").timeselector(<#if element.properties.format24hr! == 'true'>{"hours12": false}</#if>);
    });
</script>
</#if>
    <label class="label">${element.properties.label} <span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
    <#if (element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true') >
        <span>${value!?html}</span>
        <input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" />
    <#else>
        <input id="${elementParamName!}_${element.properties.elementUniqueKey!}" name="${elementParamName!}" type="text" value="${value!?html}" class="${elementParamName!} <#if error??>form-error-cell</#if>"  <#if element.properties.readonly! == 'true'>readonly</#if> />
    </#if>
</div>
