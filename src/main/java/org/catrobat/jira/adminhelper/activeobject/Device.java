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

import net.java.ao.Entity;
import net.java.ao.OneToMany;
import net.java.ao.Preload;

import java.util.Date;

@Preload
public interface Device extends Entity {
    HardwareModel getHardwareModel();

    void setHardwareModel(HardwareModel hardwareModel);

    String getSerialNumber();

    void setSerialNumber(String serialNumber);

    String getImei();

    void setImei(String imei);

    String getInventoryNumber();

    void setInventoryNumber(String inventoryNumber);

    Date getReceivedDate();

    void setReceivedDate(Date receivedDate);

    String getReceivedFrom();

    void setReceivedFrom(String receivedFrom);

    String getUsefulLifeOfAsset();

    void setUsefulLifeOfAsset(String usefulLifeOfAsset);

    Date getSortedOutDate();

    void setSortedOutDate(Date sortedOutDate);

    String getSortedOutComment();

    void setSortedOutComment(String sortedOutComment);

    @OneToMany(reverse = "getDevice")
    DeviceComment[] getDeviceComments();

    @OneToMany(reverse = "getDevice")
    Lending[] getLendings();
}
