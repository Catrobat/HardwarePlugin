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

package org.catrobat.jira.adminhelper.activeobject;

import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.Query;
import org.catrobat.jira.adminhelper.helper.HelperUtil;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class DeviceServiceImpl implements DeviceService {

    // TODO issues with postgresql
//    private static final String DEFAULT_ORDER = "upper(hardware.NAME), upper(hardware.VERSION), " +
//            "upper(device.SERIAL_NUMBER), upper(device.IMEI), upper(device.INVENTORY_NUMBER)";
    private final ActiveObjects ao;

    public DeviceServiceImpl(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }

    @Override
    public Device getDeviceById(int deviceId) {
        Device[] devices = ao.find(Device.class, Query.select().where("ID = ?", deviceId));
        if (devices.length == 1) {
            return devices[0];
        }

        return null;
    }

    @Override
    public Device edit(Device device, HardwareModel hardwareModel, String imei, String serialNumber, String inventoryNumber,
                       String receivedFrom, Date receivedDate, String usefulLifeOfAsset, Date sortedOutDate, String sortedOutComment) {
        if (device == null || hardwareModel == null) {
            return null;
        }

        imei = HelperUtil.formatString(imei);
        serialNumber = HelperUtil.formatString(serialNumber);
        inventoryNumber = HelperUtil.formatString(inventoryNumber);
        receivedFrom = HelperUtil.formatString(receivedFrom);
        usefulLifeOfAsset = HelperUtil.formatString(usefulLifeOfAsset);
        sortedOutComment = HelperUtil.formatString(sortedOutComment);

        if (!(isImeiUnique(imei, device.getID()) && isSerialNumberUnique(serialNumber, device.getID()) &&
                isInventoryNumberUnique(inventoryNumber, device.getID()))) {
            return null;
        }

        if (imei == null && serialNumber == null && inventoryNumber == null) {
            return null;
        }

        device.setHardwareModel(hardwareModel);
        device.setImei(imei);
        device.setSerialNumber(serialNumber);
        device.setInventoryNumber(inventoryNumber);
        device.setReceivedFrom(receivedFrom);
        device.setReceivedDate(receivedDate);
        device.setUsefulLifeOfAsset(usefulLifeOfAsset);
        device.setSortedOutDate(sortedOutDate);
        device.setSortedOutComment(sortedOutComment);
        device.save();

        return device;
    }

    @Override
    public Device add(HardwareModel hardwareModel, String imei, String serialNumber, String inventoryNumber,
                      String receivedFrom, Date receivedDate, String usefulLifeOfAsset) {
        if (imei != null && imei.trim().length() != 0) {
            imei = escapeHtml4(imei.trim());
        } else {
            imei = null;
        }

        if (serialNumber != null && serialNumber.trim().length() != 0) {
            serialNumber = escapeHtml4(serialNumber.trim());
        } else {
            serialNumber = null;
        }

        if (inventoryNumber != null && inventoryNumber.trim().length() != 0) {
            inventoryNumber = escapeHtml4(inventoryNumber.trim());
        } else {
            inventoryNumber = null;
        }

        if (receivedFrom != null && receivedFrom.trim().length() != 0) {
            receivedFrom = escapeHtml4(receivedFrom.trim());
        } else {
            receivedFrom = null;
        }

        if (usefulLifeOfAsset != null && usefulLifeOfAsset.trim().length() != 0) {
            usefulLifeOfAsset = escapeHtml4(usefulLifeOfAsset.trim());
        } else {
            usefulLifeOfAsset = null;
        }

        if (!(isImeiUnique(imei, 0) && isSerialNumberUnique(serialNumber, 0) && isInventoryNumberUnique(inventoryNumber, 0))
                || hardwareModel == null) {
            return null;
        }

        if (imei == null && serialNumber == null && inventoryNumber == null) {
            return null;
        }

        Device device = ao.create(Device.class);
        device.setHardwareModel(hardwareModel);
        device.setImei(imei);
        device.setSerialNumber(serialNumber);
        device.setInventoryNumber(inventoryNumber);
        device.setReceivedFrom(receivedFrom);
        device.setReceivedDate(receivedDate);
        device.setUsefulLifeOfAsset(usefulLifeOfAsset);
        device.save();

        return device;
    }

    @Override
    public List<Device> all() {
        // TODO issues with postgresql
//        return Arrays.asList(ao.find(Device.class, Query.select()
//                .alias(Device.class, "device")
//                .alias(HardwareModel.class, "hardware")
//                .join(HardwareModel.class, "device.HARDWARE_MODEL_ID = hardware.ID")
//                .order(DEFAULT_ORDER)));

        Map<String, Device> deviceMap = new TreeMap<String, Device>();
        Device[] deviceArray = ao.find(Device.class);
        for (Device device : deviceArray) {
            HardwareModel model = device.getHardwareModel();
            String name = model.getName() != null ? model.getName().toLowerCase() : "";
            String version = model.getVersion() != null ? model.getVersion().toLowerCase() : "";
            String serial = device.getSerialNumber() != null ? device.getSerialNumber().toLowerCase() : "";
            String imei = device.getImei() != null ? device.getImei().toLowerCase() : "";
            String inventory = device.getInventoryNumber() != null ? device.getInventoryNumber().toLowerCase() : "";
            deviceMap.put(name + ";" + version + ";" + serial + ";" + imei + ";" + inventory, device);
        }

        return new ArrayList<Device>(deviceMap.values());
    }

    @Override
    public Device sortOutDevice(int id, Date sortedOutDate, String sortedOutComment) {
        Device[] toSortOut = ao.find(Device.class, Query.select().where("\"ID\" = ?", id));
        if (toSortOut.length != 1)
            return null;

        if (sortedOutDate == null)
            return null;

        if (toSortOut[0].getSortedOutDate() != null)
            return null;

        if (sortedOutComment != null && sortedOutComment.trim().length() != 0) {
            sortedOutComment = escapeHtml4(sortedOutComment.trim());
        } else {
            sortedOutComment = null;
        }

        toSortOut[0].setSortedOutDate(sortedOutDate);
        toSortOut[0].setSortedOutComment(sortedOutComment);
        toSortOut[0].save();

        return toSortOut[0];
    }

    @Override
    public List<Device> getCurrentlyAvailableDevices(int hardwareId) {
        List<Device> deviceList = new ArrayList<Device>();

        // TODO issues with postgresql
//        deviceList.addAll(Arrays.asList(ao.find(Device.class, Query.select()
//                .alias(Device.class, "device")
//                .alias(HardwareModel.class, "hardware")
//                .join(HardwareModel.class, "device.HARDWARE_MODEL_ID = hardware.ID")
//                .order(DEFAULT_ORDER)
//                .where("device.HARDWARE_MODEL_ID = ? AND device.SORTED_OUT_DATE IS NULL", hardwareId))));
//
//        deviceList.removeAll(Arrays.asList(ao.find(Device.class, Query.select()
//                .alias(Lending.class, "lending")
//                .alias(Device.class, "device")
//                .join(Lending.class, "lending.DEVICE_ID = device.ID")
//                .where("lending.END IS NULL"))));

        for(Device device : all()) {
            if(device.getHardwareModel().getID() != hardwareId) {
                continue;
            }
            if(device.getSortedOutDate() == null) {
                boolean available = true;
                for(Lending lending : device.getLendings()) {
                    if(lending.getEnd() == null) {
                        available = false;
                        break;
                    }
                }
                if(available) {
                    deviceList.add(device);
                }
            }
        }

        return deviceList;
    }

    @Override
    public List<Device> getSortedOutDevices() {
        // TODO issues with postgresql
//        return Arrays.asList(ao.find(Device.class, Query.select()
//                .alias(Device.class, "device")
//                .alias(HardwareModel.class, "hardware")
//                .join(HardwareModel.class, "device.HARDWARE_MODEL_ID = hardware.ID")
//                .order(DEFAULT_ORDER)
//                .where("device.SORTED_OUT_DATE IS NOT NULL")));

        List<Device> deviceList = new ArrayList<Device>();
        for(Device device : all()) {
            if (device.getSortedOutDate() != null) {
                deviceList.add(device);
            }
        }
        return deviceList;
    }

    @Override
    public List<Device> getSortedOutDevicesForHardware(HardwareModel hardwareModel) {
        // TODO issues with postgresql
//        return Arrays.asList(ao.find(Device.class, Query.select()
//                .alias(Device.class, "device")
//                .alias(HardwareModel.class, "hardware")
//                .join(HardwareModel.class, "device.HARDWARE_MODEL_ID = hardware.ID")
//                .order(DEFAULT_ORDER)
//                .where("device.SORTED_OUT_DATE IS NOT NULL AND device.HARDWARE_MODEL_ID = ?", hardwareId)));

        List<Device> deviceList = new ArrayList<Device>();
        for(Device device : hardwareModel.getDevices()) {
            if(device.getSortedOutDate() != null) {
                deviceList.add(device);
            }
        }
        return deviceList;
    }

    private boolean isSerialNumberUnique(String serialNumber, int exceptId) {
        return serialNumber == null || serialNumber.equals("") ||
                ao.find(Device.class, Query.select().where("\"SERIAL_NUMBER\" = ? AND \"ID\" <> ?", serialNumber, exceptId)).length == 0;

    }

    private boolean isImeiUnique(String imei, int exceptId) {
        return imei == null || imei.equals("") ||
                ao.find(Device.class, Query.select().where("\"IMEI\" = ? AND \"ID\" <> ?", imei, exceptId)).length == 0;

    }

    private boolean isInventoryNumberUnique(String inventoryNumber, int exceptId) {
        return inventoryNumber == null || inventoryNumber.equals("") ||
                ao.find(Device.class, Query.select().where("\"INVENTORY_NUMBER\" = ? AND \"ID\" <> ?", inventoryNumber, exceptId)).length == 0;

    }
}
