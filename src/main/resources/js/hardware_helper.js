/**
 * Created by dominik on 26.12.16.
 */

function setReadOnlyProperties()
{
    var tabs = document.getElementsByName("hidden-if-read-only");
    for(var i = 0; i < tabs.length; i++)
    {
        tabs[i].style.display="none";
    }
}

function checkPremission(baseUrl)
{
    AJS.$.ajax({
        url: baseUrl + "/rest/admin-helper/latest/hardware/getReadOnlyStatus",
        type: "GET",
        success: function (data) {
            if(data.isReadOnly === true)
            {
                initHardwareVelocityReadonly(baseUrl);
/*                AJS.messages.hint({
                    title:"Information!",
                    body:"You were granted Read Only access, you may not change any settings! <br>"+
                    "For further information contact your Jira Admin."
                })*/
            }
            else{
                initHardwareVelocityAdmin(baseUrl);
            }
        },
        error: function (err) {
            AJS.messages.error({
                title:"There was an Error!",
                body:err.responseText
            })
        }
    });
}
