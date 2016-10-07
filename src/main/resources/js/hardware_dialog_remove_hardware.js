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

function showRemoveHardwareDialog(baseUrl, hardwareId) {
    // may be in background and therefore needs to be removed
    if (removeHardwareDialog) {
        try {
            removeHardwareDialog.remove();
        } catch (err) {
            // may be removed already
        }
    }

    AJS.$.ajax({
        url: baseUrl + urlSuffixHardwareModels,
        contentType: "application/json",
        success: function (hardwareList) {
            showRemoveHardwareDialogAjax(baseUrl, hardwareId, hardwareList);
        },
        error: function (error) {
            AJS.messages.error({
                title: "Error!",
                body: error.responseText
            });
        }
    });
}

function showRemoveHardwareDialogAjax(baseUrl, hardwareId, hardwareList) {
    removeHardwareDialog = new AJS.Dialog({
        width: 600,
        height: 250,
        id: "remove-hardware-dialog",
        closeOnOutsideClick: true
    });

    var content = "";
    var contentOptions = "";
    var hardwareToRemove;
    for (var i = 0; i < hardwareList.length; i++) {
        var version = hardwareList[i].version ? " (" + hardwareList[i].version + ")" : "";
        if (hardwareId == hardwareList[i].id) {
            hardwareToRemove = hardwareList[i];
            content += "<form class=\"aui\">\n" +
                "<p>Are you sure you want to delete " + hardwareList[i].name + version + "?</p>\n";
            if (hardwareToRemove.sumOfDevices != 0) {
                content += "<p>All allocated devices need to be moved to another Hardware Model.\n" +
                    "<fieldset>\n" +
                    "<div class=\"field-group\">\n" +
                    "<label for=\"move-hardware\">Move to</label>\n" +
                    "<select class=\"select\" id=\"move-hardware\" name=\"move-hardware\" title=\"move-hardware\">\n" +
                    "<option>Select</option>\n";
            }
        } else {
            contentOptions += "<option value=\"" + hardwareList[i].id + "\">" + hardwareList[i].name + version + "</option>\n";
        }
    }
    if (hardwareToRemove.sumOfDevices != 0) {
        content += contentOptions;
    }
    content += "</select>\n" +
        "</div>\n" +
        "</fieldset></p>\n" +
        "</form>";

    removeHardwareDialog.addHeader("Remove Hardware Model");
    removeHardwareDialog.addPanel("Panel 1", content, "panel-body");


    removeHardwareDialog.addButton("Remove", function (dialog) {
        AJS.$.ajax({
            type: "DELETE",
            url: baseUrl + urlSuffixSingleHardwareModel.replace("{0}", hardwareId),
            contentType: "application/json",
            data: JSON.stringify({id: parseInt(AJS.$("#move-hardware").val())}),
            success: function () {
                AJS.messages.success({
                    title: "Success!",
                    body: "Hardware removed successfully"
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
    });
    removeHardwareDialog.addLink("Cancel", function (dialog) {
        dialog.remove();
    }, "#");

    removeHardwareDialog.gotoPage(0);
    removeHardwareDialog.gotoPanel(0);
    removeHardwareDialog.show();
}
