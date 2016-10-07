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

var lendingOutDialog;
var createHardwareDialog;
var returnDialog;
var removeHardwareDialog;
var sortOutDialog;

var urlRest = "/rest/admin-helper/latest";
var urlRestHardware = urlRest + "/hardware";
var urlSuffixUserSearch = urlRest + "/user/search";

var urlSuffixHardwareModels = urlRestHardware;
var urlSuffixSingleHardwareModel = urlRestHardware + "/{0}";
var urlSuffixSingleHardwareModelDevices = urlRestHardware + "/{0}/devices";
var urlSuffixSingleHardwareModelDevicesAvailable = urlRestHardware + "/{0}/devices/available";

var urlSuffixDevices = urlRestHardware + "/devices";
var urlSuffixDevicesOngoingLending = urlRestHardware + "/devices/ongoing-lending";
var urlSuffixDevicesSortedOut = urlRestHardware + "/devices/sorted-out";
var urlSuffixSingleDevice = urlRestHardware + "/devices/{0}";
var urlSuffixSingleDeviceLendOut = urlRestHardware + "/devices/{0}/lend-out";
var urlSuffixLendingForUser = urlRestHardware + "/lending/all-for-user";
var urlSuffixSingleDeviceCurrentLending = urlRestHardware + "/devices/{0}/current-lending";
var urlSuffixSingleDeviceSortOut = urlRestHardware + "/devices/{0}/sort-out";

var urlSuffixTypes = urlRestHardware + "/types";
var urlSuffixProducers = urlRestHardware + "/producers";
var urlSuffixOperatingSystems = urlRestHardware + "/operating-systems";
var urlSuffixReceivedFrom = urlRestHardware + "/received-from";

AJS.toInit(function () {
    var baseUrl = AJS.$("meta[name='application-base-url']").attr("content");
    fillOutAllTables(baseUrl);
    handleEvents(baseUrl);
    initIndividualRelatedLendingTab(baseUrl);
});

function fillOutAllTables(baseUrl) {
    AJS.$.ajax({
        url: baseUrl + urlSuffixHardwareModels,
        type: "GET",
        success: function (hardwareList) {
            populateOverviewTable(hardwareList);
            populateHardwareManagementTable(hardwareList);
        },
        error: function (error) {
            AJS.messages.error({
                title: "Error!",
                body: error.responseText
            });
        }
    });

    AJS.$.ajax({
        url: baseUrl + urlSuffixDevicesOngoingLending,
        type: "GET",
        success: function (deviceList) {
            populateLentOutTable(deviceList);
        },
        error: function (error) {
            AJS.messages.error({
                title: "Error!",
                body: error.responseText
            });
        }
    });

    AJS.$.ajax({
        url: baseUrl + urlSuffixDevicesSortedOut,
        type: "GET",
        success: function (deviceList) {
            populateSortedOutTable(deviceList);
        },
        error: function (error) {
            AJS.messages.error({
                title: "Error!",
                body: error.responseText
            });
        }
    });

    AJS.$.ajax({
        url: baseUrl + urlSuffixDevices,
        type: "GET",
        success: function (deviceList) {
            populateActiveDevicesTable(deviceList);
            populateAllDevicesTable(deviceList);
        },
        error: function (error) {
            AJS.messages.error({
                title: "Error!",
                body: error.responseText
            });
        }
    });
}

function initIndividualRelatedLendingTab(baseUrl) {
    var individualList;
    AJS.$("#individual-lending-search").auiSelect2({
        placeholder: "Search for user",
        minimumInputLength: 1,
        ajax: {
            url: baseUrl + urlSuffixUserSearch,
            dataType: "json",
            type: "POST",
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
        }
    })
        .on("select2-selecting", function (e) {
            AJS.$.ajax({
                url: baseUrl + urlSuffixLendingForUser,
                type: "POST",
                dataType: "json",
                data: {user: unescapeHtml(e.val)},
                success: function (lendingList) {
                    if(lendingList.length == 0) {
                        AJS.$("#table-individual").html('<tr><td colspan="10">No entry found for this user.</td></tr>');
                    } else {
                        var tableHtml = "";
                        for(var i = 0; i < lendingList.length; i++) {
                            tableHtml += '<tr><td class="name">' + formatString(lendingList[i].device.hardwareModelName) + '</td>' +
                                '<td class="serial">' + formatString(lendingList[i].device.serialNumber) + '</td>' +
                                '<td class="imei">' + formatString(lendingList[i].device.imei) + '</td>' +
                                '<td class="inventory">' + formatString(lendingList[i].device.inventoryNumber) + '</td>' +
                                '<td class="issuer">' + formatString(lendingList[i].lentOutIssuer) + '</td>' +
                                '<td class="begin">' + getShortDate(lendingList[i].begin) + '</td>' +
                                '<td class="end">' + getShortDate(lendingList[i].end) + '</td>' +
                                '<td class="purpose">' + formatString(lendingList[i].purpose) + '</td>' +
                                '<td class="comment">' + formatString(lendingList[i].comment) + '</td>' +
                                '<td><a class="device_details" id="' + lendingList[i].device.id + '" href="#">Details</a></td></tr>';
                        }
                        AJS.$("#table-individual").html(tableHtml);
                    }
                    AJS.$("#search-filter-individual").val('');
                    individualList = new List("tabs-individual-lending", {page: Number.MAX_VALUE, valueNames: ["name", "serial", "imei",
                        "inventory", "issuer", "begin", "end", "purpose", "comment"]});
                    AJS.$("#table-individual").trigger("update");
                },
                error: function (error) {
                    AJS.messages.error({
                        title: "Error!",
                        body: error.responseText
                    });
                }
            });
        });

    AJS.$("#table-individual").html('<tr><td colspan="10">Please use above input mask to search for an user.</td></tr>');
    AJS.$("#table-individual").trigger("update");
}

function handleEvents(baseUrl) {
    AJS.$(document).on("click", ".lending_out", function (e) {
        e.preventDefault();
        showLendoutDialog(baseUrl, e.target.id);
    });

    AJS.$(document).on("click", ".direct_lending_out", function (e) {
        var hardwareId = e.target.id.split(",")[0];
        var deviceId = e.target.id.split(",")[1];
        e.preventDefault();
        showLendoutDialog(baseUrl, hardwareId, deviceId);
    });

    AJS.$(document).on("click", ".device_sort_out", function (e) {
        e.preventDefault();
        showSortoutDialog(baseUrl, e.target.id);
    });

    AJS.$(document).on("click", ".device_return", function (e) {
        e.preventDefault();
        showReturnDialog(baseUrl, e.target.id);
    });

    AJS.$(document).on("click", ".device_details", function (e) {
        e.preventDefault();
        showDeviceDetailDialog(baseUrl, e.target.id);
    });

    AJS.$(document).on("click", ".edit_model", function (e) {
        e.preventDefault();
        showNewHardwareDialog(baseUrl, e.target.id);
    });

    AJS.$(document).on("click", "#new_model", function (e) {
        e.preventDefault();
        showNewHardwareDialog(baseUrl);
    });

    AJS.$(document).on("click", ".remove_model", function (e) {
        e.preventDefault();
        showRemoveHardwareDialog(baseUrl, e.target.id);
    });

    AJS.$("#new_device").click(function (e) {
        e.preventDefault();
        showNewDeviceDialog(baseUrl);
    });
}

function getShortDate(dateString) {
    if (typeof dateString === "string") {
        return dateString.split("T")[0];
    }

    return "";
}

function formatDateForForm(dateString) {
    if(typeof dateString === 'undefined') {
        return '';
    }
    var date = new Date(dateString);
    var day = ("0" + date.getDate()).slice(-2);
    var month = ("0" + (date.getMonth() + 1)).slice(-2);
    return date.getFullYear() + "-" + (month) + "-" + (day);
}

function formatString(string) {
    if(typeof string === 'undefined') {
        return '';
    }
    return string;
}

function unescapeHtml(safe) {
    if(safe) {
        return AJS.$('<div />').html(safe).text();
    } else {
        return '';
    }
}
