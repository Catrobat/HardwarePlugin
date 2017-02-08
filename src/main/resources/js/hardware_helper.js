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

function initGroupUserSearchField(baseUrl)
{
    AJS.$("#hardware-permission").auiSelect2({
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

    AJS.$("#save-hardware-premission").click(function() {
       formulateReadonlyJSONAndSendToServer(baseUrl);
    });

    AJS.$("#clear-readonly-lists").click(function () {
        clearAllReadOnlyLists(baseUrl);
    });
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
               AJS.messages.hint({
                   title:"Information!",
                   body:"You were granted Read Only access, you may not change any settings! <br>"+
                       "For further information contact your Jira Admin."
                   })
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

function initHdwReadOnlyUsersAndGroups(baseUrl)
{
  AJS.$.ajax({
      url: baseUrl + "/rest/admin-helper/latest/hardware/getReadOnlyUsersAndGroups",
      type: "GET",
      success: function (config) {
          var approved = [];
          if (config.readOnlyGroups) {
              for (var i = 0; i < config.readOnlyGroups.length; i++) {
                  approved.push({id: "groups-" + config.readOnlyGroups[i], text: config.readOnlyGroups[i]});
              }
          }

          if (config.readOnlyUsers) {
              for (var i = 0; i < config.readOnlyUsers.length; i++) {
                  approved.push({id: "users-" + config.readOnlyUsers[i], text: config.readOnlyUsers[i]});
              }
          }
          console.log(approved[0]);
          AJS.$("#hardware-permission").auiSelect2("data", approved);
      },
      error : function () {
          AJS.messages.error(
              {
                  title:"Error!",
                  body:"There was an error loading ReadOnly Data!"
              })
      }
  })
}

function formulateReadonlyJSONAndSendToServer(baseUrl)
{
    var usersAndGroups = AJS.$("#hardware-permission").auiSelect2("val");
    var readOnlyUsers = [];
    var readOnlyGroups = [];
    for (var i = 0; i < usersAndGroups.length; i++) {
        if (usersAndGroups[i].match("^users-")) {
            readOnlyUsers.push(usersAndGroups[i].split("users-")[1]);
        } else if (usersAndGroups[i].match("^groups-")) {
            readOnlyGroups.push(usersAndGroups[i].split("groups-")[1]);
        }
    }

    var config = {};
    config.readOnlyUsers = readOnlyUsers;
    config.readOnlyGroups = readOnlyGroups;

    AJS.$.ajax({
        url: baseUrl + "/rest/admin-helper/latest/hardware/saveReadOnlyUsersAndGroups",
        type:"PUT",
        contentType: "application/json",
        data: JSON.stringify(config),
        dateType: "json",
        success: function () {
            AJS.messages.success({
                title:"Success",
                body:"ReadOnly Users and Groups were Successfully saved!"
            });
            location.reload(true);
        },
        error: function (err) {
            AJS.messages.error({
                title:"Error",
                body:"There was an error processing your data <br>"+ err.responseText
            })
        }

    })
}

function clearAllReadOnlyLists(baseUrl) {
    AJS.$.ajax({
        url: baseUrl + "/rest/admin-helper/latest/hardware/resetReadonlyUsersAndGroups",
        type: "POST",
        success: function () {
            AJS.messages.success({
                title:"Success",
                body:"Reset was successful!"
            });
            location.reload(true);
        },
        error: function (err) {
            AJS.$.message.error({
                title:"Error!",
                body:"There was an error processing your data <br>" + err.responseText
            })
        }
    })
}
