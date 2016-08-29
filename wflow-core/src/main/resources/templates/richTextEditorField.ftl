<div class="form-cell" ${elementMetaData!}>

<#if element.properties.readonly! != 'true'>

    <#if !(request.getAttribute("org.joget.plugin.enterprise.RichTextEditorField_EDITABLE")??) >
        <script type="text/javascript" src="${request.contextPath}/js/tiny_mce/jquery.tinymce.js"></script>
        <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.RichTextEditorField/js/jquery.richtext.js"></script>
        <link rel="stylesheet" href="${request.contextPath}/plugin/org.joget.plugin.enterprise.RichTextEditorField/css/jquery.richtext.css" />
    </#if>

    <label class="label">${element.properties.label!} <span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
    <div class="form-clear"></div>
    <textarea id="${elementParamName!}_${element.properties.elementUniqueKey!}" name="${elementParamName!}" style='height:300px;width:100%' class="tinymce ${elementParamName!} <#if error??>form-error-cell</#if>" <#if element.properties.readonly! == 'true'>readonly</#if>>${value!?html}</textarea>

<script type="text/javascript">
    $(document).ready(function() {
        $('#${elementParamName!}_${element.properties.elementUniqueKey!}').richtext({contextPath : '${request.contextPath}'});
    });
</script>

<#else>
    <label class="label">${element.properties.label!} <span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
    <#if includeMetaData>
        <span class="form-floating-label">@@form.richtexteditorfield.readonlyRichTextEditor@@</span>
    </#if>
    <div class="form-clear"></div>
    <div>${value!}</div>
</#if>
</div>