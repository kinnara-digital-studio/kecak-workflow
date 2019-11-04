<div id="${elementParamName!}" class="form-section <#if !(element.properties.label?? && element.properties.label != "") >no_label</#if> section_${element.properties.elementUniqueKey!}" ${elementMetaData!} <#if visible == false && includeMetaData == false>style="display: none"</#if>>
    <div class="box-header with-border">
        <#if element.properties.label?? && element.properties.label != ""><h3 class="box-title">${element.properties.label!}</h3></#if>
    </div>
    <div class="box-body">
        <#list element.children as e>
            ${e.render(formData, includeMetaData!false)}
        </#list>
    </div>

<#if visibilityControlParam?? && includeMetaData == false>
<script type="text/javascript" src="${request.contextPath}/plugin/org.joget.apps.form.model.Section/js/section.js"></script>
<script type="text/javascript">
    $(document).ready(function() {
        new VisibilityMonitor($('.section_${element.properties.elementUniqueKey!}'), "${visibilityControlParam!}", "${element.properties.visibilityValue!}", "${element.properties.regex!}").init();
    });
</script>
</#if>
<#if includeMetaData == false>
<div style="clear:both"></div>
</#if>
</div>
