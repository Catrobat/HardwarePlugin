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

function showDeviceDetailDialog(baseUrl, deviceId) {
    AJS.$.ajax({
        url: baseUrl + urlSuffixSingleDevice.replace("{0}", deviceId),
        type: "GET",
        success: function (device) {
            showDeviceDetailDialogAjax(baseUrl, device)
        },
        error: function (error) {
            AJS.messages.error({
                title: "Error!",
                body: error.responseText
            });
        }
    });
}

function showDeviceDetailDialogAjax(baseUrl, device) {
    var dialog = new AJS.Dialog({
        width: 900,
        height: 460,
        id: "device-details-dialog",
        closeOnOutsideClick: true
    });

    var modelContent = "<table class=\"aui\">\n" +
        "    <tbody>\n" +
        "        <tr>\n" +
        "            <td>Name</td>\n" +
        "            <td>" + formatString(device.hardwareModel.name) + "</td>\n" +
        "        </tr>\n" +
        "        <tr>\n" +
        "            <td>Type</td>\n" +
        "            <td>" + formatString(device.hardwareModel.typeOfDevice) + "</td>\n" +
        "        </tr>\n" +
        "        <tr>\n" +
        "            <td>Version</td>\n" +
        "            <td>" + formatString(device.hardwareModel.version) + "</td>\n" +
        "        </tr>\n" +
        "        <tr>\n" +
        "            <td>Price</td>\n" +
        "            <td>" + formatString(device.hardwareModel.price) + "</td>\n" +
        "        </tr>\n" +
        "        <tr>\n" +
        "            <td>Producer</td>\n" +
        "            <td>" + formatString(device.hardwareModel.producer) + "</td>\n" +
        "        </tr>\n" +
        "        <tr>\n" +
        "            <td>OS</td>\n" +
        "            <td>" + formatString(device.hardwareModel.operatingSystem) + "</td>\n" +
        "        </tr>\n" +
        "        <tr>\n" +
        "            <td>Article Number</td>\n" +
        "            <td>" + formatString(device.hardwareModel.articleNumber) + "</td>\n" +
        "        </tr>\n" +
        "    </tbody>\n" +
        "</table>";

    var deviceContent = "<table class=\"aui\">\n" +
        "    <tbody>\n" +
        "        <tr>\n" +
        "            <td>Serial Number</td>\n" +
        "            <td>" + formatString(device.serialNumber) + "</td>\n" +
        "        </tr>\n" +
        "        <tr>\n" +
        "            <td>IMEI</td>\n" +
        "            <td>" + formatString(device.imei) + "</td>\n" +
        "        </tr>\n" +
        "        <tr>\n" +
        "            <td>Inventory Number</td>\n" +
        "            <td>" + formatString(device.inventoryNumber) + "</td>\n" +
        "        </tr>\n" +
        "        <tr>\n" +
        "            <td>Received Date</td>\n" +
        "            <td>" + getShortDate(device.receivedDate) + "</td>\n" +
        "        </tr>\n" +
        "        <tr>\n" +
        "            <td>Received From</td>\n" +
        "            <td>" + formatString(device.receivedFrom) + "</td>\n" +
        "        </tr>\n" +
        "        <tr>\n" +
        "            <td>Useful life of asset</td>\n" +
        "            <td>" + formatString(device.usefulLiveOfAsset) + "</td>\n" +
        "        </tr>\n" +
        "        <tr>\n" +
        "            <td>Sorted Out Date</td>\n" +
        "            <td>" + getShortDate(device.sortedOutDate) + "</td>\n" +
        "        </tr>\n" +
        "        <tr>\n" +
        "            <td>Sorted Out Comment</td>\n" +
        "            <td>" + formatString(device.sortedOutComment) + "</td>\n" +
        "        </tr>\n" +
        "    </tbody>\n" +
        "</table>\n" +
        "<div class=\"change-details\"><a class=\"change_details\" href=\"#\">change Device details</a></div>";

    var commentContent = "<table class=\"aui\">\n" +
        "    <thead>\n" +
        "        <tr>\n" +
        "            <th id=\"basic-author\">Author</th>\n" +
        "            <th id=\"basic-date\">Date</th>\n" +
        "            <th id=\"basic-comment\">Comment</th>\n" +
        "        </tr>\n" +
        "    </thead>\n" +
        "    <tbody>\n";
    for (var i = 0; i < device.comments.length; i++) {
        commentContent += "        <tr>\n" +
            "            <td headers=\"basic-author\">" + formatString(device.comments[i].author) + "</td>\n" +
            "            <td headers=\"basic-date\"><nobr>" + getShortDate(device.comments[i].date) + "</nobr></td>\n" +
            "            <td headers=\"basic-comment\">" + formatString(device.comments[i].comment) + "</td>\n" +
            "        </tr>\n";
    }
    commentContent += "    </tbody>\n" +
        "</table>";

    var historyContent = "<table class=\"aui\">\n" +
        "    <thead>\n" +
        "        <tr>\n" +
        "            <th id=\"basic-issuer\">Issuer</th>\n" +
        "            <th id=\"basic-by\">Lent out by</th>\n" +
        "            <th id=\"basic-begin\">Begin</th>\n" +
        "            <th id=\"basic-end\">End</th>\n" +
        "            <th id=\"basic-purpose\">Purpose</th>\n" +
        "            <th id=\"basic-comment\">Comment</th>\n" +
        "        </tr>\n" +
        "    </thead>\n" +
        "    <tbody>\n";
    for (var i = 0; i < device.lendings.length; i++) {
        historyContent += "        <tr>\n" +
            "            <td headers=\"basic-issuer\">" + formatString(device.lendings[i].lentOutIssuer) + "</td>\n" +
            "            <td headers=\"basic-by\">" + formatString(device.lendings[i].lentOutBy) + "</td>\n" +
            "            <td headers=\"basic-begin\"><nobr>" + getShortDate(device.lendings[i].begin) + "</nobr></td>\n" +
            "            <td headers=\"basic-end\"><nobr>" + getShortDate(device.lendings[i].end) + "</nobr></td>\n" +
            "            <td headers=\"basic-purpose\">" + formatString(device.lendings[i].purpose) + "</td>\n" +
            "            <td headers=\"basic-comment\">" + formatString(device.lendings[i].comment) + "</td>\n" +
            "        </tr>\n";
    }
    historyContent +=
        "    </tbody>\n" +
        "</table>";

    dialog.addHeader("Device Details");
    dialog.addPanel("Model", modelContent, "panel-body");
    dialog.addPanel("Device", deviceContent, "panel-body");
    dialog.addPanel("Device Comments", commentContent, "panel-body");
    dialog.addPanel("Lending History", historyContent, "panel-body");

    dialog.addButton("OK", function (dialog) {
        dialog.remove();
    });

    dialog.gotoPage(0);
    dialog.gotoPanel(0);
    dialog.show();

    AJS.$('.change_details').click(function (e) {
        e.preventDefault();
        dialog.remove();
        showNewDeviceDialog(baseUrl, device);
    });
}
