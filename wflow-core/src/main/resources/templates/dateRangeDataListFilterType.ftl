<script type="text/javascript" src="${contextPath}/plugin/org.joget.apps.datalist.lib.TextFieldDataListFilterType/js/jquery.placeholder.min.js"></script>
<#if request.getAttribute("currentLocale")!?starts_with("zh") >
    <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/js/jquery.ui.datepicker-zh-CN.js"></script>
</#if>
<#if element.properties.showFieldLabel! == 'true'>
    <span class="dateRangeLabel">${label!?html}</span>
</#if>
<input id="${name!}_from" name="${name!}" type="text" size="10" value="${value1!?html}" readonly="readonly" class="datepicker from" placeholder="${element.properties.fromLabel!?html}"/>
<span class="dateRangeSeparator"> - </span>
<input id="${name!}_to" name="${name!}" type="text" size="10" value="${value2!?html}" readonly="readonly" class="datepicker to" placeholder="${element.properties.toLabel!?html}"/>
<#assign newName = name!?replace("-", "") >

<script type="text/javascript">
    $(document).ready(function(){
        $('#${name!}_from, #${name!}_to').datepicker({
            showOn: "button",
            buttonImage: "${contextPath}/css/images/calendar.png",
            buttonImageOnly: true,
            changeMonth: true,
            changeYear: true,
            onSelect: function( selectedDate ) {
                ${newName!}_limit_date_range(this, selectedDate);
            }
            <#if element.properties.format! != ''>
                ,dateFormat: "${element.properties.format!}"
            </#if>
            <#if element.properties.yearRange! != ''>
                ,yearRange: "${element.properties.yearRange}"
            </#if>            
        });
        $('#${name!}_from, #${name!}_to').placeholder();
        
        //reset to empty when click
        $('#${name!}_from, #${name!}_to').click(function(){
            $(this).val('');
            ${newName!}_limit_date_range(this, '');
        });

        //set limit when value is not empty
        <#if value1??> 
            ${newName!}_limit_date_range($('#${name!}_from'), '${value1!}');
        </#if>
        <#if value2??> 
            ${newName!}_limit_date_range($('#${name!}_to'), '${value2!}');
        </#if>
    });

    function ${newName!}_limit_date_range(element, selectedDate){
        try {
            var option = $(element).hasClass("from") ? "minDate" : "maxDate";
            var instance = $(element).data( "datepicker" );
            var date = null;
            if (selectedDate != '') {
                date = $.datepicker.parseDate(
                        instance.settings.dateFormat ||
                    $.datepicker._defaults.dateFormat
                    ,selectedDate, instance.settings );
            }
            $("#${name!}_from, #${name!}_to").not(element).datepicker( "option", option, date );
        } catch(err) {}    
    }
</script>