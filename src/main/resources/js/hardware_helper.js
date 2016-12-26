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

    AJS.$("#save-hardware-settings").submit = function() {
       alert("save pressed");
    }
}