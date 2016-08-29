<script src="${request.contextPath}/js/JSONError.js"></script>
<script src="${request.contextPath}/js/JSON.js"></script>
<script src="${request.contextPath}/pbuilder/js/jquery.jsPlumb-1.6.4-min.js"></script>
<script src="${request.contextPath}/pbuilder/js/html2canvas-0.4.1.js"></script>
<script src="${request.contextPath}/pbuilder/js/jquery.plugin.html2canvas.js"></script>        
<script src="${request.contextPath}/pbuilder/js/rgbcolor.js"></script> 
<script src="${request.contextPath}/pbuilder/js/StackBlur.js"></script>
<script src="${request.contextPath}/pbuilder/js/canvg.js"></script>
<script src="${request.contextPath}/pbuilder/js/jquery.format.js"></script> 
<script src="${request.contextPath}/js/jquery/jquery.propertyeditor.js"></script>
<script src="${request.contextPath}/js/json/util.js"></script>
<script src="${request.contextPath}/pbuilder/js/undomanager.js"></script> 
<script src="${request.contextPath}/pbuilder/js/pbuilder.js"></script>
<script src="${request.contextPath}/js/jquery.qtip/jquery.qtip.min.js"></script>
<link href="${request.contextPath}/js/jquery.qtip/jquery.qtip.min.css" rel="stylesheet" type="text/css" />
<script src="${request.contextPath}/plugin/org.joget.plugin.enterprise.ProcessStatusMenu/js/dagre.min.js"></script>
<script src="${request.contextPath}/plugin/org.joget.plugin.enterprise.ProcessStatusMenu/js/processStatus.js"></script>
<link href="${request.contextPath}/plugin/org.joget.plugin.enterprise.ProcessStatusMenu/css/processStatus.css" rel="stylesheet" type="text/css" />

<div class="process-status-body-content">
    ${element.properties.customHeader!}

    <#if data??>
        <script>
            $(document).ready(function(){
                var ps = new ProcessStatus(${data!}, $("#process-status-canvas"), {
                    "start.activity" : "@@userview.processStatus.start.activity@@",
                    "node.status" : "@@userview.processStatus.node.status@@",
                    "node.assignees" : "@@userview.processStatus.node.assignees@@",
                    "node.createdTime" : "@@userview.processStatus.node.createdTime@@",
                    "node.finishedTime" : "@@userview.processStatus.node.finishedTime@@",
                    "node.serviceLevelMonitor" : "@@userview.processStatus.node.serviceLevelMonitor@@",
                    "node.dueDate" : "@@userview.processStatus.node.dueDate@@",
                    "node.delay" : "@@userview.processStatus.node.delay@@",
                    "status.running" : "@@userview.processStatus.status.running@@",
                    "status.completed" : "@@userview.processStatus.status.completed@@",
                    "status.aborted" : "@@userview.processStatus.status.aborted@@",
                    "status.terminated" : "@@userview.processStatus.status.terminated@@"
                },{
                    "showProcessName" : "${element.properties.showProcessName!}"
                });
                $("#xpdls textarea").each(function(){
                    ps.loadXpdl($(this).attr("id"), $(this).val());
                });
                $("#xpdls").remove();  
                ps.draw();
            });
        </script>
        <div id="process-status-canvas"></div>
        <div id="xpdls" style="display:none;">
            <#list xpdls?keys as xpdl>
                <textarea id="${xpdl!}">${xpdls[xpdl]?html}</textarea>
            </#list>
        </div>
    <#else> 
        <h3>@@userview.processStatus.noData@@</h3>
    </#if>

    ${element.properties.customFooter!}
</div>
