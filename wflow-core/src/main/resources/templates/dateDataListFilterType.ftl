<script type="text/javascript" src="${contextPath}/plugin/org.joget.apps.datalist.lib.TextFieldDataListFilterType/js/jquery.placeholder.min.js"></script>
<#if request.getAttribute("currentLocale")!?starts_with("zh") >
    <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/js/jquery.ui.datepicker-zh-CN.js"></script>
</#if>
<input id="${name!}" name="${name!}" type="text" size="10" value="${value!?html}" readonly="readonly" class="datepicker" placeholder="${label!?html}"/>
<script type="text/javascript">
    $(document).ready(function(){
        $('#${name!}').datepicker({
            showOn: "button",
            buttonImage: "${contextPath}/css/images/calendar.png",
            buttonImageOnly: true,
            changeMonth: true,
            changeYear: true
            <#if element.properties.format! != ''>
                ,dateFormat: "${element.properties.format}"
            </#if>
            <#if element.properties.yearRange! != ''>
                ,yearRange: "${element.properties.yearRange}"
            </#if>
        });
        $('#${name!}').placeholder();
        //reset to empty when click
        $('#${name!}').click(function(){
            $(this).val('');
        });
    });
</script>