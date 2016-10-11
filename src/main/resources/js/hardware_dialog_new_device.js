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

function showNewDeviceDialog(baseUrl, editableDevice) {
    AJS.$.ajax({
        url: baseUrl + urlSuffixHardwareModels,
        type: "GET",
        success: function (deviceList) {
            showNewDeviceDialogAjax(baseUrl, deviceList, editableDevice)
        },
        error: function (error) {
            AJS.messages.error({
                title: "Error!",
                body: error.responseText
            });
        }
    });
}

function showNewDeviceDialogAjax(baseUrl, hardwareList, editableDevice) {
    var dialog = new AJS.Dialog({
        width: 600,
        height: 500,
        id: "new-device-dialog",
        closeOnOutsideClick: true
    });

    var device;
    var header = "Create New Device";
    if (editableDevice) {
        header = "Edit Device";
        device = editableDevice;
        device.receivedDate = new Date(device.receivedDate);
    } else {
        var device = {
            id: 0,
            serialNumber: "",
            imei: "",
            inventoryNumber: "",
            receivedDate: new Date(),
            receivedFrom: "",
            usefulLiveOfAsset: "",
            hardwareModel: {}
        }
    }

    var content = "<div id=\"dialog_error\"></div><form action=\"#\" method=\"post\" id=\"d\" class=\"aui\">\n" +
        "<fieldset>\n" +
        "<div class=\"field-group\">\n" +
        "<label for=\"hardware_selection\">Hardware Model<span class=\"aui-icon icon-required\"> required</span></label>\n" +
        "<select class=\"select\" id=\"hardware_selection\" name=\"hardware\" title=\"hardware\">\n" +
        "<option>Select</option>\n";
    for (var i = 0; i < hardwareList.length; i++) {
        content += "<option value=\"" + hardwareList[i].id + "\">" + hardwareList[i].name;
        if (hardwareList[i].version) {
            content += " (" + hardwareList[i].version + ")";
        }
        content += "</option>\n";
    }
    content += "</select>\n" +
        "<div class=\"error\" id=\"select_hardware_error\">Select a hardware model</div>\n" +
        "</div>\n" +
        "\n" +
        "<div class=\"field-group\">\n" +
        "<label for=\"serial\">Serial number</label>\n" +
        "<input class=\"text device_id\" type=\"text\" id=\"serial\" name=\"serial\" title=\"serial number\" value=\"" + formatString(device.serialNumber) + "\">\n" +
        "</div>\n" +
        "\n" +
        "<div class=\"field-group\">\n" +
        "<label for=\"imei\">IMEI</label>\n" +
        "<input class=\"text device_id\" type=\"text\" id=\"imei\" name=\"imei\" title=\"imei\" value=\"" + formatString(device.imei) + "\">\n" +
        "</div>\n" +
        "\n" +
        "<div class=\"field-group\">\n" +
        "<label for=\"inventory\">Inventory number</label>\n" +
        "<input class=\"text device_id\" type=\"text\" id=\"inventory\" name=\"inventory\" title=\"inventory number\" value=\"" + formatString(device.inventoryNumber) + "\">\n" +
        "<div class=\"error\" id=\"unique_id\">At least one unique identifier must be filled out (Serial/IMEI/Inventory)</div>\n" +
        "</div>\n" +
        "\n" +
        "<div class=\"field-group\">\n" +
        "<label for=\"received_date\">Received date</label>\n" +
        "<input class=\"text\" type=\"date\" id=\"received_date\" name=\"received_date\" value=\"" + device.receivedDate + "\"/>\n" +
        "</div>\n" +
        "\n" +
        "<div class=\"field-group\">\n" +
        "<label for=\"received_from\">Received from</label>\n" +
        "<input class=\"text\" type=\"text\" id=\"received_from\" name=\"received_from\" title=\"received from\" value=\"" + formatString(device.receivedFrom) + "\">\n" +
        "</div>\n" +
        "\n" +
        "<div class=\"field-group\">\n" +
        "<label for=\"life_of_asset\">Useful life of asset</label>\n" +
        "<input class=\"text medium-field\" type=\"text\" id=\"life_of_asset\" name=\"life_of_asset\" title=\"life of asset\" value=\"" + formatString(device.usefulLiveOfAsset) + "\"> months\n" +
        "<div class=\"description\">Amount of months when this device is obsolete</div>\n" +
        "</div>\n";
    if (device.id != 0) {
        content += "<div class=\"field-group\">\n" +
            "<label for=\"sort-out-comment\">Sort out Comment</label>\n" +
            "<textarea class=\"textarea\" name=\"sort-out-comment\" id=\"sort-out-comment\" placeholder=\"Your comment here...\">" + formatString(device.sortedOutComment) + "</textarea>\n" +
            "</div>\n" +
            "<div class=\"field-group\">\n" +
            "<label for=\"sort-out-date\">Sort out date</label>\n" +
            "<input class=\"text\" type=\"date\" id=\"sort-out-date\" name=\"sort-out-date\" title=\"sort-out-date\">\n" +
            "</div>\n";
    }
    content += "</fieldset>\n" +
        "</form>";

    dialog.addHeader(header);
    dialog.addPanel("Panel 1", content, "panel-body");

    dialog.addButton("Save", function (dialog) {
        if ((AJS.$("#serial").val() || AJS.$("#imei").val() || AJS.$("#inventory").val()) && AJS.$("#hardware_selection").val()) {
            device.serialNumber = AJS.$("#serial").val();
            device.imei = AJS.$("#imei").val();
            device.inventoryNumber = AJS.$("#inventory").val();
            device.receivedDate = new Date(AJS.$("#received_date").val());
            device.receivedFrom = AJS.$("#received_from").auiSelect2("val");
            device.usefulLiveOfAsset = AJS.$("#life_of_asset").val();
            if (device.id != 0) {
                device.sortedOutComment = AJS.$("#sort-out-comment").val();
                device.sortedOutDate = new Date(AJS.$("#sort-out-date").val());
            }

            AJS.$.ajax({
                url: baseUrl + urlSuffixSingleHardwareModelDevices.replace("{0}", AJS.$("#hardware_selection").val()),
                type: "PUT",
                contentType: "application/json",
                data: JSON.stringify(device),
                dateType: "json",
                success: function () {
                    AJS.messages.success({
                        title: "Success!",
                        body: "Device added successfully"
                    });
                    fillOutAllTables(baseUrl);
                    dialog.remove();
                },
                error: function (error) {
                    AJS.messages.error("#dialog_error", {
                        title: "Error!",
                        body: error.responseText
                    });
                }
            });
        } else {
            AJS.messages.error("#dialog_error", {
                title: "Error!",
                body: "Please fill out above fields"
            });
        }
    }, "dialog_submit_button");
    dialog.addLink("Cancel", function (dialog) {
        dialog.remove();
    }, "#");

    dialog.gotoPage(0);
    dialog.gotoPanel(0);
    dialog.show();

    AJS.$("#received_date").attr("value", formatDateForForm(device.receivedDate));
    AJS.$("#sort-out-date").attr("value", formatDateForForm(device.sortedOutDate));

    if (device.hardwareModel.id) {
        AJS.$("#hardware_selection").val(device.hardwareModel.id);
    }

    if (editableDevice) {
        AJS.$("#unique_id").hide();
        AJS.$("#select_hardware_error").hide();
    }

    AJS.$(".device_id").change(function () {
        if (AJS.$("#serial").val() || AJS.$("#imei").val() || AJS.$("#inventory").val()) {
            AJS.$("#unique_id").hide();
        } else {
            AJS.$("#unique_id").show();
        }
    });

    AJS.$("#hardware_selection").change(function () {
        if (AJS.$(this).val()) {
            AJS.$("#select_hardware_error").hide();
        } else {
            AJS.$("#select_hardware_error").show();
        }
    });

    AJS.$("#received_from").auiSelect2({
        placeholder: "Search for existing entries",
        ajax: {
            url: baseUrl + urlSuffixReceivedFrom,
            dataType: "json",
            data: function (term, page) {
                return term;
            },
            results: function (data, page) {
                var select2data = [];
                select2data.push({id: null, text: "<none>"});
                for (var i = 0; i < data.length; i++) {
                    select2data.push({id: unescapeHtml(data[i].receivedFrom), text: unescapeHtml(data[i].receivedFrom)});
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
        },
        initSelection: function (element, callback) {
            callback({id: element.val(), text: element.val()});
        }
    }).auiSelect2("val", unescapeHtml(device.receivedFrom));
}
