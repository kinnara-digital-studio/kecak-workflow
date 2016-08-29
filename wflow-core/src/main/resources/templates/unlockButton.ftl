<ul id="addon-action-buttons">
    <li class="unlockButton"><button onclick="unlockUser()">@@app.edm.label.unlock@@</button></li>
</ul>
<script>
    function unlockUser(){
        if (confirm('@@app.edm.label.unlock.confirmation@@')) {
            var callback = {
                success : function(response) {
                    if ("true" == response) {
                        $(".unlockButton").remove();
                        alert("@@app.edm.message.userUnlocked@@");
                    } else {
                        alert("@@app.edm.message.userUnlockedFailed@@");
                    }
                }
            }
            var request = ConnectionManager.post('${url!}', callback);
        }
    }
</script>