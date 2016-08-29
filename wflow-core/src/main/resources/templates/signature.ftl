<div class="form-cell" ${elementMetaData!}>
    
    <label class="label">${element.properties.label} <span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
    <div class="form-cell-value sigPad" style="width:${width!}px;" >
        <link rel="stylesheet" href="${request.contextPath}/plugin/org.joget.plugin.enterprise.Signature/js/signature_pad/assets/jquery.signaturepad.css" />
        <#if (element.properties.readonly! == 'true' && value??)>
            <img class="pad" src="${src!?html}" />
        <#else>
            <#if !(request.getAttribute("org.joget.plugin.enterprise.Signature_EDITABLE")??) >
                <script src="${request.contextPath}/plugin/org.joget.plugin.enterprise.Signature/js/signature_pad/jquery.signaturepad.min.js"></script>
                <script src="${request.contextPath}/plugin/org.joget.plugin.enterprise.Signature/js/signature_pad/assets/json2.min.js"></script>
                <!--[if lt IE 9]><script src="${request.contextPath}/plugin/org.joget.plugin.enterprise.Signature/js/signature_pad/assets/flashcanvas.js"></script><![endif]-->
            </#if>
            <div id="${elementParamName!}_sigPad" class="sigPad">
                <canvas class="pad" width="${width!}" height="${height!}"></canvas>
                <input id="${elementParamName!}" name="${elementParamName!}" type="hidden" class="output"/>
                <div class="clearButton"><a href="#clear">Clear</a></div>
            </div>
            <#if !includeMetaData >
                <script>
                    $(document).ready(function() {
                        ${elementParamName!}_sigPad();
                    });
                    // handle mobile page show
                    $(document).bind("pageshow", function() {
                        ${elementParamName!}_sigPad();
                    });
                    function ${elementParamName!}_sigPad(){
                        var ${elementParamName!}_sigPad = $('#${elementParamName!}_sigPad').signaturePad({
                            drawOnly:true,
                            validateFields: false,
                            defaultAction: "drawIt",
                            lineColour: "#fff"
                        });
                        <#if value??>
                            ${elementParamName!}_sigPad.regenerate(${value!});
                        </#if>
                    }
                </script>
            </#if>
        </#if>
    </div>
    <div class="form-clear"></div>
</div>