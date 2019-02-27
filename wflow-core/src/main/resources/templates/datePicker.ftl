<div class="form-cell form-group <#if error??>has-error</#if>" ${elementMetaData!}>
    <#if element.properties.readonly! != 'true'>
        <#if !(request.getAttribute("org.joget.apps.form.lib.DatePicker_EDITABLE")??) >
            <link rel="stylesheet" href="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/bower_components/bootstrap-daterangepicker/daterangepicker.css">
            <link rel="stylesheet" href="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/bower_components/bootstrap-datepicker/dist/css/bootstrap-datepicker.min.css">
            <script src="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/bower_components/moment/min/moment.min.js"></script>
            <script src="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/bower_components/bootstrap-daterangepicker/daterangepicker.js"></script>
            <script src="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/bower_components/bootstrap-datepicker/dist/js/bootstrap-datepicker.min.js"></script>
            <#if request.getAttribute("currentLocale")!?starts_with("zh") >
                <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/js/jquery.ui.datepicker-zh-CN.js"></script>
            </#if>
        </#if>
    <script type="text/javascript">
        $(document).ready(function() {
            $("#${elementParamName!}_${element.properties.elementUniqueKey!}").datepicker({
                autoclose:true
                <#if element.properties.format! != ''>
                ,format: "${element.properties.format}"
                </#if>
            });
            /*
            $("#${elementParamName!}_${element.properties.elementUniqueKey!}").cdatepicker({
                            showOn: "button",
                            buttonImage: "${request.contextPath}/css/images/calendar.png",
                            buttonImageOnly: true,
                            changeMonth: true,
                            changeYear: true
                            <#if element.properties.format! != ''>
                            ,dateFormat: "${element.properties.format}"
                            </#if>
                            <#if element.properties.yearRange! != ''>
                            ,yearRange: "${element.properties.yearRange}"
                            </#if>
                            <#if element.properties.startDateFieldId! != ''>
                            ,startDateFieldId: "${element.properties.startDateFieldId}"
                            </#if>
                            <#if element.properties.endDateFieldId! != ''>
                            ,endDateFieldId: "${element.properties.endDateFieldId}"
                            </#if>
                            <#if element.properties.currentDateAs! != ''>
                            ,currentDateAs: "${element.properties.currentDateAs}"
                            </#if>
            });
            */
        });
    </script>
    </#if>
    <label class="label">${element.properties.label} <span class="form-cell-validator">${decoration}</span></label>
    <#if (element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true') >
        <span>${value!?html}</span>
        <input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" />
    <#else>
        <div class="input-group date">
            <div class="input-group-addon">
                <i class="fa fa-calendar"></i>
            </div>
            <#if (element.properties.allowManual! == 'true')>
                <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/js/jquery.placeholder.min.js"></script>
            </#if>
            <input id="${elementParamName!}_${element.properties.elementUniqueKey!}" name="${elementParamName!}" type="text" size="${element.properties.size!}" value="${value!?html}" class="datepicker form-control ${elementParamName!} <#if error??>form-error-cell</#if>" <#if (element.properties.allowManual! != 'true' || element.properties.readonly! == 'true')>readonly</#if> <#if (element.properties.allowManual! == 'true')>placeholder="${displayFormat!?html}"</#if> />
        </div>
    </#if>
    <#if error??> <span class="form-error-message help-block">${error}</span></#if>
</div>
