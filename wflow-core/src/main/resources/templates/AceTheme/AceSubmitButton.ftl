<#if element.properties.readonly! != 'true'><input id="${elementParamName!}" name="${elementParamName!}" class="form-button btn btn-primary" type="submit" value="${element.properties.label!?html}" <#if element.properties.disabled! == 'true'>disabled</#if> ${elementMetaData!}/></#if>