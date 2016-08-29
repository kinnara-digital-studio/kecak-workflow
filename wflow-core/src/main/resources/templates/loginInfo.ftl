<script>
    $(document).ready(function(){
        var userLoginInfo = $("div.systemAlert");
        if (userLoginInfo.length === 0) {
            userLoginInfo = $("<div class=\"systemAlert\"></div>");
            userLoginInfo.append("<a class=\"systemAlertClose\">x</a>");
            $(userLoginInfo).find(".systemAlertClose").click(function(){
                $(userLoginInfo).remove();
            });
            $("body > #container, body > #page > #main:visible").prepend(userLoginInfo);
        }
        
        <#if data??>
            <#if data.lastLogedInDate??>
                $(userLoginInfo).append("<span class=\"systemAlertMessage\"><strong>@@app.edm.label.lastLogin@@</strong> ${data.lastLogedInDate!?string.medium}</span>");
            </#if>

            <#if data.failedloginAttempt??>
            $(userLoginInfo).append("<span class=\"systemAlertMessage\"><strong>@@app.edm.label.failedLoginAttemptSinceLastLogin@@</strong> ${data.failedloginAttempt!}</span>");
            </#if>
        </#if>
        
        <#if message??>
            $(userLoginInfo).append("<span class=\"systemAlertMessage passwordExpired\"><strong>${message!}</strong></span>");
        </#if>
        
    });
</script>