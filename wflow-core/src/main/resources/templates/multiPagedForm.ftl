<div class="form-cell" ${elementMetaData!}>
    <#if !(request.getAttribute("org.joget.plugin.enterprise.MultiPagedForm")??) >
        <link href="${request.contextPath}/plugin/org.joget.plugin.enterprise.MultiPagedForm/css/multiPagedForm.css" rel="stylesheet" type="text/css" />
        <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.plugin.enterprise.MultiPagedForm/js/jquery.multiPagedForm.js"></script>
    </#if>
    
    <#if element.properties.css??>
        <style type="text/css">
            ${element.properties.css!}
        </style>
    </#if>
    <div id="${elementParamName!}_multiPagedForm_${element.properties.elementUniqueKey!}" class="form-element multiPagedForm ${element.properties.displayMode!}">
        
        <input type="hidden" class="cPageNum" name="${elementParamName!}_c_page_num" value="${cPageNum!?html}" />
        <input type="hidden" class="changePage" name="${elementParamName!}_change_page" value="" />

        <div class="page-nav-panel top">
            <ul>
            <#assign mobileCss="before">
            <#list element.children as e>
                <#if e.className == "org.joget.plugin.enterprise.MultiPagedFormChild" >
                    <#assign errorCss="">
                    <#if e.hasError(formData) >
                        <#assign errorCss="error">
                    </#if>
                    <#if cPageNum! == e.properties.pageNum!>
                        <#assign mobileCss="after">
                        <li class="nav_item current ${errorCss}"><button disabled><span>${e.properties.label!?html}</span></button></li>
                    <#else>
                        <li class="nav_item ${errorCss} ${mobileCss}"><button <#if elementMetaData! != "">disabled</#if> rel="${e.properties.pageNum!}"><span>${e.properties.label!?html}</span></button></li>
                    </#if>
                </#if>
            </#list>
            </ul>
            <div style="clear:both;"></div>
        </div>

        <div class="page-container">
            <#list element.children as e>
                <#if e.className == "org.joget.plugin.enterprise.MultiPagedFormChild" >
                    <#if cPageNum! == e.properties.pageNum!> 
                        <div class="page_${e.properties.pageNum!} current">
                            ${e.render(formData, includeMetaData!false)}
                        </div>
                    <#else>
                        <div class="page_${e.properties.pageNum!}" style="display:none;">
                            ${e.render(formData, includeMetaData!false)}
                        </div>
                    </#if>
                <#else>
                    <div class="page_key_container" style="display:none;">
                        ${e.render(formData, includeMetaData!false)}
                    </div>
                </#if>
            </#list>
        </div>

        <div class="page-nav-panel bottom">
            <ul>
            <#assign mobileCss="before">
            <#list element.children as e>
                <#if e.className == "org.joget.plugin.enterprise.MultiPagedFormChild" >
                    <#assign errorCss="">
                    <#if e.hasError(formData) >
                        <#assign errorCss="error">
                    </#if>
                    <#if cPageNum! == e.properties.pageNum!>
                        <#assign mobileCss="after">
                        <li class="nav_item current ${errorCss}"><button disabled><span>${e.properties.label!?html}</span></button></li>
                    <#else>
                        <li class="nav_item ${errorCss} ${mobileCss}"><button <#if elementMetaData! != "">disabled</#if> rel="${e.properties.pageNum!}"><span>${e.properties.label!?html}</span></button></li>
                    </#if>
                </#if>
            </#list>
            </ul>
            <div style="clear:both;"></div>
        </div>

        <div class="page-button-panel">
            <input type="submit" class="page-button-prev" name="${elementParamName!}_prev_page" value="${element.properties.prevButtonlabel!?html}" <#if cPageNum! == "1">disabled</#if> />
            <input type="submit" class="page-button-next" name="${elementParamName!}_next_page" value="${element.properties.nextButtonlabel!?html}" <#if elementMetaData! != "">disabled</#if> <#if cPageNum! == element.properties.totalPage!>disabled</#if>/>
        </div>

    </div>
    <script type="text/javascript">
        $(document).ready(function(){
            $("#${elementParamName!}_multiPagedForm_${element.properties.elementUniqueKey!}").multiPagedForm();
        });
    </script>
</div>