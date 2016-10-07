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

package org.catrobat.jira.adminhelper.rest.json;

import org.catrobat.jira.adminhelper.activeobject.DeviceService;
import org.catrobat.jira.adminhelper.activeobject.HardwareModel;
import org.catrobat.jira.adminhelper.activeobject.LendingService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("unused")
@XmlRootElement
public class JsonHardwareModel {
    @XmlElement
    private int id;
    @XmlElement
    private String name;
    @XmlElement
    private String typeOfDevice;
    @XmlElement
    private String version;
    @XmlElement
    private String price;
    @XmlElement
    private String producer;
    @XmlElement
    private String operatingSystem;
    @XmlElement
    private String articleNumber;
    @XmlElement
    private int available;
    @XmlElement
    private int sumOfDevices;

    public JsonHardwareModel() {
    }

    public JsonHardwareModel(HardwareModel toCopy) {
        id = toCopy.getID();
        name = toCopy.getName();
        version = toCopy.getVersion();
        price = toCopy.getPrice();
        articleNumber = toCopy.getArticleNumber();

        if (toCopy.getTypeOfDevice() != null)
            typeOfDevice = toCopy.getTypeOfDevice().getTypeOfDeviceName();
        if (toCopy.getProducer() != null)
            producer = toCopy.getProducer().getProducerName();
        if (toCopy.getOperatingSystem() != null)
            operatingSystem = toCopy.getOperatingSystem().getOperatingSystemName();
    }

    public JsonHardwareModel(HardwareModel toCopy, LendingService lendingService, DeviceService deviceService) {
        this(toCopy);
        sumOfDevices = toCopy.getDevices().length - deviceService.getSortedOutDevicesForHardware(toCopy).size();
        available = sumOfDevices - lendingService.currentlyLentOutDevices(toCopy).size();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeOfDevice() {
        return typeOfDevice;
    }

    public void setTypeOfDevice(String typeOfDevice) {
        this.typeOfDevice = typeOfDevice;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getSumOfDevices() {
        return sumOfDevices;
    }

    public void setSumOfDevices(int sumOfDevices) {
        this.sumOfDevices = sumOfDevices;
    }
}
