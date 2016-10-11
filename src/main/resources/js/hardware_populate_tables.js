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

function populateOverviewTable(hardwareList) {
    var tableBody = "";

    for (var i = 0; i < hardwareList.length; i++) {
        tableBody += "<tr>\n" +
            "<td class=\"name\">" + formatString(hardwareList[i].name) + "</td>\n" +
            "<td class=\"version\">" + formatString(hardwareList[i].version) + "</td>\n" +
            "<td class=\"type\">" + formatString(hardwareList[i].typeOfDevice) + "</td>\n" +
            "<td class=\"os\">" + formatString(hardwareList[i].operatingSystem) + "</td>\n" +
            "<td class=\"available\">" + hardwareList[i].available + "/" + hardwareList[i].sumOfDevices + "</td>\n";
        if(hardwareList[i].available === 0) {
            tableBody += "<td></td>";
        } else {
            tableBody += "<td><a class=\"lending_out\" id=\"" + hardwareList[i].id + "\" href=\"#\">Lending out</a></td>\n";
        }
        tableBody += "</tr>";

    }

    AJS.$("#table-overview-body").html(tableBody);
    AJS.$("#table-overview").trigger("update");

    var overviewList = new List("tabs-overview", {page: Number.MAX_VALUE, valueNames: ["name", "version", "type", "os"]});
    overviewList.on('updated', function() {
        AJS.$("#table-overview").trigger("update");
    });
}

function populateLentOutTable(hardwareList) {
    var tableBody = "";

    for (var i = 0; i < hardwareList.length; i++) {
        var currentlyLentOutSince = new Date(hardwareList[i].currentlyLentOutSince);
        tableBody += "<tr>\n" +
            "<td class=\"name\">" + formatString(hardwareList[i].hardwareModelName) + "</td>\n" +
            "<td class=\"serial\">" + formatString(hardwareList[i].serialNumber) + "</td>\n" +
            "<td class=\"imei\">" + formatString(hardwareList[i].imei) + "</td>\n" +
            "<td class=\"inventory\">" + formatString(hardwareList[i].inventoryNumber) + "</td>\n" +
            "<td class=\"lent-out-by\">" + formatString(hardwareList[i].currentlyLentOutBy) + "</td>\n" +
            "<td class=\"lent-out-since\">" + currentlyLentOutSince.toISOString().split("T")[0] + "</td>\n" +
            "<td class=\"lent-out-purpose\">" + formatString(hardwareList[i].currentlyLentOutPurpose) + "</td>\n" +
            "<td><a class=\"device_details\" id=\"" + hardwareList[i].id + "\" href=\"#\">Details</a></td>\n" +
            "<td><a class=\"device_return\" id=\"" + hardwareList[i].id + "\" href=\"#\">Return</a></td>\n" +
            "</tr>";
    }

    AJS.$("#table-lent-out").html(tableBody);
    AJS.$("#table-lent-out").trigger("update");

    var lentOutList = new List("tabs-lent-out", {page: Number.MAX_VALUE, valueNames: ["name", "serial", "imei", "inventory", "lent-out-by", "lent-out-since", "lent-out-purpose"]});
    lentOutList.on('updated', function() {
        AJS.$("#table-lent-out").trigger("update");
    });
}

function populateSortedOutTable(deviceList) {
    var tableBody = "";

    for (var i = 0; i < deviceList.length; i++) {
        var sortedOut = new Date(deviceList[i].sortedOutDate);
        tableBody += "<tr>\n" +
            "<td class=\"name\">" + formatString(deviceList[i].hardwareModelName) + "</td>\n" +
            "<td class=\"serial\">" + formatString(deviceList[i].serialNumber) + "</td>\n" +
            "<td class=\"imei\">" + formatString(deviceList[i].imei) + "</td>\n" +
            "<td class=\"inventory\">" + formatString(deviceList[i].inventoryNumber) + "</td>\n" +
            "<td class=\"received-from\">" + formatString(deviceList[i].receivedFrom) + "</td>\n" +
            "<td class=\"date\">" + sortedOut.toISOString().split("T")[0] + "</td>\n" +
            "<td><a class=\"device_details\" id=\"" + deviceList[i].id + "\" href=\"#\">Details</a></td>\n" +
            "</tr>";
    }

    AJS.$("#table-sorted-out").html(tableBody);
    AJS.$("#table-sorted-out").trigger("update");

    var sortedOutList = new List("tabs-sorted-out", {page: Number.MAX_VALUE, valueNames: ["name", "serial", "imei", "inventory", "received-from", "date"]});
    sortedOutList.on('updated', function() {
        AJS.$("#table-sorted-out").trigger("update");
    });
}

function populateActiveDevicesTable(deviceList) {
    var tableBody = "";

    for (var i = 0; i < deviceList.length; i++) {
        if(typeof deviceList[i].sortedOutDate != 'undefined') {
            continue;
        }

        var action = "";
        if (deviceList[i].currentlyLentOutBy) {
            action = "<a class=\"device_return\" id=\"" + deviceList[i].id + "\" href=\"#\">Return device</a>";
        } else {
            action = "<a class=\"direct_lending_out\" id=\"" + deviceList[i].hardwareModelId + "," +
                deviceList[i].id + "\" href=\"#\">Lending out</a> / <a class=\"device_sort_out\" id=\"" + deviceList[i].id + "\" href=\"#\">Sort out</a>";
        }

        tableBody += "<tr>\n" +
            "<td class=\"name\">" + formatString(deviceList[i].hardwareModelName) + "</td>\n" +
            "<td class=\"serial\">" + formatString(deviceList[i].serialNumber) + "</td>\n" +
            "<td class=\"imei\">" + formatString(deviceList[i].imei) + "</td>\n" +
            "<td class=\"inventory\">" + formatString(deviceList[i].inventoryNumber) + "</td>\n" +
            "<td class=\"received-from\">" + formatString(deviceList[i].receivedFrom) + "</td>\n" +
            "<td class=\"lent-out-since\">" + getShortDate(deviceList[i].currentlyLentOutSince) + "</td>\n" +
            "<td class=\"lent-out-by\">" + formatString(deviceList[i].currentlyLentOutBy) + "</td>\n" +
            "<td class=\"action\">" + action + "</td>\n" +
            "<td><a class=\"device_details\" id=\"" + deviceList[i].id + "\" href=\"#\">Details</a></td>\n" +
            "</tr>";
    }

    AJS.$("#table-active-devices").html(tableBody);
    AJS.$("#table-active-devices").trigger("update");

    var activeDevicesList = new List("tabs-active-devices", {page: Number.MAX_VALUE, valueNames: ["name", "serial", "imei", "inventory", "received-from", "lent-out-since", "lent-out-by", "action"]});
    activeDevicesList.on('updated', function() {
        AJS.$("#table-active-devices").trigger("update");
    });
}

function populateAllDevicesTable(deviceList) {
    var tableBody = "";

    for (var i = 0; i < deviceList.length; i++) {
        var action = "";
        if (deviceList[i].currentlyLentOutBy) {
            action = "<a class=\"device_return\" id=\"" + deviceList[i].id + "\" href=\"#\">Return device</a>";
        } else if (!deviceList[i].sortedOutDate) {
            action = "<a class=\"direct_lending_out\" id=\"" + deviceList[i].hardwareModelId + "," +
                deviceList[i].id + "\" href=\"#\">Lending out</a> / <a class=\"device_sort_out\" id=\"" + deviceList[i].id + "\" href=\"#\">Sort out</a>";
        }

        tableBody += "<tr>\n" +
            "<td class=\"name\">" + formatString(deviceList[i].hardwareModelName) + "</td>\n" +
            "<td class=\"serial\">" + formatString(deviceList[i].serialNumber) + "</td>\n" +
            "<td class=\"imei\">" + formatString(deviceList[i].imei) + "</td>\n" +
            "<td class=\"inventory\">" + formatString(deviceList[i].inventoryNumber) + "</td>\n" +
            "<td class=\"received-from\">" + formatString(deviceList[i].receivedFrom) + "</td>\n" +
            "<td class=\"lent-out-since\">" + getShortDate(deviceList[i].currentlyLentOutSince) + "</td>\n" +
            "<td class=\"lent-out-by\">" + formatString(deviceList[i].currentlyLentOutBy) + "</td>\n" +
            "<td class=\"sorted-out\">" + getShortDate(deviceList[i].sortedOutDate) + "</td>\n" +
            "<td class=\"action\">" + action + "</td>\n" +
            "<td><a class=\"device_details\" id=\"" + deviceList[i].id + "\" href=\"#\">Details</a></td>\n" +
            "</tr>";
    }

    AJS.$("#table-all-devices").html(tableBody);
    AJS.$("#table-all-devices").trigger("update");

    var allDevicesList = new List("tabs-all-devices", {page: Number.MAX_VALUE, valueNames: ["name", "serial", "imei", "inventory", "received-from", "lent-out-since", "lent-out-by", "sorted-out", "action"]});
    allDevicesList.on('updated', function() {
        AJS.$("#table-all-devices").trigger("update");
    });
}

function populateHardwareManagementTable(hardwareList) {
    var tableBody = "";

    for (var i = 0; i < hardwareList.length; i++) {
        tableBody += "<tr>\n" +
            "<td class=\"name\">" + formatString(hardwareList[i].name) + "</td>\n" +
            "<td class=\"version\">" + formatString(hardwareList[i].version) + "</td>\n" +
            "<td class=\"type\">" + formatString(hardwareList[i].typeOfDevice) + "</td>\n" +
            "<td class=\"os\">" + formatString(hardwareList[i].operatingSystem) + "</td>\n" +
            "<td>" + hardwareList[i].sumOfDevices + "</td>\n" +
            "<td><a class=\"edit_model\" id=\"" + hardwareList[i].id + "\" href=\"#\">Edit</a></td>\n" +
            "<td><a class=\"remove_model\" id=\"" + hardwareList[i].id + "\" href=\"#\">Remove</a></td>\n" +
            "</tr>";
    }

    AJS.$("#table-management").html(tableBody);
    AJS.$("#table-management").trigger("update");

    var managementList = new List("tabs-hardware-management", {page: Number.MAX_VALUE, valueNames: ["name", "version", "type", "os"]});
    managementList.on('updated', function() {
        AJS.$("#table-management").trigger("update");
    });
}
