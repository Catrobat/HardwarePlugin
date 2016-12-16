/**
 * Created by dominik on 12.12.16.
 */
var baseUrl = AJS.$("meta[name='application-base-url']").attr("content");

function enableSettingsChange() {
    var status = document.getElementById("github_organization").disabled;
    {
        if (status === false) {
            document.getElementById("github_organization").disabled = true;
            document.getElementById("github_token_public").disabled = true;
            document.getElementById("github_token").disabled = true;
        }
        else {
            document.getElementById("github_organization").disabled = false;
            document.getElementById("github_token_public").disabled = false;
            document.getElementById("github_token").disabled = false;
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
    git_config.defaultGithubTeam = AJS.$("#default-github-team").auiSelect2("val");

    AJS.$(".loadingDiv").show();
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
            AJS.$(".loadingDiv").hide();
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
