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

import com.atlassian.activeobjects.tx.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
public interface DeviceService {

    Device edit(Device device, HardwareModel hardwareModel, String imei, String serialNumber, String inventoryNumber,
                String receivedFrom, Date receivedDate, String usefulLifOfAsset, Date sortedOutDate, String sortedOutComment);

    Device add(HardwareModel hardwareModel, String imei, String serialNumber, String inventoryNumber, String receivedFrom, Date receivedDate, String usefulLifeOfAsset);

    List<Device> all();

    Device getDeviceById(int deviceId);

    Device sortOutDevice(int id, Date sortedOutDate, String sortedOutComment);

    List<Device> getCurrentlyAvailableDevices(int hardwareId);

    List<Device> getSortedOutDevices();

    List<Device> getSortedOutDevicesForHardware(HardwareModel hardwareModel);
}
