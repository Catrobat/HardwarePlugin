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

function showReturnDialog(baseUrl, deviceId) {
    AJS.$.ajax({
        url: baseUrl + urlSuffixSingleDeviceCurrentLending.replace("{0}", deviceId),
        type: "GET",
        success: function (lendingDetails) {
            showReturnDialogAjax(baseUrl, deviceId, lendingDetails)
        },
        error: function (error) {
            AJS.messages.error({
                title: "Error!",
                body: error.responseText
            });
        }
    });
}

function showReturnDialogAjax(baseUrl, deviceId, lendingDetails) {
    // may be in background and therefore needs to be removed
    if (returnDialog) {
        try {
            returnDialog.remove();
        } catch (err) {
            // may be removed already
        }
    }

    returnDialog = new AJS.Dialog({
        width: 600,
        height: 400,
        id: "returning-device-dialog",
        closeOnOutsideClick: true
    });

    var content = "<form action=\"#\" method=\"post\" id=\"d\" class=\"aui\">\n" +
        "    <fieldset>\n" +
        "        <div class=\"field-group\">\n" +
        "            <label for=\"device-comment\">Device Comment</label>\n" +
        "            <textarea class=\"textarea\" name=\"device-comment\" id=\"device-comment\" placeholder=\"Your comment here...\"></textarea>\n" +
        "        </div>\n" +
        "        <div class=\"field-group\">\n" +
        "            <label for=\"end-date\">End Date</label>\n" +
        "            <input class=\"text\" type=\"date\" id=\"end-date\" name=\"end-date\" title=\"end-date\">\n" +
        "        </div>\n" +
        "        <div class=\"field-group\">\n" +
        "            <label for=\"lending-purpose\">Lending Purpose</label>\n" +
        "            <input class=\"text\" type=\"text\" id=\"lending-purpose\" name=\"lending-purpose\" title=\"lending-purpose\">\n" +
        "        </div>\n" +
        "        <div class=\"field-group\">\n" +
        "            <label for=\"lending-comment\">Lending Comment</label>\n" +
        "            <textarea class=\"textarea\" name=\"lending-comment\" id=\"lending-comment\" placeholder=\"Your comment here...\"></textarea>\n" +
        "        </div>\n" +
        "    </fieldset>\n" +
        " </form> ";

    returnDialog.addHeader("Returning Device");
    returnDialog.addPanel("Panel 1", content, "panel-body");

    returnDialog.addButton("Save", function (dialog) {
        var lending = {
            end: new Date(AJS.$("#end-date").val()),
            purpose: AJS.$("#lending-purpose").val(),
            comment: AJS.$("#lending-comment").val(),
            device: {comments: [
                {comment: AJS.$("#device-comment").val()}
            ]}
        };
        AJS.$.ajax({
            url: baseUrl + urlSuffixSingleDeviceCurrentLending.replace("{0}", deviceId),
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify(lending),
            success: function () {
                AJS.messages.success({
                    title: "Success!",
                    body: "Device brought back successfully"
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
    returnDialog.addLink("Cancel", function (dialog) {
        dialog.remove();
    }, "#");

    var now = new Date();
    var day = ("0" + now.getDate()).slice(-2);
    var month = ("0" + (now.getMonth() + 1)).slice(-2);
    var today = now.getFullYear() + "-" + (month) + "-" + (day);
    AJS.$("#end-date").attr("value", today);

    if (lendingDetails.purpose) {
        AJS.$("#lending-purpose").val(unescapeHtml(lendingDetails.purpose));

    }

    if (lendingDetails.comment) {
        AJS.$("#lending-comment").val(unescapeHtml(lendingDetails.comment));
    }

    returnDialog.gotoPage(0);
    returnDialog.gotoPanel(0);
    returnDialog.show();
}
