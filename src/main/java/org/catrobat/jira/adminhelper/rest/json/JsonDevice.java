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

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import org.catrobat.jira.adminhelper.activeobject.Device;
import org.catrobat.jira.adminhelper.activeobject.DeviceComment;
import org.catrobat.jira.adminhelper.activeobject.Lending;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
@XmlRootElement
public class JsonDevice {

    @XmlElement
    private int id;

    @XmlElement
    private String hardwareModelName;

    @XmlElement
    private int hardwareModelId;

    @XmlElement
    private String serialNumber;

    @XmlElement
    private String imei;

    @XmlElement
    private String inventoryNumber;

    @XmlElement
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date receivedDate;

    @XmlElement
    private String receivedFrom;

    @XmlElement
    private String usefulLiveOfAsset;

    @XmlElement
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date sortedOutDate;

    @XmlElement
    private String sortedOutComment;

    @XmlElement
    private String currentlyLentOutBy;

    @XmlElement
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date currentlyLentOutSince;

    @XmlElement
    private String currentlyLentOutPurpose;

    @XmlElement
    private List<JsonDeviceComment> comments;

    @XmlElement
    private List<JsonLending> lendings;

    @XmlElement
    private JsonHardwareModel hardwareModel;

    public JsonDevice() {

    }

    public JsonDevice(JsonHardwareModel hardwareModel)
    {
        this.hardwareModel = hardwareModel;
    }

    public JsonDevice(Device toCopy) {
        id = toCopy.getID();
        hardwareModelName = toCopy.getHardwareModel().getName();
        if (toCopy.getHardwareModel().getVersion() != null && toCopy.getHardwareModel().getVersion().length() != 0) {
            hardwareModelName += " (" + toCopy.getHardwareModel().getVersion() + ")";
        }
        hardwareModelId = toCopy.getHardwareModel().getID();
        serialNumber = toCopy.getSerialNumber();
        imei = toCopy.getImei();
        inventoryNumber = toCopy.getInventoryNumber();
        receivedDate = toCopy.getReceivedDate();
        receivedFrom = toCopy.getReceivedFrom();
        usefulLiveOfAsset = toCopy.getUsefulLifeOfAsset();
        sortedOutDate = toCopy.getSortedOutDate();
        sortedOutComment = toCopy.getSortedOutComment();

        Lending[] lendingsArray = toCopy.getLendings();
        for (int i = lendingsArray.length - 1; i >= 0; i--) {
            if (lendingsArray[i].getEnd() == null) {
                currentlyLentOutSince = lendingsArray[i].getBegin();
                currentlyLentOutPurpose = lendingsArray[i].getPurpose();
                ApplicationUser user = ComponentAccessor.getUserManager().getUserByKey(lendingsArray[i].getLendingByUserKey());
                if (user != null)
                    currentlyLentOutBy = user.getUsername();
                else
                    currentlyLentOutBy = lendingsArray[i].getLendingByUserKey();
            }
        }
    }

    public JsonDevice(Device toCopy, UserManager userManager) {
        this(toCopy);

        this.comments = new ArrayList<JsonDeviceComment>();
        DeviceComment[] deviceComments = toCopy.getDeviceComments();
        for (DeviceComment deviceComment : deviceComments) {
            this.comments.add(new JsonDeviceComment(deviceComment, userManager));
        }

        this.lendings = new ArrayList<JsonLending>();
        Lending[] lendings = toCopy.getLendings();
        for (Lending lending : lendings) {
            this.lendings.add(new JsonLending(lending, userManager));
        }

        this.hardwareModel = new JsonHardwareModel(toCopy.getHardwareModel());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHardwareModelName() {
        return hardwareModelName;
    }

    public void setHardwareModelName(String hardwareModelName) {
        this.hardwareModelName = hardwareModelName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getReceivedFrom() {
        return receivedFrom;
    }

    public void setReceivedFrom(String receivedFrom) {
        this.receivedFrom = receivedFrom;
    }

    public String getUsefulLiveOfAsset() {
        return usefulLiveOfAsset;
    }

    public void setUsefulLiveOfAsset(String usefulLiveOfAsset) {
        this.usefulLiveOfAsset = usefulLiveOfAsset;
    }

    public Date getSortedOutDate() {
        return sortedOutDate;
    }

    public void setSortedOutDate(Date sortedOutDate) {
        this.sortedOutDate = sortedOutDate;
    }

    public String getSortedOutComment() {
        return sortedOutComment;
    }

    public void setSortedOutComment(String sortedOutComment) {
        this.sortedOutComment = sortedOutComment;
    }

    public String getCurrentlyLentOutBy() {
        return currentlyLentOutBy;
    }

    public void setCurrentlyLentOutBy(String currentlyLentOutBy) {
        this.currentlyLentOutBy = currentlyLentOutBy;
    }

    public Date getCurrentlyLentOutSince() {
        return currentlyLentOutSince;
    }

    public void setCurrentlyLentOutSince(Date currentlyLentOutSince) {
        this.currentlyLentOutSince = currentlyLentOutSince;
    }

    public List<JsonDeviceComment> getComments() {
        return comments;
    }

    public void setComments(List<JsonDeviceComment> comments) {
        this.comments = comments;
    }

    public List<JsonLending> getLendings() {
        return lendings;
    }

    public void setLendings(List<JsonLending> lendings) {
        this.lendings = lendings;
    }

    public JsonHardwareModel getHardwareModel() {
        return hardwareModel;
    }

    public void setHardwareModel(JsonHardwareModel hardwareModel) {
        this.hardwareModel = hardwareModel;
    }

    public int getHardwareModelId() {
        return hardwareModelId;
    }

    public void setHardwareModelId(int hardwareModelId) {
        this.hardwareModelId = hardwareModelId;
    }
}
