    <div id="main-body-header">
        @@app.edm.label.changePassword@@
    </div>
    <div id="main-body-content">
        <#if updated! == "true">
            <script>
                parent.window.location = '${redirectUrl!}';
            </script>
        <#else>
            <#if errors??>
                <div class="form-errors">
                    <#list errors! as error>
                        ${error!}<br/>
                    </#list>
                </div>
            </#if>
            <form id="changePassword" action="?a=cps" class="form" method="POST">
                <input id="t" name="t" type="hidden" value="${t!}"/>
                <fieldset>
                    <legend>@@app.edm.label.login@@</legend>
                    <div class="form-row">
                        <label for="username">@@app.edm.label.username@@</label>
                        <span class="form-input"><input id="username" name="username" type="hidden" value="${username!}"/>${username!}</span>
                    </div>
                    <div class="form-row">
                        <label for="password">@@app.edm.label.password@@</label>
                        <span class="form-input"><input id="password" name="password" type="password" value="" autocomplete="off" /> *</span>
                    </div>
                </fieldset>
                <fieldset>
                    <legend>@@app.edm.label.newPassword@@</legend>
                    <div class="form-row">
                        <label for="newPassword">@@app.edm.label.newPassword@@</label>
                        <span class="form-input"><input id="newPassword" name="newPassword" type="password" value="" autocomplete="off"/> *</span>
                    </div>
                    <div class="form-row">
                        <label for="newPasswordConfirm">@@app.edm.label.confirmNewPassword@@</label>
                        <span class="form-input"><input id="newPasswordConfirm" name="newPasswordConfirm" type="password" value="" autocomplete="off"/> *</span>
                    </div>
                    <div class="policies" style="display:block">
                        <#list policies! as policy>
                            <span>${policy}</span>
                        </#list>
                    </div>
                </fieldset>
                <div class="form-buttons">
                    <input class="form-button" type="submit" value="@@app.edm.label.submit@@" />
                </div>
            </form>
        </#if>
    </div>