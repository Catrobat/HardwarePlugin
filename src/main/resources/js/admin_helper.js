/**
 * Created by dominik on 12.12.16.
 */
function enableSettingsChange() {
    var status = document.getElementById("github_organization").disabled;
    {
        if (status === false) {
            document.getElementById("github_organization").disabled = true;
            document.getElementById("github_token_public").disabled = true;
            document.getElementById("github_token").disabled = true;
            document.getElementById("default-github-team").disabled = true;
        }
        else {
            document.getElementById("github_organization").disabled = false;
            document.getElementById("github_token_public").disabled = false;
            document.getElementById("github_token").disabled = false;
            document.getElementById("default-github-team").disabled = false;
        }
    }
}

function sendDataToServer(url)
{
    var git_config = {};

    if (!AJS.$("#github_token").attr("placeholder"))
    {
        git_config.githubToken = AJS.$("#github_token").val();
    }

    git_config.githubTokenPublic = AJS.$("#github_token_public").val();
    git_config.githubOrganization = AJS.$("#github_organization").val();
    git_config.defaultGithubTeam = AJS.$("#default-github-team").auiSelect2("val")

    AJS.$.ajax({
        url: url + "/rest/admin-helper/1.0/config/saveGithubConfig",
        type: "PUT",
        contentType: "application/json",
        data: JSON.stringify(git_config),
        processData: false,
        success: function () {
            AJS.messages.success({
                title: "Success!",
                body: "Github Settings were successfully saved!"
            });
        },
        error: function (error) {
            AJS.messages.error({
                title: "Error!",
                body: error.responseText
            });
            AJS.$(".loadingDiv").hide();
        }
    });
}

function  checkPublicTokenAndOrganization(url) {

    if (AJS.$("#github_token").val() !== '')
    {
        var new_private_token = AJS.$("#github_token").val();
        var new_organization = AJS.$("#github_organization").val();

        var res = AJS.$.ajax({
            url: "https://api.github.com/orgs/"+ new_organization +"/teams?access_token=" + new_private_token,
            type:"GET"
        });

        AJS.$.when(res)
            .done(function () {
                sendDataToServer(url)
            })
            .fail(function(error)
            {
                console.log(error.status);
                if(error.status == 401) {
                    AJS.messages.error({
                        title: "Error: " + error.status,
                        body: "Authentication Failed, check your private Token!"
                    })
                }
                if(error.status == 404) {
                    AJS.messages.error({
                        title: "Error: "+ error.status,
                        body: "The given Organization was not found!"
                    })
                }
            });
    }
    else
    {
        var settings = {};
        settings.githubOrganization = AJS.$("#github_organization").val();

        var res = AJS.$.ajax({
            url: url + "/rest/admin-helper/1.0/config/checkSettings",
            type: "PUT",
            async: false,
            contentType: "application/json",
            data: JSON.stringify(settings)
        });

        AJS.$.when(res)
            .done(function () {
                sendDataToServer(url)
            })
            .fail(function (error) {
                AJS.messages.error({
                    title:"Error",
                    body: error.responseText
                })
            })
    }
}

function redirectToDownload()
{
    var checked = document.getElementById("enable_full_download").checked;
    window.open(baseUrl+"/plugins/servlet/admin_helper/download_backup?config=true&hardware="+checked,"_self");
}

function redirectToUpload()
{
    window.open(baseUrl+"/plugins/servlet/admin_helper/upload_backup","_self");
}
