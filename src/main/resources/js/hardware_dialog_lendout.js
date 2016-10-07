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

function showLendoutDialog(baseUrl, hardwareId, deviceId) {
    AJS.$.ajax({
        url: baseUrl + urlSuffixSingleHardwareModelDevicesAvailable.replace("{0}", hardwareId),
        type: "GET",
        success: function (deviceList) {
            showLendOutDialogAjax(baseUrl, hardwareId, deviceList, deviceId)
        },
        error: function (error) {
            AJS.messages.error({
                title: "Error!",
                body: error.responseText
            });
        }
    });
}

function showLendOutDialogAjax(baseUrl, hardwareId, deviceList, deviceId) {
    // may be in background and therefore needs to be removed
    if (lendingOutDialog) {
        try {
            lendingOutDialog.remove();
        } catch (err) {
            // may be removed already
        }
    }

    lendingOutDialog = new AJS.Dialog({
        width: 840,
        height: 600,
        id: "lend-out-dialog",
        closeOnOutsideClick: true
    });

    var selectedDeviceId = 0;

    var contentDevices = "<div id=\"lend-out-message-bar-1\"></div>" +
        "<div class=\"search-field-dialog\">\n" +
        "<form class=\"aui\" onsubmit=\"return false;\">\n" +
        "<label for=\"search-filter-lent-out-dialog\">Search Filter</label>\n" +
        "<input class=\"search text\" type=\"text\" id=\"search-filter-lent-out-dialog\" name=\"search-filter-lent-out-dialog\"\n" +
        "title=\"search-filter-lent-out-dialog\" autocomplete=\"off\">\n" +
        "</form>\n" +
        "</div>" +
        "<table class=\"aui aui-table-interactive aui-table-sortable\">\n" +
        "<thead>\n" +
        "<tr>\n" +
        "<th>Serialnumber</th>\n" +
        "<th>IMEI</th>\n" +
        "<th>Inventorynumber</th>\n" +
        "<th class=\"aui-table-column-unsortable\">Choose</th>\n" +
        "</tr>\n" +
        "</thead>\n" +
        "<tbody class=\"list\" id=\"table-lent-out\">\n";
    deviceList.map = [];
    for (var i = 0; i < deviceList.length; i++) {
        contentDevices += "<tr>\n" +
            "<td class=\"serial\">" + formatString(deviceList[i].serialNumber) + "</td>\n" +
            "<td class=\"imei\">" + formatString(deviceList[i].imei) + "</td>\n" +
            "<td class=\"inventory\">" + formatString(deviceList[i].inventoryNumber) + "</td>\n" +
            "<td><a class=\"choose\" id=\"" + deviceList[i].id + "\" href=\"#\">Choose...</a></td>\n" +
            "</tr>\n";

        deviceList.map[deviceList[i].id] = i;
    }
    contentDevices += "</tbody>\n" +
        "</table>";


    var contentDetails = "<div id=\"lend-out-message-bar-2\"></div>" +
        "<form action=\"#\" method=\"post\" id=\"d\" class=\"aui\">\n" +
        "    <fieldset>\n" +
        "        <div class=\"field-group\">\n" +
        "            <label for=\"serial\">Serialnumber</label>\n" +
        "            <input class=\"text\" type=\"text\" id=\"serial\" name=\"serial\" title=\"serial\" disabled>\n" +
        "        </div>\n" +
        "        <div class=\"field-group\">\n" +
        "            <label for=\"imei\">IMEI</label>\n" +
        "            <input class=\"text\" type=\"text\" id=\"imei\" name=\"imei\" title=\"imei\" disabled>\n" +
        "        </div>\n" +
        "        <div class=\"field-group\">\n" +
        "            <label for=\"inventory\">Inventorynumber</label>\n" +
        "            <input class=\"text\" type=\"text\" id=\"inventory\" name=\"inventory\" title=\"inventory\" disabled>\n" +
        "        </div>\n" +
        "        <div class=\"field-group\">\n" +
        "            <label for=\"user\">User<span class=\"aui-icon icon-required\"> required</span></label>\n" +
        "            <input autocomplete=\"off\" class=\"text\" type=\"text\" id=\"user\" name=\"user\" title=\"username\">\n" +
        "        </div>\n" +
        "        <div class=\"field-group\">\n" +
        "            <label for=\"device-comment\">Device Comment</label>\n" +
        "            <textarea class=\"textarea\" name=\"device-comment\" id=\"device-comment\" placeholder=\"Your comment here...\"></textarea>\n" +
        "        </div>\n" +
        "        <div class=\"field-group\">\n" +
        "            <label for=\"begin_date\">Begin Date</label>\n" +
        "            <input class=\"text\" type=\"date\" id=\"begin_date\" name=\"begin_date\" title=\"begin_date\">\n" +
        "        </div>\n" +
        "        <div class=\"field-group\">\n" +
        "            <label for=\"lending_purpose\">Lending Purpose</label>\n" +
        "            <input class=\"text\" type=\"text\" id=\"lending_purpose\" name=\"lending_purpose\" title=\"lending purpose\">\n" +
        "        </div>\n" +
        "        <div class=\"field-group\">\n" +
        "            <label for=\"lending-comment\">Lending Comment</label>\n" +
        "            <textarea class=\"textarea\" name=\"lending-comment\" id=\"lending-comment\" placeholder=\"Your comment here...\"></textarea>\n" +
        "        </div>\n" +
        "    </fieldset>\n" +
        "</form>";

    lendingOutDialog.addHeader("Lending Out - " + deviceList[0].hardwareModelName);
    lendingOutDialog.addPanel("Choose device", contentDevices, "panel-body-device");

    lendingOutDialog.addPanel("Details", contentDetails, "panel-body-details");

    lendingOutDialog.addButton("Save", function (dialog) {
        if (!AJS.$("#user").auiSelect2("val")) {
            AJS.messages.error("#lend-out-message-bar-1", {
                title: "Username is empty!",
                fadeout: true
            });
            AJS.messages.error("#lend-out-message-bar-2", {
                title: "Username is empty!",
                fadeout: true
            });
            return;
        } else if (selectedDeviceId == 0) {
            AJS.messages.error("#lend-out-message-bar-1", {
                title: "No device chosen!",
                fadeout: true
            });
            AJS.messages.error("#lend-out-message-bar-2", {
                title: "No device chosen!",
                fadeout: true
            });
            return;
        }

        var lendingOut = {
            lentOutBy: AJS.$("#user").auiSelect2("val"),
            begin: new Date(AJS.$("#begin_date").val()),
            purpose: AJS.$("#lending_purpose").val(),
            comment: AJS.$("#lending-comment").val(),
            device: {comments: [
                {comment: AJS.$("#device-comment").val()}
            ]}
        };

        AJS.$.ajax({
            url: baseUrl + urlSuffixSingleDeviceLendOut.replace("{0}", selectedDeviceId),
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify(lendingOut),
            dataType: "json",
            success: function () {
                AJS.messages.success({
                    title: "Success!",
                    body: "Device lent out successfully"
                });
                fillOutAllTables(baseUrl);
            },
            error: function (error) {
                AJS.messages.error({
                    title: "Error!",
                    body: error.responseText
                });
            }
        });

        dialog.remove();
    }, "lend-out-save");
    lendingOutDialog.addLink("Cancel", function (dialog) {
        dialog.remove();
    }, "#");

    AJS.$(".choose").click(function (e) {
        e.preventDefault();

        lendingOutDialog.gotoPanel(1);

        AJS.$("#lend-out-dialog").find("a#" + selectedDeviceId).closest("tr").css("background-color", "");
        selectedDeviceId = e.target.id;
        AJS.$("#lend-out-dialog").find("a#" + selectedDeviceId).closest("tr").css("background-color", "#e0e0e0");

        AJS.$("#serial").val(unescapeHtml(deviceList[deviceList.map[selectedDeviceId]].serialNumber));
        AJS.$("#imei").val(unescapeHtml(deviceList[deviceList.map[selectedDeviceId]].imei));
        AJS.$("#inventory").val(unescapeHtml(deviceList[deviceList.map[selectedDeviceId]].inventoryNumber));
    });

    AJS.$("#user").auiSelect2({
        placeholder: "Search for user",
        minimumInputLength: 1,
        ajax: {
            //url: baseUrl + "/rest/api/2/user/picker",
            url: baseUrl + urlSuffixUserSearch,
            type: "POST",
            dataType: "json",
            data: function (term, page) {
                return {query: term};
            },
            results: function (data, page) {
                var select2data = [];
                for (var i = 0; i < data.length; i++) {
                    select2data.push({id: data[i].userName, text: unescapeHtml(data[i].displayName)});
                }
                return {results: select2data};
            }
        },
        //Allow manually entered text in drop down.
        createSearchChoice: function (term, data) {
            if (AJS.$(data).filter(function () {
                return this.text.localeCompare(term) === 0;
            }).length === 0) {
                return {id: term, text: term};
            }
        }
    });

    var now = new Date();
    var day = ("0" + now.getDate()).slice(-2);
    var month = ("0" + (now.getMonth() + 1)).slice(-2);
    var today = now.getFullYear() + "-" + (month) + "-" + (day);
    AJS.$("#begin_date").attr("value", today);

    lendingOutDialog.gotoPage(0);
    lendingOutDialog.gotoPanel(0);
    lendingOutDialog.show();

    var lendOutDialogList = new List("lend-out-dialog", {page: Number.MAX_VALUE, valueNames: ["serial", "imei", "inventory"]});

    if(typeof deviceId != 'undefined') {
        AJS.$("#lend-out-dialog").find("a#" + deviceId).closest("tr").css("background-color", "");
        AJS.$("#lend-out-dialog").find("a#" + deviceId).closest("tr").css("background-color", "#e0e0e0");

        lendingOutDialog.gotoPanel(1);
        selectedDeviceId = deviceId;

        AJS.$("#serial").val(unescapeHtml(deviceList[deviceList.map[deviceId]].serialNumber));
        AJS.$("#imei").val(unescapeHtml(deviceList[deviceList.map[deviceId]].imei));
        AJS.$("#inventory").val(unescapeHtml(deviceList[deviceList.map[deviceId]].inventoryNumber));
    }
}
