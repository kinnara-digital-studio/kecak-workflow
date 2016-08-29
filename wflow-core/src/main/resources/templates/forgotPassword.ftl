    <div id="main-body-header">
        @@app.edm.label.forgotPassword@@
    </div>
    <div id="main-body-content">
        <#if updated! == "true">
            <p>@@app.edm.message.passwordReset@@</p>
        <#else>
            <#if errors??>
                <div class="form-errors">
                    <#list errors! as error>
                        ${error!}<br/>
                    </#list>
                </div>
            </#if>
            <form id="forgotPassword" action="?a=fps" class="form" method="POST">
                <fieldset>
                    <div class="form-row">
                        <label for="username">@@app.edm.label.username@@</label>
                        <span class="form-input"><input id="username" name="username" type="text" value=""/> *</span>
                    </div>
                </fieldset>
                <div class="form-buttons">
                    <input class="form-button" type="submit" value="@@app.edm.label.submit@@" />
                </div>
            </form>
        </#if>
    </div>