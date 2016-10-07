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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class LendingServiceImpl implements LendingService {

    private final ActiveObjects ao;

    public LendingServiceImpl(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }

    @Override
    public Lending lendOut(Device device, String lendingByUserKey, String lendingIssuerUserKey, String purpose, String comment, Date begin) {
        if (device == null || lendingByUserKey == null || lendingByUserKey.trim().length() == 0 || lendingIssuerUserKey == null ||
                lendingIssuerUserKey.trim().length() == 0 || begin == null) {
            return null;
        }

        if (ao.find(Lending.class, Query.select().where("\"DEVICE_ID\" = ? AND \"END\" IS NULL", device.getID())).length != 0) {
            return null;
        }

        lendingByUserKey = escapeHtml4(lendingByUserKey.trim());
        lendingIssuerUserKey = escapeHtml4(lendingIssuerUserKey.trim());
        if (purpose != null)
            purpose = escapeHtml4(purpose.trim());
        if (comment != null)
            comment = escapeHtml4(comment.trim());

        Lending lending = ao.create(Lending.class);
        lending.setDevice(device);
        lending.setLendingByUserKey(lendingByUserKey);
        lending.setLendingIssuerUserKey(lendingIssuerUserKey);
        lending.setPurpose(purpose);
        lending.setComment(comment);
        lending.setBegin(begin);
        lending.save();

        return lending;
    }

    @Override
    public Lending bringBack(Lending lending, String purpose, String comment, Date end) {
        if (lending == null)
            return null;

        if (purpose != null)
            purpose = escapeHtml4(purpose.trim());
        if (comment != null)
            comment = escapeHtml4(comment.trim());

        // just the date matters - not the time
        if (end == null || HelperUtil.clearTime(end).getTime() < HelperUtil.clearTime(lending.getBegin()).getTime()) {
            return null;
        }

        lending.setPurpose(purpose);
        lending.setComment(comment);
        lending.setEnd(end);
        lending.save();

        return lending;
    }

    @Override
    public List<Lending> currentlyLentOut() {
        return Arrays.asList(ao.find(Lending.class, Query.select().where("END IS NULL")));
    }

    @Override
    public List<Lending> currentlyLentOutDevices(HardwareModel hardwareModel) {
        if (hardwareModel == null)
            return new ArrayList<Lending>();

        // TODO issues with postgresql
//        return Arrays.asList(ao.find(Lending.class, Query.select()
//                .alias(Lending.class, "lending")
//                .alias(Device.class, "device")
//                .join(Device.class, "lending.DEVICE_ID = device.ID")
//                .where("lending.END IS NULL AND device.HARDWARE_MODEL_ID = ?", hardwareModel.getID())));

        List<Lending> lendingList = new ArrayList<Lending>();
        for(Device device : hardwareModel.getDevices()) {
            for(Lending lending : device.getLendings()) {
                if(lending.getEnd() == null) {
                    lendingList.add(lending);
                }
            }
        }

        return lendingList;
    }

    @Override
    public List<Lending> all() {
        return Arrays.asList(ao.find(Lending.class));
    }

    @Override
    public List<Lending> searchAllForUser(String lendingByUserKey) {
        if(lendingByUserKey == null || lendingByUserKey.trim().length() == 0) {
            return null;
        }

        return Arrays.asList(ao.find(Lending.class, Query.select()
                .where("upper(\"LENDING_BY_USER_KEY\") = upper(?)", escapeHtml4(lendingByUserKey.trim()))
                .order("BEGIN ASC")));
    }
}
