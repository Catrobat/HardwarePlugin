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

function showSortoutDialog(baseUrl, deviceId) {
    // may be in background and therefore needs to be removed
    if (sortOutDialog) {
        try {
            sortOutDialog.remove();
        } catch (err) {
            // may be removed already
        }
    }

    sortOutDialog = new AJS.Dialog({
        width: 600,
        height: 300,
        id: "sort-out-dialog",
        closeOnOutsideClick: true
    });

    var content = "<form class=\"aui\">\n" +
        "    <fieldset>\n" +
        "        <div class=\"field-group\">\n" +
        "            <label for=\"sort-out-comment\">Sort out Comment</label>\n" +
        "            <textarea class=\"textarea\" name=\"sort-out-comment\" id=\"sort-out-comment\" placeholder=\"Your comment here...\"></textarea>\n" +
        "        </div>\n" +
        "        <div class=\"field-group\">\n" +
        "            <label for=\"sort-out-date\">Sort out date</label>\n" +
        "            <input class=\"text\" type=\"date\" id=\"sort-out-date\" name=\"sort-out-date\" title=\"sort-out-date\">\n" +
        "        </div>\n" +
        "    </fieldset>\n" +
        " </form> ";

    sortOutDialog.addHeader("Sort Out Device");
    sortOutDialog.addPanel("Panel 1", content, "panel-body");

    sortOutDialog.addButton("Save", function (dialog) {
        var device = {
            sortedOutDate: new Date(AJS.$("#sort-out-date").val()),
            sortedOutComment: AJS.$("#sort-out-comment").val()
        };
        AJS.$.ajax({
            url: baseUrl + urlSuffixSingleDeviceSortOut.replace("{0}", deviceId),
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify(device),
            success: function () {
                AJS.messages.success({
                    title: "Success!",
                    body: "Device sorted out successfully"
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
    sortOutDialog.addLink("Cancel", function (dialog) {
        dialog.remove();
    }, "#");

    AJS.$("#sort-out-date").attr("value", formatDateForForm(new Date()));

    sortOutDialog.gotoPage(0);
    sortOutDialog.gotoPanel(0);
    sortOutDialog.show();
}
