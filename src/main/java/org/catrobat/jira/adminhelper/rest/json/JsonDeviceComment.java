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
import org.catrobat.jira.adminhelper.activeobject.DeviceComment;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@SuppressWarnings("unused")
@XmlRootElement
public class JsonDeviceComment {
    @XmlElement
    int id;

    @XmlElement
    String author;

    @XmlElement
    String comment;

    @XmlElement
    @XmlJavaTypeAdapter(DateAdapter.class)
    Date date;

    public JsonDeviceComment() {

    }

    public JsonDeviceComment(DeviceComment toCopy, UserManager userManager) {
        id = toCopy.getID();
        ApplicationUser authorUser = userManager.getUserByKey(toCopy.getAuthor());
        author = authorUser == null ? toCopy.getAuthor() : authorUser.getUsername();
        comment = toCopy.getComment();
        date = toCopy.getDate();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
