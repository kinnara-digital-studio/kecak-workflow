    <#if close??>
        <script type="text/javascript">
            if (parent && parent.PopupDialog.closeDialog) {
                parent.PopupDialog.closeDialog();
            }
        </script>
    </#if>
    <div id="main-body-header">
        @@enterprise.console.label.license@@
    </div>
    <div id="main-body-content">
        <form id="licenseForm" class="form" method="POST" action="service">
            <fieldset>
                <legend>@@enterprise.console.label.license@@</legend>
                <#if message?? && message! != ''>
                    <div style="text-align:center" class="form-errors">
                        ${message!}
                    </div>    
                </#if>
                <div class="form-row">
                    <label for="field1">@@enterprise.console.label.license.systemKey@@</label>
                    <span class="form-input">${systemKey!}</span>
                </div>
                <div class="form-row">
                    <label for="field1">@@enterprise.console.label.username@@</label>
                    <span class="form-input"><input id="username" name="username" type="text" value=""/> *</span>
                </div>
                <div class="form-row">
                    <label for="field1">@@enterprise.console.label.password@@</label>
                    <span class="form-input"><input id="password" name="password" type="password" value=""/> *</span>
                </div>
                <div class="form-row">
                    <label for="field1">@@enterprise.console.label.license@@</label>
                    <span class="form-input"><textarea id="license" name="license" cols="40" rows="10"></textarea> *</span>
                </div>
            </fieldset>
            <div class="form-buttons">
                <input class="form-button" type="submit" value="@@enterprise.console.label.submit@@" />
                <input type="hidden" name="spot" value="license" />
                <input type="hidden" name="action" value="submit" />
            </div>
        </form>
    </div>


