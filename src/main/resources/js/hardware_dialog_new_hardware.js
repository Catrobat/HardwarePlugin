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

function showNewHardwareDialog(baseUrl, hardwareId) {
    if (typeof hardwareId !== 'undefined') {
        AJS.$.ajax({
            url: baseUrl + urlSuffixSingleHardwareModel.replace("{0}", hardwareId),
            type: "GET",
            success: function (hardwareModel) {
                showNewHardwareDialogAjax(baseUrl, hardwareModel);
            },
            error: function (error) {
                AJS.messages.error({
                    title: "Error!",
                    body: error.responseText
                });
            }
        });
    } else {
        showNewHardwareDialogAjax(baseUrl);
    }
}

function showNewHardwareDialogAjax(baseUrl, editableHardwareModel) {
    // may be in background and therefore needs to be removed
    if (createHardwareDialog) {
        try {
            createHardwareDialog.remove();
        } catch (err) {
            // may be removed already
        }
    }

    var hardwareModel;
    if (editableHardwareModel) {
        hardwareModel = editableHardwareModel;
    } else {
        hardwareModel = {
            name: "",
            typeOfDevice: "",
            version: "",
            price: "",
            producer: "",
            operatingSystem: "",
            articleNumber: ""
        };
    }

    createHardwareDialog = new AJS.Dialog({
        width: 600,
        height: 600,
        id: "example-dialog",
        closeOnOutsideClick: true
    });

    var content = "<form action=\"#\" method=\"post\" id=\"d\" class=\"aui\">\n" +
        "        <fieldset>\n" +
        "            <div class=\"field-group\">\n" +
        "                <label for=\"name\">Name<span class=\"aui-icon icon-required\"> required</span></label>\n" +
        "                <input class=\"text\" type=\"text\" id=\"name\" name=\"name\" title=\"name\" value=\"" + hardwareModel.name + "\">\n" +
        "\n" +
        "                <div class=\"description\">Name or short term for describing the hardware type</div>\n" +
        "            </div>\n" +
        "\n" +
        "            <div class=\"field-group\">\n" +
        "                <label for=\"type-of-device\">Type of device</label>\n" +
        "                <input class=\"text\" type=\"text\" id=\"type-of-device\" name=\"type-of-device\" title=\"type-of-device\">\n" +
        "\n" +
        "                <div class=\"description\">Type of device class for better grouping</div>\n" +
        "            </div>\n" +
        "\n" +
        "            <div class=\"field-group\">\n" +
        "                <label for=\"version\">Version</label>\n" +
        "                <input class=\"text\" type=\"text\" id=\"version\" name=\"version\" title=\"version\" value=\"" + hardwareModel.version + "\">\n" +
        "\n" +
        "                <div class=\"description\">e.g. 8 GB/16 GB Version</div>\n" +
        "            </div>\n" +
        "\n" +
        "            <div class=\"field-group\">\n" +
        "                <label for=\"price\">Price</label>\n" +
        "                <input class=\"text\" type=\"text\" id=\"price\" name=\"price\" title=\"price\" value=\"" + hardwareModel.price + "\">\n" +
        "                <div class=\"description\">For better comparability use Euro as currency</div>\n" +
        "            </div>\n" +
        "\n" +
        "            <div class=\"field-group\">\n" +
        "                <label for=\"producer\">Producer</label>\n" +
        "                <input class=\"text\" type=\"text\" id=\"producer\" name=\"producer\" title=\"producer\">\n" +
        "\n" +
        "                <div class=\"description\">Main Producer or brand</div>\n" +
        "            </div>\n" +
        "\n" +
        "            <div class=\"field-group\">\n" +
        "                <label for=\"operating-system\">Operating System</label>\n" +
        "                <input class=\"text\" type=\"text\" id=\"operating-system\" name=\"operating-system\" title=\"operating-system\">\n" +
        "\n" +
        "                <div class=\"description\">Android/iOS/Windows Phone</div>\n" +
        "            </div>\n" +
        "\n" +
        "            <div class=\"field-group\">\n" +
        "                <label for=\"article-number\">Article number</label>\n" +
        "                <input class=\"text\" type=\"text\" id=\"article-number\" name=\"article-number\" title=\"article-number\" value=\"" + hardwareModel.articleNumber + "\">\n" +
        "\n" +
        "                <div class=\"description\">Unique item number for product (e.g. GTIN)</div>\n" +
        "            </div>\n" +
        "        </fieldset>\n" +
        "    </form>";

    createHardwareDialog.addHeader("Create/Edit Hardware Model");
    createHardwareDialog.addPanel("Panel 1", content, "panel-body");

    createHardwareDialog.addButton("Save", function (dialog) {
        hardwareModel.name = AJS.$("#name").val();
        hardwareModel.typeOfDevice = AJS.$("#type-of-device").auiSelect2("val");
        hardwareModel.version = AJS.$("#version").val();
        hardwareModel.price = AJS.$("#price").val();
        hardwareModel.producer = AJS.$("#producer").auiSelect2("val");
        hardwareModel.operatingSystem = AJS.$("#operating-system").auiSelect2("val");
        hardwareModel.articleNumber = AJS.$("#article-number").val();

        AJS.$.ajax({
            url: baseUrl + urlSuffixHardwareModels,
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify(hardwareModel),
            dataType: "json",
            success: function () {
                AJS.messages.success({
                    title: "Success!",
                    body: "Hardware added successfully"
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

        createHardwareDialog.remove();
    });
    createHardwareDialog.addLink("Cancel", function (dialog) {
        createHardwareDialog.remove();
    }, "#");

    AJS.$("#type-of-device").auiSelect2({
        placeholder: "Existing types of devices",
        ajax: {
            url: baseUrl + urlSuffixTypes,
            dataType: "json",
            data: function (term, page) {
                return term;
            },
            results: function (data, page) {
                var select2data = [];
                for (var i = 0; i < data.length; i++) {
                    select2data.push({id: unescapeHtml(data[i]), text: unescapeHtml(data[i])});
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
    }).auiSelect2("val", unescapeHtml(hardwareModel.typeOfDevice));

    AJS.$("#producer").auiSelect2({
        placeholder: "Existing producers",
        ajax: {
            url: baseUrl + urlSuffixProducers,
            dataType: "json",
            data: function (term, page) {
                return term;
            },
            results: function (data, page) {
                var select2data = [];
                for (var i = 0; i < data.length; i++) {
                    select2data.push({id: unescapeHtml(data[i]), text: unescapeHtml(data[i])});
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
    }).auiSelect2("val", unescapeHtml(hardwareModel.producer));

    AJS.$("#operating-system").auiSelect2({
        placeholder: "Existing operating systems",
        ajax: {
            url: baseUrl + urlSuffixOperatingSystems,
            dataType: "json",
            data: function (term, page) {
                return term;
            },
            results: function (data, page) {
                var select2data = [];
                for (var i = 0; i < data.length; i++) {
                    select2data.push({id: unescapeHtml(data[i]), text: unescapeHtml(data[i])});
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
    }).auiSelect2("val", unescapeHtml(hardwareModel.operatingSystem));

    createHardwareDialog.gotoPage(0);
    createHardwareDialog.gotoPanel(0);
    createHardwareDialog.show();
}
