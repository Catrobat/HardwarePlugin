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
import net.java.ao.Preload;

import java.util.Date;

@Preload
public interface Lending extends Entity {
    Device getDevice();

    void setDevice(Device device);

    String getLendingByUserKey();

    void setLendingByUserKey(String lendingByUserKey);

    String getLendingIssuerUserKey();

    void setLendingIssuerUserKey(String lendingIssuerUserKey);

    Date getBegin();

    void setBegin(Date begin);

    Date getEnd();

    void setEnd(Date end);

    String getPurpose();

    void setPurpose(String purpose);

    String getComment();

    void setComment(String comment);

}
