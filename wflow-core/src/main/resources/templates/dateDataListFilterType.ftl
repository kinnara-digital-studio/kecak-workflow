<link rel="stylesheet" href="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/node_modules/bootstrap-daterangepicker/daterangepicker.css">
<link rel="stylesheet" href="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/node_modules/bootstrap-datepicker/dist/css/bootstrap-datepicker.min.css">
<script src="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/node_modules/moment/min/moment.min.js"></script>
<script src="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/node_modules/bootstrap-daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/node_modules/bootstrap-datepicker/dist/js/bootstrap-datepicker.min.js"></script>

<script type="text/javascript" src="${contextPath}/plugin/org.joget.apps.datalist.lib.TextFieldDataListFilterType/js/jquery.placeholder.min.js"></script>
<#if request.getAttribute("currentLocale")!?starts_with("zh") >
    <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/js/jquery.ui.datepicker-zh-CN.js"></script>
</#if>
<div class="input-group date">
    <div class="input-group-addon">
        <i class="fa fa-calendar"></i>
    </div>
    <#if (element.properties.allowManual! == 'true')>
        <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/js/jquery.placeholder.min.js"></script>
    </#if>
    <input id="${name!}" name="${name!}" type="text" size="10" value="${value!?html}" class="datepicker form-control" readonly="readonly" placeholder="${label!?html}"/>
</div>
<script type="text/javascript">
    $(document).ready(function(){

        $('#${name!}').datepicker({
            autoclose:true
            <#if element.properties.format! != ''>
            ,format: "${element.properties.format}"
            </#if>
        });
        /*
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
        */
        $('#${name!}').placeholder();
        //reset to empty when click
        $('#${name!}').click(function(){
            $(this).val('');
        });
    });
</script>