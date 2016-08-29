<#if type! == 'autocomplete'>
    <script type="text/javascript" src="${contextPath}/plugin/org.joget.apps.datalist.lib.TextFieldDataListFilterType/js/jquery.placeholder.min.js"></script>
    
    <input id="${name!}_display" type="text" size="10" <#if value??> value="${options[value]!?html}"</#if> placeholder="${label!?html}">
    <input type="hidden" id="${name!}" name="${name!}" value="${value!?html}">

    <script>
        $(function() {
            $('#${name!}_display').placeholder();
            
            var ${name!?replace("-", "")}Options = [
                <#list options?keys as key>
                    { value: '${key!?js_string}', label : '${options[key]!?js_string}' } <#if key_has_next>,</#if>
                </#list>
            ];

            $( "#${name!}_display" ).autocomplete({
                minLength: 0,
                source: ${name!?replace("-", "")}Options,
                focus: function( event, ui ) {
                    $( "#${name!}_display" ).val( ui.item.label );
                    return false;
                },
                select: function( event, ui ) {
                    $( "#${name!}_display" ).val( ui.item.label );
                    $( "#${name!}" ).val( ui.item.value );

                    return false;
                }
            })
            .data( "ui-autocomplete" )._renderItem = function( ul, item ) {
                return $( "<li>" )
                    .append( "<a>" + item.label + "</a>" )
                    .appendTo( ul );
            };

            $( "#${name!}_display" ).change(function(){
                if($(this).val() == "") {
                    $( "#${name!}" ).val("");
                }
            });
        });
    </script>
<#else>
    <select id="${name!}" name="${name!}" <#if element.properties.size?? && element.properties.size != ''> size="${element.properties.size!}"</#if> <#if element.properties.multiple?? && element.properties.multiple == 'true'> multiple="true"</#if>>
        <option value="" style="color:gray;">${label!?html}</option>
        <#list options?keys as key>
             <option value="${key?html}" <#if values?? && values?seq_contains(key)>selected</#if> style="color:black;">${options[key]!?html}</option>
        </#list>
    </select>
    <script type="text/javascript">
        $(document).ready(function(){
            if($("#${name!}").val() == "") {
                $("#${name!}").css("color", "gray");
            }

            $("#${name!}").change(function(){
                if($(this).val() == "") {
                    $(this).css("color", "gray");
                } else {
                    $(this).css("color", "inherit");
                }
            });
        });
    </script>
</#if>