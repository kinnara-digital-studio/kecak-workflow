<link href="${request.contextPath}/css/form.css?build=@@build.number@@" rel="stylesheet">
<#if element.properties.isPreview! == 'true' >
    <script>
        $(document).ready(function() {
            $(".form-button").attr("disabled", "disabled");
        });
    </script>
</#if>

<div class="viewCsvImport-body-content">
    <#if element.properties.customHeader! == '' >
        <div class="viewCsvImport-body-header">
            <h4>${element.properties.label!}</h4>
        </div>
    <#else>
        ${element.properties.customHeader!}
    </#if>
    <#if element.properties.view! == 'success'>
        <#if element.properties.messageOnSuccess! != '' >
            <script>
                alert("${element.properties.messageOnSuccess!}");
            </script>
        </#if>
        <#if element.properties.redirectUrl! != '' >
            <script>
                parent.location = "${element.properties.redirectUrl!}";
            </script>
        </#if>
        <div class="result">
            <style>
                .toggle{display:block; margin-top:5px;cursor:pointer;}
            </style>
            <#if element.properties.mode! == 'DELETE' >
                <a class="toggle">${successDeletedCount!} @@userview.csvimportmenu.recordsDeleted@@</a>
                <div style="display:none">
                    @@userview.csvimportmenu.rowNum@@<br/>
                    ${successDeletedRows!}
                </div>
            <#else>
                <a class="toggle">${successImportedCount!} @@userview.csvimportmenu.recordsImported@@</a>
                <div style="display:none">
                    @@userview.csvimportmenu.rowNum@@<br/>
                    ${successImportedRows!}
                </div>
                <#if successUpdatedCount! != 0 >
                    <a class="toggle">${successUpdatedCount!} @@userview.csvimportmenu.recordsUpdated@@</a>
                    <div style="display:none">
                        @@userview.csvimportmenu.rowNum@@<br/>
                        ${successUpdatedRows!}
                    </div>
                </#if>
            </#if>
            <#if skippedCount! != 0 >
                <a class="toggle">${skippedCount!} @@userview.csvimportmenu.recordsSkipped@@</a>
                <div style="display:none">
                    @@userview.csvimportmenu.rowNum@@<br/>
                    ${skippedRows!}
                </div>
            </#if>
            <#if validationErrorCount! != 0 >
                <a class="toggle">${validationErrorCount!} @@userview.csvimportmenu.recordsError@@</a>
                <div style="display:none">
                    <table cellspacing="0" style="width:100%;" class="table table-bordered">
                        <tbody>
                        <tr>
                            <th width="20%">@@userview.csvimportmenu.row@@</th>
                            <th>@@userview.csvimportmenu.errors@@</th>
                        </tr>
                        <#list validationErrorRows?keys as row>
                            <tr class="grid-row">
                                <td>${row}</td>
                                <td>
                                    ${validationErrorRows[row]}
                                </td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>    
            </#if>
        </div>
    <#elseif element.properties.view! == 'displayForm'>
        <fieldset id="form-canvas">
            <#if element.properties.error! == 'true' >
                <style>
                    .errors{color:red; margin-bottom:20px;}
                </style>
                <div class="errors">
                    <span>
                        <#if element.properties.messageOnError! != '' >
                            ${element.properties.messageOnError!}
                        <#else>
                            @@userview.csvimportmenu.error@@
                        </#if>
                    </span>
                </div>
            </#if>
            <form id="csvImportForm" method="post" action="${element.properties.url!}" class="form form-container" enctype="multipart/form-data">
                <div class="form-section" id="section_upload">
                    <div class="form-section-title"></div>
                    <div style="width: 100%" class="form-column" id="">
                        <div class="form-cell">
                            <label for="csvImportFile" class="label upload">@@general.method.label.selectFile@@ <span class="form-cell-validator">*</span></label>
                            <input id="csvImportFile" type="file" name="csvImportFile"/>
                        </div>
                        <div class="form-cell">
                            <label for="mode" class="label upload">@@userview.csvimportmenu.importMode@@</label>
                            <div class="form-cell-value">
                                <label style="display:block; width:100%; float:none;">
                                    <input type="radio" value="NEW" name="mode">
                                    @@userview.csvimportmenu.importMode.new@@
                                </label>
                                <label style="display:block; width:100%; float:none;">
                                    <input type="radio" value="NEW & UPDATE" name="mode" checked>
                                    @@userview.csvimportmenu.importMode.new_update@@
                                </label>
                                <#if element.properties.disabledDelete! != 'true' >
                                    <label style="display:block; width:100%; float:none;">
                                        <input type="radio" value="DELETE" name="mode">
                                        @@userview.csvimportmenu.importMode.delete@@
                                    </label>
                                </#if>
                            </div>
                        </div>
                        <div class="form-cell">
                            <label for="validateData" class="label upload">@@userview.csvimportmenu.validateData@@</label>
                            <div class="form-cell-value">
                                <label>
                                    <input type="checkbox" value="true" name="validateData" checked>
                                </label>
                            </div>
                        </div>
                    </div>
                    <div style="clear:both"></div>
                </div>
                <div class="form-section section_" id="section-actions">
                    <div class="form-section-title"></div>
                    <div style="width: " class="form-column form-column-horizontal" id="">
                        <div class="form-cell">
                            <input type="submit" value="@@general.method.label.upload@@" class="form-button btn" name="submit" id="submit">
                        </div>
                    </div>
                </div>    
            </form>
        </fieldset>
    </#if>
    <#if element.properties.customFooter! != '' >
        ${element.properties.customFooter!}
    </#if>
</div>
<script>
    $(document).ready(function(){
        <#if element.properties.corfirmation! != '' >
            $("form#csvImportForm").submit(function(){
                return confirm("${element.properties.corfirmation!}");
            });
        </#if>

        $(".toggle").click(function(){
            $(this).next().toggle();
        });
    });
</script>
