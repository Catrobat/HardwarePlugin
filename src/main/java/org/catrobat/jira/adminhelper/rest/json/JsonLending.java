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

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import org.catrobat.jira.adminhelper.activeobject.Lending;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@SuppressWarnings("unused")
@XmlRootElement
public class JsonLending {

    @XmlElement
    private int id;

    @XmlElement
    private JsonDevice device;

    @XmlElement
    private String lentOutBy;

    @XmlElement
    private String lentOutIssuer;

    @XmlElement
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date begin;

    @XmlElement
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date end;

    @XmlElement
    private String purpose;

    @XmlElement
    private String comment;

    public JsonLending() {

    }

    public JsonLending(Lending toCopy, UserManager userManager) {
        id = toCopy.getID();
        device = new JsonDevice(toCopy.getDevice());
        ApplicationUser lentOutByUser = userManager.getUserByKey(toCopy.getLendingByUserKey());
        lentOutBy = lentOutByUser == null ? toCopy.getLendingByUserKey() : lentOutByUser.getUsername();
        ApplicationUser lentOutIssuerUser = userManager.getUserByKey(toCopy.getLendingIssuerUserKey());
        lentOutIssuer = lentOutIssuerUser == null ? toCopy.getLendingIssuerUserKey() : lentOutIssuerUser.getUsername();
        begin = toCopy.getBegin();
        end = toCopy.getEnd();
        purpose = toCopy.getPurpose();
        comment = toCopy.getComment();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JsonDevice getDevice() {
        return device;
    }

    public void setDevice(JsonDevice device) {
        this.device = device;
    }

    public String getLentOutBy() {
        return lentOutBy;
    }

    public void setLentOutBy(String lentOutBy) {
        this.lentOutBy = lentOutBy;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLentOutIssuer() {
        return lentOutIssuer;
    }

    public void setLentOutIssuer(String lentOutIssuer) {
        this.lentOutIssuer = lentOutIssuer;
    }
}
