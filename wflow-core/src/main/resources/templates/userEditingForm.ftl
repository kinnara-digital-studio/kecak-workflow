<fieldset>
    <legend>@@app.edm.label.adminSetting@@</legend>
    <div class="form-row">
        <label for="field1">@@app.edm.label.forcePasswordChange@@</label>
        <span class="form-input">
            <input type="checkbox" id="forcePasswordChange" name="forcePasswordChange" ${forcePasswordChange!}/>
        </span>
    </div> 
    <div class="form-row">
        <label for="field1">@@app.edm.label.resetPassword@@</label>
        <span class="form-input">
            <input type="checkbox" id="resetPassword" name="resetPassword" ${resetPassword!}/>
        </span>
    </div>
    <div class="form-row">
        <label for="field1">@@app.edm.label.noPasswordExpiration@@</label>
        <span class="form-input">
            <input type="checkbox" id="noPasswordExpiration" name="noPasswordExpiration" ${noPasswordExpiration!}/>
        </span>
    </div>
</fieldset>
<script>
    $(document).ready(function(){
        $("#email").parent().append(" *");
        $('#resetPassword').change(function () {
            if ($(this).attr("checked")) {
                $("#password, #confirmPassword").parent().parent().hide();
                $("#password, #confirmPassword").each(function(){
                    $(this).attr("name", $(this).attr("name") + "_");
                });
            } else {
                $("#password, #confirmPassword").parent().parent().show();
                $("#password, #confirmPassword").each(function(){
                    $(this).attr("name", $(this).attr("name").replace("_", ""));
                });
            }
        });
    });
</script>