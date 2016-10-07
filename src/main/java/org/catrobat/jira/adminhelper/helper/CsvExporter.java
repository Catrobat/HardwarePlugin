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

package org.catrobat.jira.adminhelper.helper;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import org.catrobat.jira.adminhelper.activeobject.Device;
import org.catrobat.jira.adminhelper.activeobject.DeviceComment;
import org.catrobat.jira.adminhelper.activeobject.HardwareModel;
import org.catrobat.jira.adminhelper.activeobject.Lending;

import java.util.List;

import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;

public class CsvExporter {

    public static final String DELIMITER = ";";
    public static final String NEW_LINE = "\n";
    private final List<Device> deviceList;
    private final UserManager userManager;

    public CsvExporter(final List<Device> deviceList, final UserManager userManager) {
        this.deviceList = deviceList;
        this.userManager = userManager;
    }

    public String getCsvString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name" + DELIMITER +
                "Version" + DELIMITER +
                "Type of Device" + DELIMITER +
                "Operating System" + DELIMITER +
                "Producer" + DELIMITER +
                "Article Number" + DELIMITER +
                "Price" + DELIMITER +
                "IMEI" + DELIMITER +
                "Serial Number" + DELIMITER +
                "Inventory Number" + DELIMITER +
                "Received Date" + DELIMITER +
                "Received From" + DELIMITER +
                "Useful Life Of Asset" + DELIMITER +
                "Sorted Out Comment" + DELIMITER +
                "Sorted Out Date" + DELIMITER +
                "Lending Begin" + DELIMITER +
                "Lending End" + DELIMITER +
                "Lending Purpose" + DELIMITER +
                "Lending Comment" + DELIMITER +
                "Lending Issuer" + DELIMITER +
                "Lent By" + DELIMITER +
                "Device Comment" + DELIMITER +
                "Device Comment Author" + DELIMITER +
                "Device Comment Date" + NEW_LINE);

        for (Device device : deviceList) {
            HardwareModel hardwareModel = device.getHardwareModel();
            sb.append(unescape(hardwareModel.getName())).append(DELIMITER);
            sb.append(unescape(hardwareModel.getVersion())).append(DELIMITER);
            if (hardwareModel.getTypeOfDevice() != null)
                sb.append(unescape(hardwareModel.getTypeOfDevice().getTypeOfDeviceName())).append(DELIMITER);
            else
                sb.append(DELIMITER);
            if (hardwareModel.getOperatingSystem() != null)
                sb.append(unescape(hardwareModel.getOperatingSystem().getOperatingSystemName())).append(DELIMITER);
            else
                sb.append(DELIMITER);
            if (hardwareModel.getProducer() != null)
                sb.append(unescape(hardwareModel.getProducer().getProducerName())).append(DELIMITER);
            else
                sb.append(DELIMITER);
            sb.append(unescape(hardwareModel.getArticleNumber())).append(DELIMITER);
            sb.append(unescape(hardwareModel.getPrice())).append(DELIMITER);
            sb.append(unescape(device.getImei())).append(DELIMITER);
            sb.append(unescape(device.getSerialNumber())).append(DELIMITER);
            sb.append(unescape(device.getInventoryNumber())).append(DELIMITER);
            sb.append(device.getReceivedDate()).append(DELIMITER);
            sb.append(unescape(device.getReceivedFrom())).append(DELIMITER);
            sb.append(unescape(device.getUsefulLifeOfAsset())).append(DELIMITER);
            sb.append(unescape(device.getSortedOutComment())).append(DELIMITER);
            sb.append(device.getSortedOutDate()).append(DELIMITER);

            Lending[] lending = device.getLendings();
            DeviceComment[] deviceComments = device.getDeviceComments();


            Lending latestLendingEntry = null;
            for (Lending lendingEntry : lending) {
                if (latestLendingEntry == null || lendingEntry.getBegin().after(latestLendingEntry.getBegin())) {
                    latestLendingEntry = lendingEntry;
                }
            }

            if (latestLendingEntry != null) {
                sb.append(latestLendingEntry.getBegin()).append(DELIMITER);
                sb.append(latestLendingEntry.getEnd()).append(DELIMITER);
                sb.append(unescape(latestLendingEntry.getPurpose())).append(DELIMITER);
                sb.append(unescape(latestLendingEntry.getComment())).append(DELIMITER);

                ApplicationUser lendingIssuerUser = userManager.getUserByKey(latestLendingEntry.getLendingIssuerUserKey());
                if (lendingIssuerUser != null) {
                    sb.append(lendingIssuerUser.getDisplayName()).append(DELIMITER);
                } else {
                    sb.append(unescape(latestLendingEntry.getLendingIssuerUserKey())).append(DELIMITER);
                }

                ApplicationUser lendingByUser = userManager.getUserByKey(latestLendingEntry.getLendingByUserKey());
                if (lendingByUser != null) {
                    sb.append(lendingByUser.getDisplayName()).append(DELIMITER);
                } else {
                    sb.append(unescape(latestLendingEntry.getLendingByUserKey())).append(DELIMITER);
                }

            } else {
                sb.append(DELIMITER + DELIMITER + DELIMITER + DELIMITER + DELIMITER + DELIMITER);
            }

            DeviceComment latestDeviceComment = null;
            for (DeviceComment deviceComment : deviceComments) {
                if (latestDeviceComment == null || deviceComment.getDate().after(latestDeviceComment.getDate())) {
                    latestDeviceComment = deviceComment;
                }
            }

            if (latestDeviceComment != null) {
                sb.append(unescape(latestDeviceComment.getComment())).append(DELIMITER);

                ApplicationUser author = userManager.getUserByKey(latestDeviceComment.getAuthor());
                if (author != null) {
                    sb.append(author.getDisplayName()).append(DELIMITER);
                } else {
                    sb.append(unescape(latestDeviceComment.getAuthor())).append(DELIMITER);
                }

                sb.append(latestDeviceComment.getDate()).append(NEW_LINE);
            } else {
                sb.append(DELIMITER + DELIMITER + NEW_LINE);
            }
        }

        return sb.toString();
    }

    private String unescape(String escapedHtml4String) {
        if (escapedHtml4String == null || escapedHtml4String.trim().length() == 0) {
            return "\"\"";
        } else {
            return "\"" + unescapeHtml4(escapedHtml4String).replaceAll("\"", "\"\"") + "\"";
        }
    }
}
