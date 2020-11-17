/*
 * Copyright 2014 Stephan Fellhofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

"use strict";
var baseUrl;

AJS.toInit(function () {

    baseUrl = AJS.$("meta[name='application-base-url']").attr("content");
    var teams = [];
    var localTempResources = [];
    var editNameDialog;

    function scrollToAnchor(aid) {
        var aTag = AJS.$("a[name='" + aid + "']");
        AJS.$('html,body').animate({scrollTop: 0}, 'slow');
    }

    initGroupUserSearchField(baseUrl);
    initHdwReadOnlyUsersAndGroups(baseUrl);

    function populateForm() {
        AJS.$(".loadingDiv").show();
        AJS.$.ajax({
            url: baseUrl + "/rest/admin-helper/latest/config/getConfig",
            dataType: "json",
            success: function (config) {
                if (config.githubToken)
                    AJS.$("#github_token").attr("placeholder", config.githubToken);
                if (config.githubTokenPublic)
                    AJS.$("#github_token_public").val(unescapeHtml(config.githubTokenPublic));
                if (config.githubOrganization)
                    AJS.$("#github_organization").val(unescapeHtml(config.githubOrganization));
                if (config.mailFromName)
                    AJS.$("#mail-from-name").val(config.mailFromName);
                if (config.mailFrom)
                    AJS.$("#mail-from").val(config.mailFrom);
                if (config.mailSubject)
                    AJS.$("#mail-subject").val(config.mailSubject);
                if (config.mailBody)
                    AJS.$("#mail-body").val(config.mailBody);
                localTempResources = [];
                AJS.$("#resources").empty();
                for (var i = 0; i < config.resources.length; i++) {
                    var resource = config.resources[i];
                    localTempResources.push(resource['resourceName']);
                    var tempResourceName = resource['resourceName'].replace(/\W/g, '-');
                    AJS.$("#resources").append('<div class="field-group">' +
                        '<label for="' + tempResourceName + '">' + resource['resourceName'] + '</label>' +
                        '<input class="text single-jira-group" type="text" id="' + tempResourceName + '">' +
                        '</div>');
                }
                teams = [];
                AJS.$("#teams").empty();
                for (var i = 0; i < config.teams.length; i++) {
                    var team = config.teams[i];
                    teams.push(team['name']);
                    var tempTeamName = team['name'].replace(/\W/g, '-');
                    AJS.$("#teams").append("<h3>" + team['name'] +
                    "<button class=\"aui-button aui-button-subtle\" value=\"" + team['name'] + "\">" +
                    "<span class=\"aui-icon aui-icon-small aui-iconfont-edit\">Editing</span> Edit</button></h3><fieldset>");
                    AJS.$("#teams").append("<div class=\"field-group\"><label for=\"" + tempTeamName + "-github-teams\">GitHub Teams</label><input class=\"text github\" type=\"text\" id=\"" + tempTeamName + "-github-teams\" name=\"github-teams\" value=\"" + team["githubTeams"] + "\"></div>");
                    AJS.$("#teams").append("<div class=\"field-group\"><label for=\"" + tempTeamName + "-coordinator\">Coordinator</label><input class=\"text jira-group\" type=\"text\" id=\"" + tempTeamName + "-coordinator\" value=\"" + team['coordinatorGroups'] + "\"></div>");
                    AJS.$("#teams").append("<div class=\"field-group\"><label for=\"" + tempTeamName + "-senior\">Senior</label><input class=\"text jira-group\" type=\"text\" id=\"" + tempTeamName + "-senior\" value=\"" + team['seniorGroups'] + "\"></div>");
                    AJS.$("#teams").append("<div class=\"field-group\"><label for=\"" + tempTeamName + "-developer\">Developer</label><input class=\"text jira-group\" type=\"text\" id=\"" + tempTeamName + "-developer\" value=\"" + team['developerGroups'] + "\"></div>");
                    AJS.$("#teams").append("</fieldset>");
                }

                try {
                    var singleGithubData = [];
                    for (var i = 0; i < config.availableGithubTeams.length; i++) {
                        singleGithubData.push({
                            id: config.availableGithubTeams[i],
                            text: config.availableGithubTeams[i]
                        });
                    }
                }
                catch(err)
                {
                    console.log("there was an error loading github teams message is:" + err.message);
                }
                AJS.$(".github-single").auiSelect2({
                    placeholder: "Search for team",
                    minimumInputLength: 0,
                    data: singleGithubData
                });

                AJS.$(".github-single").auiSelect2("data", {
                    id: config.defaultGithubTeam,
                    text: config.defaultGithubTeam
                });

                if (config.availableGithubTeams) {
                    AJS.$(".github").auiSelect2({
                        placeholder: "Search for teams",
                        tags: config.availableGithubTeams,
                        tokenSeparators: [",", " "]
                    });
                }

                AJS.$("#userdirectory").auiSelect2({
                    placeholder: "Search for directories",
                    minimumInputLength: 0,
                    ajax: {
                        url: baseUrl + "/rest/admin-helper/latest/config/getDirectories",
                        dataType: "json",
                        data: function (term, page) {
                            return {query: term};
                        },
                        results: function (data, page) {
                            var select2data = [];
                            for (var i = 0; i < data.length; i++) {
                                select2data.push({id: data[i].userDirectoryId, text: data[i].userDirectoryName});
                            }
                            return {results: select2data};
                        }
                    }

                });

                AJS.$(".single-jira-group").auiSelect2({
                    placeholder: "Search for group",
                    minimumInputLength: 0,
                    ajax: {
                        url: baseUrl + "/rest/api/2/groups/picker",
                        dataType: "json",
                        data: function (term, page) {
                            return {query: term};
                        },
                        results: function (data, page) {
                            var select2data = [];
                            for (var i = 0; i < data.groups.length; i++) {
                                select2data.push({id: data.groups[i].name, text: data.groups[i].name});
                            }
                            return {results: select2data};
                        }
                    }
                });

                AJS.$("#plugin-permission").auiSelect2({
                    placeholder: "Search for users and groups",
                    minimumInputLength: 0,
                    tags: true,
                    tokenSeparators: [",", " "],
                    ajax: {
                        url: baseUrl + "/rest/api/2/groupuserpicker",
                        dataType: "json",
                        data: function (term, page) {
                            return {query: term};
                        },
                        results: function (data, page) {
                            var select2data = [];
                            for (var i = 0; i < data.groups.groups.length; i++) {
                                select2data.push({
                                    id: "groups-" + data.groups.groups[i].name,
                                    text: data.groups.groups[i].name
                                });
                            }
                            for (var i = 0; i < data.users.users.length; i++) {
                                select2data.push({
                                    id: "users-" + data.users.users[i].name,
                                    text: data.users.users[i].name
                                });
                            }
                            return {results: select2data};
                        }
                    },
                    initSelection: function (elements, callback) {
                        var data = [];
                        var array = elements.val().split(",");
                        for (var i = 0; i < array.length; i++) {
                            data.push({id: array[i], text: array[i].replace(/^users-/i, "").replace(/^groups-/i, "")});
                        }
                        callback(data);
                    }
                });

                AJS.$(".jira-group").auiSelect2({
                    placeholder: "Search for groups",
                    minimumInputLength: 0,
                    tags: true,
                    tokenSeparators: [",", " "],
                    ajax: {
                        url: baseUrl + "/rest/api/2/groups/picker",
                        dataType: "json",
                        data: function (term, page) {
                            return {query: term};
                        },
                        results: function (data, page) {
                            var select2data = [];
                            for (var i = 0; i < data.groups.length; i++) {
                                select2data.push({id: data.groups[i].name, text: data.groups[i].name});
                            }
                            return {results: select2data};
                        }
                    },
                    initSelection: function (elements, callback) {
                        var data = [];
                        var array = elements.val().split(",");
                        for (var i = 0; i < array.length; i++) {
                            data.push({id: array[i], text: array[i].replace(/^users-/i, "").replace(/^groups-/i, "")});
                        }
                        callback(data);
                    }
                });

                var approved = [];
                if (config.approvedGroups) {
                    for (var i = 0; i < config.approvedGroups.length; i++) {
                        approved.push({id: "groups-" + config.approvedGroups[i], text: config.approvedGroups[i]});
                    }
                }

                if (config.approvedUsers) {
                    for (var i = 0; i < config.approvedUsers.length; i++) {
                        approved.push({id: "users-" + config.approvedUsers[i], text: config.approvedUsers[i]});
                    }
                }


                AJS.$("#plugin-permission").auiSelect2("data", approved);
                AJS.$("#userdirectory").auiSelect2("data", {
                    id: config.userDirectoryId,
                    text: config.userDirectoryName
                });
                for(var i = 0; i < config.resources.length; i++) {
                    var resource = config.resources[i];
                    var tempResourceName = resource['resourceName'].replace(/\W/g, "-");
                    AJS.$("#" + tempResourceName).auiSelect2("data", {
                            id: resource['groupName'],
                            text: resource['groupName']
                        });
                }

                AJS.$(".loadingDiv").hide();
            },
            error: function (error) {
                AJS.messages.error({
                    title: "Error!",
                    body: "Something went wrong! \n" +
                    "error message is: " + error.responseText
                });

                AJS.$(".loadingDiv").hide();
            }
        });
    }

    function updateConfig() {
        var config = {};

        config.mailFromName = AJS.$("#mail-from-name").val();
        config.mailFrom = AJS.$("#mail-from").val();
        config.mailSubject = AJS.$("#mail-subject").val();
        config.mailBody = AJS.$("#mail-body").val();
        config.userDirectoryId = AJS.$("#userdirectory").auiSelect2("val");

        saveConfig(baseUrl, config);
    }

    function saveTeamsAndResources()
    {
        var config = {};
        config.teamAndResources = true;

        config.resources = [];
        for(var i = 0; i < localTempResources.length; i++) {
            var resource = {};
            resource.resourceName = localTempResources[i];
            var tempResourceName = localTempResources[i].replace(/\W/g, "-");
            resource.groupName = AJS.$("#" + tempResourceName).auiSelect2("val");
            config.resources.push(resource);
        }

        config.teams = [];
        for (var i = 0; i < teams.length; i++) {
            var tempTeamName = teams[i].replace(/\W/g, '-');
            var tempTeam = {};
            tempTeam.name = teams[i];
            tempTeam.githubTeams = AJS.$("#" + tempTeamName + "-github-teams").auiSelect2("val");

            tempTeam.coordinatorGroups = AJS.$("#" + tempTeamName + "-coordinator").auiSelect2("val");
            for (var j = 0; j < tempTeam.coordinatorGroups.length; j++) {
                tempTeam.coordinatorGroups[j] = tempTeam.coordinatorGroups[j].replace(/^groups-/i, "");
            }

            tempTeam.seniorGroups = AJS.$("#" + tempTeamName + "-senior").auiSelect2("val");
            for (var j = 0; j < tempTeam.seniorGroups.length; j++) {
                tempTeam.seniorGroups[j] = tempTeam.seniorGroups[j].replace(/^groups-/i, "");
            }

            tempTeam.developerGroups = AJS.$("#" + tempTeamName + "-developer").auiSelect2("val");
            for (var j = 0; j < tempTeam.developerGroups.length; j++) {
                tempTeam.developerGroups[j] = tempTeam.developerGroups[j].replace(/^groups-/i, "");
            }

            config.teams.push(tempTeam);
        }

        saveConfig(baseUrl, config);
    }

    function addTeam() {
        AJS.$(".loadingDiv").show();
        AJS.$.ajax({
            url: baseUrl + "/rest/admin-helper/1.0/config/addTeam",
            type: "PUT",
            contentType: "application/json",
            data: AJS.$("#team").attr("value"),
            processData: false,
            success: function () {
                AJS.messages.success({
                    title: "Success!",
                    body: "Team added!"
                });
                AJS.$(".loadingDiv").hide();
            },
            error: function (error) {
                AJS.messages.error({
                    title: "Error!",
                    body: "Something went wrong!<br />" + error.responseText
                });
                AJS.$(".loadingDiv").hide();
            }
        });
    }

    function addResource() {
        AJS.$(".loadingDiv").show();
        AJS.$.ajax({
            url: baseUrl + "/rest/admin-helper/1.0/config/addResource",
            type: "PUT",
            contentType: "application/json",
            data: AJS.$("#edit-resource").attr("value"),
            processData: false,
            success: function () {
                AJS.messages.success({
                    title: "Success!",
                    body: "Resource added!"
                });
                AJS.$(".loadingDiv").hide();
            },
            error: function (error) {
                AJS.messages.error({
                    title: "Error!",
                    body: "Something went wrong!<br />" + error.responseText
                });
                AJS.$(".loadingDiv").hide();
            }
        });
    }

    function removeResource() {
        AJS.$(".loadingDiv").show();
        AJS.$.ajax({
            url: baseUrl + "/rest/admin-helper/1.0/config/removeResource",
            type: "PUT",
            contentType: "application/json",
            data: AJS.$("#edit-resource").attr("value"),
            processData: false,
            success: function () {
                AJS.messages.success({
                    title: "Success!",
                    body: "Resource removed!"
                });
                AJS.$(".loadingDiv").hide();
            },
            error: function (error) {
                AJS.messages.error({
                    title: "Error!",
                    body: "Something went wrong!<br />" + error.responseText
                });
                AJS.$(".loadingDiv").hide();
            }
        });
    }

    function editTeam(teamName) {
        // may be in background and therefore needs to be removed
        if (editNameDialog) {
            try {
                editNameDialog.remove();
            } catch (err) {
                // may be removed already
            }
        }

        editNameDialog = new AJS.Dialog({
            width: 600,
            height: 200,
            id: "edit-name-dialog",
            closeOnOutsideClick: true
        });

        var content = "<form class=\"aui\">\n" +
            "    <fieldset>\n" +
            "        <div class=\"field-group\">\n" +
            "            <label for=\"new-name\">New Team Name</label>\n" +
            "            <input class=\"text\" type=\"text\" id=\"new-name\" name=\"new-name\" title=\"new-name\">\n" +
            "        </div>\n" +
            "    </fieldset>\n" +
            " </form> ";

        editNameDialog.addHeader("New Team Name for " + teamName);
        editNameDialog.addPanel("Panel 1", content, "panel-body");

        editNameDialog.addButton("Save", function (dialog) {
            AJS.$(".loadingDiv").show();
            AJS.$.ajax({
                url: baseUrl + "/rest/admin-helper/1.0/config/editTeam",
                type: "PUT",
                contentType: "application/json",
                data: JSON.stringify([teamName, AJS.$("#new-name").val()]),
                processData: false,
                success: function () {
                    AJS.messages.success({
                        title: "Success!",
                        body: "Team edited!"
                    });
                    populateForm();
                    scrollToAnchor('top');
                    AJS.$(".loadingDiv").hide();
                },
                error: function (error) {
                    AJS.messages.error({
                        title: "Error!",
                        body: "Something went wrong!<br />" + error.responseText
                    });
                    scrollToAnchor('top');
                    AJS.$(".loadingDiv").hide();
                }
            });

            dialog.remove();
        });
        editNameDialog.addLink("Cancel", function (dialog) {
            dialog.remove();
        }, "#");

        editNameDialog.gotoPage(0);
        editNameDialog.gotoPanel(0);
        editNameDialog.show();
    }

    function removeTeam() {
        AJS.$(".loadingDiv").show();
        AJS.$.ajax({
            url: baseUrl + "/rest/admin-helper/1.0/config/removeTeam",
            type: "PUT",
            contentType: "application/json",
            data: AJS.$("#team").attr("value"),
            processData: false,
            success: function () {
                AJS.messages.success({
                    title: "Success!",
                    body: "Team removed!"
                });
                AJS.$(".loadingDiv").hide();
            },
            error: function () {
                AJS.messages.error({
                    title: "Error!",
                    body: "Something went wrong!"
                });
                AJS.$(".loadingDiv").hide();
            }
        });
    }

    populateForm();

    AJS.$("#general").submit(function (e) {
        e.preventDefault();
        if (e.originalEvent.submitter.value === 'Save') {
            updateConfig();
            scrollToAnchor('top');
        } else {
            editTeam(e.originalEvent.submitter.value);
        }
    });

    AJS.$("#modify-teams").submit(function (e) {
        e.preventDefault();
        addTeam();
        populateForm();
        scrollToAnchor('top');
    });

    AJS.$("#modify-resources").submit(function (e) {
        e.preventDefault();
        addResource();
        populateForm();
        scrollToAnchor('top');
    });

    AJS.$("#remove").click(function (e) {
        e.preventDefault();
        removeTeam();
        scrollToAnchor('top');
    });

    AJS.$("#remove-resource").click(function (e) {
        e.preventDefault();
        removeResource();
        scrollToAnchor('top');
    });

    AJS.$("#save-github-settings").click(function (e) {
        e.preventDefault();
        saveGithubSettings();
        scrollToAnchor('top');
    });

    AJS.$("#save-permission").click(function (e) {
        e.preventDefault();
        savePluginPermission(baseUrl);
        populateForm();
        scrollToAnchor('top');
    });

    AJS.$("a[href='#tabs-general']").click(function () {
        AJS.$("#teams").html("");
        populateForm();
    });

    AJS.$("#enable_settings_change").change(function()
    {
        enableSettingsChange();
    });

    AJS.$("#download_config_backup").click(function (e) {
        e.preventDefault();
        redirectToDownloadConfig();
    });

    AJS.$("#download_hdw_config_backup").click(function (e) {
        e.preventDefault();
        redirectToDownloadHDWConfig();
    });

    AJS.$("#upload_backup").click(function (e) {
        e.preventDefault();
        redirectToUpload();
    });

    AJS.$("#clear-permissions").click(function (e) {
        e.preventDefault();
        clearAllReadOnlyLists(baseUrl);
        clearPluginPermission(baseUrl);
        populateForm();
    });

    AJS.$("#save-team-resources").click(function(e){
        e.preventDefault();
        saveTeamsAndResources();
    });

    AJS.$("#reset-hardware").click(function (e) {
        e.preventDefault();
        showHardwareDeletionDialog();
    });

    function unescapeHtml(safe) {
        if(safe) {
            return AJS.$('<div />').html(safe).text();
        } else {
            return '';
        }
    }

    function saveGithubSettings() {
        if ((!AJS.$("#github_token").val() && !AJS.$("#github_token").attr("placeholder")) || !AJS.$("#github_organization").val()
            || !AJS.$("#github_token_public").val()) {
            AJS.messages.error({
                title: "Error!",
                body: "API Tokens and Organisation must be filled out"
            });
            return;
        }

        AJS.$.ajax({
            headers:{
                "Authorization": "token " + config.githubTokenPublic
            },
            url: "https://api.github.com/search/users?q=User",
            type: "GET",
            success : function() {
                checkPublicTokenAndOrganization(baseUrl)
            },
            error : function (error) {
                AJS.messages.error({
                    title : "Error " + error.status,
                    body : "Authentification Failed! \n" +
                    "There was an error with your public token!"
                })
            }
        });
    }
});